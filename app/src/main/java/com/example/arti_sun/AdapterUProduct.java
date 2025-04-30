package com.example.arti_sun;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterUProduct extends RecyclerView.Adapter<AdapterUProduct.HolderUProduct> implements Filterable {

    private Context context;
    ArrayList<ProductList> productsList, filterList;
    private UFilterProduct uFilterProduct;

    public AdapterUProduct(Context context, ArrayList<ProductList> productsList){
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @Override
    public Filter getFilter() {
        if (uFilterProduct == null){
            uFilterProduct = new UFilterProduct(this,filterList);
        }
        return uFilterProduct;
    }

    public class HolderUProduct extends RecyclerView.ViewHolder {
        private ImageView productImg;
        private TextView ProductName, ProductDes, add2cart,ProductPrice;

        public HolderUProduct(@NonNull View itemView) {
            super(itemView);

            productImg = itemView.findViewById(R.id.productImg);
            ProductName = itemView.findViewById(R.id.ProductName);
            ProductDes = itemView.findViewById(R.id.ProductDes);
            add2cart = itemView.findViewById(R.id.add2cart);
            ProductPrice = itemView.findViewById(R.id.ProductPrice);


        }
    }

    @NonNull
    @Override
    public AdapterUProduct.HolderUProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.urow_product,parent,false);
        return new HolderUProduct(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUProduct.HolderUProduct holder, int position) {
        //getdata
        final ProductList productLists=productsList.get(position);
        String id = productLists.getProductId();
        String ProductCategory = productLists.getProductCategory();
        String ProductPrice = productLists.getProductPrice();
        String ProductQuantity = productLists.getProductQuantity();
        String ProductName = productLists.getProductName();
        String productImg = productLists.getProductImage();
        String ProductDes = productLists.getProductDescription();
        String timestamp = productLists.getTimestamp();

        //setdata
        holder.ProductName.setText(ProductName);
        holder.ProductPrice.setText("RM"+ ProductPrice);
        holder.ProductDes.setText(ProductDes);
        try{
            Picasso.get().load(productImg).placeholder(R.drawable.ic_image).into(holder.productImg);
        }
        catch (Exception e){
            holder.productImg.setImageResource(R.drawable.ic_image);
        }
        holder.add2cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showquantityDislog(productLists);

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    private double cost = 0;
    private double finalcost = 0;
    private int quantityy = 0;
    private int maxQuantity = 0;
    private void showquantityDislog(ProductList productLists) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity, null);

        ImageView productimg = view.findViewById(R.id.productimg);
        TextView productname = view.findViewById(R.id.productname);
        TextView productquantity = view.findViewById(R.id.productquantity);//
        TextView productprice = view.findViewById(R.id.productprice);
        TextView quantity = view.findViewById(R.id.quantity);//user
        TextView finalTV = view.findViewById(R.id.finalTV);
        TextView productdescriptiom = view.findViewById(R.id.productdescriptiom);
        ImageButton minus = view.findViewById(R.id.minus);
        ImageButton add = view.findViewById(R.id.add);
        Button continuebtn = view.findViewById(R.id.continuebtn);


        //get data
        String id = productLists.getProductId();
        //String ProductCategory = productLists.getProductCategory();
        String ProductPrice = productLists.getProductPrice();
        String ProductQuantity = productLists.getProductQuantity();
        String ProductName = productLists.getProductName();
        String productImg1 = productLists.getProductImage();
        String ProductDes = productLists.getProductDescription();
        //String timestamp = productLists.getTimestamp();

        cost = Double.parseDouble(ProductPrice.replaceAll("",""));
        finalcost = Double.parseDouble(ProductPrice.replaceAll("",""));
        maxQuantity = Integer.parseInt(ProductQuantity.replaceAll("",""));
        quantityy = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try{
            Picasso.get().load(productImg1).placeholder(R.drawable.ic_image2).into(productimg);
        }
        catch (Exception e){
            productimg.setImageResource(R.drawable.ic_image2);
        }

        productname.setText("" + ProductName);
        productquantity.setText("" + ProductQuantity);
        productprice.setText("" + ProductPrice);
        quantity.setText("" + quantityy);
        finalTV.setText("" + finalcost);
        productdescriptiom.setText("" + ProductDes);

        AlertDialog dialog = builder.create();
        dialog.show();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantityy < maxQuantity) { // check if current quantity is less than maximum allowed quantity
                    finalcost += cost;
                    quantityy++;
                    finalTV.setText("" + finalcost);
                    quantity.setText(""+quantityy);
                }
                else {
                    // show an error message or disable the add button
                    Toast.makeText(context.getApplicationContext(), "Maximum quantity reached", Toast.LENGTH_SHORT).show();
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantityy >1){
                    finalcost = finalcost - cost;
                    quantityy--;

                    finalTV.setText("RM" + finalcost);
                    quantity.setText(""+quantityy);
                }
            }
        });

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = productname.getText().toString().trim();
                String priceEach = productprice.getText().toString().trim().replace("","");
                String price = finalTV.getText().toString().trim().replace("","");
                String quantitylah = quantity.getText().toString().trim();
                
                add2Cart(id, title, priceEach, price, quantitylah);
                dialog.dismiss();
                

            }
        });



    }

    private int itemId=1;
    private void add2Cart(String id, String title, String priceEach, String price, String quantitylah) {
        itemId++;
        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[] {"text","unique"}))
                .addColumn(new Column("Item_PID", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Name", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Price", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[] {"text","not null"}))
                .doneTableColumn();

        Boolean b = easyDB
                .addData("Item_Id",itemId)
                .addData("Item_PID", id)
                .addData("Item_Name",title)
                .addData("Item_Price_Each", priceEach)
                .addData("Item_Price",price)
                .addData("Item_Quantity", quantitylah)
                .doneDataAdding();
        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();


    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }


}
