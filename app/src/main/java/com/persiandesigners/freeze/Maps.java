package com.persiandesigners.freeze;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.persiandesigners.freeze.Util.Html;
import com.persiandesigners.freeze.Util.MyLocationListener;
import com.persiandesigners.freeze.Util.MyToast;
import com.persiandesigners.freeze.Util.OnTaskFinished;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by navid on 10/3/2017.
 */
public class Maps extends AppCompatActivity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "this";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    static GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;


    MapFragment googleMap;
    GoogleMap Map;
    Double lat = 0.0, lon;
    Boolean clicked;
    Typeface typeface;
    Button submit;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        declare();
        clicked = false;
        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doGPS() {
        if(mGoogleApiClient==null) {
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (!isGooglePlayServicesAvailable()) {
                MyToast.makeText(Maps.this, "گوگل پلی سرویس برای استفاده از نقشه نیاز است");
                finish();
            }

            mGoogleApiClient.connect();
        }

        LocationManager mlocManager = null;
        LocationListener mlocListener;
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(Maps.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Maps.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//gps is connected
            mGoogleApiClient.connect();
        } else {
            AlertDialog.Builder a = new AlertDialog.Builder(Maps.this);
            a.setMessage(("جی پی اس خاموش است. آیا میخواهید روشن کنید؟"));
            a.setPositiveButton(("بله"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                    boolean enabled = service
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!enabled) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }
            });
            a.setNegativeButton(("خیر"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = a.show();
            TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.RIGHT);
            messageText.setTypeface(typeface);
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
            Log.v(TAG, "onStart fired ..............");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient!=null) {
            Log.v(TAG, "onStop fired ..............");
            mGoogleApiClient.disconnect();
            Log.v(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if(mGoogleApiClient!=null) {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            Log.v(TAG, "Location update started ..............: ");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        Log.v(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            Map.clear();

            String lats = String.valueOf(mCurrentLocation.getLatitude());
            String lngs = String.valueOf(mCurrentLocation.getLongitude());
            Log.v("this","At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lats + "\n" +
                    "Longitude: " + lngs + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation.getProvider());
            lat=mCurrentLocation.getLatitude();
            lon=mCurrentLocation.getLongitude() ;

            LatLng sydney = new LatLng(lat, lon);
            Map.addMarker(new MarkerOptions().position(sydney).title(""));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(15).build();
            Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            Log.v(TAG, "location is null ...............");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.v(TAG, "Location update resumed .....................");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            doGPS();
        }
    }
    private void submit() {
        if (clicked || lat != 0.0) {
            SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
            SharedPreferences.Editor pref = settings.edit();
            pref.putString("lat", lat + "");
            pref.putString("lon", lon + "");
            pref.commit();
            onBackPressed();
            finish();
            Log.v("this", lat + " gps " + lon);
        } else {
            MyToast.makeText(Maps.this, "موقعیت جغرافیایی مشخص نشده است");
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doGPS();
                } else {
                    MyToast.makeText(Maps.this, "دسترسی به جی پی اس غیرفعال است");
                }
                return;
            }
        }
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            googleMap.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Map=googleMap;

                    getDefultLatLon(Map);

                    if (googleMap == null) {
                        Toast.makeText(getApplicationContext(),
                                "نقشه روی گوشی شما قابل نمایش نیست", Toast.LENGTH_SHORT)
                                .show();
                    }

                    Map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            Map.clear();
                            Map.addMarker(new MarkerOptions().position(latLng));
                            lat = latLng.latitude;
                            lon = latLng.longitude;
                            clicked = true;
                            stopLocationUpdates();
                        }
                    });
                }
            });

        }

    }

    private void getDefultLatLon(GoogleMap map) {
        long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        new Html(new OnTaskFinished()
        {
            @Override
            public void onFeedRetrieved(String body)
            {
                Log.v("this", body);
                if(body.equals("errordade")){

                }else{
                    try {
                        JSONObject json=new JSONObject(body);

                        LatLng sydney = new LatLng(json.optDouble("lat"), json.optDouble("long"));
                        //Map.addMarker(new MarkerOptions().position(sydney).title(""));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                        Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },false,Maps.this,"").execute(getString(R.string.url)+"/getDefLatLon.php?n="+number );

    }


    private void declare() {
        typeface = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");

        submit = (Button) findViewById(R.id.submit);
        submit.setTypeface(typeface);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }


}
