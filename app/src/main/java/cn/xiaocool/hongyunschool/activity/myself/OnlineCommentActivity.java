package cn.xiaocool.hongyunschool.activity.myself;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.bean.OnlineComment;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.CommonAdapter;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.ViewHolder;
import cn.xiaocool.hongyunschool.view.RefreshLayout;
/**
 * 在线客服
 */
public class OnlineCommentActivity extends BaseActivity {

    @BindView(R.id.fragment_third_lv_trend)
    ListView fragmentThirdLvTrend;
    @BindView(R.id.fragment_third_srl_trend)
    RefreshLayout fragmentThirdSrlTrend;
    private Context context;
    private List<OnlineComment> onlineComments;
    private CommonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_comment);
        ButterKnife.bind(this);
        context = this;
        onlineComments = new ArrayList<>();
        setTopName("在线客服");
        setRefresh();
        setRightText("反馈").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(FeedbackActivity.class);
            }
        });
    }

    /**
     * 设置下拉刷新
     */
    private void setRefresh() {
        fragmentThirdSrlTrend.setColorSchemeResources(R.color.white);
        fragmentThirdSrlTrend.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.themeColor));
        fragmentThirdSrlTrend.setProgressViewOffset(true, 10, 100);
        fragmentThirdSrlTrend.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requsetData();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentThirdSrlTrend.setRefreshing(false);
                    }
                }, 5000);
            }
        });
    }

    @Override
    public void requsetData() {
        String url = NetConstantUrl.GET_ONLINE_COMMENT + SPUtils.get(context, LocalConstant.USER_ID,"");
        VolleyUtil.VolleyGetRequest(context, url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(context, result)) {
                    fragmentThirdSrlTrend.setRefreshing(false);
                    onlineComments.clear();
                    onlineComments.addAll(getBeanFromJson(result));
                    adapter = new CommonAdapter<OnlineComment>(context,onlineComments,R.layout.item_online_comment) {
                        @Override
                        public void convert(ViewHolder holder, OnlineComment onlineComment) {
                            String date = String.valueOf(new Date().getTime()/1000);
                            holder.setText(R.id.tv_comment,onlineComment.getContent())
                                    .setTimeText(R.id.tv_comment_time,onlineComment.getSend_time())
                                    .setText(R.id.tv_feedback,onlineComment.getFeedback().equals("")?"客服还没有给回复":onlineComment.getFeedback())
                                    .setTimeText(R.id.tv_feedback_time,onlineComment.getFeed_time().equals("0")?date:onlineComment.getFeed_time())
                                    .setImageResource(R.id.iv_feedback,onlineComment.getFeedback().equals("")?R.drawable.weihuifu:R.drawable.huifu);
                        }
                    };
                    fragmentThirdLvTrend.setAdapter(adapter);
                }else {
                    fragmentThirdSrlTrend.setRefreshing(false);
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    /**
     * 字符串转模型
     * @param result
     * @return
     */
    private List<OnlineComment> getBeanFromJson(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<OnlineComment>>() {
        }.getType());
    }
}
