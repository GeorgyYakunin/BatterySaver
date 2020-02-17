package com.example.batterysaver;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityCool extends AppCompatActivity {
    CircleImageView circleImageView;
    ObjectAnimator animation;
    private TextView tvDegreC, tvTemperature;
    private RelativeLayout rlSuccessfully;
    private Button btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cool);

        tvDegreC = findViewById(R.id.tv_degreC);
        tvDegreC.setText(" \u2103");
        tvTemperature = findViewById(R.id.tv_do);
        rlSuccessfully = findViewById(R.id.rl_successfully);
        btnComplete = findViewById(R.id.btn_complete);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        circleImageView = findViewById(R.id.img_fan);
        circleImageView.setColorFilter(this.getResources().getColor(android.R.color.white));
        animation = ObjectAnimator.ofFloat(circleImageView, "rotation", 0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setRepeatMode(ValueAnimator.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animation.end();
                circleImageView.setVisibility(View.INVISIBLE);
                rlSuccessfully.setVisibility(View.VISIBLE);
                btnComplete.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }
}
