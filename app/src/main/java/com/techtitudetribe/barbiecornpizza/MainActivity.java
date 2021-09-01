package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MeowBottomNavigation meo;
    private final static int ID_PROFILE=1;
    private final static int ID_MENU=2;
    private final static int ID_HOME=3;
    private final static int ID_CART=4;
    private final static int ID_ABOUT=5;
    private long backPressedTime;
    private FirebaseAuth mAuth;
    private int requestCode = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        meo=(MeowBottomNavigation) findViewById(R.id.bottom_nav);
        meo.add(new MeowBottomNavigation.Model(ID_PROFILE, R.drawable.icon_profile));
        meo.add(new MeowBottomNavigation.Model(ID_MENU, R.drawable.icon_support));
        meo.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.icon_home));
        meo.add(new MeowBottomNavigation.Model(ID_CART, R.drawable.icon_cart));
        meo.add(new MeowBottomNavigation.Model(ID_ABOUT, R.drawable.icon_about));

        if(savedInstanceState == null) {
            HomeFragment fragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
        }

        FirebaseMessaging.getInstance().subscribeToTopic("user")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Done";
                        if (!task.isSuccessful()) {
                            msg = "Fail";
                        }
                    }
                });

        meo.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                Fragment select_fragment = null;
                switch (item.getId())
                {
                    case ID_PROFILE : select_fragment = new ProfileFragment();
                        break;
                    case ID_MENU : select_fragment = new ContactFragment();
                        break;
                    case ID_HOME : select_fragment = new HomeFragment();
                        break;
                    case ID_CART: select_fragment = new CartFragment();
                        break;
                    case ID_ABOUT : select_fragment = new AboutUsFragment();
                        break;
                    /*case ID_SETTING : select_fragment = new SettingsFragment();
                        break;*/
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,select_fragment).commit();
            }
        });

        meo.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment select_fragment = null;
                switch (item.getId())
                {
                    case ID_PROFILE : select_fragment = new ProfileFragment();
                        break;
                    case ID_MENU : select_fragment = new ContactFragment();
                        break;
                    case ID_HOME : select_fragment = new HomeFragment();
                        break;
                    case ID_CART: select_fragment = new CartFragment();
                        break;
                    case ID_ABOUT : select_fragment = new AboutUsFragment();
                        break;
                    /*case ID_SETTING : select_fragment = new SettingsFragment();
                        break;*/
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,select_fragment).commit();
            }
        });

        meo.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                Fragment select_fragment = null;
                switch (item.getId())
                {

                    case ID_PROFILE : select_fragment = new ProfileFragment();
                        break;
                    case ID_MENU : select_fragment = new ContactFragment();
                        break;
                    case ID_HOME : select_fragment = new HomeFragment();
                        break;
                    case ID_CART: select_fragment = new CartFragment();
                        break;
                    case ID_ABOUT : select_fragment = new AboutUsFragment();
                        break;
                    /*case ID_SETTING : select_fragment = new SettingsFragment();
                        break;*/
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,select_fragment).commit();
            }
        });
        meo.show(ID_HOME,true);

        /*if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, requestCode);

        }*/



        updateToken();
    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 44 : {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }*/

    private void updateToken() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();

        HashMap hashMap = new HashMap();
        hashMap.put("token",refreshToken);

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(mAuth.getCurrentUser().getUid()).updateChildren(hashMap);
    }

    @Override
    public void onBackPressed() {
        meo.show(ID_HOME,true);
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press back again to exit...", Toast.LENGTH_SHORT).show();
        }
        backPressedTime=System.currentTimeMillis();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            sendUserToLoginActivity();
        }
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LocationActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }



}