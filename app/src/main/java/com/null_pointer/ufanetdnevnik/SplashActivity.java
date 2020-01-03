package com.null_pointer.diarycloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.model.DiaryCloud;


public class SplashActivity extends AppCompatActivity {

    private ImageView mImage;
    private Thread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);


        mImage = (ImageView) findViewById(R.id.image);

        startAnimation();

    }

    private void startAnimation() {
        mThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    int waited = 0;
                    while (waited < 1000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashActivity.this, LogIn.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.getStackTrace();
                }
            }
        };
        mThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
