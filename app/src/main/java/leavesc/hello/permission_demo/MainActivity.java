package leavesc.hello.permission_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import leavesc.hello.permission_demo.activity.ActivityPermissionActivity;
import leavesc.hello.permission_demo.fragment.FragmentPermissionActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_request_activity).setOnClickListener(this);
        findViewById(R.id.btn_request_fragment).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request_activity:
                startActivity(new Intent(this, ActivityPermissionActivity.class));
                break;
            case R.id.btn_request_fragment:
                startActivity(new Intent(this, FragmentPermissionActivity.class));
                break;
        }
    }

}
