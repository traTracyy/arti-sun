package com.example.arti_sun;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop>{
    private Context context;
    public ArrayList<ModelShop> shopsList;

    public AdapterShop(Context context, ArrayList<ModelShop> shopsList) {
        this.context = context;
        this.shopsList = shopsList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.urow_shop,parent,false);
        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {

        //get data
        ModelShop modelShop = shopsList.get(position);
        final String uid = modelShop.getUid();
        String email = modelShop.getEmail();
        String name = modelShop.getName();
        String phnum = modelShop.getPhnum();
        String password = modelShop.getPassword();
        String shopname = modelShop.getShopname();
        String address = modelShop.getAddress();
        String timestamp = modelShop.getTimestamp();
        String online = modelShop.getOnline();
        String ShopImg = modelShop.getProfileImage();
        String shopOpen = modelShop.getShopOpen();
        String role = modelShop.getRole();

        loadReviews(modelShop, holder);

        //set data
        holder.ShopName.setText(shopname);
        holder.phonenumber.setText(phnum);
        if (online.equals("true")){
            holder.online.setVisibility(View.VISIBLE);
        }else {holder.online.setVisibility(View.GONE);}
        if (shopOpen.equals("true")){
            holder.Close.setVisibility(View.GONE);
        }else {holder.Close.setVisibility(View.VISIBLE);}
        try{
            Picasso.get().load(ShopImg).placeholder(R.drawable.ic_image).into(holder.ShopImg);
        }
        catch (Exception e){
            holder.ShopImg.setImageResource(R.drawable.ic_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopDetails shopDetails = new ShopDetails();

                // Set the arguments for the Fragment
                Bundle args = new Bundle();
                args.putString("shopUid", uid);
                shopDetails.setArguments(args);

                // Replace the current Fragment with the new one
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container1, shopDetails)
                        .addToBackStack(null)
                        .commit();

            }
        });

    }
    private float ratingSum = 0;

    private void loadReviews(ModelShop modelShop, final HolderShop holder) {
        String shopUid = modelShop.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingSum = 0;
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+dataSnapshot.child("ratings").getValue());
                            ratingSum = ratingSum + rating;

                        }

                        long numOfrev = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numOfrev;

                        holder.ratingBar.setRating(avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    public int getItemCount() {
        return shopsList.size();
    }


    class HolderShop extends RecyclerView.ViewHolder{
        private ImageView ShopImg,online;
        private TextView ShopName,phonenumber,Close;
        private RatingBar ratingBar;



        public HolderShop(@NonNull View itemView) {
            super(itemView);

            ShopImg = itemView.findViewById(R.id.ShopImg);
            online = itemView.findViewById(R.id.online);
            ShopName = itemView.findViewById(R.id.ShopName);
            phonenumber = itemView.findViewById(R.id.phonenumber);
            Close = itemView.findViewById(R.id.Close);
            ratingBar = itemView.findViewById(R.id.ratingBar);


        }
    }
}
