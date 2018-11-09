package com.xyzinnotech.ddn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyzinnotech.ddn.fragment.DwellingsListFragment;
import com.xyzinnotech.ddn.model.Dwellinginfo;

import io.realm.Realm;

import static com.xyzinnotech.ddn.SingleFragmentActivity.KEY_SINGLE_FRAGMENT;

public class DwellingsListActivity extends AppCompatActivity {

    private static final String ARG_DDN = "ddn";
    private String ddn;
    private Dwellinginfo mDwellingInf;
    private Realm mRealm;
    private TextView full_address;
    private TextView landmark;
    private TextView town_name;
    private TextView mandal_name;
    private TextView district_name;
    private TextView pincode;
    private Button view_address;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior view_address_behavior;
    private LinearLayout view_layout;
    private TextView add_address_link;
    private TextView edit_address_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dwellings_list);

        bottomSheet = (LinearLayout) findViewById(R.id.ddn_bottom_sheet);
        view_address_behavior = BottomSheetBehavior.from(bottomSheet);
        ddn = getIntent().getStringExtra("ddn");
        full_address = (TextView) findViewById(R.id.txt_full_addres);
        landmark = (TextView) findViewById(R.id.txt_landmark);
        town_name = (TextView) findViewById(R.id.txt_village_name);
        mandal_name = (TextView) findViewById(R.id.txt_mandal_name);
        district_name = (TextView) findViewById(R.id.txt_district_name);
        pincode = (TextView) findViewById(R.id.txt_pincode);
        view_address = (Button) bottomSheet.findViewById(R.id.txt_full_info);
        view_layout = (LinearLayout) findViewById(R.id.view_layout);
        add_address_link = (TextView) findViewById(R.id.add_address_btn);
        edit_address_btn = (TextView) findViewById(R.id.edit_address_btn);
        mRealm = Realm.getDefaultInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        view_address_behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        view_address.setText("Hide Address");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        view_address.setText("View Address");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        add_address_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DwellingsListActivity.this, AddDdnInfoActivity.class);
                intent.putExtra("ddn", getIntent().getStringExtra("ddn"));
                startActivity(intent);

                view_address_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        view_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (view_address_behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    view_address_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    showDwellingInfo();
                } else {
                    view_address_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        edit_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DwellingsListActivity.this, AddDdnInfoActivity.class);
                intent.putExtra("ddn", getIntent().getStringExtra("ddn"));
                startActivity(intent);

                view_address_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DwellingsListActivity.this, SingleFragmentActivity.class);
                intent.putExtra("ddn", ddn);
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

    private void showDwellingInfo() {
        mDwellingInf = mRealm.where(Dwellinginfo.class).equalTo("ddn", ddn).findFirst();
        if (mDwellingInf != null) {

            view_layout.setVisibility(View.VISIBLE);
            add_address_link.setVisibility(View.GONE);
            full_address.setText(mDwellingInf.getfullAddress());
            landmark.setText(mDwellingInf.getLandmark());
            town_name.setText(mDwellingInf.getvillageName());
            mandal_name.setText(mDwellingInf.getmandalName());
            district_name.setText(mDwellingInf.getdistrictName());
            pincode.setText(mDwellingInf.getpincode());
        } else {
            view_layout.setVisibility(View.GONE);
            add_address_link.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showDwellingInfo();
    }
}