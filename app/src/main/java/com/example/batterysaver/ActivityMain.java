package com.example.batterysaver;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.batterysaver.Adapter.FragmentAdapter;
import com.example.batterysaver.Commom.Commom;
import com.example.batterysaver.Fragment.FragmentBattery;
import com.example.batterysaver.Fragment.FragmentPowerMode;
import com.example.batterysaver.Fragment.FragmentSettings;
import com.example.batterysaver.Service.BatteryService;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
//    private InterstitialAd mInterstitialAd;
    private static final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 1;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;

    // Get value battery
    private IntentFilter intentFilter;
    private BroadcastBattery broadcastBattery;

    //Broadcast Service
    private BatteryService batteryService;
    private Intent intent;

    //Dialog
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        fragmentManager = getSupportFragmentManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                // Do stuff here
                Log.d("son,pt", "Can't write");
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp();
        }
//        initAdsFull();
        initViews();

        intent = new Intent(this, BatteryService.class);
        batteryService = new BatteryService();

        broadcastBattery = new BroadcastBattery();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastBattery, intentFilter);

        if (!Commom.isMyServiceRunning(batteryService.getClass(), this)) {
            startService(intent);
        }
    }

    class BroadcastBattery extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Commom.progress = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            Commom.temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            Commom.voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp() {
        NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (!n.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent, ActivityMain.ON_DO_NOT_DISTURB_CALLBACK_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityMain.ON_DO_NOT_DISTURB_CALLBACK_CODE) {
            this.requestDoNotDisturbPermissionOrSetDoNotDisturbApi23AndUp();
        }
    }

    @SuppressLint("RestrictedApi")
    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_main);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.mo, R.string.dong) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        viewPager.setOffscreenPageLimit(2);
        setupFm(viewPager);
        toolbar.setTitle("Battery");
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new PageChange());
    }

    private void setupFm(ViewPager viewPager) {
        FragmentAdapter Adapter = new FragmentAdapter(fragmentManager);
        //Add All Fragment To List
        Adapter.add(new FragmentBattery(), "Battery");
        Adapter.add(new FragmentPowerMode(), "Power mode");
        Adapter.add(new FragmentSettings(), "Settings");
        viewPager.setAdapter(Adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.nav_battery:
                viewPager.setCurrentItem(0);
                menuItem.setChecked(true);
                break;
            case R.id.nav_cooling:
                Intent intent3 = new Intent(this, ActivityCool.class);
                startActivity(intent3);
                break;
            case R.id.nav_power_cleaner:
                break;
            case R.id.nav_power_mode:
                viewPager.setCurrentItem(1);
                break;
            case R.id.nav_settings:
                viewPager.setCurrentItem(2);
                break;
            case R.id.nav_rank:
                Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                ResolveInfo resolveInfo = getPackageManager().resolveActivity(powerUsageIntent, 0);
                if (resolveInfo != null) {
                    startActivity(powerUsageIntent);
                }
                break;
            case R.id.bt_battery:
                viewPager.setCurrentItem(0);
                break;
            case R.id.bt_powermode:
                viewPager.setCurrentItem(1);
                break;
            case R.id.bt_setting:
                viewPager.setCurrentItem(2);
                break;
        }
        return false;
    }

    public class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    toolbar.setTitle("Battery");
                    Commom.iStartThread.startThread();
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                    break;
                case 1:
                    toolbar.setTitle("Power mode");
                    Commom.iStopThread.stopThread();
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                    break;
                case 2:
                    toolbar.setTitle("Settings");
                    Commom.iStopThread.stopThread();
                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Confirm exit?");
            builder.setTitle("Battery Saver");
            builder.setIcon(R.drawable.ic_battery_notification);

            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialog = builder.create();
            alertDialog.show();
        }

    }

    @Override
    protected void onDestroy() {
        Log.d("son.pt", "destroy activity");
        unregisterReceiver(broadcastBattery);
        stopService(intent);
        super.onDestroy();
    }

//    private void initAdsFull() {
//        mInterstitialAd = new InterstitialAd(this);
//        MobileAds.initialize(this,
//                getResources().getString(R.string.mobile_id));
//        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ads_full));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                if (mInterstitialAd != null) {
//
//                    mInterstitialAd.show();
//                }
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                // Code to be executed when an ad request fails.
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when the ad is displayed.
//            }
//
//            @Override
//            public void onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when the interstitial ad is closed.
//            }
//        });
//
//    }
}
