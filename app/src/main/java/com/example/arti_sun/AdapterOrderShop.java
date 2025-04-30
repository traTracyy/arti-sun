package com.example.arti_sun;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

public class AdapterOrderShop extends RecyclerView.Adapter<AdapterOrderShop.HolderOrderShop> implements Filterable {

    private Context context;
    public ArrayList<ModelOrderShop> orderShopArrayList, filterList;
    private FilterOrderShop filter;

    public AdapterOrderShop(Context context, ArrayList<ModelOrderShop> orderShopArrayList) {
        this.context = context;
        this.orderShopArrayList = orderShopArrayList;
        this.filterList = orderShopArrayList;
    }

    @NonNull
    @Override
    public AdapterOrderShop.HolderOrderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.srow_order,parent,false);
        return new HolderOrderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderShop.HolderOrderShop holder, int position) {

        ModelOrderShop modelOrderShop = orderShopArrayList.get(position);

        final String orderId = modelOrderShop.getOrderId();
        String orderTime = modelOrderShop.getOrderTime();
        String orderStatus = modelOrderShop.getOrderStatus();
        String orderCost = modelOrderShop.getOrderCost();
        final String orderBy = modelOrderShop.getOrderBy();
        String orderTo = modelOrderShop.getOrderTo();
        String orderAddress = modelOrderShop.getOrderAddress();//

        loadUserInfo(modelOrderShop, holder);

        holder.orderIdtv.setText("Order Id: "+orderId);
        holder.statustv.setText(orderStatus);
        holder.amounttv.setText("RM"+orderCost);

        if (orderStatus.equals("In Progress")){
            holder.statustv.setTextColor(context.getResources().getColor(R.color.black));
        } else if (orderStatus.equals("Completed")) {
            holder.statustv.setTextColor(context.getResources().getColor(R.color.green));
        }else if (orderStatus.equals("Cancelled")) {
            holder.statustv.setTextColor(context.getResources().getColor(R.color.teal_700));
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.datetv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                OrderDetailsSeller fragment = OrderDetailsSeller.newInstance(orderId, orderBy);
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });





    }

    private void loadUserInfo(ModelOrderShop modelOrderShop, final HolderOrderShop holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(modelOrderShop.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+ snapshot.child("email").getValue();
                        holder.emailtv.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderShopArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterOrderShop(this,filterList);
        }
        return filter;
    }

    public class HolderOrderShop extends RecyclerView.ViewHolder {
        private TextView orderIdtv, emailtv, datetv, amounttv,statustv;
        public HolderOrderShop(@NonNull View itemView) {
            super(itemView);

            orderIdtv = itemView.findViewById(R.id.orderIdtv);
            emailtv = itemView.findViewById(R.id.emailtv);
            datetv = itemView.findViewById(R.id.datetv);
            amounttv = itemView.findViewById(R.id.amounttv);
            statustv = itemView.findViewById(R.id.statustv);

        }
    }
}
