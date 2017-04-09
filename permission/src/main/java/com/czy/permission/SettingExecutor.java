package com.czy.permission;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.czy.permission.Callback.SettingService;

class SettingExecutor implements SettingService {

    private Object object;

    private int requestCode;

    SettingExecutor(@NonNull Object object, int requestCode) {
        this.object = object;
        this.requestCode = requestCode;
    }

    @Override
    public void execute() {
        // 跳转到当前应用的详细设置界面
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", PermissionUtils.getContext(object).getPackageName(), null));
        startForResult(object, intent, requestCode);
    }

    @Override
    public void cancel() {

    }

    private static void startForResult(Object object, Intent intent, int requestCode) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof android.support.v4.app.Fragment) {
            ((android.support.v4.app.Fragment) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).startActivityForResult(intent, requestCode);
        }
    }

}