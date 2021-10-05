package com.example.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
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

import org.w3c.dom.Text;

public class BookmarksActivity extends AppCompatActivity {

    public SQLiteDatabase db;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookmarksActivity.this, MainActivity.class);
                BookmarksActivity.this.startActivity(intent);
            }
        });

        TextView tripleDot = findViewById(R.id.tripleDot);
        tripleDot.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(Menu.NONE, 101, Menu.NONE, "Блокировка рекламы");
                MenuItem findBookmarks = menu.add(Menu.NONE, 102, Menu.NONE, "Найти на странице");
                findBookmarks.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
            }
        });

        db = openOrCreateDatabase("bowser.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor bookmarks = db.rawQuery("Select * from bookmarks", null);
        bookmarks.moveToFirst();
        LinearLayout layoutOfBookmarks = findViewById(R.id.layoutOfBookmarks);
        LinearLayout scrollLayoutOfBookmarks = findViewById(R.id.scrollLayoutOfBookmarks);
        while (true) {
            LinearLayout bookmarkLayout = new LinearLayout(BookmarksActivity.this);
            bookmarkLayout.setContentDescription(bookmarks.getString(2));
            bookmarkLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BookmarksActivity.this, MainActivity.class);
                    intent.putExtra("urlfromhistory", v.getContentDescription().toString());
                    BookmarksActivity.this.startActivity(intent);
                }
            });
            ImageView bookmarkImg = new ImageView(BookmarksActivity.this);
            bookmarkImg.setImageResource(R.drawable.star);
            bookmarkImg.setLayoutParams(new ConstraintLayout.LayoutParams(175, 175));
            bookmarkLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 175));
            TextView bookmarkTitle = new TextView(BookmarksActivity.this);
            bookmarkTitle.setText(bookmarks.getString(1));
            TextView bookmarkUrl = new TextView(BookmarksActivity.this);
            bookmarkUrl.setText(bookmarks.getString(2));

//            layoutOfBookmarks.addView(bookmarkLayout);
            scrollLayoutOfBookmarks.addView(bookmarkLayout);

            bookmarkLayout.addView(bookmarkImg);
            bookmarkLayout.addView(bookmarkTitle);
            bookmarkLayout.addView(bookmarkUrl);
            if(!bookmarks.moveToNext()){
                break;
            }
        }
    }
}
