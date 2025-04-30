package com.example.arti_sun;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SProduct extends Fragment {

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private RelativeLayout addbtn;
    private TextView welcometxt, showalltxt;

    private EditText SearchProduct;
    private ImageButton filter;
    private RecyclerView productsRv;
    private ArrayList<ProductList>productList;
    private ProductAdapter productAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_product, container, false);

        addbtn = view.findViewById(R.id.addbtn);
        welcometxt = view.findViewById(R.id.welcometxt);
        SearchProduct = view.findViewById(R.id.SearchProduct);
        filter = view.findViewById(R.id.filter);
        showalltxt = view.findViewById(R.id.showalltxt);
        productsRv = view.findViewById(R.id.productsRv);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);

        checkuser();

        loadAllProducts();

        SearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try{
                    productAdapter.getFilter().filter(charSequence);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable charSequence) {

            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new SAddProduct();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container,fragment).commit();
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String selected = Constants.productCategories1[i];
                                showalltxt.setText(selected);
                                if (selected.equals("All")){
                                    loadAllProducts();
                                }else {
                                    loadFilteredProducts(selected);
                                }
                            }
                        }).show();
            }
        });




        return view;

    }

    private void loadFilteredProducts(String selected) {
        productList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String productCategory = ""+ds.child("ProductCategory").getValue();

                            if (selected.equals(productCategory)){

                                ProductList productList1 = ds.getValue(ProductList.class);
                                productList.add(productList1);
                            }

                        }
                        productAdapter = new ProductAdapter(getActivity(),productList);
                        productsRv.setAdapter(productAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadAllProducts() {
        productList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ProductList productList1 = ds.getValue(ProductList.class);
                            productList.add(productList1);
                        }
                        productAdapter = new ProductAdapter(getActivity(),productList);
                        productsRv.setAdapter(productAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void checkuser(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            loadInfo();
        }
    }
    //loading user's information
    private void loadInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            //String role = ""+ds.child("role").getValue();

                            welcometxt.setText("Welcome, "+ name);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



}