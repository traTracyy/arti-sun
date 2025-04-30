package com.example.arti_sun;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.HolderProduct> implements Filterable {

    private Context context;
    private FilterProduct filter;
    ArrayList<ProductList> productList, filterList;

    public ProductAdapter(Context context, ArrayList<ProductList> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.srow_product,parent,false);
        return new HolderProduct(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProduct holder, int position) {

        ProductList productList1=productList.get(position);
        String id = productList1.getProductId();
        String uid = productList1.getUid();
        String ProductCategory = productList1.getProductCategory();
        String ProductPrice = productList1.getProductPrice();
        String ProductQuantity = productList1.getProductQuantity();
        String ProductName = productList1.getProductName();
        String productImg = productList1.getProductImage();
        String ProductDes = productList1.getProductDescription();
        String timestamp = productList1.getTimestamp();

        holder.ProductName.setText(ProductName);
        holder.ProductCategory.setText(ProductCategory);
        holder.ProductPrice.setText("RM: "+ ProductPrice);
        holder.ProductQuantity.setText("Quantity: "+ProductQuantity);
        try{
            Picasso.get().load(productImg).placeholder(R.drawable.ic_image).into(holder.productImg);
        }
        catch (Exception e){
            holder.productImg.setImageResource(R.drawable.ic_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                detailsBottomSheet(productList1);//productList??

            }
        });
    }

    private void detailsBottomSheet(ProductList productList1) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.product_details_seller, null);
        bottomSheetDialog.setContentView(view);

        RelativeLayout backbtn = view.findViewById(R.id.Rlayout2);
        RelativeLayout deletebtn = view.findViewById(R.id.Rlayout1);
        RelativeLayout editbtn = view.findViewById(R.id.Rlayout3);
        ImageView productic = view.findViewById(R.id.productic);
        TextView ProductTitletv = view.findViewById(R.id.ProductTitle);
        TextView ProductDestv = view.findViewById(R.id.ProductDes);
        TextView ProductPricetv = view.findViewById(R.id.ProductPrice);
        TextView ProductQuantitytv = view.findViewById(R.id.ProductQuantity);
        TextView ProductCategorytv = view.findViewById(R.id.ProductCategory);

        //get data
        final String id = productList1.getProductId();
        String uid = productList1.getUid();
        String ProductCategory = productList1.getProductCategory();
        String ProductPrice = productList1.getProductPrice();
        String ProductQuantity = productList1.getProductQuantity();
        String ProductDes = productList1.getProductDescription();
        final String ProductName = productList1.getProductName();
        String productImg = productList1.getProductImage();
        String timestamp = productList1.getTimestamp();

        //set data
        ProductTitletv.setText(ProductName);
        ProductDestv.setText(ProductDes);
        ProductPricetv.setText(ProductPrice);
        ProductQuantitytv.setText(ProductQuantity);
        ProductCategorytv.setText(ProductCategory);

        try{
            Picasso.get().load(productImg).placeholder(R.drawable.ic_image).into(productic);
        }
        catch (Exception e){
            productic.setImageResource(R.drawable.ic_image);
        }

        bottomSheetDialog.show();

        //edit
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();

                // Create a new instance of SEditProduct Fragment
                SEditProduct editProductFragment = new SEditProduct();

                // Set the arguments for the Fragment
                Bundle args = new Bundle();
                args.putString("productId", id);
                editProductFragment.setArguments(args);

                // Replace the current Fragment with the new one
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, editProductFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });



        //delete
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Detele")
                        .setMessage("Are you confirm to delete product "+ProductName+" ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //delete
                                deleteProduct(id);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //cancel delete
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            }
        });

        //back
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();

            }
        });






    }

    private void deleteProduct(String id) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "product deleted", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProduct(this,filterList);
        }
        return filter;
    }

    class HolderProduct extends RecyclerView.ViewHolder{


        private ImageView productImg;
        private TextView ProductName,ProductCategory,ProductPrice,ProductQuantity;

        public HolderProduct(@NonNull View itemView) {
            super(itemView);

            productImg = itemView.findViewById(R.id.productImg);
            ProductName = itemView.findViewById(R.id.ProductName);
            ProductCategory = itemView.findViewById(R.id.ProductCategory);
            ProductPrice = itemView.findViewById(R.id.ProductPrice);
            ProductQuantity = itemView.findViewById(R.id.ProductQuantity);

        }
    }
}
