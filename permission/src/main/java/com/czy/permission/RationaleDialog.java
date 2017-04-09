package com.czy.permission;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.czy.permission.Callback.Rationale;
import com.czy.permission.View.AlertDialog;

public class RationaleDialog {

    private AlertDialog.Builder builder;

    private Rationale rationale;

    RationaleDialog(@NonNull Context context, @NonNull Rationale rationale) {
        builder = AlertDialog.build(context)
                .setCancelable(false)
                .setMessage("应用曾被拒绝授权，请您同意授权，否则功能将无法正常使用")
                .setPositiveButton("确定", clickListener)
                .setNegativeButton("取消", clickListener);
        this.rationale = rationale;
    }

    private DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_NEGATIVE:
                    rationale.cancel();
                    break;
                case DialogInterface.BUTTON_POSITIVE:
                    rationale.resume();
                    break;
            }
        }
    };

    @NonNull
    public RationaleDialog setTitle(@NonNull String title) {
        builder.setTitle(title);
        return this;
    }

    @NonNull
    public RationaleDialog setTitle(@StringRes int title) {
        builder.setTitle(title);
        return this;
    }

    @NonNull
    public RationaleDialog setMessage(@NonNull String message) {
        builder.setMessage(message);
        return this;
    }

    @NonNull
    public RationaleDialog setMessage(@StringRes int message) {
        builder.setMessage(message);
        return this;
    }

    @NonNull
    public RationaleDialog setNegativeButton(@NonNull String text) {
        builder.setNegativeButton(text, clickListener);
        return this;
    }

    @NonNull
    public RationaleDialog setNegativeButton(@StringRes int text) {
        builder.setNegativeButton(text, clickListener);
        return this;
    }

    @NonNull
    public RationaleDialog setPositiveButton(@NonNull String text) {
        builder.setPositiveButton(text, clickListener);
        return this;
    }

    @NonNull
    public RationaleDialog setPositiveButton(@StringRes int text) {
        builder.setPositiveButton(text, clickListener);
        return this;
    }

    public void show() {
        builder.show();
    }

}
