package com.manasshaktiiui.actions;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.manasshaktiiui.R;

/**
 * Android WindowManager Overlay Action handler.
 * Renders on-screen conscious-friction overlays and breath gates with pressure-sensitive touch dismissal.
 */
public class OverlayAction {

    private static final String TAG = "OverlayAction";
    private static View activeOverlayView;
    private static WindowManager windowManager;

    public static void showOverlay(Context context, String message, int durationSeconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            Log.w(TAG, "Cannot show overlay: SYSTEM_ALERT_WINDOW permission not granted.");
            return;
        }

        dismissOverlay();

        try {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null) return;

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                            WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
            );
            params.gravity = Gravity.CENTER;

            LayoutInflater inflater = LayoutInflater.from(context);
            activeOverlayView = inflater.inflate(R.layout.overlay_watchman, null);

            TextView text = activeOverlayView.findViewById(R.id.mentor_text);
            if (text != null) {
                text.setText(message);
            }

            // Pressure-sensitive dismissal check (> 0.7f touch pressure)
            activeOverlayView.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getPressure() > 0.7f) {
                        dismissOverlay();
                        Log.d(TAG, "Overlay dismissed via conscious pressure touch.");
                    }
                }
                v.performClick();
                return false;
            });

            Button closeButton = activeOverlayView.findViewById(R.id.close_button);
            if (closeButton != null) {
                closeButton.setOnClickListener(v -> dismissOverlay());
            }

            windowManager.addView(activeOverlayView, params);
            Log.d(TAG, "Mindful overlay presented on screen.");

        } catch (Exception e) {
            Log.e(TAG, "Failed to display overlay view", e);
        }
    }

    public static void dismissOverlay() {
        if (windowManager != null && activeOverlayView != null) {
            try {
                windowManager.removeView(activeOverlayView);
                Log.d(TAG, "Overlay dismissed.");
            } catch (Exception e) {
                Log.e(TAG, "Error dismissing overlay", e);
            } finally {
                activeOverlayView = null;
            }
        }
    }
}
