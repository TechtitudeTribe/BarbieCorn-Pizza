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

public class EverydayOffersActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView,addressList;
    private DatabaseReference menuRef,addRef;
    private String shopAddress, pizzaSize="Medium";
    private CardView mediumLayout, largeLayout;
    private TextView mediumSize, largeSize;
    private TextView placeOrder, noMenuItem;
    private LinearLayout menuItemAdded;
    private RelativeLayout menuItemAddedLayout;
    private int position = -1;
    private Boolean isOpen = false;
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
    private TextView customAddressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_everday_offers);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        addRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyAddresses");

        shopAddress = getIntent().getStringExtra("key");
        menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress);

        progressBar = (ProgressBar) findViewById(R.id.everyday_special_offers_progress_bar);

        recyclerView = (RecyclerView) findViewById(R.id.everyday_special_offers_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //blockOne = (ImageView) findViewById(R.id.block_screen_one);
        //blockTwo = (CardView) findViewById(R.id.block_screen_two);

        noAddress = (TextView) findViewById(R.id.everyday_offer_no_address_found);
        addressFrame = (FrameLayout) findViewById(R.id.everyday_offer_address_list_frame);
        addressLayout = (RelativeLayout) findViewById(R.id.everyday_offer_address_list_layout);
        addProgressBar = (ProgressBar) findViewById(R.id.everyday_offer_address_list_progress_bar);

        customAddressButton = (TextView) findViewById(R.id.custom_address_everyday_offer_button);
        customAddress = (EditText) findViewById(R.id.custom_address_everyday_offer);

        back = (ImageView) findViewById(R.id.everyday_offers_back);

        sellerId = (TextView) findViewById(R.id.everyday_special_offer_seller_id);
        itemAddedName = (TextView) findViewById(R.id.everyday_offers_list_item_name);
        itemAddedDescription = (TextView) findViewById(R.id.everyday_offers_list_item_description);
        itemAddedPrice = (TextView) findViewById(R.id.everyday_offers_list_item_price);
        itemAddedImage = (ImageView) findViewById(R.id.everyday_offers_list_item_image);

        menuItemAddedLayout = (RelativeLayout) findViewById(R.id.everyday_offers_list_view_layout);
        menuItemAdded = (LinearLayout) findViewById(R.id.everyday_offers_menu_item_list);
        noMenuItem = (TextView) findViewById(R.id.everyday_offers_no_menu_item);
        placeOrder = (TextView) findViewById(R.id.everyday_offers_place_order);
        mediumLayout = (CardView) findViewById(R.id.everyday_offer_pizza_size_medium_layout);
        mediumSize = (TextView) findViewById(R.id.everyday_offer_pizza_size_medium);
        largeLayout = (CardView) findViewById(R.id.everyday_offer_pizza_size_large_layout);
        largeSize = (TextView) findViewById(R.id.everyday_offer_pizza_size_large);

        addressList = (RecyclerView) findViewById(R.id.everyday_offer_address_list);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setStackFromEnd(false);
        addressList.setLayoutManager(linearLayoutManager1);

        customAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(customAddress.getText().toString().trim()))
                {
                    Toast.makeText(EverydayOffersActivity.this, "Please fill your address details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    address = customAddress.getText().toString().trim();
                    customAddressButton.setText("Saved!");
                    placeOrder.setText(getResources().getString(R.string.placeOrder));
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EverydayOffersActivity.super.onBackPressed();
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

        addRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    noAddress.setVisibility(View.GONE);
                    addressFrame.setVisibility(View.VISIBLE);
                    displayEverydayOfferAddressList();
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
                if (itemAddedName.getText().toString().equals("Item Name"))
                {
                    Toast.makeText(EverydayOffersActivity.this, "Please select your pizza...", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(EverydayOffersActivity.this, "Please choose an address", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent intent = new Intent(EverydayOffersActivity.this,OrderConfirmationActivity.class);
                            intent.putExtra("itemName",itemAddedName.getText().toString());
                            intent.putExtra("totalPrice",itemAddedPrice.getText().toString());
                            intent.putExtra("itemDescription","Offer Applied : Everyday Offers"+"\nSize : "+pizzaSize);
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

        displayMenuItems();


    }

    private void displayMenuItems() {
        Query sort = menuRef.child("MenuItem").child("VegPizza").orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter,OfferMenuViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<MenuItemAdapter, OfferMenuViewHolder>(
                        MenuItemAdapter.class,
                        R.layout.everyday_offer_layout,
                        OfferMenuViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(OfferMenuViewHolder offerMenuViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                        View view = (View) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_view);
                        TextView list = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_add_list);
                        TextView bottom = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_description);
                        CardView listLayout = (CardView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_add_layout);
                        TextView price = (TextView) offerMenuViewHolder.mView.findViewById(R.id.everyday_special_offer_item_price);

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
                                progressBar.setVisibility(View.GONE);
                            }
                            else
                            {
                                offerMenuViewHolder.setMedium(menuItemAdapter.getLarge());
                                offerMenuViewHolder.setName(menuItemAdapter.getName());
                                offerMenuViewHolder.setImage(menuItemAdapter.getImage(),getApplicationContext());
                                offerMenuViewHolder.setDescription(menuItemAdapter.getDescription());
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
                                key = getRef(i).getKey();
                                notifyDataSetChanged();
                                noMenuItem.setVisibility(View.INVISIBLE);
                                menuItemAdded.setVisibility(View.VISIBLE);
                                itemAddedName.setText(menuItemAdapter.getName());
                                itemAddedDescription.setText(menuItemAdapter.getDescription());
                                itemAddedPrice.setText(price.getText().toString());
                                Picasso.with(EverydayOffersActivity.this).load(menuItemAdapter.getImage()).into(itemAddedImage);
                            }
                        });

                        if (position==i)
                        {
                            listLayout.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            listLayout.setVisibility(View.VISIBLE);
                        }

                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class OfferMenuViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public OfferMenuViewHolder(@NonNull View itemView) {
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

    private void displayEverydayOfferAddressList() {
        Query sort = addRef.orderByChild("count");
        FirebaseRecyclerAdapter<MyAddressAdapter, EverydayOfferAddressViewHolder> fra =
                new FirebaseRecyclerAdapter<MyAddressAdapter, EverydayOfferAddressViewHolder>(
                        MyAddressAdapter.class,
                        R.layout.cart_address_layout,
                        EverydayOfferAddressViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(EverydayOfferAddressViewHolder cartAddressViewHolder, MyAddressAdapter myAddressAdapter, int i) {

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

    public static class EverydayOfferAddressViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        public EverydayOfferAddressViewHolder(@NonNull View itemView) {
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