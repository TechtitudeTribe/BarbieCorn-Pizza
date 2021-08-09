package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShopDetailsActivity extends AppCompatActivity {

    private RecyclerView shopDetailsList;
    private DatabaseReference shopRef,userRef;
    private FirebaseAuth mAuth;
    private String currentUser,contactNumber;
    private static final int REQUEST_CALL = 1;
    private ProgressBar shopProgressBar;
    private RelativeLayout shopCloseLayout;
    private ImageView shopCloseButton;
    private TextView shopAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        shopAddress = (TextView) findViewById(R.id.menu_fragment_shop_address);
        shopAddress.setText(getIntent().getStringExtra("shopAddress"));

        shopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(getIntent().getStringExtra("shopAddress"));

        shopCloseLayout = (RelativeLayout) findViewById(R.id.shop_close_relative_layout);
        shopCloseButton = (ImageView) findViewById(R.id.shop_details_close);
        shopProgressBar = (ProgressBar) findViewById(R.id.shop_details_list_progress_bar);

        shopDetailsList = (RecyclerView) findViewById(R.id.shop_details_list);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setStackFromEnd(false);
        shopDetailsList.setLayoutManager(linearLayoutManager1);


        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);
        shopCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopCloseLayout.startAnimation(close);
                shopCloseLayout.setVisibility(View.GONE);
            }
        });


        displayShopList();
    }

    private void displayShopList() {
        Query sort = shopRef.orderByChild("count");
        FirebaseRecyclerAdapter<ShopDetailsAdapter,ShopDetailsViewHolder> frc =
                new FirebaseRecyclerAdapter<ShopDetailsAdapter, ShopDetailsViewHolder>(
                        ShopDetailsAdapter.class,
                        R.layout.shop_details_layout,
                        ShopDetailsViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(ShopDetailsViewHolder shopDetailsViewHolder, ShopDetailsAdapter shopDetailsAdapter, int i) {

                        //ImageView status = (ImageView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_status);
                        ProgressBar progressBar = (ProgressBar) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_progress_bar);
                        ImageView phone = (ImageView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_phone);
                        ImageView message = (ImageView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_mail);
                        Animation leftAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left_animation);
                        Animation bottomAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_animation);
                        Animation open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_open);
                        TextView name = (TextView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_name);
                        TextView status = (TextView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_status);
                        View relativeLayout = (View) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_relative_layout);

                        shopDetailsViewHolder.setShopName(shopDetailsAdapter.getShopName());
                        shopDetailsViewHolder.setShopSchedule(shopDetailsAdapter.getShopSchedule());
                        shopDetailsViewHolder.setShopFrontImage(shopDetailsAdapter.getShopFrontImage(),getApplicationContext());
                        shopProgressBar.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        String key = getRef(i).getKey();

                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                status.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                status.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                status.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                status.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                name.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }


                        if (shopDetailsAdapter.getShopStatus().equals("Open"))
                        {
                            status.setText("Open");
                        }
                        else
                        {
                            status.setText("Close");
                        }

                        shopDetailsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                shopRef.child(key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists())
                                        {
                                            String status = snapshot.child("shopStatus").getValue().toString();
                                            if (status.equals("Open"))
                                            {
                                                shopCloseLayout.setVisibility(View.GONE);
                                                Intent intent = new Intent(getApplicationContext(),MyMenuActivity.class);
                                                intent.putExtra("key",key);
                                                intent.putExtra("userId",shopDetailsAdapter.getUserId());
                                                intent.putExtra("shopName",shopDetailsAdapter.getShopName());
                                                intent.putExtra("deliveryCharge",shopDetailsAdapter.getDeliveryCharge());
                                                intent.putExtra("upi",shopDetailsAdapter.getUpi());
                                                intent.putExtra("shopAddress",getIntent().getStringExtra("shopAddress"));
                                                intent.putExtra("category",getIntent().getStringExtra("category"));
                                                startActivity(intent);
                                            }
                                            else
                                            {
                                                shopCloseLayout.setVisibility(View.VISIBLE);
                                                shopCloseLayout.startAnimation(open);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });

                        phone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                contactNumber = shopDetailsAdapter.getShopContact();
                                makePhoneCall(contactNumber);
                            }
                        });

                        message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent messageIntent = openWhatsapp(getApplicationContext(),"+91 "+shopDetailsAdapter.getShopContact());
                                startActivity(messageIntent);
                            }
                        });

                    }
                };
        shopDetailsList.setAdapter(frc);
    }

    public static class ShopDetailsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ShopDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setShopName(String shopName)
        {
            TextView name = (TextView) mView.findViewById(R.id.shop_details_name);
            name.setText(shopName);
        }

        public void setShopSchedule(String shopSchedule)
        {
            TextView schedule = (TextView) mView.findViewById(R.id.shop_details_schedule);
            schedule.setText(shopSchedule);
        }


        public void setShopFrontImage(String shopFrontImage, Context context)
        {
            ImageView image = (ImageView) mView.findViewById(R.id.shop_details_shop_image);
            Picasso.with(context).load(shopFrontImage).placeholder(R.drawable.ic_baseline_shop_default).into(image);
        }
    }

    private void makePhoneCall(String contactNumber) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + contactNumber;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(contactNumber);
            } else {
                Toast.makeText(getApplicationContext(),"Permission Denied...",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Intent openWhatsapp(Context context, String whatsappNumber) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.whatsapp", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + whatsappNumber));
        } catch (Exception ex) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + whatsappNumber));
        }
    }
}