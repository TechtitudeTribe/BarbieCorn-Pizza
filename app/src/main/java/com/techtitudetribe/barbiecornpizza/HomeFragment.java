package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderLayout;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    private SliderLayout sliderLayout;
    private LinearLayout exploreLinearLayout;
    private CardView everydayOffers;
    private TextView shopAddress,homeChangeAddress;
    private FirebaseAuth mAuth;
    private String currentUser,location;
    private DatabaseReference userRef;
    private LinearLayout linearLayout;
    private CardView myFavButton, pizzaButton, sideOrdersButton, desertsButton, beveragesButton;
    private ImageView share;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        sliderLayout =(SliderLayout) v.findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(3); //set scroll delay in seconds :

        pizzaButton = (CardView) v.findViewById(R.id.veg_pizza_home_card);
        sideOrdersButton = (CardView) v.findViewById(R.id.side_orders_home_card);
        desertsButton = (CardView) v.findViewById(R.id.deserts_home_card);
        beveragesButton = (CardView) v.findViewById(R.id.beverages_home_card);

        share = (ImageView) v.findViewById(R.id.home_app_share_button);
        myFavButton = (CardView) v.findViewById(R.id.home_my_favourite);
        exploreLinearLayout = (LinearLayout) v.findViewById(R.id.home_explore_menu_layout);
        everydayOffers = (CardView) v.findViewById(R.id.everyday_offers);
        linearLayout = (LinearLayout) v.findViewById(R.id.home_loading_bar);
        shopAddress = (TextView) v.findViewById(R.id.home_shop_address);
        homeChangeAddress = (TextView) v.findViewById(R.id.home_change_address);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String address = snapshot.child("Address").getValue().toString();
                shopAddress.setText(address);
                linearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BarbieCorn Pizza");
                String shareMessage= "*BarbieCorn Pizza*\nThe Taste of Town\n\n\n*Get 10% off on your first order.*\n\n\nDownload this awesome application to order delicious foods...\n";
                shareMessage = shareMessage + "\n\nhttps://play.google.com/store/apps/details?id=com.techtitudetribe.barbiecornpizza"+"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            }
        });

        pizzaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyMenuActivity.class);
                intent.putExtra("shopAddress",shopAddress.getText().toString());
                intent.putExtra("category","a");
                startActivity(intent);
            }
        });

        sideOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyMenuActivity.class);
                intent.putExtra("shopAddress",shopAddress.getText().toString());
                intent.putExtra("category","b");
                startActivity(intent);
            }
        });

        desertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyMenuActivity.class);
                intent.putExtra("shopAddress",shopAddress.getText().toString());
                intent.putExtra("category","c");
                startActivity(intent);
            }
        });

        beveragesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyMenuActivity.class);
                intent.putExtra("shopAddress",shopAddress.getText().toString());
                intent.putExtra("category","d");
                startActivity(intent);
            }
        });

        everydayOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ExclusiveOffersActivity.class);
                intent.putExtra("shopAddress",shopAddress.getText().toString());
                startActivity(intent);
                //Toast.makeText(getActivity(),"No Offers details updated yet...",Toast.LENGTH_SHORT).show();
            }
        });

        homeChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                View view = LayoutInflater.from(getActivity()).inflate(
                        R.layout.change_location_layout,
                        (RelativeLayout)v.findViewById(R.id.change_location_relative_layout)
                );
                builder.setView(view);

                CardView chandpur = (CardView) view.findViewById(R.id.location_chandpur_card);
                CardView kanth = (CardView) view.findViewById(R.id.location_kanth_card);
                CardView mbd = (CardView) view.findViewById(R.id.location_moradabad_card);

                TextView chandpurText = (TextView) view.findViewById(R.id.location_chandpur);
                TextView kanthText = (TextView) view.findViewById(R.id.location_kanth);
                TextView mbdText = (TextView) view.findViewById(R.id.location_moradabad);

                chandpur.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chandpurText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                        mbdText.setTextColor(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                        kanthText.setTextColor(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                        chandpurText.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                        mbdText.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white));
                        kanthText.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white));
                        location = "Chandpur";
                    }
                });

                mbd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chandpurText.setTextColor(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                        mbdText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                        kanthText.setTextColor(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                        chandpurText.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white));
                        mbdText.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                        kanthText.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white));
                        location = "Moradabad";
                    }
                });

                kanth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chandpurText.setTextColor(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                        mbdText.setTextColor(ContextCompat.getColor(getActivity(),R.color.smoky_black));
                        kanthText.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                        chandpurText.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white));
                        mbdText.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white));
                        kanthText.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.creative_red));
                        location = "Kanth";
                    }
                });

                TextView cancel = (TextView) view.findViewById(R.id.change_location_cancel);
                TextView confirm = (TextView) view.findViewById(R.id.change_location_update);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.change_location_progress_bar);
                //userUpdateProfile.setVisibility(View.VISIBLE);
                //userUpdateProfile.startAnimation(profileOpen);

                final AlertDialog alertDialog = builder.create();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //userUpdateProfile.setVisibility(View.GONE);
                        //userUpdateProfile.startAnimation(profileClose);
                        alertDialog.dismiss();
                    }
                });
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(location))
                        {
                            Toast.makeText(v.getContext(), "Please select a city...", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            HashMap hashMap = new HashMap();
                            hashMap.put("Address",location);

                            userRef.updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error!=null)
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        String message = error.getMessage();
                                        Toast.makeText(v.getContext(),"Error Occurred : "+message,Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.GONE);

                                        Toast.makeText(v.getContext(),"Location updated successfully...",Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        });

        myFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyFavoriteActivity.class);
                startActivity(intent);
            }
        });

        setSliderViews();

        return v;
    }

    private void setSliderViews() {
        for (int i = 1; i <= 2; i++) {

            DefaultSliderView sliderView = new DefaultSliderView(getActivity());

            switch (i) {
                case 1:
                    sliderView.setImageDrawable(R.drawable.is1);
                    break;
                case 2:
                    sliderView.setImageDrawable(R.drawable.is2);
                    break;

            }

            sliderView.setImageScaleType(ImageView.ScaleType.FIT_XY);
            sliderLayout.addSliderView(sliderView);

        }
    }
}