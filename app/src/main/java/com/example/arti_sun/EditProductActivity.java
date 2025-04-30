package com.example.arti_sun;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class EditProductActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        productId = getIntent().getStringExtra("productId");

        //define
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //load details
        loadproduct();

        Rlayout2 = findViewById(R.id.Rlayout2);
        productImg = findViewById(R.id.productImg);
        Pname = findViewById(R.id.Pname);
        Pdescription = findViewById(R.id.Pdescription);
        Pcategory = findViewById(R.id.Pcategory);
        Pprice = findViewById(R.id.Pprice);
        Pquantity = findViewById(R.id.Pquantity);
        updateproductbtn = findViewById(R.id.saveproductbtn);




        Rlayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
            Toast.makeText(this,"Please Enter The Product Name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(ProductDescription)){
            Toast.makeText(this,"Please Enter The Product Description",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(ProductCategory)){
            Toast.makeText(this,"Please Choose a Category",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(ProductQuantity)){
            Toast.makeText(this,"Please Enter The Product Quantity",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(ProductPrice)){
            Toast.makeText(this,"Please Enter The Product Price",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(EditProductActivity.this,"Updated",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(EditProductActivity.this,"Updated",Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    //category
    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category").setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String category = Constants.productCategories[which];
                        Pcategory.setText(category);
                    }
                })
                .show();
    }

    //account to choose image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data!=null){
            mImageUri = data.getData();
            imagev();
        }

    }

    //choose image from device
    private void imagev(){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mImageUri);
        }catch (IOException e){
            e.printStackTrace();
        }
        productImg.setImageBitmap(bitmap);

    }












}