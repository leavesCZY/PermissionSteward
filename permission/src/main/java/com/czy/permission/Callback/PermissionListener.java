package com.czy.permission.Callback;

import java.util.List;

public interface PermissionListener {

    /**
     * 权限全部申请通过时回调
     *
     * @param requestCode      请求码
     * @param grantPermissionList 申请通过的全部权限
     */
    void onSucceed(int requestCode, List<String> grantPermissionList);

    /**
     * 权限没有全部申请通过时回调
     *
     * @param requestCode       请求码
     * @param deniedPermissionList 没有申请通过的权限
     */
    void onFailed(int requestCode, List<String> deniedPermissionList);

}
