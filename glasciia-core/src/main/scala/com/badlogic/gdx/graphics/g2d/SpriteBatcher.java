/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.graphics.g2d;

import static com.badlogic.gdx.graphics.g2d.Sprite.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.NumberUtils;
import com.github.gigurra.math.Box2;
import com.github.gigurra.math.Vec2;

import java.nio.IntBuffer;

/**
 *
 * COPY PASTA
 * COPY PASTA
 * COPY PASTA
 * COPY PASTA
 * COPY PASTA
 * COPY PASTA
 * COPY PASTA
 *
 *
 * Modified version of LibGdx's PolygonSpriteBatcher
 *
 *
 *
 *
 * A SpriteBatcher is used to draw 2D polygons that reference a texture (region). The class will batch the drawing commands
 * and optimize them for processing by the GPU.
 * <p>
 * To draw something with a SpriteBatcher one has to first call the {@link SpriteBatcher#begin()} method which will
 * setup appropriate render states. When you are done with drawing you have to call {@link SpriteBatcher#end()} which will
 * actually draw the things you specified.
 * <p>
 * All drawing commands of the SpriteBatcher operate in screen coordinates. The screen coordinate system has an x-axis
 * pointing to the right, an y-axis pointing upwards and the origin is in the lower left corner of the screen. You can also
 * provide your own transformation and projection matrices if you so wish.
 * <p>
 * A SpriteBatcher is managed. In case the OpenGL context is lost all OpenGL resources a SpriteBatcher uses internally
 * get invalidated. A context is lost when a user switches to another application or receives an incoming call on Android. A
 * SpritPolygonSpriteBatcheBatch will be automatically reloaded after the OpenGL context is restored.
 * <p>
 * A SpriteBatcher is a pretty heavy object so you should only ever have one in your program.
 * <p>
 * A SpriteBatcher works with OpenGL ES 1.x and 2.0. In the case of a 2.0 context it will use its own custom shader to draw
 * all provided sprites. You can set your own custom shader via {@link #setShader(ShaderProgram)}.
 * <p>
 * A SpriteBatcher has to be disposed if it is no longer used.
 * @author mzechner
 * @author Stefan Bachmann
 * @author Nathan Sweet */
public final class SpriteBatcher implements Batch {
    private Mesh mesh;

    private final float[] vertices;
    private final short[] triangles;
    private int vertexIndex, triangleIndex;
    private Texture lastTexture;
    private float invTexWidth = 0, invTexHeight = 0;
    private boolean drawing;

    private final Matrix4 transformMatrix = new Matrix4();
    private final Matrix4 projectionMatrix = new Matrix4();
    private final Matrix4 combinedMatrix = new Matrix4();

    private boolean blendingDisabled;
    private int blendSrcFunc = GL20.GL_SRC_ALPHA;
    private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;

    private boolean depthTestEnabled = false;
    private boolean depthWriteEnabled = true;
    private int depthFunc = GL20.GL_LEQUAL;

    private boolean redMask = true;
    private boolean greenMask = true;
    private boolean blueMask = true;
    private boolean alphaMask = true;

    private final ShaderProgram shader;
    private ShaderProgram customShader;
    private boolean ownsShader;
    private final IntBuffer fourIntsBuffer;

    float color = Color.WHITE.toFloatBits();
    private Color tempColor = new Color(1, 1, 1, 1);

    /** Number of render calls since the last {@link #begin()}. **/
    public int renderCalls = 0;

    /** Number of rendering calls, ever. Will not be reset unless set manually. **/
    public int totalRenderCalls = 0;

    /** The maximum number of triangles rendered in one batch so far. **/
    public int maxTrianglesInBatch = 0;

    /** Constructs a SpriteBatcher with the default shader, 2000 vertices, and 4000 triangles.
     * @see #SpriteBatcher(int, int, ShaderProgram) */
    public SpriteBatcher () {
        this(2000, null);
    }

    /** Constructs a SpriteBatcher with the default shader, size vertices, and size * 2 triangles.
     * @param size The max number of vertices and number of triangles in a single batch. Max of 32767.
     * @see #SpriteBatcher(int, int, ShaderProgram) */
    public SpriteBatcher (int size) {
        this(size, size * 2, null);
    }

    /** Constructs a SpriteBatcher with the specified shader, size vertices and size * 2 triangles.
     * @param size The max number of vertices and number of triangles in a single batch. Max of 32767.
     * @see #SpriteBatcher(int, int, ShaderProgram) */
    public SpriteBatcher (int size, ShaderProgram defaultShader) {
        this(size, size * 2, defaultShader);
    }

    /** Constructs a new SpriteBatcher. Sets the projection matrix to an orthographic projection with y-axis point upwards,
     * x-axis point to the right and the origin being in the bottom left corner of the screen. The projection will be pixel perfect
     * with respect to the current screen resolution.
     * <p>
     * The defaultShader specifies the shader to use. Note that the names for uniforms for this default shader are different than
     * the ones expect for shaders set with {@link #setShader(ShaderProgram)}. See {@link SpriteBatch#createDefaultShader()}.
     * @param maxVertices The max number of vertices in a single batch. Max of 32767.
     * @param maxTriangles The max number of triangles in a single batch.
     * @param defaultShader The default shader to use. This is not owned by the SpriteBatcher and must be disposed separately.
     *           May be null to use the default shader. */
    public SpriteBatcher (int maxVertices, int maxTriangles, ShaderProgram defaultShader) {
        // 32767 is max vertex index.
        if (maxVertices > 32767)
            throw new IllegalArgumentException("Can't have more than 32767 vertices per batch: " + maxVertices);

        Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
        if (Gdx.gl30 != null) {
            vertexDataType = VertexDataType.VertexBufferObjectWithVAO;
        }
        mesh = new Mesh(vertexDataType, false, maxVertices, maxTriangles * 3,
                new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        vertices = new float[maxVertices * VERTEX_SIZE];
        triangles = new short[maxTriangles * 3];
        fourIntsBuffer = BufferUtils.newIntBuffer(16);

        if (defaultShader == null) {
            shader = SpriteBatch.createDefaultShader();
            ownsShader = true;
        } else
            shader = defaultShader;

        projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void begin () {
        if (drawing) throw new IllegalStateException("SpriteBatcher.end must be called before begin.");
        renderCalls = 0;

        setupDepthTest(depthTestEnabled, depthWriteEnabled, depthFunc, true);
        setupColorMasks(redMask, greenMask, blueMask, alphaMask, true);
        if (customShader != null)
            customShader.begin();
        else
            shader.begin();
        setupMatrices();

        drawing = true;
    }

    @Override
    public void end () {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before end.");
        if (vertexIndex > 0) flush();
        lastTexture = null;
        drawing = false;

        GL20 gl = Gdx.gl;
        setupColorMasks(true, true, true, true, true);
        setupDepthTest(false, true, GL20.GL_LEQUAL, true);
        if (isBlendingEnabled()) gl.glDisable(GL20.GL_BLEND);

        if (customShader != null)
            customShader.end();
        else
            shader.end();
    }

    public Box2 getScissorBox() {
        Gdx.gl.glGetIntegerv(GL20.GL_SCISSOR_BOX, fourIntsBuffer);
        fourIntsBuffer.rewind();
        return new Box2(new Vec2(fourIntsBuffer.get(0), fourIntsBuffer.get(1)), new Vec2(fourIntsBuffer.get(2), fourIntsBuffer.get(3)));
    }


    public Box2 getViewport() {
        Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, fourIntsBuffer);
        fourIntsBuffer.rewind();
        return new Box2(new Vec2(fourIntsBuffer.get(0), fourIntsBuffer.get(1)), new Vec2(fourIntsBuffer.get(2), fourIntsBuffer.get(3)));
    }

    public void setScissorBox(Box2 region) {
        flush();
        Gdx.gl.glScissor((int)region.left(), (int)region.bottom(), (int)region.width(), (int)region.height());
    }

    public void setViewport(Box2 region) {
        flush();
        Gdx.gl.glViewport((int)region.left(), (int)region.bottom(), (int)region.width(), (int)region.height());
    }

    public void setupDepthTest(boolean test, boolean write, int func) {
        setupDepthTest(test, write, func, false);
    }

    public boolean getDepthWrite() {
        return this.depthWriteEnabled;
    }

    public boolean getDepthTest() {
        return this.depthTestEnabled;
    }

    public int getDepthFunc() {
        return this.depthFunc;
    }

    public boolean getRedMask() {
        return this.redMask;
    }

    public boolean getGreenMask() {
        return this.greenMask;
    }

    public boolean getBlueMask() {
        return this.blueMask;
    }

    public boolean getAlphaMask() {
        return this.alphaMask;
    }

    public void setupDepthTest(boolean test, boolean write, int func, boolean force) {
        if (force ||
                test != this.depthTestEnabled ||
                write != this.depthWriteEnabled ||
                func != this.depthFunc) {

            flush();

            this.depthTestEnabled = test;
            this.depthWriteEnabled = write;
            this.depthFunc = func;

            if (depthTestEnabled) {
                Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            } else {
                Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            }
            Gdx.gl.glDepthMask(depthWriteEnabled);
            Gdx.gl.glDepthFunc(depthFunc);
        }
    }

    public void setupColorMasks(boolean red, boolean green, boolean blue, boolean alpha) {
        setupColorMasks(red, green, blue, alpha, false);
    }

    public void setupColorMasks(boolean red, boolean green, boolean blue, boolean alpha, boolean force) {
        if (force ||
                red != this.redMask ||
                green != this.greenMask ||
                blue != this.blueMask ||
                alpha != this.alphaMask) {

            flush();

            this.redMask = red;
            this.greenMask = green;
            this.blueMask = blue;
            this.alphaMask = alpha;

            Gdx.gl.glColorMask(redMask, greenMask, blueMask, alphaMask);
        }
    }

    @Override
    public void setColor (Color tint) {
        color = tint.toFloatBits();
    }

    @Override
    public void setColor (float r, float g, float b, float a) {
        int intBits = (int)(255 * a) << 24 | (int)(255 * b) << 16 | (int)(255 * g) << 8 | (int)(255 * r);
        color = NumberUtils.intToFloatColor(intBits);
    }

    @Override
    public void setColor (float color) {
        this.color = color;
    }

    @Override
    public Color getColor () {
        int intBits = NumberUtils.floatToIntColor(color);
        Color color = this.tempColor;
        color.r = (intBits & 0xff) / 255f;
        color.g = ((intBits >>> 8) & 0xff) / 255f;
        color.b = ((intBits >>> 16) & 0xff) / 255f;
        color.a = ((intBits >>> 24) & 0xff) / 255f;
        return color;
    }

    @Override
    public float getPackedColor () {
        return color;
    }

    /** Draws a polygon region with the bottom left corner at x,y having the width and height of the region. */
    public void draw (PolygonRegion region, float x, float y) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final short[] regionTriangles = region.triangles;
        final int regionTrianglesLength = regionTriangles.length;
        final float[] regionVertices = region.vertices;
        final int regionVerticesLength = regionVertices.length;

        final Texture texture = region.region.texture;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + regionTrianglesLength > triangles.length
                || vertexIndex + regionVerticesLength * VERTEX_SIZE / 2 > vertices.length) flush();

        int triangleIndex = this.triangleIndex;
        int vertexIndex = this.vertexIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;

        for (int i = 0; i < regionTrianglesLength; i++)
            triangles[triangleIndex++] = (short)(regionTriangles[i] + startVertex);
        this.triangleIndex = triangleIndex;

        final float[] vertices = this.vertices;
        final float color = this.color;
        final float[] textureCoords = region.textureCoords;

        for (int i = 0; i < regionVerticesLength; i += 2) {
            vertices[vertexIndex++] = regionVertices[i] + x;
            vertices[vertexIndex++] = regionVertices[i + 1] + y;
            vertices[vertexIndex++] = color;
            vertices[vertexIndex++] = textureCoords[i];
            vertices[vertexIndex++] = textureCoords[i + 1];
        }
        this.vertexIndex = vertexIndex;
    }

    /** Draws a polygon region with the given transform */
    public void draw (PolygonRegion region, Affine2 transform) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final short[] regionTriangles = region.triangles;
        final int regionTrianglesLength = regionTriangles.length;
        final float[] regionVertices = region.vertices;
        final int regionVerticesLength = regionVertices.length;

        final Texture texture = region.region.texture;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + regionTrianglesLength > triangles.length
                || vertexIndex + regionVerticesLength * VERTEX_SIZE / 2 > vertices.length) flush();

        int triangleIndex = this.triangleIndex;
        int vertexIndex = this.vertexIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;

        for (int i = 0; i < regionTrianglesLength; i++)
            triangles[triangleIndex++] = (short)(regionTriangles[i] + startVertex);

        this.triangleIndex = triangleIndex;

        final float[] vertices = this.vertices;
        final float color = this.color;
        final float[] textureCoords = region.textureCoords;

        final Vector2 point = new Vector2();

        for (int i = 0; i < regionVerticesLength; i += 2) {
            point.x = regionVertices[i];
            point.y = regionVertices[i + 1];
            transform.applyTo(point);

            vertices[vertexIndex++] = point.x;
            vertices[vertexIndex++] = point.y;
            vertices[vertexIndex++] = color;
            vertices[vertexIndex++] = textureCoords[i];
            vertices[vertexIndex++] = textureCoords[i + 1];
        }
        this.vertexIndex = vertexIndex;
    }

    /** Draws a polygon region with the bottom left corner at x,y and stretching the region to cover the given width and height. */
    public void draw (PolygonRegion region, float x, float y, float width, float height) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final short[] regionTriangles = region.triangles;
        final int regionTrianglesLength = regionTriangles.length;
        final float[] regionVertices = region.vertices;
        final int regionVerticesLength = regionVertices.length;
        final TextureRegion textureRegion = region.region;

        final Texture texture = textureRegion.texture;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + regionTrianglesLength > triangles.length
                || vertexIndex + regionVerticesLength * VERTEX_SIZE / 2 > vertices.length) flush();

        int triangleIndex = this.triangleIndex;
        int vertexIndex = this.vertexIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;

        for (int i = 0, n = regionTriangles.length; i < n; i++)
            triangles[triangleIndex++] = (short)(regionTriangles[i] + startVertex);
        this.triangleIndex = triangleIndex;

        final float[] vertices = this.vertices;
        final float color = this.color;
        final float[] textureCoords = region.textureCoords;
        final float sX = width / textureRegion.regionWidth;
        final float sY = height / textureRegion.regionHeight;

        for (int i = 0; i < regionVerticesLength; i += 2) {
            vertices[vertexIndex++] = regionVertices[i] * sX + x;
            vertices[vertexIndex++] = regionVertices[i + 1] * sY + y;
            vertices[vertexIndex++] = color;
            vertices[vertexIndex++] = textureCoords[i];
            vertices[vertexIndex++] = textureCoords[i + 1];
        }
        this.vertexIndex = vertexIndex;
    }

    /** Draws the polygon region with the bottom left corner at x,y and stretching the region to cover the given width and height.
     * The polygon region is offset by originX, originY relative to the origin. Scale specifies the scaling factor by which the
     * polygon region should be scaled around originX, originY. Rotation specifies the angle of counter clockwise rotation of the
     * rectangle around originX, originY. */
    public void draw (PolygonRegion region, float x, float y, float originX, float originY, float width, float height,
                      float scaleX, float scaleY, float rotation) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final short[] regionTriangles = region.triangles;
        final int regionTrianglesLength = regionTriangles.length;
        final float[] regionVertices = region.vertices;
        final int regionVerticesLength = regionVertices.length;
        final TextureRegion textureRegion = region.region;

        Texture texture = textureRegion.texture;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + regionTrianglesLength > triangles.length
                || vertexIndex + regionVerticesLength * VERTEX_SIZE / 2 > vertices.length) flush();

        int triangleIndex = this.triangleIndex;
        int vertexIndex = this.vertexIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;

        for (int i = 0; i < regionTrianglesLength; i++)
            triangles[triangleIndex++] = (short)(regionTriangles[i] + startVertex);
        this.triangleIndex = triangleIndex;

        final float[] vertices = this.vertices;
        final float color = this.color;
        final float[] textureCoords = region.textureCoords;

        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        final float sX = width / textureRegion.regionWidth;
        final float sY = height / textureRegion.regionHeight;
        final float cos = MathUtils.cosDeg(rotation);
        final float sin = MathUtils.sinDeg(rotation);

        float fx, fy;
        for (int i = 0; i < regionVerticesLength; i += 2) {
            fx = (regionVertices[i] * sX - originX) * scaleX;
            fy = (regionVertices[i + 1] * sY - originY) * scaleY;
            vertices[vertexIndex++] = cos * fx - sin * fy + worldOriginX;
            vertices[vertexIndex++] = sin * fx + cos * fy + worldOriginY;
            vertices[vertexIndex++] = color;
            vertices[vertexIndex++] = textureCoords[i];
            vertices[vertexIndex++] = textureCoords[i + 1];
        }
        this.vertexIndex = vertexIndex;
    }

    /** Draws the polygon using the given vertices and triangles. Each vertices must be made up of 5 elements in this order: x, y,
     * color, u, v. */
    public void draw (Texture texture, float[] polygonVertices, int verticesOffset, int verticesCount, short[] polygonTriangles,
                      int trianglesOffset, int trianglesCount) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + trianglesCount > triangles.length || vertexIndex + verticesCount > vertices.length) //
            flush();

        int triangleIndex = this.triangleIndex;
        final int vertexIndex = this.vertexIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;

        for (int i = trianglesOffset, n = i + trianglesCount; i < n; i++)
            triangles[triangleIndex++] = (short)(polygonTriangles[i] + startVertex);
        this.triangleIndex = triangleIndex;

        System.arraycopy(polygonVertices, verticesOffset, vertices, vertexIndex, verticesCount);
        this.vertexIndex += verticesCount;
    }

    @Override
    public void draw (Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX,
                      float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 > triangles.length || vertexIndex + SPRITE_SIZE > vertices.length) //
            flush();

        int triangleIndex = this.triangleIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;

        // bottom left and top right corner points relative to origin
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;

        // scale
        if (scaleX != 1 || scaleY != 1) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }

        // construct corner points, start from top left and go counter clockwise
        final float p1x = fx;
        final float p1y = fy;
        final float p2x = fx;
        final float p2y = fy2;
        final float p3x = fx2;
        final float p3y = fy2;
        final float p4x = fx2;
        final float p4y = fy;

        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;

        // rotate
        if (rotation != 0) {
            final float cos = MathUtils.cosDeg(rotation);
            final float sin = MathUtils.sinDeg(rotation);

            x1 = cos * p1x - sin * p1y;
            y1 = sin * p1x + cos * p1y;

            x2 = cos * p2x - sin * p2y;
            y2 = sin * p2x + cos * p2y;

            x3 = cos * p3x - sin * p3y;
            y3 = sin * p3x + cos * p3y;

            x4 = x1 + (x3 - x2);
            y4 = y3 - (y2 - y1);
        } else {
            x1 = p1x;
            y1 = p1y;

            x2 = p2x;
            y2 = p2y;

            x3 = p3x;
            y3 = p3y;

            x4 = p4x;
            y4 = p4y;
        }

        x1 += worldOriginX;
        y1 += worldOriginY;
        x2 += worldOriginX;
        y2 += worldOriginY;
        x3 += worldOriginX;
        y3 += worldOriginY;
        x4 += worldOriginX;
        y4 += worldOriginY;

        float u = srcX * invTexWidth;
        float v = (srcY + srcHeight) * invTexHeight;
        float u2 = (srcX + srcWidth) * invTexWidth;
        float v2 = srcY * invTexHeight;

        if (flipX) {
            float tmp = u;
            u = u2;
            u2 = tmp;
        }

        if (flipY) {
            float tmp = v;
            v = v2;
            v2 = tmp;
        }

        float color = this.color;
        int idx = this.vertexIndex;
        vertices[idx++] = x1;
        vertices[idx++] = y1;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;

        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;

        vertices[idx++] = x3;
        vertices[idx++] = y3;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = x4;
        vertices[idx++] = y4;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }

    @Override
    public void draw (Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth,
                      int srcHeight, boolean flipX, boolean flipY) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 > triangles.length || vertexIndex + SPRITE_SIZE > vertices.length) //
            flush();

        int triangleIndex = this.triangleIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;

        float u = srcX * invTexWidth;
        float v = (srcY + srcHeight) * invTexHeight;
        float u2 = (srcX + srcWidth) * invTexWidth;
        float v2 = srcY * invTexHeight;
        final float fx2 = x + width;
        final float fy2 = y + height;

        if (flipX) {
            float tmp = u;
            u = u2;
            u2 = tmp;
        }

        if (flipY) {
            float tmp = v;
            v = v2;
            v2 = tmp;
        }

        float color = this.color;
        int idx = this.vertexIndex;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;

        vertices[idx++] = x;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;

        vertices[idx++] = fx2;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = fx2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }

    @Override
    public void draw (Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 > triangles.length || vertexIndex + SPRITE_SIZE > vertices.length) //
            flush();

        int triangleIndex = this.triangleIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;

        final float u = srcX * invTexWidth;
        final float v = (srcY + srcHeight) * invTexHeight;
        final float u2 = (srcX + srcWidth) * invTexWidth;
        final float v2 = srcY * invTexHeight;
        final float fx2 = x + srcWidth;
        final float fy2 = y + srcHeight;

        float color = this.color;
        int idx = this.vertexIndex;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;

        vertices[idx++] = x;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;

        vertices[idx++] = fx2;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = fx2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }

    @Override
    public void draw (Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 > triangles.length || vertexIndex + SPRITE_SIZE > vertices.length) //
            flush();

        int triangleIndex = this.triangleIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;

        final float fx2 = x + width;
        final float fy2 = y + height;

        float color = this.color;
        int idx = this.vertexIndex;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;

        vertices[idx++] = x;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;

        vertices[idx++] = fx2;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = fx2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }

    @Override
    public void draw (Texture texture, float x, float y) {
        draw(texture, x, y, texture.getWidth(), texture.getHeight());
    }

    @Override
    public void draw (Texture texture, float x, float y, float width, float height) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 > triangles.length || vertexIndex + SPRITE_SIZE > vertices.length) //
            flush();

        int triangleIndex = this.triangleIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;

        final float fx2 = x + width;
        final float fy2 = y + height;
        final float u = 0;
        final float v = 1;
        final float u2 = 1;
        final float v2 = 0;

        float color = this.color;
        int idx = this.vertexIndex;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;

        vertices[idx++] = x;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;

        vertices[idx++] = fx2;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = fx2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }

    @Override
    public void draw (Texture texture, float[] spriteVertices, int offset, int count) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        final int triangleCount = count / SPRITE_SIZE * 6;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + triangleCount > triangles.length || vertexIndex + count > vertices.length) //
            flush();

        final int vertexIndex = this.vertexIndex;
        int triangleIndex = this.triangleIndex;
        short vertex = (short)(vertexIndex / VERTEX_SIZE);
        for (int n = triangleIndex + triangleCount; triangleIndex < n; triangleIndex += 6, vertex += 4) {
            triangles[triangleIndex] = vertex;
            triangles[triangleIndex + 1] = (short)(vertex + 1);
            triangles[triangleIndex + 2] = (short)(vertex + 2);
            triangles[triangleIndex + 3] = (short)(vertex + 2);
            triangles[triangleIndex + 4] = (short)(vertex + 3);
            triangles[triangleIndex + 5] = vertex;
        }
        this.triangleIndex = triangleIndex;

        System.arraycopy(spriteVertices, offset, vertices, vertexIndex, count);
        this.vertexIndex += count;
    }

    @Override
    public void draw (TextureRegion region, float x, float y) {
        draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
    }

    @Override
    public void draw (TextureRegion region, float x, float y, float width, float height) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        Texture texture = region.texture;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 > triangles.length || vertexIndex + SPRITE_SIZE > vertices.length) //
            flush();

        int triangleIndex = this.triangleIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;

        final float fx2 = x + width;
        final float fy2 = y + height;
        final float u = region.u;
        final float v = region.v2;
        final float u2 = region.u2;
        final float v2 = region.v;

        float color = this.color;
        int idx = this.vertexIndex;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;

        vertices[idx++] = x;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;

        vertices[idx++] = fx2;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = fx2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }

    @Override
    public void draw (TextureRegion region, float x, float y, float originX, float originY, float width, float height,
                      float scaleX, float scaleY, float rotation) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        Texture texture = region.texture;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 > triangles.length || vertexIndex + SPRITE_SIZE > vertices.length) //
            flush();

        int triangleIndex = this.triangleIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;

        // bottom left and top right corner points relative to origin
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;

        // scale
        if (scaleX != 1 || scaleY != 1) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }

        // construct corner points, start from top left and go counter clockwise
        final float p1x = fx;
        final float p1y = fy;
        final float p2x = fx;
        final float p2y = fy2;
        final float p3x = fx2;
        final float p3y = fy2;
        final float p4x = fx2;
        final float p4y = fy;

        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;

        // rotate
        if (rotation != 0) {
            final float cos = MathUtils.cosDeg(rotation);
            final float sin = MathUtils.sinDeg(rotation);

            x1 = cos * p1x - sin * p1y;
            y1 = sin * p1x + cos * p1y;

            x2 = cos * p2x - sin * p2y;
            y2 = sin * p2x + cos * p2y;

            x3 = cos * p3x - sin * p3y;
            y3 = sin * p3x + cos * p3y;

            x4 = x1 + (x3 - x2);
            y4 = y3 - (y2 - y1);
        } else {
            x1 = p1x;
            y1 = p1y;

            x2 = p2x;
            y2 = p2y;

            x3 = p3x;
            y3 = p3y;

            x4 = p4x;
            y4 = p4y;
        }

        x1 += worldOriginX;
        y1 += worldOriginY;
        x2 += worldOriginX;
        y2 += worldOriginY;
        x3 += worldOriginX;
        y3 += worldOriginY;
        x4 += worldOriginX;
        y4 += worldOriginY;

        final float u = region.u;
        final float v = region.v2;
        final float u2 = region.u2;
        final float v2 = region.v;

        float color = this.color;
        int idx = this.vertexIndex;
        vertices[idx++] = x1;
        vertices[idx++] = y1;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;

        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;

        vertices[idx++] = x3;
        vertices[idx++] = y3;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = x4;
        vertices[idx++] = y4;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }

    @Override
    public void draw (TextureRegion region, float x, float y, float originX, float originY, float width, float height,
                      float scaleX, float scaleY, float rotation, boolean clockwise) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        Texture texture = region.texture;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 > triangles.length || vertexIndex + SPRITE_SIZE > vertices.length) //
            flush();

        int triangleIndex = this.triangleIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;

        // bottom left and top right corner points relative to origin
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;

        // scale
        if (scaleX != 1 || scaleY != 1) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }

        // construct corner points, start from top left and go counter clockwise
        final float p1x = fx;
        final float p1y = fy;
        final float p2x = fx;
        final float p2y = fy2;
        final float p3x = fx2;
        final float p3y = fy2;
        final float p4x = fx2;
        final float p4y = fy;

        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;

        // rotate
        if (rotation != 0) {
            final float cos = MathUtils.cosDeg(rotation);
            final float sin = MathUtils.sinDeg(rotation);

            x1 = cos * p1x - sin * p1y;
            y1 = sin * p1x + cos * p1y;

            x2 = cos * p2x - sin * p2y;
            y2 = sin * p2x + cos * p2y;

            x3 = cos * p3x - sin * p3y;
            y3 = sin * p3x + cos * p3y;

            x4 = x1 + (x3 - x2);
            y4 = y3 - (y2 - y1);
        } else {
            x1 = p1x;
            y1 = p1y;

            x2 = p2x;
            y2 = p2y;

            x3 = p3x;
            y3 = p3y;

            x4 = p4x;
            y4 = p4y;
        }

        x1 += worldOriginX;
        y1 += worldOriginY;
        x2 += worldOriginX;
        y2 += worldOriginY;
        x3 += worldOriginX;
        y3 += worldOriginY;
        x4 += worldOriginX;
        y4 += worldOriginY;

        float u1, v1, u2, v2, u3, v3, u4, v4;
        if (clockwise) {
            u1 = region.u2;
            v1 = region.v2;
            u2 = region.u;
            v2 = region.v2;
            u3 = region.u;
            v3 = region.v;
            u4 = region.u2;
            v4 = region.v;
        } else {
            u1 = region.u;
            v1 = region.v;
            u2 = region.u2;
            v2 = region.v;
            u3 = region.u2;
            v3 = region.v2;
            u4 = region.u;
            v4 = region.v2;
        }

        float color = this.color;
        int idx = this.vertexIndex;
        vertices[idx++] = x1;
        vertices[idx++] = y1;
        vertices[idx++] = color;
        vertices[idx++] = u1;
        vertices[idx++] = v1;

        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = x3;
        vertices[idx++] = y3;
        vertices[idx++] = color;
        vertices[idx++] = u3;
        vertices[idx++] = v3;

        vertices[idx++] = x4;
        vertices[idx++] = y4;
        vertices[idx++] = color;
        vertices[idx++] = u4;
        vertices[idx++] = v4;
        this.vertexIndex = idx;
    }

    @Override
    public void draw (TextureRegion region, float width, float height, Affine2 transform) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        Texture texture = region.texture;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 > triangles.length || vertexIndex + SPRITE_SIZE > vertices.length) //
            flush();

        int triangleIndex = this.triangleIndex;
        final int startVertex = vertexIndex / VERTEX_SIZE;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;

        // construct corner points
        float x1 = transform.m02;
        float y1 = transform.m12;
        float x2 = transform.m01 * height + transform.m02;
        float y2 = transform.m11 * height + transform.m12;
        float x3 = transform.m00 * width + transform.m01 * height + transform.m02;
        float y3 = transform.m10 * width + transform.m11 * height + transform.m12;
        float x4 = transform.m00 * width + transform.m02;
        float y4 = transform.m10 * width + transform.m12;

        float u = region.u;
        float v = region.v2;
        float u2 = region.u2;
        float v2 = region.v;

        float color = this.color;
        int idx = vertexIndex;
        vertices[idx++] = x1;
        vertices[idx++] = y1;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;

        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;

        vertices[idx++] = x3;
        vertices[idx++] = y3;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = x4;
        vertices[idx++] = y4;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        vertexIndex = idx;
    }

    public void drawRepeat (TextureRegion region, float width, float height, Affine2 transform, int repeatCount, Vector2 repeatOffset) {
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");
        if (6 * repeatCount > triangles.length) throw new IllegalArgumentException("drawRepeat batch is too large");
        if (SPRITE_SIZE * repeatCount > vertices.length) throw new IllegalArgumentException("drawRepeat batch is too large");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        Texture texture = region.texture;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 * repeatCount > triangles.length || vertexIndex + SPRITE_SIZE * repeatCount > vertices.length) //
            flush();


        float u = region.u;
        float v = region.v2;
        float u2 = region.u2;
        float v2 = region.v;
        float color = this.color;

        final boolean identityTransform = isIdentity(transform);

        // construct corner points
        Vector2 ll = new Vector2();
        Vector2 lr = new Vector2();
        Vector2 ur = new Vector2();
        Vector2 ul = new Vector2();
        for (int iRepeat = 0; iRepeat < repeatCount; iRepeat++) {

            ll.x = iRepeat * repeatOffset.x;
            ll.y = iRepeat * repeatOffset.y;

            lr.x = ll.x + width;
            lr.y = ll.y;

            ul.x = ll.x;
            ul.y = ll.y + height;

            ur.x = ll.x + width;
            ur.y = ll.y + height;

            if (!identityTransform) {
                transform.applyTo(ll);
                transform.applyTo(lr);
                transform.applyTo(ur);
                transform.applyTo(ul);
            }

            int triangleIndex = this.triangleIndex;
            final int startVertex = vertexIndex / VERTEX_SIZE;
            triangles[triangleIndex++] = (short)startVertex;
            triangles[triangleIndex++] = (short)(startVertex + 1);
            triangles[triangleIndex++] = (short)(startVertex + 2);
            triangles[triangleIndex++] = (short)(startVertex + 2);
            triangles[triangleIndex++] = (short)(startVertex + 3);
            triangles[triangleIndex++] = (short)startVertex;
            this.triangleIndex = triangleIndex;

            int idx = vertexIndex;
            vertices[idx++] = ll.x;
            vertices[idx++] = ll.y;
            vertices[idx++] = color;
            vertices[idx++] = u;
            vertices[idx++] = v;

            vertices[idx++] = ul.x;
            vertices[idx++] = ul.y;
            vertices[idx++] = color;
            vertices[idx++] = u;
            vertices[idx++] = v2;

            vertices[idx++] = ur.x;
            vertices[idx++] = ur.y;
            vertices[idx++] = color;
            vertices[idx++] = u2;
            vertices[idx++] = v2;

            vertices[idx++] = lr.x;
            vertices[idx++] = lr.y;
            vertices[idx++] = color;
            vertices[idx++] = u2;
            vertices[idx++] = v;
            this.vertexIndex = idx;

        }

    }
    
    public void drawRepeat (TextureRegion region, Affine2 transform, float[] source) {
        
        final int repeatCount = source.length / 8;
        
        if (!drawing) throw new IllegalStateException("SpriteBatcher.begin must be called before draw.");
        if (6 * repeatCount > triangles.length) throw new IllegalArgumentException("drawRepeat batch is too large");
        if (SPRITE_SIZE * repeatCount > vertices.length) throw new IllegalArgumentException("drawRepeat batch is too large");

        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;

        Texture texture = region.texture;
        if (texture != lastTexture)
            switchTexture(texture);
        else if (triangleIndex + 6 * repeatCount > triangles.length || vertexIndex + SPRITE_SIZE * repeatCount > vertices.length) //
            flush();


        float u = region.u;
        float v = region.v2;
        float u2 = region.u2;
        float v2 = region.v;
        float color = this.color;

        int iSrc = 0;
        final boolean identityTransform = isIdentity(transform);
        
        // construct corner points
        Vector2 ll = new Vector2();
        Vector2 lr = new Vector2();
        Vector2 ur = new Vector2();
        Vector2 ul = new Vector2();

        for (int iRepeat = 0; iRepeat < repeatCount; iRepeat++) {

            ll.x = source[iSrc++];
            ll.y = source[iSrc++];

            ul.x = source[iSrc++];
            ul.y = source[iSrc++];

            ur.x = source[iSrc++];
            ur.y = source[iSrc++];

            lr.x = source[iSrc++];
            lr.y = source[iSrc++];

            if (!identityTransform) {
                transform.applyTo(ll);
                transform.applyTo(lr);
                transform.applyTo(ur);
                transform.applyTo(ul);
            }

            int triangleIndex = this.triangleIndex;
            final int startVertex = vertexIndex / VERTEX_SIZE;
            triangles[triangleIndex++] = (short)startVertex;
            triangles[triangleIndex++] = (short)(startVertex + 1);
            triangles[triangleIndex++] = (short)(startVertex + 2);
            triangles[triangleIndex++] = (short)(startVertex + 2);
            triangles[triangleIndex++] = (short)(startVertex + 3);
            triangles[triangleIndex++] = (short)startVertex;
            this.triangleIndex = triangleIndex;

            int idx = vertexIndex;
            vertices[idx++] = ll.x;
            vertices[idx++] = ll.y;
            vertices[idx++] = color;
            vertices[idx++] = u;
            vertices[idx++] = v;

            vertices[idx++] = ul.x;
            vertices[idx++] = ul.y;
            vertices[idx++] = color;
            vertices[idx++] = u;
            vertices[idx++] = v2;

            vertices[idx++] = ur.x;
            vertices[idx++] = ur.y;
            vertices[idx++] = color;
            vertices[idx++] = u2;
            vertices[idx++] = v2;

            vertices[idx++] = lr.x;
            vertices[idx++] = lr.y;
            vertices[idx++] = color;
            vertices[idx++] = u2;
            vertices[idx++] = v;
            this.vertexIndex = idx;

        }

    }

    private final boolean isIdentity(Affine2 transform) {
        return transform.m00 == 1.0f &&
                transform.m01 == 0.0f &&
                transform.m02 == 0.0f &&
                transform.m10 == 0.0f &&
                transform.m11 == 1.0f &&
                transform.m12 == 0.0f;
    }

    @Override
    public void flush () {
        if (vertexIndex == 0) return;

        renderCalls++;
        totalRenderCalls++;
        int trianglesInBatch = triangleIndex;
        if (trianglesInBatch > maxTrianglesInBatch) maxTrianglesInBatch = trianglesInBatch;

        lastTexture.bind();
        Mesh mesh = this.mesh;
        mesh.setVertices(vertices, 0, vertexIndex);
        mesh.setIndices(triangles, 0, triangleIndex);
        if (blendingDisabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        } else {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            if (blendSrcFunc != -1) Gdx.gl.glBlendFunc(blendSrcFunc, blendDstFunc);
        }

        mesh.render(customShader != null ? customShader : shader, GL20.GL_TRIANGLES, 0, trianglesInBatch);

        vertexIndex = 0;
        triangleIndex = 0;
    }

    @Override
    public void disableBlending () {
        flush();
        blendingDisabled = true;
    }

    @Override
    public void enableBlending () {
        flush();
        blendingDisabled = false;
    }

    @Override
    public void setBlendFunction (int srcFunc, int dstFunc) {
        if (blendSrcFunc == srcFunc && blendDstFunc == dstFunc) return;
        flush();
        blendSrcFunc = srcFunc;
        blendDstFunc = dstFunc;
    }

    @Override
    public int getBlendSrcFunc () {
        return blendSrcFunc;
    }

    @Override
    public int getBlendDstFunc () {
        return blendDstFunc;
    }

    @Override
    public void dispose () {
        mesh.dispose();
        if (ownsShader && shader != null) shader.dispose();
    }

    @Override
    public Matrix4 getProjectionMatrix () {
        return projectionMatrix;
    }

    @Override
    public Matrix4 getTransformMatrix () {
        return transformMatrix;
    }

    @Override
    public void setProjectionMatrix (Matrix4 projection) {
        if (drawing) flush();
        projectionMatrix.set(projection);
        if (drawing) setupMatrices();
    }

    @Override
    public void setTransformMatrix (Matrix4 transform) {
        if (drawing) flush();
        transformMatrix.set(transform);
        if (drawing) setupMatrices();
    }

    private void setupMatrices () {
        combinedMatrix.set(projectionMatrix).mul(transformMatrix);
        if (customShader != null) {
            customShader.setUniformMatrix("u_projTrans", combinedMatrix);
            customShader.setUniformi("u_texture", 0);
        } else {
            shader.setUniformMatrix("u_projTrans", combinedMatrix);
            shader.setUniformi("u_texture", 0);
        }
    }

    private void switchTexture (Texture texture) {
        flush();
        lastTexture = texture;
        invTexWidth = 1.0f / texture.getWidth();
        invTexHeight = 1.0f / texture.getHeight();
    }

    @Override
    public void setShader (ShaderProgram shader) {
        if (drawing) {
            flush();
            if (customShader != null)
                customShader.end();
            else
                this.shader.end();
        }
        customShader = shader;
        if (drawing) {
            if (customShader != null)
                customShader.begin();
            else
                this.shader.begin();
            setupMatrices();
        }
    }

    @Override
    public ShaderProgram getShader () {
        if (customShader == null) {
            return shader;
        }
        return customShader;
    }

    @Override
    public boolean isBlendingEnabled () {
        return !blendingDisabled;
    }

    @Override
    public boolean isDrawing () {
        return drawing;
    }
}
