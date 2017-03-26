package acasoteam.pakistapp.asynktask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import acasoteam.pakistapp.MapsActivity;

/**
 * Created by andre on 18/12/2016.
 */

public class GetInfo extends AsyncTask<String, Void, Void> {

    private Exception exception;

    String res;


    BufferedReader reader = null;

    Context context;

    public GetInfo(Context activity) {
        this.context = activity;

    }

    protected Void doInBackground(String... urls) {
        try {
            HttpURLConnection urlConnection = null;
            URL url = new URL(urls[0]);
            Log.v("GetInfo","urls[0]:"+urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty( "charset", "utf-8");

            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.v("GetInfo","return null");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                Log.v("GetInfo","buffer.length() == 0");
                return null;
            }
            res = buffer.toString();

            //return  buffer.toString();


        } catch (Exception e) {
            this.exception = e;
            e.printStackTrace();
            Log.d("GetInfo", "eccez:" + e.getMessage());

            //return null;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        // TODO: check this.exception
        // TODO: do something with the feed

        CharSequence text = "";
        Log.v("GetInfo","res:"+res);

        if (res.trim().equals("0") || res.trim().equals("-1")){
            text = "Si è verificato qalche problema";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            try {

                JSONObject feedbacks = new JSONObject(res);
                Log.v("GetInfo","name:"+feedbacks.getString("name"));
                Log.v("GetInfo","avgRate:"+feedbacks.getDouble("avgRate"));
                Log.v("GetInfo","numVote:"+feedbacks.getInt("numVote"));
                Log.v("GetInfo","numVote:"+feedbacks.getString("address"));

                if (context instanceof MapsActivity) {

                    ((MapsActivity)context).getRb().setRating((float)feedbacks.getDouble("avgRate"));
                    //todo: cambiare lo split
                    ((MapsActivity)context).getAddress().setText(feedbacks.getString("address").split(",")[0]);

                }







            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }
}
