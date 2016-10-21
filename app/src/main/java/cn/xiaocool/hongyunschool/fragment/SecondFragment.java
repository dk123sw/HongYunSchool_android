package cn.xiaocool.hongyunschool.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.activity.infromation.ClassNewsActivity;
import cn.xiaocool.hongyunschool.activity.infromation.MessageActivity;
import cn.xiaocool.hongyunschool.activity.infromation.ParentMessageActivity;
import cn.xiaocool.hongyunschool.activity.infromation.SchoolAnnounceActivity;
import cn.xiaocool.hongyunschool.activity.infromation.SchoolNewsActivity;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.utils.BaseFragment;
import cn.xiaocool.hongyunschool.utils.SPUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends BaseFragment {


    @BindView(R.id.second_top_name)
    TextView secondTopName;
    @BindView(R.id.sn_tv_mirror_title)
    TextView snTvMirrorTitle;
    @BindView(R.id.cn_tv_mirror_title)
    TextView cnTvMirrorTitle;
    @BindView(R.id.sa_tv_mirror_title)
    TextView saTvMirrorTitle;
    @BindView(R.id.msge_tv_mirror_title)
    TextView msgeTvMirrorTitle;
    @BindView(R.id.second_rl_school_news)
    RelativeLayout secondRlSchoolNews;
    @BindView(R.id.second_rl_class_news)
    RelativeLayout secondRlClassNews;
    @BindView(R.id.second_rl_school_announce)
    RelativeLayout secondRlSchoolAnnounce;
    @BindView(R.id.second_rl_message)
    RelativeLayout secondRlMessage;
    @BindView(R.id.second_rl_message_back)
    RelativeLayout secondRlMessageBack;
    private Context context;


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }


    @Override
    public void initData() {
        secondTopName.setText("消息");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        context = getActivity();
        //根据登录身份确定布局
        checkLayout();
        return rootView;
    }

    /**
     * 根据登录身份确定布局
     */
    private void checkLayout() {
/*        //家长（隐藏校内通知、短息发送）
        if (SPUtils.get(context, LocalConstant.USER_TYPE, "").equals("0")) {
            secondRlSchoolAnnounce.setVisibility(View.GONE);
            secondRlMessage.setVisibility(View.GONE);
        }*/
        //任课老师(隐藏班级消息)
        /*if (SPUtils.get(context, LocalConstant.USER_TYPE, "").equals("1")
                && !SPUtils.get(context, LocalConstant.USER_IS_CLASSLEADER, "").equals("y")
                && !SPUtils.get(context, LocalConstant.USER_IS_PRINSIPLE, "").equals("y")) {
            secondRlClassNews.setVisibility(View.GONE);
        }*/

        if (SPUtils.get(context,LocalConstant.USER_IS_PRINSIPLE,"").equals("y")){
            secondRlMessageBack.setVisibility(View.VISIBLE);
        }else {
            secondRlMessageBack.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.second_rl_school_news, R.id.second_rl_class_news, R.id.second_rl_school_announce, R.id.second_rl_message, R.id.second_rl_message_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.second_rl_school_news:
                startActivity(new Intent(mActivity, SchoolNewsActivity.class));
                break;
            case R.id.second_rl_class_news:
                startActivity(new Intent(mActivity, ClassNewsActivity.class));
                break;
            case R.id.second_rl_school_announce:
                startActivity(new Intent(mActivity, SchoolAnnounceActivity.class));
                break;
            case R.id.second_rl_message:
                startActivity(new Intent(mActivity, MessageActivity.class));
                break;
            case R.id.second_rl_message_back:
                Intent intent = new Intent();
                intent.putExtra(LocalConstant.PARENT_MESSAGE_FLAG,"back");
                intent.setClass(mActivity, ParentMessageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
