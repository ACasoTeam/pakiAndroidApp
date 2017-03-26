package acasoteam.pakistapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import android.widget.LinearLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import acasoteam.pakistapp.asynktask.GetAddress;
import acasoteam.pakistapp.asynktask.GetJson;
import acasoteam.pakistapp.database.DBHelper;
import acasoteam.pakistapp.entity.Paki;


import com.facebook.FacebookSdk;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMarkerClickListener{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    DBHelper myHelper;
    SQLiteDatabase db;

    String loginId = null;
    String name = null;
    String surname = null;
    String email = null;

    Activity activity;

    LatLng latLng;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    CallbackManager callbackManager;
    BottomSheetBehavior bottomSheetBehavior;
    Marker marker;
    RatingBar rb;

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // set hideable or not
        try {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


            int idpaki = Integer.parseInt(marker.getTag().toString());
            Paki paki = myHelper.selectPaki(db, idpaki);
            Log.v ("MapsActivity", "idPaki:"+paki.getIdPaki());
            Log.v ("MapsActivity", "avgrate:"+paki.getAvgRate());
            Log.v ("MapsActivity", "NumVote:"+paki.getNumVote());
            Log.v ("MapsActivity", "lat:"+paki.getLat());
            Log.v ("MapsActivity", "lon:"+paki.getLon());

            PakiDao pakidao = new PakiDao();

            pakidao.getFeedback(idpaki, this);
        } catch (Exception e) {
            Log.v ("MapsActivity", "exception: "+e.getMessage());
        }
        return true;
    }

    public void showBottomSheet(View v) {
        if(bottomSheetBehavior.getState() == 4)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        else if(bottomSheetBehavior.getState() == 3)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.v("MapsActivity", "Place: " + place.getName());
                LatLng PlatLng = place.getLatLng();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(PlatLng));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("LOG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


        //super.onActivityResult(requestCode, resultCode, data);
       /* if (resultCode == RESULT_OK)
        {
            Log.v("MapsActivity","onSuccess");
            Log.v("MapsActivity","data: "+ data.toString());
            Log.v("MapsActivity","data extras: "+data.getExtras().toString());


        }
        else
        {
            Log.v("MapsActivity","else");

        }*/
       // callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        activity = this;
        callbackManager = CallbackManager.Factory.create();
        //LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setReadPermissions("public_profile");
        //Log.v("MapsActivity","getLoginBehavior:"+loginButton.getLoginBehavior().toString());
        // FacebookSdk.sdkInitialize(getApplicationContext());

	   /*fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) fab2.getLayoutParams();
            p.setMargins(0, 12, 21, -10);
        }*/

        // get the bottom sheet view
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);

        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        /*// change the state of the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // set the peek height
        bottomSheetBehavior.setPeekHeight(340);

        // set hideable or not
        bottomSheetBehavior.setHideable(false);

        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });*/

        rb=(RatingBar)findViewById(R.id.ratingBar);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),Float.toString(rating),Toast.LENGTH_LONG).show();

            }

        });

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("MapsActivity","onSuccess");
                final AccessToken accessToken = loginResult.getAccessToken();

                GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        loginId = user.optString("id");
                        name = user.optString("name");
                        email = user.optString("email");
                        Log.d("MapsActivity", "name:"+name);
                        Log.d("MapsActivity", "email:"+email);

                        Log.d("MapsActivity", user.optString("id"));
                        Log.d("MapsActivity", user.optString("email"));
                        if (loginId != null) {
                            Log.v("MapsActivity","loginId != null, ed è:"+loginId);

                            ReportDao reportdao = new ReportDao();

                            //todo: cambiare ste assegnazioni random
                            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                reportdao.sendReport(1,latLng, getApplicationContext());

                            }
                        } else {
                            Log.v("MapsActivity","loginId == 0");

                        }
                    }
                }).executeAsync();

            }

            @Override
            public void onCancel() {
                Log.v("MapsActivity","onCancel");
                loginId = null;

            }

            @Override
            public void onError(FacebookException exception) {
                Log.v("MapsActivity","onError");
                Log.e("MapsActivity","ERROR: "+exception.getMessage());
                loginId = null;
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
                    }
                }



            }
        });
        //LoginManager.getInstance().logOut();

        //FINE PROVA

        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
        int version = pref.getInt("version",0);

        String u = "https://acaso-pakistapp.rhcloud.com/PakiOperation?action=pakilist&version="+version;
/*
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);*/
        String out = "";
        myHelper = DBHelper.getInstance(getApplicationContext());
        db = myHelper.getWritableDatabase();
        try {
            out = new GetJson().execute(u).get();
            Log.v("MapsActivity","out.trim:"+out.trim());
            if (!out.trim().equals("0")) {
                JSONObject res = new JSONObject(out);

                JSONArray jPakis = res.getJSONArray("pakis");

                SharedPreferences.Editor editor = getSharedPreferences("session", MODE_PRIVATE).edit();
                editor.putInt("version", res.has("version")?res.getInt("version"):0);
                editor.commit();

                //PROVA





                myHelper.createDB(db, jPakis);
            }





        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("MapsActivity",""+out);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*try {
            int a = new GetAddress().execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

/*

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();

        autocompleteFragment.setFilter(typeFilter);


        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(44.98034238084972, 7.49267578125),
                new LatLng(45.1278045274732, 7.804412841796875)));*/


/*
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.v("MapsActivity", "Place: " + place.getName());
                LatLng PlatLng = place.getLatLng();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(PlatLng));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.v("MapsActivity", "An error occurred: " + status);
            }
        });*/

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(this);



        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        List<Paki> pakis = null;
        try {
            pakis = myHelper.selectPakis(db);

            for (Paki paki : pakis){
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(paki.getLat(), paki.getLon())));
                marker.setTag(paki.getIdPaki());
                Log.v("MapsActivity","lat:"+paki.getLat()+", lon:"+paki.getLon());
            }




        } catch (Exception e) {
            e.printStackTrace();
        }


        if (pakis != null) {


        }
    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }


        //Place current location marker

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        /*
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        */

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    private void getMyLocation() {

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            latLng = new LatLng(location.getLatitude(), location.getLongitude());


            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public void goToNearest(View view) {
        PakiDao pakidao = new PakiDao();

        //todo: cambiare ste assegnazioni random
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = getLastKnownLocation();

            if (location != null) {

                latLng = new LatLng(location.getLatitude(), location.getLongitude());

                Paki nearestP = pakidao.goToNearest(latLng, getApplicationContext());
                LatLng PlatLng = new LatLng(nearestP.getLat(), nearestP.getLon());


                mMap.animateCamera(CameraUpdateFactory.newLatLng(PlatLng));
            }



        }

    }


    public void report(View view) {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

    }




    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }

            }



        }
        Log.v("MapsActivity","bestLocation:"+bestLocation);
        return bestLocation;
    }

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    public void search(View view){

        try {

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();




            LatLngBounds bound = new LatLngBounds(
                    new LatLng(44.98034238084972, 7.49267578125),
                    new LatLng(45.1278045274732, 7.804412841796875));


            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .setBoundsBias(bound)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    //nuovo codice

    public void navigator(View view){
        //tutto temporaneo
        double mLat = 44.98034238084972;
        double mLong =7.49267578125;
        startNavigation(mLat,mLong);
    }


    public void startNavigation(double lat, double lon){
        /*
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" +lat+","+lon));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/

        /*
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lon);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
*/

        Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lon+"z=25");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }

    }

}
