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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyFavoriteActivity extends AppCompatActivity {

    private LinearLayout noFavLayout;
    private DatabaseReference favRef,cartRef,cartDetailRef;
    private FirebaseAuth firebaseAuth;
    private String currentUser;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView shopItemNamesText;
    private TextView localNameText;
    private long shopItems =0, cartItems =0;
    private CartShopItemAdapter cartShopItemAdapter;
    private CartItemAdapter cartItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        favRef = FirebaseDatabase.getInstance().getReference().child("MyFavorites").child(currentUser);
        cartRef = FirebaseDatabase.getInstance().getReference().child("MyCart").child(currentUser);
        cartDetailRef = FirebaseDatabase.getInstance().getReference().child("CartItems").child(currentUser);

        noFavLayout = (LinearLayout) findViewById(R.id.no_fav_layout);
        progressBar = (ProgressBar) findViewById(R.id.my_fav_progress_bar);

        cartShopItemAdapter = new CartShopItemAdapter();
        cartItemAdapter = new CartItemAdapter();

        localNameText = (TextView) findViewById(R.id.local_fav_text);
        shopItemNamesText = (TextView) findViewById(R.id.fav_shop_item_names_text);
        recyclerView = (RecyclerView) findViewById(R.id.my_fav_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    noFavLayout.setVisibility(View.GONE);
                    displayAllFavorites();
                }
                else
                {
                    noFavLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void displayAllFavorites() {
        Query sort = favRef.orderByChild("count");

        FirebaseRecyclerAdapter<MyFavAdapter,MyFavViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MyFavAdapter, MyFavViewHolder>(
                        MyFavAdapter.class,
                        R.layout.fav_item_layout,
                        MyFavViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(MyFavViewHolder myFavViewHolder, MyFavAdapter myFavAdapter, int i) {

                        myFavViewHolder.setItemDescription(myFavAdapter.getItemDescription());
                        myFavViewHolder.setItemImage(myFavAdapter.getItemImage(),getApplicationContext());
                        myFavViewHolder.setItemName(myFavAdapter.getItemName());
                        myFavViewHolder.setItemCustomizedPrice(myFavAdapter.getItemPrice());
                        progressBar.setVisibility(View.GONE);

                        View relativeLayout = (View) myFavViewHolder.mView.findViewById(R.id.fav_item_view);
                        TextView textView = (TextView) myFavViewHolder.mView.findViewById(R.id.fav_item_description);
                        CardView cardView = (CardView) myFavViewHolder.mView.findViewById(R.id.fav_item_add_cart_layout);
                        TextView cart = (TextView) myFavViewHolder.mView.findViewById(R.id.fav_item_add_cart);

                        CardView dltCard = (CardView) myFavViewHolder.mView.findViewById(R.id.fav_item_dlt_layout);
                        TextView dlt = (TextView) myFavViewHolder.mView.findViewById(R.id.fav_item_dlt);
                        String key = getRef(i).getKey();
                        //TextView itemQuantity = (TextView) myFavViewHolder.mView.findViewById(R.id.fav_item_quantity);
                        TextView itemName = (TextView) myFavViewHolder.mView.findViewById(R.id.fav_item_name);
                        ProgressBar progressBar1 = (ProgressBar) myFavViewHolder.mView.findViewById(R.id.fav_item_progress_cart);

                        switch (i%3)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                cart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                dlt.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                cart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                dlt.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                textView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                cart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                dlt.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                cart.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                dlt.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cardView.setVisibility(View.INVISIBLE);
                                progressBar1.setVisibility(View.VISIBLE);

                                localNameText.setText(myFavAdapter.getItemName());
                                //shopItemNamesText.setText(myFavAdapter.getItemName());
                                cartDetailRef.child(myFavAdapter.getShopName()).addValueEventListener(new ValueEventListener() {
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

                                cartRef.child(myFavAdapter.getShopName()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("itemNames")) {
                                            String shopItemNames = snapshot.child("itemNames").getValue().toString() + ",";
                                            shopItemNamesText.setText(shopItemNames);
                                        } else {
                                            shopItemNamesText.setText("");
                                        }

                                        if (shopItemNamesText.getText().toString().contains(localNameText.getText().toString())) {
                                            Toast.makeText(MyFavoriteActivity.this, "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                            cardView.setVisibility(View.INVISIBLE);
                                            progressBar1.setVisibility(View.GONE);
                                        } else {


                                            cartShopItemAdapter.setCount(shopItems + 1);
                                            cartShopItemAdapter.setItemNames(shopItemNamesText.getText().toString() + myFavAdapter.getItemName());
                                            cartShopItemAdapter.setShopName(myFavAdapter.getShopName());
                                            cartShopItemAdapter.setSellerId(myFavAdapter.getSellerId());
                                            cartShopItemAdapter.setDeliveryCharge(myFavAdapter.getDeliveryCharge());
                                            cartShopItemAdapter.setUpi(myFavAdapter.getUpi());

                                            ValueEventListener valueEventListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    cartRef.child(myFavAdapter.getShopName()).setValue(cartShopItemAdapter);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            };
                                            cartRef.child(myFavAdapter.getShopName()).addListenerForSingleValueEvent(valueEventListener);

                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                                            String currentDateandTime = sdf.format(new Date());
                                            cartItemAdapter.setCount(cartItems + 1);
                                            cartItemAdapter.setItemDescription( myFavAdapter.getItemDescription());
                                            cartItemAdapter.setItemCustomizedPrice(myFavAdapter.getItemPrice());
                                            cartItemAdapter.setItemImage(myFavAdapter.getItemImage());
                                            cartItemAdapter.setItemName( myFavAdapter.getItemName());
                                            cartItemAdapter.setItemPrice(myFavAdapter.getItemPrice());
                                            cartItemAdapter.setItemQuantity("1");

                                            ValueEventListener valueEventListener1 = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                   cartDetailRef.child(myFavAdapter.getShopName()).child("CartItem" + currentDateandTime).setValue(cartItemAdapter);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            };
                                            cartDetailRef.child(myFavAdapter.getShopName()).child("CartItem" + currentDateandTime).addListenerForSingleValueEvent(valueEventListener1);

                                            Toast.makeText(getApplicationContext(), "Item is added to cart successfully...", Toast.LENGTH_SHORT).show();
                                            cardView.setVisibility(View.INVISIBLE);
                                            progressBar1.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }

                                });


                            }
                        });
 
                        dltCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dltCard.setVisibility(View.GONE);
                                progressBar1.setVisibility(View.VISIBLE);
                                favRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task1) {
                                        if (task1.isComplete())
                                        {
                                            dltCard.setVisibility(View.GONE);
                                            progressBar1.setVisibility(View.GONE);
                                            Toast.makeText(MyFavoriteActivity.this, "Item deleted successfully...", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            dltCard.setVisibility(View.VISIBLE);
                                            progressBar1.setVisibility(View.GONE);
                                            Toast.makeText(MyFavoriteActivity.this, "Error Occurred : "+task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyFavViewHolder extends RecyclerView.ViewHolder {

        View mView = itemView;
        public MyFavViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setItemName(String itemName)
        {
            TextView name = (TextView) mView.findViewById(R.id.fav_item_name);
            name.setText(itemName);
        }

        public void setItemCustomizedPrice(String itemCustomizedPrice)
        {
            TextView price = (TextView) mView.findViewById(R.id.fav_item_price);
            price.setText(itemCustomizedPrice);
        }

        public void setItemImage(String itemImage, Context context)
        {
            ImageView image = (ImageView) mView.findViewById(R.id.fav_item_image);
            Picasso.with(context).load(itemImage).placeholder(R.drawable.ic_fastfood_210).into(image);
        }

        public void setItemDescription(String itemDescription)
        {
            TextView description = (TextView) mView.findViewById(R.id.fav_item_description);
            description.setText(itemDescription);
        }

    }
}