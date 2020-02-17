package com.example.batterysaver.Fragment;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.batterysaver.Commom.Commom;
import com.example.batterysaver.R;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;

public class FragmentPowerMode extends Fragment implements View.OnClickListener {
    private ImageView imgPowerSaving, imgNormal, imgSleep;
    private RadioButton rbPower, rbSleep, rbNormal;
    private TextView tvPower, tvSleep, tvNormal, tvMode, tvBrightness, tvTime, tvVibrate, tvWifi, tvBluetooth;
    private Dialog dialog;
    private AudioManager audioManager;
    private WifiManager wifiManager;
    private BluetoothAdapter mBluetoothAdapter;
//    private AdView adView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_p_mode, container, false);

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        initViews(view);
        checkRbIsSelected();

        return view;
    }

    private void checkRbIsSelected() {
        if (wifiManager.isWifiEnabled()) {
            rbNormal.setChecked(true);
        } else {
            if (Commom.currentBrightnessValue <= 13) {
                rbPower.setChecked(true);
            } else {
                rbSleep.setChecked(true);
            }
        }
    }

    private void initViews(View view) {

//        MobileAds.initialize(view.getContext(), getResources().getString(R.string.mobile_id));
//        adView = view.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);


        imgNormal = view.findViewById(R.id.img_normal);
        imgNormal.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        imgPowerSaving = view.findViewById(R.id.img_power_saving);
        imgPowerSaving.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        imgSleep = view.findViewById(R.id.img_sleep);
        imgSleep.setColorFilter(getContext().getResources().getColor(android.R.color.white));

        rbPower = view.findViewById(R.id.rb_power);
        rbPower.setOnClickListener(this);
        rbNormal = view.findViewById(R.id.rb_normal);
        rbNormal.setOnClickListener(this);
        rbSleep = view.findViewById(R.id.rb_sleep);
        rbSleep.setOnClickListener(this);
        tvNormal = view.findViewById(R.id.tv_normal);
        tvNormal.setOnClickListener(this);
        tvPower = view.findViewById(R.id.tv_power_saving);
        tvPower.setOnClickListener(this);
        tvSleep = view.findViewById(R.id.tv_sleep);
        tvSleep.setOnClickListener(this);

        ////Dialog
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_information);

        tvMode = dialog.findViewById(R.id.tv_mode);
        tvBluetooth = dialog.findViewById(R.id.tv_bluetooth);
        tvBrightness = dialog.findViewById(R.id.tv_screen_brightness);
        tvWifi = dialog.findViewById(R.id.tv_wifi);
        tvVibrate = dialog.findViewById(R.id.tv_vibrate);
        tvTime = dialog.findViewById(R.id.tv_screen_time);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_power:
                rbNormal.setChecked(false);
                rbSleep.setChecked(false);
                setUpPowerMode(13, 15000, AudioManager.RINGER_MODE_SILENT, false);
                break;
            case R.id.rb_normal:
                rbSleep.setChecked(false);
                rbPower.setChecked(false);
                setUpPowerMode(128, 60000, AudioManager.RINGER_MODE_NORMAL, true);
                break;
            case R.id.rb_sleep:
                rbNormal.setChecked(false);
                rbPower.setChecked(false);
                setUpPowerMode(26, 25000, AudioManager.RINGER_MODE_SILENT, false);
                break;
            case R.id.tv_normal:
                showInfo("Auto", "60s", "OFF", "ON", "OFF", "Normal mode");
                break;
            case R.id.tv_power_saving:
                showInfo("5%", "15s", "OFF", "OFF", "OFF", "Power saving mode");
                break;
            case R.id.tv_sleep:
                showInfo("10%", "25s", "OFF", "OFF", "OFF", "Sleep mode");
                break;
        }
    }

    private void setUpPowerMode(int i, int i1, int ringerModeSilent, boolean b) {
        Commom.currentBrightnessValue = i;
        Commom.changedBrightness(getContext());
        android.provider.Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, i1);
        audioManager.setRingerMode(ringerModeSilent);
        wifiManager.setWifiEnabled(b);
        mBluetoothAdapter.disable();
    }

    private void showInfo(String auto, String s, String off, String on, String off1, String mode) {
        tvBrightness.setText(auto);
        tvTime.setText(s);
        tvVibrate.setText(off);
        tvWifi.setText(on);
        tvBluetooth.setText(off1);
        tvMode.setText(mode);

        dialog.show();
    }

}
