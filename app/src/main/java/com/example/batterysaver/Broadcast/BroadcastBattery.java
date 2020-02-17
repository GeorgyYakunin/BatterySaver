package com.example.batterysaver.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.batterysaver.Service.BatteryService;

public class BroadcastBattery extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("son.pt", "nhan o broadcast");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                context.startForegroundService(new Intent(context, BatteryService.class));
            } catch (Exception e) {
                Log.d("son.pt", e.toString());
            }
        } else {
            context.startService(new Intent(context, BatteryService.class));
        }
    }
}
