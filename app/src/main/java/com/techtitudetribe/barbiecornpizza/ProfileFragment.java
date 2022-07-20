package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.VolumeShaper;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import java.util.HashMap;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView userName, userEmail, userNumber, userLogo;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private String currentUser;
    private RelativeLayout myOrderButton,myAddressButton,logOutButton,changeLanguageButton;
    private RelativeLayout userUpdateProfile;
    private ImageView editUserProfile;
    private String languageString;
    private ProfileAdapter profileAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadLocale();
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);

        userName = (TextView) v.findViewById(R.id.profile_name);
        userEmail = (TextView) v.findViewById(R.id.profile_email);
        userNumber = (TextView) v.findViewById(R.id.profile_number);
        userLogo = (TextView) v.findViewById(R.id.profile_logo);
        myOrderButton = (RelativeLayout) v.findViewById(R.id.my_orders_button);
        myAddressButton = (RelativeLayout) v.findViewById(R.id.my_addresses_button);
        userUpdateProfile = (RelativeLayout) v.findViewById(R.id.update_profile_relative_layout);
        editUserProfile = (ImageView) v.findViewById(R.id.edit_user_profile);
        logOutButton = (RelativeLayout) v.findViewById(R.id.log_out_button);
        changeLanguageButton = (RelativeLayout) v.findViewById(R.id.change_language_button);

        profileAdapter = new ProfileAdapter();

        editUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                View view = LayoutInflater.from(getActivity()).inflate(
                        R.layout.edit_profile_layout,
                        (RelativeLayout)v.findViewById(R.id.update_profile_relative_layout)
                );
                builder.setView(view);

                TextView cancelProfileUpdate = (TextView) view.findViewById(R.id.update_user_profile_cancel);
                EditText editUsername = (EditText) view.findViewById(R.id.edit_username);
                EditText editEmail = (EditText) view.findViewById(R.id.edit_email_id);
                EditText editContact = (EditText) view.findViewById(R.id.edit_contact_number);
                TextView confirmProfileUpdate = (TextView) view.findViewById(R.id.update_user_profile_update);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.edit_user_profile_progress_bar);
                //userUpdateProfile.setVisibility(View.VISIBLE);
                //userUpdateProfile.startAnimation(profileOpen);

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            String name = snapshot.child("Name").getValue().toString();
                            String mail = snapshot.child("Email").getValue().toString();
                            String contact = snapshot.child("ContactNumber").getValue().toString();
                            editUsername.setText(name);
                            editEmail.setText(mail);
                            editContact.setText(contact);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final AlertDialog alertDialog = builder.create();

                cancelProfileUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //userUpdateProfile.setVisibility(View.GONE);
                        //userUpdateProfile.startAnimation(profileClose);
                        alertDialog.dismiss();
                    }
                });
                confirmProfileUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(editUsername.getText().toString()))
                        {
                            Toast.makeText(v.getContext(), "Username is invalid", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else if (TextUtils.isEmpty(editEmail.getText().toString()))
                        {
                            Toast.makeText(v.getContext(), "E-mail is invalid", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }else {

                            HashMap hashMap = new HashMap();
                            hashMap.put("Email",editEmail.getText().toString());
                            hashMap.put("Name",editUsername.getText().toString());
                            hashMap.put("ContactNumber",editContact.getText().toString());

                            userRef.updateChildren(hashMap);

                            progressBar.setVisibility(View.GONE);
                            alertDialog.dismiss();
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

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                View view = LayoutInflater.from(getActivity()).inflate(
                        R.layout.log_out_layout,
                        (RelativeLayout)v.findViewById(R.id.log_out_relative_layout)
                );
                builder.setView(view);

                TextView cancelLogOut = (TextView) view.findViewById(R.id.cancel_log_out);
                TextView confirmLogOut = (TextView) view.findViewById(R.id.confirm_log_out);

                final AlertDialog alertDialog = builder.create();

                cancelLogOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //userUpdateProfile.setVisibility(View.GONE);
                        //userUpdateProfile.startAnimation(profileClose);
                        alertDialog.dismiss();
                    }
                });
                confirmLogOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut();
                        Intent intent = new Intent(getActivity(),LocationActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        });

        changeLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
                View view = LayoutInflater.from(getActivity()).inflate(
                        R.layout.change_language_layout,
                        (RelativeLayout)v.findViewById(R.id.change_language_relative_layout)
                );
                builder.setView(view);

                CardView hindiCard = (CardView) view.findViewById(R.id.choose_language_hindi_card);
                CardView englishCard = (CardView) view.findViewById(R.id.choose_language_english_card);
                TextView hindi = (TextView) view.findViewById(R.id.choose_language_hindi);
                TextView english = (TextView) view.findViewById(R.id.choose_language_english);
                TextView cancelLang = (TextView) view.findViewById(R.id.cancel_change_language);
                TextView confirmLang = (TextView) view.findViewById(R.id.confirm_change_language);

                hindiCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hindi.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                        hindi.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.title_background));
                        english.setTextColor(ContextCompat.getColor(getActivity(),R.color.title_background));
                        english.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white));
                        languageString = "hi";
                    }
                });

                englishCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hindi.setTextColor(ContextCompat.getColor(getActivity(),R.color.title_background));
                        hindi.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.white));
                        english.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                        english.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.title_background));
                        languageString = "en";
                    }
                });

                final AlertDialog alertDialog = builder.create();

                cancelLang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //userUpdateProfile.setVisibility(View.GONE);
                        //userUpdateProfile.startAnimation(profileClose);
                        alertDialog.dismiss();
                    }
                });
                confirmLang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Locale locale = new Locale(languageString);
                        Locale.setDefault(locale);
                        Configuration configuration = new Configuration();
                        configuration.locale = locale;
                        getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings",Context.MODE_PRIVATE).edit();
                        editor.putString("My_Lang",languageString);
                        editor.apply();
                        alertDialog.dismiss();
                        loadLocale();
                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String name = snapshot.child("Name").getValue().toString();
                    String number = snapshot.child("ContactNumber").getValue().toString();
                    String mail = snapshot.child("Email").getValue().toString();
                    String address = snapshot.child("Address").getValue().toString();
                    char logo = name.charAt(0);
                    String logoString = String.valueOf(logo);

                    userName.setText(name);
                    userEmail.setText(mail);
                    userNumber.setText("+91 "+number);
                    userLogo.setText(logoString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyOrderActivity.class);
                startActivity(intent);
            }
        });

        myAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyAddressActivity.class);
                startActivity(intent);
            }
        });


        return v;
    }


    public void loadLocale()
    {
        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings",Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang",language);
        editor.apply();
    }


}