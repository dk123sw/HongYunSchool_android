package cn.xiaocool.hongyunschool.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.activity.schoolnet.zhuanlan.FivePublicActivity;
import cn.xiaocool.hongyunschool.activity.infromation.ParentMessageActivity;
import cn.xiaocool.hongyunschool.activity.schoolnet.zhuanlan.SchoolWebDetailActivity;
import cn.xiaocool.hongyunschool.activity.schoolnet.zhuanlan.WebListActivity;
import cn.xiaocool.hongyunschool.adapter.WebMaxThreeAdapter;
import cn.xiaocool.hongyunschool.bean.Classevents;
import cn.xiaocool.hongyunschool.bean.WebListInfo;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseFragment;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.view.NoScrollListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener,ViewPagerEx.OnPageChangeListener  {


    @BindView(R.id.first_top_name)
    TextView firstTopName;
    @BindView(R.id.announcement)
    RelativeLayout announcement;
    @BindView(R.id.web_gonggao_list)
    NoScrollListView webGonggaoList;
    @BindView(R.id.web_news_trends)
    RelativeLayout webNewsTrends;
    @BindView(R.id.web_news_list)
    NoScrollListView webNewsList;
    @BindView(R.id.slider)
    SliderLayout slider;
    @BindView(R.id.school_news_srl)
    SwipeRefreshLayout schoolNewsSrl;
    private int tag = 0;

    private ArrayList<WebListInfo> announceList;
    private ArrayList<WebListInfo> newsList;
    private ArrayList<Classevents.PicBean> picBeans;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }


    @Override
    protected void initEvent() {
        super.initEvent();
        announceList = new ArrayList<>();
        newsList = new ArrayList<>();
        picBeans = new ArrayList<>();
        //设置标题
        firstTopName.setText("校网");
        //设置下拉刷新
        settingRefresh();

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
                initData();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        schoolNewsSrl.setRefreshing(false);
                    }
                }, 5000);
            }
        });

    }
    private void setLunBo() {
//        List<ImageCycleView.ImageInfo> list = new ArrayList<ImageCycleView.ImageInfo>();
//
//        //res图片资源
//        list.add(new ImageCycleView.ImageInfo(R.drawable.ll1, "", ""));
//        list.add(new ImageCycleView.ImageInfo(R.drawable.ll2, "", ""));
//        list.add(new ImageCycleView.ImageInfo(R.drawable.ll3, "", ""));
//        list.add(new ImageCycleView.ImageInfo(R.drawable.ll4, "", ""));
//        list.add(new ImageCycleView.ImageInfo(R.drawable.ll5,"",""));
//
//        showViewPager(list);

        HashMap<String,String> file_maps = new HashMap<String, String>();
        for (int i =0;i<picBeans.size();i++){
            file_maps.put(""+i, NetConstantUrl.IMAGE_URL + picBeans.get(i).getPicture_url());
        }

        showViewPager(file_maps);
    }

    //轮播图片
    private void showViewPager(HashMap<String,String> file_maps) {
        if(tag==0) {
            int i = 0;
            for (String name : file_maps.keySet()) {
                TextSliderView textSliderView = new TextSliderView(getActivity());
                // initialize a SliderLayout
                textSliderView
                        .description(picBeans.get(i).getDescription())
                        .image(file_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", name);
                i++;
                slider.addSlider(textSliderView);
            }
            slider.setPresetTransformer(SliderLayout.Transformer.Stack);
            slider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
            slider.setCustomAnimation(new DescriptionAnimation());
            slider.setDuration(4000);
            slider.addOnPageChangeListener(this);
            tag = 1;
        }
    }
//    private void showViewPager(List<ImageCycleView.ImageInfo> list) {
//        icvTopView.setAutoCycle(true); //自动播放
//        icvTopView.setCycleDelayed(2000);//设置自动轮播循环时间
//        icvTopView.loadData(list, new ImageCycleView.LoadImageCallBack() {
//            @Override
//            public ImageView loadAndDisplay(ImageCycleView.ImageInfo imageInfo) {
//
//                //本地图片
//                ImageView imageView = new ImageView(mActivity);
//                imageView.setImageResource(Integer.parseInt(imageInfo.image.toString()));
//                return imageView;
//
//
////				//使用SD卡图片
////				SmartImageView smartImageView=new SmartImageView(MainActivity.this);
////				smartImageView.setImageURI(Uri.fromFile((File)imageInfo.image));
////				return smartImageView;
//
////				//使用SmartImageView，既可以使用网络图片也可以使用本地资源
////				SmartImageView smartImageView=new SmartImageView(MainActivity.this);
////				smartImageView.setImageResource(Integer.parseInt(imageInfo.image.toString()));
////				return smartImageView;
//
//                //使用BitmapUtils,只能使用网络图片
////				BitmapUtils bitmapUtils = new BitmapUtils(MainActivity.this);
////				ImageView imageView = new ImageView(MainActivity.this);
////				bitmapUtils.display(imageView, imageInfo.image.toString());
////				return imageView;
//
//
//            }
//        });

//    }

    @Override
    public void initData() {
        String schoolid = (String) SPUtils.get(mActivity, LocalConstant.SCHOOL_ID, "1");
        String announceUrl = NetConstantUrl.GET_WEB_SCHOOL_NOTICE + schoolid;
        //获取校园公告
        VolleyUtil.VolleyGetRequest(mActivity, announceUrl, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(mActivity, result)) {
                    announceList.clear();
                    announceList.addAll(JsonParser(result));
                }
                setAnnounceAdapter();
            }

            @Override
            public void onError() {

            }
        });
        String newsUrl = NetConstantUrl.GET_WEB_SCHOOL_NEWS + schoolid;
        //获取新闻动态
        VolleyUtil.VolleyGetRequest(mActivity, newsUrl, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(mActivity, result)) {
                    newsList.clear();
                    newsList.addAll(JsonParser(result));
                }
                setNewsAdapter();
            }

            @Override
            public void onError() {

            }
        });

        //获取轮播图
        String slideUrl = NetConstantUrl.GET_SLIDER_URL + schoolid;
        VolleyUtil.VolleyGetRequest(mActivity, slideUrl, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(mActivity, result)) {
                    picBeans.clear();
                    picBeans.addAll(JsonParserPic(result));
                }
                setLunBo();
            }

            @Override
            public void onError() {

            }
        });

        schoolNewsSrl.setRefreshing(false);
    }

    /**
     * 校园公告适配器
     */
    private void setNewsAdapter() {
        webNewsList.setAdapter(new WebMaxThreeAdapter(newsList, mActivity));
        webNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, SchoolWebDetailActivity.class);
                Bundle bundle = new Bundle();
                newsList.get(position).setWhere(LocalConstant.WEB_NEWS);
                bundle.putSerializable(LocalConstant.WEB_FLAG, newsList.get(position));
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            }
        });
    }

    /**
     * 新闻动态适配器
     */
    private void setAnnounceAdapter() {
        webGonggaoList.setAdapter(new WebMaxThreeAdapter(announceList, mActivity));
        webGonggaoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, SchoolWebDetailActivity.class);
                Bundle bundle = new Bundle();
                announceList.get(position).setWhere(LocalConstant.WEB_NOTICE);
                bundle.putSerializable(LocalConstant.WEB_FLAG, announceList.get(position));
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
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

    @OnClick({R.id.web_rl_schoolintrouce, R.id.web_rl_teacher_style, R.id.web_rl_student_show, R.id.web_rl_five_public, R.id.web_rl_exciting_event, R.id.web_rl_parent_message,R.id.gonggao_more, R.id.news_more})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.web_rl_schoolintrouce://学校介绍
                intent = new Intent(mActivity, WebListActivity.class);
                intent.putExtra("title", "学校概况");
                intent.putExtra(LocalConstant.WEB_FLAG, LocalConstant.WEB_INTROUCE);
                startActivity(intent);
                break;
            case R.id.web_rl_teacher_style://教师风采
                intent = new Intent(mActivity, WebListActivity.class);
                intent.putExtra("title", "教师风采");
                intent.putExtra(LocalConstant.WEB_FLAG, LocalConstant.WEB_TEACHER);
                startActivity(intent);
                break;
            case R.id.web_rl_student_show://学生秀场
                intent = new Intent(mActivity, WebListActivity.class);
                intent.putExtra("title", "学生秀场");
                intent.putExtra(LocalConstant.WEB_FLAG, LocalConstant.WEB_STUDENT);
                startActivity(intent);
                break;
            case R.id.web_rl_five_public://五项公开专栏
                intent = new Intent(mActivity, FivePublicActivity.class);
                intent.putExtra("title", "五项公开专栏");
                startActivity(intent);
                break;
            case R.id.web_rl_exciting_event://
                intent = new Intent(mActivity, WebListActivity.class);
                intent.putExtra("title", "精彩活动");
                intent.putExtra(LocalConstant.WEB_FLAG, LocalConstant.WEB_ACTIVITY);
                startActivity(intent);
                break;
            case R.id.web_rl_parent_message:
//                ToastUtil.showShort(mActivity,"该功能暂未开放！");
                intent = new Intent(mActivity, ParentMessageActivity.class);
                intent.putExtra("title", "家长信箱");
                startActivity(intent);
                break;
            case R.id.gonggao_more:
                intent = new Intent(mActivity, WebListActivity.class);
                intent.putExtra("title", "校园公告");
                intent.putExtra(LocalConstant.WEB_FLAG, LocalConstant.WEB_NOTICE);
                startActivity(intent);
                break;
            case R.id.news_more:
                intent = new Intent(mActivity, WebListActivity.class);
                intent.putExtra("title", "新闻动态");
                intent.putExtra(LocalConstant.WEB_FLAG, LocalConstant.WEB_NEWS);
                startActivity(intent);
                break;
        }
    }

    public List<Classevents.PicBean> JsonParserPic(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<List<Classevents.PicBean>>() {
        }.getType());
    }

    public List<WebListInfo> JsonParser(String result) {
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

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
