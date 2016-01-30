package it.dedonatis.emanuele.drugstore.fragments;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.Pharmacies;
import it.dedonatis.emanuele.drugstore.rest.PharmacyRestService;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class PharmaciesFragment extends Fragment  {

    final static String LOG_TAG = PharmaciesFragment.class.getSimpleName();
    final static String API_BASE_URL = "http://opendatasalutedata.cloudapp.net";

    GoogleMap mMap;

    public PharmaciesFragment() {}

    public static PharmaciesFragment newInstance() {
        PharmaciesFragment fragment = new PharmaciesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupPharmaciesView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_pharmacies, container, false);
        return fragmentView;
    }


    private void setupPharmaciesView() {

         Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        PharmacyRestService restService = retrofit.create(PharmacyRestService.class);

        String descrizionecomune = "CHIETI";
        restService.getPharmacies("descrizionecomune eq '" + descrizionecomune + "'", "json").enqueue(
                new Callback<Pharmacies>() {

                    @Override
                    public void onResponse(Response<Pharmacies> response,
                                           Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Pharmacies pharmacies = response.body();
                            Log.v(LOG_TAG, pharmacies.size() + "");
                            for (int i = 0; i < pharmacies.size(); i++)
                                Log.v(LOG_TAG, i + ": " + pharmacies.get(i).toString());

                        }else {
                            Log.e(LOG_TAG, response.message());
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.v(LOG_TAG, "FAIL: " + t.toString());
                    }
                });

    }

}
