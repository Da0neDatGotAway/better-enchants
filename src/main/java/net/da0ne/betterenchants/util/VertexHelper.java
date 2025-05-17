package net.da0ne.betterenchants.util;

import org.joml.Vector3f;

public class VertexHelper {
    public static Vector3f[] getVertexPos(int[] vertexData)
    {
        int vertices = vertexData.length/8;
        Vector3f[] returnList = new Vector3f[vertices];
        for(int i =  0; i < vertices; i++)
        {
            int vertStride = (i*8);
            Vector3f vertPos = new Vector3f(Float.intBitsToFloat(vertexData[vertStride]),Float.intBitsToFloat(vertexData[vertStride+1]),Float.intBitsToFloat(vertexData[vertStride+2]));
            returnList[i] = vertPos;
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

    public static Vector3f[] getFaceCardinalDirs(Vector3f[] defaultVerts, float scale) {
        if (defaultVerts.length == 4) {
            Vector3f center = new Vector3f();
            for (Vector3f vert : defaultVerts) {
                center.add(vert);
            }
            center.div(defaultVerts.length);

            Vector3f corner1 = defaultVerts[0];
            Vector3f corner2 = defaultVerts[1];
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
}
