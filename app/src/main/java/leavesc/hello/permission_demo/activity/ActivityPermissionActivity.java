package leavesc.hello.permission_demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import leavesc.hello.permission_demo.R;
import leavesc.hello.permission_demo.activity.annotation.ActivityPermissionAnnotationActivity;
import leavesc.hello.permission_demo.activity.listener.ActivityPermissionListenerActivity;

public class ActivityPermissionActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_activity);
        findViewById(R.id.btn_request_listener).setOnClickListener(this);
        findViewById(R.id.btn_request_annotation).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request_listener:
                startActivity(new Intent(this, ActivityPermissionListenerActivity.class));
                break;
            case R.id.btn_request_annotation:
                startActivity(new Intent(this, ActivityPermissionAnnotationActivity.class));
                break;
        }
    }

}
