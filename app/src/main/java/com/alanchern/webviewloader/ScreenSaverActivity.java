package com.alanchern.webviewloader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alanchern on 2/12/17.
 */

public class ScreenSaverActivity extends BaseActivity {
    private static final String TAG = "ScreenSaverActivity";
    private static final String IMAGE_PATH = "screen_saver_images";
    private static final String DEFAULT_PATH = "default_image";

    /*** control duration of each screen saver image ***/
    private static final int DURATION = 5; // seconds

    /*** server directory link ***/
    private static final String SERVER_DIRECTORY = "http://ocic.nthu.edu.tw/kiosk/";

    /*** number of images inside server directory ***/
    private static final int NUM_IMAGES = 9;

    private Handler mLoadImageHandler = new Handler();
    private Runnable mLoadImageRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_saver);

        loadServerImages();
    }

    private void loadServerImages() {
        final ImageView imageView = (ImageView) findViewById(R.id.image_view);

        mLoadImageRunnable = new Runnable() {
            private int count = 1;

            @Override
            public void run() {
                try {
                    imageView.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "image invisible");

                    String imageUrl = SERVER_DIRECTORY + count + ".jpg";
                    Glide.with(ScreenSaverActivity.this)
                            .load(imageUrl)
                            .asBitmap()
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                    imageView.setImageBitmap(bitmap);
                                    imageView.setVisibility(View.VISIBLE);
                                    Log.d(TAG, "current count: " + count);
                                }
                            });

                    if (count == NUM_IMAGES) {
                        count = 1;
                    } else {
                        count++;
                    }

                    mLoadImageHandler.postDelayed(mLoadImageRunnable, DURATION * 1000);
                } catch (Exception e) {
                    Log.e(TAG, "loadServerImages() exception!");
                    loadLocalImages();
                }
            }
        };

        mLoadImageHandler.post(mLoadImageRunnable);
    }

    private void loadLocalImages() {
        final ImageView imageView = (ImageView) findViewById(R.id.image_view);

        try {
            final String[] imageNames = getAssets().list(IMAGE_PATH);

            mLoadImageRunnable = new Runnable() {
                private int count = 0;

                @Override
                public void run() {
                    try {
                        InputStream inputStream = null;

                        if (imageNames.length == 1) {
                            inputStream = getAssets().open(IMAGE_PATH + File.separator + imageNames[0]);
                        } else if (imageNames.length > 1) {
                            inputStream = getAssets().open(IMAGE_PATH + File.separator + imageNames[count]);

                            if (count == imageNames.length - 1) {
                                count = 0;
                            } else {
                                count++;
                            }

                            mLoadImageHandler.postDelayed(mLoadImageRunnable, DURATION * 1000);
                        } else {
                            String[] names = getAssets().list(DEFAULT_PATH);
                            if (names.length > 0) {
                                inputStream = getAssets().open(DEFAULT_PATH + File.separator + names[0]);
                            } else {
                                onUserInteraction();
                            }
                        }

                        imageView.setImageDrawable(Drawable.createFromStream(inputStream, null));
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            };

            mLoadImageHandler.post(mLoadImageRunnable);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        mLoadImageHandler.removeCallbacks(mLoadImageRunnable);
        finish();
    }
}
