package com.liquidglass.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.Toast;
import android.media.projection.MediaProjectionManager;

public class MainActivity extends Activity {

    private static final int SCREEN_CAPTURE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView web = new WebView(this);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        web.loadUrl("file:///android_asset/index.html");

        setContentView(web);

        if (!Settings.canDrawOverlays(this)) {

            Toast.makeText(
                    this,
                    "Allow floating button permission",
                    Toast.LENGTH_LONG
            ).show();

            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
            );

            startActivity(intent);

        } else {

            requestScreenCapture();
        }
    }


    private void requestScreenCapture() {

        MediaProjectionManager manager =
                (MediaProjectionManager)
                getSystemService(
                        MEDIA_PROJECTION_SERVICE
                );

        Intent captureIntent =
                manager.createScreenCaptureIntent();

        startActivityForResult(
                captureIntent,
                SCREEN_CAPTURE_REQUEST
        );
    }


    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (
                requestCode ==
                SCREEN_CAPTURE_REQUEST
                &&
                resultCode == RESULT_OK
                &&
                data != null
        ) {

            // Screen capture permission granted.
            // We will connect the captured screen
            // to the Liquid Glass renderer next.

            startService(
                    new Intent(
                            this,
                            FloatingService.class
                    )
            );

        } else {

            Toast.makeText(
                    this,
                    "Screen capture permission is required",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}