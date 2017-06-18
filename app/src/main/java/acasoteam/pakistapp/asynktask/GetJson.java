package acasoteam.pakistapp.asynktask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import acasoteam.pakistapp.MapsActivity;
import acasoteam.pakistapp.entity.Paki;

/**
 * Created by andre on 18/12/2016.
 */

public class GetJson extends AsyncTask<String, Void, String> {

    private Exception exception;



    BufferedReader reader = null;

    Context context;
    String out;

    public GetJson(Context activity) {
        this.context = activity;

    }


    protected String doInBackground(String... urls) {
        try {
            HttpURLConnection urlConnection = null;
            URL url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty( "charset", "utf-8");

            urlConnection.connect();

            Log.v("GetJson","dopo il connect");

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.v("GetJson","return null");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                Log.v("GetJson","buffer.length() == 0");
                return null;
            }
            //forecastJsonStr = buffer.toString();

            Log.v("GetJson",buffer.toString());
            out = buffer.toString();

            return  buffer.toString();


        } catch (Exception e) {
            this.exception = e;
            e.printStackTrace();
            Log.d("test debug", "eccez:" + e.getMessage());

            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed

        Log.v("MapsActivity","out.trim:"+out.trim());
        if (out != null && !out.trim().equals("0")) {
            JSONObject res = null;
            try {
                res = new JSONObject(out);
                JSONArray jPakis = res.getJSONArray("pakis");

                SharedPreferences.Editor editor = context.getSharedPreferences("session", context.MODE_PRIVATE).edit();
                editor.putInt("version", res.has("version")?res.getInt("version"):0);
                editor.commit();
                ((MapsActivity)context).myHelper.createDB(((MapsActivity)context).db, jPakis);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<Paki> pakis = null;
            try {
                pakis = ((MapsActivity)context).myHelper.selectPakis(((MapsActivity)context).db);
                Marker marker;
                for (Paki paki : pakis) {
                    marker = ((MapsActivity)context).mMap.addMarker(new MarkerOptions().position(new LatLng(paki.getLat(), paki.getLon())));
                    marker.setTag(paki.getIdPaki());
                    Log.v("MapsActivity", "lat:" + paki.getLat() + ", lon:" + paki.getLon());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }



    }
}
