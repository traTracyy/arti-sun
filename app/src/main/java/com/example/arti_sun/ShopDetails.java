package com.example.arti_sun;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetails extends Fragment {

    private ArrayList<ProductList> productsLists;
    private String shopUid, shopName, shopEmail, shopPhone, shopAddress;
    private ImageView shopImage;
    private RelativeLayout Rlayout2,revhis;
    private String myphone,myaddress;
    private TextView cartbtn;
    private TextView shopNameTv, phoneTv, emailTv, addressTv, openTv,showalltxt,shopname;
    private ImageButton call, email, filter;
    private EditText SearchProduct;
    private RecyclerView productsRv;
    private FirebaseAuth firebaseAuth;
    private AdapterCart adapterCart;
    private RatingBar ratingbar;

    private ProgressDialog progressDialog;
    private Uri mImageUri;
    private ArrayList<ProductList> productsList;
    private AdapterUProduct adapterUProduct;

    private ArrayList<ModelCart> modelCarts;
    public TextView allTotalPriceTV,cartcount;
    private EasyDB easyDB;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_details, container, false);
        shopUid = getArguments().getString("shopUid");
        //define
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        shopNameTv = view.findViewById(R.id.shopNameTv);
        cartcount = view.findViewById(R.id.cartcount);
        Rlayout2 = view.findViewById(R.id.Rlayout2);
        cartbtn = view.findViewById(R.id.cartbtn);
        showalltxt = view.findViewById(R.id.showalltxt);
        phoneTv = view.findViewById(R.id.phoneTv);
        emailTv = view.findViewById(R.id.emailTv);
        addressTv = view.findViewById(R.id.addressTv);
        openTv = view.findViewById(R.id.openTv);
        call = view.findViewById(R.id.call);
        email = view.findViewById(R.id.email);
        filter = view.findViewById(R.id.filter);
        SearchProduct = view.findViewById(R.id.SearchProduct);
        shopImage = view.findViewById(R.id.shopImage);
        productsRv = view.findViewById(R.id.productsRv);
        revhis = view.findViewById(R.id.revhis);
        ratingbar = view.findViewById(R.id.ratingbar);


        loadInfo();
        loadShopDetails();
        loadShopProducts();
        loadReviews();
        
        
        easyDB = EasyDB.init(getActivity(), "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[] {"text","unique"}))
                .addColumn(new Column("Item_PID", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Name", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Price", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[] {"text","not null"}))
                .doneTableColumn();

        deleteCartData();
        cartCount();

        Rlayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UHome();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container1,fragment).commit();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dial();
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emaila();
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
                                    loadShopProducts();
                                }else {
                                    adapterUProduct.getFilter().filter(selected);
                                }
                            }
                        }).show();
            }
        });
        SearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try{
                    adapterUProduct.getFilter().filter(charSequence);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable charSequence) {

            }
        });
        cartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCartDialog(ShopDetails.this);
            }
        });

        revhis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShopReview shopReview = new ShopReview();

                // Create a bundle and pass the shopUid value to it
                Bundle bundle = new Bundle();
                bundle.putString("shopUid", shopUid);

                // Set the arguments of the ShopReview fragment to the bundle
                shopReview.setArguments(bundle);

                // Replace the current fragment with the ShopReview fragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container1, shopReview);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



        return view;
    }


    private float ratingSum = 0;

    private void loadReviews() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingSum = 0;
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+dataSnapshot.child("ratings").getValue());
                            ratingSum = ratingSum + rating;

                        }

                        long numOfrev = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numOfrev;

                        ratingbar.setRating(avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //load info
    private void loadInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String uid = ""+ds.child("uid").getValue();
                            String role = ""+ds.child("role").getValue();
                            myaddress = ""+ds.child("address").getValue();
                            String email = ""+ds.child("email").getValue();
                            String name = ""+ds.child("name").getValue();
                            myphone = ""+ds.child("phnum").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void deleteCartData() {
        easyDB.deleteAllDataFromTable();
    }

    // TODO: Delete cartcount maybe
    public void cartCount(){
        int count = easyDB.getAllData().getCount();
        if (count<=0){
            cartcount.setVisibility(View.GONE);
        }
        else {
            cartcount.setVisibility(View.VISIBLE);
            cartcount.setText(""+count);
        }

    }
    public double allTotalPrice = 0.00;

    private void ShowCartDialog(ShopDetails shopDetails) {

        modelCarts = new ArrayList<>();


        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_cart,null);
        TextView shopname = view.findViewById(R.id.shopname);
        RecyclerView cartitem = view.findViewById(R.id.cartitem123);
        allTotalPriceTV = view.findViewById(R.id.pricetotallah);
        Button chechoutbtn = view.findViewById(R.id.chechoutbtn);

        AdapterCart adapterCart = new AdapterCart(getContext(), shopDetails, modelCarts);

        cartitem.setAdapter(adapterCart);

        shopname.setText(shopName);

        EasyDB easyDB = EasyDB.init(requireContext(), "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[] {"text","unique"}))
                .addColumn(new Column("Item_PID", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Name", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Price", new String[] {"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[] {"text","not null"}))
                .doneTableColumn();

        Cursor cursor = easyDB.getAllData();
        while (cursor.moveToNext()){
            String id = cursor.getString(1);
            String pId = cursor.getString(2);
            String name = cursor.getString(3);
            String price = cursor.getString(4);
            String cost = cursor.getString(5);
            String quantity = cursor.getString(6);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);
            ModelCart modelCart = new ModelCart(""+id,""+pId,""+name,""+price,""+cost,""+quantity);

            modelCarts.add(modelCart);
        }
        adapterCart = new AdapterCart(getContext(), shopDetails, modelCarts);


        cartitem.setAdapter(adapterCart);

        allTotalPriceTV.setText(""+String.format("%.2f",allTotalPrice));



        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.00;
            }
        });


        chechoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (myphone.equals("")||myphone.equals("null")){
                    Toast.makeText(getActivity(),"Please Enter Your Phone Number in Setting Page",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (myaddress.equals("")||myaddress.equals("null")){
                    Toast.makeText(getActivity(),"Please Enter Your Home Address in Setting Page",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (modelCarts.size() ==0){
                    Toast.makeText(getActivity(),"No Item in Cart",Toast.LENGTH_SHORT).show();
                    return;
                }

                submitOrder();

            }
        });


    }

    private void submitOrder() {
        progressDialog.setMessage("Placing Order...");
        progressDialog.show();
        String timestamp = ""+ System.currentTimeMillis();
        String cost = allTotalPriceTV.getText().toString().trim().replace("","");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId",""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderStatus",""+"In Progress");
        hashMap.put("orderCost",""+cost);
        hashMap.put("orderBy",""+firebaseAuth.getUid());
        hashMap.put("orderTo",""+shopUid);
        hashMap.put("orderAddress",""+myaddress);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (int i=0; i<modelCarts.size();i++){
                            String id = modelCarts.get(i).getId();
                            String pId = modelCarts.get(i).getpId();
                            String name = modelCarts.get(i).getName();
                            String price = modelCarts.get(i).getPrice();
                            String cost = modelCarts.get(i).getCost();
                            String quantity = modelCarts.get(i).getQuantity();

                            HashMap<String, String > hashMap1 = new HashMap<>();
                            hashMap1.put("pId",pId);
                            hashMap1.put("name",name);
                            hashMap1.put("price",price);
                            hashMap1.put("cost",cost);
                            hashMap1.put("quantity",quantity);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                        }
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Order Placed Successful",Toast.LENGTH_SHORT).show();

                        OrderDetailsUser fragment = OrderDetailsUser.newInstance(shopUid, timestamp);
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        transaction.replace(R.id.container1, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void emaila() {
    }
    private void dial() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toast.makeText(getActivity(),""+shopPhone,Toast.LENGTH_SHORT).show();
    }
    private void loadShopProducts() {
        productsList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ProductList productList = ds.getValue(ProductList.class);
                            productsList.add(productList);

                        }
                        adapterUProduct = new AdapterUProduct(getActivity(),productsList);
                        productsRv.setAdapter(adapterUProduct);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadShopDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = ""+ snapshot.child("name").getValue();
                String shopOpen = ""+ snapshot.child("shopOpen").getValue();
                shopName = ""+ snapshot.child("shopname").getValue();
                shopEmail = ""+ snapshot.child("email").getValue();
                shopPhone = ""+ snapshot.child("phnum").getValue();
                shopAddress = ""+ snapshot.child("address").getValue();
                String ProductImg = ""+ snapshot.child("profileImage").getValue();

                //set data
                shopNameTv.setText(shopName);
                emailTv.setText(shopEmail);
                phoneTv.setText(shopPhone);
                addressTv.setText(shopAddress);
                if (shopOpen.equals("true")){openTv.setText("Open");}
                else {openTv.setText("Closed");}
                try {
                    Picasso.get().load(ProductImg).into(shopImage);}
                catch (Exception e){Picasso.get().load(R.drawable.ic_profile).into(shopImage);}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}