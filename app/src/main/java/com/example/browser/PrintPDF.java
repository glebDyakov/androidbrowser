package com.example.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PrintPDF extends AppCompatActivity {

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printpdf);

//        TextView returnBtn = findViewById(R.id.returnBtn);
//        returnBtn.setText("<");
//        returnBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PrintPDF.this, MainActivity.class);
//                PrintPDF.this.startActivity(intent);
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    }
