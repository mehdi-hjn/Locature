package com.example.mehdih.locature_01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategorieActivity extends AppCompatActivity {

    private ImageView citadine, berling, sport, limousine, suv, camion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_categorie);

        citadine = (ImageView) findViewById(R.id.citadine);
        berling = (ImageView) findViewById(R.id.berling);
        sport = (ImageView) findViewById(R.id.sport);
        limousine = (ImageView) findViewById(R.id.limousine);
        camion = (ImageView) findViewById(R.id.camion);
        suv = (ImageView) findViewById(R.id.quatre_quatre);



        citadine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategorieActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category", "citadine");
                startActivity(intent);
            }
        });



        suv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategorieActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category", "suv");
                startActivity(intent);
            }
        });

        berling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategorieActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category", "berling");
                startActivity(intent);
            }
        });

        camion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategorieActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category", "camion");
                startActivity(intent);
            }
        });

        limousine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategorieActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category", "limousine");
                startActivity(intent);
            }
        });

        sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategorieActivity.this,AdminAddNewProductActivity.class);
                intent.putExtra("category", "sport");
                startActivity(intent);
            }
        });






    }
}
