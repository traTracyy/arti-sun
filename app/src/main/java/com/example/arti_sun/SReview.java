package com.example.arti_sun;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SReview extends Fragment {
    private String shopUid;
    private TextView bacttxt,snametv,ratingtv;
    private ImageView pimage;
    private RatingBar ratingbar;
    private RecyclerView reviewrv;
    private FirebaseAuth firebaseAuth;


    private ArrayList<ModelReview> reviewArrayList;
    private AdapterReview adapterReview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_review, container, false);

        snametv = view.findViewById(R.id.snametv);
        ratingtv = view.findViewById(R.id.ratingtv);
        pimage = view.findViewById(R.id.pimage);
        ratingbar = view.findViewById(R.id.ratingbar);
        reviewrv = view.findViewById(R.id.reviewrv);

        firebaseAuth = FirebaseAuth.getInstance();
        shopUid = firebaseAuth.getUid();
        loadShopDetails();
        loadReviews();


        return view;


    }


    private float ratingSum = 0;
    private void loadReviews() {
        reviewArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewArrayList.clear();
                        ratingSum = 0;
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+dataSnapshot.child("ratings").getValue());
                            ratingSum = ratingSum + rating;

                            ModelReview modelReview = dataSnapshot.getValue(ModelReview.class);
                            reviewArrayList.add(modelReview);


                        }
                        adapterReview = new AdapterReview(getActivity(),reviewArrayList);
                        reviewrv.setAdapter(adapterReview);

                        long numOfrev = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numOfrev;

                        ratingtv.setText("Average: "+String.format("%.2f",avgRating)+" /5");
                        ratingbar.setRating(avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void loadShopDetails() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopname = "" + snapshot.child("shopname").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();
                        snametv.setText(shopname);

                        try {
                            Picasso.get().load(profileImage).into(pimage);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.ic_profile).into(pimage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        }
    }