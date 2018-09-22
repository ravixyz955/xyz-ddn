package com.xyzinnotech.ddn.utils;

import android.graphics.Color;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.MultiPolygon;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.turf.TurfMeasurement;
import com.mapbox.turf.TurfMeta;
import com.mapbox.turf.TurfMisc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by apple on 23/03/18.
 */

public class MapboxUtils {

    public static PolygonOptions createPolygonOptions(List<List<Double[]>> coordinates) {
        PolygonOptions polygonOptions = new PolygonOptions();
        for(Double[] position : coordinates.get(0)) {
            LatLng point = new LatLng(position[1], position[0]);
            polygonOptions.add(point);
        }
        return polygonOptions;
    }

    public static void addPolygonToMap(MapboxMap mapboxMap, CameraPosition cameraPosition, List<List<Double[]>> coordinates, boolean removePrevious) {
        PolygonOptions polygonOptions = MapboxUtils.createPolygonOptions(coordinates);
        if(removePrevious) {
            for(com.mapbox.mapboxsdk.annotations.Polygon poly : mapboxMap.getPolygons()) {
                mapboxMap.removePolygon(poly);
            }
        }
        mapboxMap.addPolygon(polygonOptions.fillColor(Color.parseColor("#ff0000")));
        flyTo(mapboxMap, cameraPosition);
    }

    public static void removeAllPolygons(MapboxMap mapboxMap) {
        for(com.mapbox.mapboxsdk.annotations.Polygon poly : mapboxMap.getPolygons()) {
            mapboxMap.removePolygon(poly);
        }
    }

    public static LatLng getCentreOfFeature(Feature feature) {
        double[] bbox = TurfMeasurement.bbox(Objects.requireNonNull(feature.geometry()));
        Point midPoint = TurfMeasurement.midpoint(Point.fromLngLat(bbox[0], bbox[1]), Point.fromLngLat(bbox[2], bbox[3]));
        return new LatLng(midPoint.latitude(), midPoint.longitude());
    }

    public static Feature getNearestFeature(LatLng latLng, List<Feature> features) {
        List<Point> coordinates = null;
        double min = Double.POSITIVE_INFINITY;
        Feature nearestFeature = null;
        for(int i = 0; i < features.size(); i++) {
            if(features.get(i).geometry().type().equalsIgnoreCase("Polygon")) {
                coordinates = TurfMeta.coordAll((Polygon) features.get(i).geometry(), true);
            } else if(features.get(i).geometry().type().equalsIgnoreCase("MultiPolygon")) {
                coordinates = TurfMeta.coordAll((MultiPolygon) features.get(i).geometry(), true);
            } else if(features.get(i).geometry().type().equalsIgnoreCase("LineString")) {
                coordinates = TurfMeta.coordAll((LineString) features.get(i).geometry());
            }
            Point fromPoint = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
            Feature point = TurfMisc.nearestPointOnLine(fromPoint, coordinates);
            double distance = point.getNumberProperty("dist").doubleValue();
            if(distance < min) {
                nearestFeature = features.get(i);
                min = distance;
            }
        }
        return nearestFeature;
    }

    public static Point getNearestPoint(LatLng latLng, Feature feature) {
        List<Point> coordinates = null;
        if(feature.geometry().type().equalsIgnoreCase("Polygon")) {
            coordinates = TurfMeta.coordAll((Polygon) feature.geometry(), true);
        } else if(feature.geometry().type().equalsIgnoreCase("MultiPolygon")) {
            coordinates = TurfMeta.coordAll((MultiPolygon) feature.geometry(), true);
        } else if(feature.geometry().type().equalsIgnoreCase("LineString")) {
            coordinates = TurfMeta.coordAll((LineString) feature.geometry());
        }
        Point fromPoint = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
        Feature point = TurfMisc.nearestPointOnLine(fromPoint, coordinates);
        return (Point) point.geometry();
    }

    public static void flyTo(MapboxMap mapboxMap, CameraPosition cameraPosition) {
//        CameraPosition position = new CameraPosition.Builder()
//                .target(latLng) // Sets the new camera position
//                .zoom(zoom) // Sets the zoom
//                .bearing(180) // Rotate the camera
//                .tilt(45) // Set the camera tilt
//                .build(); // Creates a CameraPosition from the builder

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000);
    }

    public static PolygonOptions generatePerimeter(LatLng centerCoordinates, double radiusInKilometers, int numberOfSides) {
        List<LatLng> positions = new ArrayList<>();
        double distanceX = radiusInKilometers / (111.319 * Math.cos(centerCoordinates.getLatitude() * Math.PI / 180));
        double distanceY = radiusInKilometers / 110.574;

        double slice = (2 * Math.PI) / numberOfSides;

        double theta;
        double x;
        double y;
        LatLng position;
        for (int i = 0; i < numberOfSides; ++i) {
            theta = i * slice;
            x = distanceX * Math.cos(theta);
            y = distanceY * Math.sin(theta);

            position = new LatLng(centerCoordinates.getLatitude() + y,
                    centerCoordinates.getLongitude() + x);
            positions.add(position);
        }
        return new PolygonOptions()
                .addAll(positions)
                .fillColor(Color.parseColor("#008080"))
                .alpha(0.4f);
    }
}
