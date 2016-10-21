package cn.xiaocool.hongyunschool.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.adapter.ViewPagerAdapter;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.utils.SPUtils;
/**
 * 第一次安装所用到的Activity
 */
public class SplashActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener {
    private   int UID ;
    private Context mContext;
    private TextView tv_login;
    // 引导图片资源
    private static final int[] pics = { R.drawable.splash_1, R.drawable.splash_2,R.drawable.splash_3};
    // 定义ViewPager对象
    private ViewPager viewPager;
    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;
    // 定义一个ArrayList来存放View
    private ArrayList<View> views;
    private LinearLayout ll_point;
    // 底部小点的图片
    private ImageView[] points;
    // 记录当前选中位置
    private int currentIndex;
    Boolean isFirstIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);
        mContext = this;
        initView();
        SharedPreferences pref = mContext.getSharedPreferences("myActivityName", 0);
        //取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = pref.getBoolean("isFirstIn", true);
        //AppInfo appInfo = new AppInfo();
        if (isFirstIn) {
            pref = mContext.getSharedPreferences("myActivityName", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();
            initData();
        }else{
            // 动画
            AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
            animation.setDuration(1500);
            view.startAnimation(animation);
            LoginToWxt();
        }
    }
    /**
     * 初始化组件
     */
    private void initView() {
        // 实例化ArrayList对象
        views = new ArrayList<View>();
        // 实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager_flash);
        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);
        viewPager.setVisibility(View.GONE);
        ll_point = (LinearLayout) findViewById(R.id.ll_flash);
        ll_point.setVisibility(View.GONE);
        tv_login = (TextView) findViewById(R.id.tv_flash);
        tv_login.setVisibility(View.GONE);
        tv_login.setOnClickListener(this);
    }
    /**
     * 初始化数据
     */
    @SuppressWarnings("deprecation")
    private void initData() {
// 定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        // 初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            // 防止图片不能填满屏幕
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            // 加载图片资源
            iv.setImageResource(pics[i]);
            views.add(iv);
        }
        viewPager.setVisibility(View.VISIBLE);
        // 设置数据
        viewPager.setAdapter(vpAdapter);
        // 设置监听
        viewPager.setOnPageChangeListener(this);
        // 初始化底部小点
        initPoint();
    }

    /**
     * 初始化底部小点
     */
    private void initPoint() {
        points = new ImageView[pics.length];
        ll_point.setVisibility(View.VISIBLE);
        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) ll_point.getChildAt(i);
            // 默认都设为灰色
            points[i].setEnabled(true);
            // 给每个小点设置监听
            points[i].setOnClickListener(this);
            // 设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        // 设置当面默认的位置
        currentIndex = 0;
        // 设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_flash:
                try{
                    PackageManager pm = mContext.getPackageManager();
                    PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
                    String currentVersion = "" + pi.versionCode;
                    if (SPUtils.contains(mContext, LocalConstant.USER_ID)) {// 已登录
                        LoginToWxt();
                    } else {// 未登录
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }

                }catch (Exception e){

                }
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
                break;
            default:
                int position = (Integer) v.getTag();
                setCurView(position);
                setCurDot(position);
                break;

        }
    }
    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
        if (positon == pics.length - 1) {
            ll_point.setVisibility(View.GONE);
            tv_login.setVisibility(View.VISIBLE);
        } else {
            tv_login.setVisibility(View.GONE);
            ll_point.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
//        StatService.onPause(this);
    }
    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    /**
     * 新的页面被选中时调用
     */
    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
        setCurDot(arg0);

    }
    private void LoginToWxt() {
            if (SPUtils.contains(mContext, LocalConstant.USER_ID)) {// 曾经登录过
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }else{
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
