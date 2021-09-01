package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ExclusiveOffersActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView menuListView1,menuListView2,addressList;
    private DatabaseReference menuRef,addRef;
    private ProgressBar progressBarOne,progressBarTwo, addProgressBar;
    private ImageView back;
    private FrameLayout frameLayoutOne, frameLayoutTwo,addressFrame;
    private String priceCondition="", pizzaSize="Medium",currentUser, userId;
    private int positionOne = -1, positionTwo = -1, positionAdd = -1;
    private CardView mediumLayout, largeLayout;
    private TextView mediumSize, largeSize, oneText, twoText, one, two;
    private TextView payablePrice;
    private Boolean itemAvailable = false;
    private ImageView blockOne;
    private CardView blockTwo, oneLayout, twoLayout;
    private String offerStatus="active",shopAddress,key,address;
    private LinearLayout oneLinearLayout, twoLinearLayout;
    private View oneView, twoView;
    private TextView placeOrder, noAddress,sellerId;
    private RelativeLayout addressLayout;
    private Boolean isOpen = false;
    private EditText customAddress;
    private TextView customAddressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exclusive_offers);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        addRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyAddresses");

        shopAddress = getIntent().getStringExtra("key");
        menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress);

        back = (ImageView) findViewById(R.id.exclusive_offers_back);

        placeOrder = (TextView) findViewById(R.id.exclusive_offers_place_order);
        blockOne = (ImageView) findViewById(R.id.block_screen_one);
        blockTwo = (CardView) findViewById(R.id.block_screen_two);
        mediumLayout = (CardView) findViewById(R.id.offer_pizza_size_medium_layout);
        mediumSize = (TextView) findViewById(R.id.offer_pizza_size_medium);
        largeLayout = (CardView) findViewById(R.id.offer_pizza_size_large_layout);
        largeSize = (TextView) findViewById(R.id.offer_pizza_size_large);
        frameLayoutOne = (FrameLayout) findViewById(R.id.everyday_offers_pizza_frame_layout1);
        frameLayoutTwo = (FrameLayout) findViewById(R.id.everyday_offers_pizza_frame_layout2);

        customAddressButton = (TextView) findViewById(R.id.custom_address_exclusive_offer_button);
        customAddress = (EditText) findViewById(R.id.custom_address_exclusive_offer);

        oneText = (TextView) findViewById(R.id.exclusive_offers_pizza_one_text);
        twoText = (TextView) findViewById(R.id.exclusive_offers_pizza_two_text);
        oneLayout = (CardView) findViewById(R.id.exclusive_offers_pizza_one_card);
        twoLayout = (CardView) findViewById(R.id.exclusive_offers_pizza_two_card);
        one = (TextView) findViewById(R.id.exclusive_offers_pizza_one);
        two = (TextView) findViewById(R.id.exclusive_offers_pizza_two);

        oneLinearLayout = (LinearLayout) findViewById(R.id.exclusive_offers_pizza_one_linear);
        twoLinearLayout = (LinearLayout) findViewById(R.id.exclusive_offers_pizza_two_linear);


        sellerId = (TextView) findViewById(R.id.exclusive_offer_seller_id);
        oneView = (View) findViewById(R.id.exclusive_offers_pizza_one_view);
        twoView = (View) findViewById(R.id.exclusive_offers_pizza_two_view);

        payablePrice = (TextView) findViewById(R.id.exclusive_offers_total_cost);

        menuListView1 = (RecyclerView) findViewById(R.id.everyday_offers_pizza_layout1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        menuListView1.setLayoutManager(linearLayoutManager);

        menuListView2 = (RecyclerView) findViewById(R.id.everyday_offers_pizza_layout2);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(false);
        linearLayoutManager2.setStackFromEnd(false);
        menuListView2.setLayoutManager(linearLayoutManager2);

        noAddress = (TextView) findViewById(R.id.exclusive_offer_no_address_found);
        addressFrame = (FrameLayout) findViewById(R.id.exclusive_offer_address_list_frame);
        addressLayout = (RelativeLayout) findViewById(R.id.exclusive_offer_address_list_layout);
        addProgressBar = (ProgressBar) findViewById(R.id.exclusive_offer_address_list_progress_bar);

        addressList = (RecyclerView) findViewById(R.id.exclusive_offer_address_list);
        LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(this);
        linearLayoutManager4.setReverseLayout(false);
        linearLayoutManager4.setStackFromEnd(false);
        addressList.setLayoutManager(linearLayoutManager4);

        customAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(customAddress.getText().toString().trim()))
                {
                    Toast.makeText(ExclusiveOffersActivity.this, "Please fill your address details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    address = customAddress.getText().toString().trim();
                    customAddressButton.setText("Saved!");
                }
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
                    positionOne = -1;
                    positionTwo = -1;
                    one.setText("Pizza Name");
                    two.setText("Pizza Name");
                    one.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    two.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    twoLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    oneText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    frameLayoutOne.setVisibility(View.VISIBLE);
                    frameLayoutTwo.setVisibility(View.GONE);
                    displayMenuItems1(offerStatus);
                    displayMenuItems2(offerStatus);
                    payablePrice.setText("000");
                }
            }
        });

        addRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    noAddress.setVisibility(View.GONE);
                    addressFrame.setVisibility(View.VISIBLE);
                    displayExclusiveOfferAddressList();
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
                    positionOne = -1;
                    positionTwo = -1;
                    one.setText("Pizza Name");
                    two.setText("Pizza Name");
                    one.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    two.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    twoLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    oneText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    frameLayoutOne.setVisibility(View.VISIBLE);
                    frameLayoutTwo.setVisibility(View.GONE);
                    displayMenuItems1(offerStatus);
                    displayMenuItems2(offerStatus);
                    payablePrice.setText("000");
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

        progressBarOne = (ProgressBar) findViewById(R.id.everyday_offers_progress_bar1);
        progressBarTwo = (ProgressBar) findViewById(R.id.everyday_offers_progress_bar2);

        oneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                one.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                two.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                oneLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                twoLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                oneText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                twoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                oneView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                twoView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                frameLayoutOne.setVisibility(View.VISIBLE);
                frameLayoutTwo.setVisibility(View.GONE);
                displayMenuItems1(offerStatus);
            }
        });

        twoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!one.getText().toString().equals("Pizza Name"))
                {
                    one.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    two.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    oneLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    twoLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    oneText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    twoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    oneView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                    twoView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                    frameLayoutOne.setVisibility(View.GONE);
                    frameLayoutTwo.setVisibility(View.VISIBLE);
                    displayMenuItems2(offerStatus);
                }
                else
                {
                    Toast.makeText(ExclusiveOffersActivity.this, getResources().getString(R.string.selectYourFirstPizza), Toast.LENGTH_SHORT).show();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExclusiveOffersActivity.super.onBackPressed();
            }
        });

        Animation open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_open);
        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (one.getText().toString().equals("Pizza Name"))
                {
                    Toast.makeText(ExclusiveOffersActivity.this, getResources().getString(R.string.selectYourFirstPizza), Toast.LENGTH_SHORT).show();
                }
                else
                if (two.getText().toString().equals("Pizza Name"))
                {
                    Toast.makeText(ExclusiveOffersActivity.this, getResources().getString(R.string.selectYourSecondPizza), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ExclusiveOffersActivity.this, "Please choose an address", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent intent = new Intent(ExclusiveOffersActivity.this,OrderConfirmationActivity.class);
                            intent.putExtra("itemName",one.getText().toString()+" and "+two.getText().toString());
                            intent.putExtra("totalPrice",payablePrice.getText().toString());
                            intent.putExtra("itemDescription","Offer Applied : Exclusive Offers"+"\nSize : "+pizzaSize);
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

        displayMenuItems1(offerStatus);
    }

    private void displayExclusiveOfferAddressList()
    {
        Query sort = addRef.orderByChild("count");
        FirebaseRecyclerAdapter<MyAddressAdapter, ExclusiveOfferAddressViewHolder> fra =
                new FirebaseRecyclerAdapter<MyAddressAdapter, ExclusiveOfferAddressViewHolder>(
                        MyAddressAdapter.class,
                        R.layout.cart_address_layout,
                        ExclusiveOfferAddressViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(ExclusiveOfferAddressViewHolder cartAddressViewHolder, MyAddressAdapter myAddressAdapter, int i) {

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

    private void displayMenuItems1(String offerStatus) {
        Query sorting = menuRef.child("MenuItem").child("VegPizza").orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder>(
                        MenuItemAdapter.class,
                        R.layout.everyday_offer_layout,
                        MenuItemViewHolder.class,
                        sorting
                ) {
                    @Override
                    protected void populateViewHolder(MenuItemViewHolder menuItemViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                        View view = (View) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_view);
                        TextView list = (TextView) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_add_list);
                        TextView bottom = (TextView) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_description);
                        CardView listLayout = (CardView) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_add_layout);
                        TextView price = (TextView) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_item_price);

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

                        if (offerStatus.equals("active"))
                        {

                            blockOne.setVisibility(View.GONE);
                            blockTwo.setVisibility(View.GONE);
                        }
                        else
                        {
                            blockOne.setVisibility(View.VISIBLE);
                            blockTwo.setVisibility(View.VISIBLE);
                        }

                        listLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                positionOne = i;
                                key = getRef(i).getKey();
                                notifyDataSetChanged();
                                one.setText(menuItemAdapter.getName());
                                two.setText("Pizza Name");
                                priceCondition = price.getText().toString();
                                positionTwo = -1;
                                payablePrice.setText(price.getText().toString());

                            }
                        });

                        if (Integer.parseInt(menuItemAdapter.getMedium())>=350)
                        {
                            menuItemViewHolder.mView.setVisibility(View.VISIBLE);
                            menuItemViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            if (pizzaSize.equals("Medium"))
                            {
                                menuItemViewHolder.setName(menuItemAdapter.getName());
                                menuItemViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                                menuItemViewHolder.setMedium(menuItemAdapter.getMedium());
                                menuItemViewHolder.setDescription(menuItemAdapter.getDescription());
                                progressBarOne.setVisibility(View.GONE);
                            }
                            else
                            {
                                menuItemViewHolder.setMedium(menuItemAdapter.getLarge());
                                menuItemViewHolder.setName(menuItemAdapter.getName());
                                menuItemViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                                menuItemViewHolder.setDescription(menuItemAdapter.getDescription());
                                progressBarOne.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            menuItemViewHolder.mView.setVisibility(View.GONE);
                            menuItemViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                            progressBarOne.setVisibility(View.GONE);
                        }

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
        menuListView1.setAdapter(firebaseRecyclerAdapter);
    }

    private void displayMenuItems2(String offerStatus) {
        Query sorting = menuRef.child("MenuItem").child("VegPizza").orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder>(
                        MenuItemAdapter.class,
                        R.layout.everyday_offer_layout,
                        MenuItemViewHolder.class,
                        sorting
                ) {
                    @Override
                    protected void populateViewHolder(MenuItemViewHolder menuItemViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                        View view = (View) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_view);
                        TextView list = (TextView) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_add_list);
                        TextView bottom = (TextView) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_description);
                        CardView listLayout = (CardView) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_add_layout);
                        TextView price = (TextView) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_item_price);
                        LinearLayout linearLayout = (LinearLayout) menuItemViewHolder.mView.findViewById(R.id.everyday_special_offer_price_layout);

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

                        linearLayout.setVisibility(View.GONE);
                        if (offerStatus.equals("active"))
                        {
                            listLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    positionTwo = i;
                                    key = getRef(i).getKey();
                                    notifyDataSetChanged();
                                    two.setText(menuItemAdapter.getName());
                                }
                            });
                            blockOne.setVisibility(View.GONE);
                            blockTwo.setVisibility(View.GONE);
                        }
                        else
                        {
                            blockOne.setVisibility(View.VISIBLE);
                            blockTwo.setVisibility(View.VISIBLE);
                        }

                        if (pizzaSize.equals("Medium"))
                        {
                            if (menuItemAdapter.getMedium().equals(priceCondition))
                            {
                                menuItemViewHolder.setName(menuItemAdapter.getName());
                                menuItemViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                                menuItemViewHolder.setMedium(menuItemAdapter.getMedium());
                                menuItemViewHolder.setDescription(menuItemAdapter.getDescription());
                                progressBarTwo.setVisibility(View.GONE);
                                menuItemViewHolder.mView.setVisibility(View.VISIBLE);
                                menuItemViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            }
                            else
                            {
                                menuItemViewHolder.mView.setVisibility(View.GONE);
                                menuItemViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                progressBarTwo.setVisibility(View.GONE);
                            }

                        }
                        else
                        {
                            if (menuItemAdapter.getLarge().equals(priceCondition))
                            {
                                menuItemViewHolder.setMedium(menuItemAdapter.getLarge());
                                menuItemViewHolder.setName(menuItemAdapter.getName());
                                menuItemViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                                menuItemViewHolder.setDescription(menuItemAdapter.getDescription());
                                progressBarTwo.setVisibility(View.GONE);
                                menuItemViewHolder.mView.setVisibility(View.VISIBLE);
                                menuItemViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            }
                            else
                            {
                                menuItemViewHolder.mView.setVisibility(View.GONE);
                                menuItemViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                progressBarTwo.setVisibility(View.GONE);
                            }

                        }

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
        menuListView2.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MenuItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
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


    public static class ExclusiveOfferAddressViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ExclusiveOfferAddressViewHolder(@NonNull View itemView) {
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