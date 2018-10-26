package com.xyzinnotech.ddn;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.xyzinnotech.ddn.fragment.RegionsListFragment;
import com.xyzinnotech.ddn.model.Dwelling;
import com.xyzinnotech.ddn.network.service.DataSyncAPIService;
import com.xyzinnotech.ddn.utils.DataUtils;
import com.xyzinnotech.ddn.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class RegionsListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private FloatingSearchView mSearchView;
    private RegionsListFragment regionsListFragment;
    private DataSyncAPIService dataSyncAPIService;
    private String apiKey = "W8BcdRqxTaA1XT3jgzLOz6g8qJKO8Gx7";
    private Realm mRealm;
    private static Realm refRealm;
    public static Context context;
    private static Toast t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regions_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSearchView = findViewById(R.id.floating_search_view);
        context = this;

        dataSyncAPIService = NetworkUtils.provideDataSyncAPIService(this);
        t = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        t.getView().setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
        mRealm = Realm.getDefaultInstance();
        refRealm = mRealm;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (NetworkUtils.isConnectingToInternet(this)) {
//            syncData(dataSyncAPIService);

        } else {
            /*t.setText("Internet is off");
            t.show();*/
        }

        FragmentManager fm = getSupportFragmentManager();
        regionsListFragment = RegionsListFragment.newInstance(null, null);
        fm.beginTransaction().add(R.id.content_regions_list, regionsListFragment).commit();

        TextView navUserName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        navUserName.setText(DataUtils.getName(this));
        TextView navUserEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        navUserEmail.setText(DataUtils.getEmail(this));

        mSearchView.attachNavigationDrawerToMenuButton(drawer);

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                mSearchView.showProgress();
                if (newQuery != null && regionsListFragment != null) {
                    regionsListFragment.applyFilter(newQuery);
                }
                mSearchView.hideProgress();
            }
        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_sync) {
                    if (NetworkUtils.isConnectingToInternet(RegionsListActivity.this)) {
                        syncData(dataSyncAPIService);
                    } else {
                        t.setText("No Network");
                        t.show();
                    }
                } else if (item.getItemId() == R.id.action_voice_rec) {
                    promptSpeechInput();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_projects) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.content_regions_list, RegionsListFragment.newInstance(null, null)).commit();
            setTitle("Projects");
        } else if (id == R.id.nav_flight_plans) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.content_regions_list, RegionsListFragment.newInstance(null, null)).commit();
            setTitle("Flight Plans");
        } else if (id == R.id.nav_slideshow) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.content_regions_list, RegionsListFragment.newInstance(null, null)).commit();
            setTitle("Flight Plans");
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * sync data
     *
     * @param dataSyncAPIService
     */
    public void syncData(DataSyncAPIService dataSyncAPIService) {
        if (dataSyncAPIService != null) {

            RealmResults<Dwelling> realmResults = refRealm.where(Dwelling.class).equalTo("offset", 1).findAll();
            Gson gson = new Gson();
            if (realmResults.size() > 0) {
                String json = gson.toJson(refRealm.copyFromRealm(realmResults));
                JsonArray jsonArray = new Gson().fromJson(json, JsonArray.class);
                Log.d("syncdata", "syncData: " + json);

                /*dataSyncAPIService.getPutList(jsonArray, apiKey).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            t.setText("sync success!");
                            t.show();
                            refRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    for (Dwelling dwelling : realmResults) {
                                        dwelling.setOffset(0);
                                    }
                                }
                            });
                        } else {
                            try {
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(RegionsListActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/
            } else {
                t.setText("Remote database is up-to-date");
                t.show();
            }
        }
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String searchText = result.get(0);
                    mSearchView.setSearchText(searchText);
                    if (searchText != null && regionsListFragment != null) {
                        regionsListFragment.applyFilter(searchText);
                    }
                }
                break;
            }

        }
    }
}