package com.xyzinnotech.ddn;

import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;
import com.xyzinnotech.ddn.utils.GoogleApiHelper;

public class DDNApplication extends Application {

    private GoogleApiHelper googleApiHelper;

    private static DDNApplication mInstance;

    @Override
    public void onCreate() {

        super.onCreate();

        mInstance = this;

        googleApiHelper = new GoogleApiHelper(mInstance);

        Mapbox.getInstance(getApplicationContext(), getResources().getString(R.string.mapbox_access_token));
    }

    public static synchronized DDNApplication getInstance() {

        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {

        return this.googleApiHelper;
    }

    public static GoogleApiHelper getGoogleApiHelper() {

        return getInstance().getGoogleApiHelperInstance();
    }
}
