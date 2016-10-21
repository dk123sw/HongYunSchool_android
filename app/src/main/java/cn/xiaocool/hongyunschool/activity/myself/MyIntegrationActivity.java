package cn.xiaocool.hongyunschool.activity.myself;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.bean.Integration;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.CommonAdapter;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.ViewHolder;
/**
 * 我的积分
 */
public class MyIntegrationActivity extends BaseActivity {
    @BindView(R.id.activity_my_integration_tv_number)
    TextView activityMyIntegrationTvNumber;
    @BindView(R.id.activity_my_integration_lv)
    ListView activityMyIntegrationLv;
    private Context context;
    private List<Integration> integrations;
    private CommonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_integration);
        ButterKnife.bind(this);
        setTopName("积分");
        context = this;
        integrations = new ArrayList<>();
    }

    @Override
    public void requsetData() {
        String url_total = NetConstantUrl.GET_INTEGRATION_TOTAL + SPUtils.get(context, LocalConstant.USER_ID,"");
        String url_list = NetConstantUrl.GET_INTEGRATION_LIST + SPUtils.get(context, LocalConstant.USER_ID,"");
        VolleyUtil.VolleyGetRequest(context, url_total, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if(JsonResult.JSONparser(MyIntegrationActivity.this, result)){
                    JSONObject data = null;
                    try {
                        JSONObject object = new JSONObject(result);
                        data = object.optJSONObject("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    activityMyIntegrationTvNumber.setText(data.optString("integral"));
                }else{
                    activityMyIntegrationTvNumber.setText("0");
                }
            }

            @Override
            public void onError() {
                activityMyIntegrationTvNumber.setText("0");
            }
        });
        VolleyUtil.VolleyGetRequest(context, url_list, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(context,result)){
                    integrations.clear();
                    integrations.addAll(getBeanFromJson(result));
                    adapter = new CommonAdapter<Integration>(context,integrations,R.layout.item_integration_info) {
                        @Override
                        public void convert(ViewHolder holder, Integration integration) {
                            holder.setText(R.id.item_integration_tv_name,checkTypeToName(integration.getType()))
                                    .setText(R.id.item_integration_tv_detail,checkTypeToNumber(integration.getType()))
                                    .setTimeText(R.id.item_integration_tv_time,integration.getCreate_time());
                        }
                    };
                    activityMyIntegrationLv.setAdapter(adapter);
                }else{
                    activityMyIntegrationTvNumber.setText("0");
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    /**
     * 判断积分类型（名称）
     * @param type
     */
    public String checkTypeToName(String type){
        if(type.equals("1")){
            return "发布动态";
        }
        if(type.equals("2")){
            return "发布班级消息";
        }
        if(type.equals("3")){
            return "发布学校消息";
        }
        return "";
    }

    /**
     * 判断积分类型（积分）
     * @param type
     */
    public String checkTypeToNumber(String type){
        if(type.equals("1")){
            return "+4";
        }
        if(type.equals("2")){
            return "+3";
        }
        if(type.equals("3")){
            return "+6";
        }
        return "";
    }

    /**
     * 字符串转模型
     *
     * @param result
     * @return
     */
    private List<Integration> getBeanFromJson(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<Integration>>() {
        }.getType());
    }
}
