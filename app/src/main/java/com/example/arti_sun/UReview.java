package com.example.arti_sun;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UReview extends Fragment {
    private String shopUid;
    private TextView bacttxt, snametv;
    private EditText reviewEt;
    private ImageView pimage;
    private RatingBar rating;
    private Button donerevbtn;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_u_review, container, false);

        bacttxt = view.findViewById(R.id.bacttxt);
        snametv = view.findViewById(R.id.snametv);
        reviewEt = view.findViewById(R.id.reviewEt);
        rating = view.findViewById(R.id.rating);
        donerevbtn = view.findViewById(R.id.donerevbtn);
        pimage = view.findViewById(R.id.pimage);


        //TODO: accept arg, for review
        Bundle bundle = getArguments();
        if (bundle != null) {
            shopUid = bundle.getString("shopUid");
        }

        firebaseAuth = FirebaseAuth.getInstance();
        loadRev();
        loadShopInfo();

        bacttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new OrderDetailsUser();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container1,fragment).commit();


            }
        });

        donerevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputrev();
            }
        });




        return view;
    }

    private void loadShopInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String shopname = ""+snapshot.child("shopname").getValue();
                String profileImage = ""+snapshot.child("profileImage").getValue();

                snametv.setText(shopname);

                try {
                    Picasso.get().load(profileImage).into(pimage);}
                catch (Exception e){Picasso.get().load(R.drawable.ic_profile).into(pimage);}


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadRev() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String uid = ""+snapshot.child("uid").getValue();
                            String ratings = ""+snapshot.child("ratings").getValue();
                            String review = ""+snapshot.child("review").getValue();
                            String timestamp = ""+snapshot.child("timestamp").getValue();

                            float myRating = Float.parseFloat(ratings);
                            rating.setRating(myRating);
                            reviewEt.setText(review);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void inputrev() {
        String ratings = ""+rating.getRating();
        String review = reviewEt.getText().toString().trim();

        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("ratings",""+ ratings);
        hashMap.put("review",""+ review);
        hashMap.put("timestamp",""+ timestamp);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Ratings").child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(),"Submitted", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }







}