package com.example.batterysaver.Fragment;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.batterysaver.Commom.Commom;
import com.example.batterysaver.ActivityCool;
import com.example.batterysaver.Listener.IStartThread;
import com.example.batterysaver.Listener.IStopThread;
import com.example.batterysaver.R;

import me.itangqi.waveloadingview.WaveLoadingView;

public class FragmentBattery extends Fragment implements View.OnClickListener, IStopThread, IStartThread {
    private WaveLoadingView waveLoadingView;
    private TextView tvTemperature, tvVoltage, tvCapacity, tvTalk, tvInternet, tvGame;
    private ImageView imgWifi, imgData, imgBrightness, imgRing, imgShock, imgLocation, imgBluetooth, imgFlightmode;
    private LinearLayout llWifi, llData, llBrightness, llRing, llShock, llLocation, llBluetooth, llFlightmode, llCooling;

    //Content resolver used as a handle to the system's settings
    private AudioManager audioManager;
    private BluetoothAdapter mBluetoothAdapter;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler();
    private boolean isRunning;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isRunning = true;
            tvTemperature.setText(((float) Commom.temperature) / 10 + " \u2103");
            StringBuilder voltage = new StringBuilder(String.valueOf(Commom.voltage * 0.001));
            voltage.setLength(4);
            tvVoltage.setText(voltage.append(" V"));
            tvCapacity.setText(((int) Commom.getBatteryCapacity(getContext())) + "mAh");

            Commom.setCurrentBrightnessValue(getContext());
            setBrightness();

            checkRingMode();
            checkWifi();
            check3G();
            checkLocation();
            checkFlightMode();
            checkBluetooth();

            int totalCapacity = (int) (Commom.getBatteryCapacity(getContext()) * 0.01 * 0.0133 * Commom.progress + 270);
            waveLoadingView.setProgressValue(Commom.progress);
            waveLoadingView.setCenterTitle(Commom.progress + "%");
            tvGame.setText(Commom.formatAvailableTime(totalCapacity));
            tvInternet.setText(Commom.formatAvailableTime(totalCapacity * 2));
            tvTalk.setText(Commom.formatAvailableTime(totalCapacity * 3));

            if (Commom.progress <= 20) {
                waveLoadingView.setBorderColor(Color.RED);
                waveLoadingView.setWaveColor(Color.RED);
            } else if (Commom.progress > 21 && Commom.progress <= 80) {
                waveLoadingView.setBorderColor(Color.rgb(255, 111, 0));
                waveLoadingView.setWaveColor(Color.rgb(255, 111, 0));
            } else {
                waveLoadingView.setBorderColor(Color.GREEN);
                waveLoadingView.setWaveColor(Color.GREEN);
            }

            handler.postDelayed(this, 500);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_battery, container, false);

        initViews(view);

        Log.d("son.pt", "onCreateView");
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Commom.iStopThread = this;
        Commom.iStartThread = this;

        Log.d("son.pt", "onCreate");
    }

    private void setBrightness() {
        if (Commom.currentBrightnessValue < 126) {
            imgBrightness.setImageResource(R.drawable.item_brightness_low);
            imgBrightness.setColorFilter(getContext().getResources().getColor(R.color.colorItemDisable));
        } else if (Commom.currentBrightnessValue >= 126 && Commom.currentBrightnessValue < 255) {
            imgBrightness.setImageResource(R.drawable.item_brightness_medium);
            imgBrightness.setColorFilter(getContext().getResources().getColor(R.color.colorItemDisable));
        } else if (Commom.currentBrightnessValue == 255) {
            imgBrightness.setImageResource(R.drawable.item_brightness);
            imgBrightness.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        }
    }

    private void initViews(View view) {
        waveLoadingView = view.findViewById(R.id.waveLoadingView);
        tvCapacity = view.findViewById(R.id.tv_capacity);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvVoltage = view.findViewById(R.id.tv_voltage);
        tvGame = view.findViewById(R.id.tv_game);
        tvInternet = view.findViewById(R.id.tv_internet);
        tvTalk = view.findViewById(R.id.tv_talk_time);
        imgBluetooth = view.findViewById(R.id.img_bluetooth);
        imgBrightness = view.findViewById(R.id.img_brightness);
        imgData = view.findViewById(R.id.img_data);
        imgFlightmode = view.findViewById(R.id.img_flightmode);
        imgLocation = view.findViewById(R.id.img_location);
        imgRing = view.findViewById(R.id.img_ring);
        imgShock = view.findViewById(R.id.img_shock);
        imgWifi = view.findViewById(R.id.img_wifi);
        llWifi = view.findViewById(R.id.ll_wifi);
        llWifi.setOnClickListener(this);
        llBluetooth = view.findViewById(R.id.ll_bluetooth);
        llBluetooth.setOnClickListener(this);
        llBrightness = view.findViewById(R.id.ll_brightness);
        llBrightness.setOnClickListener(this);
        llData = view.findViewById(R.id.ll_data);
        llData.setOnClickListener(this);
        llFlightmode = view.findViewById(R.id.ll_flightmode);
        llFlightmode.setOnClickListener(this);
        llLocation = view.findViewById(R.id.ll_location);
        llLocation.setOnClickListener(this);
        llRing = view.findViewById(R.id.ll_ring);
        llRing.setOnClickListener(this);
        llShock = view.findViewById(R.id.ll_shock);
        llShock.setOnClickListener(this);
        llCooling = view.findViewById(R.id.ll_cooling);
        llCooling.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_wifi:
                changedWifi();
                break;
            case R.id.ll_bluetooth:
                changedBluetooth();
                break;
            case R.id.ll_brightness:
                Commom.changedBrightness(getContext());
                break;
            case R.id.ll_data:
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                startActivity(intent);
                break;
            case R.id.ll_flightmode:
                Intent intent2 = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                startActivity(intent2);
                break;
            case R.id.ll_location:
                Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent1);
                break;
            case R.id.ll_ring:
                changedRing(true);
                break;
            case R.id.ll_shock:
                changedRing(false);
                break;
            case R.id.ll_cooling:
                Intent intent3 = new Intent(getContext(), ActivityCool.class);
                startActivity(intent3);
                break;
        }
    }

    private void changedBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {
            mBluetoothAdapter.disable();
        }
    }

    private void changedRing(Boolean isSilent) {
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                audioManager.setRingerMode(1);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                audioManager.setRingerMode(2);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                if (isSilent) {
                    audioManager.setRingerMode(2);
                } else {
                    audioManager.setRingerMode(1);
                }
                break;
        }
    }

    private void changedWifi() {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (Commom.isWifiEnable) {
            wifiManager.setWifiEnabled(false);
        } else {
            wifiManager.setWifiEnabled(true);
        }
    }


    private void checkFlightMode() {
        if (Commom.isAirplaneModeOn(getContext())) {
            imgFlightmode.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        } else {
            imgFlightmode.setColorFilter(getContext().getResources().getColor(R.color.colorItemDisable));
        }
    }

    private void checkLocation() {
        if (Commom.isLocationEnabled(getContext())) {
            imgLocation.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        } else {
            imgLocation.setColorFilter(getContext().getResources().getColor(R.color.colorItemDisable));
        }
    }

    private void check3G() {
        if (Commom.check3GOnOff(getContext())) {
            imgData.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        } else {
            imgData.setColorFilter(getContext().getResources().getColor(R.color.colorItemDisable));
        }
    }

    private void checkWifi() {
        if (Commom.checkWifiOnAndConnected(getContext())) {
            imgWifi.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        } else {
            imgWifi.setColorFilter(getContext().getResources().getColor(R.color.colorItemDisable));
        }
    }

    private void checkRingMode() {
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            imgRing.setColorFilter(getContext().getResources().getColor(android.R.color.white));
            imgShock.setColorFilter(getContext().getResources().getColor(R.color.colorItemDisable));
        } else {
            imgRing.setColorFilter(getContext().getResources().getColor(R.color.colorItemDisable));
            imgShock.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        }
    }

    private void checkBluetooth() {
        if (mBluetoothAdapter.isEnabled()) {
            imgBluetooth.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        } else {
            imgBluetooth.setColorFilter(getContext().getResources().getColor(R.color.colorItemDisable));
        }
    }

    @Override
    public void onPause() {
        handler.removeCallbacksAndMessages(null);
        isRunning = false;
        super.onPause();
    }

    @Override
    public void onStart() {
        getActivity().runOnUiThread(runnable);
        super.onStart();
    }

    @Override
    public void stopThread() {
        if (isRunning) {
            handler.removeCallbacksAndMessages(null);
            isRunning = false;
        }
    }

    @Override
    public void startThread() {
        getActivity().runOnUiThread(runnable);
    }
}
