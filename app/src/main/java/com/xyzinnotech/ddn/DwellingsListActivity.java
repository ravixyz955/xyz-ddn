package com.xyzinnotech.ddn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xyzinnotech.ddn.fragment.DwellingsListFragment;

import static com.xyzinnotech.ddn.SingleFragmentActivity.KEY_SINGLE_FRAGMENT;

public class DwellingsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dwellings_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DwellingsListActivity.this, SingleFragmentActivity.class);
                intent.putExtra("ddn", getIntent().getStringExtra("ddn"));
                intent.putExtra("insert", "insert");
                intent.putExtra(KEY_SINGLE_FRAGMENT, SingleFragmentActivity.FragmentName.DWELLINGDETAILS);
                startActivity(intent);
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        DwellingsListFragment dwellingsListFragment = DwellingsListFragment.newInstance();
        fm.beginTransaction().add(R.id.content_dwellings_list, dwellingsListFragment).commit();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
