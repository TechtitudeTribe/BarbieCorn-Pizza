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
import android.view.View;
import android.widget.HorizontalScrollView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyMenuActivity extends AppCompatActivity {

    private TextView categoryVegPizzaText, categoryNonVegPizzaText, categoryBurgerText, categorySideOrdersText, categoryBeveragesText, categoryPastaText, categoryFrenchFriesText, categoryDesertsText;
    private ImageView categoryVegPizza, categoryNonVegPizza, categoryBurger, categorySideOrders, categoryBeverages, categoryPasta, categoryFrenchFries, categoryDeserts;
    private LinearLayout categoryVegPizzaLayout, categoryNonVegPizzaLayout, categoryBurgerLayout, categorySideOrdersLayout, categoryBeveragesLayout, categoryPastaLayout, categoryFrenchFriesLayout, categoryDesertsLayout;
    private RecyclerView menuListView;
    private DatabaseReference menuRef,cartRef,favRef,shopRef;
    private FirebaseAuth mAuth;
    private String currentUser,categoryName="VegPizza",shopName;
    private long count,cartItems=0,favItems=0,shopItems=0;
    private ProgressBar progressBar;
    private TextView shopItemNamesText,favItemNamesText,shopAddress,deliveryCharge,userId,upi;
    private String favStatus="";
    private char homeCategory;
    private HorizontalScrollView horizontalScrollView;
    private int scrollPosition;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_menu);

        shopAddress = (TextView) findViewById(R.id.menu_fragment_shop_address);
        shopAddress.setText(getIntent().getStringExtra("shopAddress"));
        //key = getIntent().getStringExtra("key");
        homeCategory = getIntent().getStringExtra("category").charAt(0);
        categoryName = getIntent().getStringExtra("categoryName");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        deliveryCharge = (TextView) findViewById(R.id.my_menu_delivery_charge);
        userId = (TextView) findViewById(R.id.my_menu_seller_id);
        upi = (TextView) findViewById(R.id.my_menu_upi);
        container = (LinearLayout) findViewById(R.id.horizontal_scroll_container);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.menu_horizontal);
        shopRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString());
        cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyCart");
        favRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyFavorites");
        menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("VegPizza");
        progressBar = (ProgressBar) findViewById(R.id.menu_fragment_progress_bar);
        shopItemNamesText = (TextView) findViewById(R.id.shop_name_item_text);

        favItemNamesText = (TextView) findViewById(R.id.fav_menu_iten_name_text);
        categoryVegPizza = (ImageView) findViewById(R.id.category_veg_pizza);
        categoryNonVegPizza = (ImageView) findViewById(R.id.category_non_veg_pizza);
        categoryBurger = (ImageView) findViewById(R.id.category_burger);
        categorySideOrders = (ImageView) findViewById(R.id.category_side_orders);
        categoryBeverages = (ImageView) findViewById(R.id.category_beverages);
        categoryPasta = (ImageView) findViewById(R.id.category_pasta);
        categoryFrenchFries = (ImageView) findViewById(R.id.category_french_fries);
        categoryDeserts = (ImageView) findViewById(R.id.category_deserts);

        categoryVegPizzaLayout = (LinearLayout) findViewById(R.id.category_veg_pizza_layout);
        categoryNonVegPizzaLayout = (LinearLayout) findViewById(R.id.category_non_veg_pizza_layout);
        categoryBurgerLayout = (LinearLayout) findViewById(R.id.category_burger_layout);
        categorySideOrdersLayout = (LinearLayout) findViewById(R.id.category_side_orders_layout);
        categoryBeveragesLayout = (LinearLayout) findViewById(R.id.category_beverages_layout);
        categoryPastaLayout = (LinearLayout) findViewById(R.id.category_pasta_layout);
        categoryFrenchFriesLayout = (LinearLayout) findViewById(R.id.category_french_fries_layout);
        categoryDesertsLayout = (LinearLayout) findViewById(R.id.category_deserts_layout);

        categoryVegPizzaText = (TextView) findViewById(R.id.category_veg_pizza_text);
        categoryNonVegPizzaText = (TextView) findViewById(R.id.category_non_veg_pizza_text);
        categoryBurgerText = (TextView) findViewById(R.id.category_burger_text);
        categorySideOrdersText = (TextView) findViewById(R.id.category_side_orders_text);
        categoryBeveragesText = (TextView) findViewById(R.id.category_beverages_text);
        categoryPastaText = (TextView) findViewById(R.id.category_pasta_text);
        categoryFrenchFriesText = (TextView) findViewById(R.id.category_french_fries_text);
        categoryDesertsText = (TextView) findViewById(R.id.category_deserts_text);

        menuListView = (RecyclerView) findViewById(R.id.menu_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        menuListView.setLayoutManager(linearLayoutManager);

        switch (homeCategory)
        {
            case 'a' :  categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                scrollPosition = 0;
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("VegPizza");
                break;
            case 'b' :   categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                scrollPosition = 2;
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("SideOrders");
                break;
            case 'c' :  categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                scrollPosition = 6;
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("Deserts");
                break;
            case 'd' :  categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                scrollPosition = 3;
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("Beverages");
                break;
        }

        categoryVegPizzaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("VegPizza");
                categoryName=categoryVegPizzaText.getText().toString();
                categoryName = categoryName.replace(" ","");
                displayMenuItems();
            }
        });
        categoryNonVegPizzaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("NonVegPizza");
                categoryName=categoryNonVegPizzaText.getText().toString();
                categoryName = categoryName.replace(" ","");
                displayMenuItems();
            }
        });
        categoryBurgerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("Burger");
                categoryName=categoryBurgerText.getText().toString();
                displayMenuItems();
            }
        });
        categorySideOrdersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("SideOrders");
                categoryName=categorySideOrdersText.getText().toString();
                displayMenuItems();
            }
        });
        categoryBeveragesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("Beverages");
                categoryName=categoryBeveragesText.getText().toString();
                displayMenuItems();
            }
        });
        categoryPastaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("Pasta");
                categoryName=categoryPastaText.getText().toString();
                displayMenuItems();
            }
        });
        categoryFrenchFriesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("FrenchFries");
                categoryName=categoryFrenchFriesText.getText().toString();
                displayMenuItems();
            }
        });
        categoryDesertsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                categoryVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizza.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurger.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrders.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeverages.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPasta.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFries.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDeserts.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                categoryVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryNonVegPizzaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBurgerText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categorySideOrdersText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryBeveragesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryPastaText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryFrenchFriesText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                categoryDesertsText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.smoky_black));
                menuRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(shopAddress.getText().toString()).child("MenuItem").child("Deserts");
                categoryName=categoryDesertsText.getText().toString();
                displayMenuItems();
            }
        });

        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String dc = snapshot.child("deliveryCharge").getValue().toString();
                    String upiString = snapshot.child("upi").getValue().toString();
                    String ui = snapshot.child("userId").getValue().toString();

                    deliveryCharge.setText(dc);
                    upi.setText(upiString);
                    userId.setText(ui);
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
        cartRef.child(shopAddress.getText().toString()).addValueEventListener(new ValueEventListener() {
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

        cartRef.child(shopAddress.getText().toString()).child("CartItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren())
                {
                    cartItems = snapshot.getChildrenCount();
                }
                else
                {
                    cartItems = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    favItems = snapshot.getChildrenCount();
                }
                else
                {
                    favItems=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String itemName = ds.child("itemName").getValue().toString();
                    favStatus = favStatus + itemName;
                }
                favItemNamesText.setText(favStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        displayMenuItems();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            // do smoothScrollTo(...);
            horizontalScrollView.smoothScrollTo(scrollPosition,0);
        }
    }

    private void displayMenuItems() {
        Query sorting = menuRef.orderByChild("count");
        FirebaseRecyclerAdapter<MenuItemAdapter,MenuItemViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MenuItemAdapter, MenuItemViewHolder>(
                        MenuItemAdapter.class,
                        R.layout.menu_item_layout,
                        MenuItemViewHolder.class,
                        sorting
                ) {
                    @Override
                    protected void populateViewHolder(MenuItemViewHolder menuItemViewHolder, MenuItemAdapter menuItemAdapter, int i) {

                        CardView cartLayout = (CardView) menuItemViewHolder.mView.findViewById(R.id.menu_item_cart_layout);
                        CardView exploreLayout = (CardView) menuItemViewHolder.mView.findViewById(R.id.menu_item_add_layout);
                        TextView fav = (TextView) menuItemViewHolder.mView.findViewById(R.id.menu_item_fav);
                        ProgressBar cartProgress = (ProgressBar) menuItemViewHolder.mView.findViewById(R.id.menu_item_progress_bar);
                        ProgressBar favProgress = (ProgressBar) menuItemViewHolder.mView.findViewById(R.id.menu_item_fav_progress_bar);
                        View view = (View) menuItemViewHolder.mView.findViewById(R.id.menu_item_view);
                        TextView cart = (TextView) menuItemViewHolder.mView.findViewById(R.id.menu_item_cart);
                        ImageView list = (ImageView) menuItemViewHolder.mView.findViewById(R.id.menu_item_add);
                        TextView bottom = (TextView) menuItemViewHolder.mView.findViewById(R.id.menu_item_description);
                        String menuKey = getRef(i).getKey();

                        switch (i%3)
                        {
                            case 0 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                cart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                fav.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                cart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                fav.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                break;
                            case 2 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                cart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                fav.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                list.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                bottom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                cart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                fav.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        menuItemViewHolder.setName(menuItemAdapter.getName());
                        menuItemViewHolder.setImage(getApplicationContext(),menuItemAdapter.getImage());
                        menuItemViewHolder.setPrice(menuItemAdapter.getPrice());
                        menuItemViewHolder.setDescription(menuItemAdapter.getDescription());
                        progressBar.setVisibility(View.GONE);

                        if(menuItemAdapter.getAvailability().equals("Yes"))
                        {
                            if(categoryName.equals("VegPizza")||categoryName.equals("Non-VegPizza"))
                            {
                                exploreLayout.setVisibility(View.VISIBLE);
                                cartLayout.setVisibility(View.GONE);
                                bottom.setVisibility(View.VISIBLE);
                                fav.setVisibility(View.GONE);
                                menuItemViewHolder.mView
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getApplicationContext(),MenuItemDescriptionActivity.class);
                                                intent.putExtra("menuKey",menuKey);
                                                intent.putExtra("category",categoryName);
                                                intent.putExtra("userID",userId.getText().toString());
                                                intent.putExtra("shopAddress",shopAddress.getText().toString());
                                                intent.putExtra("deliveryCharge",deliveryCharge.getText().toString());
                                                intent.putExtra("upi",upi.getText().toString());
                                                startActivity(intent);
                                            }
                                        });
                            }
                            else
                            {
                                exploreLayout.setVisibility(View.GONE);
                                cartLayout.setVisibility(View.VISIBLE);
                                bottom.setVisibility(View.GONE);
                                fav.setVisibility(View.VISIBLE);
                                cartLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        cartLayout.setVisibility(View.GONE);
                                        cartProgress.setVisibility(View.VISIBLE);

                                        if (shopItemNamesText.getText().toString().contains(menuItemAdapter.getName()))
                                        {
                                            Toast.makeText(getApplicationContext(), "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                            cartLayout.setVisibility(View.GONE);
                                            cartProgress.setVisibility(View.GONE);
                                        }
                                        else {

                                            HashMap hashMap1 = new HashMap();
                                            hashMap1.put("count", shopItems + 1);
                                            hashMap1.put("itemNames", shopItemNamesText.getText().toString() + menuItemAdapter.getName());
                                            hashMap1.put("shopName", shopAddress.getText().toString());
                                            hashMap1.put("sellerId", userId.getText().toString());
                                            hashMap1.put("deliveryCharge",deliveryCharge.getText().toString());
                                            hashMap1.put("upi",upi.getText().toString());

                                            cartRef.child(shopAddress.getText().toString()).updateChildren(hashMap1);

                                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                                                        String currentDateandTime = sdf.format(new Date());

                                                        HashMap hashMap = new HashMap();
                                                        hashMap.put("count", cartItems + 1);
                                                        hashMap.put("itemName", menuItemAdapter.getName());
                                                        hashMap.put("itemPrice", menuItemAdapter.getPrice());
                                                        hashMap.put("itemCustomizedPrice", menuItemAdapter.getPrice());
                                                        hashMap.put("itemDescription", "No modification in this item by the user...");
                                                        hashMap.put("itemImage", menuItemAdapter.getImage());
                                                        hashMap.put("itemQuantity", "1");

                                                        cartRef.child(shopAddress.getText().toString()).child("CartItems").child("CartItem" + currentDateandTime).updateChildren(hashMap);
                                            cartLayout.setVisibility(View.GONE);
                                            cartProgress.setVisibility(View.GONE);
                                        /*, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(@Nullable DatabaseError error1, @NonNull DatabaseReference ref) {
                                                                if(error1!=null)
                                                                {
                                                                    String message = error1.getMessage();
                                                                    Toast.makeText(getApplicationContext(), "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                                                                    cartProgress.setVisibility(View.GONE);
                                                                    cart.setVisibility(View.VISIBLE);
                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(getApplicationContext(), "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                                                    cart.setVisibility(View.GONE);
                                                                    cartProgress.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });*/
                                        }
                                    }
                                });
                            }
                        }
                        else
                        {
                            cartLayout.setVisibility(View.GONE);
                            exploreLayout.setVisibility(View.GONE);
                            fav.setVisibility(View.GONE);
                            menuItemViewHolder.mView.setClickable(false);
                        }

                        fav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (favItemNamesText.getText().toString().contains(menuItemAdapter.getName()))
                                {
                                    fav.setVisibility(View.GONE);
                                }
                                else
                                {
                                    fav.setVisibility(View.GONE);
                                    favProgress.setVisibility(View.VISIBLE);
                                    
                                    Map<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("count",favItems+1);
                                    hashMap.put("shopName", shopAddress.getText().toString());
                                    hashMap.put("sellerId", userId.getText().toString());
                                    hashMap.put("deliveryCharge",deliveryCharge.getText().toString());
                                    hashMap.put("upi",upi.getText().toString());
                                    hashMap.put("itemName",menuItemAdapter.getName());
                                    hashMap.put("itemPrice",menuItemAdapter.getPrice());
                                    hashMap.put("itemImage",menuItemAdapter.getImage());
                                    hashMap.put("itemDescription","No modification is done by the user...");

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                                    String currentDateandTime = sdf.format(new Date());

                                    favRef.child("MyFav"+currentDateandTime).updateChildren(hashMap);
                                    favProgress.setVisibility(View.GONE);
                                }

                            }
                        });

                    }
                };
        menuListView.setAdapter(firebaseRecyclerAdapter);
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

        public void setPrice(String price)
        {
            TextView menuPrice = (TextView) mView.findViewById(R.id.menu_item_price);
            menuPrice.setText(price);
        }

        public void setDescription(String description)
        {
            TextView menuDescription = (TextView) mView.findViewById(R.id.menu_item_description);
            menuDescription.setText(description);
        }
    }
}