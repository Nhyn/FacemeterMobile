package training.facemetermobile.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import training.facemetermobile.Configuration.Config;
import training.facemetermobile.Configuration.PermissionUtils;
import training.facemetermobile.Model.Person;
import training.facemetermobile.R;

import static android.os.Build.VERSION_CODES.M;

public class FindNearbyActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = FindNearbyActivity.class.getSimpleName();

    private boolean mPermissionDenied = false;
    private String personName = "";
    private String nameP,desP;
    private double loclatitude;
    private double loclongitude;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby);
        Firebase.setAndroidContext(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.containsKey("personName")) {
                personName = bundle.getString("personName", "");
             //   Log.e(TAG, "nama"+personName);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-6.2187742,106.8023846), 9));

        if(android.os.Build.VERSION.SDK_INT>=M){
            String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION };
            if (EasyPermissions.hasPermissions(this, perms)) {
                // Have permissions, do the thing!
                Toast.makeText(this, "TODO: Location and Contacts things", Toast.LENGTH_LONG).show();
            } else {
                // Ask for both permissions
                EasyPermissions.requestPermissions(this, "This app needs access to your location to know where and who you are.",RC_LOCATION_CONTACTS_PERM
                        , perms);
            }
        }

        enableMyLocation();
        if(personName!=""){

        Firebase ref = new Firebase(Config.FIREBASE_URL);
        Person person = new Person();

        ref.child("Person").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Person person = postSnapshot.getValue(Person.class);

                    mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(person.getLatitude(),person.getLongitude()))
                        .title("Name : "+person.getName())
                        .snippet("Description : "+person.getDescription())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        if(loclatitude!=0) {
            person.setLatitude(loclatitude);
            person.setLongitude(loclongitude);
        }
    }
        else{
            Toast.makeText(getApplicationContext(), "You Must Loggin First to Find Others!", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Your location now!", Toast.LENGTH_SHORT).show();

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {
                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }
                    });

            Location location = locationManager.getLastKnownLocation(provider);
            Log.e(TAG, "tes lokasi");
            if (location == null) {
                Log.e(TAG, "location null");
            } else {
                Log.e(TAG, "location tidak null");
                loclatitude = location.getLatitude();
                loclongitude = location.getLongitude();

                LatLng coordinate = new LatLng(loclatitude, loclongitude);
                CameraUpdate locationnow = CameraUpdateFactory.newLatLngZoom(
                        coordinate, 16);
                mMap.animateCamera(locationnow);

               // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loclatitude, loclongitude), 13));

                Firebase ref = new Firebase(Config.FIREBASE_URL);
                Firebase objRef = ref.child("Person");
                Query updateProfile = objRef.orderByChild("name").equalTo(personName);

                updateProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot tasksSnapshot) {
                        if (tasksSnapshot.exists()) {
                 //           Log.e(TAG, "nama"+personName);
                            for (DataSnapshot snapshot : tasksSnapshot.getChildren()) {
                                snapshot.getRef().child("latitude").setValue(loclatitude);
                                snapshot.getRef().child("longitude").setValue(loclongitude);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            enableMyLocation();
        } else {

            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
}

