package com.looksery.demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.looksery.demo.util.ShaderGLUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;

public class GPUImageActivity extends Activity {

    private static final String TAG = "MediaFrameworkActivity";

    private FilterRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        mRenderer = new FilterRenderer(this);
        glSurfaceView.setRenderer(mRenderer);
        setContentView(glSurfaceView);
    }

    public static class FilterRenderer implements GLSurfaceView.Renderer {

        private final Bitmap mPhoto;
        private final GPUImageFilter mGPUImageFilter;
        private int mWidth;
        private int mHeight;

        private Effect mEffect;
        private EffectContext mEffectContext;

        private int mInputTexture;
        private FloatBuffer mGeometryBuffer;
        private FloatBuffer mTextureBuffer;

        public FilterRenderer(Context context) {
            mPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.lenna);
            GPUImageGaussianBlurFilter gpuImageGaussianBlurFilter = new GPUImageGaussianBlurFilter();
            gpuImageGaussianBlurFilter.setBlurSize(20);
            mGPUImageFilter = gpuImageGaussianBlurFilter;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0f, 0f, 0f, 0f);

            mInputTexture = ShaderGLUtils.createTextureWithBitmap(mPhoto);
            mGPUImageFilter.init();
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

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mWidth = width;
            mHeight = height;
            mGPUImageFilter.onOutputSizeChanged(mWidth, mHeight);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            setViewport();
            mGPUImageFilter.onDraw(mInputTexture, mGeometryBuffer, mTextureBuffer);
        }

        private void setViewport() {
            int square = Math.min(mWidth, mHeight);
            GLES20.glViewport((mWidth - square) / 2, (mHeight - square) / 2, square, square);
        }
    }
}
