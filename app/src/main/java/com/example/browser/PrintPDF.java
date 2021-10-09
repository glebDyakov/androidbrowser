package com.example.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.printservice.PrinterDiscoverySession;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class PrintPDF extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
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

//        PrintManager printManager = (PrintManager) getApplicationContext().getSystemService(Context.PRINT_SERVICE);
//        PrinterDiscoverySession mDiscoverySession = printManager.crea
//        OurDiscoveryListener discoveryListener = new OurDiscoveryListener(nsdManager);
//        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    }
