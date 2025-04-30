package com.example.arti_sun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

public class RegisterUserActivity extends AppCompatActivity {

    private EditText namehint,phonenumberhint, emailhint,passwordhint,passwordhint2,addresshint;
    private TextView registersellertxt;
    private Button registerbtn1;
    private ImageView profile;
    private RelativeLayout Rlayout2;
    private Uri ImagePath;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Rlayout2 = findViewById(R.id.Rlayout2);//back
        registersellertxt = findViewById(R.id.registersellertxt);
        namehint= findViewById(R.id.namehint);
        phonenumberhint = findViewById(R.id.phonenumberhint);
        registerbtn1 = findViewById(R.id.registerbtn1);
        profile = findViewById(R.id.profile);
        emailhint = findViewById(R.id.emailhint);
        passwordhint = findViewById(R.id.passwordhint);
        passwordhint2 = findViewById(R.id.passwordhint2);
        addresshint = findViewById(R.id.addresshint);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Connecting...");
        progressDialog.setCanceledOnTouchOutside(false);


        //change to register as seller
        registersellertxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterUserActivity.this, RegisterSellerActivity.class));
            }
        });

        //back to login page
        Rlayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterUserActivity.this, LoginActivity.class));
            }
        });

        //confirm register as seller
        //using function:saveFirebaseData,reqisterselleracc,inputData
        registerbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

        //choose and upload profile
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(Intent.ACTION_PICK);
                takePictureIntent.setType("image/*");
                startActivityForResult(takePictureIntent,1);
            }
        });

    }


    //account to choose image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data!=null){
            ImagePath = data.getData();
            imagev();
        }

    }

    //choose image from device
    private void imagev(){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),ImagePath);
        }catch (IOException e){
            e.printStackTrace();
        }
        profile.setImageBitmap(bitmap);

    }

    private String name,email,phnum,password,cpassword,address;

    //validate
    private void inputData(){
        name=namehint.getText().toString().trim();
        email=emailhint.getText().toString().trim();
        phnum=phonenumberhint.getText().toString().trim();
        password=passwordhint.getText().toString().trim();
        cpassword=passwordhint2.getText().toString().trim();
        address=addresshint.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please Enter Your User Name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(address)){
            Toast.makeText(this,"Please Enter Your Home Address",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email Address",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Your Email Address",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phnum)){
            Toast.makeText(this,"Please Enter Your Contact Number",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<8){
            Toast.makeText(this,"Password at least 8 characters!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(cpassword)){
            Toast.makeText(this,"Password at least 8 characters!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(cpassword)){
            Toast.makeText(this,"Please Enter Confirm Password",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(cpassword)){
            Toast.makeText(this,"Password doesn't match, pleas enter both correctly!",Toast.LENGTH_SHORT).show();
            return;
        }
        reqisteruseracc();

    }

    //register user account
    private void reqisteruseracc(){
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUserActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //save data to firebase
    private void saveFirebaseData(){
        progressDialog.setMessage("Successful!");
        String timestamp = ""+System.currentTimeMillis();

        if(ImagePath==null){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+email);
            hashMap.put("address",""+address);
            hashMap.put("name",""+name);
            hashMap.put("phnum",""+phnum);
            hashMap.put("password",""+password);
            hashMap.put("online","true");
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("role","user");
            hashMap.put("profileImage","");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterUserActivity.this, MUserActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterUserActivity.this, MUserActivity.class));
                            finish();
                        }
                    });
        }
        else {
            //save image with info
            String filePathAndName = "Profile_images/"+""+firebaseAuth.getUid();
            //upload image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(ImagePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+email);
                                hashMap.put("address",""+address);
                                hashMap.put("name",""+name);
                                hashMap.put("phnum",""+phnum);
                                hashMap.put("password",""+password);
                                hashMap.put("timestamp",""+timestamp);
                                hashMap.put("role","user");
                                hashMap.put("online","true");
                                hashMap.put("profileImage",""+downloadImageUri);//url of uploaded image

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterUserActivity.this, MUserActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterUserActivity.this, MUserActivity.class));
                                                finish();
                                            }
                                        });

                            }


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }
    }


}
