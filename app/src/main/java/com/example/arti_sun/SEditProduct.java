package com.example.arti_sun;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

public class SEditProduct extends Fragment {
    private  String productId;
    private RelativeLayout Rlayout2;
    private ImageView productImg;
    private EditText Pname,Pdescription;
    private TextView Pcategory,Pprice,Pquantity;
    private Button updateproductbtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri mImageUri;
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.fragment_s_edit_product, container, false);



         productId = getArguments().getString("productId");


         //define
         firebaseAuth = FirebaseAuth.getInstance();
         progressDialog = new ProgressDialog(getActivity());
         progressDialog.setTitle("Please Wait");
         progressDialog.setCanceledOnTouchOutside(false);

         //load details
         loadproduct();

         Rlayout2 = view.findViewById(R.id.Rlayout2);
         productImg = view.findViewById(R.id.productImg);
         Pname = view.findViewById(R.id.Pname);
         Pdescription = view.findViewById(R.id.Pdescription);
         Pcategory = view.findViewById(R.id.Pcategory);
         Pprice = view.findViewById(R.id.Pprice);
         Pquantity = view.findViewById(R.id.Pquantity);
         updateproductbtn = view.findViewById(R.id.saveproductbtn);





         //back
         Rlayout2.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Fragment fragment = new SProduct();
                 FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                 fragmentTransaction.replace(R.id.container,fragment).commit();
             }
         });
         //catogory
         Pcategory.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 categoryDialog();
             }
         });

         //add product
         updateproductbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 inputData();
             }
         });


         //choose product image
         productImg.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent takePictureIntent = new Intent(Intent.ACTION_PICK);
                 takePictureIntent.setType("image/*");
                 startActivityForResult(takePictureIntent,1);
             }
         });


         return view;

    }

    private void loadproduct() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String productId = ""+snapshot.child("productId").getValue();
                        String ProductName = ""+snapshot.child("ProductName").getValue();
                        String ProductDescription = ""+snapshot.child("ProductDescription").getValue();
                        String ProductCategory = ""+snapshot.child("ProductCategory").getValue();
                        String ProductQuantity = ""+snapshot.child("ProductQuantity").getValue();
                        String ProductPrice = ""+snapshot.child("ProductPrice").getValue();
                        String ProductImage = ""+snapshot.child("ProductImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();



                        //productImg = findViewById(R.id.productImg);
                        Pname.setText(ProductName);
                        Pdescription.setText(ProductDescription);
                        Pcategory.setText(ProductCategory);
                        Pprice.setText(ProductPrice);
                        Pquantity.setText(ProductQuantity);

                        try {
                            Picasso.get().load(ProductImage).into(productImg);
                        }catch (Exception e){
                            Picasso.get().load(R.drawable.ic_image).into(productImg);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String ProductName, ProductDescription, ProductCategory, ProductQuantity,ProductPrice;

    //validate
    private void inputData() {
        ProductName = Pname.getText().toString().trim();
        ProductDescription = Pdescription.getText().toString().trim();
        ProductCategory = Pcategory.getText().toString().trim();
        ProductQuantity = Pquantity.getText().toString().trim();
        ProductPrice = Pprice.getText().toString().trim();

        if(TextUtils.isEmpty(ProductName)){
            Toast.makeText(getActivity(),"Please Enter The Product Name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(ProductDescription)){
            Toast.makeText(getActivity(),"Please Enter The Product Description",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(ProductCategory)){
            Toast.makeText(getActivity(),"Please Choose a Category",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(ProductQuantity)){
            Toast.makeText(getActivity(),"Please Enter The Product Quantity",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(ProductPrice)){
            Toast.makeText(getActivity(),"Please Enter The Product Price",Toast.LENGTH_SHORT).show();
            return;
        }
        UpdateProduct();

    }

    private void UpdateProduct() {
        progressDialog.setMessage("Updating...");
        progressDialog.show();
        if (mImageUri == null){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ProductName",""+ProductName);
            hashMap.put("ProductDescription",""+ProductDescription);
            hashMap.put("ProductCategory",""+ProductCategory);
            hashMap.put("ProductQuantity",""+ProductQuantity);
            hashMap.put("ProductPrice",""+ProductPrice);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            //updated
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Updated",Toast.LENGTH_SHORT).show();
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
        else {


            String filePathAndName = "ProductImage/"+""+productId;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloasImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("ProductName",""+ProductName);
                                hashMap.put("ProductDescription",""+ProductDescription);
                                hashMap.put("ProductCategory",""+ProductCategory);
                                hashMap.put("ProductQuantity",""+ProductQuantity);
                                hashMap.put("ProductPrice",""+ProductPrice);
                                hashMap.put("ProductImage",""+downloasImageUri);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Products").child(productId)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                //updated
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(),"Updated",Toast.LENGTH_SHORT).show();
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

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    //category
    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Product Category").setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String category = Constants.productCategories[which];
                        Pcategory.setText(category);
                    }
                })
                .show();
    }

    //want to choose image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            imagev(mImageUri, requireContext().getContentResolver());
        }
    }

    //choose image from device
    private void imagev(Uri mImageUri, ContentResolver contentResolver) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        productImg.setImageBitmap(bitmap);
    }








}