
package uk.co.avodev.mobex.module;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.annotation.Nullable;
import android.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Objects;
import android.app.KeyguardManager;

public class MobexModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private void sendEvent(ReactContext reactContext,
            String eventName,
            @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    public MobexModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        registerBroadcastReceiver();
    }

    @Override
    public String getName() {
        return "MobexModule";
    }

    private final BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WritableMap params = Arguments.createMap();
            String action = "";
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                action = "ACTION_USER_PRESENT";
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                action = "ACTION_SCREEN_OFF";
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                action = "ACTION_SCREEN_ON";
            }
            params.putString("action", action);
            sendEvent(reactContext, "evt.rn.locked", params);

        }
    };

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        reactContext.registerReceiver(mScreenStateReceiver, filter);
    }

    @ReactMethod
    public void killApp(@Nullable Callback callbackBeforeExit) {
        if (callbackBeforeExit != null) {
            callbackBeforeExit.invoke();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    @ReactMethod
    public void isScreenLocked(Callback callback) {
        KeyguardManager keyguardManager = (KeyguardManager) reactContext.getSystemService(Context.KEYGUARD_SERVICE);
        boolean isLocked = keyguardManager.isKeyguardLocked();
        callback.invoke(null, isLocked);
    }

    @ReactMethod
    public void requestOverlayPermission(Promise promise) {
        
        String device = Build.MANUFACTURER;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this.reactContext)) {
                    if (device.equals("Xiaomi")) {
                        try {
                            // MIUI 8
                            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setData(Uri.fromParts("package", this.reactContext.getPackageName(), null));
                            localIntent.setClassName("com.miui.securitycenter",
                                    "com.miui.permcenter.permissions.PermissionsEditorActivity");
                            localIntent.putExtra("extra_pkgname", this.reactContext.getPackageName());
                            this.reactContext.startActivity(localIntent);
                            return;
                        } catch (Exception ignore) {
                        }
                        try {
                            // MIUI 5/6/7
                            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            localIntent.setData(Uri.fromParts("package", this.reactContext.getPackageName(), null));
                            localIntent.setClassName("com.miui.securitycenter",
                                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                            localIntent.putExtra("extra_pkgname", this.reactContext.getPackageName());
                            this.reactContext.startActivity(localIntent);
                            return;
                        } catch (Exception ignore) {
                        }
                        // Otherwise jump to application details
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", this.reactContext.getPackageName(), null);
                        intent.setData(uri);
                        this.reactContext.startActivity(intent);
                    } else {
                        Intent overlaySettings = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + this.reactContext.getPackageName()));
                        // reactContext.startActivityForResult(overlaySettings, 5469);
                        this.reactContext.startActivityForResult(overlaySettings, 0, null);
                    }
                }
            } else {
                promise.resolve(true);
            }
        } catch (Error e) {
            promise.reject(e);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    

    @ReactMethod
    public void isRequestOverlayPermissionGranted(Callback callback) {
      try {
        boolean equal = Settings.canDrawOverlays(reactContext);
        callback.invoke(equal);
      } catch (Exception e) {
        callback.invoke(e.getMessage());
      }
    }
    

}
