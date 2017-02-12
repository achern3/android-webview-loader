package com.alanchern.webviewloader;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

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

    private Handler mLoadImageHandler = new Handler();
    private Runnable mLoadImageRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_saver);

        init();
    }

    private void init() {
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
