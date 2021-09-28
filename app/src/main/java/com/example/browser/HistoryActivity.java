package com.example.browser;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        for (int historyRecordIdx = 0; historyRecordIdx < DatabaseUtils.queryNumEntries(db, "history"); historyRecordIdx++) {
            LinearLayout bookmarkLayout = new LinearLayout(HistoryActivity.this);
            ImageView bookmarkImg = new ImageView(HistoryActivity.this);
            bookmarkImg.setImageResource(R.drawable.star);
            TextView bookmarkTitle = new TextView(HistoryActivity.this);
            bookmarkTitle.setText(historyRecords.getString(0));
            TextView bookmarkUrl = new TextView(HistoryActivity.this);
            bookmarkUrl.setText(historyRecords.getString(1));
            bookmarkLayout.addView(bookmarkImg);
            bookmarkLayout.addView(bookmarkTitle);
            bookmarkLayout.addView(bookmarkUrl);
            layoutOfHistoryRecords.addView(bookmarkLayout);
            if (historyRecordIdx < DatabaseUtils.queryNumEntries(db, "history") - 1) {
                historyRecords.moveToNext();
            }
        }
    }
}
