package cn.xiaocool.hongyunschool.utils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.app.MyApplication;


public abstract class BaseActivity extends FragmentActivity {

    /** 是否沉浸状态栏 **/
    private boolean isSetStatusBar = true;

    private View view;
    private TextView top_name,top_tv_right;
    private View top_back;
    private RelativeLayout top_right;
    private ImageView back,top_img_right;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.setContentView(R.layout.activity_base);
        //添加Activity集合
        MyApplication.getInstance().addActivity(this);
        try {
            if (isSetStatusBar) {
                steepStatusBar();
            }
        }catch (Exception e){

        }
//      @DK 测试活动的方法
        Log.e("BaseActivity" , getClass().getSimpleName());

        view = findViewById(R.id.top_bar);
        top_name = (TextView) findViewById(R.id.top_name);
        top_back = findViewById(R.id.top_back);
        back = (ImageView) findViewById(R.id.back);
        top_right = (RelativeLayout) findViewById(R.id.top_right);
        top_img_right = (ImageView) findViewById(R.id.top_img_right);
        top_tv_right = (TextView) findViewById(R.id.top_tv_right);
        top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        requsetData();
    }

    /**请求数据*/
    public abstract void requsetData();



    public void startActivity(Class<?> clz){
        startActivity(clz,null);
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * [沉浸状态栏]
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
//            透明导航栏导致RadioButton与手机自带导航栏重叠
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            setStatusHeight();
        }
    }

    /**
     * 设置沉浸式状态栏高度
     */
    private void setStatusHeight() {
        ViewGroup.LayoutParams layoutParams =  findViewById(R.id.status_view).getLayoutParams();
        layoutParams.width = ScreenUtils.getScreenWidth(this);
        layoutParams.height = ScreenUtils.getStatusHeight(this);
        findViewById(R.id.status_view).setLayoutParams(layoutParams);
    }

    /**
     * [防止快速点击]
     *
     * @return
     */
    private boolean fastClick() {
        long lastClick = 0;
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }

    /**
     * 设置bar
     */
    public void setTopName(String string) {
        top_name.setText(string);
    }

    /**
     * 得到bar
     */
    public View getTopView() {
        return view;
    }

    /**
     * 隐藏
     */
    public void hideTopView() {
        view.setVisibility(View.GONE);
    }

    /**
     * 得到TextView
     */
    public TextView getTopNameView() {
        return top_name;
    }

    /**
     * 显示right文字按钮
     * @param text
     */
    public View setRightText(String text){
        top_tv_right.setVisibility(View.VISIBLE);
        top_tv_right.setText(text);
        top_img_right.setVisibility(View.GONE);
        return top_right;
    }

    /**
     * 显示right图片按钮
     * @param drawable
     * @return
     */
    public View setRightImg(int drawable){
        top_img_right.setVisibility(View.VISIBLE);
        top_img_right.setImageResource(drawable);
        top_tv_right.setVisibility(View.GONE);
        return top_right;
    }

    /**
     * 隐藏左边按钮
     */
    public void hideLeft(){
        top_back.setVisibility(View.GONE);
    }
    /**
     * 设置布局
     */
    public void setContentView(int layoutResID) {
        RelativeLayout container = (RelativeLayout) findViewById(R.id.base_container);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(layoutResID, null);
        container.addView(v);
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        v.setLayoutParams(layoutParams);
    }
}
