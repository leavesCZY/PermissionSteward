package com.czy.test.Fragment.Annotation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.czy.test.R;

public class FragmentPermissionAnnotationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_permission_annotation);
        getSupportFragmentManager().beginTransaction().add(R.id.ll_root, new AnnotationFragment()).commit();
    }

}
