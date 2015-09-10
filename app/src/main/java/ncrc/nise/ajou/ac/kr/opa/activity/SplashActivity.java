package ncrc.nise.ajou.ac.kr.opa.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import ncrc.nise.ajou.ac.kr.opa.R;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 초기화
        initialize();
    }

    /* 초기화 */
    private void initialize() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000); // finish after 1 second
    }
}
