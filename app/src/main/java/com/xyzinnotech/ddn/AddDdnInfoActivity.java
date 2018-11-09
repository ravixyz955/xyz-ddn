package com.xyzinnotech.ddn;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xyzinnotech.ddn.model.Dwellinginfo;
import com.xyzinnotech.ddn.utils.Utils;

import io.realm.Realm;

public class AddDdnInfoActivity extends AppCompatActivity {

    private EditText full_address;
    private EditText town_name;
    private EditText mandal_name;
    private EditText district_name;
    private EditText landmark;
    private EditText pincode;
    private FloatingActionButton save_info_fab;
    private Dwellinginfo mDwellingInf;
    private String ddn;
    private static final String ARG_DDN = "ddn";
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ddn_info);

        mRealm = Realm.getDefaultInstance();
        full_address = (EditText) findViewById(R.id.full_address);
        town_name = (EditText) findViewById(R.id.town_name);
        mandal_name = (EditText) findViewById(R.id.mandal_name);
        district_name = (EditText) findViewById(R.id.district_name);
        landmark = (EditText) findViewById(R.id.landmark);
        pincode = (EditText) findViewById(R.id.pincode);
        save_info_fab = (FloatingActionButton) findViewById(R.id.save_address_fab);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(ARG_DDN)) {
            ddn = getIntent().getStringExtra(ARG_DDN);
            getSupportActionBar().setTitle(ddn);
        }
        mDwellingInf = mRealm.where(Dwellinginfo.class).equalTo("ddn", ddn).findFirst();
        if (mDwellingInf == null) {
            mDwellingInf = new Dwellinginfo();
            mDwellingInf.setDdn(ddn);
        } else {
            full_address.setText(mDwellingInf.getfullAddress());
            landmark.setText(mDwellingInf.getLandmark());
            town_name.setText(mDwellingInf.getvillageName());
            mandal_name.setText(mDwellingInf.getmandalName());
            district_name.setText(mDwellingInf.getdistrictName());
            pincode.setText(mDwellingInf.getpincode());
        }

        save_info_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(full_address.getText())) {
                    full_address.setError("enter address");
                } else if (TextUtils.isEmpty(town_name.getText())) {
                    town_name.requestFocus();
                    town_name.setError("enter town/village name");
                } else if (TextUtils.isEmpty(mandal_name.getText())) {
                    mandal_name.requestFocus();
                    mandal_name.setError("enter mandal name");
                } else if (TextUtils.isEmpty(district_name.getText())) {
                    district_name.requestFocus();
                    district_name.setError("enter district name");
                } else if (TextUtils.isEmpty(pincode.getText())) {
                    pincode.requestFocus();
                    pincode.setError("entere pincode");
                } else {

                    mRealm.beginTransaction();
                    mDwellingInf.setfullAddress(full_address.getText().toString());
                    if (TextUtils.isEmpty(landmark.getText())) {
                        mDwellingInf.setLandmark("landmark" + " : -");
                    } else {
                        mDwellingInf.setLandmark(landmark.getText().toString());
                    }
                    mDwellingInf.setvillageName(town_name.getText().toString());
                    mDwellingInf.setmandalName(mandal_name.getText().toString());
                    mDwellingInf.setOffset(1);
                    mDwellingInf.setdistrictName(district_name.getText().toString());
                    mDwellingInf.setpincode(pincode.getText().toString());

                    mRealm.insertOrUpdate(mDwellingInf);
                    mRealm.commitTransaction();

                    Utils.showToast(AddDdnInfoActivity.this, "Saved successfully!");
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (this != null) {
                this.finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
