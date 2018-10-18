package mytoken.eos.io;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.WindowManager;


import java.util.Timer;
import java.util.TimerTask;

public class LunchActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_lunch);

        mTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mhandler.sendEmptyMessage(0);
            }
        };
        mTimer.schedule(timerTask, 1500);

    }

    Timer mTimer;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mTimer.cancel();
//                    if (isFirstLogin) {
//                        Intent intent = new Intent(LaunchActivity.this, FirstLoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
                    Intent intent = new Intent(LunchActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }


    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }
}
