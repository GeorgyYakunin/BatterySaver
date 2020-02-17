package com.example.batterysaver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.batterysaver.Commom.Commom;
import com.example.batterysaver.Listener.OnSwipeTouchListener;
import com.example.batterysaver.Service.BatteryService;
import com.skyfishjy.library.RippleBackground;

public class ActivityScreenLock extends AppCompatActivity {
    private TextView tvTalkTime, tvInternet, tvGame;
    private RippleBackground rippleBackground;
    private FrameLayout flLock;
    private IntentFilter intentFilter;
    private BroadcastBattery broadcastBattery;
    //Broadcast Service
    private BatteryService batteryService;
    private Intent intent;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_lock_);

        rippleBackground = findViewById(R.id.content);
        tvGame = findViewById(R.id.tv_game);
        tvInternet = findViewById(R.id.tv_internet);
        tvTalkTime = findViewById(R.id.tv_talk_time);
        flLock = findViewById(R.id.fl_lock);

        rippleBackground.startRippleAnimation();

//        intent = new Intent(this, BatteryService.class);
//        batteryService = new BatteryService();
//
//        if (!Commom.isMyServiceRunning(batteryService.getClass(), this)) {
//            startService(intent);
//        }

        intentFilter = new IntentFilter();
        broadcastBattery = new BroadcastBattery();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(broadcastBattery, intentFilter);

        flLock.setOnTouchListener(new OnSwipeTouchListener(ActivityScreenLock.this) {
            public void onSwipeRight() {
                finish();
            }

            public void onSwipeLeft() {
                finish();
            }
        });

    }

    class BroadcastBattery extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int totalCapacity = (int) (Commom.getBatteryCapacity(context) * 0.01 * 0.0133 * Commom.progress + 270);
                tvGame.setText(Commom.formatAvailableTime(totalCapacity));
                tvInternet.setText(Commom.formatAvailableTime(totalCapacity * 2));
                tvTalkTime.setText(Commom.formatAvailableTime(totalCapacity * 3));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastBattery);
    }
}

