package net.enchantoutline.util;

import net.enchantoutline.mixin_accessors.ModelPartAccessor;
import net.enchantoutline.mixin_accessors.ModelPart_CuboidAccessor;
import net.enchantoutline.model.HijackedModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Function;

public class ModelHelper {
    public static final ThreadLocal<Boolean> FLIP_CUBOIDS = ThreadLocal.withInitial(() -> false);

    public static HijackedModel getThickenedModel(Model original, Function<Identifier, RenderLayer> layerFactory, float scale){
        ModelPart root = original.getRootPart();
        ModelPart thickenedRoot = thickenedModelPart(root, scale);

        return new HijackedModel(thickenedRoot, layerFactory);
    }

    public static ModelPart thickenedModelPart(ModelPart original, float scale){
        //the times 15 is just because it turns out that makes the output about visually equal to the bakedItemRenderer
        return thickenedModelPartInternal(original, scale * ModelPart.Vertex.SCALE_FACTOR);
    }

    //get thick
    private static ModelPart thickenedModelPartInternal(ModelPart original, float scale){

        ModelPartAccessor modelPartAccessor = (ModelPartAccessor)(Object)original;
        List<ModelPart.Cuboid> cuboids = modelPartAccessor.enchantOutline$getCuboids();

        List<ModelPart.Cuboid> thickCuboids = new ArrayList<>();
        for(var cuboid: cuboids){
            ModelPart_CuboidAccessor accessor = ((ModelPart_CuboidAccessor)cuboid);
            for(Direction dir : accessor.enchantOutline$getDirections()){
                thickCuboids.addAll(thickenCuboidFace(cuboid, dir, scale/*, stack*/));
            }
        }

        Map<String, ModelPart> oldChildren = modelPartAccessor.enchantOutline$getChildren();
        Map<String, ModelPart> newChildren = new HashMap<>(oldChildren.size()+1);

        //Vector3f posOffset = new Vector3f(0,0,0);
        //posOffset = VertexHelper.transformVector(stack, posOffset);

        ModelPart undoTransformModelPart = new ModelPart(thickCuboids, Map.of());
        //undoTransformModelPart.setOrigin(posOffset.x(),posOffset.y(),posOffset.z());
        ModelTransform transform = new ModelTransform(original.originX, original.originY, original.originZ, original.pitch, original.yaw, original.roll, original.xScale, original.yScale, original.zScale);
        undoTransformModelPart.setTransform(transform);
        newChildren.put("EnchantOutline", undoTransformModelPart);

        for(var set :oldChildren.entrySet()){
            newChildren.put(set.getKey(), thickenedModelPartInternal(set.getValue(), scale));
        }

        ModelPart thickModelPart = new ModelPart(List.of(), newChildren);
        thickModelPart.setDefaultTransform(original.getDefaultTransform());
        ModelTransform trans = thickModelPart.getTransform();
        thickModelPart.setTransform(trans);

        return thickModelPart;
    }

    private static List<ModelPart.Cuboid> thickenCuboidFace(ModelPart.Cuboid original, Direction dir, float scale/*, MatrixStack stack*/){
        List<ModelPart.Cuboid> thickenedCuboids = new ArrayList<>(4);

        Vector3f normal = new Vector3f(dir.getUnitVector());
        normal.mul(scale);

        ModelPart_CuboidAccessor accessor = ((ModelPart_CuboidAccessor)original);

        Vector3f[] verts;

        int u = accessor.enchantOutline$getU();
        int v = accessor.enchantOutline$getV();

        float x = original.minX;
        float y = original.minY;
        float z = original.minZ;

        Vector3f startPos = new Vector3f(x, y, z);
        //startPos = VertexHelper.transformVector(stack, startPos);

        float sizeX = original.maxX - x;
        float sizeY = original.maxY - y;
        float sizeZ = original.maxZ - z;

        //TODO: use these to calculate a new scale so things have a consistent outline size
        float extraX = accessor.enchantOutline$getExtraX();
        float extraY = accessor.enchantOutline$getExtraY();
        float extraZ = accessor.enchantOutline$getExtraZ();

        boolean mirror = accessor.enchantOutline$getMirror();

        float f = x + sizeX;
        float g = y + sizeY;
        float h = z + sizeZ;

        x -= extraX;
        y -= extraY;
        z -= extraZ;

        f += extraX;
        g += extraY;
        h += extraZ;
        if (mirror) {
            float i = f;
            f = x;
            x = i;
        }

        Vector3f vertex = new Vector3f(x, y, z);
        Vector3f vertex2 = new Vector3f(f, y, z);
        Vector3f vertex3 = new Vector3f(f, g, z);
        Vector3f vertex4 = new Vector3f(x, g, z);
        Vector3f vertex5 = new Vector3f(x, y, h);
        Vector3f vertex6 = new Vector3f(f, y, h);
        Vector3f vertex7 = new Vector3f(f, g, h);
        Vector3f vertex8 = new Vector3f(x, g, h);

        if(dir.equals(Direction.DOWN)){
            verts = new Vector3f[]{vertex6, vertex5, vertex, vertex2};
        }else if(dir.equals(Direction.UP)){
            verts = new Vector3f[]{vertex3, vertex4, vertex8, vertex7};
        }else if(dir.equals(Direction.WEST)){
            verts = new Vector3f[]{vertex, vertex5, vertex8, vertex4};
        }else if(dir.equals(Direction.NORTH)){
            verts = new Vector3f[]{vertex2, vertex, vertex4, vertex3};
        }else if(dir.equals(Direction.EAST)){
            verts = new Vector3f[]{vertex6, vertex2, vertex3, vertex7};
        }else {
            verts = new Vector3f[]{vertex5, vertex6, vertex7, vertex8};
        }

        //EnchantmentGlintOutline.LOGGER.info("scale1: {}", scale);
        //scale = (scale * (extraX*2 + sizeX)/sizeX);
        //EnchantmentGlintOutline.LOGGER.info("scale2: {}", scale);
        Vector3f[] cardinalDirs = VertexHelper.getFaceCardinalDirs(verts, scale);
        if(cardinalDirs != null) {
            for (Vector3f cardDir : cardinalDirs) {
                Vector3f movedPos = VertexHelper.growVert(startPos, cardDir, normal);
                //movedPos.sub(startPos).mul(1/((extraX + sizeX)/sizeX), 1/((extraY + sizeY)/sizeY), 1/((extraZ + sizeZ)/sizeZ)).add(startPos);
                ModelHelper.FLIP_CUBOIDS.set(true);
                thickenedCuboids.add(new ModelPart.Cuboid(u, v, movedPos.x(), movedPos.y(), movedPos.z(), sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, accessor.enchantOutline$getTextureWidth(), accessor.enchantOutline$getTextureHeight(), Set.of(dir)));
                ModelHelper.FLIP_CUBOIDS.set(false);
            }
        }

        return thickenedCuboids;
    }
}
