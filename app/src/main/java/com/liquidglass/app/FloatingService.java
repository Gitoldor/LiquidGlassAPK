package com.liquidglass.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private TextView button;
    private WindowManager.LayoutParams params;

    private int startX;
    private int startY;
    private float touchX;
    private float touchY;

    private boolean expanded = false;

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager =
                (WindowManager) getSystemService(WINDOW_SERVICE);

        button = new TextView(this);

        button.setText("◉");
        button.setTextColor(Color.WHITE);
        button.setTextSize(24);
        button.setGravity(Gravity.CENTER);

        GradientDrawable glass = new GradientDrawable();
        glass.setColor(Color.argb(170, 255, 255, 255));
        glass.setShape(GradientDrawable.OVAL);

        button.setBackground(glass);

        params = new WindowManager.LayoutParams(
                70,
                70,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.graphics.PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 250;
        params.y = 500;

        button.setOnTouchListener((view, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    startX = params.x;
                    startY = params.y;

                    touchX = event.getRawX();
                    touchY = event.getRawY();

                    return true;

                case MotionEvent.ACTION_MOVE:

                    params.x = startX +
                            (int)(event.getRawX() - touchX);

                    params.y = startY +
                            (int)(event.getRawY() - touchY);

                    windowManager.updateViewLayout(
                            button,
                            params
                    );

                    return true;

                case MotionEvent.ACTION_UP:

                    float dx =
                            Math.abs(event.getRawX() - touchX);

                    float dy =
                            Math.abs(event.getRawY() - touchY);

                    // If finger barely moved, treat it as a tap
                    if (dx < 20 && dy < 20) {
                        toggleExpanded();
                    }

                    return true;
            }

            return true;
        });

        windowManager.addView(button, params);
    }


    private void toggleExpanded() {

        if (!expanded) {

            expanded = true;

            params.width = 220;
            params.height = 80;

            button.setText("  ◉   Liquid Glass");

            GradientDrawable glass =
                    new GradientDrawable();

            glass.setColor(
                    Color.argb(190, 255, 255, 255)
            );

            glass.setCornerRadius(40);

            button.setBackground(glass);

        } else {

            expanded = false;

            params.width = 70;
            params.height = 70;

            button.setText("◉");

            GradientDrawable glass =
                    new GradientDrawable();

            glass.setColor(
                    Color.argb(170, 255, 255, 255)
            );

            glass.setShape(GradientDrawable.OVAL);

            button.setBackground(glass);
        }

        windowManager.updateViewLayout(
                button,
                params
        );
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        if (button != null) {
            windowManager.removeView(button);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}