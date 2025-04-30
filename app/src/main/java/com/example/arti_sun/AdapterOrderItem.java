package com.example.arti_sun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterOrderItem extends RecyclerView.Adapter<AdapterOrderItem.HolderOrderItem>{

    private Context context;
    private ArrayList<ModelOrderItem> orderItemArrayList;

    public AdapterOrderItem(Context context, ArrayList<ModelOrderItem> orderItemArrayList) {
        this.context = context;
        this.orderItemArrayList = orderItemArrayList;
    }

    @NonNull
    @Override
    public HolderOrderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ordereditem,parent,false);
        return new HolderOrderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderItem holder, int position) {

        ModelOrderItem modelOrderItem = orderItemArrayList.get(position);

        String getpId = modelOrderItem.getpId();
        String name = modelOrderItem.getName();
        String price = modelOrderItem.getPrice();
        String cost = modelOrderItem.getCost();
        String quantity = modelOrderItem.getQuantity();

        holder.itemtitleTv.setText(name);
        holder.priceeachTv.setText("RM"+price);
        holder.quantityTv.setText(quantity);
        holder.amountTv.setText("RM"+cost);


    }

    @Override
    public int getItemCount() {
        return orderItemArrayList.size();
    }

    class HolderOrderItem extends RecyclerView.ViewHolder{

        private TextView itemtitleTv, priceeachTv, quantityTv, amountTv;

        public HolderOrderItem(@NonNull View itemView) {
            super(itemView);

            itemtitleTv = itemView.findViewById(R.id.itemtitleTv);
            priceeachTv = itemView.findViewById(R.id.priceeachTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            amountTv = itemView.findViewById(R.id.amountTv);
        }
    }

}
