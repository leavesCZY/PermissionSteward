package com.czy.permission;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

class PermissionUtils {

    /**
     * 获取对象上下文
     *
     * @param object 对象
     * @return 上下文
     */
    static Context getContext(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof android.support.v4.app.Fragment) {
            return ((android.support.v4.app.Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        }
        throw new IllegalArgumentException("The " + object.getClass().getName() + " is not support.");
    }

    /**
     * 判断未获得的权限 deniedPermissions 是否需要在再次申请权限前对用户进行权限申请说明
     * shouldShowRequestPermissionRationale() 方法默认会返回false
     * 当用户第一次申请权限时拒绝授权，且没有选择不再提醒，则之后会返回true
     * 当用户选择了不再提醒，则之后会返回false
     * 当用户在设置界面将权限关闭，则返回false
     *
     * @param object            对象
     * @param deniedPermissions 未获得的权限
     * @return 是否需要权限申请说明
     */
    static boolean shouldShowRationalePermissions(Object object, String... deniedPermissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        for (String permission : deniedPermissions) {
            if (object instanceof Activity) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, permission)) {
                    return true;
                }
            } else if (object instanceof android.support.v4.app.Fragment) {
                if (((android.support.v4.app.Fragment) object).shouldShowRequestPermissionRationale(permission)) {
                    return true;
                }
            } else if (object instanceof android.app.Fragment) {
                if (((android.app.Fragment) object).shouldShowRequestPermissionRationale(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

}
