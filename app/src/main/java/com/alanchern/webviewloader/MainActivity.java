package com.alanchern.webviewloader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
    /*** screen saver images need to go into 'assets/screen_saver_images' folder ***/
    /*** local html/css files need to go into 'assets/local' folder ***/

    /*** control main page url ***/
    private static final String MAIN_PAGE = "http://ocic.nthu.edu.tw/cii/floor_guide.html";

    /*** control local html file ***/
    private static final String LOCAL_PAGE = "file:///android_asset/local/floor.html";

    /*** control inactive time before showing screen saver ***/
    private static final int TIMEOUT = 40; // seconds

    private Button mHomeButton;
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
        mHomeButton = (Button) findViewById(R.id.home_button);
        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                }
            }
        });

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new CustomClient());

        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

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
            if (isNetworkAvailable()) {
                // load website
                mWebView.loadUrl(MAIN_PAGE);
            } else {
                // load local html file
                mWebView.loadUrl(LOCAL_PAGE);
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        return (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected());
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

    private class CustomClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.endsWith(".pdf")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "application/pdf");
                PackageManager packageManager = getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                    return true;
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.cannot_read_pdf), Toast.LENGTH_SHORT).show();
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
