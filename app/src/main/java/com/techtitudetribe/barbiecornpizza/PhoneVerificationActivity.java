package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;

public class PhoneVerificationActivity extends AppCompatActivity {

    private TextView sendOtp;
    private EditText phoneNumber;
    private String address;
    private CountryCodePicker countryCodePicker;
    private FirebaseAuth firebaseAuth;
    private EditText email, password;
    private TextView loginButton;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        firebaseAuth = FirebaseAuth.getInstance();
        sendOtp = (TextView) findViewById(R.id.send_otp);
        phoneNumber = (EditText) findViewById(R.id.phone_verify_number);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.phone_verify_country_code);
        countryCodePicker.registerCarrierNumberEditText(phoneNumber);

        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        loginButton  = (TextView) findViewById(R.id.login_button);
        register = (TextView) findViewById(R.id.phone_login);

        address = getIntent().getStringExtra("Address").toString();

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),OtpVerificationActivity.class);
                intent.putExtra("Number",countryCodePicker.getFullNumberWithPlus().replace(" ",""));
                intent.putExtra("Address",address);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SetupActivity.class);
                intent.putExtra("Address",address);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(email.getText().toString().trim()))
                {
                    Toast.makeText(PhoneVerificationActivity.this, "E-mail is missing...", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(password.getText().toString().trim()))
                {
                    Toast.makeText(PhoneVerificationActivity.this, "Password is missing...", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        Intent intent = new Intent(PhoneVerificationActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(PhoneVerificationActivity.this, "Login successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(PhoneVerificationActivity.this, "Error Occurred : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }


}