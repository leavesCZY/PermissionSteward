package com.czy.permission;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.czy.permission.Callback.SettingService;
import com.czy.permission.View.AlertDialog;

public class SettingDialog {

    private AlertDialog.Builder builder;

    private SettingService settingService;

    SettingDialog(@NonNull Context context, @NonNull SettingService settingService) {
        builder = AlertDialog.build(context)
                .setCancelable(false)
                .setTitle("权限申请失败")
                .setMessage("无法获得必要的权限，请到设置页面手动授权，否则功能将无法正常使用")
                .setPositiveButton("去设置", clickListener)
                .setNegativeButton("取消", clickListener);
        this.settingService = settingService;
    }

    private DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_NEGATIVE:
                    settingService.cancel();
                    break;
                case DialogInterface.BUTTON_POSITIVE:
                    settingService.execute();
                    break;
            }
        }
    };

    @NonNull
    public SettingDialog setTitle(@NonNull String title) {
        builder.setTitle(title);
        return this;
    }

    @NonNull
    public SettingDialog setTitle(@StringRes int title) {
        builder.setTitle(title);
        return this;
    }

    @NonNull
    public SettingDialog setMessage(@NonNull String message) {
        builder.setMessage(message);
        return this;
    }

    @NonNull
    public SettingDialog setMessage(@StringRes int message) {
        builder.setMessage(message);
        return this;
    }

    @NonNull
    public SettingDialog setNegativeButton(@NonNull String text) {
        builder.setNegativeButton(text, clickListener);
        return this;
    }

    @NonNull
    public SettingDialog setNegativeButton(@StringRes int text) {
        builder.setNegativeButton(text, clickListener);
        return this;
    }

    @NonNull
    public SettingDialog setPositiveButton(@NonNull String text) {
        builder.setPositiveButton(text, clickListener);
        return this;
    }

    @NonNull
    public SettingDialog setPositiveButton(@StringRes int text) {
        builder.setPositiveButton(text, clickListener);
        return this;
    }

    public void show() {
        builder.show();
    }

}
