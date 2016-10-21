package cn.xiaocool.hongyunschool.activity.infromation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.SendRequest;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.ToastUtil;

public class AddParentMessageActivity extends BaseActivity {

    @BindView(R.id.activity_add_parent_ed_message)
    EditText activityAddParentEdMessage;
    @BindView(R.id.activity_add_parent_bt_message)
    Button activityAddParentBtMessage;
    public static final int SEND_FEEDBACK = 0x110;
    private Context context;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEND_FEEDBACK:
                    if (JsonResult.JSONparser(context, String.valueOf(msg.obj))){
                        ToastUtil.showShort(context,"感谢您的反馈!");
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parent_message);
        ButterKnife.bind(this);
        context = this;
        setTopName("反馈");

    }

    @Override
    public void requsetData() {

    }

    @OnClick(R.id.activity_add_parent_bt_message)
    public void onClick() {
        String userid = SPUtils.get(context, LocalConstant.USER_ID,"").toString();
        String classid = SPUtils.get(context,LocalConstant.USER_CLASSID,"").toString();
        String msg = activityAddParentEdMessage.getText().toString();
        if(msg.length()==0){
            ToastUtil.showShort(context,"发送消息不能为空！");
        }else{
            new SendRequest(context,handler).send_feedback(userid,classid,
                    SPUtils.get(context,LocalConstant.SCHOOL_ID,"1").toString(),msg,SEND_FEEDBACK);
        }
    }
}
