package com.xyzinnotech.ddn;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.plugins.offline.model.NotificationOptions;
import com.mapbox.mapboxsdk.plugins.offline.model.OfflineDownloadOptions;
import com.mapbox.mapboxsdk.plugins.offline.offline.OfflineDownloadChangeListener;
import com.mapbox.mapboxsdk.plugins.offline.offline.OfflineDownloadService;
import com.mapbox.mapboxsdk.plugins.offline.offline.OfflineDownloadStateReceiver;
import com.mapbox.mapboxsdk.plugins.offline.offline.OfflinePlugin;
import com.mapbox.mapboxsdk.plugins.offline.utils.OfflineUtils;
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
import com.xyzinnotech.ddn.utils.Utils;

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
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;

public class DDNMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, OfflineDownloadChangeListener {
    private static final String SELECTED_GEOJSON_SOURCE = "selected_geojson_source";
    private static final String SELECTED_BUILDINGS_HIGHLIGHT_LAYER = "selected_buildings_highlight_layer";
    private static final String CIRCLELAYER_GEOJSON_SOURCE = "circlelayer_geojson_source";
    private static final String DWELLINGS_HIGHLIGHT_LAYER = "dwellings_highlight_layer";
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    static double latitude;
    static double longitude;
    com.mapbox.mapboxsdk.annotations.Marker mCurrLocationMarker;
    private String mapbox_id = null;
    private ProgressBar progressBar;
    private String id;
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    OfflineTilePyramidRegionDefinition definition;
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
    private ArrayList<Region> regions;
    private int selectedRegionIndex, selectedRegionOfflinePosition;
    private static OfflineRegion[] offlineRegionsList;
    private String regionName;
    private CameraPosition cameraPosition;
    private int roadDdn;
    private Realm mRealm;
    private boolean isChecked = false;
    private OfflinePlugin offlinePlugin;
    private IntentFilter mIntentfilter;
    OfflineDownloadStateReceiver offlineDownloadStateReceiver;
    private static FillLayer highlightLayer;
    private double selectedRegionZoom;
    private Intent OfflineDownloadServiceIntent;
    private NotificationManagerCompat notificationManager;
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private Utils prefSharedUtils, tilePrefSharedUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddnmap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        offlinePlugin = OfflinePlugin.getInstance(this);
        mRealm = Realm.getDefaultInstance();
        offlineDownloadStateReceiver = new OfflineDownloadStateReceiver();
        OfflineDownloadServiceIntent = new Intent(this, OfflineDownloadService.class);
        notificationManager = NotificationManagerCompat.from(this);

        prefSharedUtils = Utils.init(this, "OfflineFeatureList");
        tilePrefSharedUtils = Utils.init(this, "MyPref");
        mIntentfilter = new IntentFilter("com.mapbox.mapboxsdk.plugins.offline");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (getIntent().hasExtra("selectedRegionIndex")) {
            selectedRegionIndex = getIntent().getIntExtra("selectedRegionIndex", 0);
        }

        if (getIntent().hasExtra(Region.class.getName())) {
            mRegion = getIntent().getParcelableExtra(Region.class.getName());
            if (mRegion != null) {
                setTitle(mRegion.getName());
                regionName = mRegion.getName();
            }
        } else {
            String jsonArray = tilePrefSharedUtils.get("regionsArray");
            if (jsonArray != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Region>>() {
                }.getType();
                regions = gson.fromJson(jsonArray, type);
                setTitle(regions.get(selectedRegionIndex).getName());
                regionName = regions.get(selectedRegionIndex).getName();
            }
        }

        ddnapiService = NetworkUtils.provideDDNAPIService(this);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        ddnText = findViewById(R.id.map_ddn_text);
        progressBar = findViewById(R.id.offline_download_progress_bar);

        if (NetworkUtils.isConnectingToInternet(DDNMapActivity.this)) {
            getOfflineRegionList();
        } else {
            progressBar.setVisibility(View.GONE);
            OfflineManager offlineManager = OfflineManager.getInstance(DDNMapActivity.this);
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                @Override
                public void onList(final OfflineRegion[] offlineRegions) {
                    if (offlineRegions == null || offlineRegions.length == 0) {
                        Utils.showToast(DDNMapActivity.this, "No Regions Yet");
                        return;
                    } else {
                        offlineRegionsList = offlineRegions;
                        selectedRegionOfflinePosition = getMapPosition();
                        if (selectedRegionOfflinePosition >= 0) {
                            definition = (OfflineTilePyramidRegionDefinition) offlineRegions[selectedRegionOfflinePosition].getDefinition();
                            if (definition != null) {
                                LatLngBounds bounds = (definition.getBounds());
                                double regionZoom = (definition.getMaxZoom());

                                cameraPosition = new CameraPosition.Builder()
                                        .target(bounds.getCenter())
                                        .zoom(18)
                                        .build();
                            }
                        } else {
                            Utils.showToast(DDNMapActivity.this, "No Offline Map found");
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e("onListError", "Error: " + error);
                }
            });
        }
    }

    private final OfflineRegion.OfflineRegionDeleteCallback offlineRegionDeleteCallback =
            new OfflineRegion.OfflineRegionDeleteCallback() {
                @Override
                public void onDelete() {
                    Utils.showToast(DDNMapActivity.this, getRegionName(offlineRegionsList[0]) + " " + " Offline map removed.");
                }

                @Override
                public void onError(String error) {
                    Utils.showToast(DDNMapActivity.this, "Error getting offline region state: " + error);
                }
            };

    private void getOfflineRegionList() {
        OfflineManager offlineManager = OfflineManager.getInstance(DDNMapActivity.this);
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(OfflineRegion[] offlineRegions) {
                offlineRegionsList = offlineRegions;
            }

            @Override
            public void onError(String error) {
                Log.e("onListError", "Error: " + error);
            }
        });
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.map = mapboxMap;
        projection = mapboxMap.getProjection();
        map.clear();
        if (mRegion != null) {
            mapbox_id = mRegion.getMapboxId();
        } else {
            mapbox_id = regions.get(selectedRegionIndex).getTilesetId();
        }

        if (NetworkUtils.isConnectingToInternet(this)) {

            addTileset();
            navigateToRegion();
            ddnapiService.getFeaturesList(mRegion.getProjectId()).enqueue(new Callback<ArrayList<Feature>>() {
                @Override
                public void onResponse(Response<ArrayList<Feature>> response, Retrofit retrofit) {
                    featureList = response.body();
                    FeatureCollection featureCollection = FeatureCollection.fromFeatures(featureList);
                    if (!prefSharedUtils.contains(mRegion.getName())) {
                        prefSharedUtils.put(mRegion.getName(), featureCollection.toJson());
                    }
                    prefSharedUtils.finish();
                    GeoJsonSource geoJsonSource = new GeoJsonSource(GEOJSON_SOURCE, featureCollection);
                    map.addSource(geoJsonSource);
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        } else {

            if (prefSharedUtils.contains(regionName)) {
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                addOfflineTileSet();
//                navigateToOfflineRegion();
                String featureListStr = prefSharedUtils.get(regionName);

                if (featureListStr != null) {
                    try {
                        FeatureCollection featureCollection = FeatureCollection.fromJson(featureListStr);
                        GeoJsonSource geoJsonSource = new GeoJsonSource(GEOJSON_SOURCE, featureCollection);
                        map.addSource(geoJsonSource);
                    } catch (Exception e) {
                        Log.d("onListError", "onMapReady: " + e.getMessage());
                    }
                }
            }
        }

        addLineLayer();
        addFillLayer();
        addHighlightLayer();

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

    private void addHighlightLayer() {
        highlightLayer = new FillLayer(BUILDINGS_HIGHLIGHT_LAYER, GEOJSON_SOURCE);
        highlightLayer.setProperties(
                PropertyFactory.fillColor(Color.parseColor("#800000")),
                PropertyFactory.fillOpacity(0.9f)
        );

        highlightLayer.setFilter(eq(get("ddn"), literal("")));
        map.addLayer(highlightLayer);
    }

    private void addFillLayer() {
        FillLayer fillLayer = new FillLayer(BUILDINGS_FILL_LAYER, GEOJSON_SOURCE);
        fillLayer.setProperties(PropertyFactory.fillOpacity(0.0f));
        map.addLayer(fillLayer);
    }

    private void addLineLayer() {
        LineLayer lineLayer = new LineLayer(BUILDINGS_LINE_LAYER, GEOJSON_SOURCE);
        lineLayer.setProperties(
                PropertyFactory.lineWidth(1f),
                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
        );
    }

    private void showListOfBuildings(String selectedDdn) {
        ArrayList<Feature> features = featureList;
        ArrayList<Feature> selectedFeatures = new ArrayList<>();

        if (features != null) {

            for (Feature feature : features) {
                if (feature != null) {
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
        int tileSize;
        TileSet tileSet = new TileSet("2.2.0", "https://api.mapbox.com/v4/" + mapbox_id + "/{z}/{x}/{y}@2x.png?access_token=" + getString(R.string.mapbox_access_token));
        if (selectedRegionIndex == 0) {
            tileSize = 100;
        } else {
            tileSize = 100;
        }
        RasterSource rasterSource1 = new RasterSource("tileSet", tileSet, tileSize);
        map.addSource(rasterSource1);
        RasterLayer rasterLayer1 = new RasterLayer("dd", "tileSet");
        map.addLayer(rasterLayer1);
    }

    private void addOfflineTileSet() {
        TileSet tileSet = new TileSet("2.2.0", "https://api.mapbox.com/v4/" + mapbox_id + "/{z}/{x}/{y}@2x.png?access_token=" + getString(R.string.mapbox_access_token));
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
        Region region = regions.get(selectedRegionOfflinePosition);
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
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
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

        if (location != null && mCurrLocationMarker != null) {
            setCameraPosition(location);
            mCurrLocationMarker.remove();
        }

        latitude = 16.9891;
        longitude = 82.2475;

        com.mapbox.mapboxsdk.geometry.LatLng latLng = new com.mapbox.mapboxsdk.geometry.LatLng(DataUtils.latitude, DataUtils.longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        mCurrLocationMarker = map.addMarker(markerOptions);
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
        offlinePlugin.addOfflineDownloadStateChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        this.registerReceiver(offlineDownloadStateReceiver, mIntentfilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (offlineDownloadStateReceiver != null) {
            unregisterReceiver(offlineDownloadStateReceiver);
            offlineDownloadStateReceiver = null;
        }
        stopService(OfflineDownloadServiceIntent);
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
        offlinePlugin.removeOfflineDownloadStateChangeListener(this);
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

    private void resumeDownload() {

        Location location = new Location("");
        if (map != null && mRegion != null) {
            location.setLatitude(mRegion.getCenter()[1]);
            location.setLongitude(mRegion.getCenter()[0]);
        }
        String styleUrl = map.getStyleUrl();
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        if (selectedRegionIndex == 0) {
            selectedRegionZoom = 14.5;
        } else {
            selectedRegionZoom = 16;
        }

        if (map.getSource("tileSet") != null) {
            map.removeLayer("dd");
            map.removeSource("tileSet");
        }
        addTileset();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), selectedRegionZoom));

        double minZoom = 14;
        double maxZoom = 22;
        float pixelRatio = this.getResources().getDisplayMetrics().density;
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                styleUrl, bounds, minZoom, maxZoom, pixelRatio);

        // Customize the download notification's appearance
        NotificationOptions notificationOptions = NotificationOptions.builder(this)
                .smallIconRes(R.drawable.mapbox_logo_icon)
                .returnActivity(DDNMapActivity.class.getName())
                .build();

        Runnable delayedTask = new Runnable() {
            @Override
            public void run() {
                // Start downloading the map tiles for offline use
                offlinePlugin.startDownload(
                        OfflineDownloadOptions.builder()
                                .definition(definition)
                                .metadata(OfflineUtils.convertRegionName(regionName))
                                .notificationOptions(notificationOptions)
                                .build()
                );
            }
        };
        mainThreadHandler.postDelayed(delayedTask, 5000);
    }

    private int getMapPosition() {

        int i = 0;
        if (offlineRegionsList.length >= 0) {

            for (OfflineRegion offlineRegion : offlineRegionsList) {
                if (getRegionName(offlineRegion).equalsIgnoreCase(regionName)) {
                    return i;
                }
                i += 1;
            }
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ddn_map, menu);
        MenuItem download_btn = menu.getItem(0);
        if (!NetworkUtils.isConnectingToInternet(this)) {
            download_btn.setVisible(false);
        } else {
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

            if (getMapPosition() >= 0) {
                Utils.showToast(this, "Map already downloaded!");
            } else {
                if (offlineRegionsList.length >= 1) {
                    offlineRegionsList[0].delete(offlineRegionDeleteCallback);
                    resumeDownload();
                } else {
                    resumeDownload();
                }
            }
            return true;
        } else if (id == R.id.action_showlist_dwelling) {
            showAvailableDwelling(item);
            return true;
        } else if (id == R.id.action_add_parcel) {
//            addParcel();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addParcel() {
        Intent intent = new Intent(this, CreateParcelActivity.class);
        intent.putExtra("mapboxId", mapbox_id);
        intent.putExtra("title", regionName);
        startActivity(intent);
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
                Utils.showToast(this, "No dwelling found");
            }
        }
    }

    private String getRegionName(OfflineRegion offlineRegion) {
        // Get the region name from the offline region metadata
        String regionName = null;
        if (offlineRegion.getID() != 0) {

            try {
                byte[] metadata = offlineRegion.getMetadata();
                String json = new String(metadata, JSON_CHARSET);
                JSONObject jsonObject = new JSONObject(json);
                regionName = jsonObject.getString(JSON_FIELD_REGION_NAME);
            } catch (Exception exception) {
                Log.e("MetaDataError", "Failed to decode metadata: " + exception.getMessage());
                regionName = String.format(getString(R.string.region_name), offlineRegion.getID());
            }
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

    @Override
    public void onCreate(OfflineDownloadOptions offlineDownload) {

    }

    @Override
    public void onSuccess(OfflineDownloadOptions offlineDownload) {
        Utils.showToast(this, "Map downloaded successfully!");
        notificationManager.cancel(offlineDownload.uuid().intValue());
        stopService(OfflineDownloadServiceIntent);
        getOfflineRegionList();
    }

    @Override
    public void onCancel(OfflineDownloadOptions offlineDownload) {
        stopService(OfflineDownloadServiceIntent);
    }

    @Override
    public void onError(OfflineDownloadOptions offlineDownload, String error, String message) {
        Log.d("MapDownloadError:", "onError: " + error + message);
    }

    @Override
    public void onProgress(OfflineDownloadOptions offlineDownload, int progress) {

    }
}