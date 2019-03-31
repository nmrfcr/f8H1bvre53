package com.tekapic;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();

                //get action intent send here

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                }
                else {
                    startActivity(new Intent(LauncherActivity.this, HomeActivity.class));
                }

            }
        }, 500);
    }





}
