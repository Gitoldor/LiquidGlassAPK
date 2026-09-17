package com.liquidglass.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show our HTML page
        WebView web = new WebView(this);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);

        web.loadUrl("file:///android_asset/index.html");

        setContentView(web);

        // Ask for floating permission
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

            startService(
                new Intent(this, FloatingService.class)
            );
        }
    }
}