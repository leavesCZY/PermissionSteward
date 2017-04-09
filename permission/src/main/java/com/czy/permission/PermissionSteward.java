package com.czy.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.czy.permission.Annotation.PermissionFailed;
import com.czy.permission.Annotation.PermissionSucceed;
import com.czy.permission.Callback.PermissionListener;
import com.czy.permission.Callback.Rationale;
import com.czy.permission.Callback.RationaleListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PermissionSteward {

    private static final String TAG = "PermissionSteward";

    private PermissionSteward() {

    }

    /**
     * 判断是否拥有指定权限
     *
     * @param context    上下文
     * @param permission 权限
     * @return 是否拥有指定权限
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断用户是否对曾拒绝授权的权限设置了不再提醒（如果是的话会使得系统直接拒绝应用获得权限）
     *
     * @param activity             Activity
     * @param deniedPermissionList 拒绝过的权限
     * @return 是否设置了不再提醒
     */
    public static boolean hasAlwaysDeniedPermission(@NonNull Activity activity, @NonNull List<String> deniedPermissionList) {
        for (String deniedPermission : deniedPermissionList) {
            if (!PermissionUtils.shouldShowRationalePermissions(activity, deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否对曾拒绝授权的权限设置了不再提醒（如果是的话会使得系统直接拒绝应用获得权限）
     *
     * @param fragment             android.support.v4.app.Fragment
     * @param deniedPermissionList 拒绝过的权限
     * @return 是否设置了不再提醒
     */
    public static boolean hasAlwaysDeniedPermission(@NonNull android.support.v4.app.Fragment fragment, @NonNull List<String> deniedPermissionList) {
        for (String deniedPermission : deniedPermissionList) {
            if (!PermissionUtils.shouldShowRationalePermissions(fragment, deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户是否对曾拒绝授权的权限设置了不再提醒（如果是的话会使得系统直接拒绝应用获得权限）
     *
     * @param fragment             android.app.Fragment
     * @param deniedPermissionList 拒绝过的权限
     * @return 是否设置了不再提醒
     */
    public static boolean hasAlwaysDeniedPermission(@NonNull android.app.Fragment fragment, @NonNull List<String> deniedPermissionList) {
        for (String deniedPermission : deniedPermissionList) {
            if (!PermissionUtils.shouldShowRationalePermissions(fragment, deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 注解的回调操作
     * 通过反射来对申请结果进行回调
     *
     * @param object       Activity或者Fragment
     * @param requestCode  申请权限时的请求码
     * @param permissions  要申请的权限
     * @param grantResults 每一个要申请的权限的申请结果（申请通过或否决）
     */
    private static void callbackAnnotation(@NonNull Object object, int requestCode, @NonNull String[] permissions, int[] grantResults) {
        List<String> grantedList = new ArrayList<>();
        List<String> deniedList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedList.add(permissions[i]);
            } else {
                deniedList.add(permissions[i]);
            }
        }
        //是否通过了全部权限
        boolean isAllGrant = deniedList.isEmpty();
        //根据申请结果对相应的注解进行回调
        Class<? extends Annotation> clazz = isAllGrant ? PermissionSucceed.class : PermissionFailed.class;
        Method[] methods = findMethodForRequestCode(object.getClass(), clazz, requestCode);
        //没有对应的注解方法
        if (methods.length == 0) {
            Log.e(TAG, "Not found the callback method, do you forget @PermissionSucceed or @PermissionFailed for callback method ? Or you can use PermissionListener.");
        } else {
            try {
                for (Method method : methods) {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    method.invoke(object, isAllGrant ? grantedList : deniedList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过注解类和请求码来查找对应的注解函数
     *
     * @param source      要查看的类.class
     * @param annotation  注解类.class
     * @param requestCode 请求码
     * @return 包含查找到的函数的数组
     */
    private static <T extends Annotation> Method[] findMethodForRequestCode(@NonNull Class<?> source, @NonNull Class<T> annotation, int requestCode) {
        List<Method> methodList = new ArrayList<>(1);
        for (Method method : source.getDeclaredMethods())
            if (method.isAnnotationPresent(annotation)) {
                if (isSameRequestCode(method, annotation, requestCode)) {
                    methodList.add(method);
                }
            }
        return methodList.toArray(new Method[methodList.size()]);
    }

    /**
     * 判断method函数的注解的值是否与请求码requestCode相等
     * 即根据请求码来查找对应的被注解的函数
     *
     * @param method      函数
     * @param annotation  注解类.class
     * @param requestCode 请求码
     * @return 是否相等
     */
    private static <T extends Annotation> boolean isSameRequestCode(@NonNull Method method, @NonNull Class<T> annotation, int requestCode) {
        if (PermissionSucceed.class.equals(annotation)) {
            return method.getAnnotation(PermissionSucceed.class).value() == requestCode;
        } else if (PermissionFailed.class.equals(annotation)) {
            return method.getAnnotation(PermissionFailed.class).value() == requestCode;
        }
        return false;
    }

    /**
     * 对权限申请结果进行解析，然后调用相应的回调函数
     *
     * @param requestCode        请求码
     * @param permissions        要申请的权限
     * @param grantResults       每一个要申请的权限的申请结果（申请通过或否决）
     * @param permissionListener 回调函数
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults, @NonNull PermissionListener permissionListener) {
        List<String> grantedList = new ArrayList<>();
        List<String> deniedList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedList.add(permissions[i]);
            } else {
                deniedList.add(permissions[i]);
            }
        }
        if (deniedList.isEmpty()) {
            permissionListener.onSucceed(requestCode, grantedList);
        } else {
            permissionListener.onFailed(requestCode, deniedList);
        }
    }

    public static void requestPermission(@NonNull Activity activity, int requestCode, @NonNull String... permissions) {
        new DefaultPermission(activity).requestCode(requestCode).permission(permissions).send();
    }

    public static void requestPermission(@NonNull android.support.v4.app.Fragment fragment, int requestCode, @NonNull String... permissions) {
        new DefaultPermission(fragment).requestCode(requestCode).permission(permissions).send();
    }

    public static void requestPermission(@NonNull android.app.Fragment fragment, int requestCode, @NonNull String... permissions) {
        new DefaultPermission(fragment).requestCode(requestCode).permission(permissions).send();
    }

    public static void requestPermission(@NonNull Activity activity, RationaleListener rationaleListener, int requestCode, @NonNull String... permissions) {
        new DefaultPermission(activity).requestCode(requestCode).permission(permissions).rationale(rationaleListener).send();
    }

    public static void requestPermission(@NonNull android.support.v4.app.Fragment fragment, RationaleListener rationaleListener, int requestCode, @NonNull String... permissions) {
        new DefaultPermission(fragment).requestCode(requestCode).permission(permissions).rationale(rationaleListener).send();
    }

    public static void requestPermission(@NonNull android.app.Fragment fragment, RationaleListener rationaleListener, int requestCode, @NonNull String... permissions) {
        new DefaultPermission(fragment).requestCode(requestCode).permission(permissions).rationale(rationaleListener).send();
    }

    public static RationaleDialog rationaleDialog(@NonNull Context context, @NonNull Rationale rationale) {
        return new RationaleDialog(context, rationale);
    }

    public static SettingDialog defaultSettingDialog(@NonNull Activity activity, int requestCode) {
        return new SettingDialog(activity, new SettingExecutor(activity, requestCode));
    }

    public static SettingDialog defaultSettingDialog(@NonNull android.support.v4.app.Fragment fragment, int requestCode) {
        return new SettingDialog(fragment.getActivity(), new SettingExecutor(fragment, requestCode));
    }

    public static SettingDialog defaultSettingDialog(@NonNull android.app.Fragment fragment, int requestCode) {
        return new SettingDialog(fragment.getActivity(), new SettingExecutor(fragment, requestCode));
    }

    public static void onRequestPermissionsResult(@NonNull Activity activity, int requestCode, @NonNull String[] permissions, int[] grantResults) {
        callbackAnnotation(activity, requestCode, permissions, grantResults);
    }

    public static void onRequestPermissionsResult(@NonNull android.support.v4.app.Fragment fragment, int requestCode, @NonNull String[] permissions, int[] grantResults) {
        callbackAnnotation(fragment, requestCode, permissions, grantResults);
    }

    public static void onRequestPermissionsResult(@NonNull android.app.Fragment fragment, int requestCode, @NonNull String[] permissions, int[] grantResults) {
        callbackAnnotation(fragment, requestCode, permissions, grantResults);
    }

}
