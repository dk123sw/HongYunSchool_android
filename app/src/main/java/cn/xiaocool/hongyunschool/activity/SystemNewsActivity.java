package cn.xiaocool.hongyunschool.activity;

import android.os.Bundle;

import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
/**
 * 系统通知
 */
public class SystemNewsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_news);
        setTopName("系统通知");
    }

    @Override
    public void requsetData() {

    }
}
