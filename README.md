如今网络上对于 Android 6.0 运行时权限进行分析的文章已经很多了，不了解的可以看我的另一篇文章： [Android 6.0 运行时权限解析](https://github.com/leavesC/Java_Kotlin_Android_Learn/blob/master/other/Android6.0%E8%BF%90%E8%A1%8C%E6%97%B6%E6%9D%83%E9%99%90%E8%A7%A3%E6%9E%90.md)

Android 6.0 运行时权限对于用户来说还是挺好的，对于开发者来说就多了很多工作了，如果不进行一些封装操作的话，会多出很多无谓的代码量，这里来提供一个对 Android 6.0 运行时权限的封装库，以简化操作：[PermissionSteward](https://github.com/leavesC/PermissionSteward)

先看下效果图：

在第一次申请权限被拒绝后，在第二次申请时显示一个对话框向用户说明应用为什么需要这个权限

![这里写图片描述](http://upload-images.jianshu.io/upload_images/2552605-cf3c91aab8fcdcc4?imageMogr2/auto-orient/strip)

可以自定义权限申请说明文本

![这里写图片描述](http://upload-images.jianshu.io/upload_images/2552605-29df18e7c31b6af3?imageMogr2/auto-orient/strip)

一次性申请多个权限，在用户拒绝权限且选了不再提醒时，引导用户跳转到设置界面主动开启权限

![这里写图片描述](http://upload-images.jianshu.io/upload_images/2552605-590945b843c5ae71?imageMogr2/auto-orient/strip)

这里要说明一下，我的这个封装库是基于GitHub上这位的源码来修改的：[AndPermission](https://github.com/yanzhenjie/AndPermission)
这两天看到了这个开源库，就研究了一下源码，感觉有挺多地方需要向作者学习的，不过有些地方感觉也不是太好，代码也没有注释，就修改了一下代码，并加上了详细注释


PermissionSteward 支持 Activity、android.support.v4.app.Fragment、android.app.Fragment，且权限申请操作都保持一致，回调方式提供注解和接口两种方式

## **一、通过接口来实现回调**

```java
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

```

首先让 Activity 实现 PermissionListener 接口
权限申请只需要一句即可

```java
PermissionSteward.requestPermission(this, REQUEST_CODE_PERMISSION_CALENDAR, Manifest.permission.READ_CALENDAR);
```
**REQUEST_CODE_PERMISSION_CALENDAR** 是自定义的请求码，用来标识要请求的权限
当权限被拒绝且用户没有选择不再提醒时，当再次申请权限时，以上方法会显示一个默认的用于权限说明的对话框
也可以来自定义权限申请说明

```java
	RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
            PermissionSteward.rationaleDialog(ActivityPermissionListenerActivity.this, rationale)
                .setTitle("权限好像之前被拒绝了")
                .setMessage("请授予权限，否则我没法和你玩耍啊~~")
                .show();
            }
        };
    PermissionSteward.requestPermission(this, rationaleListener, REQUEST_CODE_PERMISSION_CALENDAR, Manifest.permission.READ_CALENDAR);
```
然后重写 onRequestPermissionsResult() 方法
```java
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionSteward.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
```
当权限申请通过或者被否决时，就会回调 **onSucceed()** 或者 **onFailed()** 方法，在这两个方法中进行相应的业务逻辑处理即可

## **二、通过注解来实现回调**
一共有两个注解类

```java
/**
 * 用于标注未通过权限申请
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionFailed {
    int value() default -1;
}
```

```java
/**
 * 用于标注通过权限申请
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionSucceed {
    int value() default -1;
}
```
在代码中为当权限通过或被否决时要调用的方法加上注解，注解值即当申请权限时的请求码

```java
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
```
同样是用以下方法来申请权限

```java
PermissionSteward.requestPermission(this, REQUEST_CODE_PERMISSION_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
```
重写onRequestPermissionsResult()方法
```java
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionSteward.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
```
然后为需要的方法加上注解即可
```java
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
```

推荐通过接口的方式来实现回调，注解的方式是通过反射来完成回调的，反射毕竟会消耗一定性能

源代码不算多复杂，有兴趣的可以去研究下

### 这里提供封装库和示例代码的下载：[Android 6.0 运行时权限封装](https://github.com/leavesC/PermissionSteward)