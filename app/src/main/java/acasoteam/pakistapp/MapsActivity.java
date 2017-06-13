package acasoteam.pakistapp;

import android.Manifest;
import android.app.ActionBar;
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
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.BottomSheetBehavior;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;
import acasoteam.pakistapp.Adapter.CommentAdapter;
import acasoteam.pakistapp.asynktask.GetJson;
import acasoteam.pakistapp.database.DBHelper;
import acasoteam.pakistapp.entity.Paki;



public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMarkerClickListener{

    public GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    public DBHelper myHelper;
    public SQLiteDatabase db;
    String loginId = null;
    String name = null;
    String email = null;
    Activity activity;
    LatLng latLng;
    CallbackManager callbackManager;
    BottomSheetBehavior bottomSheetBehavior;
    Marker marker;
    ProgressBar spinBar;
    TextView address;
    RatingBar rb;
    int idpaki;
    RecyclerView rv;
    CommentAdapter adapter;
    AccessToken accessToken;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        activity = this;
        callbackManager = CallbackManager.Factory.create();

        accessToken = AccessToken.getCurrentAccessToken();

        // get the bottom sheet view
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);

        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        fab=(FloatingActionButton) findViewById((R.id.fab2)); //goToNearest
        //fab=(FloatingActionButton) findViewById((R.id.fab)); //report



        rb=(RatingBar)findViewById(R.id.ratingBar);
        address = (TextView)findViewById(R.id.address);
        spinBar = (ProgressBar) findViewById(R.id.spinBar);
        rv = (RecyclerView) findViewById(R.id.comments);
        adapter = new CommentAdapter(null, this);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),Float.toString(rating),Toast.LENGTH_LONG).show();

            }

        });


        //LoginManager.getInstance().logOut();

        //FINE PROVA

        SharedPreferences pref = getSharedPreferences("session", MODE_PRIVATE);
        int version = pref.getInt("version",0);

        String u = "https://acaso-pakistapp.rhcloud.com/PakiOperation?action=pakilist&version="+version;
        String out = "";
        myHelper = DBHelper.getInstance(getApplicationContext());
        db = myHelper.getWritableDatabase();
        try {
            new GetJson(this).execute(u);


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


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View llBottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    fab.setVisibility(View.GONE);
                    //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Log.v ("bottomSheet: ", "" + newState);
                }
                else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    //fab.setVisibility(View.GONE);
                    //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Log.v ("bottomSheet: ", "" + newState);
                }
            }

            @Override
            public void onSlide(@NonNull View llBottomSheet, float slideOffset) {
                Log.v ("bottomSheet: ", "onSlide");

            }
        });


    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        // set hideable or not
        try {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


            idpaki = Integer.parseInt(marker.getTag().toString());
            Paki paki = myHelper.selectPaki(db, idpaki);
            Log.v ("MapsActivity", "idPaki:"+paki.getIdPaki());
            Log.v ("MapsActivity", "avgrate:"+paki.getAvgRate());
            Log.v ("MapsActivity", "NumVote:"+paki.getNumVote());
            Log.v ("MapsActivity", "lat:"+paki.getLat());
            Log.v ("MapsActivity", "lon:"+paki.getLon());

            String address = paki.getAddress();

            PakiDao pakidao = new PakiDao();

            pakidao.getInfo(idpaki, address, this);

            LatLng PlatLng = new LatLng(paki.getLat(), paki.getLon());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(PlatLng));

        } catch (Exception e) {
            Log.v ("MapsActivity", "exception: "+e.getMessage());
        }
        return true;
    }

    public void showBottomSheet(View v) {

        if(bottomSheetBehavior.getState() == 4) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            PakiDao pakidao = new PakiDao();
            fab.setVisibility(View.GONE);
            Log.v("bottomSheet: ", "STATE_COLLAPSED");

            if (idpaki != 0) {
                pakidao.getFeedback(idpaki, this);
            }
        }
        else if(bottomSheetBehavior.getState() == 3)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            fab.setVisibility(View.VISIBLE);
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
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }


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

            /*while (!asyncDone) {
                //lammerda......
            }*/
            pakis = myHelper.selectPakis(db);

            for (Paki paki : pakis) {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(paki.getLat(), paki.getLon()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_beer1a)));
                marker.setTag(paki.getIdPaki());
                Log.v("MapsActivity", "lat:" + paki.getLat() + ", lon:" + paki.getLon());
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

        if (bottomSheetBehavior.getState() == 5) {
            Log.v("MapsActivity", "accesstoken:" + accessToken);
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

            // Callback registration
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.v("MapsActivity", "onSuccess");
                    accessToken = loginResult.getAccessToken();

                    if (accessToken != null) {
                        GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                                loginId = user.optString("id");
                                name = user.optString("name");
                                email = user.optString("email");

                                if (loginId != null) {
                                    Log.v("MapsActivity", "loginId != null, ed è:" + loginId);

                                    ReportDao reportdao = new ReportDao();

                                    //todo: cambiare ste assegnazioni random
                                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                            Manifest.permission.ACCESS_FINE_LOCATION)
                                            == PackageManager.PERMISSION_GRANTED) {
                                        Location location = getLastKnownLocation();

                                        if (location != null) {
                                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            Log.v("MapsActivity", "name:" + name);
                                            Log.v("MapsActivity", "email:" + email);
                                            reportdao.sendReport(loginId, latLng, name, email, getApplicationContext());
                                        } else {
                                            //todo: mettere il toast
                                            //Toast.makeText(context, "permission denied", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                } else {
                                    Log.v("MapsActivity", "loginId == 0");

                                }
                            }
                        }).executeAsync();
                    }

                }

                @Override
                public void onCancel() {
                    Log.v("MapsActivity", "onCancel");
                    loginId = null;

                }

                @Override
                public void onError(FacebookException exception) {
                    Log.v("MapsActivity", "onError");
                    Log.e("MapsActivity", "ERROR: " + exception.getMessage());
                    loginId = null;
                    if (exception instanceof FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
                        }
                    }


                }
            });

        } else if (bottomSheetBehavior.getState() == 3 || bottomSheetBehavior.getState() == 4) {
            try {
                navigator(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


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
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
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

    public void navigator(View view) throws JSONException {
        //tutto temporaneo
        if (idpaki != 0) {
            Paki paki = myHelper.selectPaki(db, idpaki);
            double mLat = paki.getLat();
            double mLong = paki.getLon();
            startNavigation(mLat, mLong);
        }
    }


    public void startNavigation(double lat, double lon){

        Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lon));
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }

    }

    public void addFeedback(int oid, String name, int rate){

        Intent i=new Intent(this,FeedActivity.class);
        i.putExtra("oid",oid);
        i.putExtra("name",name);
        i.putExtra("rate",rate);
        startActivity(i);

    }

    public void prova1(View view){
        int oid =43;
        String name = "prova";
        int rate = 4;
        addFeedback(oid,name,rate);
    }

    public TextView getAddress() {
        return address;
    }

    public void setAddress(TextView address) {
        this.address = address;
    }

    public RatingBar getRb() {
        return rb;
    }

    public void setRb(RatingBar rb) {
        this.rb = rb;
    }

    public ProgressBar getSpinBar() {
        return spinBar;
    }

    public void setSpinBar(ProgressBar spinBar) {
        this.spinBar = spinBar;
    }

    public RecyclerView getRv() {
        return rv;
    }

    public void setRv(RecyclerView rv) {
        this.rv = rv;
    }

    public CommentAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CommentAdapter adapter) {
        this.adapter = adapter;
    }


}
