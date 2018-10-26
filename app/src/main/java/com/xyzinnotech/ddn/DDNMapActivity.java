package com.xyzinnotech.ddn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Projection;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.RasterLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.RasterSource;
import com.mapbox.mapboxsdk.style.sources.TileSet;
import com.xyzinnotech.ddn.model.Dwelling;
import com.xyzinnotech.ddn.network.model.Region;
import com.xyzinnotech.ddn.network.service.DDNAPIService;
import com.xyzinnotech.ddn.utils.DataUtils;
import com.xyzinnotech.ddn.utils.NetworkUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;


public class DDNMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {
    private static final String SELECTED_GEOJSON_SOURCE = "selected_geojson_source";
    private static final String SELECTED_BUILDINGS_HIGHLIGHT_LAYER = "selected_buildings_highlight_layer";
    private static final String CIRCLELAYER_GEOJSON_SOURCE = "circlelayer_geojson_source";
    private static final String DWELLINGS_HIGHLIGHT_LAYER = "dwellings_highlight_layer";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Marker marker;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    static double latitude;
    static double longitude;
    com.mapbox.mapboxsdk.annotations.Marker mCurrLocationMarker;
    String tile_id = null;
    private boolean isEndNotified;
    private ProgressBar progressBar;
    String id;
    private OfflineRegion offlineRegion;
    private static final String TAG = "MSPSP";
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    OfflineRegion[] of;
    OfflineTilePyramidRegionDefinition definition;
    private OfflineManager offlineManager;
    public static final String BUILDINGS_LINE_LAYER = "buildings-line-layer";
    public static final String BUILDINGS_FILL_LAYER = "buildings-fill-layer";
    public static final String BUILDINGS_HIGHLIGHT_LAYER = "buildings-highlight-layer";
    public static final String GEOJSON_SOURCE = "geojson-source";
    private MapboxMap map;
    private TextView ddnText;
    private Region mRegion;
    private DDNAPIService ddnapiService;
    private ArrayList<Feature> featureList;
    private Projection projection;
    private String selectedDdn;
    private Feature selectedFeature;
    private static Toast t;
    private SharedPreferences pref, tilePref;
    private SharedPreferences.Editor editor;
    ArrayList<Region> regions;
    private int index, position;
    private static OfflineRegion[] offlineRegionsList;
    private String regionName;
    private CameraPosition cameraPosition;
    private int roadDdn;
    private Realm mRealm;
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddnmap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRealm = Realm.getDefaultInstance();

        pref = getApplicationContext().getSharedPreferences("OfflineFeatureList", Context.MODE_PRIVATE);
        tilePref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (getIntent().hasExtra("index")) {
            index = getIntent().getIntExtra("index", 0);

        }

        if (getIntent().hasExtra(Region.class.getName())) {
            mRegion = getIntent().getParcelableExtra(Region.class.getName());
            if (mRegion != null) {
                setTitle(mRegion.getName());
                regionName = mRegion.getName();
            }
        } else {
            String jsonArray = tilePref.getString("jsonArray", null);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Region>>() {
                }.getType();
                regions = gson.fromJson(jsonArray, type);
                setTitle(regions.get(index).getName());
                regionName = regions.get(index).getName();
            }
        }

        ddnapiService = NetworkUtils.provideDDNAPIService(this);

        t = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        t.getView().setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        ddnText = findViewById(R.id.map_ddn_text);
        progressBar = findViewById(R.id.offline_download_progress_bar);

        if (NetworkUtils.isConnectingToInternet(DDNMapActivity.this)) {
            OfflineManager offlineManager = OfflineManager.getInstance(DDNMapActivity.this);
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                @Override
                public void onList(OfflineRegion[] offlineRegions) {
                    offlineRegionsList = offlineRegions;
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error: " + error);
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            OfflineManager offlineManager = OfflineManager.getInstance(DDNMapActivity.this);
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                @Override
                public void onList(final OfflineRegion[] offlineRegions) {
                    if (offlineRegions == null || offlineRegions.length == 0) {
                        t.setText("No Regions Yet");
                        t.show();
                        return;
                    } else {
                        offlineRegionsList = offlineRegions;
                        position = getMapPosition();
                        if (position >= 0) {
                            Log.d(TAG, "onList: " + position);
                            definition = (OfflineTilePyramidRegionDefinition) offlineRegions[position].getDefinition();
                            if (definition != null) {
                                // Get the region bounds and zoom
                                LatLngBounds bounds = (definition.getBounds());
                                double regionZoom = (definition.getMaxZoom());

                                // Create new camera position
                                cameraPosition = new CameraPosition.Builder()
                                        .target(bounds.getCenter())
                                        .zoom(18)
                                        .build();
                            }
                        } else {
                            t.setText("No Offline Map found");
                            t.show();
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error: " + error);
                }
            });
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        offlineManager = OfflineManager.getInstance(DDNMapActivity.this);
        this.map = mapboxMap;
        projection = mapboxMap.getProjection();
        map.clear();
        // navigateToRegion();
        if (mRegion != null) {
            tile_id = mRegion.getTilesetId();
        } else {
            tile_id = regions.get(index).getTilesetId();
            Log.d("TILEID", "onMapReady: " + tile_id);
        }

        if (NetworkUtils.isConnectingToInternet(this)) {

            addTileset();
            navigateToRegion();
            ddnapiService.getFeaturesList(mRegion.getProjectId()).enqueue(new Callback<ArrayList<Feature>>() {
                @Override
                public void onResponse(Response<ArrayList<Feature>> response, Retrofit retrofit) {
                    editor = pref.edit();
//                    editor.clear().commit();
                    featureList = response.body();
                    FeatureCollection featureCollection = FeatureCollection.fromFeatures(featureList);
                    if (!pref.contains(mRegion.getName())) {
//                        editor.clear();
                        editor.putString(mRegion.getName(), featureCollection.toJson());
                    }
                    editor.commit();

                    GeoJsonSource geoJsonSource = new GeoJsonSource(GEOJSON_SOURCE, featureCollection);
                    map.addSource(geoJsonSource);
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        } else {

            if (pref.contains(regionName)) {

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                addOfflineTileSet();
//                navigateToOfflineRegion();
                String featureListStr = pref.getString(regionName, null);

                if (featureListStr != null) {
                    try {

                        FeatureCollection featureCollection = FeatureCollection.fromJson(featureListStr);
                        GeoJsonSource geoJsonSource = new GeoJsonSource(GEOJSON_SOURCE, featureCollection);
                        map.addSource(geoJsonSource);
                    } catch (Exception e) {
                        Log.d(TAG, "onMapReady: " + e.getMessage());
                    }
                }
            }
        }

        LineLayer lineLayer = new LineLayer(BUILDINGS_LINE_LAYER, GEOJSON_SOURCE);
        lineLayer.setProperties(
                PropertyFactory.lineWidth(1f),
                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
        );

        FillLayer fillLayer = new FillLayer(BUILDINGS_FILL_LAYER, GEOJSON_SOURCE);
        fillLayer.setProperties(PropertyFactory.fillOpacity(0.0f));
        map.addLayer(fillLayer);

        FillLayer highlightLayer = new FillLayer(BUILDINGS_HIGHLIGHT_LAYER, GEOJSON_SOURCE);
        highlightLayer.setProperties(
                PropertyFactory.fillColor(Color.parseColor("#800000")),
                PropertyFactory.fillOpacity(0.9f)
        );

        highlightLayer.setFilter(eq(get("ddn"), literal("")));
        map.addLayer(highlightLayer);

        map.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                PointF pointf = map.getProjection().toScreenLocation(point);
                RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);

                if (map.getSource(SELECTED_GEOJSON_SOURCE) != null) {
                    map.removeLayer(SELECTED_BUILDINGS_HIGHLIGHT_LAYER);
                    map.removeSource(SELECTED_GEOJSON_SOURCE);
                }

                List<Feature> features = map.queryRenderedFeatures(rectF, BUILDINGS_FILL_LAYER);
                if (!features.isEmpty()) {
                    selectedFeature = features.get(0);
                    if (features.get(0).hasProperty("ddn")) {
                        selectedDdn = features.get(0).getStringProperty("ddn");
                    }
                    if (selectedDdn != null && selectedDdn.contains("-")) {
                        highlightLayer.setFilter(eq(get("ddn"), literal(selectedDdn)));

                        String[] ddnParts = selectedDdn.split("-");
                        roadDdn = ddnParts[0].length();
                        if (roadDdn == 3) {
                            showListOfBuildings(ddnParts[0]);
                        }

                    } else {
                        highlightLayer.setFilter(eq(get("ddn"), literal(selectedDdn)));
                        roadDdn = selectedDdn.length();
                        if (roadDdn == 3) {
                            showListOfBuildings(selectedDdn);
                        }
                    }
                    ddnText.setText(selectedDdn);
                    LatLng latLng = projection.fromScreenLocation(pointf);
                    setCameraPosition(latLng.getLatitude(), latLng.getLongitude());
                    addMarker(selectedDdn, null, latLng, highlightLayer);
                    ddnText.setTextColor(Color.parseColor("#A9A9A9"));

                } else {
                    selectedFeature = null;
                    selectedDdn = null;
                    ddnText.setText(null);
                    highlightLayer.setFilter(eq(get("ddn"), literal("")));
                    ddnText.setTextColor(Color.parseColor("#A9A9A9"));
                }
            }
        });
    }

    private void showListOfBuildings(String selectedDdn) {
        ArrayList<Feature> features = featureList;
        ArrayList<Feature> selectedFeatures = new ArrayList<>();

        if (features != null) {

            for (Feature feature : features) {
                if (feature != null) {
                    Log.d("ASDFGH", feature.toJson());
                    if (feature.hasProperty("ddn")) {

                        if (feature.getStringProperty("ddn").length() > 3 && !feature.getStringProperty("ddn").contains("-")) {

                            if (feature.getStringProperty("ddn").contains(selectedDdn)) {
                                selectedFeatures.add(feature);
                            }
                        }
                    }
                }
            }
            highlightSelectedFeatureList(selectedFeatures);
        }
    }

    private void highlightSelectedFeatureList(ArrayList<Feature> selectedFeatures) {

        if (map.getSource(SELECTED_GEOJSON_SOURCE) != null) {
            map.removeLayer(SELECTED_BUILDINGS_HIGHLIGHT_LAYER);
            map.removeSource(SELECTED_GEOJSON_SOURCE);
        }
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(selectedFeatures);

        GeoJsonSource geoJsonSource = new GeoJsonSource(SELECTED_GEOJSON_SOURCE, featureCollection);
        map.addSource(geoJsonSource);

        FillLayer highlightLayer = new FillLayer(SELECTED_BUILDINGS_HIGHLIGHT_LAYER, SELECTED_GEOJSON_SOURCE);
        highlightLayer.setProperties(
                PropertyFactory.fillColor(Color.parseColor("#FF7700")),
                PropertyFactory.fillOpacity(0.9f)
        );

        map.addLayer(highlightLayer);
    }


    private void addTileset() {
        TileSet tileSet = new TileSet("2.2.0", "https://api.mapbox.com/v4/" + tile_id + "/{z}/{x}/{y}@2x.png?access_token=" + getString(R.string.mapbox_access_token));
        RasterSource rasterSource1 = new RasterSource("tileSet", tileSet, 256);
        map.addSource(rasterSource1);
        RasterLayer rasterLayer1 = new RasterLayer("dd", "tileSet");
        map.addLayer(rasterLayer1);

    }

    private void addOfflineTileSet() {
        TileSet tileSet = new TileSet("2.2.0", "https://api.mapbox.com/v4/" + tile_id + "/{z}/{x}/{y}@2x.png?access_token=" + getString(R.string.mapbox_access_token));
        RasterSource rasterSource1 = new RasterSource("tileSet", tileSet, 256);
        map.addSource(rasterSource1);
        RasterLayer rasterLayer1 = new RasterLayer("dd", "tileSet");
        map.addLayer(rasterLayer1);
    }

    private void navigateToRegion() {
        if (map != null && mRegion != null) {
            Location location = new Location("");
            location.setLatitude(mRegion.getCenter()[1]);
            location.setLongitude(mRegion.getCenter()[0]);
            setCameraPosition(location);
        }
    }

    private void navigateToOfflineRegion() {
        Region region = regions.get(position);
        if (map != null && region != null) {
            Location location = new Location("");
            location.setLatitude(region.getCenter()[1]);
            location.setLongitude(region.getCenter()[0]);
            setCameraPosition(location);
        }
    }

    private void addMarker(String title, String subtitle, LatLng latLng, FillLayer highlightLayer) {
        List<Marker> markers = map.getMarkers();
        for (Marker m : markers) {
            m.remove();
        }
        IconFactory iconFactory = IconFactory.getInstance(this);
        Icon icon = iconFactory.fromResource(R.drawable.map_marker_dark);

        marker = map.addMarker(new MarkerOptions()
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


    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            initializeLocationLayer();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer() {
        locationLayerPlugin = new LocationLayerPlugin(mapView, mapboxMap, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setLocationEngine(locationEngine);
        locationLayerPlugin.setRenderMode(RenderMode.COMPASS);
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
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

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 18));
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        if (location != null && mCurrLocationMarker != null) {
            originLocation = location;
            setCameraPosition(location);
            mCurrLocationMarker.remove();
        }

        latitude = 16.9891;
        longitude = 82.2475;

        com.mapbox.mapboxsdk.geometry.LatLng latLng = new com.mapbox.mapboxsdk.geometry.LatLng(DataUtils.latitude, DataUtils.longitude);


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        mCurrLocationMarker = mapboxMap.addMarker(markerOptions);
    }

    @Override
    @SuppressWarnings("missingpermission")
    public void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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
            enableLocation();
        }
    }

    private void downloadRegion() {

        if (getMapPosition() >= 0) {

            t.setText("Map already downloaded!");
            t.show();

        } else {

            resumeDownload();
        }
    }

    private void resumeDownload() {

        startProgress();
        String styleUrl = map.getStyleUrl();
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        double minZoom = 0;
        double maxZoom = 15;
        float pixelRatio = this.getResources().getDisplayMetrics().density;
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                styleUrl, bounds, minZoom, maxZoom, pixelRatio);

        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, mRegion.getName());
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }

        offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
            @Override
            public void onCreate(OfflineRegion offlineRegion) {
                Log.d(TAG, "Offline region created: " + "");
                DDNMapActivity.this.offlineRegion = offlineRegion;
                launchDownload();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error: " + error);
            }
        });
    }

    private int getMapPosition() {
        int i = 0;
        if (offlineRegionsList != null) {

            for (OfflineRegion offlineRegion : offlineRegionsList) {
                if (getRegionName(offlineRegion).equalsIgnoreCase(regionName)) {
                    return i;
                }
                i += 1;
            }
        }
        return -1;
    }

    private void launchDownload() {
        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
            @Override
            public void onStatusChanged(OfflineRegionStatus status) {
                // Compute a percentage
                double percentage = status.getRequiredResourceCount() >= 0
                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                        0.0;

                if (status.isComplete()) {
                    // Download complete
                    endProgress(getString(R.string.simple_offline_end_progress_success));
                    return;
                } else if (status.isRequiredResourceCountPrecise()) {
                    // Switch to determinate state
                    setPercentage((int) Math.round(percentage));
                }

                // Log what is being currently downloaded
                Log.d(TAG, String.format("%s/%s resources; %s bytes downloaded.",
                        String.valueOf(status.getCompletedResourceCount()),
                        String.valueOf(status.getRequiredResourceCount()),
                        String.valueOf(status.getCompletedResourceSize())));
            }

            @Override
            public void onError(OfflineRegionError error) {
                Log.e(TAG, "onError reason: " + error.getReason());
                Log.e(TAG, "onError message: " + error.getMessage());
            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit) {
                Log.e("LIMIT", "Mapbox tile count limit exceeded: " + limit);
            }
        });

        // Change the region state
        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
    }

    // Progress bar methods
    private void startProgress() {

        // Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) {
            return;
        }

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        t.setText(message);
        t.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ddn_map, menu);
        if (!NetworkUtils.isConnectingToInternet(this)) {
            MenuItem download_btn = menu.getItem(0);
            download_btn.setVisible(false);
        }
        MenuItem checkable = menu.findItem(R.id.action_showlist_dwelling);
        checkable.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_download_map) {
            downloadRegion();
            return true;
        } else if (id == R.id.action_showlist_dwelling) {
            showAvailableDwelling(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAvailableDwelling(MenuItem item) {

        isChecked = !item.isChecked();
        int icon;
        item.setChecked(isChecked);

        if (item.isChecked()) {
            isChecked = !item.isChecked();
            item.setChecked(!isChecked);
            icon = R.drawable.ic_visibility_on_white_24dp;

            showDwelling();
        } else {
            item.setChecked(isChecked);
            icon = R.drawable.ic_visibility_off_white_24dp;
            hideLayer();
        }

        item.setIcon(icon);
    }

    private void hideLayer() {
        if (map.getSource(CIRCLELAYER_GEOJSON_SOURCE) != null) {
            map.removeLayer(DWELLINGS_HIGHLIGHT_LAYER);
            map.removeSource(CIRCLELAYER_GEOJSON_SOURCE);
        }
    }

    private void showDwelling() {
        ArrayList<Feature> features = featureList;
        ArrayList<Feature> dwellingFeatures = new ArrayList<>();

        int count;

        if (map.getSource(CIRCLELAYER_GEOJSON_SOURCE) != null) {
            map.removeLayer(DWELLINGS_HIGHLIGHT_LAYER);
            map.removeSource(CIRCLELAYER_GEOJSON_SOURCE);
        }
        if (features != null) {

            for (Feature feature : features) {
                if (feature != null) {
                    if (feature.hasProperty("ddn")) {
                        String searchDdn = feature.getStringProperty("ddn");
                        RealmResults<Dwelling> dwellingsList = mRealm.where(Dwelling.class).equalTo("ddn", searchDdn).equalTo("offset", 1).findAll();
                        count = dwellingsList.size();
                        if (count >= 1) {
                            dwellingFeatures.add(feature);
                        }
                    }
                }
            }
            if (dwellingFeatures.size() > 0) {

                FeatureCollection featureCollection = FeatureCollection.fromFeatures(dwellingFeatures);
                GeoJsonSource geoJsonSource = new GeoJsonSource(CIRCLELAYER_GEOJSON_SOURCE, featureCollection);
                map.addSource(geoJsonSource);

                FillLayer highlightLayer = new FillLayer(DWELLINGS_HIGHLIGHT_LAYER, CIRCLELAYER_GEOJSON_SOURCE);
                highlightLayer.setProperties(
                        PropertyFactory.fillColor(Color.parseColor("#15F740")),
                        PropertyFactory.fillOpacity(0.9f)
                );
                map.addLayer(highlightLayer);

            } else {
                t.setText("No dwelling found");
                t.show();
            }
        }
    }

    private String getRegionName(OfflineRegion offlineRegion) {
        // Get the region name from the offline region metadata
        String regionName;

        try {
            byte[] metadata = offlineRegion.getMetadata();
            String json = new String(metadata, JSON_CHARSET);
            JSONObject jsonObject = new JSONObject(json);
            regionName = jsonObject.getString(JSON_FIELD_REGION_NAME);
        } catch (Exception exception) {
            Log.e(TAG, "Failed to decode metadata: " + exception.getMessage());
            regionName = String.format(getString(R.string.region_name), offlineRegion.getID());
        }
        return regionName;
    }

    public void detailsView(View view) {
        if (selectedFeature != null) {
            Intent intent = new Intent(this, DwellingsListActivity.class);
            intent.putExtra("ddn", selectedDdn);
            if (NetworkUtils.isConnectingToInternet(this)) {
                intent.putExtra("project", mRegion.getProjectId());
            }
            intent.putExtra("feature", selectedFeature.toJson());
            startActivity(intent);
            ddnText.setTextColor(Color.parseColor("#A9A9A9"));

        } else if (ddnText.getText().toString().equalsIgnoreCase("Click on building") || ddnText.getText().toString().equalsIgnoreCase("") || ddnText.getText().toString() == null) {
            ddnText.setText("Please click on building");
            ddnText.setTextColor(Color.RED);
        }
    }
}