package cn.xiaocool.hongyunschool.activity.schoolnet.zhuanlan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
/**
 * 五公开专栏
 */
public class FivePublicActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_public);
        ButterKnife.bind(this);
        setTopName(getIntent().getStringExtra("title")!=null?getIntent().getStringExtra("title"):"列表");
    }

    @Override
    public void requsetData() {

    }

    @OnClick({R.id.web_five_homework, R.id.web_five_kecheng, R.id.web_five_keshi, R.id.web_five_qimojiance, R.id.web_five_jiejiari})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.web_five_homework:
                intent = new Intent(this, WebOtherListActivity.class);
                intent.putExtra("title","学校作业");
                startActivity(intent);
                break;
            case R.id.web_five_kecheng:
                intent = new Intent(this, WebKeChengActivity.class);
                intent.putExtra("title","学校课程");
                startActivity(intent);
                break;
            case R.id.web_five_keshi:
                intent = new Intent(this, WebOtherListActivity.class);
                intent.putExtra("title","课时");
                startActivity(intent);
                break;
            case R.id.web_five_qimojiance:
                intent = new Intent(this, WebOtherListActivity.class);
                intent.putExtra("title","期末检测");
                startActivity(intent);
                break;
            case R.id.web_five_jiejiari:
                intent = new Intent(this, WebOtherListActivity.class);
                intent.putExtra("title","节假日");
                startActivity(intent);
                break;
        }
    }
}
