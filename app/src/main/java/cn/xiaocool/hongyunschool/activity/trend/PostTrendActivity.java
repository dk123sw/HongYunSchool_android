package cn.xiaocool.hongyunschool.activity.trend;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.adapter.LocalImgGridAdapter;
import cn.xiaocool.hongyunschool.bean.PhotoWithPath;
import cn.xiaocool.hongyunschool.callback.PushImage;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.SendRequest;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.GalleryFinalUtil;
import cn.xiaocool.hongyunschool.utils.GetImageUtil;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.ProgressUtil;
import cn.xiaocool.hongyunschool.utils.PushImageUtil;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.StringJoint;
import cn.xiaocool.hongyunschool.utils.ToastUtil;
import cn.xiaocool.hongyunschool.view.NoScrollGridView;
/**
 * 动态->新增动态
 */
public class PostTrendActivity extends BaseActivity {

    @BindView(R.id.activity_post_trend_ed_content)
    EditText activityPostTrendEdContent;
    @BindView(R.id.activity_post_trend_gv_addpic)
    NoScrollGridView activityPostTrendGvAddpic;
    private Context context;
    private ArrayList<PhotoInfo> mPhotoList;
    private ArrayList<PhotoWithPath> photoWithPaths;
    private LocalImgGridAdapter localImgGridAdapter;
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private GalleryFinalUtil galleryFinalUtil;
    private String userid,classid;
    private int type;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x110:
                    if(msg.obj!=null){
                        if (JsonResult.JSONparser(context, String.valueOf(msg.obj))){
                            finish();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_trend);
        ButterKnife.bind(this);
        setTopName("新增动态");
        setRightText("发布").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTrend();
                ProgressUtil.showLoadingDialog(PostTrendActivity.this);
            }
        });
        context = this;
        mPhotoList = new ArrayList<>();
        photoWithPaths = new ArrayList<>();
        galleryFinalUtil = new GalleryFinalUtil(9);
        userid = SPUtils.get(context, LocalConstant.USER_ID,"").toString();
        classid = SPUtils.get(context,LocalConstant.USER_CLASSID,"").toString();
        checkIdentity();
        setGrigView();
    }

    /**
     * 判断身份
     * 1-----家长
     * 2-----校长
     * 3-----老师
     */
    private void checkIdentity() {
        if(SPUtils.get(context,LocalConstant.USER_TYPE,"").equals("0")){
            type = 1;
        }else if(SPUtils.get(context,LocalConstant.USER_IS_PRINSIPLE,"").equals("y")){
            type = 2;
        }else{
            type = 3;
        }
    }


    private void sendTrend() {
        if(activityPostTrendEdContent.getText().toString().trim().equals("")){
            ToastUtil.showShort(context,"发送内容不能为空！");
            return;
        }
        //上传图片成功后发布
        new PushImageUtil().setPushIamge(this, photoWithPaths, new PushImage() {
            @Override
            public void success(boolean state) {
                //获得图片字符串
                ArrayList<String> picArray = new ArrayList<>();
                for (PhotoWithPath photo : photoWithPaths) {
                    picArray.add(photo.getPicname());
                }
                String picname = StringJoint.arrayJointchar(picArray, ",");
                new SendRequest(context,handler).send_trend(userid,
                        SPUtils.get(context,LocalConstant.SCHOOL_ID,"1").toString(),
                        classid, activityPostTrendEdContent.getText().toString(),picname,0x110);
            }

            @Override
            public void error() {
                ToastUtil.showShort(context, "图片上传失败!");
            }
        });
    }

    @Override
    public void requsetData() {

    }

    /**
     * 设置添加图片
     */
    private void setGrigView() {
        localImgGridAdapter = new LocalImgGridAdapter(mPhotoList, context);
        activityPostTrendGvAddpic.setAdapter(localImgGridAdapter);
        activityPostTrendGvAddpic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mPhotoList.size()) {
                    showActionSheet();
                }
            }
        });
    }

    /**
     * 相册拍照弹出框
     */
    private void showActionSheet() {
        ActionSheet.createBuilder(context, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("打开相册", "拍照")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

                        switch (index) {
                            case 0:
                                galleryFinalUtil.openAblum(context, mPhotoList, REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
                                break;
                            case 1:
                                //获取拍照权限
                                if (galleryFinalUtil.openCamera(context, mPhotoList, REQUEST_CODE_CAMERA, mOnHanlderResultCallback)) {
                                    return;
                                } else {
                                    String[] perms = {"android.permission.CAMERA"};
                                    int permsRequestCode = 200;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(perms, permsRequestCode);
                                    }
                                }
                                break;

                            default:
                                break;
                        }
                    }
                })
                .show();
    }



    /**
     * 选择图片后 返回的图片数据
     */

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                photoWithPaths.clear();
                mPhotoList.clear();
                mPhotoList.addAll(resultList);
                photoWithPaths.addAll(GetImageUtil.getImgWithPaths(resultList));

                localImgGridAdapter = new LocalImgGridAdapter(mPhotoList, context);
                activityPostTrendGvAddpic.setAdapter(localImgGridAdapter);
            }
        }
        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 授权权限
     * @param permsRequestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted){
                    //授权成功之后，调用系统相机进行拍照操作等
                    galleryFinalUtil.openCamera(context, mPhotoList, REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                }else{
                    //用户授权拒绝之后，友情提示一下就可以了
                    ToastUtil.showShort(this, "已拒绝进入相机，如想开启请到设置中开启！");
                }

                break;

        }

    }
}
