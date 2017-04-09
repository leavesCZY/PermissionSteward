package com.czy.test.Activity.Listener;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.czy.permission.Callback.PermissionListener;
import com.czy.permission.PermissionSteward;
import com.czy.test.R;

import java.util.List;

/**
 * 在Activity中通过回调来申请权限
 */
public class ActivityPermissionListenerActivity extends AppCompatActivity implements View.OnClickListener, PermissionListener {

    //申请单个权限（日历权限）
    private static final int REQUEST_CODE_PERMISSION_CALENDAR = 100;

    //申请多个权限（短息和联系人权限）
    private static final int REQUEST_CODE_PERMISSION_SMS_AND_CONTACTS = 200;

    private static final int REQUEST_CODE_SETTING = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_permission_listener);
        findViewById(R.id.btn_request_single).setOnClickListener(this);
        findViewById(R.id.btn_request_multi).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //一次申请单个权限
            case R.id.btn_request_single: {
                PermissionSteward.requestPermission(this, REQUEST_CODE_PERMISSION_CALENDAR, Manifest.permission.READ_CALENDAR);
                break;
            }
            //一次申请多个权限
            case R.id.btn_request_multi: {
                PermissionSteward.requestPermission(this, REQUEST_CODE_PERMISSION_SMS_AND_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS);
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionSteward.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING: {
                Toast.makeText(this, "用户从设置界面回来了", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    public void onSucceed(int requestCode, List<String> grantPermissionList) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CALENDAR: {
                Toast.makeText(this, "获取到日历权限", Toast.LENGTH_SHORT).show();
                break;
            }
            case REQUEST_CODE_PERMISSION_SMS_AND_CONTACTS: {
                Toast.makeText(this, "获取到短信、联系人权限", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    public void onFailed(int requestCode, List<String> deniedPermissionList) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CALENDAR: {
                Toast.makeText(this, "获取日历权限失败", Toast.LENGTH_SHORT).show();
                break;
            }
            case REQUEST_CODE_PERMISSION_SMS_AND_CONTACTS: {
                if (deniedPermissionList.size() == 2) {
                    Toast.makeText(this, "获取短信、联系人权限失败", Toast.LENGTH_SHORT).show();
                } else {
                    if (deniedPermissionList.get(0).equals(Manifest.permission.READ_SMS)) {
                        Toast.makeText(this, "获取联系人权限成功、获取短信权限失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "获取短信权限成功、获取联系人权限失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
        // 如果用户对权限申请操作设置了不再提醒，则提示用户到应用设置界面主动授权
        if (PermissionSteward.hasAlwaysDeniedPermission(this, deniedPermissionList)) {
            //默认提示语
            //PermissionSteward.defaultSettingDialog(this, REQUEST_CODE_SETTING).show();
            //自定义提示语
            PermissionSteward.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                    .setTitle("权限申请失败")
                    .setMessage("需要的一些权限被拒绝授权，请到设置页面手动授权，否则功能无法正常使用")
                    .setPositiveButton("好，去设置")
                    .show();
        }
    }

}
