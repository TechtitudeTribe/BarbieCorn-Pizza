package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MenuItemDescriptionActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference foodRef, cartRef,favRef;
    private String currentUser, imageUrl;
    private TextView itemPrice, itemDescription, itemName;
    private ImageView itemImage, regularSizeImage, mediumSizeImage, largeSizeImage;
    private ProgressBar itemProgressBar;
    private CardView vegToppingCard, nonVegToppingCard;
    private RelativeLayout vegToppingLayout, nonVegToppingLayout;
    private TextView vegToppingText, nonVegToppingText;
    private String key, category,menuKey,shopName,userId;
    private LinearLayout regularLayout, mediumLayout, largeLayout,vegToppingPriceLayout,nonVegToppingPriceLayout;
    private TextView regularTitle, mediumTitle, largeTitle, regularServes, mediumServes, largeServes;
    private TextView extraCheesePrice,shopItemNamesText,thinCrustPrice,cheeseBurstPrice;
    private TextView vegToppingPrice,nonVegToppingPrice;
    private ImageView addExtraCheeseVerified,addExtraCheeseButton;
    private ImageView cheeseBurstVerified, cheeseBurstButton;
    private ImageView thinCrustVerified, thinCrustButton;
    private TextView totalCostText,favItemNamesText;
    private int totalCost,cheeseAmount=0,toppingAmount=0,cheeseBurstAmount=0,thinCrustAmount=0;
    private TextView addToCart,viewCart;
    private long cartItems=0,favItems=0,shopItems=0;
    private ProgressBar cartProgressBar;
    private String pizzaSize="Medium";
    private String extraCheese = "No";
    private String cheeseBurst = "No";
    private String thinCrust = "No";
    private RecyclerView vegToppingList, nonVegToppingList;
    private DatabaseReference toppingRef;
    private ProgressBar vegToppingProgressBar, nonVegToppingProgressBar;
    private String toppingGlobal="No";
    private Boolean itemAvailable = false,favItemAvailable=false;
    private ImageView favButon;
    private ProgressBar favProgressBar;
    private String favStatus="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item_description);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();

        category = getIntent().getExtras().getString("category");
        menuKey = getIntent().getExtras().getString("menuKey");
        shopName = getIntent().getExtras().getString("shopAddress");
        userId = getIntent().getExtras().getString("userID");
        favRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyFavorites");

        favItemNamesText = (TextView) findViewById(R.id.fav_menu_iten_description_name_text);
        favButon = (ImageView) findViewById(R.id.item_description_fav);
        favProgressBar = (ProgressBar) findViewById(R.id.item_description_fav_progress_bar);
        thinCrustPrice = (TextView) findViewById(R.id.add_thin_crust_price);
        cheeseBurstPrice = (TextView) findViewById(R.id.add_cheese_burst_price);
        extraCheesePrice = (TextView) findViewById(R.id.add_extra_cheese_price);
        vegToppingPrice = (TextView) findViewById(R.id.veg_topping_price);
        nonVegToppingPrice = (TextView) findViewById(R.id.non_veg_topping_price);

        thinCrustVerified = (ImageView) findViewById(R.id.add_thin_crust_verified);
        cheeseBurstVerified = (ImageView) findViewById(R.id.add_cheese_burst_verified);
        thinCrustButton = (ImageView) findViewById(R.id.add_thin_crust_button);
        cheeseBurstButton = (ImageView) findViewById(R.id.add_cheese_burst_button);
        shopItemNamesText = (TextView) findViewById(R.id.shop_name_item_des_text);
        addExtraCheeseButton = (ImageView) findViewById(R.id.add_extra_cheese_button);
        addExtraCheeseVerified = (ImageView) findViewById(R.id.add_extra_cheese_verified);
        totalCostText = (TextView) findViewById(R.id.menu_item_description_total_cost);
        addToCart = (TextView) findViewById(R.id.menu_item_description_add_cart);
        viewCart = (TextView) findViewById(R.id.menu_item_description_view_cart);
        cartProgressBar = (ProgressBar) findViewById(R.id.menu_item_description_progress_bar);
        vegToppingProgressBar = (ProgressBar) findViewById(R.id.veg_topping_progress_bar);
        nonVegToppingProgressBar = (ProgressBar) findViewById(R.id.non_veg_topping_progress_bar);

        vegToppingList  = (RecyclerView) findViewById(R.id.veg_topping_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        vegToppingList.setLayoutManager(gridLayoutManager);

        nonVegToppingList  = (RecyclerView) findViewById(R.id.non_veg_topping_list);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getApplicationContext(),3);
        nonVegToppingList.setLayoutManager(gridLayoutManager1);

        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyCart");
        foodRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(getIntent().getStringExtra("shopAddress")).child("MenuItem").child(category).child(menuKey);
        toppingRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(getIntent().getStringExtra("shopAddress")).child("MyTopping");

        itemPrice = (TextView) findViewById(R.id.menu_item_description_item_price);
        itemDescription = (TextView) findViewById(R.id.menu_item_description_item_description);
        itemName = (TextView) findViewById(R.id.menu_item_description_item_name);

        itemImage = (ImageView) findViewById(R.id.menu_item_description_item_image);
        itemProgressBar = (ProgressBar) findViewById(R.id.menu_item_description_item_progress_bar);

        vegToppingCard = (CardView) findViewById(R.id.veg_topping_card);
        vegToppingLayout = (RelativeLayout) findViewById(R.id.veg_topping_layout);
        vegToppingText = (TextView) findViewById(R.id.veg_topping_text);
        vegToppingPriceLayout = (LinearLayout) findViewById(R.id.veg_topping_price_layout);
        nonVegToppingCard = (CardView) findViewById(R.id.non_veg_topping_card);
        nonVegToppingLayout = (RelativeLayout) findViewById(R.id.non_veg_topping_layout);
        nonVegToppingText = (TextView) findViewById(R.id.non_veg_topping_text);
        nonVegToppingPriceLayout = (LinearLayout) findViewById(R.id.non_veg_topping_price_layout);

        regularSizeImage = (ImageView) findViewById(R.id.regular_size_layout_image);
        mediumSizeImage = (ImageView) findViewById(R.id.medium_size_layout_image);
        largeSizeImage = (ImageView) findViewById(R.id.large_size_layout_image);

        regularLayout = (LinearLayout) findViewById(R.id.regular_size_layout);
        mediumLayout = (LinearLayout) findViewById(R.id.medium_size_layout);
        largeLayout = (LinearLayout) findViewById(R.id.large_size_layout);

        regularTitle = (TextView) findViewById(R.id.regular_size_layout_title);
        mediumTitle = (TextView) findViewById(R.id.medium_size_layout_title);
        largeTitle = (TextView) findViewById(R.id.large_size_layout_title);

        regularServes = (TextView) findViewById(R.id.regular_size_layout_serves);
        mediumServes = (TextView) findViewById(R.id.medium_size_layout_serves);
        largeServes = (TextView) findViewById(R.id.large_size_layout_serves);

        cheeseBurstPrice.setText("80");
        thinCrustPrice.setText("40");

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren())
                {
                    favItems = snapshot.getChildrenCount();
                }
                else
                {
                    favItems = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        regularLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regularLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                mediumLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                largeLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                regularSizeImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                mediumSizeImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                largeSizeImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                regularTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                mediumTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                largeTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                regularServes.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                mediumServes.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                largeServes.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                foodRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String mainPrice = snapshot.child("price").getValue().toString();
                            String cheesePrice = snapshot.child("smallCheese").getValue().toString();
                            String vegToppingPriceString = snapshot.child("smallVegTopping").getValue().toString();
                            String nonVegToppingPriceString = snapshot.child("smallNonVegTopping").getValue().toString();

                            itemPrice.setText(mainPrice);
                            extraCheesePrice.setText(cheesePrice);
                            vegToppingPrice.setText(vegToppingPriceString);
                            nonVegToppingPrice.setText(nonVegToppingPriceString);
                            totalCost = Integer.parseInt(mainPrice);
                            totalCostText.setText(itemPrice.getText().toString());
                            cheeseBurstPrice.setText("50");
                            thinCrustPrice.setText("00");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                addExtraCheeseButton.setVisibility(View.VISIBLE);
                addExtraCheeseVerified.setVisibility(View.GONE);
                cheeseBurstButton.setVisibility(View.VISIBLE);
                cheeseBurstVerified.setVisibility(View.GONE);
                thinCrustButton.setVisibility(View.GONE);
                thinCrustVerified.setVisibility(View.GONE);
                totalCostText.setText(itemPrice.getText().toString());
                cheeseBurst = "No";
                thinCrust = "No";
                pizzaSize="Regular";
                extraCheese = "No";
                toppingGlobal = "No";
                showVegToppingDetails();
                showNonVegToppingDetails();
            }
        });
        mediumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regularLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                mediumLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                largeLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                regularSizeImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                mediumSizeImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                largeSizeImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                regularTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                mediumTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                largeTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                regularServes.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                mediumServes.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                largeServes.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                foodRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String mainPrice = snapshot.child("medium").getValue().toString();
                            String cheesePrice = snapshot.child("mediumCheese").getValue().toString();
                            String vegToppingPriceString = snapshot.child("mediumVegTopping").getValue().toString();
                            String nonVegToppingPriceString = snapshot.child("mediumNonVegTopping").getValue().toString();

                            itemPrice.setText(mainPrice);
                            extraCheesePrice.setText(cheesePrice);
                            vegToppingPrice.setText(vegToppingPriceString);
                            nonVegToppingPrice.setText(nonVegToppingPriceString);
                            totalCost = Integer.parseInt(mainPrice);
                            totalCostText.setText(String.valueOf(totalCost));
                            cheeseBurstPrice.setText("80");
                            thinCrustPrice.setText("40");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                addExtraCheeseButton.setVisibility(View.VISIBLE);
                addExtraCheeseVerified.setVisibility(View.GONE);
                cheeseBurstButton.setVisibility(View.VISIBLE);
                cheeseBurstVerified.setVisibility(View.GONE);
                thinCrustButton.setVisibility(View.VISIBLE);
                thinCrustVerified.setVisibility(View.GONE);
                totalCostText.setText(itemPrice.getText().toString());
                cheeseBurst = "No";
                thinCrust = "No";
                pizzaSize="Medium";
                extraCheese = "No";
                toppingGlobal = "No";
                showVegToppingDetails();
                showNonVegToppingDetails();
            }
        });
        largeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regularLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                mediumLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.text_background));
                largeLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.layout_background));
                regularSizeImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                mediumSizeImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                largeSizeImage.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                regularTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                mediumTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                largeTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                regularServes.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                mediumServes.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.title_background));
                largeServes.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                foodRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String mainPrice = snapshot.child("large").getValue().toString();
                            String cheesePrice = snapshot.child("largeCheese").getValue().toString();
                            String vegToppingPriceString = snapshot.child("largeVegTopping").getValue().toString();
                            String nonVegToppingPriceString = snapshot.child("largeNonVegTopping").getValue().toString();

                            itemPrice.setText(mainPrice);
                            extraCheesePrice.setText(cheesePrice);
                            vegToppingPrice.setText(vegToppingPriceString);
                            nonVegToppingPrice.setText(nonVegToppingPriceString);
                            totalCost = Integer.parseInt(mainPrice);
                            totalCostText.setText(String.valueOf(totalCost));
                            cheeseBurstPrice.setText("100");
                            thinCrustPrice.setText("50");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                addExtraCheeseButton.setVisibility(View.VISIBLE);
                addExtraCheeseVerified.setVisibility(View.GONE);
                cheeseBurstButton.setVisibility(View.VISIBLE);
                cheeseBurstVerified.setVisibility(View.GONE);
                thinCrustButton.setVisibility(View.VISIBLE);
                thinCrustVerified.setVisibility(View.GONE);
                totalCostText.setText(itemPrice.getText().toString());
                cheeseBurst = "No";
                thinCrust = "No";
                pizzaSize="Large";
                extraCheese = "No";
                toppingGlobal = "No";
                showVegToppingDetails();
                showNonVegToppingDetails();
            }
        });

        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuItemDescriptionActivity.this,ShopCartItemActivity.class);
                intent.putExtra("key",shopName);
                startActivity(intent);
            }
        });

        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String name = snapshot.child("name").getValue().toString();
                    String description = snapshot.child("description").getValue().toString();
                    String price = snapshot.child("medium").getValue().toString();
                    String image = snapshot.child("image").getValue().toString();
                    String smallCheese = snapshot.child("mediumCheese").getValue().toString();
                    String vegToppingPriceString = snapshot.child("mediumVegTopping").getValue().toString();

                    imageUrl=image;
                    itemPrice.setText(price);
                    itemDescription.setText(description);
                    itemName.setText(name);
                    vegToppingPrice.setText(vegToppingPriceString);
                    Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.ic_fastfood_210).into(itemImage);
                    itemProgressBar.setVisibility(View.GONE);
                    totalCostText.setText(price);
                    extraCheesePrice.setText(smallCheese);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        vegToppingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vegToppingLayout.setVisibility(View.VISIBLE);
                nonVegToppingLayout.setVisibility(View.GONE);
                vegToppingPriceLayout.setVisibility(View.VISIBLE);
                nonVegToppingPriceLayout.setVisibility(View.GONE);
                vegToppingText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                vegToppingText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
                nonVegToppingText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                nonVegToppingText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
            }
        });
        nonVegToppingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vegToppingLayout.setVisibility(View.GONE);
                nonVegToppingLayout.setVisibility(View.VISIBLE);
                vegToppingPriceLayout.setVisibility(View.GONE);
                nonVegToppingPriceLayout.setVisibility(View.VISIBLE);
                vegToppingText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                vegToppingText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegToppingText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                nonVegToppingText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_red));
            }
        });

        addExtraCheeseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cheeseAmount = Integer.parseInt(extraCheesePrice.getText().toString());
                    addExtraCheeseButton.setVisibility(View.GONE);
                    addExtraCheeseVerified.setVisibility(View.VISIBLE);
                    totalCost = Integer.parseInt(totalCostText.getText().toString())+cheeseAmount;
                    totalCostText.setText(String.valueOf(totalCost));
                    extraCheese = "Yes";
                }
            });

        cheeseBurstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cheeseBurstAmount = Integer.parseInt(cheeseBurstPrice.getText().toString());
                cheeseBurstButton.setVisibility(View.GONE);
                cheeseBurstVerified.setVisibility(View.VISIBLE);
                totalCost = Integer.parseInt(totalCostText.getText().toString())+cheeseBurstAmount;
                totalCostText.setText(String.valueOf(totalCost));
                cheeseBurst = "Yes";
            }
        });

        thinCrustButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thinCrustAmount = Integer.parseInt(thinCrustPrice.getText().toString());
                thinCrustButton.setVisibility(View.GONE);
                thinCrustVerified.setVisibility(View.VISIBLE);
                totalCost = Integer.parseInt(totalCostText.getText().toString())+thinCrustAmount;
                totalCostText.setText(String.valueOf(totalCost));
                thinCrust = "Yes";
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

        addExtraCheeseVerified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cheeseAmount = Integer.parseInt(extraCheesePrice.getText().toString());;
                    addExtraCheeseButton.setVisibility(View.VISIBLE);
                    addExtraCheeseVerified.setVisibility(View.GONE);
                    totalCost = Integer.parseInt(totalCostText.getText().toString())-cheeseAmount;
                    totalCostText.setText(String.valueOf(totalCost));
                    extraCheese = "No";
                }
            });

        cheeseBurstVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cheeseBurstAmount = Integer.parseInt(cheeseBurstPrice.getText().toString());
                cheeseBurstButton.setVisibility(View.VISIBLE);
                cheeseBurstVerified.setVisibility(View.GONE);
                totalCost = Integer.parseInt(totalCostText.getText().toString())-cheeseBurstAmount;
                totalCostText.setText(String.valueOf(totalCost));
                cheeseBurst = "No";
            }
        });

        thinCrustVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thinCrustAmount = Integer.parseInt(thinCrustPrice.getText().toString());
                thinCrustButton.setVisibility(View.VISIBLE);
                thinCrustVerified.setVisibility(View.GONE);
                totalCost = Integer.parseInt(totalCostText.getText().toString())-thinCrustAmount;
                totalCostText.setText(String.valueOf(totalCost));
                thinCrust = "No";
            }
        });


        favButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String in = ds.child("itemName").getValue().toString();
                            if (itemName.getText().toString().equals(in)) {
                                favRef = ds.getRef();
                                favItemAvailable = true;
                            }
                        }

                        if (favItemAvailable) {
                            favButon.setVisibility(View.GONE);
                            favProgressBar.setVisibility(View.VISIBLE);

                            HashMap hashMap = new HashMap();
                            hashMap.put("itemPrice", totalCostText.getText().toString());
                            hashMap.put("itemDescription", "Size : " + pizzaSize + "\nExtra Cheese : " + extraCheese + "\nCheese Burst : " + cheeseBurst + "\nThin Crust : " + thinCrust + "\nExtra Topping : " + toppingGlobal);

                            favRef.updateChildren(hashMap);
                            favButon.setVisibility(View.GONE);
                            favProgressBar.setVisibility(View.GONE);
                            Toast.makeText(MenuItemDescriptionActivity.this, "Item added to favorite successfully...", Toast.LENGTH_SHORT).show();

                        } else {
                            favButon.setVisibility(View.GONE);
                            favProgressBar.setVisibility(View.VISIBLE);
                            HashMap hashMap = new HashMap();
                            hashMap.put("count", favItems + 1);
                            hashMap.put("shopName", shopName);
                            hashMap.put("sellerId", userId);
                            hashMap.put("deliveryCharge", getIntent().getStringExtra("deliveryCharge"));
                            hashMap.put("upi", getIntent().getStringExtra("upi"));
                            hashMap.put("itemName", itemName.getText().toString());
                            hashMap.put("itemPrice", totalCostText.getText().toString());
                            hashMap.put("itemImage", imageUrl);
                            hashMap.put("itemDescription", "Size : " + pizzaSize + "\nExtra Cheese : " + extraCheese + "\nCheese Burst : " + cheeseBurst + "\nThin Crust : " + thinCrust + "\nExtra Topping : " + toppingGlobal);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                            String currentDateandTime = sdf.format(new Date());

                            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyFavorites").child("MyFav" + currentDateandTime).updateChildren(hashMap);
                            favButon.setVisibility(View.GONE);
                            favProgressBar.setVisibility(View.GONE);
                            Toast.makeText(MenuItemDescriptionActivity.this, "Item added to favorite successfully...", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }
        });



                addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addToCart.setVisibility(View.GONE);
                        cartProgressBar.setVisibility(View.VISIBLE);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());

                        cartRef.child(shopName).child("CartItems").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {

                                    String in = ds.child("itemName").getValue().toString();
                                    if (itemName.getText().toString().equals(in)) {
                                        cartRef = ds.getRef();
                                        itemAvailable = true;
                                    }
                                }
                                if (itemAvailable) {
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("itemPrice", totalCostText.getText().toString());
                                    hashMap.put("itemCustomizedPrice", totalCostText.getText().toString());
                                    hashMap.put("itemDescription", "Size : " + pizzaSize + "\nExtra Cheese : " + extraCheese + "\nCheese Burst : " + cheeseBurst + "\nThin Crust : " + thinCrust + "\nExtra Topping : " + toppingGlobal);

                                    cartRef.updateChildren(hashMap);
                                    Toast.makeText(getApplicationContext(), "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                    addToCart.setVisibility(View.GONE);
                                    cartProgressBar.setVisibility(View.GONE);
                                    viewCart.setVisibility(View.VISIBLE);
                                } else     {
                                    HashMap hashMap1 = new HashMap();
                                    hashMap1.put("count", shopItems + 1);
                                    hashMap1.put("itemNames", shopItemNamesText.getText().toString() + itemName.getText().toString());
                                    hashMap1.put("shopName", shopName);
                                    hashMap1.put("sellerId", userId);
                                    hashMap1.put("deliveryCharge", getIntent().getStringExtra("deliveryCharge"));
                                    hashMap1.put("upi", getIntent().getStringExtra("upi"));

                                    FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyCart").child(shopName).updateChildren(hashMap1);
                                            {
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                                                String currentDateandTime = sdf.format(new Date());

                                                HashMap hashMap = new HashMap();
                                                hashMap.put("count", cartItems + 1);
                                                hashMap.put("itemName", itemName.getText().toString());
                                                hashMap.put("itemPrice", totalCostText.getText().toString());
                                                hashMap.put("itemCustomizedPrice", totalCostText.getText().toString());
                                                hashMap.put("itemDescription", "Size : " + pizzaSize + "\nExtra Cheese : " + extraCheese + "\nCheese Burst : " + cheeseBurst + "\nThin Crust : " + thinCrust + "\nExtra Topping : " + toppingGlobal);
                                                hashMap.put("itemImage", imageUrl);
                                                hashMap.put("itemQuantity", "1");

                                                FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyCart").child(shopName).child("CartItems").child("CartItem" + currentDateandTime)
                                                        .updateChildren(hashMap);
                                                Toast.makeText(getApplicationContext(), "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                                addToCart.setVisibility(View.GONE);
                                                cartProgressBar.setVisibility(View.GONE);
                                                viewCart.setVisibility(View.VISIBLE);
                                            }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                showVegToppingDetails();
                showNonVegToppingDetails();
            }


            private void showVegToppingDetails() {
                Query sort = toppingRef.child("VegTopping").orderByChild("count");
                FirebaseRecyclerAdapter<ToppingAdapter, ToppingViewHolder> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<ToppingAdapter, ToppingViewHolder>(
                                ToppingAdapter.class,
                                R.layout.topping_item_layout,
                                ToppingViewHolder.class,
                                sort
                        ) {
                            @Override
                            protected void populateViewHolder(ToppingViewHolder toppingViewHolder, ToppingAdapter toppingAdapter, int i) {

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
                                            totalCost = Integer.parseInt(totalCostText.getText().toString()) + Integer.parseInt(vegToppingPrice.getText().toString());
                                            totalCostText.setText(String.valueOf(totalCost));
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
                                            totalCost = Integer.parseInt(totalCostText.getText().toString()) - Integer.parseInt(vegToppingPrice.getText().toString());
                                            totalCostText.setText(String.valueOf(totalCost));
                                            imageView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                            textView.setText("no");
                                        }
                                    }
                                });
                            }
                        };
                vegToppingList.setAdapter(firebaseRecyclerAdapter);
            }

            private void showNonVegToppingDetails() {
                Query sort = toppingRef.child("NonVegTopping").orderByChild("count");
                FirebaseRecyclerAdapter<ToppingAdapter, ToppingViewHolder> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<ToppingAdapter, ToppingViewHolder>(
                                ToppingAdapter.class,
                                R.layout.topping_item_layout,
                                ToppingViewHolder.class,
                                sort
                        ) {
                            @Override
                            protected void populateViewHolder(ToppingViewHolder toppingViewHolder, ToppingAdapter toppingAdapter, int i) {

                                ImageView imageView = (ImageView) toppingViewHolder.mView.findViewById(R.id.topping_item_image);
                                TextView textView = (TextView) toppingViewHolder.mView.findViewById(R.id.topping_item_status);

                                toppingViewHolder.setItemImage(toppingAdapter.getItemImage(), getApplicationContext());
                                nonVegToppingProgressBar.setVisibility(View.GONE);

                                toppingViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (textView.getText().toString().equals("no")) {
                                            if (toppingGlobal.contains(toppingAdapter.getItemName())) {
                                                toppingGlobal = toppingGlobal + "";
                                            } else if (toppingGlobal.equals("")) {
                                                toppingGlobal = toppingAdapter.getItemName();
                                            } else {
                                                toppingGlobal = toppingGlobal + "," + toppingAdapter.getItemName();
                                            }
                                            totalCost = Integer.parseInt(totalCostText.getText().toString()) + Integer.parseInt(nonVegToppingPrice.getText().toString());
                                            totalCostText.setText(String.valueOf(totalCost));
                                            imageView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.smoky_black));
                                            textView.setText("yes");
                                        } else {
                                            if (toppingGlobal.contains(toppingAdapter.getItemName() + ",")) {
                                                toppingGlobal = toppingGlobal.replace(toppingAdapter.getItemName() + ",", "");
                                            } else if (toppingGlobal.contains("," + toppingAdapter.getItemName())) {
                                                toppingGlobal = toppingGlobal.replace("," + toppingAdapter.getItemName(), "");
                                            } else if (toppingGlobal.contains(toppingAdapter.getItemName())) {
                                                toppingGlobal = toppingGlobal.replace(toppingAdapter.getItemName(), "");
                                            }
                                            totalCost = Integer.parseInt(totalCostText.getText().toString()) - Integer.parseInt(nonVegToppingPrice.getText().toString());
                                            totalCostText.setText(String.valueOf(totalCost));
                                            imageView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                            textView.setText("no");
                                        }
                                    }
                                });
                            }
                        };
                nonVegToppingList.setAdapter(firebaseRecyclerAdapter);
            }

            public static class ToppingViewHolder extends RecyclerView.ViewHolder {

                View mView;

                public ToppingViewHolder(@NonNull View itemView) {
                    super(itemView);
                    mView = itemView;
                }

                public void setItemImage(String itemImage, Context context) {
                    ImageView imageView = (ImageView) mView.findViewById(R.id.topping_item_image);
                    Picasso.with(context).load(itemImage).placeholder(R.drawable.ic_fastfood_210).into(imageView);
                }
            }

}