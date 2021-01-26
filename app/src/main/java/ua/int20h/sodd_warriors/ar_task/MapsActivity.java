package ua.int20h.sodd_warriors.ar_task;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import ua.int20h.sodd_warriors.ar_task.network.GeocodeResponse;
import ua.int20h.sodd_warriors.ar_task.network.RetrofitInterface;
import ua.int20h.sodd_warriors.ar_task.network.geocode.Location;
import ua.int20h.sodd_warriors.ar_task.utils.PermissionCheck;
import ua.int20h.sodd_warriors.ar_task.utils.UtilsCheck;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.int20h.sodd_warriors.ar_task.network.geocode.Result;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnConnectionFailedListener
        , ConnectionCallbacks, GoogleMap.OnMapClickListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final static String TAG = "MapsActivity";
    @BindView(R.id.ar_nav_btn)
    Button ar_nav_btn;
    @BindView(R.id.progressBar_maps)
    ProgressBar progressBar;
    LocationRequest mLocationRequest;
    android.location.Location mLastLocation;
    //    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    private Location location;
    private Marker RevMarker;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<android.location.Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest


                android.location.Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (RevMarker != null) {
                    RevMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                RevMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                progressBar.setVisibility(View.GONE);


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);


//        Init_intro();

        PermissionCheck.initialPermissionCheckAll(this, this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        progressBar.setVisibility(View.GONE);
        Log.i("app", "ping");

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Attempt to find an existing FlutterFragment,
        // in case this is not the first time that onCreate() was run.


        if (!UtilsCheck.isNetworkConnected(this)) {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_content),
                    "Turn Internet On", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }

//        CardView.LayoutParams layoutParams=new CardView.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0,getStatusBarHeight(),0,0);
//        CardView cardView=(CardView) findViewById(R.id.decode_cardview);
//        cardView.setLayoutParams(layoutParams);

        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        ar_nav_btn.setOnClickListener(view -> {
//            Intent intent = new Intent(MapsActivity.this, NavActivity.class);
//            startActivity(intent);

            Intent intent = new Intent(MapsActivity.this, ArCamActivity.class);

            try {

//                intent.putExtra("SRC", sourceResultText.getText());
//                intent.putExtra("DEST", destResultText.getText());
                intent.putExtra("SRCLATLNG", mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
                intent.putExtra("DESTLATLNG", RevMarker.getPosition().latitude + "," + RevMarker.getPosition().longitude);
                startActivity(intent);
            } catch (NullPointerException npe) {
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_content),
                        "Source/Destination Fields are Invalid", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
                Log.d(TAG, "onClick: The IntentExtras are Empty");
            }

        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


//    public int getStatusBarHeight() {
//        int result = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }

//    public void initialPermissionCheck() {
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
//        }
//
//        if (ContextCompat.checkSelfPermission
//                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 20);
//
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},30);
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},40);
//        }
//    }

    void Geocode_Call(String address) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.directions_base_url))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface apiService =
                retrofit.create(RetrofitInterface.class);

        final Call<GeocodeResponse> call = apiService.getGecodeData(address,
                getResources().getString(R.string.google_maps_key));

        call.enqueue(new Callback<GeocodeResponse>() {
            @Override
            public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {

                progressBar.setVisibility(View.GONE);

                List<Result> results = response.body().getResults();
                location = results.get(0).getGeometry().getLocation();
//                Toast.makeText(MapsActivity.this, location.getLat() + "," + location.getLng(), Toast.LENGTH_SHORT).show();

                try {
                    mMap.clear();
                    LatLng loc = new LatLng(location.getLat(), location.getLng());
                    mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .title(results.get(0).getFormattedAddress())
                            .snippet(results.get(0).getGeometry().getLocationType()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f));
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    //decode_button.setBackground(getDrawable());

//                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                        @Override
//                        public boolean onMarkerClick(Marker marker) {
//                            if(marker.isInfoWindowShown())
//                                fab_menu.hideMenu(true);
//                            else
//                                fab_menu.hideMenu(false);
//                            return false;
//                        }
//                    });
                } catch (NullPointerException npe) {
                    Log.d(TAG, "onMapReady: Location is NULL");
                }
            }

            @Override
            public void onFailure(Call<GeocodeResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MapsActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void Rev_Geocode_Call(LatLng latlng) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.directions_base_url))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        progressBar.setVisibility(View.VISIBLE);

        RetrofitInterface apiService =
                retrofit.create(RetrofitInterface.class);

        final Call<GeocodeResponse> call = apiService.getRevGecodeData(latlng.latitude + "," + latlng.longitude,
                getResources().getString(R.string.google_maps_key));

        call.enqueue(new Callback<GeocodeResponse>() {
            @Override
            public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {

                progressBar.setVisibility(View.GONE);
                List<Result> results = response.body().getResults();
                String address = results.get(0).getFormattedAddress();
//                Toast.makeText(MapsActivity.this, address, Toast.LENGTH_SHORT).show();

                RevMarker.setTitle(address);
                RevMarker.setSnippet(results.get(0).getGeometry().getLocationType());
//                try{
//
//                }catch (NullPointerException npe){
//                    Log.d(TAG, "onMapReady: Location is NULL");
//                }
            }

            @Override
            public void onFailure(Call<GeocodeResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MapsActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        Log.i("app", "onnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");

        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    Log.i("app", "case");

                    Log.i("app", Arrays.toString(grantResults));
                    Log.i("app", grantResults.length + "");
                    if (grantResults.length > 0) {
                        Log.i("app", grantResults[0] + "");
                    }

                    // If request is cancelled, the result arrays are empty.

                    Log.i("app", "if true");

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.i("app", "if granted");

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                    return;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        progressBar.setVisibility(View.VISIBLE);

//        try {
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            boolean success = googleMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            this, R.raw.style_json));
//            if (!success) {
//                Log.e(TAG, "Style parsing failed.");
//            }
//        } catch (Resources.NotFoundException e) {
//            Log.e(TAG, "Can't find style. Error: ", e);
//        }

//        mMap.add

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission

//                    checkLocationPermission();

            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }


        Log.d(TAG, "onMapReady: MAP IS READY");
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mMap.clear();

                RevMarker = mMap.addMarker(new MarkerOptions().position(latLng));
//                Toast.makeText(this, latLng.latitude + " " + latLng.longitude, Toast.LENGTH_SHORT).show();
                Rev_Geocode_Call(latLng);

//                Toast.makeText(MapsActivity.this, latLng.latitude+","+latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }


        }
    }


    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_content),
                    "Turn GPS ON", Snackbar.LENGTH_LONG);
            mySnackbar.show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick: Short Click " + latLng.toString());
    }
}
