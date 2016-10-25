package cn.xiaocool.hongyunschool.activity.schoolnet.zhuanlan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaocool.hongyunschool.R;
import cn.xiaocool.hongyunschool.app.MyApplication;
import cn.xiaocool.hongyunschool.bean.WebListInfo;
import cn.xiaocool.hongyunschool.net.LocalConstant;
import cn.xiaocool.hongyunschool.net.NetConstantUrl;
import cn.xiaocool.hongyunschool.utils.BaseActivity;
import cn.xiaocool.hongyunschool.utils.ToastUtil;
import cn.xiaocool.hongyunschool.view.SharePopupWindow;
/**
 * 校网->学校概况->学校简介
 * 学校信息教师信息 ，学生信息，活动信息
 * @ 显示的是WebView
 */
public class SchoolWebDetailActivity extends BaseActivity {

    @BindView(R.id.swd_webview)
    WebView swdWebview;
    private WebListInfo webListInfo;
    SharePopupWindow takePhotoPopWin;
    private int flag=0;
    String url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_web_detail);
        ButterKnife.bind(this);

        webListInfo = (WebListInfo) getIntent().getSerializableExtra(LocalConstant.WEB_FLAG);
        setTopName(webListInfo.getPost_title());
        setRightText("分享").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopFormBottom(v);
            }
        });
    }

    public void showPopFormBottom(View view) {
        takePhotoPopWin = new SharePopupWindow(this, onClickListener);
        takePhotoPopWin.showAtLocation(swdWebview, Gravity.BOTTOM, 0, 0);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.haoyou:
                    setting();
                    break;
                case R.id.dongtai:
                    history();
                    break;
            }
        }
    };
    @Override
    public void requsetData() {
        String schoolid = webListInfo.getId();
        switch (webListInfo.getWhere()) {
            case LocalConstant.WEB_INTROUCE:
                url = NetConstantUrl.WEB_LINK + "&a=school" + "&id=" + schoolid;
                break;
            case LocalConstant.WEB_TEACHER:
                url = NetConstantUrl.WEB_LINK + "&a=teacher" + "&id=" + schoolid;
                break;
            case LocalConstant.WEB_STUDENT:
                url = NetConstantUrl.WEB_LINK + "&a=baby" + "&id=" + schoolid;
                break;
            case LocalConstant.WEB_ACTIVITY:
                url = NetConstantUrl.WEB_LINK + "&a=Interest" + "&id=" + schoolid;
                break;
            case LocalConstant.WEB_NOTICE:
                url = NetConstantUrl.WEB_LINK + "&a=notice" + "&id=" + schoolid;
                break;
            case LocalConstant.WEB_NEWS:
                url = NetConstantUrl.WEB_LINK + "&a=news" + "&id=" + schoolid;
        }
        swdWebview.loadUrl(url);
        swdWebview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //返回的是true，说明是使用webview打开的网页。否则使用系统默认的浏览器
                return true;
            }
        });

    }

    /**
     * 分享到微信好友
     */
    private void setting() {
        ToastUtil.showShort(this, "分享到微信好友");
        flag = 0;
        shareWX();
        takePhotoPopWin.dismiss();

    }

    /**
     * 分享到微信朋友圈
     */
    private void history() {
        ToastUtil.showShort(this, "分享到微信朋友圈");
        flag = 1;
        shareWX();
        takePhotoPopWin.dismiss();
    }



    /**
     * 微信分享网页
     * */
    private void shareWX() {
        //创建一个WXWebPageObject对象，用于封装要发送的Url
        WXWebpageObject webpage =new WXWebpageObject();
        webpage.webpageUrl=url;
        WXMediaMessage msg =new WXMediaMessage(webpage);
        msg.title= webListInfo.getPost_title();
        msg.description=webListInfo.getPost_excerpt();
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "分享";
        req.message = msg;
        req.scene = flag==0? SendMessageToWX.Req.WXSceneSession: SendMessageToWX.Req.WXSceneTimeline;
        MyApplication application = MyApplication.getInstance();
        application.api.sendReq(req);
    }

}
