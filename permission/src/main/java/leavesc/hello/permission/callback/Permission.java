package leavesc.hello.permission.callback;

import android.support.annotation.NonNull;

public interface Permission {

    @NonNull
    Permission permission(String... permissions);

    @NonNull
    Permission requestCode(int requestCode);

    @NonNull
    Permission rationale(RationaleListener rationaleListener);

    void send();

}
