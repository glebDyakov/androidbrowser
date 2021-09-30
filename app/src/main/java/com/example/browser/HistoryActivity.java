package com.example.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HistoryActivity extends AppCompatActivity {

    public SQLiteDatabase db;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = openOrCreateDatabase("bowser.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor historyRecords = db.rawQuery("Select * from history", null);
        historyRecords.moveToFirst();
        LinearLayout layoutOfHistoryRecords = findViewById(R.id.layoutOfHistoryRecords);
        if(DatabaseUtils.queryNumEntries(db, "history") >= 1) {
            while (true) {
                LinearLayout historyRecordLayout = new LinearLayout(HistoryActivity.this);
                ImageView historyRecordImg = new ImageView(HistoryActivity.this);
                historyRecordImg.setLayoutParams(new ConstraintLayout.LayoutParams(175, 175));
                historyRecordLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 175));
                historyRecordImg.setImageResource(R.drawable.star);
                TextView historyRecordTitle = new TextView(HistoryActivity.this);
                historyRecordTitle.setText(historyRecords.getString(1));
                TextView historyRecordUrl = new TextView(HistoryActivity.this);
                historyRecordUrl.setText(historyRecords.getString(2));
                historyRecordLayout.addView(historyRecordImg);
                historyRecordLayout.addView(historyRecordTitle);
                historyRecordLayout.addView(historyRecordUrl);
                layoutOfHistoryRecords.addView(historyRecordLayout);
                if (!historyRecords.moveToNext()) {
                    break;
                }
            }
        }

        TextView threeDot = findViewById(R.id.threeDot);
        threeDot.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem clearHistoryBtn = menu.add(Menu.NONE, 201, Menu.NONE, "Очистить журнал");
                clearHistoryBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        db.execSQL("DELETE FROM history;");
                        layoutOfHistoryRecords.removeAllViews();
                        return false;
                    }
                });
            }
        });

        ImageButton cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                HistoryActivity.this.startActivity(intent);
            }
        });

        TextView historyLabel = findViewById(R.id.historyLabel);
//        Typeface fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
//        Typeface fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/otfs/fontawesome.otf");
        Typeface fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        historyLabel.setTextColor(Color.rgb(255, 0, 0));
        historyLabel.setTextSize(125f);
        historyLabel.setTypeface(fontAwesome);
        historyLabel.setText("\uF0C0");

    }
}
