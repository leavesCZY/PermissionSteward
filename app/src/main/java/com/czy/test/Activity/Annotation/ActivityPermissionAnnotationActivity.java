package com.czy.test.Activity.Annotation;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.czy.permission.Annotation.PermissionFailed;
import com.czy.permission.Annotation.PermissionSucceed;
import com.czy.permission.Callback.Rationale;
import com.czy.permission.Callback.RationaleListener;
import com.czy.permission.PermissionSteward;
import com.czy.test.R;

import java.util.List;

public class ActivityPermissionAnnotationActivity extends AppCompatActivity implements View.OnClickListener {

    //申请位置权限
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;

    private static final int REQUEST_CODE_SETTING = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_permission_annotation);
        findViewById(R.id.btn_request_single).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_request_single) {
            //当权限被拒绝且没有选择不再提醒时，当再次申请权限时，以下方法会显示一个默认的用于权限说明的对话框
            //PermissionSteward.requestPermission(this, REQUEST_CODE_PERMISSION_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
            //也可以来自定义权限申请说明
            RationaleListener rationaleListener = new RationaleListener() {
                @Override
                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                    PermissionSteward.rationaleDialog(ActivityPermissionAnnotationActivity.this, rationale)
                            .setTitle("权限好像之前被拒绝了")
                            .setMessage("请授予权限，否则我没法和你玩耍啊~~")
                            .show();
                }
            };
            PermissionSteward.requestPermission(this, rationaleListener, REQUEST_CODE_PERMISSION_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionSteward.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING: {
                Toast.makeText(this, "用户从设置界面回来了", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    @PermissionSucceed(REQUEST_CODE_PERMISSION_LOCATION)
    private void getLocationSucceed(List<String> grantedPermissions) {
        Toast.makeText(this, "获取位置权限成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFailed(REQUEST_CODE_PERMISSION_LOCATION)
    private void getLocationFailed(List<String> deniedPermissionList) {
        Toast.makeText(this, "获取位置权限失败", Toast.LENGTH_SHORT).show();
        if (PermissionSteward.hasAlwaysDeniedPermission(this, deniedPermissionList)) {
            PermissionSteward.defaultSettingDialog(this, REQUEST_CODE_SETTING).show();
        }
    }

}
