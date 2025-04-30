package com.example.arti_sun;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderDetailsSeller extends Fragment {
    private String orderBy;
    private String orderId;
    private TextView bacttxt, orderidtv, datetv, orderstatustv, buyeremailtv, buyerphonetv, totalItemtv,totalamounttv,daddresstv;
    private RecyclerView recoItem;
    private Button editbtn;

    private ArrayList<ModelOrderItem> orderItemArrayList;
    private AdapterOrderItem adapterOrderItem;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    public static OrderDetailsSeller newInstance(String orderId, String orderBy) {
        OrderDetailsSeller fragment = new OrderDetailsSeller();
        Bundle args = new Bundle();
        args.putString("orderId", orderId);
        args.putString("orderBy", orderBy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_details_seller, container, false);

        Bundle args = getArguments();
        if (args != null) {
            orderId = args.getString("orderId");
            orderBy = args.getString("orderBy");
        }


        bacttxt = view.findViewById(R.id.bacttxt);
        orderidtv = view.findViewById(R.id.orderidtv);
        datetv = view.findViewById(R.id.datetv);
        orderstatustv = view.findViewById(R.id.orderstatustv);
        buyeremailtv = view.findViewById(R.id.buyeremailtv);
        buyerphonetv = view.findViewById(R.id.buyerphonetv);
        totalItemtv = view.findViewById(R.id.totalItemtv);
        totalamounttv = view.findViewById(R.id.totalamounttv);
        daddresstv = view.findViewById(R.id.daddresstv);;
        recoItem = view.findViewById(R.id.recoItem);
        editbtn = view.findViewById(R.id.editbtn);

        //define
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        loadBuyerinfo();
        loadOrderDetails();
        loadOrderItems();

        bacttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editorderstatus();
            }
        });


        return view;
    }

    private void editorderstatus() {

        final String[] options = {"In Progress", "Completed", "Cancelled"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Order Status")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selectedoption = options[i];
                        editOrderS(selectedoption);
                    }
                }).show();

    }

    private void editOrderS(String selectedoption) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus",""+selectedoption);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(),"Status Changed to "+ selectedoption,Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadOrderItems() {
        orderItemArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderItemArrayList.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            ModelOrderItem modelOrderItem = dataSnapshot.getValue(ModelOrderItem.class);
                            orderItemArrayList.add(modelOrderItem);
                        }
                        adapterOrderItem = new AdapterOrderItem(getActivity(), orderItemArrayList);
                        recoItem.setAdapter(adapterOrderItem);
                        totalItemtv.setText(""+snapshot.getChildrenCount());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void loadOrderDetails() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
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

    private void loadBuyerinfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+snapshot.child("email").getValue();
                        String phnum = ""+snapshot.child("phnum").getValue();

                        buyeremailtv.setText(email);
                        buyerphonetv.setText(phnum);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



}