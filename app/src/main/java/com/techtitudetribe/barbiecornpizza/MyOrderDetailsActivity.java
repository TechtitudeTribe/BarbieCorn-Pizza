package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyOrderDetailsActivity extends AppCompatActivity {

    private DatabaseReference orderRef;
    private FirebaseAuth firebaseAuth;
    private String currentUser,key;
    private TextView trackOrder;
    private RelativeLayout trackOrderLayout;
    private boolean isOpen = false;
    private View confirmView, cookingView, outView, deliveredView,cancelledView;
    private ImageView confirmImage, cookingImage, outImage, deliveredImage,cancelledImage;
    private LinearLayout confirmLayout, cookingLayout, outLayout, canelledLayout, deliveredLayout;
    private TextView itemName, itemPrice, itemDescription, itemAddress, itemPaymentMethod, itemOrderType;
    private ProgressBar progressBar;
    private TextView cancelledOrderText,readyOutOrderText,deliveredCompletedOrderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);

        key = getIntent().getExtras().getString("key");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        orderRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyOrders").child(key);

        itemName = (TextView) findViewById(R.id.order_item_description_name);
        itemPrice = (TextView) findViewById(R.id.order_item_description_price);
        itemDescription = (TextView) findViewById(R.id.order_item_description_des);
        itemAddress = (TextView) findViewById(R.id.order_item_description_address);
        itemPaymentMethod = (TextView) findViewById(R.id.order_item_description_payment_method);
        itemOrderType = (TextView) findViewById(R.id.order_item_description_order_type);
        progressBar = (ProgressBar) findViewById(R.id.order_item_description_progress_bar);
        cancelledOrderText = (TextView) findViewById(R.id.cancelled_order_text);
        readyOutOrderText = (TextView) findViewById(R.id.ready_out_order_text);
        deliveredCompletedOrderText = (TextView) findViewById(R.id.delivered_complete_order_text);

        confirmView = (View) findViewById(R.id.track_order_view_confirm);
        cancelledView = (View) findViewById(R.id.track_order_view_cancelled);
        cookingView = (View) findViewById(R.id.track_order_view_cooking);
        outView = (View) findViewById(R.id.track_order_view_out);
        deliveredView = (View) findViewById(R.id.track_order_view_delivered);

        cancelledImage = (ImageView) findViewById(R.id.track_order_image_cancelled);
        confirmImage = (ImageView) findViewById(R.id.track_order_image_confirm);
        cookingImage = (ImageView) findViewById(R.id.track_order_image_cooking);
        outImage = (ImageView) findViewById(R.id.track_order_image_out);
        deliveredImage = (ImageView) findViewById(R.id.track_order_image_delivered);

        canelledLayout = (LinearLayout) findViewById(R.id.cancelled_layout);
        confirmLayout = (LinearLayout) findViewById(R.id.confirmed_layout);
        cookingLayout = (LinearLayout) findViewById(R.id.cooking_layout);
        outLayout = (LinearLayout) findViewById(R.id.out_for_delivery_layout);
        deliveredLayout = (LinearLayout) findViewById(R.id.delivery_layout);

        trackOrder = (TextView) findViewById(R.id.order_details_track_order);
        trackOrderLayout = (RelativeLayout) findViewById(R.id.order_details_track_order_relative_layout);

        Animation open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_open);
        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String in = snapshot.child("itemNames").getValue().toString();
                    String ip = snapshot.child("itemTotalAmount").getValue().toString();
                    String id = snapshot.child("itemDescription").getValue().toString();
                    String ia = snapshot.child("address").getValue().toString();
                    String ipm = snapshot.child("paymentMethod").getValue().toString();
                    String iot = snapshot.child("orderType").getValue().toString();

                    itemName.setText(in);
                    itemDescription.setText(id);
                    itemPrice.setText(ip);
                    itemAddress.setText(ia);
                    itemPaymentMethod.setText(" : "+ipm);
                    itemOrderType.setText(" : "+iot);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        trackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen)
                {
                    trackOrderLayout.startAnimation(close);
                    trackOrderLayout.setVisibility(View.GONE);
                    isOpen=false;
                }
                else
                {
                    trackOrderLayout.startAnimation(open);
                    trackOrderLayout.setVisibility(View.VISIBLE);
                    isOpen=true;
                }
            }
        });

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String status = snapshot.child("itemStatus").getValue().toString();

                    if (status.equals("Ordered"))
                    {
                        confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                        cancelledView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                        cookingView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                        outView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                        deliveredView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                        confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                        cookingImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                        outImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                        cancelledImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                        deliveredImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                        cancelledOrderText.setText("Cancelled/Confirmed");
                        readyOutOrderText.setText("Food is Ready/On The Way");
                        deliveredCompletedOrderText.setText("Order Completed/Delivered");

                    }else
                        if (status.equals("Cancelled"))
                        {
                            confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                            cancelledView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                            confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                            cancelledImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                            cancelledOrderText.setText("Cancelled");
                            outLayout.setVisibility(View.GONE);
                            cookingLayout.setVisibility(View.GONE);
                            deliveredLayout.setVisibility(View.GONE);
                        }
                        else
                        if(status.equals("Cooking"))
                        {
                            confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                            cookingView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                            outView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                            deliveredView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                            confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                            cookingImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                            outImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                            deliveredImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                            canelledLayout.setVisibility(View.GONE);
                            readyOutOrderText.setText("Food is Ready/On The Way");
                            deliveredCompletedOrderText.setText("Order Completed/Delivered");
                        }else
                            if (status.equals("Food is Ready"))
                            {
                                confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                cookingView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                outView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                deliveredView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                                confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                cookingImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                outImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                deliveredImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                                canelledLayout.setVisibility(View.GONE);
                                readyOutOrderText.setText("Food is Ready");
                                deliveredCompletedOrderText.setText("Order Completed");
                            }else
                            if (status.equals("On The Way"))
                            {
                                confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                cookingView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                outView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                deliveredView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                                confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                cookingImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                outImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                deliveredImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                                canelledLayout.setVisibility(View.GONE);
                                readyOutOrderText.setText("On The Way");
                                deliveredCompletedOrderText.setText("Delivered");
                            }else
                                if (status.equals("Completed"))
                            {
                                confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                cookingView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                outView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                deliveredView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                cookingImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                outImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                deliveredImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                canelledLayout.setVisibility(View.GONE);
                                readyOutOrderText.setText("Food is Ready");
                                deliveredCompletedOrderText.setText("Order Completed");
                            }else
                                {
                                    confirmView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                    cookingView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                    outView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                    deliveredView.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                                    confirmImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                    cookingImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                    outImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                    deliveredImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                                    canelledLayout.setVisibility(View.GONE);
                                    readyOutOrderText.setText("On The Way");
                                    deliveredCompletedOrderText.setText("Delivered");
                                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onBackPressed() {

        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);
        if(isOpen)
        {
            trackOrderLayout.startAnimation(close);
            trackOrderLayout.setVisibility(View.GONE);
            isOpen=false;
        }
        else
        {
            super.onBackPressed();
        }

    }


}