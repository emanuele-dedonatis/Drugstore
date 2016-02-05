package it.dedonatis.emanuele.drugstore.AsyncTask;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.dedonatis.emanuele.drugstore.BuildConfig;
import it.dedonatis.emanuele.drugstore.R;

public class PharmacyJsonTask extends AsyncTask<String, String, JSONArray> {

    private static final String LOG_TAG = PharmacyJsonTask.class.getSimpleName();

    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?
    // location=48.859294,2.347589
    // &radius=5000
    // &types=food|cafe
    // &key=YOUR_API_KEY
    final String BASE_URL =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    final String LOCATION_PARAM = "location";
    final String RADIUS_PARAM = "radius";
    final String TYPES_PARAM = "types";
    final String RANKBY_PARAM = "rankBy";
    final String KEY_PARAM = "key";
    final double FROM_METER_TO_DEGREE = 0.000012;

    final int RADIUS_IN_METER = 500;

    /*
{
   "html_attributions" : [],
   "next_page_token" : "CoQC_QAAAPGCuoP-jPNyyFsvXUGJ37jybGNslNEtEI7YyQEWT2WrFP_0PQpuW5DMrAF4TU8j-4Jy6TMVbPGFiyCGyEhkDOkqGlBJRqyIISwWgBzadb69Snr5k02NfeL2KwSfiReGz9KZChwicggCcXhFQcoTymJF25xhnPOS-heVu2WdfuovyCAW4Bi-_VqsVge9omwnhtF15k71PaznPirWX-SYDofSYkvdlVmFRebA7UU5zAdTOWTdOueoGvKGILBQrFJTltz15By8pD4HHaNL0ZbjE_7wf_qZHqg2sRQfjGQJ3sww7w1SifCYZ_EhWDlFSGArcT567dQafHAxY5lQi_wT9LUSEJ7l8mq9FE9PsTLzOqnxA4IaFFURWWAiKXqkQOAKsvXWE19MmF0v",
   "results" : [
      {
         "geometry" : {
            "location" : {
               "lat" : 45.4806458,
               "lng" : 9.188861299999999
            }
         },
         "icon" : "https://maps.gstatic.com/mapfiles/place_api/icons/shopping-71.png",
         "id" : "c2695e3e971a277779a784813518e2c8960a5ff5",
         "name" : "Farmacia Smeraldo",
         "opening_hours" : {
            "open_now" : true,
            "weekday_text" : []
         },
         "place_id" : "ChIJQzLkxDTBhkcRTyxgclZeoV8",
         "reference" : "CnRkAAAAyOX2jMMAPvvILvV-8T297LkBRzz9au6RRfVbqzsE488MOySyiexjCdPbjirLp-YAWCdg_gRMIw2zFV5a9MVGUFHTGsyRBEBbgKS7U8uIlKinLsY6Oxl4ZrpMN0RU3KV2HwfAmDuNNTEPPNcmk5b23BIQ0t6AyPkifs9hhY0Jwv1ehxoUkqEC0bLvXbFJGNKeC7IDgxBHvmQ",
         "scope" : "GOOGLE",
         "types" : [ "pharmacy", "store", "health", "point_of_interest", "establishment" ],
         "vicinity" : "Viale Monte Grappa, 7, Milano"
      },
            {
         "geometry" : {
            "location" : {
               "lat" : 45.4845357,
               "lng" : 9.215714499999997
            }
         },
         "icon" : "https://maps.gstatic.com/mapfiles/place_api/icons/shopping-71.png",
         "id" : "ed915c181c52cfbadb097f7c4d52fdfe70a3312e",
         "name" : "Farmacia Ambreck",
         "photos" : [
            {
               "height" : 1152,
               "html_attributions" : [
                  "\u003ca href=\"https://maps.google.com/maps/contrib/112947855422778691500/photos\"\u003eAndrea Mangone\u003c/a\u003e"
               ],
               "photo_reference" : "CmRdAAAACt-dcxPS1ljqQomhynC8Hc9vFUHNE0PJm0azJePfoMgrhf3O1RdYuvFFca3dacN0z-VOYi5amptdWMv_92nD5jaCqxVKl8qh0Oi_HGga5PYwL34yeC6CHqH9NR33TYTiEhBPZEt7varluie2kRfXP2YsGhQqQWYAVTvuKjnWWbAIrbypYTPfOw",
               "width" : 2048
            }
         ],
         "place_id" : "ChIJj8Ee3-fGhkcRmaapqwS-gfM",
         "rating" : 4.1,
         "reference" : "CnRkAAAA2nY_PMIExcJikX5poudhqmRR2ne9YAmWzdc0J8qLnJcMYKkNTMn6Rey4TYpRNxg48-lEKWcBA2eHK2yQAkd-NBfH9ooXF9BY4c1vDNVDq3lO2tPPz8cS3Hoc1bqPiis6Ij7PKK5VCDRXlDwv2d1TPxIQqyJjsQiq5apzI0sRinN-GBoUzMY4dPNvE-zHnRO7rhGz64lrI_M",
         "scope" : "GOOGLE",
         "types" : [ "pharmacy", "store", "health", "point_of_interest", "establishment" ],
         "vicinity" : "Via Antonio Stradivari, 1, Milano"
      }
       ],
       "status" : "OK"
    }
     */

    final String ROOT_STATUS = "status";
    final String ROOT_RESULTS = "results";

    final String ITEM_GEOMETRY = "geometry";
    final String ITEM_PLACE_ID = "place_id";
    final String ITEM_NAME = "name";
    final String ITEM_VICINITY = "vicinity";

    final String GEOMETRY_LOCATION = "location";
    final String LOCATION_LAT = "lat";
    final String LOCATION_LNG = "lng";

    InputStream inputStream = null;
    String result = "";
    GoogleMap mMap;
    LatLng mCurrentPosition;
    Context mContext;
    public PharmacyJsonTask(GoogleMap map, Context context) {
        super();
        this.mContext = context;
        this.mMap = map;
    }

    protected void onPreExecute() {
    }

    @Override
    protected JSONArray doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        mCurrentPosition = new LatLng(Double.valueOf(params[0]), Double.valueOf(params[1]));

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String resultJsonStr = null;

        String radius = RADIUS_IN_METER + "";
        String type = "pharmacy";
        String rankby = "distance";
        try {
            // https://maps.googleapis.com/maps/api/place/nearbysearch/json?
            // location=48.859294,2.347589
            // &radius=5000
            // &types=food|cafe
            // &key=YOUR_API_KEY
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(LOCATION_PARAM, params[0] + "," + params[1])
                    .appendQueryParameter(RADIUS_PARAM, radius)
                    .appendQueryParameter(TYPES_PARAM, type)
                    .appendQueryParameter(RANKBY_PARAM, rankby)
                    .appendQueryParameter(KEY_PARAM, BuildConfig.GOOGLE_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());


            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            resultJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            JSONObject resultJson = new JSONObject(resultJsonStr);
            String status = resultJson.getString(ROOT_STATUS);
            if(status.equalsIgnoreCase("ok")) {
                return resultJson.getJSONArray(ROOT_RESULTS);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing result string", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONArray resultsArray) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.clear();
        mMap.setMyLocationEnabled(true);

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        bounds.include(mCurrentPosition);

        if(resultsArray != null) {
            float min_distance = Float.MAX_VALUE;
            float max_distance = 0;

            Marker nearest = null;

            for(int i=0; i < resultsArray.length(); i++) {

                JSONObject jObject = null;
                try {
                    jObject = resultsArray.getJSONObject(i);

                    String place_id = jObject.getString(ITEM_PLACE_ID);
                    JSONObject geometry = jObject.getJSONObject(ITEM_GEOMETRY);
                    JSONObject location = geometry.getJSONObject(GEOMETRY_LOCATION);
                    double lat = location.getDouble(LOCATION_LAT);
                    double lng = location.getDouble(LOCATION_LNG);
                    String name = jObject.getString(ITEM_NAME);
                    String vicinity = jObject.getString(ITEM_VICINITY);
                    LatLng marker = new LatLng(lat, lng);
                    float [] distance = new float[1];
                    Location.distanceBetween(mCurrentPosition.latitude,
                            mCurrentPosition.longitude,
                            lat,
                            lng,
                            distance);

                    String snippetDistance;
                    if(distance[0] < 1000)
                        snippetDistance = String.format("%.0f", distance[0]) + " m";
                    else
                        snippetDistance = String.format("%.2f", distance[0]/1000) + " km";

                    Marker current = mMap.addMarker(new MarkerOptions()
                            .position(marker)
                            .title(name)
                            .snippet(snippetDistance)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pharmacy_marker)));


                    if(distance[0] < min_distance) {
                        min_distance = distance[0];
                        nearest = current;
                    }

                    if(distance[0] > max_distance) {
                        max_distance = distance[0];
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if(nearest != null) {
                nearest.showInfoWindow();
                bounds.include(nearest.getPosition());
            }

            mMap.addCircle(new CircleOptions()
                    .center(mCurrentPosition)
                    .radius(((max_distance > 0) ? max_distance : RADIUS_IN_METER) + 50)
                    .strokeColor(0x99009688)
                    .fillColor(0x22009688)).setStrokeWidth(3);

        }else {
            // no result
            Circle mapCircle = mMap.addCircle(new CircleOptions()
                    .center(mCurrentPosition)
                    .radius(RADIUS_IN_METER + 50)
                    .strokeColor(0x99009688)
                    .fillColor(0x22009688));
            mapCircle.setStrokeWidth(3);
            LatLng extremeBound = new LatLng(mCurrentPosition.latitude, mCurrentPosition.longitude + RADIUS_IN_METER * FROM_METER_TO_DEGREE);
            bounds.include(extremeBound);

        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 200));

    }
}
