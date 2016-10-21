package cn.xiaocool.hongyunschool.activity.infromation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.SendRequest;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.ProgressUtil;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.ToastUtil;

public class AddGroupMessageActivity extends BaseActivity {

    @BindView(R.id.addsn_tv_choose_class)
    TextView addsnTvChooseClass;
    @BindView(R.id.tv_select_count)
    TextView tvSelectCount;
    @BindView(R.id.addsn_content)
    EditText addsnContent;
    private Context context;
    private String id;
    private String type;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x110:
                    if (msg.obj != null) {
                        if (JsonResult.JSONparser(AddGroupMessageActivity.this, String.valueOf(msg.obj))) {
                            ToastUtil.showShort(context, "发布成功");
                            finish();
                        }

                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_message);
        ButterKnife.bind(this);
        context = this;
        setTopName("短信发布");
        setRight();
        getIntentData();
    }

    /**
     * 高级权限获取传递数据
     */
    private void getIntentData() {
        type = getIntent().getStringExtra("type");
        if (type==null){
            type = "";
        }
    }

    /**
     * 设置发布按钮
     */
    private void setRight() {
        setRightText("发布").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNews();
                ProgressUtil.showLoadingDialog(AddGroupMessageActivity.this);
            }
        });
    }

    /**
     * 发布消息
     */
    private void sendNews() {
        //判断必填项
        if (!(addsnContent.getText().toString().length() > 0)) {
            ToastUtil.showShort(this, "发送内容不能为空!");
            return;
        }
        if (id == null) {
            ToastUtil.showShort(this, "请选择接收人!");
            return;
        }
        new SendRequest(AddGroupMessageActivity.this, handler).sendGroupMessage(id, SPUtils.get(context, LocalConstant.USER_ID, "").toString(), SPUtils.get(context, LocalConstant.SCHOOL_ID, "").toString(),addsnContent.getText().toString(), 0x110);

    }

    @Override
    public void requsetData() {

    }

    @OnClick(R.id.addsn_tv_choose_class)
    public void onClick() {
        if (type.equals("teacher")){
            Intent intent = new Intent(AddGroupMessageActivity.this, ChooseTeacherActivity.class);
            intent.putExtra("type", "message");
            startActivityForResult(intent, 101);
        }else {
            Intent intent = new Intent(AddGroupMessageActivity.this, ChooseParentActivity.class);
            intent.putExtra("type", "");
            startActivityForResult(intent, 101);
        }

    }

    /**
     * 获取返回的接收人
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 101:
                    if (data != null) {
                        ArrayList<String> ids = data.getStringArrayListExtra("ids");
                        ArrayList<String> names = data.getStringArrayListExtra("names");
                        String haschoose = "";
                        for (int i = 0; i < names.size(); i++) {
                            if (i < 3) {
                                if (names.get(i) != null || names.get(i) != "null") {
                                    haschoose = haschoose + names.get(i) + "、";
                                }
                            } else if (i == 4) {
                                haschoose = haschoose.substring(0, haschoose.length() - 1);
                                haschoose = haschoose + "等...";
                            }

                        }

                        id = null;
                        for (int i = 0; i < ids.size(); i++) {
                            id = id + "," + ids.get(i);
                        }
                        id = id.substring(5, id.length());
                        tvSelectCount.setText("共选择" + ids.size() + "人");
                        addsnTvChooseClass.setText(haschoose);
                    }

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
