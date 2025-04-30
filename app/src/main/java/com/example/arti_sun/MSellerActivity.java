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

public class MSellerActivity extends AppCompatActivity {

    private TextView username;

    //private Button logoutbtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    BottomNavigationView nav_bottom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mseller);

        //logoutbtn = findViewById(R.id.logoutbtn);
        //username = findViewById(R.id.username);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        checkuser();

        nav_bottom = findViewById(R.id.nav_bottom);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SProduct()).commit();
        nav_bottom.setSelectedItemId(R.id.nav_product);
        nav_bottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_product:
                        fragment = new SProduct();
                        break;
                    case R.id.nav_order:
                        fragment = new SOrder();
                        break;
                    case R.id.nav_review:
                        fragment = new SReview();
                        break;
                    case R.id.nav_income:
                        fragment = new SIncome();
                        break;
                    case R.id.nav_setting:
                        fragment = new SSetting();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                return true;
            }

        });




    }

    //checking user's information
    private void checkuser(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MSellerActivity.this, LoginActivity.class));
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

                            //username.setText("Hi,"+name);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}