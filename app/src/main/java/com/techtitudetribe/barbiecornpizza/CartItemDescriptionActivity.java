package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CartItemDescriptionActivity extends AppCompatActivity {

    private ImageView itemImage,increase, decrease;
    private TextView itemName, itemDescription, itemPrice, itemQuantity,itemImageUrl;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference cartRef;
    private String currentUser, key,key1, price;
    private ProgressBar progressBar;
    private int quantity;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_item_description);

        key = getIntent().getStringExtra("key");
        key1 = getIntent().getStringExtra("key1");
        price = getIntent().getStringExtra("price");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("CartItems").child(currentUser).child(key1).child(key);

        LoadingBar = new ProgressDialog(this);

        decrease = (ImageView) findViewById(R.id.cart_item_decrease);
        increase = (ImageView) findViewById(R.id.cart_item_increase);

        itemImage = (ImageView) findViewById(R.id.cart_item_description_image);
        itemName = (TextView) findViewById(R.id.cart_item_description_name);
        itemDescription = (TextView) findViewById(R.id.cart_item_description_des);
        itemPrice = (TextView) findViewById(R.id.cart_item_description_price);
        itemQuantity = (TextView) findViewById(R.id.cart_item_description_quantity);
        progressBar = (ProgressBar) findViewById(R.id.cart_item_description_progress_bar);
        increase = (ImageView) findViewById(R.id.cart_item_increase);
        decrease = (ImageView) findViewById(R.id.cart_item_decrease);
        itemImageUrl = (TextView) findViewById(R.id.cart_item_description_image_url);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("itemName").getValue().toString();
                String description = snapshot.child("itemDescription").getValue().toString();
                String imageUrl = snapshot.child("itemImage").getValue().toString();
                String quantity = snapshot.child("itemQuantity").getValue().toString();
                String price = snapshot.child("itemCustomizedPrice").getValue().toString();

                itemName.setText(name);
                itemDescription.setText(description);
                itemQuantity.setText(quantity);
                itemPrice.setText(price);
                itemImageUrl.setText(imageUrl);
                Picasso.with(getApplicationContext()).load(imageUrl).placeholder(R.drawable.ic_default_food).into(itemImage);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingBar.show();
                LoadingBar.setContentView(R.layout.progress_bar);
                LoadingBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                LoadingBar.setCanceledOnTouchOutside(false);

                quantity = Integer.parseInt(itemQuantity.getText().toString());
                quantity = quantity +1;

                Map<String,Object> hashMap = new HashMap<>();
                hashMap.put("itemCustomizedPrice",String.valueOf(Integer.parseInt(price)*quantity));
                hashMap.put("itemQuantity",String.valueOf(quantity));

                cartRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {
                            itemQuantity.setText(String.valueOf(quantity));
                            itemPrice.setText(String.valueOf(Integer.parseInt(price)*quantity));
                            LoadingBar.dismiss();
                        }
                        else
                        {
                            LoadingBar.dismiss();
                            Toast.makeText(CartItemDescriptionActivity.this, "Error Occurred : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(itemQuantity.getText().toString())==1)
                {

                }
                else
                {
                    LoadingBar.show();
                    LoadingBar.setContentView(R.layout.progress_bar);
                    LoadingBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    LoadingBar.setCanceledOnTouchOutside(false);

                    quantity = Integer.parseInt(itemQuantity.getText().toString());
                    quantity = quantity - 1;

                    Map<String,Object> hashMap = new HashMap<>();
                    hashMap.put("itemCustomizedPrice",String.valueOf(Integer.parseInt(price)*quantity));
                    hashMap.put("itemQuantity",String.valueOf(quantity));

                    cartRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful())
                            {
                                itemQuantity.setText(String.valueOf(quantity));
                                itemPrice.setText(String.valueOf(Integer.parseInt(price)*quantity));
                                LoadingBar.dismiss();
                            }
                            else
                            {
                                LoadingBar.dismiss();
                                Toast.makeText(CartItemDescriptionActivity.this, "Error Occurred : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(CartItemDescriptionActivity.this,ShopCartItemActivity.class);
        intent.putExtra("key",key1);
        startActivity(intent);
        ((Activity)CartItemDescriptionActivity.this).finish();
    }
}