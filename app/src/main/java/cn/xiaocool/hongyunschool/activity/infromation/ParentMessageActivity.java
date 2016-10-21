package cn.xiaocool.hongyunschool.activity.infromation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.bean.FeedbackLeader;
import cn.xiaocool.hongyunschool.bean.FeedbackSend;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.SendRequest;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.CommonAdapter;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.PopInputManager;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.ToastUtil;
import cn.xiaocool.hongyunschool.utils.ViewHolder;
import cn.xiaocool.hongyunschool.view.CommentPopupWindow;
import cn.xiaocool.hongyunschool.view.RefreshLayout;

/**
 * 家长信箱
 */
public class ParentMessageActivity extends BaseActivity {


    @BindView(R.id.web_parent_lv)
    ListView webParentLv;
    @BindView(R.id.web_parent_swip)
    RefreshLayout webParentSwip;

    private CommonAdapter adapter;
    private ArrayList<FeedbackSend> feedbackSends;
    private ArrayList<FeedbackLeader> feedbackLeaders;
    private Context context;
    private int type ;
    private int beginid = 0;
    private CommentPopupWindow commentPopupWindow;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x110:
                    if (msg.obj != null) {
                        if (JsonResult.JSONparser(context, String.valueOf(msg.obj))) {
                            requsetData();
                            ToastUtil.showShort(context, "回复成功！");
                        }
                    }
                    break;
            }
        }
    };
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_message);
        ButterKnife.bind(this);
        context = this;
        feedbackSends = new ArrayList<>();
        feedbackLeaders = new ArrayList<>();
        setTopName("家长信箱");
        checkIdentity();
        userid = SPUtils.get(context,LocalConstant.USER_ID,"").toString();
        if(type == 1){
            setRightText("反馈").setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(AddParentMessageActivity.class);
                }
            });
        }
        settingRefresh();
    }

    /**
     * 判断身份
     * 1-----家长
     * 2-----老师
     */
    private void checkIdentity() {
        if(SPUtils.get(context, LocalConstant.USER_TYPE,"").equals("0")){
            type = 1;
        }else {
            type = 2;
        /*}else if(SPUtils.get(context,LocalConstant.USER_IS_CLASSLEADER,"").equals("y")){
            type = 3;*/
        }
        if (getIntent().getStringExtra(LocalConstant.PARENT_MESSAGE_FLAG)!=null){
            type = 3;
        }
    }

    /**
     * 设置
     */
    private void settingRefresh() {
        webParentSwip.setColorSchemeResources(R.color.white);
        webParentSwip.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.themeColor));
        webParentSwip.setProgressViewOffset(true, 10, 100);
        webParentSwip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webParentSwip.setRefreshing(false);
                    }
                }, 5000);
                beginid = 0;
                requsetData();

            }
        });

        //上拉加载
        webParentSwip.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                webParentSwip.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (type == 1) {
                            beginid = feedbackSends.size();
                        } else if (type == 2||type ==3) {
                            beginid = feedbackLeaders.size();
                        }
                        requsetData();
                        webParentSwip.setLoading(false);
                    }
                }, 1000);
            }
        });
    }
    @Override
    public void requsetData() {
        String url = "";
        if(type == 1){
            url = NetConstantUrl.GET_FEEDBACK_PARENT + SPUtils.get(context,LocalConstant.USER_ID,"")+"&beginid=" + beginid;
        }else if (type == 2){
            url = NetConstantUrl.GET_FEEDBACK_BE + SPUtils.get(context,LocalConstant.SCHOOL_ID,"")+"&beginid=" + beginid;
        }else {
            url = NetConstantUrl.GET_FEEDBACK_NULL + SPUtils.get(context,LocalConstant.SCHOOL_ID,"")+"&beginid=" + beginid;
        }
        VolleyUtil.VolleyGetRequest(this, url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(ParentMessageActivity.this, result)) {
                    webParentSwip.setRefreshing(false);
                    if (type == 1) {
                        feedbackSends.clear();
                        feedbackSends.addAll(getBeanFromJson(result));
                        setAdapter(result);
                    }
                    if (type == 2||type ==3) {
                        feedbackLeaders.clear();
                        feedbackLeaders.addAll(getBeanFromJsonLeader(result));
                        setAdapterLeader(result);
                    }
                }else {
                    webParentSwip.setRefreshing(false);
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    /**
     * list 设置adapter
     * @param result
     */
    private void setAdapter(String result) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new CommonAdapter<FeedbackSend>(getBaseContext(), feedbackSends, R.layout.item_parent_message) {
                @Override
                public void convert(ViewHolder holder, FeedbackSend datas) {
                    setItem(holder, datas);
                }
            };
            webParentLv.setAdapter(adapter);
        }
    }

    /**
     * list 设置adapter
     * @param result
     */
    private void setAdapterLeader(String result) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new CommonAdapter<FeedbackLeader>(getBaseContext(), feedbackLeaders, R.layout.item_parent_message) {
                @Override
                public void convert(ViewHolder holder, FeedbackLeader datas) {
                    setItemLeader(holder, datas);
                }
            };
            webParentLv.setAdapter(adapter);
        }
    }


    /**
     * 对item 操作
     * @param holder
     * @param datas
     */
    private void setItem(ViewHolder holder, FeedbackSend datas) {
        holder.setText(R.id.item_sn_nickname,datas.getName())
                .setTimeText(R.id.item_sn_time,datas.getSend_time())
                .setImageByUrl(R.id.item_sn_head_iv,datas.getPhoto())
                .setText(R.id.item_sn_content, datas.getContent());
        holder.getView(R.id.btn_parent_send).setVisibility(View.GONE);
        if(datas.getFeedback().equals("")||datas.getFeedback().length()==0){
            holder.getView(R.id.ll_feedback).setVisibility(View.GONE);
        }else{
            holder.getView(R.id.ll_feedback).setVisibility(View.VISIBLE);
            holder.setText(R.id.teacher_name,datas.getTeacher_name())
                    .setTimeText(R.id.teacher_time,datas.getFeed_time())
                    .setText(R.id.teacher_content,datas.getFeedback())
                    .setImageByUrl(R.id.teacher_avatar, datas.getTeacher_photo());
        }
    }

    private void setItemLeader(ViewHolder holder, final FeedbackLeader datas) {
        holder.setText(R.id.item_sn_nickname,datas.getParent_name())
                .setTimeText(R.id.item_sn_time,datas.getSend_time())
                .setImageByUrl(R.id.item_sn_head_iv, datas.getParent_photo())
                .setText(R.id.item_sn_content, datas.getContent());

        if(datas.getFeedback().equals("")||datas.getFeedback().length()==0){
            holder.getView(R.id.ll_feedback).setVisibility(View.GONE);
            holder.getView(R.id.btn_parent_send).setVisibility(View.VISIBLE);
            holder.getView(R.id.btn_parent_send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentPopupWindow = new CommentPopupWindow(context, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()) {
                                case R.id.tv_comment:
                                    if (commentPopupWindow.ed_comment.getText().length() > 0) {
                                        new SendRequest(context,handler).feedback(datas.getId(),userid,commentPopupWindow.ed_comment.getText().toString(),0x110);
                                        commentPopupWindow.dismiss();
                                        commentPopupWindow.ed_comment.setText("");
                                    } else {

                                        Toast.makeText(context, "发送内容不能为空", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                        }
                    });
                    final EditText editText = commentPopupWindow.ed_comment;
                    commentPopupWindow.showAtLocation(webParentSwip, Gravity.BOTTOM, 0, 0);
                    //弹出系统输入法
                    PopInputManager.popInput(editText);
                }
            });
        }else{
            holder.getView(R.id.btn_parent_send).setVisibility(View.GONE);
            holder.getView(R.id.ll_feedback).setVisibility(View.VISIBLE);
            holder.setText(R.id.teacher_name,datas.getTeachername())
                    .setTimeText(R.id.teacher_time,datas.getFeed_time())
                    .setText(R.id.teacher_content,datas.getFeedback())
                    .setImageByUrl(R.id.teacher_avatar, datas.getTeacheravatar());
        }
    }


    /**
     * 字符串转模型
     *
     * @param result
     * @return
     */
    private List<FeedbackSend> getBeanFromJson(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<FeedbackSend>>() {
        }.getType());
    }

    /**
     * 字符串转模型
     *
     * @param result
     * @return
     */
    private List<FeedbackLeader> getBeanFromJsonLeader(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<FeedbackLeader>>() {
        }.getType());
    }
}
