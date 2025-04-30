package com.example.arti_sun;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;


public class SOrder extends Fragment {
    private RelativeLayout Rlayoutfilter;
    private RecyclerView SordersRv;
    private TextView fitterOrder;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_order, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);

        fitterOrder = view.findViewById(R.id.fitterOrder);
        Rlayoutfilter = view.findViewById(R.id.Rlayoutfilter);
        SordersRv = view.findViewById(R.id.SordersRv);


        loadAllOrder();

        Rlayoutfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] options = {"All", "In Progress","Completed","Cancelled"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Filter Orders:")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    fitterOrder.setText("Showing All Order(s)");
                                    adapterOrderShop.getFilter().filter("");
                                }
                                else {
                                    String optionClicked = options[i];
                                    fitterOrder.setText("Showing "+optionClicked+" Order(s)");
                                    adapterOrderShop.getFilter().filter(optionClicked);

                                }
                            }
                        }).show();
            }
        });




        return view;
    }

    private void loadAllOrder() {
        orderShopArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderShopArrayList.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            ModelOrderShop modelOrderShop = dataSnapshot.getValue(ModelOrderShop.class);
                            orderShopArrayList.add(modelOrderShop);
                        }
                        adapterOrderShop =  new AdapterOrderShop(getActivity(),orderShopArrayList);
                        SordersRv.setAdapter(adapterOrderShop);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}