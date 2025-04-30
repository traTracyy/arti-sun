package com.example.arti_sun;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class OrderDetailsUser extends Fragment {
    private String orderTo, orderId;
    private TextView bacttxt, orderidtv, datetv, orderstatustv, shopnametv, totalItemtv,totalamounttv,daddresstv;
    private RecyclerView recItem;
    private Button reviewbtn;
    private ArrayList<ModelOrderItem> orderItemArrayList;
    private AdapterOrderItem adapterOrderItem;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    public static OrderDetailsUser newInstance(String orderTo, String orderId) {
        OrderDetailsUser fragment = new OrderDetailsUser();
        Bundle args = new Bundle();
        args.putString("orderTo", orderTo);
        args.putString("orderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_details_user, container, false);

        Bundle args = getArguments();
        if(args != null){
            orderTo = args.getString("orderTo");
            orderId = args.getString("orderId");
        }

        bacttxt = view.findViewById(R.id.bacttxt);
        orderidtv = view.findViewById(R.id.orderidtv);
        reviewbtn = view.findViewById(R.id.reviewbtn);
        daddresstv = view.findViewById(R.id.daddresstv);
        datetv = view.findViewById(R.id.datetv);
        orderstatustv = view.findViewById(R.id.orderstatustv);
        shopnametv = view.findViewById(R.id.shopnametv);
        totalItemtv = view.findViewById(R.id.totalItemtv);
        totalamounttv = view.findViewById(R.id.totalamounttv);
        recItem = view.findViewById(R.id.recItem);

        //define
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadShopInfo();
        loadOrderDetails();
        loadOrderItems();


        //TODO: pass arg, for review
        reviewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UReview uReviewFragment = new UReview();
                Bundle args = new Bundle();
                args.putString("shopUid", orderTo);
                uReviewFragment.setArguments(args);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container1, uReviewFragment);
                transaction.addToBackStack(null);
                transaction.commit();


            }
        });

        bacttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });



        return view;
    }

    private void loadOrderItems() {
        orderItemArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderItemArrayList.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            ModelOrderItem modelOrderItem = dataSnapshot.getValue(ModelOrderItem.class);
                            orderItemArrayList.add(modelOrderItem);
                        }
                        adapterOrderItem = new AdapterOrderItem(getActivity(), orderItemArrayList);
                        recItem.setAdapter(adapterOrderItem);
                        totalItemtv.setText(""+snapshot.getChildrenCount());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void loadOrderDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String orderId = ""+snapshot.child("orderId").getValue();
                String orderTime = ""+snapshot.child("orderTime").getValue();
                String orderStatus = ""+snapshot.child("orderStatus").getValue();
                String orderCost = ""+snapshot.child("orderCost").getValue();
                String orderBy = ""+snapshot.child("orderBy").getValue();
                String orderTo = ""+snapshot.child("orderTo").getValue();
                String orderAddress = ""+snapshot.child("orderAddress").getValue();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(orderTime));
                String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();

                if (orderStatus.equals("In Progress")){
                    orderstatustv.setTextColor(getResources().getColor(R.color.black));
                } else if (orderStatus.equals("Completed")) {
                    orderstatustv.setTextColor(getResources().getColor(R.color.green));
                }else if (orderStatus.equals("Cancelled")) {
                    orderstatustv.setTextColor(getResources().getColor(R.color.teal_700));
                }


                orderidtv.setText(orderId);
                datetv.setText(formatedDate);
                orderstatustv.setText(orderStatus);
                totalamounttv.setText("RM"+orderCost);
                daddresstv.setText(orderAddress);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void loadShopInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopname = ""+snapshot.child("shopname").getValue();
                        shopnametv.setText(shopname);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


}