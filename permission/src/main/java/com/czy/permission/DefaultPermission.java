package com.czy.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.czy.permission.Callback.Permission;
import com.czy.permission.Callback.Rationale;
import com.czy.permission.Callback.RationaleListener;

import java.util.ArrayList;
import java.util.List;

class DefaultPermission implements Permission {

    private static final String TAG = "DefaultPermission";

    private String[] permissions;

    private String[] deniedPermissions;

    private int requestCode;

    private Object object;

    private RationaleListener rationaleListener;

    DefaultPermission(final Object object) {
        if (object == null) {
            throw new IllegalArgumentException("The object can not be null.");
        } else if (object instanceof Activity || object instanceof android.support.v4.app.Fragment || object instanceof android.app.Fragment) {
            this.object = object;
            this.rationaleListener = new RationaleListener() {
                @Override
                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                    PermissionSteward.rationaleDialog(PermissionUtils.getContext(object), rationale).show();
                }
            };
        } else {
            throw new IllegalArgumentException("The " + object.getClass().getName() + " is not support.");
        }
    }

    @NonNull
    @Override
    public Permission permission(String... permissions) {
        if (permissions == null) {
            throw new IllegalArgumentException("The permissions can not be null.");
        }
        this.permissions = permissions;
        return this;
    }

    @NonNull
    @Override
    public Permission requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    @NonNull
    @Override
    public Permission rationale(RationaleListener rationaleListener) {
        this.rationaleListener = rationaleListener;
        return this;
    }

    private Rationale rationale = new Rationale() {

        @Override
        public void cancel() {
            int[] results = new int[permissions.length];
            Context context = PermissionUtils.getContext(object);
            for (int i = 0; i < results.length; i++) {
                results[i] = ActivityCompat.checkSelfPermission(context, permissions[i]);
            }
            onRequestPermissionsResult(object, requestCode, permissions, results);
        }

        @Override
        public void resume() {
            requestPermissions(object, requestCode, deniedPermissions);
        }

    };

    @Override
    public void send() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Context context = PermissionUtils.getContext(object);
            int[] grantResults = new int[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                // 判断应用是否有在 AndroidManifest 声明该权限
                grantResults[i] = context.getPackageManager().checkPermission(permissions[i], context.getPackageName());
            }
            //如果是6.0版本之下的系统，则直接调用回调方法，不需要检查权限是否已获得
            onRequestPermissionsResult(object, requestCode, permissions, grantResults);
        } else {
            //获取要申请的权限列表中未获得的权限（可能是还未申请，或者是申请了但被拒绝）
            deniedPermissions = getDeniedPermissions(object, permissions);
            if (deniedPermissions.length > 0) {
                //如果此时需要向用户进行权限申请说明，则显示权限申请说明窗口
                if (rationaleListener != null && PermissionUtils.shouldShowRationalePermissions(object, deniedPermissions)) {
                    rationaleListener.showRequestPermissionRationale(requestCode, rationale);
                } else {
                    rationale.resume();
                }
            } else {
                //所有的权限都已经都申请通过了
                int[] grantResults = new int[permissions.length];
                for (int i = 0; i < permissions.length; i++) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                }
                onRequestPermissionsResult(object, requestCode, permissions, grantResults);
            }
        }
    }

    /**
     * 获取 permissions 中未申请通过的权限
     *
     * @param object      Activity或者Fragment
     * @param permissions 要申请的所有权限
     * @return 未申请通过的权限
     */
    private static String[] getDeniedPermissions(Object object, String... permissions) {
        List<String> deniedPermissionList = new ArrayList<>(1);
        for (String permission : permissions)
            if (!PermissionSteward.hasPermission(PermissionUtils.getContext(object), permission)) {
                deniedPermissionList.add(permission);
            }
        return deniedPermissionList.toArray(new String[deniedPermissionList.size()]);
    }

    /**
     * 请求权限
     *
     * @param object      Activity或者Fragment
     * @param requestCode 请求码
     * @param permissions 要申请的权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    private static void requestPermissions(Object object, int requestCode, String... permissions) {
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions(((Activity) object), permissions, requestCode);
        } else if (object instanceof android.support.v4.app.Fragment) {
            ((android.support.v4.app.Fragment) object).requestPermissions(permissions, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(permissions, requestCode);
        }
    }

    /**
     * 权限申请后的回调
     *
     * @param object       Activity或者Fragment
     * @param requestCode  请求码
     * @param permissions  要申请的权限
     * @param grantResults 权限申请结果
     */
    @TargetApi(Build.VERSION_CODES.M)
    private static void onRequestPermissionsResult(Object object, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (object instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Activity) object).onRequestPermissionsResult(requestCode, permissions, grantResults);
            } else if (object instanceof ActivityCompat.OnRequestPermissionsResultCallback) {
                ((ActivityCompat.OnRequestPermissionsResultCallback) object).onRequestPermissionsResult(requestCode, permissions, grantResults);
            } else {
                Log.e(TAG, "The " + object.getClass().getName() + " is not support " + "onRequestPermissionsResult()");
            }
        } else if (object instanceof android.support.v4.app.Fragment) {
            ((android.support.v4.app.Fragment) object).onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
