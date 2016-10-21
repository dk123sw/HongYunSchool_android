package cn.xiaocool.hongyunschool.activity.myself;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;

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
/**
 * 在线客服->在线反馈
 */
public class FeedbackActivity extends BaseActivity {
    @BindView(R.id.ed_comment)
    EditText edComment;
    @BindView(R.id.btn_comment)
    TextView btnComment;
    private Context context;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x110:
                    if (msg.obj!=null){
                        if (JsonResult.JSONparser(FeedbackActivity.this, String.valueOf(msg.obj))){
                            ToastUtil.showShort(context,"反馈成功");
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
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        setTopName("在线反馈");
        context = this;
    }

    @Override
    public void requsetData() {

    }

    @OnClick(R.id.btn_comment)
    public void onClick() {
        String content = edComment.getText().toString();
        if(content.equals("")||content.length()==0){
            ToastUtil.showShort(context,"输入的内容不能为空！");
            return;
        }
        ProgressUtil.showLoadingDialog(this);
        new SendRequest(context,handler).sendFeedback(SPUtils.get(context, LocalConstant.USER_ID,"").toString(),
                SPUtils.get(context,LocalConstant.SCHOOL_ID,"").toString(),content,0x110);
    }
}
