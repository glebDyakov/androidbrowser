package com.example.browser;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

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

        TextView tabAddBtn = findViewById(R.id.tabAddBtn);
        tabAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView dotDotDot = findViewById(R.id.dotDotDot);
        dotDotDot.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem clearTabsBtn = menu.add(Menu.NONE, 301, Menu.NONE, "Закрыть все вкладки");
                clearTabsBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        db.execSQL("DELETE FROM tabs;");
                        layoutOfTabs.removeAllViews();
                        return false;
                    }
                });
            }
        });
    }
}
