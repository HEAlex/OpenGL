package com.looksery.demo.util;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public class MediaEffectsFilter {

    private static final String TAG = "DrawTextureShader";

    private String VERTEXT_SOURCE =
            "attribute vec2 aPosition;" +
            "attribute vec2 aTexPosition;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  gl_Position = vec4(aPosition, 0.0, 1.0);" +
            "  vTexPosition = aTexPosition;" +
            "}";

    private String FRAGMENT_SOURCE =
            "precision mediump float;" +
            "uniform sampler2D uTexture;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  gl_FragColor = texture2D(uTexture, vTexPosition);" +
            "}";

    private int program = GLES20.GL_NONE;

    private FloatBuffer mGeometryBuffer;
    private FloatBuffer mTextureBuffer;

    private int mPositionLocation;
    private int mTexPositionLocation;
    private int mUTextureLocation;

    private boolean initialized() {
        return program != GLES20.GL_NONE;
    }

    public void initialise() {
        program = ShaderGLUtils.loadProgram(VERTEXT_SOURCE, FRAGMENT_SOURCE);

        mPositionLocation = GLES20.glGetAttribLocation(program, "aPosition");
        mTexPositionLocation = GLES20.glGetAttribLocation(program, "aTexPosition");
        mUTextureLocation = GLES20.glGetUniformLocation(program, "uTexture");

        float[] geometry = {
                -1f,  1f,
                1f,  1f,
                -1f, -1f,
                1f, -1f
        };
        mGeometryBuffer = ShaderGLUtils.floatBufferDirectFromArray(geometry);
        float[] textureCoords = {
                0f, 0f,
                1, 0f,
                0f, 1f,
                1f, 1f
        };
        mTextureBuffer = ShaderGLUtils.floatBufferDirectFromArray(textureCoords);
    }

    public void bind() {
        GLES20.glUseProgram(program);

        GLES20.glVertexAttribPointer(mPositionLocation, 2, GLES20.GL_FLOAT, false, 0, mGeometryBuffer);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        GLES20.glVertexAttribPointer(mTexPositionLocation, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(mTexPositionLocation);
    }

    public void draw(int texture) {
        if (!initialized()) {
            initialise();
        }
        bind();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(mUTextureLocation, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        unbind();
    }
    public void unbind() {
        GLES20.glDisableVertexAttribArray(mPositionLocation);
        GLES20.glDisableVertexAttribArray(mTexPositionLocation);
        GLES20.glUseProgram(GLES20.GL_NONE);
    }
}
