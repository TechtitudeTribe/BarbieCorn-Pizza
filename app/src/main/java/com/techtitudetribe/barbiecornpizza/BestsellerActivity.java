package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class BestsellerActivity extends AppCompatActivity {

    private ProgressBar progressBar,vegToppingProgressBar;
    private RecyclerView recyclerView,addressList;
    private DatabaseReference menuRef,addRef;
    private String shopAddress, pizzaSize="Medium";
    private String extraCheese = "No";
    private String cheeseBurst = "No";
    private String thinCrust = "No";
    private String toppingGlobal="No";
    private CardView mediumLayout, largeLayout;
    private TextView mediumSize, largeSize;
    private TextView placeOrder, noMenuItem;
    private LinearLayout menuItemAdded;
    private RelativeLayout menuItemAddedLayout;
    private int position = -1;
    private RecyclerView vegToppingList;
    private Boolean isOpen = false;
    private ImageView addExtraCheeseVerified,addExtraCheeseButton;
    private ImageView cheeseBurstVerified, cheeseBurstButton;
    private ImageView thinCrustVerified, thinCrustButton;
    private FirebaseAuth mAuth;
    private String address,key,currentUser;
    private TextView itemAddedName, itemAddedPrice, itemAddedDescription;
    private ImageView itemAddedImage;
    private TextView sellerId,noAddress;
    private RelativeLayout addressLayout;
    private ProgressBar addProgressBar;
    private ImageView back;
    private FrameLayout addressFrame;
    private EditText customAddress;
    private DatabaseReference toppingRef;
    private TextView customAddressButton,cheeseBurstPrice,thinCrustPrice,extraCheesePrice,cancelCustomisePizza, addCustomisePizza;
    private RelativeLayout customizePizza;
    private TextView customisePizzaPrice;
    private TextView customizeToppingPrice;
    private int totalCost,cheeseAmount=0,toppingAmount=0,cheeseBurstAmount=0,thinCrustAmount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestseller);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        addRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyAddresses");

        shopAddress = getIntent().getStringExtra("key");
        menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress);
        toppingRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress).child("MyTopping").child("VegTopping");

        progressBar = (ProgressBar) findViewById(R.id.bestseller_offers_progress_bar);

        customizeToppingPrice = (TextView) findViewById(R.id.bestseller_veg_topping_price);
        cancelCustomisePizza = (TextView) findViewById(R.id.bestseller_offers_custimze_pizza_cancel);
        addCustomisePizza = (TextView) findViewById(R.id.bestseller_offers_custimze_pizza_add);
        customizePizza = (RelativeLayout) findViewById(R.id.bestseller_offers_customize_layout);
        recyclerView = (RecyclerView) findViewById(R.id.bestseller_offers_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        vegToppingProgressBar = (ProgressBar) findViewById(R.id.bestseller_veg_topping_progress_bar);
        vegToppingList  = (RecyclerView) findViewById(R.id.bestseller_veg_topping_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        vegToppingList.setLayoutManager(gridLayoutManager);

        //blockOne = (ImageView) findViewById(R.id.block_screen_one);
        //blockTwo = (CardView) findViewById(R.id.block_screen_two);

        noAddress = (TextView) findViewById(R.id.bestseller_offer_no_address_found);
        addressFrame = (FrameLayout) findViewById(R.id.bestseller_offer_address_list_frame);
        addressLayout = (RelativeLayout) findViewById(R.id.bestseller_offer_address_list_layout);
        addProgressBar = (ProgressBar) findViewById(R.id.bestseller_offer_address_list_progress_bar);

        thinCrustPrice = (TextView) findViewById(R.id.bestseller_add_thin_crust_price);
        cheeseBurstPrice = (TextView) findViewById(R.id.bestseller_add_cheese_burst_price);
        extraCheesePrice = (TextView) findViewById(R.id.bestseller_add_extra_cheese_price);

        customAddressButton = (TextView) findViewById(R.id.custom_address_bestseller_offer_button);
        customAddress = (EditText) findViewById(R.id.custom_address_bestseller_offer);

        back = (ImageView) findViewById(R.id.bestseller_offers_back);

        sellerId = (TextView) findViewById(R.id.bestseller_special_offer_seller_id);
        itemAddedName = (TextView) findViewById(R.id.bestseller_offers_list_item_name);
        itemAddedDescription = (TextView) findViewById(R.id.bestseller_offers_list_item_description);
        itemAddedPrice = (TextView) findViewById(R.id.bestseller_offers_list_item_price);
        itemAddedImage = (ImageView) findViewById(R.id.bestseller_offers_list_item_image);

        cheeseBurstPrice.setText("80");
        thinCrustPrice.setText("40");

        customisePizzaPrice = (TextView) findViewById(R.id.bestseller_offers_customize_layout_total_cost);
        menuItemAddedLayout = (RelativeLayout) findViewById(R.id.bestseller_offers_list_view_layout);
        menuItemAdded = (LinearLayout) findViewById(R.id.bestseller_offers_menu_item_list);
        noMenuItem = (TextView) findViewById(R.id.bestseller_offers_no_menu_item);
        placeOrder = (TextView) findViewById(R.id.bestseller_offers_place_order);
        mediumLayout = (CardView) findViewById(R.id.bestseller_offer_pizza_size_medium_layout);
        mediumSize = (TextView) findViewById(R.id.bestseller_offer_pizza_size_medium);
        largeLayout = (CardView) findViewById(R.id.bestseller_offer_pizza_size_large_layout);
        largeSize = (TextView) findViewById(R.id.bestseller_offer_pizza_size_large);

        thinCrustVerified = (ImageView) findViewById(R.id.bestseller_add_thin_crust_verified);
        cheeseBurstVerified = (ImageView) findViewById(R.id.bestseller_add_cheese_burst_verified);
        thinCrustButton = (ImageView) findViewById(R.id.bestseller_add_thin_crust_button);
        cheeseBurstButton = (ImageView) findViewById(R.id.bestseller_add_cheese_burst_button);
        addExtraCheeseButton = (ImageView) findViewById(R.id.bestseller_add_extra_cheese_button);
        addExtraCheeseVerified = (ImageView) findViewById(R.id.bestseller_add_extra_cheese_verified);

        addressList = (RecyclerView) findViewById(R.id.bestseller_offer_address_list);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setStackFromEnd(false);
        addressList.setLayoutManager(linearLayoutManager1);

        customAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(customAddress.getText().toString().trim()))
                {
                    Toast.makeText(BestsellerActivity.this, "Please fill your address details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    address = customAddress.getText().toString().trim();
                    customAddressButton.setText("Saved!");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BestsellerActivity.super.onBackPressed();
            }
        });

        mediumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediumSize.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                mediumSize.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                largeSize.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                largeSize.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                pizzaSize = "Medium";
                cheeseBurstPrice.setText("80");
                thinCrustPrice.setText("40");
                displayMenuItems();
            }
        });

        largeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediumSize.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                mediumSize.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                largeSize.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                largeSize.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                pizzaSize = "Large";
                cheeseBurstPrice.setText("100");
                thinCrustPrice.setText("50");
                displayMenuItems();
            }
        });

        Animation open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_open);
        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);

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

        addExtraCheeseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cheeseAmount = Integer.parseInt(extraCheesePrice.getText().toString());
                addExtraCheeseButton.setVisibility(View.GONE);
                addExtraCheeseVerified.setVisibility(View.VISIBLE);
                totalCost = Integer.parseInt(customisePizzaPrice.getText().toString())+cheeseAmount;
                customisePizzaPrice.setText(String.valueOf(totalCost));
                extraCheese = "Yes";
            }
        });

        cheeseBurstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cheeseBurstAmount = Integer.parseInt(cheeseBurstPrice.getText().toString());
                cheeseBurstButton.setVisibility(View.GONE);
                cheeseBurstVerified.setVisibility(View.VISIBLE);
                totalCost = Integer.parseInt(customisePizzaPrice.getText().toString())+cheeseBurstAmount;
                customisePizzaPrice.setText(String.valueOf(totalCost));
                cheeseBurst = "Yes";
            }
        });

        thinCrustButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thinCrustAmount = Integer.parseInt(thinCrustPrice.getText().toString());
                thinCrustButton.setVisibility(View.GONE);
                thinCrustVerified.setVisibility(View.VISIBLE);
                totalCost = Integer.parseInt(customisePizzaPrice.getText().toString())+thinCrustAmount;
                customisePizzaPrice.setText(String.valueOf(totalCost));
                thinCrust = "Yes";
            }
        });

        addExtraCheeseVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cheeseAmount = Integer.parseInt(extraCheesePrice.getText().toString());;
                addExtraCheeseButton.setVisibility(View.VISIBLE);
                addExtraCheeseVerified.setVisibility(View.GONE);
                totalCost = Integer.parseInt(customisePizzaPrice.getText().toString())-cheeseAmount;
                customisePizzaPrice.setText(String.valueOf(totalCost));
                extraCheese = "No";
            }
        });

        cheeseBurstVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cheeseBurstAmount = Integer.parseInt(cheeseBurstPrice.getText().toString());
                cheeseBurstButton.setVisibility(View.VISIBLE);
                cheeseBurstVerified.setVisibility(View.GONE);
                totalCost = Integer.parseInt(customisePizzaPrice.getText().toString())-cheeseBurstAmount;
                customisePizzaPrice.setText(String.valueOf(totalCost));
                cheeseBurst = "No";
            }
        });

        thinCrustVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thinCrustAmount = Integer.parseInt(thinCrustPrice.getText().toString());
                thinCrustButton.setVisibility(View.VISIBLE);
                thinCrustVerified.setVisibility(View.GONE);
                totalCost = Integer.parseInt(customisePizzaPrice.getText().toString())-thinCrustAmount;
                customisePizzaPrice.setText(String.valueOf(totalCost));
                thinCrust = "No";
            }
        });

        addRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    noAddress.setVisibility(View.GONE);
                    addressFrame.setVisibility(View.VISIBLE);
                    displayBestsellerOfferAddressList();
                }
                else
                {
                    noAddress.setVisibility(View.VISIBLE);
                    addressFrame.setVisibility(View.GONE);
                    addProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(itemAddedPrice.getText().toString()))
                {
                    Toast.makeText(BestsellerActivity.this, "Please select your pizza...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (placeOrder.getText().toString().equals("Select an Address")||placeOrder.getText().toString().equals("एक पता चुनें"))
                    {
                        if(isOpen)
                        {
                            addressLayout.startAnimation(close);
                            addressLayout.setVisibility(View.INVISIBLE);
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
                            Toast.makeText(BestsellerActivity.this, "Please choose an address", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String finalAmount;
                            if (Integer.parseInt(itemAddedPrice.getText().toString())>=750 && Integer.parseInt(itemAddedPrice.getText().toString())<850)
                            {
                                finalAmount = "500";
                                Intent intent = new Intent(BestsellerActivity.this,OrderConfirmationActivity.class);
                                intent.putExtra("itemName",itemAddedName.getText().toString());
                                intent.putExtra("totalPrice",finalAmount);
                                intent.putExtra("itemDescription","Offer Applied : Bestseller Offers"+"\n"+itemAddedDescription.getText().toString());
                                intent.putExtra("address",address);
                                intent.putExtra("sellerId",sellerId.getText().toString());
                                intent.putExtra("shopName",shopAddress);
                                intent.putExtra("itemNumbers","1");
                                intent.putExtra("key",key);
                                startActivity(intent);
                            }
                            else if ((Integer.parseInt(itemAddedPrice.getText().toString())>=850 && Integer.parseInt(itemAddedPrice.getText().toString())<950))
                            {
                                finalAmount = "600";
                                Intent intent = new Intent(BestsellerActivity.this,OrderConfirmationActivity.class);
                                intent.putExtra("itemName",itemAddedName.getText().toString());
                                intent.putExtra("totalPrice",finalAmount);
                                intent.putExtra("itemDescription","Offer Applied : Bestseller Offers"+"\n"+itemAddedDescription.getText().toString());
                                intent.putExtra("address",address);
                                intent.putExtra("sellerId",sellerId.getText().toString());
                                intent.putExtra("shopName",shopAddress);
                                intent.putExtra("itemNumbers","1");
                                intent.putExtra("key",key);
                                startActivity(intent);
                            }
                            else if ((Integer.parseInt(itemAddedPrice.getText().toString())>=950 && Integer.parseInt(itemAddedPrice.getText().toString())<1100))
                            {
                                finalAmount = "700";
                                Intent intent = new Intent(BestsellerActivity.this,OrderConfirmationActivity.class);
                                intent.putExtra("itemName",itemAddedName.getText().toString());
                                intent.putExtra("totalPrice",finalAmount);
                                intent.putExtra("itemDescription","Offer Applied : Bestseller Offers"+"\n"+itemAddedDescription.getText().toString());
                                intent.putExtra("address",address);
                                intent.putExtra("sellerId",sellerId.getText().toString());
                                intent.putExtra("shopName",shopAddress);
                                intent.putExtra("itemNumbers","1");
                                intent.putExtra("key",key);
                                startActivity(intent);
                            }
                            else
                            {
                                addressLayout.startAnimation(close);
                                addressLayout.setVisibility(View.INVISIBLE);
                                Toast.makeText(BestsellerActivity.this, "Minimum order is Rs. 750\nCustomize your pizza...", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                }

            }
        });

        cancelCustomisePizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customizePizza.setVisibility(View.GONE);
            }
        });

        addCustomisePizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //customizePizza.setVisibility(View.VISIBLE);
                customizePizza.setVisibility(View.GONE);
                noMenuItem.setVisibility(View.INVISIBLE);
                menuItemAdded.setVisibility(View.VISIBLE);
                itemAddedPrice.setText(customisePizzaPrice.getText().toString());
                addExtraCheeseButton.setVisibility(View.VISIBLE);
                addExtraCheeseVerified.setVisibility(View.GONE);
                cheeseBurstButton.setVisibility(View.VISIBLE);
                cheeseBurstVerified.setVisibility(View.GONE);
                thinCrustButton.setVisibility(View.VISIBLE);
                thinCrustVerified.setVisibility(View.GONE);
                itemAddedDescription.setText("Size : "+pizzaSize+"\n"+"Extra Cheese : "+extraCheese+"\n"+"Thin Crust : "+thinCrust+"\n"+"Cheese Burst : "+cheeseBurst+"\nTopping : "+toppingGlobal);
                cheeseBurst="No";
                thinCrust="No";
                extraCheese="No";
                toppingGlobal = "";

            }
        });

        displayMenuItems();

    }

    private void displayMenuItems() {
        Query sort = menuRef.child("MenuItem").child("VegPizza").orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter, BestsellerMenuViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<MenuItemAdapter, BestsellerMenuViewHolder>(
                MenuItemAdapter.class,
                R.layout.everyday_offer_layout,
                BestsellerMenuViewHolder.class,
                sort
        ) {
            @Override
            protected void populateViewHolder(BestsellerMenuViewHolder offerMenuViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                View view = (View) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_view);
                TextView list = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_add_list);
                TextView bottom = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_description);
                CardView listLayout = (CardView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_add_layout);
                TextView price = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_item_price);
                list.setText("Explore");

                if (Integer.parseInt(menuItemAdapter.getMedium())>=350)
                {
                    offerMenuViewHolder.mView.setVisibility(View.VISIBLE);
                    offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (pizzaSize.equals("Medium"))
                    {
                        offerMenuViewHolder.setName(menuItemAdapter.getName());
                        offerMenuViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                        offerMenuViewHolder.setMedium(menuItemAdapter.getMedium());
                        offerMenuViewHolder.setDescription(menuItemAdapter.getDescription());
                        customisePizzaPrice.setText(menuItemAdapter.getMedium());
                        extraCheesePrice.setText(menuItemAdapter.getMediumCheese());
                        customizeToppingPrice.setText(menuItemAdapter.getLargeVegTopping());
                        progressBar.setVisibility(View.GONE);
                    }
                    else
                    {
                        offerMenuViewHolder.setMedium(menuItemAdapter.getLarge());
                        offerMenuViewHolder.setName(menuItemAdapter.getName());
                        offerMenuViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                        offerMenuViewHolder.setDescription(menuItemAdapter.getDescription());
                        customisePizzaPrice.setText(menuItemAdapter.getMedium());
                        extraCheesePrice.setText(menuItemAdapter.getLargeCheese());
                        customizeToppingPrice.setText(menuItemAdapter.getLargeVegTopping());
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else
                {
                    offerMenuViewHolder.mView.setVisibility(View.GONE);
                    offerMenuViewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    progressBar.setVisibility(View.GONE);
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
                        position = i;
                        customizePizza.setVisibility(View.VISIBLE);
                        key = getRef(i).getKey();
                        notifyDataSetChanged();
                        showVegToppingDetails();
                        itemAddedName.setText(menuItemAdapter.getName());
                        //itemAddedDescription.setText(menuItemAdapter.getDescription());
                        Picasso.with(BestsellerActivity.this).load(menuItemAdapter.getImage()).into(itemAddedImage);
                    }
                });

                /*if (position==i)
                {
                    listLayout.setVisibility(View.INVISIBLE);
                }
                else
                {
                    listLayout.setVisibility(View.VISIBLE);
                }*/
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BestsellerMenuViewHolder extends RecyclerView.ViewHolder {

        View mView = itemView;
        public BestsellerMenuViewHolder(@NonNull View itemView) {
            super(itemView);
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

    private void displayBestsellerOfferAddressList() {
        Query sort = addRef.orderByChild("count");
        FirebaseRecyclerAdapter<MyAddressAdapter, BestsellerOfferAddressViewHolder> fra =
                new FirebaseRecyclerAdapter<MyAddressAdapter, BestsellerOfferAddressViewHolder>(
                        MyAddressAdapter.class,
                        R.layout.cart_address_layout,
                        BestsellerOfferAddressViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(BestsellerOfferAddressViewHolder cartAddressViewHolder, MyAddressAdapter myAddressAdapter, int i) {

                        TextView textView = (TextView) cartAddressViewHolder.mView.findViewById(R.id.cart_address_layout_text);

                        cartAddressViewHolder.setAddress(myAddressAdapter.getAddress());

                        addProgressBar.setVisibility(View.GONE);
                        cartAddressViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                position = i;
                                notifyDataSetChanged();
                                address = myAddressAdapter.getAddress();
                                placeOrder.setText(getResources().getString(R.string.placeOrder));
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

    public static class BestsellerOfferAddressViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public BestsellerOfferAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            mView= itemView;
        }

        public void setAddress(String address)
        {
            TextView addressText = (TextView) mView.findViewById(R.id.cart_address_layout_text);
            addressText.setText(address);
        }
    }

    private void showVegToppingDetails() {
        Query sort = toppingRef.orderByChild("count");
        FirebaseRecyclerAdapter<ToppingAdapter, OffersToppingViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ToppingAdapter, OffersToppingViewHolder>(
                        ToppingAdapter.class,
                        R.layout.topping_item_layout,
                        OffersToppingViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(OffersToppingViewHolder toppingViewHolder, ToppingAdapter toppingAdapter, int i) {

                        ImageView imageView = (ImageView) toppingViewHolder.mView.findViewById(R.id.topping_item_image);
                        TextView textView = (TextView) toppingViewHolder.mView.findViewById(R.id.topping_item_status);

                        toppingViewHolder.setItemImage(toppingAdapter.getItemImage(), getApplicationContext());
                        vegToppingProgressBar.setVisibility(View.GONE);

                        toppingViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (textView.getText().toString().equals("no")) {
                                    if (toppingGlobal.contains(toppingAdapter.getItemName())) {
                                        toppingGlobal = toppingGlobal + "";
                                    } else if (toppingGlobal.equals("No")) {
                                        toppingGlobal = toppingAdapter.getItemName();
                                    } else {
                                        toppingGlobal = toppingGlobal + "," + toppingAdapter.getItemName();
                                    }
                                    totalCost = Integer.parseInt(customisePizzaPrice.getText().toString()) + Integer.parseInt(customizeToppingPrice.getText().toString());
                                    customisePizzaPrice.setText(String.valueOf(totalCost));
                                    imageView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.smoky_black));
                                    textView.setText("yes");
                                } else {
                                    if (toppingGlobal.contains(toppingAdapter.getItemName() + ",")) {
                                        toppingGlobal = toppingGlobal.replace(toppingAdapter.getItemName() + ",", "");
                                    } else if (toppingGlobal.contains("," + toppingAdapter.getItemName())) {
                                        toppingGlobal = toppingGlobal.replace("," + toppingAdapter.getItemName(), "");
                                    } else if (toppingGlobal.contains(toppingAdapter.getItemName())) {
                                        toppingGlobal = toppingGlobal.replace(toppingAdapter.getItemName(), "") + "No";
                                    }
                                    totalCost = Integer.parseInt(customisePizzaPrice.getText().toString()) - Integer.parseInt(customizeToppingPrice.getText().toString());
                                    customisePizzaPrice.setText(String.valueOf(totalCost));
                                    imageView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                    textView.setText("no");
                                }
                            }
                        });
                    }
                };
        vegToppingList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class OffersToppingViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public OffersToppingViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setItemImage(String itemImage, Context context) {
            ImageView imageView = (ImageView) mView.findViewById(R.id.topping_item_image);
            Picasso.with(context).load(itemImage).placeholder(R.drawable.ic_fastfood_210).into(imageView);
        }
    }
}