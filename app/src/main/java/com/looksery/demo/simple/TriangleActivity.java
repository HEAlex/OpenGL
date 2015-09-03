package com.looksery.demo.simple;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.looksery.demo.util.ShaderGLUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TriangleActivity extends Activity implements GLSurfaceView.Renderer {

    private static final String TAG = "TriangleActivity";

    private static final String VERTEX_SHADER_CODE =
            "attribute vec4 position;" +
            "void main() {" +
            "   gl_Position = position;" +
            "}";

    private static final String FRAGMENT_SHADER_CODE =
            "void main() {" +
            "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);" +
            "}";

    private GLSurfaceView mGLSurfaceView;

    private int mProgram;
    private FloatBuffer mPoints;

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

        mProgram = ShaderGLUtils.loadProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);

        GLES20.glBindAttribLocation(mProgram, 0, "position");
        GLES20.glUseProgram(mProgram);

        float[] geometry = {
                -0.5f, -0.5f, 0f, 1f,
                0.5f, -0.5f, 0f, 1f,
                -0.5f,  0.5f, 0f, 1f
        };
        mPoints = ShaderGLUtils.floatBufferDirectFromArray(geometry);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glVertexAttribPointer(0, 4, GLES20.GL_FLOAT, false, 0, mPoints);
        GLES20.glEnableVertexAttribArray(0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);
    }
}
