package cn.xiaocool.hongyunschool.activity.schoolnet.zhuanlan;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.bean.WebOther;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.CommonAdapter;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.ViewHolder;
/**
 * 五项公开专栏除课程以为其他4项
 */
public class WebOtherListActivity extends BaseActivity {

    @BindView(R.id.web_list)
    ListView webList;
    @BindView(R.id.web_list_swip)
    SwipeRefreshLayout webListSwip;

    private Context context;
    private List<WebOther> webOthers;
    private CommonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_other_list);
        ButterKnife.bind(this);
        context = this;
        webOthers = new ArrayList<>();
        settingRefresh();
    }

    /**
     * 设置
     */
    private void settingRefresh() {
        webListSwip.setColorSchemeResources(R.color.white);
        webListSwip.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.themeColor));
        webListSwip.setProgressViewOffset(true, 10, 100);
        webListSwip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requsetData();
            }
        });

        setTopName(getIntent().getStringExtra("title") != null ? getIntent().getStringExtra("title") : "列表");

        webList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("title",getIntent().getStringExtra("title"));
                bundle.putSerializable(LocalConstant.WEB_FLAG_FIVE, webOthers.get(position));
                startActivity(FivePublicWebDetailActivity.class, bundle);
            }
        });
    }

    @Override
    public void requsetData() {
        String url = "";
        String schoolid = SPUtils.get(context,LocalConstant.SCHOOL_ID,"1").toString();
        switch (getIntent().getStringExtra("title")){
            case "学校作业":
                url = NetConstantUrl.FIVE_PUBLIC_HOMEWORK +schoolid;
                break;
            case "校本课程":
                url = NetConstantUrl.FIVE_PUBLIC_SUBJECT1+schoolid;
                break;
            case "选修课程":
                url = NetConstantUrl.FIVE_PUBLIC_SUBJECT2+schoolid;
                break;
            case "课时":
                url = NetConstantUrl.FIVE_PUBLIC_SUBJECT_TIME+schoolid;
                break;
            case "期末检测":
                url = NetConstantUrl.FIVE_PUBLIC_END+schoolid;
                break;
            case "节假日":
                url = NetConstantUrl.FIVE_PUBLIC_HOLIDAY+schoolid;
                break;
        }
        VolleyUtil.VolleyGetRequest(this, url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(WebOtherListActivity.this, result)) {
                    webListSwip.setRefreshing(false);
                    webOthers.clear();
                    webOthers.addAll(getBeanFromJson(result));
                    adapter = new CommonAdapter<WebOther>(context,webOthers,R.layout.item_other_web) {
                        @Override
                        public void convert(ViewHolder holder, WebOther webOther) {
                            holder.setText(R.id.title,webOther.getPost_title());
                        }
                    };
                    webList.setAdapter(adapter);
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
    private List<WebOther> getBeanFromJson(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<WebOther>>() {
        }.getType());
    }
}
