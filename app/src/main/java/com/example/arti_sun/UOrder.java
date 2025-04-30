package com.example.arti_sun;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UOrder extends Fragment {

    private RecyclerView ordersRv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri mImageUri;

    private ArrayList<ModelUOrer> modelUOrers1;
    private AdapterUOrder adapterUOrder1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_u_order, container, false);


        //define
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        ordersRv = view.findViewById(R.id.ordersRv);

        loadOrders();


        loadInfo();

        return view;
    }


    //load info
    private void loadInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String uid = ""+ds.child("uid").getValue();
                            String role = ""+ds.child("role").getValue();
                            String myaddress = ""+ds.child("address").getValue();
                            String email = ""+ds.child("email").getValue();
                            String name = ""+ds.child("name").getValue();
                            String myphone = ""+ds.child("phnum").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadOrders() {
        modelUOrers1 = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelUOrers1.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                                            ModelUOrer modelUOrer = ds.getValue(ModelUOrer.class);

                                            //add to list
                                            modelUOrers1.add(modelUOrer);
                                        }
                                        //setup adapter
                                        adapterUOrder1 = new AdapterUOrder(getActivity(),modelUOrers1);
                                        ///set to r view
                                        ordersRv.setAdapter(adapterUOrder1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}