package net.enchantoutline.util;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class QuadHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger(QuadHelper.class);

    public static List<BakedQuad> thickenQuad(List<BakedQuad> original, float percentSize){
        List<BakedQuad> newQuads = new ArrayList<>(original.size()*4);
        for (BakedQuad quad : original) {
            Vector3f[] defaultVerts = {new Vector3f(quad.position0()), new Vector3f(quad.position1()), new Vector3f(quad.position2()), new Vector3f(quad.position3())};

            Vec3i intVec = quad.face().getVector();
            Vector3f faceVec = new Vector3f(intVec.getX(), intVec.getY(), intVec.getZ());
            faceVec.mul(percentSize);

            Vector3f[] cardinalDirs = VertexHelper.getFaceCardinalDirs(defaultVerts, percentSize);
            if(cardinalDirs != null){
                for (Vector3f dir : cardinalDirs) {
                    Vector3f[] vertPoses = VertexHelper.growFace(defaultVerts, dir, faceVec);

                    //VertexHelper.flip(vertexData)
                    BakedQuad enchantmentQuad = new BakedQuad(vertPoses[3], vertPoses[2], vertPoses[1], vertPoses[0], quad.packedUV3(), quad.packedUV2(), quad.packedUV1(), quad.packedUV0(), 0, quad.face().getOpposite(), null, false, 100);

                    newQuads.add(enchantmentQuad);
                }
            }
            else{
                LOGGER.warn("Quad did not have 4 vertices");
            }
        }
        return newQuads;
    }
}
