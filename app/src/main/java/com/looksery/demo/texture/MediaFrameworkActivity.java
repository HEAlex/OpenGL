package com.looksery.demo.texture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.looksery.demo.R;
import com.looksery.demo.util.DrawTextureShader;
import com.looksery.demo.util.ShaderGLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MediaFrameworkActivity extends Activity {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.effects, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mRenderer.setEffect(item.getItemId());
        return true;
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
            GLES20.glClearColor(0f, 0f, 0f, 0f);

            mInputTexture = ShaderGLUtils.createTextureWithBitmap(mPhoto);
            mResultTexture = ShaderGLUtils.createTexture();

            setEffect(R.id.grain);
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
            setViewport();
            mDrawTextureShader.draw(mResultTexture);
        }

        private void setViewport() {
            int square = Math.min(mWidth, mHeight);
            GLES20.glViewport((mWidth - square) / 2, (mHeight - square) / 2, square, square);
        }

        private synchronized void applyEffect() {
            if (mEffect != null) {
                mEffect.apply(mInputTexture, mPhoto.getWidth(), mPhoto.getHeight(), mResultTexture);
            }
        }

        public synchronized void setEffect(int mEffectId ) {
            if (mEffectContext == null) {
                mEffectContext = EffectContext.createWithCurrentGlContext();
            }
            EffectFactory effectFactory = mEffectContext.getFactory();
            if (mEffect != null) {
                mEffect.release();
            }
            switch (mEffectId) {
                case R.id.none:
                    mEffect = null;
                    break;
                case R.id.autofix:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_AUTOFIX);
                    mEffect.setParameter("scale", 0.5f);
                    break;
                case R.id.bw:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BLACKWHITE);
                    mEffect.setParameter("black", .1f);
                    mEffect.setParameter("white", .7f);
                    break;
                case R.id.brightness:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
                    mEffect.setParameter("brightness", 2.0f);
                    break;
                case R.id.contrast:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CONTRAST);
                    mEffect.setParameter("contrast", 1.4f);
                    break;
                case R.id.crossprocess:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CROSSPROCESS);
                    break;
                case R.id.documentary:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DOCUMENTARY);
                    break;
                case R.id.duotone:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DUOTONE);
                    mEffect.setParameter("first_color", Color.YELLOW);
                    mEffect.setParameter("second_color", Color.DKGRAY);
                    break;
                case R.id.filllight:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FILLLIGHT);
                    mEffect.setParameter("strength", .8f);
                    break;
                case R.id.fisheye:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FISHEYE);
                    mEffect.setParameter("scale", .5f);
                    break;
                case R.id.flipvert:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FLIP);
                    mEffect.setParameter("vertical", true);
                    break;
                case R.id.fliphor:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FLIP);
                    mEffect.setParameter("horizontal", true);
                    break;
                case R.id.grain:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAIN);
                    mEffect.setParameter("strength", 1.0f);
                    break;
                case R.id.grayscale:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
                    break;
                case R.id.lomoish:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_LOMOISH);
                    break;
                case R.id.negative:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_NEGATIVE);
                    break;
                case R.id.posterize:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_POSTERIZE);
                    break;
                case R.id.rotate:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_ROTATE);
                    mEffect.setParameter("angle", 180);
                    break;
                case R.id.saturate:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SATURATE);
                    mEffect.setParameter("scale", .5f);
                    break;
                case R.id.sepia:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SEPIA);
                    break;
                case R.id.sharpen:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SHARPEN);
                    break;
                case R.id.temperature:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TEMPERATURE);
                    mEffect.setParameter("scale", .9f);
                    break;
                case R.id.tint:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TINT);
                    mEffect.setParameter("tint", Color.MAGENTA);
                    break;
                case R.id.vignette:
                    mEffect = effectFactory.createEffect(EffectFactory.EFFECT_VIGNETTE);
                    mEffect.setParameter("scale", .5f);
                    break;
                default:
                    break;
            }
        }
    }
}
