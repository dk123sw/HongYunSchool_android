package cn.xiaocool.hongyunschool.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.bean.CheckVersionModel;
import cn.xiaocool.hongyunschool.fragment.FirstFragment;
import cn.xiaocool.hongyunschool.fragment.FourFragment;
import cn.xiaocool.hongyunschool.fragment.SecondFragment;
import cn.xiaocool.hongyunschool.fragment.SecondParentFragment;
import cn.xiaocool.hongyunschool.fragment.ThirdFragment;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.view.NiceDialog;


public class MainActivity extends BaseActivity {


    @BindView(R.id.fragment_container)
    RelativeLayout fragmentContainer;
    @BindView(R.id.main_tab_home)
    RadioButton mainTabHome;
    @BindView(R.id.main_tab_sort)
    RadioButton mainTabSort;
    @BindView(R.id.main_tab_quick)
    RadioButton mainTabQuick;
    @BindView(R.id.main_tab_mine)
    RadioButton mainTabMine;
    private int index, currentTabIndex;
    private FirstFragment firstFragment;
    private SecondFragment secondFragment;
    private ThirdFragment thirdFragment;
    private FourFragment fourFragment;
    private SecondParentFragment secondParentFragment;
    private Fragment[] fragments;
    private Context context;

//  弹出的对话框
    private NiceDialog mDialog = null;
    private CheckVersionModel versionModel;
    private static final int REQUEST_WRITE_STORAGE = 111;
    //apk下载链接
    private static final String APK_DOWNLOAD_URL = "http://hyx.xiaocool.net/hyx.apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        hideTopView();
        context = this;
        setVersionDialog();
        init();
        Log.e("TAG", SPUtils.get(context, LocalConstant.USER_IS_PRINSIPLE,"").toString()
                + SPUtils.get(context, LocalConstant.USER_IS_CLASSLEADER,"").toString()
                + SPUtils.get(context, LocalConstant.USER_CLASSID,"").toString());
        /*TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
       *//* String DEVICE_ID = tm.getDeviceId();
        Log.e("TAG",DEVICE_ID);*/
    }

    private void setVersionDialog() {
        mDialog = new NiceDialog(MainActivity.this);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.width = width-300;
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mDialog.getWindow().setAttributes(layoutParams);
    }
    @Override
    public void requsetData() {
        chechVersion();
        String url = NetConstantUrl.GET_PARENT_BYTEACHERID + "&teacherid=" + SPUtils.get(context, LocalConstant.USER_ID, "")+"&schoolid="+SPUtils.get(context, LocalConstant.SCHOOL_ID, "");;
        Log.e("child",url);
        VolleyUtil.VolleyGetRequest(this, url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(MainActivity.this, result)) {
                  SPUtils.put(MainActivity.this,LocalConstant.IS_TEACH,"1");
                }else {
                    SPUtils.put(MainActivity.this,LocalConstant.IS_TEACH,"2");
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    private void init() {
        firstFragment = new FirstFragment();
        secondFragment = new SecondFragment();
        thirdFragment = new ThirdFragment();
        fourFragment = new FourFragment();
        secondParentFragment = new SecondParentFragment();
        //根据是否登录切换不同的消息界面
        if(SPUtils.get(context,LocalConstant.USER_TYPE,"1").equals("0")){
            fragments = new Fragment[]{firstFragment,secondParentFragment,thirdFragment,fourFragment};
        }else{
            fragments = new Fragment[]{firstFragment,secondFragment,thirdFragment,fourFragment};
        }
        getFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
    }

    @OnClick({R.id.main_tab_home, R.id.main_tab_sort, R.id.main_tab_quick, R.id.main_tab_mine})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_tab_home:
                index = 0;
                break;
            case R.id.main_tab_sort:
                index = 1;
                break;
            case R.id.main_tab_quick:
                index = 2;
                break;
            case R.id.main_tab_mine:
                index = 3;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                transaction.add(R.id.fragment_container, fragments[index]);
            }
            transaction.show(fragments[index]);
            transaction.commit();

        }
        currentTabIndex = index;
    }



    /**
     * 检查版本更新
     */
    private void chechVersion() {

        String versionId = getResources().getString(R.string.versionid).toString();
        String url =  NetConstantUrl.CHECK_VERSION + versionId;
        VolleyUtil.VolleyGetRequest(getBaseContext(), url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(context, result)) {
                    versionModel = getBeanFromJson(result);
                    showDialogByYorNo(versionModel.getVersionid());
                    mDialog.show();
                } else {
//                    mDialog.setTitle("暂无最新版本");
//                    mDialog.setContent("感谢您的使用！");
//                    mDialog.setOKButton("确定", new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            mDialog.dismiss();
//                        }
//                    });

                }
            }

            @Override
            public void onError() {

            }
        });

    }


    //展示dialog
    private void showDialogByYorNo(String versionid) {

        if (Integer.valueOf(versionid)>Integer.valueOf(getResources().getString(R.string.versionid).toString())){
            mDialog.setTitle("发现新版本");
            mDialog.setContent(versionModel.getDescription());
            mDialog.setOKButton("立即更新", new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //请求存储权限
//                    boolean hasPermission = (ContextCompat.checkSelfPermission(getBaseContext(),
//                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
//                    if (!hasPermission) {
//                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
//                        ActivityCompat.shouldShowRequestPermissionRationale((Activity) getBaseContext(),
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                    } else {
                        //下载
                        startDownload();
//                    }

                }
            });
            mDialog.setCancelButton("退出", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            mDialog.show();
        }else {
            mDialog.setTitle("已经是最新版本");
            mDialog.setContent("感谢您的使用！");
            mDialog.setOKButton("确定", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            mDialog.show();
        }
    }
//    Android6.0新增加的运行时权限检测，这个是对存储空间的运行判断
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //获取到存储权限,进行下载
                    startDownload();
                } else {
                    Toast.makeText(context, "不授予存储权限将无法进行下载!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 启动下载
     */
    private void startDownload() {
        Uri uri = Uri.parse(APK_DOWNLOAD_URL);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
//        Intent it = new Intent(getBaseContext(), UpdateService.class);
//        //下载地址
//        Log.e("apkUrl", versionModel.getUrl());
//        it.putExtra("apkUrl", APK_DOWNLOAD_URL);
//        startService(it);

//        关闭此对话框，将其从屏幕中删除。 这个方法可以从任何线程安全地调用。
        mDialog.dismiss();
    }

    /**
     * 字符串转模型
     * @param result
     * @return
     */
    private CheckVersionModel getBeanFromJson(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(data, new TypeToken<CheckVersionModel>() {
        }.getType());
    }

}
