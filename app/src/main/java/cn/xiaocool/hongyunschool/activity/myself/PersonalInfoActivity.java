package cn.xiaocool.hongyunschool.activity.myself;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.bean.PhotoWithPath;
import cn.xiaocool.hongyunschool.bean.UserInfo;
import cn.xiaocool.hongyunschool.callback.PushImage;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.net.SendRequest;
import cn.xiaocool.hongyunschool.net.VolleyUtil;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.GalleryFinalUtil;
import cn.xiaocool.hongyunschool.utils.GetImageUtil;
import cn.xiaocool.hongyunschool.utils.ImgLoadUtil;
import cn.xiaocool.hongyunschool.utils.JsonResult;
import cn.xiaocool.hongyunschool.utils.PopInputManager;
import cn.xiaocool.hongyunschool.utils.PushImageUtil;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.StringJoint;
import cn.xiaocool.hongyunschool.utils.ToastUtil;
import cn.xiaocool.hongyunschool.view.ChangeNamePopupWindow;
import de.hdodenhof.circleimageview.CircleImageView;
/**
 * 个人信息
 */

public class PersonalInfoActivity extends BaseActivity {
    @BindView(R.id.activity_personal_info_iv_avatar)
    CircleImageView activityPersonalInfoIvAvatar;
    @BindView(R.id.activity_personal_info_rl_avatar)
    RelativeLayout activityPersonalInfoRlAvatar;
    @BindView(R.id.activity_personal_info_tv_name)
    TextView activityPersonalInfoTvName;
    @BindView(R.id.activity_personal_info_rl_name)
    RelativeLayout activityPersonalInfoRlName;
    @BindView(R.id.activity_personal_info_tv_sex)
    TextView activityPersonalInfoTvSex;
    @BindView(R.id.activity_personal_info_rl_sex)
    RelativeLayout activityPersonalInfoRlSex;
    @BindView(R.id.activity_personal_info_tv_phone)
    TextView activityPersonalInfoTvPhone;
    @BindView(R.id.activity_personal_info_rl_phone)
    RelativeLayout activityPersonalInfoRlPhone;
    @BindView(R.id.activity_personal_info_rl_changepsw)
    RelativeLayout activityPersonalInfoRlChangepsw;
    @BindView(R.id.activity_personal_info_ll_main)
    LinearLayout activityPersonalInfoLlMain;
    private Context context;
    private ArrayList<PhotoInfo> mPhotoList;
    private ArrayList<PhotoWithPath> photoWithPaths;
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private GalleryFinalUtil galleryFinalUtil;
    private UserInfo userInfo;
    private ChangeNamePopupWindow commentPopupWindow;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x110:
                    if (msg.obj != null) {
                        if (JsonResult.JSONparser(context, String.valueOf(msg.obj))) {
                            requsetData();
                            ToastUtil.showShort(context, "修改性别成功！");
                        }
                    }
                    break;
                case 0x111:
                    if (msg.obj != null) {
                        if (JsonResult.JSONparser(context, String.valueOf(msg.obj))) {
                            requsetData();
                            ToastUtil.showShort(context, "修改姓名成功！");
                        }
                    }
                    break;
                case 0x112:
                    if (msg.obj != null) {
                        if (JsonResult.JSONparser(context, String.valueOf(msg.obj))) {
                            requsetData();
                            ToastUtil.showShort(context, "修改头像成功！");
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);
        context = this;
        setTopName("个人信息");
        mPhotoList = new ArrayList<>();
        photoWithPaths = new ArrayList<>();
        galleryFinalUtil = new GalleryFinalUtil(1);
    }

    @Override
    public void requsetData() {
        String url = NetConstantUrl.GET_USER_INFO + "&userid=" + SPUtils.get(context, LocalConstant.USER_ID, "");
        VolleyUtil.VolleyGetRequest(context, url, new VolleyUtil.VolleyJsonCallback() {
            @Override
            public void onSuccess(String result) {
                if (JsonResult.JSONparser(context, result)) {
                    showUserInfo(result);
                }
            }

            @Override
            public void onError() {

            }

        });
    }

    /**
     * 显示个人信息
     *
     * @param result
     */
    private void showUserInfo(String result) {
        String data = "";
        try {
            JSONObject json = new JSONObject(result);
            data = json.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userInfo = new Gson().fromJson(data, new TypeToken<UserInfo>() {
        }.getType());
        activityPersonalInfoTvName.setText(userInfo.getName());
        activityPersonalInfoTvPhone.setText(userInfo.getPhone());
        ImgLoadUtil.display(NetConstantUrl.IMAGE_URL + userInfo.getPhoto(), activityPersonalInfoIvAvatar);
        activityPersonalInfoTvSex.setText(userInfo.getSex().equals("1") ? "男" : "女");
    }

    @OnClick({R.id.activity_personal_info_rl_avatar, R.id.activity_personal_info_rl_name, R.id.activity_personal_info_rl_sex, R.id.activity_personal_info_rl_changepsw})
    public void onClick(View view) {
        switch (view.getId()) {
            //修改头像
            case R.id.activity_personal_info_rl_avatar:
                showActionSheet();
                break;
            //修改名字
            case R.id.activity_personal_info_rl_name:
                changeName();
                break;
            //修改性别
            case R.id.activity_personal_info_rl_sex:
                ShowSexDialog();
                break;
            //修改密码
            case R.id.activity_personal_info_rl_changepsw:
                startActivity(ChangePswActivity.class);
                break;
        }
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
                ImgLoadUtil.display("file:/" + mPhotoList.get(0).getPhotoPath(), activityPersonalInfoIvAvatar);
                //上传图片成功后发布
                new PushImageUtil().setPushIamge(context, photoWithPaths, new PushImage() {
                    @Override
                    public void success(boolean state) {
                        //获得图片字符串
                        ArrayList<String> picArray = new ArrayList<>();
                        for (PhotoWithPath photo : photoWithPaths) {
                            picArray.add(photo.getPicname());
                        }
                        String picname = StringJoint.arrayJointchar(picArray, ",");
                        new SendRequest(context, handler).updateHeadImg(userInfo.getId(), picname, 0x112);
                    }

                    @Override
                    public void error() {
                        ToastUtil.showShort(context, "图片上传失败!");
                    }
                });
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 授权权限
     *
     * @param permsRequestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        switch (permsRequestCode) {

            case 200:

                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    //授权成功之后，调用系统相机进行拍照操作等
                    galleryFinalUtil.openCamera(context, mPhotoList, REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                } else {
                    //用户授权拒绝之后，友情提示一下就可以了
                    ToastUtil.showShort(this, "已拒绝进入相机，如想开启请到设置中开启！");
                }

                break;

        }

    }

    /**
     * 修改性别弹出框
     */
    private void ShowSexDialog() {
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT).setNegativeButton("男", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!activityPersonalInfoTvSex.getText().toString().equals("男")) {
                    new SendRequest(context, handler).updateSex(userInfo.getId(), 1, 0x110);
                }
                activityPersonalInfoTvSex.setText("男");

            }
        }).setPositiveButton("女", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                if (!activityPersonalInfoTvSex.getText().toString().equals("女")) {
                    new SendRequest(context, handler).updateSex(userInfo.getId(), 0, 0x110);
                }
                activityPersonalInfoTvSex.setText("女");
            }
        }).show();
    }

    /**
     * 修改姓名
     */
    private void changeName() {
        commentPopupWindow = new ChangeNamePopupWindow(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_comment:
                        if (commentPopupWindow.ed_comment.getText().length() > 0) {
                            new SendRequest(context,handler).updateName(userInfo.getId(),commentPopupWindow.ed_comment.getText().toString(),0x111);
                            commentPopupWindow.dismiss();
                            commentPopupWindow.ed_comment.setText("");
                        } else {
                            Toast.makeText(context, "发送内容不能为空", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
        final EditText editText = commentPopupWindow.ed_comment;
        commentPopupWindow.showAtLocation(activityPersonalInfoLlMain, Gravity.BOTTOM, 0, 0);
        //弹出系统输入法
        PopInputManager.popInput(editText);
    }
}
