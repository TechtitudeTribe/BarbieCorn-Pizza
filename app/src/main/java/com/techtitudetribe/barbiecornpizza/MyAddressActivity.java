package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MyAddressActivity extends AppCompatActivity {

    private DatabaseReference addressRef;
    private FirebaseAuth firebaseAuth;
    private String currentUser;
    private RecyclerView recyclerView;
    private RelativeLayout newAddressLayout, noAddress;
    private FloatingActionButton floatingActionButton;
    private Boolean rotation=true;
    private TextView newAddressCancel, newAddressConfirm,chooseCurrentLocation;
    private long count= 0;
    private EditText houseNo, area, landmark, town, pincode;
    private ProgressBar progressBar,newAddProgressBar;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MyAddressAdapter myAddressAdapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        addressRef = FirebaseDatabase.getInstance().getReference().child("MyAddresses").child(currentUser);
        newAddressLayout = (RelativeLayout) findViewById(R.id.add_new_address_layout);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.add_address_floating_button);

        myAddressAdapter1 = new MyAddressAdapter();

        noAddress = (RelativeLayout) findViewById(R.id.no_address_layout);
        newAddressCancel = (TextView) findViewById(R.id.add_new_address_cancel_button);
        newAddressConfirm = (TextView) findViewById(R.id.add_new_address_add_button);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        houseNo = (EditText) findViewById(R.id.new_address_house_no);
        area = (EditText) findViewById(R.id.new_address_area);
        landmark = (EditText) findViewById(R.id.new_address_landmark);
        town = (EditText) findViewById(R.id.new_address_town);
        pincode = (EditText) findViewById(R.id.new_address_pincode);
        progressBar = (ProgressBar) findViewById(R.id.my_address_activity_progress_bar);
        newAddProgressBar = (ProgressBar) findViewById(R.id.add_new_address_progress_bar);
        chooseCurrentLocation = (TextView) findViewById(R.id.choose_current_location);

        Animation zoom_in = AnimationUtils.loadAnimation(this,R.anim.zoom_in_animation);
        Animation zoom_out = AnimationUtils.loadAnimation(this,R.anim.zoom_out_animation);
        Animation clockwise = AnimationUtils.loadAnimation(this,R.anim.clockwise_rotation);
        Animation anticlockwise = AnimationUtils.loadAnimation(this,R.anim.anticlockwise_rotation);

        chooseCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MyAddressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(MyAddressActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

        addressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    noAddress.setVisibility(View.GONE);
                    displaAddressList();
                    count = snapshot.getChildrenCount();
                }
                else
                {
                    noAddress.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    count= 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        newAddressCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAddressLayout.setVisibility(View.GONE);
                newAddressLayout.startAnimation(zoom_out);
                floatingActionButton.startAnimation(clockwise);
                rotation=true;
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        newAddressConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(houseNo.getText().toString()))
                {
                    Toast.makeText(MyAddressActivity.this, "Invalid House No...", Toast.LENGTH_SHORT).show();
                }else
                if (TextUtils.isEmpty(area.getText().toString()))
                {
                    Toast.makeText(MyAddressActivity.this, "Area is missing...", Toast.LENGTH_SHORT).show();
                }
                else
                if (TextUtils.isEmpty(landmark.getText().toString()))
                {
                    Toast.makeText(MyAddressActivity.this, "Missing landmark...", Toast.LENGTH_SHORT).show();
                }
                else
                if (TextUtils.isEmpty(town.getText().toString()))
                {
                    Toast.makeText(MyAddressActivity.this, "Fill town name...", Toast.LENGTH_SHORT).show();
                }
                else
                if (TextUtils.isEmpty(pincode.getText().toString()))
                {
                    Toast.makeText(MyAddressActivity.this, "Pincode is missing...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    newAddProgressBar.setVisibility(View.VISIBLE);
                    myAddressAdapter1.setAddress(houseNo.getText().toString()+", "+area.getText().toString()+", near "+landmark.getText().toString()+", "+town.getText().toString()+" - "+pincode.getText().toString());
                    myAddressAdapter1.setCount(count+1);
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            addressRef.child("Address"+currentDateandTime).setValue(myAddressAdapter1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    addressRef.child("Address"+currentDateandTime).addValueEventListener(valueEventListener);
                    newAddressLayout.setVisibility(View.GONE);
                    newAddressLayout.startAnimation(zoom_out);
                    floatingActionButton.startAnimation(clockwise);
                    rotation=true;
                    newAddProgressBar.setVisibility(View.GONE);
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rotation)
                {
                    newAddressLayout.setVisibility(View.VISIBLE);
                    newAddressLayout.startAnimation(zoom_in);
                    floatingActionButton.startAnimation(anticlockwise);
                    rotation=false;
                }
                else
                {
                    newAddressLayout.setVisibility(View.GONE);
                    newAddressLayout.startAnimation(zoom_out);
                    floatingActionButton.startAnimation(clockwise);
                    rotation=true;
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.my_address_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);


    }

    private void displaAddressList() {
        Query sort = addressRef.orderByChild("count");
        FirebaseRecyclerAdapter<MyAddressAdapter,MyAddressViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MyAddressAdapter, MyAddressViewHolder>(
                        MyAddressAdapter.class,
                        R.layout.my_addresses_layout,
                        MyAddressViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(MyAddressViewHolder myAddressViewHolder, MyAddressAdapter myAddressAdapter, int i) {
                        TextView relativeLayout = (TextView) myAddressViewHolder.mView.findViewById(R.id.my_addresses_text);
                        ImageView delete = (ImageView) myAddressViewHolder.mView.findViewById(R.id.my_addresses_delete);
                        String key = getRef(i).getKey();

                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                //textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                //textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                //textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                //textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }
                        myAddressViewHolder.setAddress(myAddressAdapter.getAddress());
                        progressBar.setVisibility(View.GONE);

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addressRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(getApplicationContext(),"Address removed successfully...",Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(MyAddressActivity.this, "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyAddressViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public MyAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setAddress(String address)
        {
            TextView textView = (TextView) mView.findViewById(R.id.my_addresses_text);
            textView.setText(address);
        }
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
                        Geocoder geocoder = new Geocoder(MyAddressActivity.this, Locale.getDefault());
                        List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        town.setText(address.get(0).getLocality());
                        pincode.setText(address.get(0).getPostalCode());
                        area.setText(address.get(0).getSubLocality());
                        //chooseCurrentLocation.setText(address.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}