package com.alanchern.webviewloader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends BaseActivity {
    /*** screen saver images need to go into 'assets/screen_saver_images' folder ***/
    /*** local html/css files need to go into 'assets/local' folder ***/

    /*** control main page url ***/
    private static final String MAIN_PAGE = "http://google.com";

    /*** control local html file ***/
    private static final String LOCAL_PAGE = "file:///android_asset/local/floor.html";

    /*** control inactive time before showing screen saver ***/
    private static final int TIMEOUT = 60; // seconds

    private WebView mWebView;
    private Handler mTimerHandler = new Handler();
    private Runnable mTimerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTimerHandler.postDelayed(mTimerRunnable, TIMEOUT * 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    private void init() {
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient());
        loadMainPage();

        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                loadMainPage();
                mTimerHandler.removeCallbacks(mTimerRunnable);

                Intent intent = new Intent(MainActivity.this, ScreenSaverActivity.class);
                startActivity(intent);
            }
        };
    }

    private void loadMainPage() {
        if (mWebView != null) {
            // load website
            mWebView.loadUrl(MAIN_PAGE);

            // load local html file
            // mWebView.loadUrl(LOCAL_PAGE);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        mTimerHandler.removeCallbacks(mTimerRunnable);
        mTimerHandler.postDelayed(mTimerRunnable, TIMEOUT * 1000);
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        }
        // super.onBackPressed();
    }
}
