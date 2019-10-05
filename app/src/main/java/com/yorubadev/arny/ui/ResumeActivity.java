package com.yorubadev.arny.ui;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.yorubadev.arny.R;
import com.yorubadev.arny.utilities.ActivityLauncher;


public class ResumeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);
        new Handler().postDelayed(() -> {
            ActivityLauncher.launchMainActivity(this);
            finish();
        }, 1000);
    }
}
