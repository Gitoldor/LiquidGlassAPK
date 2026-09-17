package com.liquidglass.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private WebView webView;
    private WindowManager.LayoutParams params;

    private int startX;
    private int startY;

    private float touchX;
    private float touchY;

    private boolean expanded = false;

    private long lastTap = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager =
                (WindowManager) getSystemService(WINDOW_SERVICE);

        webView = new WebView(this);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        webView.setBackgroundColor(Color.TRANSPARENT);

        webView.loadUrl(
                "file:///android_asset/index.html"
        );

        params = new WindowManager.LayoutParams(
                70,
                70,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.graphics.PixelFormat.TRANSLUCENT
        );

        params.gravity =
                Gravity.TOP | Gravity.START;

        params.x = 250;
        params.y = 500;

        webView.setOnTouchListener(
                (view, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    startX = params.x;
                    startY = params.y;

                    touchX = event.getRawX();
                    touchY = event.getRawY();

                    return true;


                case MotionEvent.ACTION_MOVE:

                    params.x =
                            startX +
                            (int)(event.getRawX() - touchX);

                    params.y =
                            startY +
                            (int)(event.getRawY() - touchY);

                    windowManager.updateViewLayout(
                            webView,
                            params
                    );

                    return true;


                case MotionEvent.ACTION_UP:

                    float dx =
                            Math.abs(
                                event.getRawX() - touchX
                            );

                    float dy =
                            Math.abs(
                                event.getRawY() - touchY
                            );

                    if (dx < 20 && dy < 20) {

                        long now =
                                System.currentTimeMillis();

                        if (now - lastTap < 350) {

                            openYouTube();

                            lastTap = 0;

                        } else {

                            lastTap = now;

                            webView.postDelayed(
                                () -> {

                                    if (
                                        lastTap != 0 &&
                                        System.currentTimeMillis()
                                        - lastTap >= 350
                                    ) {

                                        toggleExpanded();

                                        lastTap = 0;
                                    }

                                },
                                360
                            );
                        }
                    }

                    return true;
            }

            return true;
        });

        windowManager.addView(
                webView,
                params
        );
    }


    private void toggleExpanded() {

        if (!expanded) {

            expanded = true;

            params.width = 220;
            params.height = 80;

        } else {

            expanded = false;

            params.width = 70;
            params.height = 70;
        }

        windowManager.updateViewLayout(
                webView,
                params
        );
    }


    private void openYouTube() {

        try {

            Intent intent =
                    getPackageManager()
                    .getLaunchIntentForPackage(
                        "com.google.android.youtube"
                    );

            if (intent != null) {

                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                );

                startActivity(intent);

            } else {

                Intent browser =
                        new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                "https://www.youtube.com/"
                            )
                        );

                browser.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                );

                startActivity(browser);
            }

        } catch (Exception e) {
        }
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        if (webView != null) {

            windowManager.removeView(webView);

            webView.destroy();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}