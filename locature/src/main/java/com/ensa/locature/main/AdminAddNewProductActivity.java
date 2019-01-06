package com.ensa.locature.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class AdminAddNewProductActivity extends AppCompatActivity {

    private  String CategoryName, Desc, Price, Pname, savecurrentdate, savecurrenttime;
    private Button AddNewProductButton;
    private EditText InputProductName, InputProductDesc,InputProductPrice;
    private ImageView InputProductImage;
    private static final int GalleryPick=1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;


    private  String productRandomKey, downloadImageUrl;
    private  StorageReference ProductImagesRef;

    private DatabaseReference ProductsRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        loadingBar = new ProgressDialog(this);

        CategoryName = getIntent().getExtras().get("category").toString();
     ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
     ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

     InputProductName = (EditText) findViewById(R.id.product_name);
     InputProductDesc = (EditText) findViewById(R.id.product_desc);
     InputProductPrice= (EditText) findViewById(R.id.product_price);
     InputProductImage = (ImageView) findViewById(R.id.select_product_image);

     AddNewProductButton = (Button) findViewById(R.id.add_new_product) ;

     InputProductImage.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             OpenGallery();
         }
     });

     AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateProductData();

            }
        });

    }

    private void OpenGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }


    }

    private void ValidateProductData(){
        Desc = InputProductDesc.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this, "Please enter an image!",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(Desc)){
            Toast.makeText(this, "Please enter a description!",Toast.LENGTH_SHORT).show();

        }else  if(TextUtils.isEmpty(Pname)){
            Toast.makeText(this, "Please enter a name",Toast.LENGTH_SHORT).show();

        }else  if(TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Please enter a price",Toast.LENGTH_SHORT).show();

        }else{

            StoreProductInfo();

        }

    }

    private  void StoreProductInfo(){

        loadingBar.setTitle("Adding new product");
        loadingBar.setMessage("Please wait while we are adding the new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        savecurrentdate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm,ss a");
        savecurrenttime = currentTime.format(calendar.getTime());

        productRandomKey = savecurrentdate + savecurrenttime ;

        final StorageReference filepath = ProductImagesRef.child(ImageUri.getLastPathSegment()+ productRandomKey);

        final UploadTask uploadTask = filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();


            }
        })  .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Image uploaded susccefuly", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageUrl = filepath.getDownloadUrl().toString();
                        return  filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this, "getting Product image url successfuly", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDB();
                        }
                    }
                });
            }
        });

        }

        private  void SaveProductInfoToDB(){

            HashMap<String,Object> productMap = new HashMap<>();
            productMap.put("pid",productRandomKey);
            productMap.put("date",savecurrentdate);
            productMap.put("time",savecurrenttime);
            productMap.put("image",downloadImageUrl);
            productMap.put("category",CategoryName);
            productMap.put("price",Price);
            productMap.put("pname",Pname);

            ProductsRef.child(productRandomKey).updateChildren(productMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Intent intent = new Intent(AdminAddNewProductActivity.this,AdminCategorieActivity.class );
                                startActivity(intent);

                                loadingBar.dismiss();
                                Toast.makeText(AdminAddNewProductActivity.this, "products added  successfuly", Toast.LENGTH_SHORT).show();

                            }else{
                                loadingBar.dismiss();
                                String msg = task.getException().toString();
                                Toast.makeText(AdminAddNewProductActivity.this, "error : "+msg , Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }


    }







