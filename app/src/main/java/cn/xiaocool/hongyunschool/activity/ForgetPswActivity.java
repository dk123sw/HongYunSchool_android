package cn.xiaocool.hongyunschool.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.ProgressUtil;
import cn.xiaocool.hongyunschool.utils.ToastUtil;
/**
 * 登录界面 忘记密码
 */

public class ForgetPswActivity extends BaseActivity {
    @BindView(R.id.activity_forget_psw_ed_phone)
    EditText activityForgetPswEdPhone;
    @BindView(R.id.activity_forget_psw_btn_getcode)
    TextView activityForgetPswBtnGetcode;
    @BindView(R.id.activity_forget_psw_ed_code)
    EditText activityForgetPswEdCode;
    @BindView(R.id.activity_forget_psw_ed_psw)
    EditText activityForgetPswEdPsw;
    @BindView(R.id.activity_forget_psw_cb_psw)
    CheckBox activityForgetPswCbPsw;
    @BindView(R.id.activity_forget_psw_ed_confirmpsw)
    EditText activityForgetPswEdConfirmpsw;
    @BindView(R.id.activity_forget_psw_cb_confirmpsw)
    CheckBox activityForgetPswCbConfirmpsw;
    @BindView(R.id.activity_forget_psw_btn_finish)
    TextView activityForgetPswBtnFinish;
    private Context context;

    private int time = 30;
    private int flag = 0;//是否发送验证码标志
    private Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (time == 0){
                activityForgetPswBtnGetcode.setText("获取验证码");
                activityForgetPswBtnGetcode.setFocusable(true);
                activityForgetPswBtnGetcode.setFocusableInTouchMode(true);
                activityForgetPswBtnGetcode.setClickable(true);
                time = 30;
            }else {
                activityForgetPswBtnGetcode.setClickable(false);
                activityForgetPswBtnGetcode.setText(time-- + "秒重发");
                timeHandler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw);
        ButterKnife.bind(this);
        setTopName("忘记密码");
        context = this;
    }

    @Override
    public void requsetData() {

    }

    @OnClick({R.id.activity_forget_psw_btn_getcode, R.id.activity_forget_psw_cb_psw, R.id.activity_forget_psw_cb_confirmpsw, R.id.activity_forget_psw_btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            //获取验证码
            case R.id.activity_forget_psw_btn_getcode:
                flag = 1;
                getVerification();
                break;
            //切换密码可见性
            case R.id.activity_forget_psw_cb_psw:
                changePswVisibility(activityForgetPswCbPsw,activityForgetPswEdPsw);
                break;
            //切换确认密码可见性
            case R.id.activity_forget_psw_cb_confirmpsw:
                changePswVisibility(activityForgetPswCbConfirmpsw,activityForgetPswEdConfirmpsw);
                break;
            //完成
            case R.id.activity_forget_psw_btn_finish:
                sendForgetPsy();
                break;
        }
    }

    /**
     * 返回数据
     */
    private void sendForgetPsy() {

        final String telephone = activityForgetPswEdPhone.getText().toString().trim();
        String code = activityForgetPswEdCode.getText().toString().trim();
        //手机号不能为空
        //检查手机号码是否合理
        if (!isMobile(telephone)) {
            Toast.makeText(context, "手机号码有误，请检查！", Toast.LENGTH_SHORT).show();
            return;
        }

        //判断是否发送验证码
        if (flag==0){
            ToastUtil.showShort(this,"请获取验证码!");
            return;
        }
        if (activityForgetPswEdCode.getText().toString().length()<1){
            ToastUtil.showShort(this, "请输入验证码!");
            return;
        }

        //判断两次输入密码是否相同
        if (activityForgetPswEdPsw.getText().toString().length()<1||activityForgetPswEdConfirmpsw.getText().toString().length()<1){
            ToastUtil.showShort(this,"请输入密码!");
            return;
        }
        if (!activityForgetPswEdPsw.getText().toString().equals(activityForgetPswEdConfirmpsw.getText().toString())){
            ToastUtil.showShort(this,"两次输入密码不同，请重新输入!");
            return;
        }
        final String psw = activityForgetPswEdPsw.getText().toString();
        //判断验证码是否正确
        String url = NetConstantUrl.PHONE_CODE_ISOK + "&phone="+telephone+"&code="+code;
        ProgressUtil.showLoadingDialog(this);
        VolleyUtil.VolleyGetRequest(this, url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(ForgetPswActivity.this,result)){

                    String setPswUrl = NetConstantUrl.FORGET_SET_PSW+"&phone="+telephone+"&pass="+psw;
                    VolleyUtil.VolleyGetRequest(ForgetPswActivity.this, setPswUrl, new VolleyUtil.VolleyJsonCallback() {
                        @Override
                        public void onSuccess(String result) {
                            if (JsonResult.JSONparser(ForgetPswActivity.this, result)) {
                                ToastUtil.showShort(ForgetPswActivity.this, "密码找回成功!");
                                ProgressUtil.dissmisLoadingDialog();
                                finish();

                            }else {
                                ToastUtil.showShort(ForgetPswActivity.this,"密码找回失败!");
                                ProgressUtil.dissmisLoadingDialog();
                            }
                        }

                        @Override
                        public void onError() {
                            ProgressUtil.dissmisLoadingDialog();
                        }
                    });

                }else {
                    ProgressUtil.dissmisLoadingDialog();
                    ToastUtil.showShort(ForgetPswActivity.this, "验证码验证失败!");

                }

            }

            @Override
            public void onError() {
                ProgressUtil.dissmisLoadingDialog();
            }
        });

    }

    /**
     * 获取验证码
     */
    private void getVerification() {
        String telephone = activityForgetPswEdPhone.getText().toString().trim();
        //检查手机号码是否合理
        if (!isMobile(telephone)) {
            Toast.makeText(context, "手机号码有误，请检查！", Toast.LENGTH_SHORT).show();
            return;
        }
        timeHandler.sendEmptyMessageDelayed(1, 100);
        String url = NetConstantUrl.GET_PHONE_CODE+"&phone="+telephone;
        VolleyUtil.VolleyGetRequest(this, url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(ForgetPswActivity.this,result)){
                    ToastUtil.showShort(ForgetPswActivity.this,"验证码发送成功!");
                }else {
                    ToastUtil.showShort(ForgetPswActivity.this,"验证码发送失败，请稍后再试!");
                }

            }

            @Override
            public void onError() {
            }
        });
    }

    /**
     * 校验电话号码
     *
     * @param str
     * @return 是否合法的电话号码
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 切换密码可见性
     * @param checkBox
     * @param editText
     */
    public void changePswVisibility(CheckBox checkBox,EditText editText){
        if(checkBox.isChecked()){
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }else{
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        editText.postInvalidate();
        //切换后将EditText光标置于末尾
        CharSequence charSequence = editText.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }
}
