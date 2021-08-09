package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
    private RecyclerView menuListView1,menuListView2;
    private DatabaseReference menuRef, shopRef, cartRef;
    private ProgressBar progressBarOne,progressBarTwo,shopProgressBar,cartProgressBar;
    private ImageView back;
    private RecyclerView shopDetailsList;
    private RelativeLayout bothPizza;
    private LinearLayout linearLayout;
    private FrameLayout frameLayoutPizza1;
    private ImageView pizzaOneImage, pizzaTwoImage, pizzaOneDelete, pizzaTwoDelete;
    private TextView pizzaOnePrice, pizzaTwoPrice, pizzaOneName, pizzaTwoName;
    private CardView partitionOne, partitionTwo;
    private FrameLayout frameLayoutOne, frameLayoutTwo;
    private String shopKey="", priceCondition="", pizzaSize="Medium",currentUser, userId,shopName="Yummy";
    private int positionOne = -1, positionTwo = -1;
    private CardView mediumLayout, largeLayout;
    private TextView mediumSize, largeSize;
    private TextView addToCart, payablePrice, shopItemNamesText;
    private long cartItems=0,shopItems=0;
    private Boolean itemAvailable = false;
    private RelativeLayout footer;
    private ImageView blockOne;
    private CardView blockTwo;
    private String offerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exclusive_offers);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyCart");
        menuRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(getIntent().getStringExtra("shopAddress"));
        shopRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(getIntent().getStringExtra("shopAddress"));

        shopDetailsList = (RecyclerView) findViewById(R.id.offers_shop_details_list);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setStackFromEnd(false);
        shopDetailsList.setLayoutManager(linearLayoutManager1);

        linearLayout = (LinearLayout) findViewById(R.id.offers_shop_details_linear_layout);
        frameLayoutPizza1 = (FrameLayout) findViewById(R.id.everyday_offers_pizza_frame_layout1);
        bothPizza = (RelativeLayout) findViewById(R.id.both_pizza_relative_layout);
        back = (ImageView) findViewById(R.id.everyday_offers_back);

        blockOne = (ImageView) findViewById(R.id.block_screen_one);
        blockTwo = (CardView) findViewById(R.id.block_screen_two);
        footer = (RelativeLayout) findViewById(R.id.exclusive_offers_footer);
        shopItemNamesText = (TextView) findViewById(R.id.shop_name_item_offers_text);
        mediumLayout = (CardView) findViewById(R.id.offer_pizza_size_medium_layout);
        mediumSize = (TextView) findViewById(R.id.offer_pizza_size_medium);
        largeLayout = (CardView) findViewById(R.id.offer_pizza_size_large_layout);
        largeSize = (TextView) findViewById(R.id.offer_pizza_size_large);
        frameLayoutOne = (FrameLayout) findViewById(R.id.everyday_offers_pizza_frame_layout1);
        frameLayoutTwo = (FrameLayout) findViewById(R.id.everyday_offers_pizza_frame_layout2);
        partitionOne = (CardView) findViewById(R.id.offers_partition_one);
        partitionTwo = (CardView) findViewById(R.id.offers_partition_two);
        pizzaOneDelete = (ImageView) findViewById(R.id.everyday_offers_pizza_one_delete);
        pizzaTwoDelete = (ImageView) findViewById(R.id.everyday_offers_pizza_two_delete);
        pizzaOneImage = (ImageView) findViewById(R.id.everyday_offers_pizza_one);
        pizzaTwoImage = (ImageView) findViewById(R.id.everyday_offers_pizza_two);
        pizzaOneName = (TextView) findViewById(R.id.everyday_offers_pizza_one_name);
        pizzaTwoName = (TextView) findViewById(R.id.everyday_offers_pizza_two_name);
        pizzaOnePrice = (TextView) findViewById(R.id.everyday_offers_pizza_one_price);
        pizzaTwoPrice = (TextView) findViewById(R.id.everyday_offers_pizza_two_price);

        addToCart = (TextView) findViewById(R.id.offers_add_cart);
        payablePrice = (TextView) findViewById(R.id.exclusive_offers_total_cost);
        cartProgressBar = (ProgressBar) findViewById(R.id.offers_cart_progress_bar);

        menuListView1 = (RecyclerView) findViewById(R.id.everyday_offers_pizza_layout1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        menuListView1.setLayoutManager(linearLayoutManager);

        menuListView2 = (RecyclerView) findViewById(R.id.everyday_offers_pizza_layout2);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        menuListView2.setLayoutManager(linearLayoutManager2);

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
                    pizzaOneImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.medium_pizza));
                    pizzaOnePrice.setText("000");
                    pizzaOneName.setText("Pizza Name 1");
                    pizzaTwoImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.medium_pizza));
                    pizzaTwoPrice.setText("000");
                    pizzaTwoName.setText("Pizza Name 2");
                    frameLayoutOne.setVisibility(View.VISIBLE);
                    frameLayoutTwo.setVisibility(View.GONE);
                    displayMenuItems1(shopKey, offerStatus);
                    displayMenuItems2(shopKey,offerStatus);
                    payablePrice.setText("000");
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
                    pizzaOneImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.medium_pizza));
                    pizzaOnePrice.setText("000");
                    pizzaOneName.setText("Pizza Name 1");
                    pizzaTwoImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.medium_pizza));
                    pizzaTwoPrice.setText("000");
                    pizzaTwoName.setText("Pizza Name 2");
                    pizzaSize = "Large";
                    frameLayoutOne.setVisibility(View.VISIBLE);
                    frameLayoutTwo.setVisibility(View.GONE);
                    displayMenuItems1(shopKey, offerStatus);
                    displayMenuItems2(shopKey, offerStatus);
                    payablePrice.setText("000");
                }
            }
        });

        partitionOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayoutOne.setVisibility(View.VISIBLE);
                frameLayoutTwo.setVisibility(View.GONE);
                displayMenuItems1(shopKey, offerStatus);
            }
        });

        partitionTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!priceCondition.equals(""))
                {
                    frameLayoutOne.setVisibility(View.GONE);
                    frameLayoutTwo.setVisibility(View.VISIBLE);
                    displayMenuItems2(shopKey, offerStatus);
                }
                else
                {
                    Toast.makeText(ExclusiveOffersActivity.this, getResources().getString(R.string.selectYourFirstPizza), Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressBarOne = (ProgressBar) findViewById(R.id.everyday_offers_progress_bar1);
        progressBarTwo = (ProgressBar) findViewById(R.id.everyday_offers_progress_bar2);
        shopProgressBar = (ProgressBar) findViewById(R.id.offers_shop_details_list_progress_bar);

        pizzaOneDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pizzaOneImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.medium_pizza));
                pizzaOnePrice.setText("000");
                pizzaOneName.setText("Pizza Name 1");
            }
        });

        pizzaTwoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pizzaTwoImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.medium_pizza));
                pizzaTwoPrice.setText("000");
                pizzaTwoName.setText("Pizza Name 2");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExclusiveOffersActivity.super.onBackPressed();
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.equals(pizzaOneName.getText().toString(),"Pizza Name 1"))
                {
                    Toast.makeText(ExclusiveOffersActivity.this, getResources().getString(R.string.selectYourFirstPizza), Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.equals(pizzaTwoName.getText().toString(),"Pizza Name 2"))
                {
                    Toast.makeText(ExclusiveOffersActivity.this, getResources().getString(R.string.selectYourSecondPizza), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addToCart.setVisibility(View.GONE);
                    cartProgressBar.setVisibility(View.VISIBLE);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    cartRef.child(shopName).child("CartItems").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren())
                            {

                                String in = ds.child("itemName").getValue().toString();
                                if ((pizzaOneName.getText().toString()+" & "+pizzaTwoName.getText().toString()).equals(in))
                                {
                                    cartRef = ds.getRef();
                                    itemAvailable = true;
                                }

                            }
                            if (itemAvailable)
                            {
                                HashMap hashMap = new HashMap();
                                hashMap.put("itemPrice",payablePrice.getText().toString());
                                hashMap.put("itemCustomizedPrice",payablePrice.getText().toString());
                                hashMap.put("itemDescription","Offer Applied : Buy One Get One Free"+"\nSize : "+pizzaSize);

                                cartRef.updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error!=null)
                                        {
                                            String message = error.getMessage();
                                            Toast.makeText(getApplicationContext(),"Error Occurred : "+message,Toast.LENGTH_SHORT).show();
                                            cartProgressBar.setVisibility(View.GONE);
                                            addToCart.setVisibility(View.VISIBLE);
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                            addToCart.setVisibility(View.GONE);
                                            cartProgressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            else
                            {
                                HashMap hashMap1 = new HashMap();
                                hashMap1.put("count",shopItems+1);
                                hashMap1.put("itemNames",shopItemNamesText.getText().toString()+pizzaOneName.getText().toString()+" & "+pizzaTwoName.getText().toString());
                                hashMap1.put("shopName",shopName);
                                hashMap1.put("sellerId",userId);

                                cartRef.child(shopName).updateChildren(hashMap1, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error1, @NonNull DatabaseReference ref) {
                                        if (error1!=null)
                                        {
                                            String message = error1.getMessage();
                                            Toast.makeText(getApplicationContext(),"Error Occurred : "+message,Toast.LENGTH_SHORT).show();
                                            cartProgressBar.setVisibility(View.GONE);
                                            //viewCart.setVisibility(View.GONE);
                                            addToCart.setVisibility(View.VISIBLE);
                                        }
                                        else
                                        {
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                                            String currentDateandTime = sdf.format(new Date());

                                            HashMap hashMap = new HashMap();
                                            hashMap.put("count",cartItems+1);
                                            hashMap.put("itemName",pizzaOneName.getText().toString()+" & "+pizzaTwoName.getText().toString());
                                            hashMap.put("itemPrice",payablePrice.getText().toString());
                                            hashMap.put("itemCustomizedPrice",payablePrice.getText().toString());
                                            hashMap.put("itemDescription","Offer Applied : Buy One Get One Free"+"\nSize : "+pizzaSize);
                                            hashMap.put("itemImage","https://firebasestorage.googleapis.com/v0/b/barbiecorn-pizza.appspot.com/o/Burger%2FFarmhouse.png?alt=media&token=4fe3612d-862a-4c8d-b601-69974820861c");
                                            hashMap.put("itemQuantity","1");

                                            cartRef.child(shopName).child("CartItems").child("CartItem"+currentDateandTime)
                                                    .updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error2, @NonNull DatabaseReference ref) {
                                                    if (error2!=null)
                                                    {
                                                        String message = error2.getMessage();
                                                        Toast.makeText(getApplicationContext(),"Error Occurred : "+message,Toast.LENGTH_SHORT).show();
                                                        cartProgressBar.setVisibility(View.GONE);
                                                        //viewCart.setVisibility(View.GONE);
                                                        addToCart.setVisibility(View.VISIBLE);
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(getApplicationContext(), "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                                        addToCart.setVisibility(View.GONE);
                                                        cartProgressBar.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
        });

        displayShopList();
    }

    private void displayShopList() {
        Query sort = shopRef.orderByChild("count");
        FirebaseRecyclerAdapter<ShopDetailsAdapter, ShopDetailsViewHolder> frc =
                new FirebaseRecyclerAdapter<ShopDetailsAdapter, ShopDetailsViewHolder>(
                        ShopDetailsAdapter.class,
                        R.layout.offers_shop_details_layout,
                        ShopDetailsViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(ShopDetailsViewHolder shopDetailsViewHolder, ShopDetailsAdapter shopDetailsAdapter, int i) {


                        //Animation leftAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.left_animation);
                        //Animation bottomAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.bottom_animation);
                        //Animation open = AnimationUtils.loadAnimation(getActivity(),R.anim.order_track_open);
                        TextView name = (TextView) shopDetailsViewHolder.mView.findViewById(R.id.offers_shop_details_name);
                        //TextView status = (TextView) shopDetailsViewHolder.mView.findViewById(R.id.shop_details_status);
                        View relativeLayout = (View) shopDetailsViewHolder.mView.findViewById(R.id.offers_shop_details_relative_layout);

                        shopDetailsViewHolder.setShopName(shopDetailsAdapter.getShopName());
                        userId = shopDetailsAdapter.getUserId();
                        shopDetailsViewHolder.setShopFrontImage(shopDetailsAdapter.getShopFrontImage(),getApplicationContext());
                        String key = getRef(i).getKey();
                        shopKey = key;
                        shopProgressBar.setVisibility(View.GONE);


                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        shopDetailsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                offerStatus = shopDetailsAdapter.getExclusiveOffersStatus();
                                linearLayout.setVisibility(View.GONE);
                                bothPizza.setVisibility(View.VISIBLE);
                                shopName = shopDetailsAdapter.getShopName();
                                frameLayoutPizza1.setVisibility(View.VISIBLE);
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
                                displayMenuItems1(key,offerStatus);
                                footer.setVisibility(View.VISIBLE);
                            }
                        });

                        cartRef.child(shopName).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild("itemNames"))
                                {
                                    String shopItemNames = snapshot.child("itemNames").getValue().toString()+",";
                                    shopItemNamesText.setText(shopItemNames);
                                }
                                else
                                {
                                    shopItemNamesText.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        cartRef.child(shopName).child("CartItems").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists())
                                {
                                    cartItems = snapshot.getChildrenCount();
                                }
                                else
                                {
                                    cartItems=0;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        cartRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists())
                                {
                                    shopItems = snapshot.getChildrenCount();
                                }
                                else
                                {
                                    shopItems=0;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                };
        shopDetailsList.setAdapter(frc);
    }

    private void displayMenuItems1(String key, String offerStatus) {
        Query sorting = menuRef.child(key).child("MenuItem").child("VegPizza").orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder>(
                        MenuItemAdapter.class,
                        R.layout.menu_item_layout,
                        MenuItemViewHolder.class,
                        sorting
                ) {
                    @Override
                    protected void populateViewHolder(MenuItemViewHolder menuItemViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                        RelativeLayout relativeLayout = (RelativeLayout) menuItemViewHolder.mView.findViewById(R.id.menu_item_relative_layout);
                        ImageView cart = (ImageView) menuItemViewHolder.mView.findViewById(R.id.menu_item_cart);
                        ImageView explore = (ImageView) menuItemViewHolder.mView.findViewById(R.id.menu_item_add);
                        TextView itemPriceOffer = (TextView) menuItemViewHolder.mView.findViewById(R.id.menu_item_price);
                        ProgressBar cartProgress = (ProgressBar) menuItemViewHolder.mView.findViewById(R.id.menu_item_progress_bar);
                        ProgressBar favProgress = (ProgressBar) menuItemViewHolder.mView.findViewById(R.id.menu_item_fav_progress_bar);
                        String menuKey = getRef(i).getKey();

                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        if (offerStatus.equals("active"))
                        {
                            explore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Picasso.with(getApplicationContext()).load(menuItemAdapter.getImage()).into(pizzaOneImage);
                                    pizzaOneName.setText(menuItemAdapter.getName());
                                    pizzaOnePrice.setText(itemPriceOffer.getText().toString());
                                    priceCondition = itemPriceOffer.getText().toString();
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
                            menuItemViewHolder.setName(menuItemAdapter.getName());
                            menuItemViewHolder.setImage(getApplicationContext(),menuItemAdapter.getImage());
                            menuItemViewHolder.setMedium(menuItemAdapter.getMedium());
                            progressBarOne.setVisibility(View.GONE);
                        }
                        else
                        {
                            menuItemViewHolder.setMedium(menuItemAdapter.getLarge());
                            menuItemViewHolder.setName(menuItemAdapter.getName());
                            menuItemViewHolder.setImage(getApplicationContext(),menuItemAdapter.getImage());
                            progressBarOne.setVisibility(View.GONE);
                        }


                    }
                };
        menuListView1.setAdapter(firebaseRecyclerAdapter);
    }

    private void displayMenuItems2(String key, String offerStatus) {
        Query sorting = menuRef.child(key).child("MenuItem").child("VegPizza").orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder>(
                        MenuItemAdapter.class,
                        R.layout.menu_item_layout,
                        MenuItemViewHolder.class,
                        sorting
                ) {
                    @Override
                    protected void populateViewHolder(MenuItemViewHolder menuItemViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                        RelativeLayout relativeLayout = (RelativeLayout) menuItemViewHolder.mView.findViewById(R.id.menu_item_relative_layout);
                        ImageView cart = (ImageView) menuItemViewHolder.mView.findViewById(R.id.menu_item_cart);
                        ImageView explore = (ImageView) menuItemViewHolder.mView.findViewById(R.id.menu_item_add);
                        TextView itemPriceOffer = (TextView) menuItemViewHolder.mView.findViewById(R.id.menu_item_price);
                        ProgressBar cartProgress = (ProgressBar) menuItemViewHolder.mView.findViewById(R.id.menu_item_progress_bar);
                        ProgressBar favProgress = (ProgressBar) menuItemViewHolder.mView.findViewById(R.id.menu_item_fav_progress_bar);
                        String menuKey = getRef(i).getKey();

                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        if (offerStatus.equals("active"))
                        {
                            explore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Picasso.with(getApplicationContext()).load(menuItemAdapter.getImage()).into(pizzaTwoImage);
                                    pizzaTwoName.setText(menuItemAdapter.getName());
                                    pizzaTwoPrice.setText(itemPriceOffer.getText().toString());
                                    payablePrice.setText(itemPriceOffer.getText().toString());
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
                                menuItemViewHolder.setImage(getApplicationContext(),menuItemAdapter.getImage());
                                menuItemViewHolder.setMedium(menuItemAdapter.getMedium());
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
                                menuItemViewHolder.setName(menuItemAdapter.getName());
                                menuItemViewHolder.setImage(getApplicationContext(),menuItemAdapter.getImage());
                                menuItemViewHolder.setMedium(menuItemAdapter.getLarge());
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

        public void setName(String name)
        {
            TextView menuName = (TextView) mView.findViewById(R.id.menu_item_name);
            menuName.setText(name);
        }

        public void setImage(Context ctx, String image)
        {
            ImageView menuImage = (ImageView) mView.findViewById(R.id.menu_item_image);
            Picasso.with(ctx).load(image).placeholder(R.drawable.ic_fastfood_210).into(menuImage);
        }

        public void setMedium(String medium)
        {
            TextView menuPrice = (TextView) mView.findViewById(R.id.menu_item_price);
            menuPrice.setText(medium);
        }
    }

    public static class ShopDetailsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ShopDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setShopName(String shopName)
        {
            TextView name = (TextView) mView.findViewById(R.id.offers_shop_details_name);
            name.setText(shopName);
        }

        public void setShopFrontImage(String shopFrontImage, Context context)
        {
            ImageView image = (ImageView) mView.findViewById(R.id.offers_shop_details_shop_image);
            Picasso.with(context).load(shopFrontImage).placeholder(R.drawable.ic_baseline_shop_default).into(image);
        }
    }
}