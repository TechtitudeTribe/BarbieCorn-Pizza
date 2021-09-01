package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class FestivalOfferActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference menuRef,addRef;
    private String currentUser, shopAddress, pizzaSize="Medium";
    private RecyclerView listOne, listTwo, listThree,addressList;
    private CardView mediumLayout, largeLayout, oneLayout, twoLayout, threeLayout;
    private TextView mediumSize, largeSize, one, two, three;
    private TextView totalCost,placeOrder;
    private ProgressBar progressBarOne,progressBarTwo,progressBarThree;
    private FrameLayout frameLayoutOne, frameLayoutTwo, frameLayoutThree, addressFrame;
    private LinearLayout oneLinearLayout, twoLinearLayout, threeLinearLayout;
    private TextView oneText, twoText, threeText;
    private View oneView, twoView, threeView;
    private Integer positionOne = -1, positionTwo = -1, positionThree = -1, positionAdd=-1;
    private String offerStatus="active";
    private String priceCondition="";
    private String key,address;
    private TextView sellerId,noAddress;
    private ImageView back;
    private RelativeLayout addressLayout;
    private ProgressBar addProgressBar;
    private Boolean isOpen = false;
    private EditText customAddress;
    private TextView customAddressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival_offer);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        addRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyAddresses");

        shopAddress = getIntent().getStringExtra("key");
        menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress);

        back = (ImageView) findViewById(R.id.festival_offers_back);

        sellerId = (TextView) findViewById(R.id.festival_offer_seller_id);
        mediumLayout = (CardView) findViewById(R.id.festival_offer_pizza_size_medium_layout);
        largeLayout = (CardView) findViewById(R.id.festival_offer_pizza_size_large_layout);
        oneLayout = (CardView) findViewById(R.id.festival_offers_pizza_one_card);
        twoLayout = (CardView) findViewById(R.id.festival_offers_pizza_two_card);
        threeLayout = (CardView) findViewById(R.id.festival_offers_pizza_three_card);

        mediumSize = (TextView) findViewById(R.id.festival_offer_pizza_size_medium);
        largeSize = (TextView) findViewById(R.id.festival_offer_pizza_size_large);
        one = (TextView) findViewById(R.id.festival_offers_pizza_one);
        two = (TextView) findViewById(R.id.festival_offers_pizza_two);
        three = (TextView) findViewById(R.id.festival_offers_pizza_three);

        oneLinearLayout = (LinearLayout) findViewById(R.id.festival_offers_pizza_one_linear);
        twoLinearLayout = (LinearLayout) findViewById(R.id.festival_offers_pizza_two_linear);
        threeLinearLayout = (LinearLayout) findViewById(R.id.festival_offers_pizza_three_linear);

        placeOrder = (TextView) findViewById(R.id.festival_offers_place_order);
        totalCost = (TextView) findViewById(R.id.festival_offers_total_cost);
        oneText = (TextView) findViewById(R.id.festival_offers_pizza_one_text);
        twoText = (TextView) findViewById(R.id.festival_offers_pizza_two_text);
        threeText = (TextView) findViewById(R.id.festival_offers_pizza_three_text);

        oneView = (View) findViewById(R.id.festival_offers_pizza_one_view);
        twoView = (View) findViewById(R.id.festival_offers_pizza_two_view);
        threeView = (View) findViewById(R.id.festival_offers_pizza_three_view);

        progressBarOne = (ProgressBar) findViewById(R.id.festival_offers_progress_bar1);
        progressBarTwo = (ProgressBar) findViewById(R.id.festival_offers_progress_bar2);
        progressBarThree = (ProgressBar) findViewById(R.id.festival_offers_progress_bar3);

        frameLayoutOne = (FrameLayout) findViewById(R.id.festival_offers_pizza_frame_layout1);
        frameLayoutTwo = (FrameLayout) findViewById(R.id.festival_offers_pizza_frame_layout2);
        frameLayoutThree = (FrameLayout) findViewById(R.id.festival_offers_pizza_frame_layout3);

        customAddressButton = (TextView) findViewById(R.id.custom_address_festival_offer_button);
        customAddress = (EditText) findViewById(R.id.custom_address_festival_offer);

        listOne = (RecyclerView) findViewById(R.id.festival_offers_pizza_layout1);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setStackFromEnd(false);
        listOne.setLayoutManager(linearLayoutManager1);

        listTwo = (RecyclerView) findViewById(R.id.festival_offers_pizza_layout2);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(false);
        linearLayoutManager2.setStackFromEnd(false);
        listTwo.setLayoutManager(linearLayoutManager2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FestivalOfferActivity.super.onBackPressed();
            }
        });

        listThree = (RecyclerView) findViewById(R.id.festival_offers_pizza_layout3);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(this);
        linearLayoutManager3.setReverseLayout(false);
        linearLayoutManager3.setStackFromEnd(false);
        listThree.setLayoutManager(linearLayoutManager3);

        noAddress = (TextView) findViewById(R.id.festival_offer_no_address_found);
        addressFrame = (FrameLayout) findViewById(R.id.festival_offer_address_list_frame);
        addressLayout = (RelativeLayout) findViewById(R.id.festival_offer_address_list_layout);
        addProgressBar = (ProgressBar) findViewById(R.id.festival_offer_address_list_progress_bar);

        addressList = (RecyclerView) findViewById(R.id.festival_offer_address_list);
        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(this);
        linearLayoutManager4.setReverseLayout(false);
        linearLayoutManager4.setStackFromEnd(false);
        addressList.setLayoutManager(linearLayoutManager4);

        customAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(customAddress.getText().toString().trim()))
                {
                    Toast.makeText(FestivalOfferActivity.this, "Please fill your address details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    address = customAddress.getText().toString().trim();
                    customAddressButton.setText("Saved!");
                }
            }
        });

        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String si = snapshot.child("userId").getValue().toString();
                    sellerId.setText(si);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    noAddress.setVisibility(View.GONE);
                    addressFrame.setVisibility(View.VISIBLE);
                    displayFestivalOfferAddressList();
                }
                else
                {
                    noAddress.setVisibility(View.VISIBLE);
                    addressFrame.setVisibility(View.INVISIBLE);
                    addProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mediumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediumSize.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                mediumSize.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                largeSize.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                largeSize.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                if (!pizzaSize.equals("Medium"))
                {
                    pizzaSize = "Medium";
                    one.setText("Pizza Name");
                    two.setText("Pizza Name");
                    three.setText("Pizza Name");
                    positionOne = -1;
                    positionThree = -1;
                    positionTwo = -1;
                    one.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    two.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    three.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    twoLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    threeLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    oneText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    threeText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    threeView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    frameLayoutOne.setVisibility(View.VISIBLE);
                    frameLayoutTwo.setVisibility(View.GONE);
                    frameLayoutThree.setVisibility(View.GONE);
                    displayMenuItems1();
                    totalCost.setText("000");
                }
            }
        });

        largeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediumSize.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                mediumSize.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                largeSize.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                largeSize.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                if (!pizzaSize.equals("Large"))
                {
                    pizzaSize = "Large";
                    one.setText("Pizza Name");
                    two.setText("Pizza Name");
                    three.setText("Pizza Name");
                    positionOne = -1;
                    positionThree = -1;
                    positionTwo = -1;
                    one.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    two.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    three.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    twoLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    threeLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    oneText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    threeText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    threeView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    frameLayoutOne.setVisibility(View.VISIBLE);
                    frameLayoutTwo.setVisibility(View.GONE);
                    frameLayoutThree.setVisibility(View.GONE);
                    displayMenuItems1();
                    totalCost.setText("000");
                }
            }
        });

        oneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                two.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                three.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                oneLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                twoLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                threeLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                oneText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                twoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                threeText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                oneView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                twoView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                threeView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                frameLayoutOne.setVisibility(View.VISIBLE);
                frameLayoutTwo.setVisibility(View.GONE);
                frameLayoutThree.setVisibility(View.GONE);
                displayMenuItems1();
            }
        });

        twoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!one.getText().toString().equals("Pizza Name"))
                {
                    one.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    two.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    three.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    threeLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    oneText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    twoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    threeText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    twoView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    threeView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    frameLayoutOne.setVisibility(View.GONE);
                    frameLayoutTwo.setVisibility(View.VISIBLE);
                    frameLayoutThree.setVisibility(View.GONE);
                    displayMenuItems2();
                }
                else
                {
                    Toast.makeText(FestivalOfferActivity.this, getResources().getString(R.string.selectYourFirstPizza), Toast.LENGTH_SHORT).show();
                }

            }
        });

        threeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (one.getText().toString().equals("Pizza Name"))
                {
                    Toast.makeText(FestivalOfferActivity.this, getResources().getString(R.string.selectYourFirstPizza), Toast.LENGTH_SHORT).show();
                }
                else if(two.getText().toString().equals("Pizza Name"))
                {
                    Toast.makeText(FestivalOfferActivity.this, getResources().getString(R.string.selectYourSecondPizza), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    one.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    two.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    three.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    oneLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    threeLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    twoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    threeText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    oneView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    twoView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    threeView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    frameLayoutOne.setVisibility(View.GONE);
                    frameLayoutTwo.setVisibility(View.GONE);
                    frameLayoutThree.setVisibility(View.VISIBLE);
                    displayMenuItems3();
                }

            }
        });

        Animation open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_open);
        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (one.getText().toString().equals("Pizza Name"))
                {
                    Toast.makeText(FestivalOfferActivity.this, getResources().getString(R.string.selectYourFirstPizza), Toast.LENGTH_SHORT).show();
                }
                else
                if (two.getText().toString().equals("Pizza Name"))
                {
                    Toast.makeText(FestivalOfferActivity.this, getResources().getString(R.string.selectYourSecondPizza), Toast.LENGTH_SHORT).show();
                }
                else
                if (three.getText().toString().equals("Pizza Name"))
                {
                    Toast.makeText(FestivalOfferActivity.this, "Select your third pizza...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (placeOrder.getText().toString().equals("Select an Address")||placeOrder.getText().toString().equals("एक पता चुनें"))
                    {
                        if(isOpen)
                        {
                            addressLayout.startAnimation(close);
                            addressLayout.setVisibility(View.GONE);
                            isOpen=false;
                        }
                        else
                        {
                            addressLayout.startAnimation(open);
                            addressLayout.setVisibility(View.VISIBLE);
                            isOpen=true;
                        }
                    }
                    else
                    {
                        if (TextUtils.isEmpty(address))
                        {
                            Toast.makeText(FestivalOfferActivity.this, "Please choose an address", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent intent = new Intent(FestivalOfferActivity.this,OrderConfirmationActivity.class);
                            intent.putExtra("itemName",one.getText().toString()+", "+two.getText().toString()+" and "+three.getText().toString());
                            intent.putExtra("totalPrice",totalCost.getText().toString());
                            intent.putExtra("itemDescription","Offer Applied : Festival Offers"+"\nSize : "+pizzaSize);
                            intent.putExtra("address",address);
                            intent.putExtra("sellerId",sellerId.getText().toString());
                            intent.putExtra("shopName",shopAddress);
                            intent.putExtra("itemNumbers","1");
                            intent.putExtra("key",key);
                            startActivity(intent);
                        }
                    }

                }

            }
        });

        displayMenuItems1();
        displayMenuItems2();
        displayMenuItems3();
    }

    private void displayMenuItems1() {
        Query sort = menuRef.child("MenuItem").child("VegPizza").orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter, FestivalOfferMenuViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<MenuItemAdapter, FestivalOfferMenuViewHolder>(
                MenuItemAdapter.class,
                R.layout.everyday_offer_layout,
                FestivalOfferMenuViewHolder.class,
                sort
        ) {
            @Override
            protected void populateViewHolder(FestivalOfferMenuViewHolder offerMenuViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                View view = (View) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_view);
                TextView list = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_add_list);
                TextView bottom = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_description);
                CardView listLayout = (CardView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_add_layout);
                TextView price = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_item_price);

                if (Integer.parseInt(menuItemAdapter.getMedium())>=320)
                {
                    offerMenuViewHolder.mView.setVisibility(View.VISIBLE);
                    offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (pizzaSize.equals("Medium"))
                    {
                        offerMenuViewHolder.setName(menuItemAdapter.getName());
                        offerMenuViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                        offerMenuViewHolder.setMedium(menuItemAdapter.getMedium());
                        offerMenuViewHolder.setDescription(menuItemAdapter.getDescription());
                        progressBarOne.setVisibility(View.GONE);
                    }
                    else
                    {
                        offerMenuViewHolder.setMedium(menuItemAdapter.getLarge());
                        offerMenuViewHolder.setName(menuItemAdapter.getName());
                        offerMenuViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                        offerMenuViewHolder.setDescription(menuItemAdapter.getDescription());
                        progressBarOne.setVisibility(View.GONE);
                    }
                }
                else
                {
                    offerMenuViewHolder.mView.setVisibility(View.GONE);
                    offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    progressBarOne.setVisibility(View.GONE);
                }

                switch (i%3)
                {
                    case 0 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        break;
                    case 1 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        break;
                    case 2 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        break;
                    case 3 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                        break;
                }

                listLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        positionOne = i;
                        key = getRef(i).getKey();
                        notifyDataSetChanged();
                        one.setText(menuItemAdapter.getName());
                        two.setText("Pizza Name");
                        three.setText("Pizza Name");
                        priceCondition = price.getText().toString();
                        positionTwo = -1;
                        positionThree = -1;
                        totalCost.setText(priceCondition);
                        //Picasso.with(FestivalOfferActivity.this).load(menuItemAdapter.getImage()).into(itemAddedImage);
                    }
                });

                if (positionOne==i)
                {
                    listLayout.setVisibility(View.INVISIBLE);
                }
                else
                {
                    listLayout.setVisibility(View.VISIBLE);
                }

            }
        };
        listOne.setAdapter(firebaseRecyclerAdapter);
    }
    private void displayMenuItems2() {
        Query sort = menuRef.child("MenuItem").child("VegPizza").orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter, FestivalOfferMenuViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<MenuItemAdapter, FestivalOfferMenuViewHolder>(
                MenuItemAdapter.class,
                R.layout.everyday_offer_layout,
                FestivalOfferMenuViewHolder.class,
                sort
        ) {
            @Override
            protected void populateViewHolder(FestivalOfferMenuViewHolder offerMenuViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                View view = (View) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_view);
                TextView list = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_add_list);
                TextView bottom = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_description);
                CardView listLayout = (CardView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_add_layout);
                TextView price = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_item_price);


                    if (pizzaSize.equals("Medium"))
                    {
                        if (menuItemAdapter.getMedium().equals(priceCondition))
                        {
                            offerMenuViewHolder.setName(menuItemAdapter.getName());
                            offerMenuViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                            offerMenuViewHolder.setMedium(menuItemAdapter.getMedium());
                            offerMenuViewHolder.setDescription(menuItemAdapter.getDescription());
                            progressBarTwo.setVisibility(View.GONE);
                            offerMenuViewHolder.mView.setVisibility(View.VISIBLE);
                            offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                        else
                        {
                            offerMenuViewHolder.mView.setVisibility(View.GONE);
                            offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                            progressBarTwo.setVisibility(View.GONE);
                        }

                    }
                    else
                    {
                        if (menuItemAdapter.getLarge().equals(priceCondition))
                        {
                            offerMenuViewHolder.setMedium(menuItemAdapter.getLarge());
                            offerMenuViewHolder.setName(menuItemAdapter.getName());
                            offerMenuViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                            offerMenuViewHolder.setDescription(menuItemAdapter.getDescription());
                            progressBarTwo.setVisibility(View.GONE);
                            offerMenuViewHolder.mView.setVisibility(View.VISIBLE);
                            offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                        else
                        {
                            offerMenuViewHolder.mView.setVisibility(View.GONE);
                            offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                            progressBarTwo.setVisibility(View.GONE);
                        }

                    }


                switch (i%3)
                {
                    case 0 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        break;
                    case 1 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        break;
                    case 2 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        break;
                    case 3 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                        break;
                }

                listLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        positionTwo = i;
                        key = getRef(i).getKey();
                        notifyDataSetChanged();
                        two.setText(menuItemAdapter.getName());
                        totalCost.setText(String.valueOf(2*Integer.parseInt(priceCondition)));
                        //Picasso.with(FestivalOfferActivity.this).load(menuItemAdapter.getImage()).into(itemAddedImage);
                    }
                });

                if (positionTwo==i)
                {
                    listLayout.setVisibility(View.INVISIBLE);
                }
                else
                {
                    listLayout.setVisibility(View.VISIBLE);
                }

            }
        };
        listTwo.setAdapter(firebaseRecyclerAdapter);
    }
    private void displayMenuItems3() {
        Query sort = menuRef.child("MenuItem").child("VegPizza").orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter, FestivalOfferMenuViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<MenuItemAdapter, FestivalOfferMenuViewHolder>(
                MenuItemAdapter.class,
                R.layout.everyday_offer_layout,
                FestivalOfferMenuViewHolder.class,
                sort
        ) {
            @Override
            protected void populateViewHolder(FestivalOfferMenuViewHolder offerMenuViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                View view = (View) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_view);
                TextView list = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_add_list);
                TextView bottom = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_description);
                CardView listLayout = (CardView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_add_layout);
                TextView price = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_item_price);

                if (pizzaSize.equals("Medium"))
                {
                    if (menuItemAdapter.getMedium().equals(priceCondition))
                    {
                        offerMenuViewHolder.setName(menuItemAdapter.getName());
                        offerMenuViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                        offerMenuViewHolder.setMedium(menuItemAdapter.getMedium());
                        offerMenuViewHolder.setDescription(menuItemAdapter.getDescription());
                        progressBarThree.setVisibility(View.GONE);
                        offerMenuViewHolder.mView.setVisibility(View.VISIBLE);
                        offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                    else
                    {
                        offerMenuViewHolder.mView.setVisibility(View.GONE);
                        offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                        progressBarThree.setVisibility(View.GONE);
                    }

                }
                else
                {
                    if (menuItemAdapter.getLarge().equals(priceCondition))
                    {
                        offerMenuViewHolder.setMedium(menuItemAdapter.getLarge());
                        offerMenuViewHolder.setName(menuItemAdapter.getName());
                        offerMenuViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                        offerMenuViewHolder.setDescription(menuItemAdapter.getDescription());
                        progressBarThree.setVisibility(View.GONE);
                        offerMenuViewHolder.mView.setVisibility(View.VISIBLE);
                        offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                    else
                    {
                        offerMenuViewHolder.mView.setVisibility(View.GONE);
                        offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                        progressBarThree.setVisibility(View.GONE);
                    }

                }

                switch (i%3)
                {
                    case 0 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        break;
                    case 1 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        break;
                    case 2 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        break;
                    case 3 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                        list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                        bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                        break;
                }

                listLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        positionThree = i;
                        key = getRef(i).getKey();
                        notifyDataSetChanged();
                        three.setText(menuItemAdapter.getName());
                        //Picasso.with(FestivalOfferActivity.this).load(menuItemAdapter.getImage()).into(itemAddedImage);
                    }
                });

                if (positionThree==i)
                {
                    listLayout.setVisibility(View.INVISIBLE);
                }
                else
                {
                    listLayout.setVisibility(View.VISIBLE);
                }

            }
        };
        listThree.setAdapter(firebaseRecyclerAdapter);
    }

    private void displayFestivalOfferAddressList()
    {
        Query sort = addRef.orderByChild("count");
        FirebaseRecyclerAdapter<MyAddressAdapter, FestivalOfferAddressViewHolder> fra =
                new FirebaseRecyclerAdapter<MyAddressAdapter, FestivalOfferAddressViewHolder>(
                        MyAddressAdapter.class,
                        R.layout.cart_address_layout,
                        FestivalOfferAddressViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(FestivalOfferAddressViewHolder cartAddressViewHolder, MyAddressAdapter myAddressAdapter, int i) {

                        TextView textView = (TextView) cartAddressViewHolder.mView.findViewById(R.id.cart_address_layout_text);

                        cartAddressViewHolder.setAddress(myAddressAdapter.getAddress());

                        addProgressBar.setVisibility(View.GONE);
                        cartAddressViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                positionAdd = i;
                                notifyDataSetChanged();
                                address = myAddressAdapter.getAddress();
                                placeOrder.setText(getResources().getString(R.string.placeOrder));
                            }

                        });

                        if (positionAdd==i)
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

    public static class FestivalOfferMenuViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public FestivalOfferMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(String image, Context context)
        {
            ImageView imageView = (ImageView) mView.findViewById(R.id.everyday_special_offer_image);
            Picasso.with(context).load(image).into(imageView);
        }

        public void setName(String name)
        {
            TextView textView = (TextView) mView.findViewById(R.id.everyday_special_offer_name);
            textView.setText(name);
        }

        public void setDescription(String description)
        {
            TextView textView = (TextView) mView.findViewById(R.id.everyday_special_offer_description);
            textView.setText(description);
        }

        public void setMedium(String medium)
        {
            TextView menuPrice = (TextView) mView.findViewById(R.id.everyday_special_offer_item_price);
            menuPrice.setText(medium);
        }
    }

    public static class FestivalOfferAddressViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public FestivalOfferAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setAddress(String address)
        {
            TextView addressText = (TextView) mView.findViewById(R.id.cart_address_layout_text);
            addressText.setText(address);
        }
    }
}