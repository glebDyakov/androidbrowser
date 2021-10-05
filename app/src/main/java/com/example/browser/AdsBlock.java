package com.example.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdsBlock extends AppCompatActivity {

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adsblock);

        TextView toMainPageBtn = findViewById(R.id.toMainPageBtn);
        toMainPageBtn.setText("<");
        toMainPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdsBlock.this, MainActivity.class);
                AdsBlock.this.startActivity(intent);
            }
        });

    }

}