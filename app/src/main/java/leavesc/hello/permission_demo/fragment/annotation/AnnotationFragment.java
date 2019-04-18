package leavesc.hello.permission_demo.fragment.annotation;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import leavesc.hello.permission.PermissionSteward;
import leavesc.hello.permission.annotation.PermissionFailed;
import leavesc.hello.permission.annotation.PermissionSucceed;
import leavesc.hello.permission_demo.R;

public class AnnotationFragment extends Fragment implements View.OnClickListener {

    //申请电话权限
    private static final int REQUEST_CODE_PERMISSION_CALL_PHONE = 100;

    private static final int REQUEST_CODE_SETTING = 300;

    public AnnotationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_annotation, container, false);
        view.findViewById(R.id.btn_request_single).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        //当权限被拒绝且没有选择不再提醒时，当再次申请权限时，以下方法会显示一个默认的用于权限说明的对话框
        //PermissionSteward.requestPermission(this, REQUEST_CODE_PERMISSION_CALL_PHONE, Manifest.permission.CALL_PHONE);
        //也可以选择不显示权限申请说明
        PermissionSteward.requestPermission(this, null, REQUEST_CODE_PERMISSION_CALL_PHONE, Manifest.permission.CALL_PHONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionSteward.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING: {
                Toast.makeText(getContext(), "用户从设置界面回来了", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    @PermissionSucceed(REQUEST_CODE_PERMISSION_CALL_PHONE)
    private void getCallPhoneSucceed(List<String> grantedPermissionList) {
        Toast.makeText(getContext(), "获取电话权限成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFailed(REQUEST_CODE_PERMISSION_CALL_PHONE)
    private void getCallPhoneFailed(List<String> deniedPermissionList) {
        Toast.makeText(getContext(), "获取电话权限失败", Toast.LENGTH_SHORT).show();
        if (PermissionSteward.hasAlwaysDeniedPermission(this, deniedPermissionList)) {
            PermissionSteward.defaultSettingDialog(this, REQUEST_CODE_SETTING).show();
        }
    }

}
