package com.example.arti_sun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MUserActivity extends AppCompatActivity {

    private TextView username;

    //private Button logoutbtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    BottomNavigationView nav_bottom1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muser);

        //logoutbtn = findViewById(R.id.logoutbtn);
        //username = findViewById(R.id.username);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        checkuser();

        nav_bottom1 = findViewById(R.id.nav_bottom1);
        getSupportFragmentManager().beginTransaction().replace(R.id.container1, new UHome()).commit();
        nav_bottom1.setSelectedItemId(R.id.nav_uhome);
        nav_bottom1.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_uhome:
                        fragment = new UHome();
                        break;
                    case R.id.nav_uorder:
                        fragment = new UOrder();
                        break;
                    case R.id.nav_ewallter:
                        fragment = new UEwallet();
                        break;
                    case R.id.nav_usetting:
                        fragment = new USetting();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragment).commit();
                return true;
            }

        });

    }

    //checking user's information
    private void checkuser(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MUserActivity.this, LoginActivity.class));
            finish();
        }
        else {
            loadInfo();
        }
    }

    //loading user's information
    private void loadInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String role = ""+ds.child("role").getValue();

                            //username.setText(name + "("+role+")");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}