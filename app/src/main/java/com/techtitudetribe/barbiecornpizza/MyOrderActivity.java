package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyOrderActivity extends AppCompatActivity {

    private DatabaseReference orderRef;
    private FirebaseAuth firebaseAuth;
    private String currentUser;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout noOrderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        orderRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("MyOrders");

        progressBar = (ProgressBar) findViewById(R.id.my_orders_activity_progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.my_order_list_view);
        noOrderLayout = (LinearLayout) findViewById(R.id.no_orders_layout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    noOrderLayout.setVisibility(View.GONE);
                    displayOrderList();
                }
                else
                {
                    noOrderLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //displayOrderList();
    }

    private void displayOrderList() {
        Query sort = orderRef.orderByChild("count");
        FirebaseRecyclerAdapter<MyOrderAdapter,MyOrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MyOrderAdapter, MyOrderViewHolder>(
                        MyOrderAdapter.class,
                        R.layout.my_order_item_layout,
                        MyOrderViewHolder.class,
                        sort
                ) {
                    @Override
                    protected void populateViewHolder(MyOrderViewHolder myOrderViewHolder, MyOrderAdapter myOrderAdapter, int i) {

                        RelativeLayout relativeLayout = (RelativeLayout) myOrderViewHolder.mView.findViewById(R.id.my_order_item_relative_layout);
                        TextView textView = (TextView) myOrderViewHolder.mView.findViewById(R.id.my_order_item_order_number);
                        String key = getRef(i).getKey();

                        switch (i%4)
                        {
                            case 0 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_green));
                                break;
                            case 1 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_orange));
                                break;
                            case 2 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_sky_blue));
                                break;
                            case 3 : relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.creative_violet));
                                break;
                        }

                        myOrderViewHolder.setItemNames(myOrderAdapter.getItemNames());
                        myOrderViewHolder.setItemNumber(myOrderAdapter.getItemNumber());
                        myOrderViewHolder.setItemTotalAmount(myOrderAdapter.getItemTotalAmount());
                        myOrderViewHolder.setItemPlacedDate(myOrderAdapter.getItemPlacedDate());
                        myOrderViewHolder.setCount(myOrderAdapter.getCount());
                        progressBar.setVisibility(View.GONE);

                        myOrderViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MyOrderActivity.this,MyOrderDetailsActivity.class);
                                intent.putExtra("key",key);
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyOrderViewHolder extends RecyclerView.ViewHolder{

        View mView ;
        public MyOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setItemNames(String itemNames)
        {
            TextView name = (TextView) mView.findViewById(R.id.my_order_item_name);
            name.setText(itemNames);
        }

        public void setItemNumber(String itemNumber)
        {
            TextView number = (TextView) mView.findViewById(R.id.my_order_item_numbers);
            number.setText(itemNumber+" items");
        }

        public void setItemPlacedDate(String itemPlacedDate)
        {
            TextView date = (TextView) mView.findViewById(R.id.my_order_item_placed_date);
            date.setText("Order Placed on "+itemPlacedDate);
        }

        public void setItemTotalAmount(String itemTotalAmount)
        {
            TextView amount = (TextView) mView.findViewById(R.id.my_order_item_total_amount);
            amount.setText("for Amount Rs."+itemTotalAmount);
        }

        public void setCount(long count)
        {
            TextView orderNumber = (TextView) mView.findViewById(R.id.my_order_item_order_number);
            orderNumber.setText(String.valueOf(count));
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}