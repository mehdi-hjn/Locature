package com.ensa.locature.main;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ensa.locature.main.Model.Users;
import com.ensa.locature.main.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText InputNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private  String parentDbName= "Users";
    private com.rey.material.widget.CheckBox RememberMe;

    private TextView AdminLink, notAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InputNumber = (EditText) findViewById(R.id.login_phone);
        InputPassword = (EditText) findViewById(R.id.login_password);
        LoginButton = (Button) findViewById(R.id.login_btn);
        loadingBar = new ProgressDialog(this);

        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        notAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);

        RememberMe = (com.rey.material.widget.CheckBox) findViewById(R.id.remember_me_box) ;
        Paper.init(this);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

         AdminLink.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 LoginButton.setText("Login Admin");
                 AdminLink.setVisibility(View.INVISIBLE);
                 notAdminLink.setVisibility(View.VISIBLE);
                 parentDbName = "Admins";
             }
         });

         notAdminLink.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 LoginButton.setText("Login");
                 AdminLink.setVisibility(View.VISIBLE);
                 notAdminLink.setVisibility(View.INVISIBLE);
                 parentDbName="Users";

             }
         });

    }


    private  void LoginUser(){
        String phone = InputNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking the crefentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccesToAccount(phone,password);
        }

    }

    private  void AllowAccesToAccount(final String phone, final String password){

        if(RememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);


        }


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(phone).exists()){

                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone)){
                        if(usersData.getPassword().equals(password)){
                            if(parentDbName.equals("Admins")){

                                Toast.makeText(LoginActivity.this,"Welcome admin, Logged in Successfuly !", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();
                               Intent intent = new Intent(LoginActivity.this, AdminCategorieActivity.class);
                               Prevalent.currentOnlineUser = usersData;
                               startActivity(intent);

                            }else if(parentDbName.equals("Users")){

                                Toast.makeText(LoginActivity.this,"Logged in Successfuly !", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();

                               // Changed Behaviour => Go to MapActivity as First Activity
                               Intent intent = new Intent(LoginActivity.this, MapActivity.class);

                               Prevalent.currentOnlineUser = usersData;
                               startActivity(intent);
                            }

                        }else{

                            Toast.makeText(LoginActivity.this,"Wrong Password !", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }

                    }

                }else{
                    Toast.makeText(LoginActivity.this,"Account with this " + phone + " number doesn't exists !", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
