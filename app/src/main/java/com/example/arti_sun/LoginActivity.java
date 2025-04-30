package com.example.arti_sun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    private EditText emailhint,passwordhint,passwordhint2;
    private TextView forgotpassword,registersellertxt,registeruser;
    private Button loginbtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailhint = findViewById(R.id.emailhint);
        passwordhint = findViewById(R.id.passwordhint);
        passwordhint2 = findViewById(R.id.passwordhint2);
        forgotpassword = findViewById(R.id.forgotpassword);
        registersellertxt = findViewById(R.id.registersellertxt);
        registeruser = findViewById(R.id.registeruser);
        loginbtn = findViewById(R.id.loginbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Connecting...");
        progressDialog.setCanceledOnTouchOutside(false);

        //lead to seller register page
        registersellertxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterSellerActivity.class));
            }
        });
        //lead to user register page
        registeruser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterUserActivity.class));
            }
        });
        //lead to forgot password page
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        //login. function:login,toOnline,UserRole
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();

            }
        });

    }



    private String email,password,cpassword;

    //validate entered info
    private void login(){
        email = emailhint.getText().toString().trim();
        password = passwordhint.getText().toString().trim();
        cpassword = passwordhint2.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email Address",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Your Email Address",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<8){
            Toast.makeText(this,"Password at least 8 characters!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(cpassword)){
            Toast.makeText(this,"Password at least 8 characters!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(cpassword)){
            Toast.makeText(this,"Please Enter Confirm Password",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(cpassword)){
            Toast.makeText(this,"Password doesn't match, please enter both correctly!",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //successful
                        toOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //change to onlive status
    private void toOnline(){
        progressDialog.setMessage("Loading...");

        HashMap<String, Object>hashMap = new HashMap<>();
        hashMap.put("online","true");

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        UserRole();                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //check the user role
    private void UserRole(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String role = ""+ds.child("role").getValue();
                            if (role.equals("seller")){
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MSellerActivity.class));
                                finish();
                            }
                            else {
                                //buyer
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MUserActivity.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



}