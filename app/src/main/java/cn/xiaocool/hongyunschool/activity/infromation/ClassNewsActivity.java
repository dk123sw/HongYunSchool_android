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
import cn.xiaocool.hongyunschool.bean.ClassNewsAll;
import cn.xiaocool.hongyunschool.bean.ClassNewsReceive;
import cn.xiaocool.hongyunschool.bean.ClassNewsSend;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.CommonAdapter;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.ToastUtil;
import cn.xiaocool.hongyunschool.utils.ViewHolder;
import cn.xiaocool.hongyunschool.view.RefreshLayout;

/**
 *  班级消息
 */

public class ClassNewsActivity extends BaseActivity {


    @BindView(R.id.school_news_lv)
    ListView schoolNewsLv;
    @BindView(R.id.school_news_srl)
    RefreshLayout schoolNewsSrl;

    private CommonAdapter adapter;
    private List<ClassNewsSend> classNewsSends;
    private List<ClassNewsAll> classNewsAlls;
    private List<ClassNewsReceive> classNewsReceives;
    private Context context;
    private int type;
    private int beginid = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_news);
        ButterKnife.bind(this);
        context = this;
        classNewsSends = new ArrayList<>();
        classNewsAlls = new ArrayList<>();
        classNewsReceives = new ArrayList<>();
        setTopName("班级消息");
        //判断身份
        checkIdentity();
        //班主任可以发班级消息
        if(type == 2){
            setRightImg(R.drawable.ic_fabu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( SPUtils.get(context, LocalConstant.USER_IS_PRINSIPLE, "").equals("y")){
                        startActivity(AddClassNewsActivity.class);
                        return;
                    }
                    String isTeach = String.valueOf(SPUtils.get(context, LocalConstant.IS_TEACH, "0"));
                    if (isTeach.equals("1")){
                        startActivity(AddClassNewsActivity.class);
                    }else if (isTeach.equals("2")){
                        ToastUtil.showShort(context,"您没有任教的班级！");
                    }else {
                        ToastUtil.showShort(context,"网络很忙，请稍后在试！");
                    }


                }
            });
        }
        settingRefresh();
    }

    /**
     * 判断身份
     * 1-----家长
     * 2-----校长
     * 3-----班主任
     * 4-----校长+班主任
     */
    private void checkIdentity() {
        if(SPUtils.get(context, LocalConstant.USER_TYPE,"").equals("0")){
            type = 1;
        }else {
            /*if(SPUtils.get(context,LocalConstant.USER_IS_PRINSIPLE,"").equals("y")&&SPUtils.get(context, LocalConstant.USER_IS_CLASSLEADER,"").equals("y"))
                type = 4;
            if(SPUtils.get(context,LocalConstant.USER_IS_PRINSIPLE,"").equals("y")&&SPUtils.get(context, LocalConstant.USER_IS_CLASSLEADER,"").equals("n"))
                type = 2;
            if(SPUtils.get(context,LocalConstant.USER_IS_PRINSIPLE,"").equals("n")&&SPUtils.get(context, LocalConstant.USER_IS_CLASSLEADER,"").equals("y"))
                type = 3;*/
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
                        if (type == 2||type == 4) {
                            beginid = classNewsAlls.size();
                        } else if (type == 1) {
                            beginid = classNewsReceives.size();
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
        if(type == 2){
            url = NetConstantUrl.GET_CLASS_NEWS_ALL + SPUtils.get(context,LocalConstant.SCHOOL_ID,"1")+"&userid=" + SPUtils.get(context,LocalConstant.USER_ID,"")+"&beginid=" + beginid;
//            url = NetConstantUrl.GET_CLASS_NEWS_SEND + "&userid=" + SPUtils.get(context,LocalConstant.USER_ID,"").toString();
        }else if(type == 1){
            url = NetConstantUrl.GET_CLASS_NEWS_RECEIVE + "&receiverid=" + SPUtils.get(context,LocalConstant.USER_BABYID,"").toString()+"&userid="+SPUtils.get(context,LocalConstant.USER_ID,"")+"&beginid=" + beginid;
        }/*else if(type == 2||type == 4){
            url = NetConstantUrl.GET_CLASS_NEWS_ALL + SPUtils.get(context,LocalConstant.SCHOOL_ID,"1");
        }*/
        Log.e("getclassnew",url);
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
        //家长
        if (type == 1){
            classNewsReceives.clear();
            classNewsReceives.addAll(getBeanFromJsonReceive(result));
            Collections.sort(classNewsReceives, new Comparator<ClassNewsReceive>() {
                @Override
                public int compare(ClassNewsReceive lhs, ClassNewsReceive rhs) {
                    return (int) (Long.parseLong(rhs.getHomework_info().get(0).getCreate_time()) - Long.parseLong(lhs.getHomework_info().get(0).getCreate_time()));
                }
            });
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new CommonAdapter<ClassNewsReceive>(getBaseContext(), classNewsReceives, R.layout.school_announcement_item) {
                        @Override
                        public void convert(ViewHolder holder, ClassNewsReceive datas) {
                            setItemReceive(holder, datas);
                        }
                };
                schoolNewsLv.setAdapter(adapter);
            }
        }/*else if(type == 2){
            classNewsSends.clear();
            classNewsSends.addAll(getBeanFromJsonSend(result));
            Collections.sort(classNewsSends, new Comparator<ClassNewsSend>() {
                @Override
                public int compare(ClassNewsSend lhs, ClassNewsSend rhs) {
                    return (int) (Long.parseLong(rhs.getCreate_time())-Long.parseLong(lhs.getCreate_time()));
                }
            });
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new CommonAdapter<ClassNewsSend>(getBaseContext(), classNewsSends, R.layout.school_announcement_item) {
                    @Override
                    public void convert(ViewHolder holder, ClassNewsSend datas) {
                        setItemSend(holder, datas);
                    }
                };
                schoolNewsLv.setAdapter(adapter);
            }
        }*/else if(type == 2||type == 4) {
            classNewsAlls.clear();
            classNewsAlls.addAll(getBeanFromJsonAll(result));
            Collections.sort(classNewsAlls, new Comparator<ClassNewsAll>() {
                @Override
                public int compare(ClassNewsAll lhs, ClassNewsAll rhs) {
                    return (int) (Long.parseLong(rhs.getCreate_time())-Long.parseLong(lhs.getCreate_time()));
                }
            });
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new CommonAdapter<ClassNewsAll>(getBaseContext(), classNewsAlls, R.layout.school_announcement_item) {
                    @Override
                    public void convert(ViewHolder holder, ClassNewsAll datas) {
                        setItemAll(holder, datas);
                    }
                };
                schoolNewsLv.setAdapter(adapter);
            }
        }
    }

    /**
     * 对item 操作(家长)
     * @param holder
     * @param datas
     */
    private void setItemReceive(ViewHolder holder, ClassNewsReceive datas) {

        //获取图片字符串数组
        ArrayList<String> images = new ArrayList<>();
        for (int i=0;i<datas.getPicture().size();i++){
            images.add(datas.getPicture().get(i).getPicture_url());
        }

        //判断已读和未读

        final ArrayList<ClassNewsReceive.ReceiveListBean> notReads = new ArrayList<>();
        final ArrayList<ClassNewsReceive.ReceiveListBean> alreadyReads = new ArrayList<>();
        if (datas.getReceive_list().size()>0){
            for (int i=0;i<datas.getReceive_list().size();i++){
                if (datas.getReceive_list().get(i).getRead_time()==null||datas.getReceive_list().get(i).getRead_time().equals("null")){
                    notReads.add(datas.getReceive_list().get(i));
                }else {
                    alreadyReads.add(datas.getReceive_list().get(i));
                }
            }
        }

        holder.setText(R.id.item_sn_content, datas.getHomework_info().get(0).getContent())
        .setTimeText(R.id.item_sn_time,datas.getHomework_info().get(0).getCreate_time())
        .setText(R.id.item_sn_nickname,datas.getHomework_info().get(0).getName())
                .setItemImages(this, R.id.item_sn_onepic, R.id.item_sn_gridpic, images)
                .setImageByUrl(R.id.item_sn_head_iv,datas.getHomework_info().get(0).getPhoto())
                .setText(R.id.item_sn_read, "总发" + datas.getReceive_list().size() + " 已读" + alreadyReads.size() + " 未读" + notReads.size());

    }

    /**
     * 对item 操作(班主任)
     * @param holder
     * @param datas
     */
    private void setItemSend(ViewHolder holder, ClassNewsSend datas) {

        //获取图片字符串数组
        ArrayList<String> images = new ArrayList<>();
        for (int i=0;i<datas.getPic().size();i++){
            images.add(datas.getPic().get(i).getPicture_url());
        }

        //判断已读和未读

        final ArrayList<ClassNewsSend.ReceiverlistBean> notReads = new ArrayList<>();
        final ArrayList<ClassNewsSend.ReceiverlistBean> alreadyReads = new ArrayList<>();
        if (datas.getReceiverlist().size()>0){
            for (int i=0;i<datas.getReceiverlist().size();i++){
                if (datas.getReceiverlist().get(i).getRead_time()==null||datas.getReceiverlist().get(i).getRead_time().equals("null")){
                    notReads.add(datas.getReceiverlist().get(i));
                }else {
                    alreadyReads.add(datas.getReceiverlist().get(i));
                }
            }
        }

        holder.setText(R.id.item_sn_content, datas.getContent())
                .setTimeText(R.id.item_sn_time, datas.getCreate_time())
                .setText(R.id.item_sn_nickname, datas.getTeacher_info().getName())
                .setItemImages(this, R.id.item_sn_onepic, R.id.item_sn_gridpic, images)
                .setImageByUrl(R.id.item_sn_head_iv, datas.getTeacher_info().getPhoto())
                .setText(R.id.item_sn_read, "总发" + datas.getReceiverlist().size() + " 已读" + alreadyReads.size() + " 未读" + notReads.size());

        //进入已读未读界面
        holder.getView(R.id.item_sn_read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("yidu",alreadyReads);
                bundle.putSerializable("weidu",notReads);
                startActivity(ReadListClassNewTeacherActivity.class, bundle);
            }
        });
    }

    /**
     * 对item 操作(校长)
     * @param holder
     * @param datas
     */
    private void setItemAll(ViewHolder holder, ClassNewsAll datas) {

        //获取图片字符串数组
        ArrayList<String> images = new ArrayList<>();
        for (int i=0;i<datas.getPic().size();i++){
            images.add(datas.getPic().get(i).getPicture_url());
        }

        //判断已读和未读

        final ArrayList<ClassNewsAll.ReceiverlistBean> notReads = new ArrayList<>();
        final ArrayList<ClassNewsAll.ReceiverlistBean> alreadyReads = new ArrayList<>();
        if (datas.getReceiverlist().size()>0){
            for (int i=0;i<datas.getReceiverlist().size();i++){
                if (datas.getReceiverlist().get(i).getRead_time()==null||datas.getReceiverlist().get(i).getRead_time().equals("null")){
                    notReads.add(datas.getReceiverlist().get(i));
                }else {
                    alreadyReads.add(datas.getReceiverlist().get(i));
                }
            }
        }

        holder.setText(R.id.item_sn_content, datas.getContent())
                .setTimeText(R.id.item_sn_time, datas.getCreate_time())
                .setText(R.id.item_sn_nickname, datas.getName())
                .setItemImages(this, R.id.item_sn_onepic, R.id.item_sn_gridpic, images)
                .setImageByUrl(R.id.item_sn_head_iv, datas.getPhoto())
                .setText(R.id.item_sn_read, "总发" + datas.getReceiverlist().size() + " 已读" + alreadyReads.size() + " 未读" + notReads.size());

        //进入已读未读界面
        holder.getView(R.id.item_sn_read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("yidu",alreadyReads);
                bundle.putSerializable("weidu",notReads);
                startActivity(ReadListClassNewLeaderActivity.class, bundle);
            }
        });
    }

    /**
     * 字符串转模型(家长)
     * @param result
     * @return
     */
    private List<ClassNewsReceive> getBeanFromJsonReceive(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<ClassNewsReceive>>() {
        }.getType());
    }

    /**
     * 字符串转模型(班主任)
     * @param result
     * @return
     */
    private List<ClassNewsSend> getBeanFromJsonSend(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<ClassNewsSend>>() {
        }.getType());
    }

    /**
     * 字符串转模型(校长)
     * @param result
     * @return
     */
    private List<ClassNewsAll> getBeanFromJsonAll(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<ClassNewsAll>>() {
        }.getType());
    }
}
