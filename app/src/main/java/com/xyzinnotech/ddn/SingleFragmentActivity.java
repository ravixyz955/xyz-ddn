package com.xyzinnotech.ddn;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.xyzinnotech.ddn.fragment.DwellingDetailsFragment;

import io.realm.Realm;

public class SingleFragmentActivity extends AppCompatActivity {

    public static final String KEY_SINGLE_FRAGMENT = "fragment";
    private Realm mRealm;
    private static final String ARG_DDN = "ddn";
    private static final String DELETE = "delete";
    String ddn;
    Bundle extras;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        extras = getIntent().getExtras();

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

        mRealm = Realm.getDefaultInstance();

        if (extras != null && extras.containsKey(KEY_SINGLE_FRAGMENT)) {
            ddn = extras.getString(ARG_DDN);
            Fragment fragment = getFragmentByName((FragmentName) extras.get(KEY_SINGLE_FRAGMENT));
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.singlefragment_container, fragment);
            fragmentTransaction.commit();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.icon_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete:
                DwellingDetailsFragment frgmt = new DwellingDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("delete", DELETE);
                frgmt.removeListItem();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.singlefragment_container, frgmt);
                fragmentTransaction.commit();
                finish();

                break;
        }
        return super.onOptionsItemSelected(item);
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
