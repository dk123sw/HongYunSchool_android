package cn.xiaocool.hongyunschool.activity.infromation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.bean.SchoolNewsReceiver;
import cn.xiaocool.hongyunschool.bean.SchoolNewsSend;
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
 * 学校消息
 */

public class SchoolNewsActivity extends BaseActivity {


    @BindView(R.id.school_news_lv)
    ListView schoolNewsLv;
    @BindView(R.id.school_news_srl)
    RefreshLayout schoolNewsSrl;
    private Context context;
    private int type;
    private int beginid = 0;

    private CommonAdapter adapter;
    private List<SchoolNewsSend> schoolNewsSendList;
    private List<SchoolNewsReceiver> schoolNewsReceiverList;
    private List<SchoolNewsReceiver.ReceiveBean> receiveBeans;
    public static final String TAG = "学校消息";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_news);
        ButterKnife.bind(this);
        schoolNewsSendList = new ArrayList<>();
        schoolNewsReceiverList = new ArrayList<>();
        receiveBeans = new ArrayList<>();
        setTopName("学校消息");
        context = this;
        //判断身份
        checkIdentity();
        //如果是校长，显示右侧发布学校消息按钮
        if(type == 2) {
            setRightImg(R.drawable.ic_fabu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(AddSchoolNewsActivity.class);
                }
            });
        }
        settingRefresh();
    }

    /**
     * 判断身份
     * 1----家长
     * 2----校长
     * 3----老师
     */
    private void checkIdentity() {
        if(SPUtils.get(context, LocalConstant.USER_TYPE,"").equals("0")){
            type = 1;
        }else if(SPUtils.get(context,LocalConstant.USER_IS_PRINSIPLE,"").equals("y")){
            type = 2;
        }else{
            type =3;
        }
    }


    /**
     * 设置
     */
    private void settingRefresh() {
        schoolNewsSrl.setColorSchemeResources(R.color.white);
        schoolNewsSrl.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.themeColor));
        schoolNewsSrl.setProgressViewOffset(true, 10, 100);
        schoolNewsSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                beginid = 0;
                requsetData();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        schoolNewsSrl.setRefreshing(false);
                    }
                }, 5000);
            }
        });

        //上拉加载
        schoolNewsSrl.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                schoolNewsSrl.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(type == 1||type ==3){
                            beginid = schoolNewsReceiverList.size();
                        }else if (type == 2){
                            beginid = schoolNewsSendList.size();
                        }
                        requsetData();
                        schoolNewsSrl.setLoading(false);
                    }
                }, 1000);
            }
        });

    }

    @Override
    public void requsetData() {
        String url = "";
        //判断身份并请求对应数据
        if(type == 1){
            url = NetConstantUrl.GET_SCHOOL_NEWS_RECEIVE + "&userid=" + SPUtils.get(context,LocalConstant.USER_ID,"")+"&beginid=" + beginid;
        }else if(type == 2){
            url = NetConstantUrl.GET_SCHOOL_NEWS_SEND + "&send_user_id=" +SPUtils.get(context,LocalConstant.USER_ID,"")+"&beginid=" + beginid;
        }else if(type == 3){
            url = NetConstantUrl.GET_SCHOOL_NEWS_RECEIVE + "&userid=" + SPUtils.get(context,LocalConstant.USER_ID,"") + "&type=1"+"&beginid=" + beginid;
        }
        Log.e(TAG, url);
        VolleyUtil.VolleyGetRequest(this, url, new
                VolleyUtil.VolleyJsonCallback() {
                    @Override
                    public void onSuccess(String result) {
                        if (JsonResult.JSONparser(getBaseContext(), result)) {
                            schoolNewsSrl.setRefreshing(false);
                            setAdapter(result);
                        }else {
                            schoolNewsSrl.setRefreshing(false);
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
        if(type == 1||type ==3){
            schoolNewsReceiverList.clear();
            schoolNewsReceiverList.addAll(getBeanFromJsonReceive(result));
            receiveBeans.clear();
            receiveBeans.addAll(changeBean(schoolNewsReceiverList));
            Collections.sort(receiveBeans, new Comparator<SchoolNewsReceiver.ReceiveBean>() {
                @Override
                public int compare(SchoolNewsReceiver.ReceiveBean lhs, SchoolNewsReceiver.ReceiveBean rhs) {
                    return (int) (Long.parseLong(rhs.getSend_message().getMessage_time()) - Long.parseLong(lhs.getSend_message().getMessage_time()));
                }
            });
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new CommonAdapter<SchoolNewsReceiver.ReceiveBean>(getBaseContext(), receiveBeans, R.layout.school_news_item) {
                    @Override
                    public void convert(ViewHolder holder, SchoolNewsReceiver.ReceiveBean datas) {
                        setReceiveItem(holder, datas);
                    }
                };
                schoolNewsLv.setAdapter(adapter);
            }
        }else if(type == 2){
            schoolNewsSendList.clear();
            schoolNewsSendList.addAll(getBeanFromJsonSend(result));
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new CommonAdapter<SchoolNewsSend>(getBaseContext(), schoolNewsSendList, R.layout.school_news_item) {
                    @Override
                    public void convert(ViewHolder holder, SchoolNewsSend datas) {
                        setSendItem(holder, datas);
                    }
                };
                schoolNewsLv.setAdapter(adapter);
            }
        }

    }

    private List<SchoolNewsReceiver.ReceiveBean> changeBean(List<SchoolNewsReceiver> schoolNewsReceiverList) {
        List<SchoolNewsReceiver.ReceiveBean> receiver = new ArrayList<>();
        for(int i = 0;i<schoolNewsReceiverList.size();i++){
            receiver.addAll(schoolNewsReceiverList.get(i).getReceive());
        }
        return receiver;
    }


    /**
     * 对item 操作(接收)
     * @param holder
     * @param datas
     */
    private void setReceiveItem(ViewHolder holder, SchoolNewsReceiver.ReceiveBean datas) {

        //获取图片字符串数组
        ArrayList<String> images = new ArrayList<>();
        for (int i=0;i<datas.getPic().size();i++){
            images.add(datas.getPic().get(i).getPicture_url());
        }

        /*//判断已读和未读

        final ArrayList<SchoolNewsReceiver.ReceiverBean> notReads = new ArrayList<>();
        final ArrayList<SchoolNewsReceiver.ReceiverBean> alreadyReads = new ArrayList<>();
        if (datas.getReceiver().size()>0){
            for (int i=0;i<datas.getReceiver().size();i++){
                if (datas.getReceiver().get(i).getRead_time()==null||datas.getReceiver().get(i).getRead_time().equals("null")){
                    notReads.add(datas.getReceiver().get(i));
                }else {
                    alreadyReads.add(datas.getReceiver().get(i));
                }
            }
        }*/

        holder.setText(R.id.item_sn_content, datas.getSend_message().getMessage_content())
                .setTimeText(R.id.item_sn_time, datas.getSend_message().getMessage_time())
                .setText(R.id.item_sn_nickname,datas.getSend_message().getSend_user_name())
                .setImageByUrl(R.id.item_sn_head_iv,datas.getSend_message().getPhoto())
                .setItemImages(this, R.id.item_sn_onepic, R.id.item_sn_gridpic, images);
                //.setText(R.id.item_sn_read, "总发" + datas.getReceiver().size() + " 已读" + alreadyReads.size() + " 未读" + notReads.size());
    }

    /**
     * 对item 操作(发送)
     * @param holder
     * @param datas
     */
    private void setSendItem(ViewHolder holder, SchoolNewsSend datas) {

        //获取图片字符串数组
        ArrayList<String> images = new ArrayList<>();
        for (int i=0;i<datas.getPicture().size();i++){
            images.add(datas.getPicture().get(i).getPicture_url());
        }

        /*//判断已读和未读

        final ArrayList<SchoolNewsSend.ReceiverBean> notReads = new ArrayList<>();
        final ArrayList<SchoolNewsSend.ReceiverBean> alreadyReads = new ArrayList<>();
        if (datas.getReceiver().size()>0){
            for (int i=0;i<datas.getReceiver().size();i++){
                if (datas.getReceiver().get(i).getRead_time()==null||datas.getReceiver().get(i).getRead_time().equals("null")){
                    notReads.add(datas.getReceiver().get(i));
                }else {
                    alreadyReads.add(datas.getReceiver().get(i));
                }
            }
        }*/

        holder.setText(R.id.item_sn_content, datas.getMessage_content())
        .setTimeText(R.id.item_sn_time, datas.getMessage_time())
        .setText(R.id.item_sn_nickname, datas.getSend_user_name())
                .setImageByUrl(R.id.item_sn_head_iv, datas.getUserphoto())
                .setItemImages(this, R.id.item_sn_onepic, R.id.item_sn_gridpic, images);
        //.setText(R.id.item_sn_read,"总发" + datas.getReceiver().size()+" 已读"+alreadyReads.size()+" 未读"+notReads.size());

        //进入已读未读界面
        /*holder.getView(R.id.item_sn_read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("yidu",alreadyReads);
                bundle.putSerializable("weidu",notReads);
                startActivity(ReadListSchoolAnnonceActivity.class,bundle);
            }
        });*/
    }

    /**
     * 字符串转模型（发送）
     * @param result
     * @return
     */
    private List<SchoolNewsSend> getBeanFromJsonSend(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<SchoolNewsSend>>() {
        }.getType());
    }

    /**
     * 字符串转模型（接收）
     * @param result
     * @return
     */
    private List<SchoolNewsReceiver> getBeanFromJsonReceive(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<SchoolNewsReceiver>>() {
        }.getType());
    }
}
