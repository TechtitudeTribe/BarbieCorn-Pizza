package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.multidex.MultiDex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
public class OrderConfirmationActivity extends AppCompatActivity {

    private TextView totalPrice;
    private TextView itemNames;
    private TextView proceedToPay;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference cartRef,cartDetailRef, orderRef,userRef,adminRef,offerRef,contactRef, couponRef;
    private String currentUser,address,key, actualPriceAmount;
    private long orderItems=0,adminItems=0;
    private TextView orderConfirmationDate,orderConfirmationNumbers;
    private RadioGroup paymentMethod, orderType;
    private String paymentMethodString="Pay via UPI", orderTypeString,sellerNumber;
    private TextView placeOrder;
    private String TAG ="main";
    private final int UPI_PAYMENT = 0;
    private TextView orderConfirmationDescription,sellerId,shopName,userName,userContact,deliveryCharge,actualPrice,orderShopUpi;
    private ProgressBar placeProgressBar;
    private String sellerIdGlobal,isApplied="";
    private TextView applyFirstOffer, cancelFirstOrder;
    private RelativeLayout firstOfferLayout;
    private double orderCondition=200;
    private RelativeLayout distanceCondition;
    private TextView cancelDistanceCondition, shopNumber, couponCodeApply;
    private MyOrderAdapter myOrderAdapter;
    private OrderShopAdapter orderShopAdapter;
    private EditText couponCodeText;
    private LinearLayout couponCodeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        sellerIdGlobal = getIntent().getStringExtra("sellerId");
        actualPriceAmount = getIntent().getStringExtra("totalPrice");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("MyCart").child(currentUser);
        cartDetailRef = FirebaseDatabase.getInstance().getReference().child("CartItems").child(currentUser);
        orderRef = FirebaseDatabase.getInstance().getReference().child("MyOrders").child(currentUser);
        contactRef = FirebaseDatabase.getInstance().getReference().child("NewShops");
        couponRef = FirebaseDatabase.getInstance().getReference().child("GiftVrouchers");
        adminRef = FirebaseDatabase.getInstance().getReference().child("MySellerOrders").child(sellerIdGlobal);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);

        myOrderAdapter = new MyOrderAdapter();
        orderShopAdapter = new OrderShopAdapter();

        firstOfferLayout = (RelativeLayout) findViewById(R.id.first_order_offer_main);
        applyFirstOffer = (TextView) findViewById(R.id.apply_first_order_offer);
        cancelFirstOrder = (TextView) findViewById(R.id.close_first_order_offer);
        couponCodeApply = (TextView) findViewById(R.id.apply_coupon_code);
        couponCodeText = (EditText) findViewById(R.id.coupon_code_text);
        couponCodeLayout = (LinearLayout) findViewById(R.id.coupon_code_layout);

        shopNumber = (TextView) findViewById(R.id.seller_number_order_confirmation);
        distanceCondition = (RelativeLayout) findViewById(R.id.distance_condition_card);
        cancelDistanceCondition = (TextView) findViewById(R.id.close_distance_condition_offer);
        totalPrice = (TextView) findViewById(R.id.order_total_price);
        itemNames = (TextView) findViewById(R.id.order_confirmation_item_names);
        proceedToPay = (TextView) findViewById(R.id.order_confirmation_proceed_to_pay);
        orderConfirmationDate = (TextView) findViewById(R.id.order_confirmation_date);
        orderConfirmationNumbers = (TextView) findViewById(R.id.total_no_of_orders);
        placeOrder = (TextView) findViewById(R.id.order_confirmation_place_order);
        orderConfirmationDescription = (TextView) findViewById(R.id.order_confirmation_item_description);
        placeProgressBar = (ProgressBar) findViewById(R.id.place_order_progress_bar);
        deliveryCharge = (TextView) findViewById(R.id.delivery_charge);
        actualPrice = (TextView) findViewById(R.id.order_confirmation_price);
        orderShopUpi = (TextView) findViewById(R.id.order_shop_upi);
        sellerId = (TextView) findViewById(R.id.order_seller_id);
        shopName = (TextView) findViewById(R.id.order_shop_name);
        userName = (TextView) findViewById(R.id.order_user_name);
        userContact = (TextView) findViewById(R.id.order_user_number);

        paymentMethod = (RadioGroup) findViewById(R.id.payment_method);
        orderType = (RadioGroup) findViewById(R.id.order_type);

        actualPrice.setText(getIntent().getExtras().getString("totalPrice"));
        itemNames.setText(getIntent().getExtras().getString("itemName"));
        orderConfirmationDescription.setText(getIntent().getExtras().getString("itemDescription"));
        address = getIntent().getStringExtra("address");
        sellerId.setText(getIntent().getStringExtra("sellerId"));
        shopName.setText(getIntent().getStringExtra("shopName"));
        key = getIntent().getStringExtra("key");

        couponRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {

                    String code = snapshot.child("title").getValue().toString();
                    String discount = snapshot.child("discount").getValue().toString();
                    String statusCoupon = snapshot.child("status").getValue().toString();
                    String minCoupon = snapshot.child("minimumAmount").getValue().toString();

                    if (statusCoupon.equals("active"))
                    {
                        couponCodeLayout.setVisibility(View.VISIBLE);
                        couponCodeApply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!couponCodeApply.getText().toString().equals("Applied"))
                                {
                                    if(TextUtils.isEmpty(couponCodeText.getText().toString().trim()))
                                    {
                                        Toast.makeText(OrderConfirmationActivity.this, "Please enter a valid Coupon Code...", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        String codeUser = couponCodeText.getText().toString().trim();

                                        if(Double.parseDouble(actualPriceAmount) < Double.parseDouble(minCoupon))
                                        {
                                            Toast.makeText(OrderConfirmationActivity.this, "Minimum order should be of Rs. "+minCoupon+" ...", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            if (code.toUpperCase().equals(codeUser.toUpperCase()))
                                            {
                                                double discountMain = Double.parseDouble(actualPrice.getText().toString()) * Integer.parseInt(discount)/100;
                                                double finalAmount = Double.parseDouble(actualPrice.getText().toString()) - discountMain;
                                                actualPrice.setText(String.valueOf(finalAmount));
                                                totalPrice.setText(String.valueOf(finalAmount+Integer.parseInt(deliveryCharge.getText().toString())));
                                                couponCodeApply.setText("Applied");
                                            }
                                            else
                                            {
                                                Toast.makeText(OrderConfirmationActivity.this, "This Coupon Code had been expired or You have entered the wrong one.\nTry Again...", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }

                            }
                        });
                    }
                    else
                    {
                        couponCodeLayout.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        contactRef.child(getIntent().getStringExtra("shopName")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    shopNumber.setText(snapshot.child("userContact").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cancelDistanceCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                distanceCondition.setVisibility(View.GONE);
            }
        });

        offerRef = FirebaseDatabase.getInstance().getReference().child("NewShops").child(getIntent().getStringExtra("shopName"));
        SimpleDateFormat sdfOffer = new SimpleDateFormat("Hmm", Locale.getDefault());
        String currentDateandTimeCondirion = sdfOffer.format(new Date());
            offerRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        String os = snapshot.child("specialTimeOffers").getValue().toString();
                        if (currentDateandTimeCondirion.equals("1343"))
                        {
                            if (os.equals("active"))
                            {
                                double discount = Integer.parseInt(actualPrice.getText().toString()) * 0.2;
                                double finalAmount = Integer.parseInt(actualPrice.getText().toString()) - discount;
                                actualPrice.setText(String.valueOf(finalAmount));
                                totalPrice.setText(String.valueOf(finalAmount+Integer.parseInt(deliveryCharge.getText().toString())));
                                Toast.makeText(OrderConfirmationActivity.this, "Offer Applied : Special Time Offer", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        paymentMethod.clearCheck();
        orderType.clearCheck();

        cartRef.child(shopName.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String upi = snapshot.child("upi").getValue().toString();
                    orderShopUpi.setText(upi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cartRef.child(shopName.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("deliveryCharge"))
                {
                    String dc = snapshot.child("deliveryCharge").getValue().toString();

                    if (Double.parseDouble(actualPrice.getText().toString())>=350)
                    {
                        deliveryCharge.setText("00");
                    }
                    else
                    {
                        deliveryCharge.setText(dc);
                    }
                    totalPrice.setText(String.valueOf(Double.parseDouble(actualPrice.getText().toString())+Integer.parseInt(deliveryCharge.getText().toString())));
                }
                else
                {
                    deliveryCharge.setText("00");
                    totalPrice.setText(String.valueOf(Double.parseDouble(actualPrice.getText().toString())+Integer.parseInt(deliveryCharge.getText().toString())));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        orderConfirmationNumbers.setText(getResources().getString(R.string.totalItems)+getIntent().getExtras().getString("itemNumbers"));

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("FirstOrder"))
                {
                    firstOfferLayout.setVisibility(View.GONE);
                }
                else
                {
                    firstOfferLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());

        paymentMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                paymentMethodString = radioButton.getText().toString();
                if (paymentMethodString.equals("Pay via UPI"))
                {
                    proceedToPay.setVisibility(View.VISIBLE);
                    placeOrder.setVisibility(View.GONE);
                }
                else
                {
                    proceedToPay.setVisibility(View.GONE);
                    placeOrder.setVisibility(View.VISIBLE);
                }
            }
        });

        orderType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                orderTypeString = radioButton.getText().toString();
                if(orderTypeString.equals("Dine In")||orderTypeString.equals("Takeaway"))
                {
                    deliveryCharge.setText("00");
                    totalPrice.setText(actualPrice.getText().toString());
                    orderCondition = 0;
                    distanceCondition.setVisibility(View.GONE);
                }
                else
                {
                    orderCondition = 200;
                    cartRef.child(shopName.getText().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild("deliveryCharge"))
                            {
                                String dc = snapshot.child("deliveryCharge").getValue().toString();
                                if (Double.parseDouble(actualPrice.getText().toString())>=350)
                                {
                                    deliveryCharge.setText("00");
                                }
                                else
                                {
                                    deliveryCharge.setText(dc);
                                }
                                totalPrice.setText(String.valueOf(Double.parseDouble(actualPrice.getText().toString())+Integer.parseInt(deliveryCharge.getText().toString())));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    distanceCondition.setVisibility(View.VISIBLE);
                }
            }
        });

        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    adminItems = snapshot.getChildrenCount();
                }
                else
                {
                    adminItems=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("Name").getValue().toString();
                String contact = snapshot.child("ContactNumber").getValue().toString();

                userName.setText(name);
                userContact.setText(contact);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        orderConfirmationDate.setText(getResources().getString(R.string.orderWillBePlaced)+"\n"+currentDate);

        cancelFirstOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstOfferLayout.setVisibility(View.GONE);
            }
        });

        applyFirstOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!applyFirstOffer.getText().toString().equals("Applied"))
                {
                    double discount = Integer.parseInt(actualPrice.getText().toString()) * 0.25;
                    double finalAmount = Integer.parseInt(actualPrice.getText().toString()) - discount;
                    actualPrice.setText(String.valueOf(finalAmount));
                    totalPrice.setText(String.valueOf(finalAmount+Integer.parseInt(deliveryCharge.getText().toString())));
                    applyFirstOffer.setText("Applied");
                    isApplied = "Yes";
                }
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMd", Locale.getDefault());
        String currentDateAdmin = sdf.format(new Date());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    orderItems = snapshot.getChildrenCount();
                }
                else
                {
                    orderItems=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sellerNumber = shopNumber.getText().toString();
                if (Double.parseDouble(totalPrice.getText().toString())>=orderCondition)
                {
                    checkValidations(currentDate);
                }
                else
                {
                    Toast.makeText(OrderConfirmationActivity.this, "Minimum Order is 200.\nAdd more items...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        proceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellerNumber = shopNumber.getText().toString();
                if (TextUtils.isEmpty(paymentMethodString))
                {
                    Toast.makeText(getApplicationContext(), "Select any Payment Method...", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(orderTypeString))
                {
                    Toast.makeText(getApplicationContext(), "Select any Order Type...", Toast.LENGTH_SHORT).show();
                }
                else if (Integer.parseInt(totalPrice.getText().toString())<200)
                {
                    Toast.makeText(OrderConfirmationActivity.this, "Minimum Order is 200.\nAdd more items...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //6397213673@ybl
                    payUsingUpi("BarbieCorn Pizza", orderShopUpi.getText().toString(),
                            "Food Items", totalPrice.getText().toString());

                }
            }
        });

        //updateToken();

    }

    /*private void updateToken() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }*/

    private void payUsingUpi(String name, String upiId, String note, String amount) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(OrderConfirmationActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkValidations(String currentDate) {
        if (TextUtils.isEmpty(paymentMethodString))
        {
            Toast.makeText(this, "Select any Payment Method...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(orderTypeString))
        {
            Toast.makeText(this, "Select any Order Type...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            placeProgressBar.setVisibility(View.VISIBLE);
            placeOrder.setVisibility(View.GONE);
            proceedToPay.setVisibility(View.GONE);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
            String currentTime = simpleDateFormat.format(Calendar.getInstance().getTime());

            if (isApplied.equals("Yes"))
            {
                userRef.child("FirstOrder").setValue("Yes");
            }

            myOrderAdapter.setCount(orderItems+1);
            myOrderAdapter.setAddress(address);
            myOrderAdapter.setOrderId(currentDateandTime);
            myOrderAdapter.setItemDescription(orderConfirmationDescription.getText().toString());
            myOrderAdapter.setItemNames(itemNames.getText().toString());
            myOrderAdapter.setItemNumber(getIntent().getExtras().getString("itemNumbers"));
            myOrderAdapter.setItemPlacedDate(currentDate+" at "+currentTime);
            myOrderAdapter.setItemStatus("Ordered");
            myOrderAdapter.setItemTotalAmount(totalPrice.getText().toString());
            myOrderAdapter.setOrderType(orderTypeString);
            myOrderAdapter.setPaymentMethod(paymentMethodString);
            myOrderAdapter.setUserId(currentUser);

            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orderRef.child("MyOrder"+currentDateandTime).setValue(myOrderAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            orderRef.child("MyOrder"+currentDateandTime).addListenerForSingleValueEvent(listener);
            userRef.child("recentOrder").setValue("MyOrder"+currentDateandTime);
            sendUserDetailsToAdmin(currentDate,currentDateandTime,currentTime);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(OrderConfirmationActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(OrderConfirmationActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMd", Locale.getDefault());
                String currentDateAdmin = sdf1.format(new Date());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                String currentTime = simpleDateFormat.format(Calendar.getInstance().getTime());

                if (isApplied.equals("Yes"))
                {
                    HashMap hashMap3 = new HashMap();
                    hashMap3.put("FirstOrder","Yes");

                    userRef.updateChildren(hashMap3);
                }


                myOrderAdapter.setCount(orderItems+1);
                myOrderAdapter.setAddress(address);
                myOrderAdapter.setOrderId(currentDateandTime);
                myOrderAdapter.setItemDescription(orderConfirmationDescription.getText().toString());
                myOrderAdapter.setItemNames(itemNames.getText().toString());
                myOrderAdapter.setItemNumber(getIntent().getExtras().getString("itemNumbers"));
                myOrderAdapter.setItemPlacedDate(currentDate+" at "+currentTime);
                myOrderAdapter.setItemStatus("Ordered");
                myOrderAdapter.setItemTotalAmount(totalPrice.getText().toString());
                myOrderAdapter.setOrderType(orderTypeString);
                myOrderAdapter.setPaymentMethod(paymentMethodString);
                myOrderAdapter.setUserId(currentUser);

                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderRef.child("MyOrder"+currentDateandTime).setValue(myOrderAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                orderRef.child("MyOrder"+currentDateandTime).addListenerForSingleValueEvent(listener);
                userRef.child("recentOrder").setValue("MyOrder"+currentDateandTime);
                sendUserDetailsToAdminPay(currentDate,currentDateandTime,currentTime);
                Log.e("UPI", "payment successfull: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(OrderConfirmationActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
                Toast.makeText(OrderConfirmationActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(OrderConfirmationActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean isConnectionAvailable(OrderConfirmationActivity context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    private void sendUserDetailsToAdmin(String currentDate, String currentDateandTime, String currentTime)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMd", Locale.getDefault());
        String currentDateAdmin = sdf.format(new Date());


        orderShopAdapter.setCount(orderItems+1);
        orderShopAdapter.setAddress(address);
        orderShopAdapter.setOrderId(currentDateandTime);
        orderShopAdapter.setItemDescription(orderConfirmationDescription.getText().toString());
        orderShopAdapter.setItemNames(itemNames.getText().toString());
        orderShopAdapter.setItemPlacedDate(currentDate+" at "+currentTime);
        orderShopAdapter.setItemStatus("Ordered");
        orderShopAdapter.setItemTotalAmount(totalPrice.getText().toString());
        orderShopAdapter.setOrderType(orderTypeString);
        orderShopAdapter.setPaymentMethod(paymentMethodString);
        orderShopAdapter.setUserId(currentUser);
        orderShopAdapter.setDeliveryCharge(deliveryCharge.getText().toString());
        orderShopAdapter.setTitleName("MyOrder"+currentDateandTime);
        orderShopAdapter.setCustomerName(userName.getText().toString());
        orderShopAdapter.setCustomerNumber(userContact.getText().toString());
        orderShopAdapter.setShopName(shopName.getText().toString());

        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminRef.child(currentDateAdmin).child(currentUser+currentDateandTime).setValue(orderShopAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };



        adminRef.child(currentDateAdmin).child(currentUser+currentDateandTime).addListenerForSingleValueEvent(valueEventListener1);
        Toast.makeText(OrderConfirmationActivity.this, "Your Order is placed successfully...", Toast.LENGTH_SHORT).show();
        proceedToPay.setVisibility(View.GONE);
        placeOrder.setVisibility(View.GONE);
        placeProgressBar.setVisibility(View.GONE);
        cartRef.child(shopName.getText().toString()).removeValue();
        cartDetailRef.child(shopName.getText().toString()).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Tokens").child(sellerId.getText().toString()).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usertoken=dataSnapshot.getValue(String.class);

                FcmNotificationSender notificationSender = new FcmNotificationSender(usertoken,"Alert","New Order is placed...",getApplicationContext(),OrderConfirmationActivity.this);
                notificationSender.SendNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendUserToMainActivity();
    }

    private void sendUserDetailsToAdminPay(String currentDate, String currentDateandTime, String currentTime)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMd", Locale.getDefault());
        String currentDateAdmin = sdf.format(new Date());

        orderShopAdapter.setCount(orderItems+1);
        orderShopAdapter.setAddress(address);
        orderShopAdapter.setOrderId(currentDateandTime);
        orderShopAdapter.setItemDescription(orderConfirmationDescription.getText().toString());
        orderShopAdapter.setItemNames(itemNames.getText().toString());
        orderShopAdapter.setItemPlacedDate(currentDate+" at "+currentTime);
        orderShopAdapter.setItemStatus("Ordered");
        orderShopAdapter.setItemTotalAmount(totalPrice.getText().toString());
        orderShopAdapter.setOrderType(orderTypeString);
        orderShopAdapter.setPaymentMethod(paymentMethodString);
        orderShopAdapter.setUserId(currentUser);
        orderShopAdapter.setDeliveryCharge(deliveryCharge.getText().toString());
        orderShopAdapter.setTitleName("MyOrder"+currentDateandTime);
        orderShopAdapter.setCustomerName(userName.getText().toString());
        orderShopAdapter.setCustomerNumber(userContact.getText().toString());
        orderShopAdapter.setShopName(shopName.getText().toString());

        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminRef.child(currentDateAdmin).child(currentUser+currentDateandTime).setValue(orderShopAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };



        adminRef.child(currentDateAdmin).child(currentUser+currentDateandTime).addListenerForSingleValueEvent(valueEventListener1);
        Toast.makeText(OrderConfirmationActivity.this, "Your Order is placed successfully...", Toast.LENGTH_SHORT).show();
        proceedToPay.setVisibility(View.GONE);
        placeOrder.setVisibility(View.GONE);
        placeProgressBar.setVisibility(View.GONE);
        cartRef.child(shopName.getText().toString()).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Tokens").child(sellerId.getText().toString()).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usertoken=dataSnapshot.getValue(String.class);

                FcmNotificationSender notificationSender = new FcmNotificationSender(usertoken,"Alert","New Order is placed...",getApplicationContext(),OrderConfirmationActivity.this);
                notificationSender.SendNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //SmsManager.getDefault().sendTextMessage(sellerNumber, "BBC", "New Order is placed...", null,null);
        sendUserToMainActivity();
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(OrderConfirmationActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    /*public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(OrderConfirmationActivity.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }*/



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OrderConfirmationActivity.this,ShopCartItemActivity.class);
        intent.putExtra("key",key);
        startActivity(intent);
        ((Activity)OrderConfirmationActivity.this).finish();
    }


}