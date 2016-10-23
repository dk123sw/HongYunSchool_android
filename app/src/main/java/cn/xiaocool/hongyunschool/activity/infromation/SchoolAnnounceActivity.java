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
import cn.xiaocool.hongyunschool.bean.SchoolAnnouceReceive;
import cn.xiaocool.hongyunschool.bean.SchoolAnnouncement;
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
 * 校内通知
 */

public class SchoolAnnounceActivity extends BaseActivity {


    @BindView(R.id.school_news_lv)
    ListView schoolNewsLv;
    @BindView(R.id.school_news_srl)
    RefreshLayout schoolNewsSrl;

    private CommonAdapter adapter;
    private List<SchoolAnnouncement> schoolAnnouncements;
    private List<SchoolAnnouceReceive> schoolAnnouceReceives;
    private Context context;
    private int type;
    private int beginid = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_news);
        ButterKnife.bind(this);
        context = this;
        schoolAnnouncements = new ArrayList<>();
        schoolAnnouceReceives = new ArrayList<>();
        setTopName("校内通知");
        checkIdentity();
        //校长有发送权限
        if(type == 1){
            setRightImg(R.drawable.ic_fabu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(AddSchoolAnnounceActivity.class);
                }
            });
        }
        settingRefresh();
    }

    /**
     * 判断身份
     * 1-----校长
     * 2-----老师
     */
    private void checkIdentity() {
        if(SPUtils.get(context, LocalConstant.USER_IS_PRINSIPLE,"").equals("y")){
            type = 1;
        }else{
            type = 2;
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
                        if (type == 2) {
                            beginid = schoolAnnouceReceives.size();
                        } else if (type == 1) {
                            beginid = schoolAnnouncements.size();
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
        if(type == 1){
            url = NetConstantUrl.GET_ANNOUNCE_SEND + "&userid=" + SPUtils.get(context,LocalConstant.USER_ID,"")+"&beginid=" + beginid;
        }else if(type == 2){
            url = NetConstantUrl.GET_ANNOUNCE_RECEIVE + "&receiverid=" + SPUtils.get(context,LocalConstant.USER_ID,"")+"&beginid=" + beginid;
        }
        Log.e("requsetData",url);
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
        if(type == 1){
            schoolAnnouncements.clear();
            schoolAnnouncements.addAll(getBeanFromJsonSend(result));
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new CommonAdapter<SchoolAnnouncement>(getBaseContext(), schoolAnnouncements, R.layout.school_announcement_item) {
                    @Override
                    public void convert(ViewHolder holder, SchoolAnnouncement datas) {
                        setSendItem(holder, datas);
                    }
                };
                schoolNewsLv.setAdapter(adapter);
            }
        }else if(type == 2){
            schoolAnnouceReceives.clear();
            schoolAnnouceReceives.addAll(getBeanFromJsonReceive(result));
            Collections.sort(schoolAnnouceReceives, new Comparator<SchoolAnnouceReceive>() {
                @Override
                public int compare(SchoolAnnouceReceive lhs, SchoolAnnouceReceive rhs) {
                    return (int) (Long.parseLong(rhs.getNotice_info().get(0).getCreate_time())-Long.parseLong(lhs.getNotice_info().get(0).getCreate_time()));
                }
            });
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new CommonAdapter<SchoolAnnouceReceive>(getBaseContext(), schoolAnnouceReceives, R.layout.school_announcement_item) {
                    @Override
                    public void convert(ViewHolder holder, SchoolAnnouceReceive datas) {
                        setReceiveItem(holder, datas);
                    }
                };
                schoolNewsLv.setAdapter(adapter);
            }
        }
    }

    /**
     * 对item 操作（发送）
     * @param holder
     * @param datas
     */
    private void setSendItem(ViewHolder holder, SchoolAnnouncement datas) {

        //获取图片字符串数组
        ArrayList<String> images = new ArrayList<>();
        for (int i=0;i<datas.getPic().size();i++){
            images.add(datas.getPic().get(i).getPictureurl());
        }

        //判断已读和未读

        final ArrayList<SchoolAnnouncement.ReceiveListBean> notReads = new ArrayList<>();
        final ArrayList<SchoolAnnouncement.ReceiveListBean> alreadyReads = new ArrayList<>();
        if (datas.getReceive_list().size()>0){
            for (int i=0;i<datas.getReceive_list().size();i++){
                if (datas.getReceive_list().get(i).getCreate_time()==null||datas.getReceive_list().get(i).getCreate_time().equals("0")){
                    notReads.add(datas.getReceive_list().get(i));
                }else {
                    alreadyReads.add(datas.getReceive_list().get(i));
                }
            }
        }

        holder.setText(R.id.item_sn_content, datas.getContent())
        .setTimeText(R.id.item_sn_time, datas.getCreate_time())
        .setText(R.id.item_sn_nickname, datas.getUsername())
                .setImageByUrl(R.id.item_sn_head_iv, datas.getAvatar())
        .setItemImages(this, R.id.item_sn_onepic, R.id.item_sn_gridpic, images)
        .setText(R.id.item_sn_read,"总发" + datas.getReceive_list().size()+" 已读"+alreadyReads.size()+" 未读"+notReads.size());

        //进入已读未读界面
        holder.getView(R.id.item_sn_read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("yidu",alreadyReads);
                bundle.putSerializable("weidu",notReads);
                startActivity(ReadListSchoolAnnonceActivity.class,bundle);
            }
        });
    }

    /**
     * 对item 操作（发送）
     * @param holder
     * @param datas
     */
    private void setReceiveItem(ViewHolder holder, SchoolAnnouceReceive datas) {

        //获取图片字符串数组
        ArrayList<String> images = new ArrayList<>();
        for (int i=0;i<datas.getPic().size();i++){
            images.add(datas.getPic().get(i).getPhoto());
        }

        //判断已读和未读

        final ArrayList<SchoolAnnouceReceive.ReceivListBean> notReads = new ArrayList<>();
        final ArrayList<SchoolAnnouceReceive.ReceivListBean> alreadyReads = new ArrayList<>();
        if (datas.getReceiv_list().size()>0){
            for (int i=0;i<datas.getReceiv_list().size();i++){
                if (datas.getReceiv_list().get(i).getCreate_time()==null||datas.getReceiv_list().get(i).getCreate_time().equals("0")){
                    notReads.add(datas.getReceiv_list().get(i));
                }else {
                    alreadyReads.add(datas.getReceiv_list().get(i));
                }
            }
        }

        holder.setText(R.id.item_sn_content, datas.getNotice_info().get(0).getContent())
                .setTimeText(R.id.item_sn_time, datas.getNotice_info().get(0).getCreate_time())
                .setText(R.id.item_sn_nickname, datas.getNotice_info().get(0).getName())
                .setItemImages(this, R.id.item_sn_onepic, R.id.item_sn_gridpic, images)
                .setImageByUrl(R.id.item_sn_head_iv, datas.getNotice_info().get(0).getPhoto())
                .setText(R.id.item_sn_read, "总发" + datas.getReceiv_list().size() + " 已读" + alreadyReads.size() + " 未读" + notReads.size());

    }

    /**
     * 字符串转模型(发送)
     * @param result
     * @return
     */
    private List<SchoolAnnouncement> getBeanFromJsonSend(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<SchoolAnnouncement>>() {
        }.getType());
    }

    /**
     * 字符串转模型(接收)
     * @param result
     * @return
     */
    private List<SchoolAnnouceReceive> getBeanFromJsonReceive(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<SchoolAnnouceReceive>>() {
        }.getType());
    }
}
