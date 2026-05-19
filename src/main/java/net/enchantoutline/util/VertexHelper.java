package net.enchantoutline.util;

import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class VertexHelper {
    public static Vector3f[] getVertexPos(int[] vertexData)
    {
        var outVerts = getVertexData(vertexData);
        Vector3f[] returnList = new Vector3f[outVerts.length];
        for(int i = 0; i < outVerts.length; i++){
            returnList[i] = outVerts[i].pos();
        }
        return returnList;
    }

    public static Vertex[] getVertexData(int[] vertexData)
    {
        int vertices = vertexData.length/8;
        Vertex[] returnList = new Vertex[vertices];
        for(int i =  0; i < vertices; i++)
        {
            int vertStride = (i*8);
            Vector3f vertPos = new Vector3f(Float.intBitsToFloat(vertexData[vertStride]),Float.intBitsToFloat(vertexData[vertStride+1]),Float.intBitsToFloat(vertexData[vertStride+2]));
            float u = Float.intBitsToFloat(vertexData[vertStride+4]);
            float v = Float.intBitsToFloat(vertexData[vertStride+5]);
            returnList[i] = new Vertex(vertPos, u, v);
        }
        return returnList;
    }

    public static void setVertexData(int[] outVertexData, Vector3f[] newPos)
    {
        int vertices = outVertexData.length/8;
        for(int i =  0; i < vertices; i++)
        {
            int vertStride = (i*8);
            outVertexData[vertStride] = Float.floatToIntBits(newPos[i].x);
            outVertexData[vertStride+1] = Float.floatToIntBits(newPos[i].y);
            outVertexData[vertStride+2] = Float.floatToIntBits(newPos[i].z);
        }
    }

    public static int[] flip(int[] inVertexData)
    {
        int vertices = inVertexData.length/8;
        int[] outVertextData = new int[inVertexData.length];
        for(int i =  0; i < vertices; i++)
        {
            int stride = 8;
            System.arraycopy(inVertexData, i*stride, outVertextData, (vertices-i-1) * stride, stride);
        }
        return outVertextData;
    }


    /*
     * Given a quad, this gets the diagonal axis of that quad and returns those vectors in a array. Includes the + and - of those two axis
     * @param quadVerts The vertices of the quad
     * @param scale the distance to get the cardinal directions away.
     * @return
    public static Vector3f[] getCubeCardinalDirs(Vector3f[] quadVerts, float scale) {
        if (quadVerts.length == 24) {
            Vector3f center = new Vector3f();
            for (Vector3f vert : quadVerts) {
                center.add(vert);
            }
            center.div(quadVerts.length);

            Vector3f corner1 = quadVerts[0];
            Vector3f corner2 = quadVerts[1];
            corner1.sub(center);
            corner2.sub(center);

            Vector3f side1 = new Vector3f(corner1);
            side1.add(corner2);

            Vector3f side2 = new Vector3f(corner1);
            side2.sub(corner2);

            side1.normalize();
            side2.normalize();

            //this is localDiagonal. We don't realocate cause that's not efficent
            Vector3f localDiagonal = side1;
            localDiagonal.add(side2);
            localDiagonal.mul(scale);

            Vector3f otherLocal = new Vector3f(localDiagonal).reflect(side2);

            Vector3f[] cardinalDirs = {new Vector3f(localDiagonal), new Vector3f(otherLocal), localDiagonal.mul(-1), otherLocal.mul(-1)};

            corner1.add(center);
            corner2.add(center);

            return cardinalDirs;
        }
        return null;
    }
    */

    /**
     * Given a quad, this gets the diagonal axis of that quad and returns those vectors in a array. Includes the + and - of those two axis
     * @param quadVerts The vertices of the quad
     * @param scale the distance to get the cardinal directions away.
     * @return
     */
    public static Vector3f[] getFaceCardinalDirs(Vector3f[] quadVerts, float scale) {
        if (quadVerts.length == 4) {
            Vector3f center = new Vector3f();
            for (Vector3f vert : quadVerts) {
                center.add(vert);
            }
            center.div(quadVerts.length);

            Vector3f corner1 = quadVerts[0];
            Vector3f corner2 = quadVerts[1];
            corner1.sub(center);
            corner2.sub(center);

            Vector3f side1 = new Vector3f(corner1);
            side1.add(corner2);

            Vector3f side2 = new Vector3f(corner1);
            side2.sub(corner2);

            side1.normalize();
            side2.normalize();

            //this is localDiagonal. We don't realocate cause that's not efficent
            Vector3f localDiagonal = side1;
            localDiagonal.add(side2);
            localDiagonal.mul(scale);

            Vector3f otherLocal = new Vector3f(localDiagonal).reflect(side2);

            Vector3f[] cardinalDirs = {new Vector3f(localDiagonal), new Vector3f(otherLocal), localDiagonal.mul(-1), otherLocal.mul(-1)};

            corner1.add(center);
            corner2.add(center);

            return cardinalDirs;
        }
        return null;
    }

    /**
     * Grows a face
     * @param defaultVerts
     * @param cardinalDir a vector, the direction to shift the face to.
     * @param scaledNormal the scaled normal of the quad. Used to
     * @return
     */
    public static Vector3f[] growFace(Vector3f[] defaultVerts, Vector3f cardinalDir, Vector3f scaledNormal)
    {
        Vector3f[] vertPoses = new Vector3f[defaultVerts.length];
        Vector3f normalizedNormal = new Vector3f(scaledNormal);
        normalizedNormal.normalize();
        normalizedNormal.mul(0.0001f);
        scaledNormal.add(normalizedNormal);

        for (int vertInterator = 0; vertInterator < defaultVerts.length; vertInterator++) {
            Vector3f vert = new Vector3f(defaultVerts[vertInterator]);
            vert.add(scaledNormal);
            vert.add(cardinalDir);
            vertPoses[vertInterator] = vert;
        }
        return vertPoses;
    }

    public static Vector3f growVert(Vector3f pos, Vector3f cardinalDir, Vector3f scaledNormal){
        Vector3f normalizedNormal = new Vector3f(scaledNormal);
        normalizedNormal.normalize();
        normalizedNormal.mul(0.0001f);
        scaledNormal.add(normalizedNormal);

        Vector3f vert = new Vector3f(pos);
        vert.add(scaledNormal);
        vert.add(cardinalDir);

        return vert;
    }



    public static Vector3f transformVector(MatrixStack matrices, Vector3f vec) {
        Matrix4f mat = matrices.peek().getPositionMatrix();

        Vector4f vec4 = new Vector4f(vec.x(), vec.y(), vec.z(), 1.0f);
        mat.transform(vec4);

        return new Vector3f(vec4.x(), vec4.y(), vec4.z());
    }

    /**
     * stolen from BakedQuadFactory, since its private for some reason, and I don't want to make a mixin to get what is just int manipulation
     * @param vertices
     * @param cornerIndex
     * @param pos
     * @param u
     * @param v
     */
    public static void packVertexData(int[] vertices, int cornerIndex, Vector3f pos, float u, float v) {
        int i = cornerIndex * 8;
        vertices[i] = Float.floatToRawIntBits(pos.x());
        vertices[i + 1] = Float.floatToRawIntBits(pos.y());
        vertices[i + 2] = Float.floatToRawIntBits(pos.z());
        vertices[i + 3] = -1;
        vertices[i + 4] = Float.floatToRawIntBits(u);
        vertices[i + 4 + 1] = Float.floatToRawIntBits(v);
    }

    public static record Vertex(Vector3f pos, float u, float v){

    }
}
