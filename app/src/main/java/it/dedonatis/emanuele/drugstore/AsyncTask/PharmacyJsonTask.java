package it.dedonatis.emanuele.drugstore.AsyncTask;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import it.dedonatis.emanuele.drugstore.BuildConfig;

public class PharmacyJsonTask extends AsyncTask<String, String, Void> {

    private static final String LOG_TAG = PharmacyJsonTask.class.getSimpleName();

    // https://maps.googleapis.com/maps/api/place/radarsearch/json?
    // location=48.859294,2.347589
    // &radius=5000
    // &types=food|cafe
    // &key=YOUR_API_KEY
    final String BASE_URL =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    final String LOCATION_PARAM = "location";
    final String RADIUS_PARAM = "radius";
    final String TYPES_PARAM = "types";
    final String KEY_PARAM = "key";

    /*
    {
       "html_attributions" : [],
       "results" : [
              {
             "geometry" : {
                "location" : {
                   "lat" : -33.867591,
                   "lng" : 151.201196
                }
             },
             "icon" : "http://maps.gstatic.com/mapfiles/place_api/icons/travel_agent-71.png",
             "id" : "a97f9fb468bcd26b68a23072a55af82d4b325e0d",
             "name" : "Australian Cruise Group",
             "opening_hours" : {
                "open_now" : true
             },
             "photos" : [
                {
                   "height" : 242,
                   "html_attributions" : [],
                   "photo_reference" : "CnRnAAAABjeoPQ7NUU3pDitV4Vs0BgP1FLhf_iCgStUZUr4ZuNqQnc5k43jbvjKC2hTGM8SrmdJYyOyxRO3D2yutoJwVC4Vp_dzckkjG35L6LfMm5sjrOr6uyOtr2PNCp1xQylx6vhdcpW8yZjBZCvVsjNajLBIQ-z4ttAMIc8EjEZV7LsoFgRoU6OrqxvKCnkJGb9F16W57iIV4LuM",
                   "width" : 200
                }
             ],
             "place_id" : "ChIJrTLr-GyuEmsRBfy61i59si0",
             "scope" : "GOOGLE",
             "reference" : "CoQBeQAAAFvf12y8veSQMdIMmAXQmus1zqkgKQ-O2KEX0Kr47rIRTy6HNsyosVl0CjvEBulIu_cujrSOgICdcxNioFDHtAxXBhqeR-8xXtm52Bp0lVwnO3LzLFY3jeo8WrsyIwNE1kQlGuWA4xklpOknHJuRXSQJVheRlYijOHSgsBQ35mOcEhC5IpbpqCMe82yR136087wZGhSziPEbooYkHLn9e5njOTuBprcfVw",
             "types" : [ "travel_agency", "restaurant", "food", "establishment" ],
             "vicinity" : "32 The Promenade, King Street Wharf 5, Sydney"
          }
       ],
       "status" : "OK"
    }
     */

    final String ATTR_STATUS = "status";
    InputStream inputStream = null;
    String result = "";

    protected void onPreExecute() {
        Log.v("JSON", "Pre execute");
    }

    @Override
    protected Void doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String resultJsonStr = null;

        String radius = "5000";
        String type = "pharmacy";
        try {
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(LOCATION_PARAM, params[0] + "," + params[1])
                    .appendQueryParameter(RADIUS_PARAM, radius)
                    .appendQueryParameter(TYPES_PARAM, type)
                    .appendQueryParameter(KEY_PARAM, BuildConfig.GOOGLE_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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

            Log.v(LOG_TAG, "Result string: " + resultJsonStr);
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
            String status = resultJson.getString(ATTR_STATUS);
            Log.v(LOG_TAG, "Status: " + status);
            if(status.equalsIgnoreCase("ok")) {
                Log.v(LOG_TAG, "OK -> proceed");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing result string", e);
        }

        return null;
    }
}
