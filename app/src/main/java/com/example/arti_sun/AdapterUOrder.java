package com.example.arti_sun;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterUOrder extends RecyclerView.Adapter<AdapterUOrder.HolderUOrder>{

    private Context context;
    private ArrayList<ModelUOrer> modelUOrers;

    public AdapterUOrder(Context context, ArrayList<ModelUOrer> modelUOrers) {
        this.context = context;
        this.modelUOrers = modelUOrers;
    }

    class HolderUOrder extends RecyclerView.ViewHolder {
        private TextView orderIdTv, shopnametTv, amountTv, dateTv,statusTv;
        public HolderUOrder(@NonNull View itemView) {
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            shopnametTv = itemView.findViewById(R.id.shopnametTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            statusTv = itemView.findViewById(R.id.statusTv);


        }
    }

    @NonNull
    @Override
    public AdapterUOrder.HolderUOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.urow_order,parent,false);
        return new HolderUOrder(view);

    }
    public static OrderDetailsUser newInstance(String orderTo, String orderId) {
        OrderDetailsUser fragment = new OrderDetailsUser();
        Bundle args = new Bundle();
        args.putString("orderTo", orderTo);
        args.putString("orderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onBindViewHolder(@NonNull HolderUOrder holder, int position) {

        ModelUOrer modelUOrer = modelUOrers.get(position);

        String orderId = modelUOrer.getOrderId();
        String orderTime = modelUOrer.getOrderTime();
        String orderStatus = modelUOrer.getOrderStatus();
        String orderCost = modelUOrer.getOrderCost();
        String orderBy = modelUOrer.getOrderBy();
        String orderTo = modelUOrer.getOrderTo();

        loadShopInfo(modelUOrer, holder);

        holder.orderIdTv.setText("OrderID: "+orderId);
        holder.statusTv.setText(orderStatus);
        holder.amountTv.setText("RM"+orderCost);



        if (orderStatus.equals("In Progress")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.black));
        } else if (orderStatus.equals("Completed")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
        }else if (orderStatus.equals("Cancelled")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.teal_700));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateTv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetailsUser fragment = OrderDetailsUser.newInstance(orderTo, orderId);
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container1, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


    }

    private void loadShopInfo(ModelUOrer modelUOrer, HolderUOrder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(modelUOrer.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopName = ""+ snapshot.child("shopname").getValue();
                        holder.shopnametTv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelUOrers.size();
    }

}
