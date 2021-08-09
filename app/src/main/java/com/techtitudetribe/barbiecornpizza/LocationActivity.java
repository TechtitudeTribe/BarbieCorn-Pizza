 package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private TextView spinner;
    private TextView cityChoose, confirmLocation, cityGo;
    private FirebaseAuth firebaseAuth;
    private CardView currentLocationCard;
    private LinearLayout cityNamesLayout, chooseCityCard;
    private TextView kanth, chandpur, moradabad, noCurrentLocation;
    private DatabaseReference coordinateRef, addRef;
    private TextView textLatitude, textLongitude, textLatitude2, textLongitude2;
    private Double lat1, long1, lat2, long2;
    private Boolean isNameLayout = false;
    private TextView chooseCurrentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int requestCode = 44;
    private ProgressBar progressBar, addProgressBar;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private int position = -1;
    private RecyclerView addressList;
    private TextView manualAddress;
    private String address;
    private Boolean isAddress = false;
    private RelativeLayout addressLayout;
    private LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        spinner = (TextView) findViewById(R.id.choose_location_spinner);
        cityChoose = (TextView) findViewById(R.id.city_choose);
        confirmLocation = (TextView) findViewById(R.id.location_confirm);
        chooseCityCard = (LinearLayout) findViewById(R.id.choose_city_card);
        cityNamesLayout = (LinearLayout) findViewById(R.id.city_names_layout);
        cityGo = (TextView) findViewById(R.id.choose_location_go);
        kanth = (TextView) findViewById(R.id.choose_city_kanth);
        chandpur = (TextView) findViewById(R.id.choose_city_chandpur);
        moradabad = (TextView) findViewById(R.id.choose_city_moradabad);
        noCurrentLocation = (TextView) findViewById(R.id.no_current_location);
        currentLocationCard = (CardView) findViewById(R.id.current_location_card);
        textLatitude = (TextView) findViewById(R.id.text_latitude);
        textLongitude = (TextView) findViewById(R.id.text_longitude);
        textLatitude2 = (TextView) findViewById(R.id.text_latitude2);
        textLongitude2 = (TextView) findViewById(R.id.text_longitude2);
        chooseCurrentLocation = (TextView) findViewById(R.id.choose_current_location);
        coordinateRef = FirebaseDatabase.getInstance().getReference().child("Shop Distances");
        progressBar = (ProgressBar) findViewById(R.id.linear_progress_bar);
        addProgressBar = (ProgressBar) findViewById(R.id.location_address_list_progress_bar);
        manualAddress = (TextView) findViewById(R.id.manual_address);
        addressLayout = (RelativeLayout) findViewById(R.id.location_address_list_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Animation open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.order_track_open);
        Animation close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.city_go_anim);

        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            getLocation();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }

        kanth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityGo.setVisibility(View.VISIBLE);
                cityChoose.setText("Kanth");
                cityNamesLayout.startAnimation(close);
                cityNamesLayout.setVisibility(View.GONE);
                addRef = FirebaseDatabase.getInstance().getReference().child("ManualAddress").child("Kanth");
                displayaddressList();
                address = "Kanth";
                coordinateRef.child("Kanth").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String latitude = snapshot.child("Latitude").getValue().toString();
                            String longitude = snapshot.child("Longitude").getValue().toString();

                            textLatitude.setText(latitude);
                            textLongitude.setText(longitude);
                            lat2 = Double.valueOf(textLatitude.getText().toString());
                            long2 = Double.valueOf(textLongitude.getText().toString());
                            lat1 = Double.valueOf(textLatitude2.getText().toString());
                            long1 = Double.valueOf(textLongitude2.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        chandpur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityGo.setVisibility(View.VISIBLE);
                cityChoose.setText("Chandpur");
                cityNamesLayout.startAnimation(close);
                address = "Chandpur";
                cityNamesLayout.setVisibility(View.GONE);
                addRef = FirebaseDatabase.getInstance().getReference().child("ManualAddress").child("Chandpur");
                displayaddressList();
                coordinateRef.child("Chandpur").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String latitude = snapshot.child("Latitude").getValue().toString();
                            String longitude = snapshot.child("Longitude").getValue().toString();

                            textLatitude.setText(latitude);
                            textLongitude.setText(longitude);
                            lat2 = Double.valueOf(textLatitude.getText().toString());
                            long2 = Double.valueOf(textLongitude.getText().toString());
                            lat1 = Double.valueOf(textLatitude2.getText().toString());
                            long1 = Double.valueOf(textLongitude2.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        moradabad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityGo.setVisibility(View.VISIBLE);
                cityChoose.setText("Moradabad");
                address = "Moradabad";
                cityNamesLayout.startAnimation(close);
                cityNamesLayout.setVisibility(View.GONE);
                addRef = FirebaseDatabase.getInstance().getReference().child("ManualAddress").child("Moradabad");
                displayaddressList();
                coordinateRef.child("Moradabad").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String latitude = snapshot.child("Latitude").getValue().toString();
                            String longitude = snapshot.child("Longitude").getValue().toString();

                            textLatitude.setText(latitude);
                            textLongitude.setText(longitude);
                            lat2 = Double.valueOf(textLatitude.getText().toString());
                            long2 = Double.valueOf(textLongitude.getText().toString());
                            lat1 = Double.valueOf(textLatitude2.getText().toString());
                            long1 = Double.valueOf(textLongitude2.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        addressList = (RecyclerView) findViewById(R.id.location_address_list);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setStackFromEnd(false);
        addressList.setLayoutManager(linearLayoutManager1);

        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNameLayout) {
                    cityNamesLayout.setVisibility(View.GONE);
                    isNameLayout = false;
                } else {
                    cityNamesLayout.setVisibility(View.VISIBLE);
                    isNameLayout = true;
                }
            }
        });
        cityGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cityChoose.getText().toString().equals("location...")) {
                    Toast.makeText(LocationActivity.this, "Please choose city...", Toast.LENGTH_SHORT).show();
                } else {

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        // notify user
                        final AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        getLocation();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        dialog.cancel();
                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            currentLocationCard.setVisibility(View.VISIBLE);
                            chooseCurrentLocation.setText("Fetching Details...");
                            getLocation();
                        } else {
                            ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);

                        }
                    }

                }

            }
        });

        confirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });


        if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);

        }

        Animation open1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.order_track_open);
        Animation close1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.order_track_close);

        manualAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (manualAddress.getText().toString().equals("Confirm")) {
                    manualAddress.setText("Select Address Manually");
                    addressLayout.startAnimation(close1);
                    addressLayout.setVisibility(View.GONE);
                    position = -1;
                    isAddress = false;
                    Intent intent = new Intent(getApplicationContext(), PhoneVerificationActivity.class);
                    intent.putExtra("Address", address);
                    startActivity(intent);

                } else {
                    if (!TextUtils.isEmpty(address)) {
                        if (isAddress) {
                            addressLayout.startAnimation(close1);
                            addressLayout.setVisibility(View.GONE);
                            isAddress = false;
                        } else {
                            addressLayout.startAnimation(open1);
                            addressLayout.setVisibility(View.VISIBLE);
                            isAddress = true;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please choose your city first...", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                        List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        lat1 = location.getLatitude();
                        long1 = location.getLongitude();
                        textLatitude2.setText(String.valueOf(lat1));
                        textLongitude2.setText(String.valueOf(long1));
                        distance(lat1, long1, lat2, long2);
                        //chooseCurrentLocation.setText(address.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void checkValidations() {
        if (chooseCurrentLocation.getText().toString().equals("current location")||chooseCurrentLocation.getText().toString().equals("Fetching Details..."))
        {
            Toast.makeText(this, "Please choose your city...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(),PhoneVerificationActivity.class);
            intent.putExtra("Address",chooseCurrentLocation.getText().toString());
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 44 : {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser!=null)
        {
            sendUserToMainActivity();
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(LocationActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void distance(double lat1, double long1, double lat2, double long2)
    {
        double longDiff = long1 - long2;
        double distance = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(longDiff));
        distance = Math.acos(distance);
        distance = rad2deg(distance);
        distance = distance * 60 * 1.1515;
        distance = distance * 1.609344;
        if (distance<=10)
        {
            progressBar.setVisibility(View.GONE);
            noCurrentLocation.setVisibility(View.GONE);
            chooseCurrentLocation.setVisibility(View.VISIBLE);
            confirmLocation.setVisibility(View.VISIBLE);
            chooseCurrentLocation.setText(cityChoose.getText().toString());
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            chooseCurrentLocation.setVisibility(View.GONE);
            noCurrentLocation.setVisibility(View.VISIBLE);
            confirmLocation.setVisibility(View.INVISIBLE);
            chooseCurrentLocation.setText(null);
        }
        Toast.makeText(this,"Distance : "+distance,Toast.LENGTH_SHORT).show();

    }

    private double rad2deg(double distance) {
        return (distance * 180.0 / Math.PI);
    }

    private double deg2rad(double lat1) {
        return (lat1*Math.PI/180.0);
    }

    private void displayaddressList() {
        Query sort = addRef.orderByChild("count");
        FirebaseRecyclerAdapter<MyAddressAdapter, LocationAddressViewHolder> fra =
                new FirebaseRecyclerAdapter<MyAddressAdapter, LocationAddressViewHolder>(
                        MyAddressAdapter.class,
                        R.layout.cart_address_layout,
                        LocationAddressViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(LocationAddressViewHolder locationAddressViewHolder, MyAddressAdapter myAddressAdapter, int i) {

                        TextView textView = (TextView) locationAddressViewHolder.mView.findViewById(R.id.cart_address_layout_text);

                        locationAddressViewHolder.setAddress(myAddressAdapter.getAddress());

                        addProgressBar.setVisibility(View.GONE);
                        locationAddressViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                position = i;
                                notifyDataSetChanged();
                                manualAddress.setText("Confirm");
                            }

                        });

                        if (position==i)
                        {
                            textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                            textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                        }
                        else
                        {
                            textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                            textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                        }
                    }
                };
        addressList.setAdapter(fra);
    }

    public static class LocationAddressViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public LocationAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setAddress(String address)
        {
            TextView addressText = (TextView) mView.findViewById(R.id.cart_address_layout_text);
            addressText.setText(address);
        }
    }
}