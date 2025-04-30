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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

public class SAddProduct extends Fragment {

    private RelativeLayout Rlayout2;
    private ImageView productImg;
    private EditText Pname,Pdescription;
    private TextView Pcategory,Pprice,Pquantity;
    private Button addproductbtn;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri mImageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_add_product, container, false);

        //define
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //checkuser();

        //define
        Rlayout2 = view.findViewById(R.id.Rlayout2);
        productImg = view.findViewById(R.id.productImg);
        Pname = view.findViewById(R.id.Pname);
        Pdescription = view.findViewById(R.id.Pdescription);
        Pcategory = view.findViewById(R.id.Pcategory);
        Pprice = view.findViewById(R.id.Pprice);
        Pquantity = view.findViewById(R.id.Pquantity);
        addproductbtn = view.findViewById(R.id.addproductbtn);


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
        addproductbtn.setOnClickListener(new View.OnClickListener() {
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
        addProduct();

    }

    private void addProduct() {
        progressDialog.setMessage("Adding product...");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();
        if (mImageUri == null){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("productId",""+timestamp);
            hashMap.put("ProductName",""+ProductName);
            hashMap.put("ProductDescription",""+ProductDescription);
            hashMap.put("ProductCategory",""+ProductCategory);
            hashMap.put("ProductQuantity",""+ProductQuantity);
            hashMap.put("ProductPrice",""+ProductPrice);
            hashMap.put("ProductImage","");//no image
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("uid",""+firebaseAuth.getUid());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Successfully Added!",Toast.LENGTH_SHORT).show();

                            clearData();
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
            String filePathAndName = "product_images/"+""+timestamp;
            //upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("productId",""+timestamp);
                                hashMap.put("ProductName",""+ProductName);
                                hashMap.put("ProductDescription",""+ProductDescription);
                                hashMap.put("ProductCategory",""+ProductCategory);
                                hashMap.put("ProductQuantity",""+ProductQuantity);
                                hashMap.put("ProductPrice",""+ProductPrice);
                                hashMap.put("ProductImage",""+downloadImageUri);
                                hashMap.put("timestamp",""+timestamp);
                                hashMap.put("uid",""+firebaseAuth.getUid());

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                databaseReference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(),"Successfully Added!",Toast.LENGTH_SHORT).show();

                                                clearData();
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
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void clearData() {
        Pname.setText("");
        Pdescription.setText("");
        Pprice.setText("");
        Pquantity.setText("");
        Pcategory.setText("");
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