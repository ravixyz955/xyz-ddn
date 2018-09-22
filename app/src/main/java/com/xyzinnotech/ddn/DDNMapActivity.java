package com.xyzinnotech.ddn;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Projection;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.RasterLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.RasterSource;
import com.mapbox.mapboxsdk.style.sources.TileSet;
import com.xyzinnotech.ddn.network.model.Region;
import com.xyzinnotech.ddn.network.service.DDNAPIService;
import com.xyzinnotech.ddn.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;

public class DDNMapActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, LocationEngineListener {

    public static final String TILESET_LAYER = "tileset-layer";
    public static final String TILESET_SOURCE = "tileset-source";
    public static final String BUILDINGS_LINE_LAYER = "buildings-line-layer";
    public static final String BUILDINGS_FILL_LAYER = "buildings-fill-layer";
    public static final String BUILDINGS_HIGHLIGHT_LAYER = "buildings-highlight-layer";
    public static final String GEOJSON_SOURCE = "geojson-source";
    private MapView mapView;
    private MapboxMap map;
    private TextView ddnText;
    private TextView ddnBtn;
    private RelativeLayout ddnLayout;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;
    private Region mRegion;
    private DDNAPIService ddnapiService;
    private ArrayList<Feature> featureList;
    private Projection projection;
    private String selectedDdn;
    private Feature selectedFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_ddnmap);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (getIntent().hasExtra(Region.class.getName())) {
            mRegion = getIntent().getParcelableExtra(Region.class.getName());
            if (mRegion != null) {
                setTitle(mRegion.getName());
            }
        }

        ddnapiService = NetworkUtils.provideDDNAPIService(this);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        ddnText = findViewById(R.id.map_ddn_text);
        ddnBtn = findViewById(R.id.map_ddn_details);
        ddnLayout = findViewById(R.id.map_ddn_layout);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        DDNMapActivity.this.map = mapboxMap;
        projection = mapboxMap.getProjection();
        addTileset();
        navigateToRegion();
//        enableLocationPlugin();

        ddnapiService.getFeaturesList(mRegion.getProjectId()).enqueue(new Callback<ArrayList<Feature>>() {
            @Override
            public void onResponse(Response<ArrayList<Feature>> response, Retrofit retrofit) {
                featureList = response.body();

                FeatureCollection featureCollection = FeatureCollection.fromFeatures(featureList);
                GeoJsonSource geoJsonSource = new GeoJsonSource(GEOJSON_SOURCE, featureCollection);
                map.addSource(geoJsonSource);

                LineLayer lineLayer = new LineLayer(BUILDINGS_LINE_LAYER, GEOJSON_SOURCE);
                lineLayer.setProperties(
                        PropertyFactory.lineWidth(1f),
                        PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                );
//                map.addLayer(lineLayer);

                FillLayer fillLayer = new FillLayer(BUILDINGS_FILL_LAYER, GEOJSON_SOURCE);
                fillLayer.setProperties(PropertyFactory.fillOpacity(0.0f));
                map.addLayer(fillLayer);

                FillLayer highlightLayer = new FillLayer(BUILDINGS_HIGHLIGHT_LAYER, GEOJSON_SOURCE);
                highlightLayer.setProperties(
                        PropertyFactory.fillColor(Color.parseColor("#800000")),
                        PropertyFactory.fillOpacity(0.9f)
                );
//                highlightLayer.setProperties(
//                        fillExtrusionColor(rgba(200.0f, 200.0f, 200.0f, 1.0f)),
//                        fillExtrusionHeight(literal(10)),
//                        fillExtrusionBase(literal(0)),
//                        fillExtrusionOpacity(0.9f)
//                );
                highlightLayer.setFilter(eq(get("ddn"), literal("")));
                map.addLayer(highlightLayer);

                map.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        PointF pointf = map.getProjection().toScreenLocation(point);
                        RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);

                        List<Feature> features = map.queryRenderedFeatures(rectF, BUILDINGS_FILL_LAYER);
                        if (!features.isEmpty()) {
                            selectedFeature = features.get(0);
                            selectedDdn = features.get(0).getStringProperty("ddn");
                            ddnText.setText(selectedDdn);
                            highlightLayer.setFilter(eq(get("ddn"), literal(selectedDdn)));
                            LatLng latLng = projection.fromScreenLocation(pointf);
                            setCameraPosition(latLng.getLatitude(), latLng.getLongitude());
                            addMarker(selectedDdn, null, latLng);
                        } else {
                            selectedFeature = null;
                            selectedDdn = null;
                            ddnText.setText(null);
                            highlightLayer.setFilter(eq(get("ddn"), literal("")));
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void addTileset() {
        TileSet tileSet = new TileSet("2.2.0", "https://api.mapbox.com/v4/" + mRegion.getTilesetId() +
                "/{z}/{x}/{y}@2x.png?access_token=" + getResources().getString(R.string.mapbox_access_token));
        RasterSource rasterSource = new RasterSource(TILESET_SOURCE, tileSet, 256);
        map.addSource(rasterSource);

        RasterLayer rasterLayer = new RasterLayer(TILESET_LAYER, TILESET_SOURCE);
        map.addLayer(rasterLayer);
    }

    private void navigateToRegion() {
        if (map != null && mRegion != null) {
            Location location = new Location("");
            location.setLatitude(mRegion.getCenter()[1]);
            location.setLongitude(mRegion.getCenter()[0]);
            setCameraPosition(location);
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            locationPlugin.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void addMarker(String title, String subtitle, LatLng latLng) {
        List<Marker> markers = map.getMarkers();
        for (Marker m : markers) {
            m.remove();
        }
        IconFactory iconFactory = IconFactory.getInstance(this);
        Icon icon = iconFactory.fromResource(R.drawable.map_marker_dark);

        map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(subtitle)
                .icon(icon));
    }

    private void setCameraPosition(double lat, double lng) {
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        setCameraPosition(location);
    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 18));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ddn_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download_map) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void detailsView(View view) {
        if (selectedFeature != null) {
            Intent intent = new Intent(this, DwellingsListActivity.class);
            intent.putExtra("ddn", selectedDdn);
            intent.putExtra("project", mRegion.getProjectId());
            intent.putExtra("feature", selectedFeature.toJson());
            startActivity(intent);
        }
    }
}
