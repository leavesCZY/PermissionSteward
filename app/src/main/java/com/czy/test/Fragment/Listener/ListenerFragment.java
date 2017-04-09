package com.czy.test.Fragment.Listener;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.czy.permission.Callback.PermissionListener;
import com.czy.permission.PermissionSteward;
import com.czy.test.R;

import java.util.List;

public class ListenerFragment extends Fragment implements View.OnClickListener, PermissionListener {

    //申请SD卡权限
    private static final int REQUEST_CODE_PERMISSION_EXTERNAL_STORAGE = 100;

    private static final int REQUEST_CODE_SETTING = 300;

    public ListenerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listener, container, false);
        view.findViewById(R.id.btn_request_single).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        PermissionSteward.requestPermission(this, REQUEST_CODE_PERMISSION_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionSteward.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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

    @Override
    public void onSucceed(int requestCode, List<String> grantPermissionList) {
        Toast.makeText(getContext(), "获取存储卡权限成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(int requestCode, List<String> deniedPermissionList) {
        Toast.makeText(getContext(), "获取存储卡权限失败", Toast.LENGTH_SHORT).show();
        if (PermissionSteward.hasAlwaysDeniedPermission(this, deniedPermissionList)) {
            PermissionSteward.defaultSettingDialog(this, REQUEST_CODE_SETTING).show();
        }
    }

}
