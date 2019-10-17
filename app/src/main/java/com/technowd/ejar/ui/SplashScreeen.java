package com.technowd.ejar.ui;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.technowd.ejar.MainActivity;
import com.technowd.ejar.R;

public class SplashScreeen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screeen);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                    goToMain();
                } catch (InterruptedException e) {
                    e.getLocalizedMessage();
                }
            }
        });
        thread.start();
    }
    // Go to main activity
    private void goToMain() {
        Intent intent = new Intent(SplashScreeen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
