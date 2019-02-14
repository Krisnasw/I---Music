package com.intivestudio.ryppmusic.ui.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.ui.Home.HomeActivity;
import com.intivestudio.ryppmusic.ui.Login.LoginActivity;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.pixplicity.easyprefs.library.Prefs;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(2*1000);

                    if (Prefs.getBoolean(AppConfig.IS_LOGIN, false)) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (Exception e) {

                }
            }
        };
        background.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
