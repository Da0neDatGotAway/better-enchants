package net.enchantoutline.mixin;

import net.enchantoutline.mixin_accessors.ModelPart_CubeAccessor;
import net.enchantoutline.util.ModelHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(ModelPart.Cube.class)
public class ModelPart_CubeMixin implements ModelPart_CubeAccessor {
    @Shadow
    @Final
    @Mutable
    public ModelPart.Polygon[] polygons;

    @Inject(method = "<init>", at = @At("RETURN"))
    void enchantOutline$storeOnInit(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> sides, CallbackInfo ci){
        this.u = u;
        this.v = v;
        this.extraX = extraX;
        this.extraY = extraY;
        this.extraZ = extraZ;
        this.mirror = mirror;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.directions = new HashSet<>(sides);

        if(ModelHelper.FLIP_CUBOIDS.get()){
            for (ModelPart.Polygon quad : this.polygons){
                ArrayUtils.swap(quad.vertices(), 0, 3);
                ArrayUtils.swap(quad.vertices(), 1, 2);
            }
        }
    }

    @Unique
    private int u;

    @Unique
    private int v;

    @Unique
    private float extraX;

    @Unique
    private float extraY;

    @Unique
    private float extraZ;

    @Unique
    private boolean mirror;

    @Unique
    private float textureWidth;

    @Unique
    private float textureHeight;

    @Unique
    private Set<Direction> directions;

    @Override
    public int enchantOutline$getU() {
        return u;
    }

    @Override
    public int enchantOutline$getV() {
        return v;
    }

    @Override
    public float enchantOutline$getExtraX() {
        return extraX;
    }

    @Override
    public float enchantOutline$getExtraY() {
        return extraY;
    }

    @Override
    public float enchantOutline$getExtraZ() {
        return extraZ;
    }

    @Override
    public boolean enchantOutline$getMirror() {
        return mirror;
    }

    @Override
    public float enchantOutline$getTextureWidth() {
        return textureWidth;
    }

    @Override
    public float enchantOutline$getTextureHeight() {
        return textureHeight;
    }

    @Override
    public Set<Direction> enchantOutline$getDirections() {
        return new HashSet<>(directions);
    }
}
