package com.example.tatil;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tatil.Classes.Post;
import com.example.tatil.Fragments.HomeFragment;
import com.example.tatil.Fragments.LocationsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mapLocation;
    SupportMapFragment supportMapFragment;
    static List<Address> LocationList;
    Button btn_map_destination;
    LatLng latLng;
    LocationProvider locationProvider;
    FusedLocationProviderClient client;
    TextView textView_loc, textView_lat;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle intent = getIntent().getExtras();
        btn_map_destination = findViewById(R.id.btn_map_directions);
        textView_loc = findViewById(R.id.textView_map_loc);
        textView_lat = findViewById(R.id.textView_map_lat);

        String location = intent.getString("location");
        getSharedPreferences("LOCATION", MODE_PRIVATE).edit().putString("location", location).apply();
        //  mapLocation = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        String data = getApplicationContext().getSharedPreferences("LOCATION", Context.MODE_PRIVATE).getString("location", "none");
        client = LocationServices.getFusedLocationProviderClient(this);
        System.out.println("data MAP ACTİVİTY.........." + data);

        String locationnew = data;
        Geocoder geocoder = new Geocoder(MapActivity.this);
        try {
            LocationList = geocoder.getFromLocationName(locationnew, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address2 = new Address(Locale.getDefault());
        Address address = LocationList.get(0);
        btn_map_destination.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    displayTrack("0", address.getLocality());
                }
            }
        });




    }

    void displayTrack(String source, String destination) {
        System.out.println("------------DİSPLAY TRACK------------");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MapActivity.this,
                            Locale.getDefault());

                    try {
                        System.out.println("------------GET LOCATİON TRY İNSİDE------------");

                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(),
                                1);
                        textView_loc.setText(Html.fromHtml("<font color='#6200EE'><b>Latitude:" + addresses.get(0).getLatitude()));
                        System.out.println(addresses.get(0));
                        textView_lat.setText(Html.fromHtml("<font color='#6200EE'><b>Longitude:" + addresses.get(0).getLongitude()));
                        System.out.println("****** KULANICI KONUMU"+addresses.get(0).getLocality());


                        LatLng destLatLng = null;
                        Geocoder geocoder1 = new Geocoder(MapActivity.this);
                        List<Address > destAdresses =new ArrayList<>();
                        try {
                            // May throw an IOException
                            destAdresses = geocoder1.getFromLocationName(destination, 5);

                            Address destlocation = destAdresses.get(0);
                            destlocation.setLatitude(40.576782) ;
                            destlocation.setLongitude(41.042991);

                            destLatLng = new LatLng(destlocation.getLatitude(), destlocation.getLongitude());
                            System.out.println("HEDEF:STRDAN ALINAN"+destAdresses.get(0));
                        } catch (IOException ex) {

                            ex.printStackTrace();
                         }



                        Uri uri = Uri.parse("https://www.google.co.in/maps/dir/"+addresses.get(0).getLocality()+"/"+destAdresses.get(0).getLocality());
                        Intent intent= new Intent(Intent.ACTION_VIEW,uri);
                        intent.setPackage("com.google.android.apps.maps");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });



    }

    private Address getLocation() {
        final List<Address>[] addresses = new List[1] ;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            System.out.println("------------PERMİSSİON ------------");

            return null;
        } System.out.println("------------GET LOCATİON ------------");
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                 if (location != null) {
                    Geocoder geocoder = new Geocoder(MapActivity.this,
                            Locale.getDefault());

                    try {
                        System.out.println("------------GET LOCATİON TRY İNSİDE------------");

                        addresses[0] = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(),
                                1);
                        textView_loc.setText(Html.fromHtml("<font color='#6200EE'><b>Latitude:" + addresses[0].get(0).getLatitude()));
                        System.out.println(addresses[0]);
                        textView_lat.setText(Html.fromHtml("<font color='#6200EE'><b>Longitude:" + addresses[0].get(0).getLongitude()));

                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
        System.out.println("----------"+addresses[0].get(0));
    return addresses[0].get(0);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        System.out.println("------------MAP READY ------------");

         mapLocation = googleMap;

        latLng = new LatLng(LocationList.get(0).getLatitude(), LocationList.get(0).getLongitude());
        System.out.println(LocationList.get(0));
        System.out.println(latLng);

        mapLocation.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
    }


}