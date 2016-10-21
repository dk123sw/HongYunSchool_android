package cn.xiaocool.hongyunschool.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.activity.myself.AddressActivity;
import cn.xiaocool.hongyunschool.activity.myself.MyIntegrationActivity;
import cn.xiaocool.hongyunschool.activity.myself.OnlineCommentActivity;
import cn.xiaocool.hongyunschool.activity.myself.PersonalInfoActivity;
import cn.xiaocool.hongyunschool.activity.myself.QRCodeActivity;
import cn.xiaocool.hongyunschool.activity.myself.SettingActivity;
import cn.xiaocool.hongyunschool.activity.schoolnet.zhuanlan.WebListActivity;
import cn.xiaocool.hongyunschool.bean.UserInfo;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseFragment;
import cn.xiaocool.hongyunschool.utils.ImgLoadUtil;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.ToastUtil;
import cn.xiaocool.hongyunschool.view.RoundImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FourFragment extends BaseFragment {


    @BindView(R.id.top_name)
    TextView topName;
    @BindView(R.id.fragment_four_iv_setting)
    ImageView fragmentFourIvSetting;
    @BindView(R.id.top_bar)
    RelativeLayout topBar;
    @BindView(R.id.fragment_four_iv_avatar)
    RoundImageView fragmentFourIvAvatar;
    @BindView(R.id.fragment_four_tv_name)
    TextView fragmentFourTvName;
    @BindView(R.id.fragment_four_rl_service)
    RelativeLayout fragmentFourRlService;
    @BindView(R.id.fragment_four_rl_address)
    RelativeLayout fragmentFourRlAddress;
    @BindView(R.id.fragment_four_rl_jifen)
    RelativeLayout fragmentFourRlJifen;
    @BindView(R.id.fragment_four_rl_system)
    RelativeLayout fragmentFourRlSystem;
    @BindView(R.id.fragment_four_rl_online)
    RelativeLayout fragmentFourRlOnline;
    @BindView(R.id.fragment_four_rl_code)
    RelativeLayout fragmentFourRlCode;
    private Context context;
    private UserInfo userInfo;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_four, container, false);
    }


    @Override
    public void initData() {
        String url = NetConstantUrl.GET_USER_INFO + "&userid=" + SPUtils.get(context, LocalConstant.USER_ID, "");
        VolleyUtil.VolleyGetRequest(context, url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(context, result)) {
                    showUserInfo(result);
                }
            }

            @Override
            public void onError() {

            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
    }

    @OnClick({R.id.fragment_four_iv_setting, R.id.fragment_four_iv_avatar, R.id.fragment_four_rl_service, R.id.fragment_four_rl_address, R.id.fragment_four_rl_jifen, R.id.fragment_four_rl_system, R.id.fragment_four_rl_online, R.id.fragment_four_rl_code})
    public void onClick(View view) {
        switch (view.getId()) {
            //设置
            case R.id.fragment_four_iv_setting:
                context.startActivity(new Intent(context, SettingActivity.class));
                break;
            //个人信息
            case R.id.fragment_four_iv_avatar:
                context.startActivity(new Intent(context, PersonalInfoActivity.class));
                break;
            //服务购买
            case R.id.fragment_four_rl_service:
                ToastUtil.showShort(context,"当前版本为试用版本");
                //context.startActivity(new Intent(context, ServiceBuyActivity.class));
                break;
            //通讯录
            case R.id.fragment_four_rl_address:
                context.startActivity(new Intent(context, AddressActivity.class));
                break;
            //我的积分
            case R.id.fragment_four_rl_jifen:
                context.startActivity(new Intent(context, MyIntegrationActivity.class));
                break;
            //系统通知
            case R.id.fragment_four_rl_system:
                Intent intent = new Intent(mActivity, WebListActivity.class);
                intent.putExtra("title", "系统通知");
                intent.putExtra(LocalConstant.WEB_FLAG, LocalConstant.SYSTEN_NEWS);
                startActivity(intent);
                break;
            //在线客服
            case R.id.fragment_four_rl_online:
                context.startActivity(new Intent(context, OnlineCommentActivity.class));
                break;
            //客户端二维码
            case R.id.fragment_four_rl_code:
                context.startActivity(new Intent(context,QRCodeActivity.class));
                break;
        }
    }

    /**
     * 显示个人信息
     *
     * @param result
     */
    private void showUserInfo(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userInfo = new Gson().fromJson(data, new TypeToken<UserInfo>() {
        }.getType());
        fragmentFourTvName.setText(userInfo.getName());
        ImgLoadUtil.display(NetConstantUrl.IMAGE_URL + userInfo.getPhoto(), fragmentFourIvAvatar);
    }
}
