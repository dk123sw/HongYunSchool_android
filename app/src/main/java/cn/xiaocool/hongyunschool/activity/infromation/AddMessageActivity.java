package cn.xiaocool.hongyunschool.activity.infromation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import cn.xiaocool.hongyunschool.utils.PushImageUtil;
import cn.xiaocool.hongyunschool.utils.SPUtils;
import cn.xiaocool.hongyunschool.utils.StringJoint;
import cn.xiaocool.hongyunschool.utils.ToastUtil;
import cn.xiaocool.hongyunschool.view.NoScrollGridView;

public class AddMessageActivity extends BaseActivity {

    @BindView(R.id.addsn_tv_choose_class)
    TextView addsnTvChooseClass;
    @BindView(R.id.tv_select_count)
    TextView tvSelectCount;
    @BindView(R.id.addsn_content)
    EditText addsnContent;
    @BindView(R.id.addsn_pic_grid)
    NoScrollGridView addsnPicGrid;

    private ArrayList<PhotoInfo> mPhotoList;
    private ArrayList<PhotoWithPath> photoWithPaths;
    private LocalImgGridAdapter localImgGridAdapter;
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private static final int ADD_KEY = 101;
    private GalleryFinalUtil galleryFinalUtil;
    private String id;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_KEY:
                    if (msg.obj != null) {
                        if (JsonResult.JSONparser(AddMessageActivity.this, String.valueOf(msg.obj))) {
                            finish();
                        }

                    }
                    break;
            }
        }
    };
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school_news);
        ButterKnife.bind(this);
        context = this;
        mPhotoList = new ArrayList<>();
        photoWithPaths = new ArrayList<>();
        galleryFinalUtil = new GalleryFinalUtil(9);
        setTopName("公告消息");
        setRight();
        setAddImgGrid();

    }

    /**
     * 设置添加图片部分
     */
    private void setAddImgGrid() {
        localImgGridAdapter = new LocalImgGridAdapter(mPhotoList, AddMessageActivity.this);
        addsnPicGrid.setAdapter(localImgGridAdapter);
        addsnPicGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("position", String.valueOf(position));
                if (position == mPhotoList.size()) {
                    showActionSheet();
                }

            }
        });
    }

    /**
     * 弹出选择框
     */
    private void showActionSheet() {
        ActionSheet.createBuilder(AddMessageActivity.this, getSupportFragmentManager())
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
                                galleryFinalUtil.openAblum(AddMessageActivity.this, mPhotoList, REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
                                break;
                            case 1:
                                //获取拍照权限
                                if (galleryFinalUtil.openCamera(AddMessageActivity.this, mPhotoList, REQUEST_CODE_CAMERA, mOnHanlderResultCallback)) {
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
     * 设置发布按钮
     */
    private void setRight() {
        setRightImg(R.drawable.icon_load_ing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNews();
            }
        });
    }

    /**
     * 发布消息
     */
    private void sendNews() {
        //判断必填项
        if (!(addsnContent.getText().toString().length() > 0)) {
            ToastUtil.showShort(this, "发送内容不能为空!");
            return;
        }
        if (id == null) {
            ToastUtil.showShort(this, "请选择接收人!");
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
                new SendRequest(AddMessageActivity.this, handler).send_newsgroup("",
                        SPUtils.get(context, LocalConstant.SCHOOL_ID,"1").toString()
                        , addsnContent.getText().toString(), "",id, picname, ADD_KEY);
            }

            @Override
            public void error() {
                ToastUtil.showShort(AddMessageActivity.this, "图片上传失败!");
            }
        });
    }

    @Override
    public void requsetData() {

    }

    /**
     * 进入接收人列表事件
     */
    @OnClick(R.id.addsn_tv_choose_class)
    public void onClick() {

        Intent intent = new Intent(AddMessageActivity.this, ChooseReciverActivity.class);
        intent.putExtra("type", "");
        startActivityForResult(intent, 101);
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

                localImgGridAdapter = new LocalImgGridAdapter(mPhotoList, AddMessageActivity.this);
                addsnPicGrid.setAdapter(localImgGridAdapter);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(AddMessageActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 获取返回的接收人
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 101:
                    if (data != null) {
                        ArrayList<String> ids = data.getStringArrayListExtra("ids");
                        ArrayList<String> names = data.getStringArrayListExtra("names");
                        String haschoose = "";
                        for (int i = 0; i < names.size(); i++) {
                            if (i < 3) {
                                if (names.get(i) != null || names.get(i) != "null") {
                                    haschoose = haschoose + names.get(i) + "、";
                                }
                            } else if (i == 4) {
                                haschoose = haschoose.substring(0, haschoose.length() - 1);
                                haschoose = haschoose + "等...";
                            }

                        }

                        id = null;
                        for (int i = 0; i < ids.size(); i++) {
                            id = id + "," + ids.get(i);
                        }
                        id = id.substring(5, id.length());
                        tvSelectCount.setText("共选择" + ids.size() + "人");
                        addsnTvChooseClass.setText(haschoose);
                    }

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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
                    galleryFinalUtil.openCamera(AddMessageActivity.this, mPhotoList, REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                } else {
                    //用户授权拒绝之后，友情提示一下就可以了
                    ToastUtil.showShort(this, "已拒绝进入相机，如想开启请到设置中开启！");
                }

                break;

        }

    }
}
