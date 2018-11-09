package com.xyzinnotech.ddn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CreateParcelActivity extends AppCompatActivity {
    private WebView parcel_webview;
    private String mapbox_id;
    private static final String PARCEL_BASE_URL = "https://xyzinno.tech/ddn/create-parcel/";
    private String parcel_final_url;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parcel);

        parcel_webview = (WebView) findViewById(R.id.parcel_webview);
        parcel_webview.setWebViewClient(new MyBrowser());
        parcel_webview.getSettings().setLoadsImagesAutomatically(true);
        parcel_webview.getSettings().setJavaScriptEnabled(true);
        parcel_webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (getIntent().hasExtra("mapboxId") && getIntent().hasExtra("title")) {
            title = getIntent().getStringExtra("title");
            mapbox_id = getIntent().getStringExtra("mapboxId");
            mapbox_id = "5bbdd349effbaa25503119c5";
            parcel_final_url = PARCEL_BASE_URL + mapbox_id;

            setTitle(title);
            parcel_webview.loadUrl(parcel_final_url);
            Log.d("URL", "onCreate: " + parcel_final_url);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
