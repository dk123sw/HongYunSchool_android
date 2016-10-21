package cn.xiaocool.hongyunschool.activity.schoolnet.zhuanlan;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import cn.xiaocool.hongyunschool.bean.WebListInfo;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.CommonAdapter;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.ViewHolder;
/**
 * 未被使用
 */
public class FivePublicDetailActivity extends BaseActivity {

    @BindView(R.id.web_five_detail_list)
    ListView webFiveDetailList;
    @BindView(R.id.web_five_detail_swip)
    SwipeRefreshLayout webFiveDetailSwip;

    private ArrayList<WebListInfo> datalist;
    private CommonAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_public_detail);
        ButterKnife.bind(this);
        datalist = new ArrayList<>();
        settingRefresh();
    }

    /**
     * 设置
     */
    private void settingRefresh() {
        webFiveDetailSwip.setColorSchemeResources(R.color.white);
        webFiveDetailSwip.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.themeColor));
        webFiveDetailSwip.setProgressViewOffset(true, 10, 100);
        webFiveDetailSwip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requsetData();
            }
        });

        setTopName(getIntent().getStringExtra("title") != null ? getIntent().getStringExtra("title") : "列表");
    }
    @Override
    public void requsetData() {

        String url = "";

        VolleyUtil.VolleyGetRequest(this, url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(FivePublicDetailActivity.this, result)) {
                    datalist.clear();
                    datalist.addAll(getBeanFromJson(result));
                    setAdapter();
                }
            }

            @Override
            public void onError() {

            }
        });
    }


    private void setAdapter() {
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }else {
            adapter = new CommonAdapter<WebListInfo>(this,datalist,R.layout.item_five_public_detail) {
                @Override
                public void convert(ViewHolder holder, WebListInfo webListInfo) {
                    holder.setText(R.id.item_five_pd_tv,"");
                }

            };
            webFiveDetailList.setAdapter(adapter);
        }
    }

    /**
     * 字符串转模型
     * @param result
     * @return
     */
    private List<WebListInfo> getBeanFromJson(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<WebListInfo>>() {
        }.getType());
    }
}
