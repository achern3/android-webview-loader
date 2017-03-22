package com.alanchern.webviewloader;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

/**
 * Created by alanchern on 3/22/17.
 */

public class App extends Application {
    private static final String RESTART_ACTIVITY = ".RESTART_ACTIVITY";
    private static final String ERROR_MESSAGE = "error_message";

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                Log.e(App.class.getSimpleName(), "uncaughtException()");
                restartApp(e);
            }
        });
    }

    private void restartApp(Throwable e) {
        Intent intent = new Intent();
        intent.setAction(RESTART_ACTIVITY);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        intent.putExtra(ERROR_MESSAGE, e.toString());
        startActivity(intent);

        System.exit(1); // kill off the crashed app
    }
}
