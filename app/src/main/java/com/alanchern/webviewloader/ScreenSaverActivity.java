package com.alanchern.webviewloader;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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

    /*** image urls ***/
    private static final String[] urlArray = {"https://scontent-hkg3-1.xx.fbcdn.net/v/t1.0-9/11140317_1140894485924623_737005933379357626_n.jpg?oh=02190048332c4b84240f07015a1a9eb1&oe=598EDBF0",
            "http://farm3.static.flickr.com/2500/4263017598_2c8b74e749.jpg",
            "http://farm5.static.flickr.com/4017/4370647903_df672a8171.jpg"};

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
            private int count = 0;

            @Override
            public void run() {
                if (urlArray.length > 0) {
                    Glide.with(ScreenSaverActivity.this).load(urlArray[count]).into(imageView);

                    if (count == urlArray.length - 1) {
                        count = 0;
                    } else {
                        count++;
                    }
                } else {
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
