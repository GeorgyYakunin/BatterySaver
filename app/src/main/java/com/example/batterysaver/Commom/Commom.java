package com.example.batterysaver.Commom;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.example.batterysaver.Listener.IStartThread;
import com.example.batterysaver.Listener.IStopThread;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Commom {
    public static int temperature;
    public static int progress;
    public static int voltage;
    public static int currentBrightnessValue;
    public static boolean isWifiEnable;
    private static ContentResolver cResolver;
    public static IStopThread iStopThread;
    public static IStartThread iStartThread;

    public static void setCurrentBrightnessValue(Context context) {
        try {
            currentBrightnessValue = android.provider.Settings.System.getInt(
                    context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void changedBrightness(Context context) {
        cResolver = context.getContentResolver();
        if (Commom.currentBrightnessValue == 26) {
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 26);
        } else if (Commom.currentBrightnessValue == 13) {
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 13);
        } else if (Commom.currentBrightnessValue == 128) {
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 128);
        } else if (Commom.currentBrightnessValue >= 126 && Commom.currentBrightnessValue < 255) {
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 255);
        } else if (Commom.currentBrightnessValue == 255) {
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 0);
        } else if (Commom.currentBrightnessValue < 126) {
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 126);
        }
    }

    public static double getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;
    }

    public static boolean checkWifiOnAndConnected(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if (wifiInfo.getNetworkId() == -1) {
                isWifiEnable = false;
                return false; // Not connected to an access point
            }
            isWifiEnable = true;
            return true; // Connected to an access point
        } else {
            isWifiEnable = false;
            return false; // Wi-Fi adapter is OFF
        }
    }

    public static boolean check3GOnOff(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();

        return is3g;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public static String formatAvailableTime(int progress) {
        int hour;
        int minute;
        if (progress >= 60) {
            hour = progress / 60;
            minute = progress - hour * 60;
            return hour + " h " + minute + " min";
        }
        return String.valueOf(progress) + " min";
    }

    public static void openRate(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }
}
