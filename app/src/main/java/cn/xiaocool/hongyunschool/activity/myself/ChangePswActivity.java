package cn.xiaocool.hongyunschool.activity.myself;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.ProgressUtil;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.ToastUtil;
/**
 * 个人信息->修改密码
 */

public class ChangePswActivity extends BaseActivity {

    @BindView(R.id.activity_change_psw_ed_oldpsw)
    EditText activityChangePswEdOldpsw;
    @BindView(R.id.activity_change_psw_ed_newpsw)
    EditText activityChangePswEdNewpsw;
    @BindView(R.id.activity_change_psw_ed_confirmpsw)
    EditText activityChangePswEdConfirmpsw;
    @BindView(R.id.activity_change_psw_tv_submit)
    TextView activityChangePswTvSubmit;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);
        ButterKnife.bind(this);
        setTopName("密码修改");
        context = this;
    }

    @Override
    public void requsetData() {

    }

    @OnClick(R.id.activity_change_psw_tv_submit)
    public void onClick() {
        //判断原密码是否正确
        String oldPsw = (String) SPUtils.get(context, LocalConstant.USER_PASSWORD,"");
        if (!activityChangePswEdOldpsw.getText().toString().equals(oldPsw)){
            ToastUtil.showShort(context,"原密码输入错误!");
            return;
        }
        //判断两次新密码是否相同
        if (!activityChangePswEdNewpsw.getText().toString().equals(activityChangePswEdConfirmpsw.getText().toString())){
            ToastUtil.showShort(context,"两次密码输入错误!");
            return;
        }

        //发送网络请求
        String userid = (String) SPUtils.get(context,LocalConstant.USER_ID,"");
        String psw = activityChangePswEdNewpsw.getText().toString();
        ProgressUtil.showLoadingDialog(this);
        String setPswUrl = NetConstantUrl.RESET_PSW + "&userid="+userid+"&pass="+psw;
        VolleyUtil.VolleyGetRequest(context, setPswUrl, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(context, result)) {
                    ToastUtil.showShort(context, "密码修改成功!");
                    ProgressUtil.dissmisLoadingDialog();
                    finish();

                }else {
                    ToastUtil.showShort(context,"密码修改失败!");
                    ProgressUtil.dissmisLoadingDialog();
                }
            }

            @Override
            public void onError() {
                ProgressUtil.dissmisLoadingDialog();
            }
        });
    }
}
