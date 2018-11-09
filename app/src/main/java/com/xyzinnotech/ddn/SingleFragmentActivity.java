package com.xyzinnotech.ddn;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
        if (getIntent().hasExtra("insert")) {
            //do nothing
        } else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.icon_menu, menu);
        }
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

                AlertDialog.Builder dialog = new AlertDialog.Builder(SingleFragmentActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("Are you sure you want to delete this entry?");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "Delete".
                        frgmt.removeListItem();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.singlefragment_container, frgmt);
                        fragmentTransaction.commit();
                        finish();
                    }
                })
                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel".
                            }
                        });
                final AlertDialog alert = dialog.create();
                alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setToolbarTitle(String title) {
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        setTitle(title);
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
