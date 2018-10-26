package com.xyzinnotech.ddn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
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
    private TextView town_name;
    private TextView mandal_name;
    private TextView district_name;
    private TextView pincode;
    private Button view_address;
    private int currentState;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior view_address_behavior;
    Button button;
    Button click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dwellings_list);

        ddn = getIntent().getStringExtra("ddn");
        full_address = (TextView) findViewById(R.id.txt_full_addres);
        town_name = (TextView) findViewById(R.id.txt_village_name);
        mandal_name = (TextView) findViewById(R.id.txt_mandal_name);
        district_name = (TextView) findViewById(R.id.txt_district_name);
        pincode = (TextView) findViewById(R.id.txt_pincode);
        bottomSheet = (LinearLayout) findViewById(R.id.ddn_bottom_sheet);
        view_address = (Button) bottomSheet.findViewById(R.id.txt_full_info);
        button = findViewById(R.id.click);
        click = findViewById(R.id.txt_full_info);
        mRealm = Realm.getDefaultInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        view_address_behavior = BottomSheetBehavior.from(bottomSheet);

//        click.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (view_address_behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//                    view_address_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                } else {
//                    view_address_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
//            }
//        });
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (view_address_behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//                    view_address_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                } else {
//                    view_address_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
//            }
//        });

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

        view_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (view_address_behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    view_address_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    view_address_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        /*view_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDwellingInf = mRealm.where(Dwellinginfo.class).equalTo("ddn", ddn).findFirst();
                if (mDwellingInf != null) {

                    view_address_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    full_address.setText(mDwellingInf.getAddress());
                    town_name.setText(mDwellingInf.getTown());
                    mandal_name.setText(mDwellingInf.getMandal());
                    district_name.setText(mDwellingInf.getDistrict());
                    pincode.setText(mDwellingInf.getPincode());
                } else {
                    add_address_behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });*/

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

        /*add_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DwellingsListActivity.this, AddDdnInfoActivity.class);
                intent.putExtra("ddn", getIntent().getStringExtra("ddn"));
                startActivity(intent);
                add_address_behaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });*/
        /*add_details_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DwellingsListActivity.this, AddDdnInfoActivity.class);
                intent.putExtra("ddn", getIntent().getStringExtra("ddn"));
                startActivity(intent);
            }
        });*/


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

            full_address.setText(mDwellingInf.getAddress());
            town_name.setText(mDwellingInf.getTown());
            mandal_name.setText(mDwellingInf.getMandal());
            district_name.setText(mDwellingInf.getDistrict());
            pincode.setText(mDwellingInf.getPincode());
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showDwellingInfo();
    }
}