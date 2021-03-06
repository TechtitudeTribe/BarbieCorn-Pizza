package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import java.util.HashMap;

public class ShopCartItemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference cartRef,addRef,cartDetailRef;
    private String currentUser,key1;
    private RelativeLayout noCartLayout;
    private RelativeLayout addressLayout;
    private ProgressBar addProgressBar;
    private FrameLayout addressFrame;
    private RecyclerView cartAddressList;
    private TextView proceedToCheckout,noAddress,cartItemNumbers;
    private int position = -1;
    private String name = "";
    private String address;
    private EditText customAddress;
    private String description="";
    private Boolean isOpen = false;
    private TextView customAddressButton;
    private TextView totalAmount,cartItemNames,cartItemDescription,cartToppingDescription;
    private TextView sellerId,shopName,oldItemNames;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_cart_item);

        key1 = getIntent().getStringExtra("key");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        addRef = FirebaseDatabase.getInstance().getReference().child("MyAddresses").child(currentUser);
        cartRef = FirebaseDatabase.getInstance().getReference().child("MyCart").child(currentUser).child(key1);
        cartDetailRef = FirebaseDatabase.getInstance().getReference().child("CartItems").child(currentUser).child(key1);

        noAddress = (TextView) findViewById(R.id.no_address_found);
        addressFrame = (FrameLayout) findViewById(R.id.cart_fragment_address_list_frame);
        addressLayout = (RelativeLayout) findViewById(R.id.cart_fragment_address_list_layout);
        addProgressBar = (ProgressBar) findViewById(R.id.cart_fragment_address_list_progress_bar);
        proceedToCheckout = (TextView) findViewById(R.id.proceed_to_checkout);
        noCartLayout = (RelativeLayout) findViewById(R.id.no_shop_cart_layout);
        progressBar = (ProgressBar) findViewById(R.id.cart_shop_item_progress_bar);
        cartItemNumbers = (TextView) findViewById(R.id.cart_item_numbers);
        oldItemNames = (TextView) findViewById(R.id.old_item_names);
        totalAmount = (TextView) findViewById(R.id.shop_cart_total_amount);
        cartItemNames = (TextView) findViewById(R.id.shop_cart_item_names);
        cartItemDescription = (TextView) findViewById(R.id.shop_cart_item_description);
        cartToppingDescription = (TextView) findViewById(R.id.cart_topping_description);

        customAddressButton = (TextView) findViewById(R.id.custom_address_shop_cart_button);
         customAddress = (EditText) findViewById(R.id.custom_address_shop_cart);
        sellerId = (TextView) findViewById(R.id.seller_id);
        shopName = (TextView) findViewById(R.id.shop_name);

        recyclerView = (RecyclerView) findViewById(R.id.my_shop_cart_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        cartAddressList = (RecyclerView) findViewById(R.id.cart_fragment_address_list);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setReverseLayout(false);
        linearLayoutManager1.setStackFromEnd(false);
        cartAddressList.setLayoutManager(linearLayoutManager1);

        addRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    noAddress.setVisibility(View.GONE);
                    addressFrame.setVisibility(View.VISIBLE);
                    displayCartAddressList();
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


        customAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(customAddress.getText().toString().trim()))
                {
                    Toast.makeText(ShopCartItemActivity.this, "Please fill your address details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    address = customAddress.getText().toString().trim();
                    customAddressButton.setText("Saved!");
                    proceedToCheckout.setText(getResources().getString(R.string.goForCheckout));
                }
            }
        });
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String sellerID = snapshot.child("sellerId").getValue().toString();
                    String shopNAME = snapshot.child("shopName").getValue().toString();
                    String otn = snapshot.child("itemNames").getValue().toString();

                    sellerId.setText(sellerID);
                    shopName.setText(shopNAME);
                    oldItemNames.setText(otn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cartDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    displayCartItems();
                    proceedToCheckout.setVisibility(View.VISIBLE);
                    noCartLayout.setVisibility(View.GONE);
                }
                else
                {
                    noCartLayout.setVisibility(View.VISIBLE);
                    proceedToCheckout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Animation open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_open);
        Animation close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.order_track_close);

        cartDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "";
                String description ="";
                int count = 0;
                String toppingDescription="";
                long numbers = snapshot.getChildrenCount();
                cartItemNumbers.setText(String.valueOf(numbers));
                for(DataSnapshot totalCost : snapshot.getChildren())
                {
                    int foodCalorie = Integer.parseInt(totalCost.child("itemCustomizedPrice").getValue(String.class));
                    String foodName = totalCost.child("itemName").getValue(String.class);
                    String foodDescription = totalCost.child("itemName").getValue(String.class)+"  - Qty."+totalCost.child("itemQuantity").getValue(String.class)+"\n";
                    String topping = totalCost.child("itemDescription").getValue(String.class);
                    if (!topping.equals("No modification in this item by the user..."))
                    {
                        String in = totalCost.child("itemName").getValue(String.class);
                        String id = totalCost.child("itemDescription").getValue(String.class);

                        toppingDescription = toppingDescription + "\n("+in+" Details)"+"\n"+id+"\n";
                    }
                    if (name.equals(""))
                    {
                        name = name + foodName;
                    }
                    else
                    {
                        name = name+", "+foodName;
                    }
                    count = count + foodCalorie;
                    description = description + foodDescription;
                    totalAmount.setText(String.valueOf(count));
                    cartItemNames.setText(name);
                    cartItemDescription.setText(description+toppingDescription);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        proceedToCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (proceedToCheckout.getText().toString().equals("Select an Address")||proceedToCheckout.getText().toString().equals("?????? ????????? ???????????????"))
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

                        Toast.makeText(ShopCartItemActivity.this, "Please select an address...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        cartDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String toppingDescription="";
                                int count = 0;
                                long numbers = snapshot.getChildrenCount();
                                cartItemNumbers.setText(String.valueOf(numbers));
                                for(DataSnapshot totalCost : snapshot.getChildren())
                                {
                                    int foodCalorie = Integer.parseInt(totalCost.child("itemCustomizedPrice").getValue(String.class));
                                    String foodName = totalCost.child("itemName").getValue(String.class);
                                    String foodDescription = totalCost.child("itemName").getValue(String.class)+"  - Qty."+totalCost.child("itemQuantity").getValue(String.class)+"\n";
                                    String topping = totalCost.child("itemDescription").getValue(String.class);
                                    if (!topping.equals("No modification in this item by the user..."))
                                    {
                                        String in = totalCost.child("itemName").getValue(String.class);
                                        String id = totalCost.child("itemDescription").getValue(String.class);

                                        toppingDescription = toppingDescription + "("+in+" Details)"+"\n"+id+"\n\n";
                                    }
                                    if (name.equals(""))
                                    {
                                        name = name + foodName;
                                    }
                                    else
                                    {
                                        name = name+", "+foodName;
                                    }
                                    if (description.equals(""))
                                    {
                                        description = description + foodDescription;
                                    }
                                    else
                                    {
                                        description = description+foodDescription;
                                    }
                                    count = count + foodCalorie;
                                    totalAmount.setText(String.valueOf(count));
                                    cartItemNames.setText(name);
                                    cartItemDescription.setText(description+"\n"+toppingDescription);
                                }
                                proceedToCheckout.setText(getResources().getString(R.string.selectAnAddress));
                                addressLayout.startAnimation(close);
                                addressLayout.setVisibility(View.GONE);
                                isOpen=false;
                                Intent intent = new Intent(getApplicationContext(),OrderConfirmationActivity.class);
                                intent.putExtra("itemName",cartItemNames.getText().toString());
                                intent.putExtra("totalPrice",totalAmount.getText().toString());
                                intent.putExtra("itemDescription",cartItemDescription.getText().toString());
                                intent.putExtra("address",address);
                                intent.putExtra("sellerId",sellerId.getText().toString());
                                intent.putExtra("shopName",shopName.getText().toString());
                                intent.putExtra("itemNumbers",cartItemNumbers.getText().toString());
                                intent.putExtra("key",key1);
                                startActivity(intent);
                                ((Activity)ShopCartItemActivity.this).finish();
                                cartItemNames.setText("");
                                cartItemDescription.setText("");

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }
        });

    }

    private void displayCartAddressList() {
        Query sort = addRef.orderByChild("count");
        FirebaseRecyclerAdapter<MyAddressAdapter, CartAddressViewHolder> fra =
                new FirebaseRecyclerAdapter<MyAddressAdapter, CartAddressViewHolder>(
                        MyAddressAdapter.class,
                        R.layout.cart_address_layout,
                        CartAddressViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(CartAddressViewHolder cartAddressViewHolder, MyAddressAdapter myAddressAdapter, int i) {

                        TextView textView = (TextView) cartAddressViewHolder.mView.findViewById(R.id.cart_address_layout_text);

                        cartAddressViewHolder.setAddress(myAddressAdapter.getAddress());

                        addProgressBar.setVisibility(View.GONE);

                        cartAddressViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                position = i;
                                notifyDataSetChanged();
                                address = myAddressAdapter.getAddress();
                                proceedToCheckout.setText(getResources().getString(R.string.goForCheckout));
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
        cartAddressList.setAdapter(fra);
    }

    private void displayCartItems() {
        Query sort = cartDetailRef.orderByChild("count");
        FirebaseRecyclerAdapter<CartItemAdapter, CartItemViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<CartItemAdapter, CartItemViewHolder>(
                CartItemAdapter.class,
                R.layout.cart_item_layout,
                CartItemViewHolder.class,
                sort
        ) {
            @Override
            protected void populateViewHolder(CartItemViewHolder cartItemViewHolder, CartItemAdapter cartItemAdapter, int i) {

                cartItemViewHolder.setItemDescription(cartItemAdapter.getItemDescription());
                cartItemViewHolder.setItemImage(cartItemAdapter.getItemImage(),getApplicationContext());
                cartItemViewHolder.setItemName(cartItemAdapter.getItemName());
                cartItemViewHolder.setItemQuantity(cartItemAdapter.getItemQuantity());
                cartItemViewHolder.setItemCustomizedPrice(cartItemAdapter.getItemCustomizedPrice());

                RelativeLayout relativeLayout = (RelativeLayout) cartItemViewHolder.mView.findViewById(R.id.cart_item_main_background);
                TextView textView = (TextView) cartItemViewHolder.mView.findViewById(R.id.cart_item_description);
                ImageView delete = (ImageView) cartItemViewHolder.mView.findViewById(R.id.cart_item_delete);
                String key = getRef(i).getKey();
                TextView itemQuantity = (TextView) cartItemViewHolder.mView.findViewById(R.id.cart_item_quantity);
                TextView itemPrice = (TextView) cartItemViewHolder.mView.findViewById(R.id.cart_item_price);
                CardView cardView = (CardView) cartItemViewHolder.mView.findViewById(R.id.cart_item_delete_cart_layout);
                progressBar.setVisibility(View.GONE);

                switch (i%3)
                {
                    case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        itemQuantity.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        delete.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                        break;
                    case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        itemQuantity.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        delete.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                        break;
                    case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        itemQuantity.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        delete.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                        break;
                }

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartDetailRef.child(key).removeValue();
                        if (oldItemNames.getText().toString().contains(cartItemAdapter.getItemName()+","))
                        {
                            String newItemNames = oldItemNames.getText().toString().replace(cartItemAdapter.getItemName()+",","");
                            name =  name.replace(cartItemAdapter.getItemName()+",","");
                            cartRef.child("itemNames").setValue(newItemNames);
                        }
                        else if (oldItemNames.getText().toString().contains(","+cartItemAdapter.getItemName()))
                        {
                            String newItemNames = oldItemNames.getText().toString().replace(","+cartItemAdapter.getItemName(),"");
                            name = name.replace(","+cartItemAdapter.getItemName(),"");
                            cartRef.child("itemNames").setValue(newItemNames);
                            /*Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();*/
                        }
                        else if (oldItemNames.getText().toString().contains(cartItemAdapter.getItemName()))
                        {
                            String newItemNames = oldItemNames.getText().toString().replace(cartItemAdapter.getItemName(),"");
                            name = name.replace(cartItemAdapter.getItemName(),"");
                            cartRef.removeValue();
                        }
                    }
                });

                cartItemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),CartItemDescriptionActivity.class);
                        intent.putExtra("key",key);
                        intent.putExtra("key1",key1);
                        intent.putExtra("price",cartItemAdapter.getItemPrice());
                        startActivity(intent);
                        ((Activity)ShopCartItemActivity.this).finish();
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setItemName(String itemName)
        {
            TextView name = (TextView) mView.findViewById(R.id.cart_item_name);
            name.setText(itemName);
        }

        public void setItemCustomizedPrice(String itemCustomizedPrice)
        {
            TextView price = (TextView) mView.findViewById(R.id.cart_item_price);
            price.setText(itemCustomizedPrice);
        }

        public void setItemImage(String itemImage, Context context)
        {
            ImageView image = (ImageView) mView.findViewById(R.id.cart_item_image);
            Picasso.with(context).load(itemImage).placeholder(R.drawable.ic_fastfood_210).into(image);
        }


        public void setItemDescription(String itemDescription)
        {
            TextView description = (TextView) mView.findViewById(R.id.cart_item_description);
            description.setText(itemDescription);
        }

        public void setItemQuantity(String itemQuantity)
        {
            TextView quantity = (TextView) mView.findViewById(R.id.cart_item_quantity);
            quantity.setText("Quantity : "+itemQuantity);
        }
    }

    public static class CartAddressViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public CartAddressViewHolder(@NonNull View itemView) {
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