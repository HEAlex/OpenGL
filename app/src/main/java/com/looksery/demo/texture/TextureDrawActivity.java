package com.looksery.demo.texture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.looksery.demo.R;
import com.looksery.demo.util.ShaderGLUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureDrawActivity extends Activity implements GLSurfaceView.Renderer {

    private static final String TAG = "TriangleActivity";

    private GLSurfaceView mGLSurfaceView;

    private final static String VERTEX_SHADER_SOURCE =
            "attribute vec4 aPosition;" +
            "attribute vec2 aTexPosition;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  gl_Position = aPosition;" +
            "  vTexPosition = aTexPosition;" +
            "}";

    private final static String FRAGMENT_SHADER_SOURCE =
            "precision mediump float;" +
            "uniform sampler2D uTexture;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  gl_FragColor = texture2D(uTexture, vTexPosition);" +
            "}";

    public static final float[] TEXTURE_COORDS = new float[]{
            0f, 0f,
            1, 0f,
            0f, 1f,
            1f, 1f
    };
    public static final float[] GEOMETRY = new float[]{
            -1f, 1f, 0f, 1f,
            1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 1f,
            1f, -1f, 0f, 1f
    };


    private FloatBuffer mGeometryBuffer;
    private FloatBuffer mTextureBuffer;

    private int mProgram = GLES20.GL_NONE;
    private int mTexture = GLES20.GL_NONE;

    private int mPositionLocation;
    private int mTexPositionLocation;
    private int mUTextureLocation;

    private Bitmap mPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(this);

        setContentView(mGLSurfaceView);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 0f, 0f);

        mProgram = ShaderGLUtils.loadProgram(VERTEX_SHADER_SOURCE, FRAGMENT_SHADER_SOURCE);
        GLES20.glUseProgram(mProgram);

        mPositionLocation = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTexPositionLocation = GLES20.glGetAttribLocation(mProgram, "aTexPosition");
        mUTextureLocation = GLES20.glGetUniformLocation(mProgram, "uTexture");


        mPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
        mTexture = ShaderGLUtils.createTextureWithBitmap(mPhoto);

        mGeometryBuffer = ShaderGLUtils.floatBufferDirectFromArray(GEOMETRY);
        mTextureBuffer = ShaderGLUtils.floatBufferDirectFromArray(TEXTURE_COORDS);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        int square = Math.min(width, height);
        GLES20.glViewport((width - square) / 2, (height - square) / 2, square, square);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glVertexAttribPointer(mPositionLocation, 4, GLES20.GL_FLOAT, false, 0, mGeometryBuffer);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        GLES20.glVertexAttribPointer(mTexPositionLocation, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(mTexPositionLocation);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
        GLES20.glUniform1i(mUTextureLocation, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
