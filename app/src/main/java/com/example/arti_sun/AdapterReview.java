package com.example.arti_sun;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview>{

    private Context context;
    private ArrayList<ModelReview> reviewArrayList;

    public AdapterReview(Context context, ArrayList<ModelReview> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    class HolderReview extends RecyclerView.ViewHolder{

        private ImageView profile;
        private TextView nameTv, datetv, reviewcontent;
        private RatingBar ratingbar;


        public HolderReview(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profile);
            nameTv = itemView.findViewById(R.id.nameTv);
            datetv = itemView.findViewById(R.id.datetv);
            reviewcontent = itemView.findViewById(R.id.reviewcontent);
            ratingbar = itemView.findViewById(R.id.ratingbar);


        }
    }
    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_review,parent,false);
        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {
        ModelReview modelReview = reviewArrayList.get(position);
        String uid = modelReview.getUid();
        String ratings = modelReview.getRatings();
        String review = modelReview.getReview();
        String timestamp = modelReview.getTimestamp();

        loadUserDetails(modelReview, holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dataFormat = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.ratingbar.setRating(Float.parseFloat(ratings));
        holder.reviewcontent.setText(review);
        holder.datetv.setText(dataFormat);


    }

    private void loadUserDetails(ModelReview modelReview, final HolderReview holder) {
        String uid = modelReview.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String name = ""+snapshot.child("name").getValue();
                        String proImg = ""+snapshot.child("profileImage").getValue();

                        holder.nameTv.setText(name);

                        try {
                            Picasso.get().load(proImg).into(holder.profile);}
                        catch (Exception e){Picasso.get().load(R.drawable.ic_profile).into(holder.profile);}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

}
