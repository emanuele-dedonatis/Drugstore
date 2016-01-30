package it.dedonatis.emanuele.drugstore.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import android.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapPharmAdapter implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String LOG_TAG = MapPharmAdapter.class.getSimpleName();
    private static final int MAP_PERMISSION_ACCESS_FINE_LOCATION = 0;

    private static final double MILAN_LAT = 45.464211;
    private static final double MILAN_LNG = 9.191383;


    private static final double PIOLA_LAT = 45.4803;
    private static final double PIOLA_LNG = 9.2237;

    Activity mActivity;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastKnownLocation;
    LatLng latLng;
    LocationManager mLocationManager;

    public MapPharmAdapter(Activity activity) {
        this.mActivity = activity;
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if ( ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Log.v(LOG_TAG, "MAP request PERMISSION");
            ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MAP_PERMISSION_ACCESS_FINE_LOCATION);
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(LOG_TAG, "MAP ready");

        mGoogleMap = googleMap;
        if ( ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Log.v(LOG_TAG, "MAP request PERMISSION");
            ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MAP_PERMISSION_ACCESS_FINE_LOCATION);
        }
        mGoogleMap.setMyLocationEnabled(true);

        buildGoogleApiClient();

        mGoogleApiClient.connect();

        mGoogleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(MILAN_LAT, MILAN_LNG)).zoom(12).build()));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(LOG_TAG, "MAP Connected");
        if ( ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Log.v(LOG_TAG, "MAP request PERMISSION");
            ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MAP_PERMISSION_ACCESS_FINE_LOCATION);
        }
        mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        useLocation(mLastKnownLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(LOG_TAG, "MAP Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(LOG_TAG, "MAP Connection failed");

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(LOG_TAG, "MAP location changed");
        useLocation(location);
    }

    private void useLocation(Location location) {
        if(location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(14).build();

            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
