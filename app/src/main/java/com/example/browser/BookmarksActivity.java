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
import androidx.constraintlayout.widget.ConstraintLayout;

public class BookmarksActivity extends AppCompatActivity {

    public SQLiteDatabase db;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        db = openOrCreateDatabase("bowser.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor bookmarks = db.rawQuery("Select * from bookmarks", null);
        bookmarks.moveToFirst();
        LinearLayout layoutOfBookmarks = findViewById(R.id.layoutOfBookmarks);
        for (int bookmarkIdx = 0; bookmarkIdx < DatabaseUtils.queryNumEntries(db, "bookmarks"); bookmarkIdx++) {
            LinearLayout bookmarkLayout = new LinearLayout(BookmarksActivity.this);
            ImageView bookmarkImg = new ImageView(BookmarksActivity.this);
            bookmarkImg.setImageResource(R.drawable.star);
            bookmarkImg.setLayoutParams(new ConstraintLayout.LayoutParams(75, 75));
            layoutOfBookmarks.setLayoutParams(new ConstraintLayout.LayoutParams(75, 75));
            TextView bookmarkTitle = new TextView(BookmarksActivity.this);
            bookmarkTitle.setText(bookmarks.getString(0));
            TextView bookmarkUrl = new TextView(BookmarksActivity.this);
            bookmarkUrl.setText(bookmarks.getString(1));
            bookmarkLayout.addView(bookmarkImg);
            bookmarkLayout.addView(bookmarkTitle);
            bookmarkLayout.addView(bookmarkUrl);
            layoutOfBookmarks.addView(bookmarkLayout);
            if (bookmarkIdx < DatabaseUtils.queryNumEntries(db, "bookmarks") - 1) {
                bookmarks.moveToNext();
            }
        }
    }
}
