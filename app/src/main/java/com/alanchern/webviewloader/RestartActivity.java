package com.alanchern.webviewloader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by alanchern on 3/22/17.
 */

public class RestartActivity extends AppCompatActivity {
    private static final String ERROR_MESSAGE = "error_message";
    private static final String RESTART_KEY = "restart_key";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String error = getIntent().getExtras().getString(ERROR_MESSAGE);
        if (error != null) {
            Log.e("APP CRASHED", error);
        }

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int numberRestarts = preferences.getInt(RESTART_KEY, 0);
        Log.d("Number of restarts", String.valueOf(numberRestarts));
        SharedPreferences.Editor editor = preferences.edit();

        if (numberRestarts == 0) {
            // prevents infinite crash cycles
            // editor.putInt(RESTART_KEY, 1).apply();

            restartApp();
        } else {
            editor.putInt(RESTART_KEY, 0).apply();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.app_crashed_restart).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    restartApp();
                }
            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).setCancelable(false).create().show();
        }
    }

    private void restartApp() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.recovering_from_crash));
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        finish();
    }
}
