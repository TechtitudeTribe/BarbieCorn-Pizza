package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference cartRef;
    private String currentUser;
    private LinearLayout cartFragmentLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView exploreMenu;
    private DatabaseReference userRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cart_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        cartRef = FirebaseDatabase.getInstance().getReference().child("MyCart").child(currentUser);
        progressBar = (ProgressBar) v.findViewById(R.id.my_cart_progress_bar);

        //exploreMenu = (TextView) v.findViewById(R.id.cart_explore_menu);
        cartFragmentLayout = (LinearLayout) v.findViewById(R.id.cart_fragment_explore_menu_layout);

        recyclerView = (RecyclerView) v.findViewById(R.id.my_cart_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren())
                {
                    displayCartItems();
                    cartFragmentLayout.setVisibility(View.GONE);
                }
                else
                {
                    cartFragmentLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cartFragmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopAddress = snapshot.child("Address").getValue().toString();
;                        Intent intent = new Intent(getActivity(),MyMenuActivity.class);
                        intent.putExtra("shopAddress",shopAddress);
                        intent.putExtra("category","a");
                        intent.putExtra("categoryName","VegPizza");
                        startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        return v;
    }

    private void displayCartItems() {
        Query sort = cartRef.orderByChild("count");
        FirebaseRecyclerAdapter<CartShopItemAdapter,CartShopItemViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<CartShopItemAdapter, CartShopItemViewHolder>(
                CartShopItemAdapter.class,
                R.layout.shop_cart_item_layout,
                CartShopItemViewHolder.class,
                sort
        ) {
            @Override
            protected void populateViewHolder(CartShopItemViewHolder cartShopItemViewHolder, CartShopItemAdapter cartShopItemAdapter, int i) {

                String key = getRef(i).getKey();
                LinearLayout linearLayout = (LinearLayout) cartShopItemViewHolder.mView.findViewById(R.id.shop_cart_item_background);
                TextView textView = (TextView) cartShopItemViewHolder.mView.findViewById(R.id.shop_cart_item_click_here);

                switch (i%4)
                {
                    case 0 : linearLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_green));
                        textView.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_green));
                        break;
                    case 1 : linearLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                        textView.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                        break;
                    case 2 : linearLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                        textView.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_sky_blue));
                        break;
                    case 3 : linearLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                        textView.setTextColor(ContextCompat.getColor(getActivity(),R.color.creative_violet));
                        break;
                }

                cartShopItemViewHolder.setItemNames(cartShopItemAdapter.getItemNames());
                cartShopItemViewHolder.setShopName(cartShopItemAdapter.getShopName());
                progressBar.setVisibility(View.GONE);

                cartShopItemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(),ShopCartItemActivity.class);
                        intent.putExtra("key",key);
                        intent.putExtra("itemNames",cartShopItemAdapter.getItemNames());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CartShopItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public CartShopItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setItemNames(String itemNames)
        {
            TextView textView = (TextView) mView.findViewById(R.id.shop_cart_items_name);
            textView.setText(itemNames);
        }

        public void setShopName(String shopName)
        {
            TextView textView = (TextView) mView.findViewById(R.id.shop_cart_item_shop_name);
            textView.setText(shopName);
        }
    }

}