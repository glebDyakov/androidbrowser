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

public class TabsActivity extends AppCompatActivity {

    public SQLiteDatabase db;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        db = openOrCreateDatabase("bowser.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor tabs = db.rawQuery("Select * from tabs", null);
        tabs.moveToFirst();
        LinearLayout layoutOfTabs = findViewById(R.id.layoutOfBookmarks);
        for (int tabIdx = 0; tabIdx < DatabaseUtils.queryNumEntries(db, "tabs"); tabIdx++) {
            LinearLayout bookmarkLayout = new LinearLayout(TabsActivity.this);
            ImageView bookmarkImg = new ImageView(TabsActivity.this);
            bookmarkImg.setImageResource(R.drawable.star);
            TextView bookmarkTitle = new TextView(TabsActivity.this);
            bookmarkTitle.setText(tabs.getString(0));
            TextView bookmarkUrl = new TextView(TabsActivity.this);
            bookmarkUrl.setText(tabs.getString(1));
            bookmarkLayout.addView(bookmarkImg);
            bookmarkLayout.addView(bookmarkTitle);
            bookmarkLayout.addView(bookmarkUrl);
            layoutOfTabs.addView(bookmarkLayout);
            if (tabIdx < DatabaseUtils.queryNumEntries(db, "tabs") - 1) {
                tabs.moveToNext();
            }
        }
    }
}
