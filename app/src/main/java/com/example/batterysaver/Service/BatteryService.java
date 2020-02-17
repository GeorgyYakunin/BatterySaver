package com.example.batterysaver.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.batterysaver.Commom.Commom;
import com.example.batterysaver.Commom.Const;
import com.example.batterysaver.Commom.SharePreferencesController;
import com.example.batterysaver.ActivityScreenLock;
import com.example.batterysaver.ActivityMain;
import com.example.batterysaver.R;

public class BatteryService extends Service {
    private IntentFilter intentFilter = new IntentFilter();
    private BroadcastBattery broadcastBattery = new BroadcastBattery();
    private NotificationManager notificationManager;
    private RemoteViews notificationLayout;
    private RemoteViews remoteViews;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d("son.pt", "onCreate Service");
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(broadcastBattery, intentFilter);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    class BroadcastBattery extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                Commom.progress = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                Commom.temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                Commom.voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            }
            switch (intent.getAction()) {
                case Intent.ACTION_POWER_CONNECTED:
                    if (SharePreferencesController.getInstance(getApplicationContext()).getBoolean(Const.KEY_NOTIFICATIONS, false)) {
                        createNotificationChannel();
                    }

                    if (SharePreferencesController.getInstance(getApplicationContext()).getBoolean(Const.KEY_NOTIFICATION_BAR, false)) {
                        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                            createNotificationBar();
                        }
                    }

                    if (SharePreferencesController.getInstance(getApplicationContext()).getBoolean(Const.KEY_CHARGIN_SCREEN, false)) {
                        Intent intent1 = new Intent(getBaseContext(), ActivityScreenLock.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                    }
                    break;
                case Intent.ACTION_BATTERY_CHANGED:
                    Log.d("son.pt", "ACTION_BATTERY_CHANGED");
                    if (SharePreferencesController.getInstance(getApplicationContext()).getBoolean(Const.KEY_NOTIFICATIONS, false)) {
                        if (remoteViews == null) {
                            createNotificationChannel();
                        } else {
                            remoteViews.setTextViewText(R.id.tv_battery_power, "Battery Power " + Commom.progress + "%");
                        }
                    }

                    if (SharePreferencesController.getInstance(getApplicationContext()).getBoolean(Const.KEY_NOTIFICATION_BAR, false)) {
                        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                            if (notificationLayout == null) {
                                createNotificationBar();
                            } else {
                                notificationLayout.setTextViewText(R.id.tv_progress, Commom.progress + " %");
                            }
                        }
                    }

                    break;
            }
        }
    }

    private void createNotificationBar() {
        notificationLayout =
                new RemoteViews(getPackageName(), R.layout.notification_bar_customize_);

        notificationLayout.setTextViewText(R.id.tv_progress, Commom.progress + " %");

        Intent intentPress = new Intent(this, ActivityMain.class);
        intentPress.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentPress, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setCustomContentView(notificationLayout);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L});
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my_channel1",
                    "BatterySaver1", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }

        Notification notification = builder.build();
        notificationManager.notify(2, notification);
    }

    public void createNotificationChannel() {
        remoteViews =
                new RemoteViews(getPackageName(), R.layout.notification_customize_);

        remoteViews.setTextViewText(R.id.tv_battery_power, "Battery Power " + Commom.progress + "%");

        Intent intentPress = new Intent(this, ActivityMain.class);
        intentPress.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Const.REQUEST_CODE_NOTIFICATION, intentPress, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setCustomContentView(remoteViews);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L});
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my_channel2",
                    "BatterySaver2", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("son.pt", "destroy service");

        notificationManager.cancelAll();
//        unregisterReceiver(broadcastBattery);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(getApplicationContext(), com.example.batterysaver.Broadcast.BroadcastBattery.class);
        sendBroadcast(broadcastIntent);
    }
}
