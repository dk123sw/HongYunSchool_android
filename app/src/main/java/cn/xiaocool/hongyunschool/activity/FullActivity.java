package cn.xiaocool.hongyunschool.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cn.jpush.android.api.JPushInterface;
import cn.xiaocool.hongyunschool.R;
/**
 * 打开apk第一个界面
 */
public class FullActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//change
        setContentView(R.layout.activity_full);

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    startActivity(new Intent(FullActivity.this, SplashActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
}
