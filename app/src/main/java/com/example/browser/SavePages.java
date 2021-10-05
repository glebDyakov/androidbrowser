package com.example.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SavePages extends AppCompatActivity {

    public SQLiteDatabase db;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savepages);

        TextView downBtn = findViewById(R.id.downBtn);
        downBtn.setText("<");
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavePages.this, MainActivity.class);
                SavePages.this.startActivity(intent);
            }
        });

        db = openOrCreateDatabase("bowser.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor savePages = db.rawQuery("Select * from savePages", null);
        savePages.moveToFirst();
        LinearLayout layoutOfSavePagesContent = findViewById(R.id.layoutOfSavePagesContent);
        if(DatabaseUtils.queryNumEntries(db, "savePages") >= 1) {
            while (true) {
                LinearLayout savePagesLayout = new LinearLayout(SavePages.this);
                savePagesLayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        Log.d("mytag", "открываем меню удаления сохранённых страниц");
                    }
                });
                ImageView savePageImg = new ImageView(SavePages.this);
                savePageImg.setLayoutParams(new ConstraintLayout.LayoutParams(175, 175));
                savePagesLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 175));
                savePageImg.setImageResource(R.drawable.star);
                TextView historyRecordTitle = new TextView(SavePages.this);
                historyRecordTitle.setText(savePages.getString(1));
                TextView historyRecordUrl = new TextView(SavePages.this);
                historyRecordUrl.setText(savePages.getString(2));
                savePagesLayout.addView(savePageImg);
                savePagesLayout.addView(historyRecordTitle);
                savePagesLayout.addView(historyRecordUrl);
                layoutOfSavePagesContent.addView(savePagesLayout);
                if (!savePages.moveToNext()) {
                    break;
                }
            }
        } else {
            LinearLayout savePagesLayout = new LinearLayout(SavePages.this);
            savePagesLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 175));
            TextView notFoundSavePages = new TextView(SavePages.this);
            notFoundSavePages.setText("Нет сохранённых страниц");
            layoutOfSavePagesContent.addView(savePagesLayout);
        }

    }

}
