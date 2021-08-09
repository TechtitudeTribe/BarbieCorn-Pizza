package com.techtitudetribe.barbiecornpizza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SetupActivity extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private TextView register;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        firebaseAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.setup_email);
        password = (EditText) findViewById(R.id.setup_password);
        confirmPassword = (EditText) findViewById(R.id.setup_confirm_password);
        register = (TextView) findViewById(R.id.setup_register_now);
        progressBar = (ProgressBar) findViewById(R.id.setup_progress_bar);
        address = getIntent().getStringExtra("Address");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });
    }

    private void checkValidations() {
        if (TextUtils.isEmpty(email.getText().toString().trim()))
        {
            Toast.makeText(this, "Please fill email...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password.getText().toString()))
        {
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(confirmPassword.getText().toString()))
        {
            Toast.makeText(this, "Please enter confirm password...", Toast.LENGTH_SHORT).show();
        }
        else if (!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim()))
        {
            Toast.makeText(this, "Password don't match. Try Again...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        String currentUser = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.hasChild(currentUser))
                                {
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("Name","Username");
                                    hashMap.put("ContactNumber","0000000000");
                                    hashMap.put("Address",address);
                                    hashMap.put("Email",email.getText().toString().trim());
                                    userRef.child(currentUser).updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if (error!=null)
                                            {
                                                progressBar.setVisibility(View.GONE);
                                                String mssg = error.getMessage();
                                                Toast.makeText(SetupActivity.this, "Error Occurred : "+mssg, Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(SetupActivity.this,MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("Address",address);
                                    userRef.child(currentUser).updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if (error!=null)
                                            {
                                                progressBar.setVisibility(View.GONE);
                                                String mssg = error.getMessage();
                                                Toast.makeText(SetupActivity.this, "Error Occurred : "+mssg, Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(SetupActivity.this,MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SetupActivity.this, "Error Occurred : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}