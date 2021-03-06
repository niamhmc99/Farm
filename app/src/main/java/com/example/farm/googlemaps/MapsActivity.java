package com.example.farm.googlemaps;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.farm.AnimalActivity;
import com.example.farm.LoginActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.VetActivity;
import com.example.farm.emissions.EmissionsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.trenzlr.firebasenotificationhelper.FirebaseNotiCallBack;
import com.trenzlr.firebasenotificationhelper.FirebaseNotificationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, BottomNavigationView.OnNavigationItemSelectedListener {


    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude,longitude;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
         FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.ic_nearbyPlaces);
        bottomNavigationView.setOnNavigationItemSelectedListener(MapsActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_home:
                Intent intent0 = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(MapsActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(MapsActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(MapsActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(MapsActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        GoogleMapOptions options= new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        options.zoomGesturesEnabled(true);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();
        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }

    public void onClick(View v)
    {
        Object[] dataTransfer = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch(v.getId())
        {
            case R.id.B_search:
                EditText tf_location =  findViewById(R.id.TF_location);
                String location = tf_location.getText().toString();
                List<Address> addressList;

                if(location!=null && !location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                        if(addressList != null)
                        {
                            for(int i = 0;i<addressList.size();i++)
                            {
                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.B_hopistals:
                mMap.clear();
                String hospital = "hospital";
                String url = getUrl(latitude, longitude, hospital);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
                break;

            case R.id.B_mechanic:
                mMap.clear();
                String feed = "car_repair";
                url = getUrl(latitude, longitude, feed);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Mechanics centers", Toast.LENGTH_SHORT).show();
                break;

            case R.id.B_vets:
                mMap.clear();
                String vets = "veterinary_care";
                url = getUrl(latitude, longitude, vets);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MapsActivity.this, "Showing Nearby Vets", Toast.LENGTH_SHORT).show();
                break;

            case R.id.B_send_location:
                if (lastlocation == null) {
                    Toast.makeText(MapsActivity.this,"Loading current location resend again", Toast.LENGTH_LONG).show();
                    checkLocationPermission();
                } else {
                    Toast.makeText(MapsActivity.this,"Sending current location please wait", Toast.LENGTH_LONG).show();
                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                    try {
                        final List<Address> fromLocation = geocoder.getFromLocation(lastlocation.getLatitude(), lastlocation.getLongitude(), 1);
                        if(fromLocation != null && fromLocation.size() != 0)
                        {
                            //Send push notification to all users other then me of current location to find help
                            FirebaseNotificationHelper.initialize(getString(R.string.server_key))
                                    .defaultJson(false, getJsonBody(fromLocation.get(0)))
                                    .setCallBack(new FirebaseNotiCallBack() {
                                        @Override
                                        public void success(String s) {
                                            showMessage(MapsActivity.this, fromLocation.get(0).getAddressLine(0)+" sent to all user");
                                        }
                                        @Override
                                        public void fail(String s) {
                                            showMessage(MapsActivity.this, "Unable to sent "+fromLocation.get(0).getAddressLine(0)+" due to " + s);
                                        }
                                    })
                                    .receiverFirebaseToken("/topics/" + getString(R.string.topic))
                                    .send();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }

    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"" + "AIzaSyDzyCapVx5jEfA3haEGUCI0jSIYFu3xUxI");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());
        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menumainopts, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MapsActivity.this, LoginActivity.class));
            case R.id.menuHome:
                Toast.makeText(this, "Home Page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMessage(Context context, String message) {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        if (message.equals("")) {
            message = "No error message has received from server.";
        }
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        final androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private String getJsonBody(Address location) {

        JSONObject jsonObjectData = new JSONObject();
        try {
            jsonObjectData.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
            jsonObjectData.put("latitude",location.getLatitude());
            jsonObjectData.put("longitude",location.getLongitude());
            jsonObjectData.put("title", "Is anyone available to help?");
            jsonObjectData.put("message", "At this location -> "+location.getAddressLine(0));
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectData.toString();
    }
}