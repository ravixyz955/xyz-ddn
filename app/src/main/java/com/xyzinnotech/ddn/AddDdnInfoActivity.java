package com.xyzinnotech.ddn;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xyzinnotech.ddn.model.Dwelling;
import com.xyzinnotech.ddn.model.Dwellinginfo;

import io.realm.Realm;

public class AddDdnInfoActivity extends AppCompatActivity {

    private EditText full_address;
    private EditText town_name;
    private EditText mandal_name;
    private EditText district_name;
    private EditText pincode;
    private Button save_info_btn;
    private Dwellinginfo mDwellingInf;
    private Toast t;
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
        pincode = (EditText) findViewById(R.id.pincode);
        save_info_btn = (Button) findViewById(R.id.save_info_btn);

        t = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        t.getView().setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));

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
            full_address.setText(mDwellingInf.getAddress());
            town_name.setText(mDwellingInf.getTown());
            mandal_name.setText(mDwellingInf.getMandal());
            district_name.setText(mDwellingInf.getDistrict());
            pincode.setText(mDwellingInf.getPincode());
        }

        save_info_btn.setOnClickListener(new View.OnClickListener() {
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
                    mDwellingInf.setAddress(full_address.getText().toString());
                    mDwellingInf.setTown(town_name.getText().toString());
                    mDwellingInf.setMandal(mandal_name.getText().toString());
                    mDwellingInf.setDistrict(district_name.getText().toString());
                    mDwellingInf.setPincode(pincode.getText().toString());

                    mRealm.insertOrUpdate(mDwellingInf);
                    mRealm.commitTransaction();

                    t.setText("Saved successfully!");
                    t.show();
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
