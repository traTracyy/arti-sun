package com.example.arti_sun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.HolderCart> {

    private Context context;
    public ArrayList<ModelCart> modelCarts;
    private ShopDetails shopDetails;

    public AdapterCart(Context context, ShopDetails shopDetails, ArrayList<ModelCart> modelCarts) {
        this.context = context;
        this.shopDetails = shopDetails;
        this.modelCarts = modelCarts;
    }


    public class HolderCart extends RecyclerView.ViewHolder {

        private ImageView productImg;
        private TextView ProductName,quantity,remove,ProductPrice;

        public HolderCart(@NonNull View itemView) {
            super(itemView);

            productImg = itemView.findViewById(R.id.productImg);
            ProductName = itemView.findViewById(R.id.ProductName);
            quantity = itemView.findViewById(R.id.quantity);
            remove = itemView.findViewById(R.id.remove);
            ProductPrice = itemView.findViewById(R.id.ProductPrice);

        }
    }
    @NonNull
    @Override
    public AdapterCart.HolderCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart,parent,false);
        return new HolderCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCart holder, int position) {

        ModelCart modelCart = modelCarts.get(position);

        final String id = modelCart.getId();
        String pId = modelCart.getpId();
        String name = modelCart.getName();
        String price = modelCart.getPrice();
        final String cost = modelCart.getCost();
        String quantity = modelCart.getQuantity();
        String pCartImg = modelCart.getCartImg();

        //set data
        holder.ProductName.setText("" + name);
        holder.ProductPrice.setText(""+cost);
        holder.quantity.setText(""+quantity);

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id", new String[] {"text","unique"}))
                        .addColumn(new Column("Item_PID", new String[] {"text","not null"}))
                        .addColumn(new Column("Item_Name", new String[] {"text","not null"}))
                        .addColumn(new Column("Item_Price_Each", new String[] {"text","not null"}))
                        .addColumn(new Column("Item_Price", new String[] {"text","not null"}))
                        .addColumn(new Column("Item_Quantity", new String[] {"text","not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1,id);
                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();

                int adapterPosition = holder.getAdapterPosition();
                modelCarts.remove(adapterPosition);
                notifyItemChanged(adapterPosition);
                notifyDataSetChanged();

                double tx = Double.parseDouble(((ShopDetails)shopDetails).allTotalPriceTV.getText().toString().trim().replace("",""));
                double totalP = tx- Double.parseDouble(cost.replace("",""));
                ((ShopDetails)shopDetails).allTotalPrice = 0.00;
                ((ShopDetails)shopDetails).allTotalPriceTV.setText("RM"+String.format("%.2f",Double.parseDouble(String.format("%.2f",totalP))));

                


            }
        });



    }

    @Override
    public int getItemCount() {
        return modelCarts.size();
    }


}
