package com.xyzinnotech.ddn;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xyzinnotech.ddn.fragment.DwellingDetailsFragment;

public class SingleFragmentActivity extends AppCompatActivity {

    public static final String KEY_SINGLE_FRAGMENT = "fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);Bundle extras = getIntent().getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (extras != null && extras.containsKey(KEY_SINGLE_FRAGMENT)) {
            Fragment fragment = getFragmentByName((FragmentName) extras.get(KEY_SINGLE_FRAGMENT));
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.singlefragment_container, fragment);
            fragmentTransaction.commit();
        } else {
            finish();
        }
    }

    public void setToolbarTitle(String title) {
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
    }

    public Fragment getFragmentByName(FragmentName name) {
        switch (name) {
            case DWELLINGDETAILS:
                return DwellingDetailsFragment.newInstance(null, null);
            default:
                return null;
        }
    }

    public enum FragmentName {
        DWELLINGDETAILS
    }
}
