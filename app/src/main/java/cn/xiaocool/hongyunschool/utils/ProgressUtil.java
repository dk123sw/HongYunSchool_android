package cn.xiaocool.hongyunschool.utils;

import android.app.Activity;

import cn.xiaocool.hongyunschool.R;

/**
 * Created by Administrator on 2016/8/31 0031.
 */
public class ProgressUtil {
    private static AppLoadingDialog loadingDialog;

    private static AppLoadingDialog getLoadingDialog(Activity activity) {
        if (loadingDialog != null) {
            loadingDialog = null;
        }
        loadingDialog = new AppLoadingDialog(activity, R.style.alert_dialog);
        return loadingDialog;

    }

    public static void showLoadingDialog(Activity activity) {
        AppLoadingDialog dialog = getLoadingDialog(activity);
        dialog.show();
    }

    public static void dissmisLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
