package com.czy.test.Fragment.Listener;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.czy.test.R;

public class FragmentPermissionListenerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_permission_listener);
        getSupportFragmentManager().beginTransaction().add(R.id.ll_root, new ListenerFragment()).commit();
    }

}
