package com.czy.test.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.czy.test.Activity.Annotation.ActivityPermissionAnnotationActivity;
import com.czy.test.Activity.Listener.ActivityPermissionListenerActivity;
import com.czy.test.R;

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
