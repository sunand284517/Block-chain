package com.manasshaktiiui.bridge;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.manasshaktiiui.sensors.WatchmanService;
import com.manasshaktiiui.ai.ManasShaktiiAgent;
import com.manasshaktiiui.sensors.BehavioralEvent;

import java.util.HashMap;

/**
 * React Native TurboModule/NativeModule Bridge.
 * Exposes monitoring control and event streams to React Native without placing LLM or sensor work on JS thread.
 */
public class ManasShaktiiModule extends ReactContextBaseJavaModule {

    private static final String TAG = "ManasShaktiiModule";
    private static ReactApplicationContext reactContextRef;

    public ManasShaktiiModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContextRef = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "ManasShaktiiNativeModule";
    }

    @ReactMethod
    public void startMonitoring(Promise promise) {
        try {
            Intent serviceIntent = new Intent(getReactApplicationContext(), WatchmanService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getReactApplicationContext().startForegroundService(serviceIntent);
            } else {
                getReactApplicationContext().startService(serviceIntent);
            }
            promise.resolve(true);
        } catch (Exception e) {
            Log.e(TAG, "Error starting Watchman service from RN", e);
            promise.reject("START_SERVICE_ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void stopMonitoring(Promise promise) {
        try {
            Intent serviceIntent = new Intent(getReactApplicationContext(), WatchmanService.class);
            getReactApplicationContext().stopService(serviceIntent);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("STOP_SERVICE_ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void getMonitoringStatus(Promise promise) {
        WritableMap map = Arguments.createMap();
        map.putBoolean("isMonitoringActive", WatchmanService.isRunning());
        map.putBoolean("isOfflineMode", true);
        map.putString("localRuntime", "MediaPipe/Gemma 2B INT4");
        promise.resolve(map);
    }

    @ReactMethod
    public void triggerManualIntervention(Promise promise) {
        try {
            BehavioralEvent manualEvent = new BehavioralEvent("MANUAL_TRIGGER", 1.0, 0, new HashMap<>());
            ManasShaktiiAgent.getInstance(getReactApplicationContext()).onBehavioralEvent(manualEvent);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("TRIGGER_ERROR", e.getMessage());
        }
    }

    public static void sendInterventionEventToJs(String actionName, String message) {
        if (reactContextRef != null && reactContextRef.hasActiveCatalystInstance()) {
            WritableMap params = Arguments.createMap();
            params.putString("action", actionName);
            params.putString("message", message);
            params.putDouble("timestamp", System.currentTimeMillis());

            reactContextRef.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("onInterventionTriggered", params);
        }
    }

    public static void sendBehavioralEventToJs(BehavioralEvent event) {
        if (reactContextRef != null && reactContextRef.hasActiveCatalystInstance() && event != null) {
            WritableMap params = Arguments.createMap();
            params.putString("event", event.getEventType());
            params.putDouble("confidence", event.getConfidence());
            params.putDouble("duration", event.getDurationSeconds());

            reactContextRef.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("onBehavioralEvent", params);
        }
    }
}
