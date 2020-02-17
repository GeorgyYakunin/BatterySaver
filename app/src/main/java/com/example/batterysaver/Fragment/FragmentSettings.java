package com.example.batterysaver.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.batterysaver.Commom.Const;
import com.example.batterysaver.Commom.SharePreferencesController;
import com.example.batterysaver.R;

public class FragmentSettings extends Fragment implements View.OnClickListener {
    private ImageView imgScreen, imgBar, imgUpdate, imgNotification;
    private Switch switchScreen, switchNotification, switchNotiBar;
    private LinearLayout llScreen, llNotification, llBar, llUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_settings, container, false);

        initViews(view);

        checkSwitch(Const.KEY_CHARGIN_SCREEN, switchScreen);
        checkSwitch(Const.KEY_NOTIFICATION_BAR, switchNotiBar);
        checkSwitch(Const.KEY_NOTIFICATIONS, switchNotification);

        return view;
    }

    private void checkSwitch(String keyCharginScreen, Switch switchScreen) {
        if (SharePreferencesController.getInstance(getContext()).getBoolean(keyCharginScreen, false)) {
            switchScreen.setChecked(true);
        } else {
            switchScreen.setChecked(false);
        }
    }

    private void initViews(View view) {
        imgNotification = view.findViewById(R.id.img_notification);
        imgNotification.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        imgBar = view.findViewById(R.id.img_bar);
        imgBar.setColorFilter(getContext().getResources().getColor(android.R.color.white));
//        imgRate = view.findViewById(R.id.img_rate);
//        imgRate.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        imgScreen = view.findViewById(R.id.img_screen);
        imgScreen.setColorFilter(getContext().getResources().getColor(android.R.color.white));
        imgUpdate = view.findViewById(R.id.img_update);
        imgUpdate.setColorFilter(getContext().getResources().getColor(android.R.color.white));

        switchNotiBar = view.findViewById(R.id.switch_bar);
        switchNotiBar.setOnClickListener(this);
        switchScreen = view.findViewById(R.id.switch_screen);
        switchScreen.setOnClickListener(this);
        switchNotification = view.findViewById(R.id.switch_notification);
        switchNotification.setOnClickListener(this);

        llBar = view.findViewById(R.id.ll_bar);
        llBar.setOnClickListener(this);
        llNotification = view.findViewById(R.id.ll_notification);
        llNotification.setOnClickListener(this);
//        llRate = view.findViewById(R.id.ll_rate);
//        llRate.setOnClickListener(this);
        llScreen = view.findViewById(R.id.ll_screen);
        llScreen.setOnClickListener(this);
        llUpdate = view.findViewById(R.id.ll_update);
        llUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_screen:
                setSwitchChanged(switchScreen, Const.KEY_CHARGIN_SCREEN);
                break;
            case R.id.ll_notification:
                setSwitchChanged(switchNotification, Const.KEY_NOTIFICATIONS);
                break;
            case R.id.ll_bar:
                setSwitchChanged(switchNotiBar, Const.KEY_NOTIFICATION_BAR);
                break;
            case R.id.ll_update:
                break;
            case R.id.switch_screen:
                setSwitchChanged(switchScreen, Const.KEY_CHARGIN_SCREEN);
                break;
            case R.id.switch_bar:
                setSwitchChanged(switchNotiBar, Const.KEY_NOTIFICATION_BAR);
                break;
            case R.id.switch_notification:
                setSwitchChanged(switchNotification, Const.KEY_NOTIFICATIONS);
                break;
        }
    }

    private void setSwitchChanged(Switch aSwitch, String keyCharginScreen) {
        if (SharePreferencesController.getInstance(getContext()).getBoolean(keyCharginScreen, false)) {
            aSwitch.setChecked(false);
            SharePreferencesController.getInstance(getContext()).putBoolean(keyCharginScreen, false);
        } else {
            aSwitch.setChecked(true);
            SharePreferencesController.getInstance(getContext()).putBoolean(keyCharginScreen, true);
        }
    }
}
