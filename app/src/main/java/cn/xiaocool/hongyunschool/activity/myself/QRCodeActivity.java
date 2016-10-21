package cn.xiaocool.hongyunschool.activity.myself;

import android.os.Bundle;

import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
/**
 * 客户端二维码
 */
public class QRCodeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        setTopName("二维码");
    }

    @Override
    public void requsetData() {

    }
}
