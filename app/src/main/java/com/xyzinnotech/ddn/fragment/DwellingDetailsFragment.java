package com.xyzinnotech.ddn.fragment;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xyzinnotech.ddn.R;
import com.xyzinnotech.ddn.RegionsListActivity;
import com.xyzinnotech.ddn.SingleFragmentActivity;
import com.xyzinnotech.ddn.model.Dwelling;
import com.xyzinnotech.ddn.network.service.DataSyncAPIService;
import com.xyzinnotech.ddn.utils.NetworkUtils;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;


public class DwellingDetailsFragment extends Fragment {

    private Dwelling mDwelling;
    private String ddn;
    private long createdAt;
    private Spinner block;
    private Spinner floor;
    private Spinner flat;
    private EditText ownerName;
    private EditText ownerAadhar;
    private EditText address;
    private EditText landmark;
    private EditText town;
    private EditText mandal;
    private EditText district;
    private EditText pincode;
    private CheckBox electricity;
    private CheckBox parking;
    private CheckBox toilets;
    private CheckBox water_connection;
    private CheckBox wells;
    private CheckBox rain_water_harvesting;
    private Spinner dwellingType;
    private Spinner structuralType;
    private EditText assessmentNo;
    private Realm mRealm;
    private RealmList<String> emenities;
    static Bundle extras;
    private static Dwelling refDwelling;
    private static Realm refRealm;
    private DataSyncAPIService dataSyncAPIService;
    private static Toast t;
    private static Activity activity;

    public DwellingDetailsFragment() {
        // Required empty public constructor
    }

    public static DwellingDetailsFragment newInstance(String param1, String param2) {
        return new DwellingDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = getActivity().getIntent().getExtras();
        dataSyncAPIService = NetworkUtils.provideDataSyncAPIService(getContext());
        activity = getActivity();

        t = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        t.getView().setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));

        if (getActivity().getIntent().hasExtra(Dwelling.class.getName())) {
            mDwelling = getActivity().getIntent().getParcelableExtra(Dwelling.class.getName());
            refDwelling = mDwelling;
        }
        if (getActivity().getIntent().hasExtra("ddn")) {
            ddn = getActivity().getIntent().getStringExtra("ddn");
            setTitle(ddn);
            if (mDwelling == null) {
                mDwelling = new Dwelling();
                mDwelling.setDdn(ddn);
                mDwelling.setCreatedAt(System.currentTimeMillis());
            }
        }
        mRealm = Realm.getDefaultInstance();
        refRealm = mRealm;
    }

    private void setTitle(String title) {
        ((SingleFragmentActivity) getActivity()).setToolbarTitle(title);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_dwelling_details, container, false);
        block = inflate.findViewById(R.id.block);
        floor = inflate.findViewById(R.id.floor);
        flat = inflate.findViewById(R.id.flat);
        ownerName = inflate.findViewById(R.id.owner_name);
        ownerAadhar = inflate.findViewById(R.id.owner_aadhar);
        address = inflate.findViewById(R.id.full_address);
        landmark = inflate.findViewById(R.id.landmark);
        town = inflate.findViewById(R.id.town_name);
        mandal = inflate.findViewById(R.id.mandal_name);
        district = inflate.findViewById(R.id.district_name);
        pincode = inflate.findViewById(R.id.pincode);
        dwellingType = inflate.findViewById(R.id.dwelling_type);
        structuralType = inflate.findViewById(R.id.structural_type);
        assessmentNo = inflate.findViewById(R.id.assessment_number);
        electricity = inflate.findViewById(R.id.electricity);
        parking = inflate.findViewById(R.id.parking);
        toilets = inflate.findViewById(R.id.toilets);
        water_connection = inflate.findViewById(R.id.water_connection);
        wells = inflate.findViewById(R.id.wells);
        rain_water_harvesting = inflate.findViewById(R.id.rain_water_harvesting);

        FloatingActionButton fab = (FloatingActionButton) inflate.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (floor.getSelectedItemPosition() < 1) {
                    ((TextView) floor.getSelectedView()).setError("");
                    validateViews(view, "Floor");
                } else if (flat.getSelectedItemPosition() < 1) {
                    ((TextView) flat.getSelectedView()).setError("");
                    validateViews(view, "Flat");
                } else if (TextUtils.isEmpty(ownerName.getText())) {
                    ownerName.requestFocus();
                    ownerName.setError("");
                    validateViews(view, "Owner name");
                } else if (dwellingType.getSelectedItemPosition() < 1) {
                    ((TextView) dwellingType.getSelectedView()).setError("");
                    validateViews(view, "Dwelling Type");
                } else {
                    if (getActivity().getIntent().hasExtra("insert")) {
                        Dwelling dwelling = mRealm.where(Dwelling.class).equalTo("CompositePrimaryKey", block.getSelectedItem().toString() + floor.getSelectedItem().toString() + flat.getSelectedItem().toString()).findFirst();
                        if (dwelling != null) {
                            t.setText("Dwelling already exists!");
                            t.show();
                        } else {
                            saveDwelling();
                        }
                    } else {
                        saveDwelling();
                    }
//                    saveDwelling();
                }
            }
        });
        return inflate;
    }

    private void validateViews(View view, String errMsg) {
        Snackbar snackbar = Snackbar.make(view.getRootView(), "   " + errMsg + " " + "cannot be empty", Snackbar.LENGTH_LONG);
        View snackbar_view = snackbar.getView();
        TextView snackbar_text = (TextView) snackbar_view.findViewById(android.support.design.R.id.snackbar_text);
        snackbar_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exclamationmark, 0, 0, 0);
        snackbar_text.setGravity(Gravity.CENTER);
        snackbar.show();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mDwelling != null) {
            emenities = mDwelling.getEmenities();
            if (emenities != null) {
                int emenitiesLength = emenities.size();
                if (emenitiesLength > 0) {
                    for (int i = 0; i < emenitiesLength; i++) {
                        if (emenities.get(i).equalsIgnoreCase("Electricity")) {
                            electricity.setChecked(true);
                        } else if (emenities.get(i).equalsIgnoreCase("Parking")) {
                            parking.setChecked(true);
                        } else if (emenities.get(i).equalsIgnoreCase("Toilets")) {
                            toilets.setChecked(true);
                        } else if (emenities.get(i).equalsIgnoreCase("Water Connection")) {
                            water_connection.setChecked(true);
                        } else if (emenities.get(i).equalsIgnoreCase("Rain Water Harvesting")) {
                            rain_water_harvesting.setChecked(true);
                        }
                    }
                }
            }
            block.setSelection(getIndexOfItem(mDwelling.getBlock(), getResources().getStringArray(R.array.block)));
            floor.setSelection(getIndexOfItem(mDwelling.getFloor(), getResources().getStringArray(R.array.floor)));
            flat.setSelection(getIndexOfItem(mDwelling.getFlatNo(), getResources().getStringArray(R.array.flat)));
            ownerName.setText(mDwelling.getOwnerName());
            ownerAadhar.setText(mDwelling.getOwnerAadhar());
            address.setText(mDwelling.getAddress());
            landmark.setText(mDwelling.getLandmark());
            town.setText(mDwelling.getTown());
            mandal.setText(mDwelling.getMandal());
            district.setText(mDwelling.getDistrict());
            pincode.setText(mDwelling.getPincode());
            dwellingType.setSelection(getIndexOfItem(mDwelling.getDwellignType(), getResources().getStringArray(R.array.dwelling_type)));
            structuralType.setSelection(getIndexOfItem(mDwelling.getStructuralType(), getResources().getStringArray(R.array.structural_type)));
            assessmentNo.setText(mDwelling.getAssessmentNo());
        }
    }

    private void saveDwelling() {
        emenities = new RealmList<String>();
        if (electricity.isChecked()) {
            emenities.add(electricity.getText().toString());
        }
        if (parking.isChecked()) {
            emenities.add(parking.getText().toString());
        }
        if (toilets.isChecked()) {
            emenities.add(toilets.getText().toString());
        }
        if (water_connection.isChecked()) {
            emenities.add(water_connection.getText().toString());
        }
        if (wells.isChecked()) {
            emenities.add(wells.getText().toString());
        }
        if (rain_water_harvesting.isChecked()) {
            emenities.add(rain_water_harvesting.getText().toString());
        }
        mDwelling.setCompositePrimaryKey(block.getSelectedItem().toString() +
                floor.getSelectedItem().toString() +
                flat.getSelectedItem().toString());
        mDwelling.setBlock(block.getSelectedItem().toString());
        mDwelling.setFloor(floor.getSelectedItem().toString());
        mDwelling.setFlatNo(flat.getSelectedItem().toString());
        mDwelling.setOwnerName(ownerName.getText().toString());
        mDwelling.setOwnerAadhar(ownerAadhar.getText().toString());
        mDwelling.setAddress(address.getText().toString());
        mDwelling.setLandmark(landmark.getText().toString());
        mDwelling.setTown(town.getText().toString());
        mDwelling.setMandal(mandal.getText().toString());
        mDwelling.setDistrict(district.getText().toString());
        mDwelling.setPincode(pincode.getText().toString());
        mDwelling.setDwellignType(dwellingType.getSelectedItem().toString());
        mDwelling.setStructuralType(structuralType.getSelectedItem().toString());
        mDwelling.setAssessmentNo(assessmentNo.getText().toString());
        mDwelling.setUpdatedAt(System.currentTimeMillis());
        mDwelling.setOffset(1);
        mDwelling.setEmenities(emenities);

        mRealm.beginTransaction();
        mRealm.insertOrUpdate(mDwelling);
        mRealm.commitTransaction();

        t.setText("Saved Successfully!");
        t.show();
//        Toast.makeText(getActivity(), "Saved Successfully!", Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    private int getIndexOfItem(String item, String[] items) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0;
    }

    public void removeListItem() {
        mDwelling = activity.getIntent().getParcelableExtra(Dwelling.class.getName());

        if (mDwelling != null) {
            Dwelling dwellingsList = refRealm.where(Dwelling.class).equalTo("CompositePrimaryKey", mDwelling.getCompositePrimaryKey()).findFirst();
            if (dwellingsList != null) {
                refRealm.beginTransaction();
                dwellingsList.deleteFromRealm();
                refRealm.commitTransaction();
            }
        }
    }
}
