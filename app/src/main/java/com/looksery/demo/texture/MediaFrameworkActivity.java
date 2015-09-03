package com.looksery.demo.texture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.looksery.demo.util.DrawTextureShader;
import com.looksery.demo.R;
import com.looksery.demo.util.ShaderGLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MediaFrameworkActivity extends Activity {

    private static final String TAG = "MediaFrameworkActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView GLSurfaceView = new GLSurfaceView(this);
        GLSurfaceView.setEGLContextClientVersion(2);
        GLSurfaceView.Renderer renderer = new FilterRenderer(this);
        GLSurfaceView.setRenderer(renderer);
        setContentView(GLSurfaceView);
    }

    public static class FilterRenderer implements GLSurfaceView.Renderer {

        private final DrawTextureShader mDrawTextureShader;

        private final Bitmap mPhoto;
        private int mWidth;
        private int mHeight;

        private Effect mEffect;
        private EffectContext mEffectContext;

        private int mInputTexture;
        private int mResultTexture;

        public FilterRenderer(Context context) {
            mPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.lenna);
            mDrawTextureShader = new DrawTextureShader();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0f, 1f, 0f, 0f);
            mInputTexture = ShaderGLUtils.createTextureWithBitmap(mPhoto);
            mResultTexture = ShaderGLUtils.createTexture();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            applyEffect();

            int square = Math.min(mWidth, mHeight);
            GLES20.glViewport((mWidth - square) / 2, (mHeight - square) / 2, square, square);
            mDrawTextureShader.draw(mResultTexture);
        }

        private void applyEffect() {
            if (mEffectContext == null) {
                mEffectContext = EffectContext.createWithCurrentGlContext();
            }
            if (mEffect != null){
                mEffect.release();
            }
            documentaryEffect();
        }

        private void grayScaleEffect(){
            EffectFactory factory = mEffectContext.getFactory();
            mEffect = factory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
            mEffect.apply(mInputTexture, mPhoto.getWidth(), mPhoto.getHeight(), mResultTexture);
        }

        private void documentaryEffect(){
            EffectFactory factory = mEffectContext.getFactory();
            mEffect = factory.createEffect(EffectFactory.EFFECT_DOCUMENTARY);
            mEffect.apply(mInputTexture, mPhoto.getWidth(), mPhoto.getHeight(), mResultTexture);
        }

        private void effectSharpenEffect(){
            EffectFactory factory = mEffectContext.getFactory();
            mEffect = factory.createEffect(EffectFactory.EFFECT_SHARPEN);
            mEffect.setParameter("scale", 0.5f);
            mEffect.apply(mInputTexture, mPhoto.getWidth(), mPhoto.getHeight(), mResultTexture);
        }

        private void brightnessEffect(){
            EffectFactory factory = mEffectContext.getFactory();
            mEffect = factory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
            mEffect.setParameter("brightness", 2f);
            mEffect.apply(mInputTexture, mPhoto.getWidth(), mPhoto.getHeight(), mResultTexture);
        }

        private void vignetteEffect(){
            EffectFactory factory = mEffectContext.getFactory();
            mEffect = factory.createEffect(EffectFactory.EFFECT_VIGNETTE);
            mEffect.setParameter("scale", 1f);
            mEffect.apply(mInputTexture, mPhoto.getWidth(), mPhoto.getHeight(), mResultTexture);
        }

        private void posterizeEffect(){
            EffectFactory factory = mEffectContext.getFactory();
            mEffect = factory.createEffect(EffectFactory.EFFECT_POSTERIZE);
            mEffect.apply(mInputTexture, mPhoto.getWidth(), mPhoto.getHeight(), mResultTexture);
        }
    }
}
