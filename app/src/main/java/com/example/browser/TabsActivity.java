package com.example.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
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
        LinearLayout layoutOfTabs = findViewById(R.id.layoutOfTabs);
        if(DatabaseUtils.queryNumEntries(db, "tabs") >= 1) {
            for (int tabIdx = 0; tabIdx < DatabaseUtils.queryNumEntries(db, "tabs"); tabIdx++) {
                LinearLayout tabLayout = new LinearLayout(TabsActivity.this);
                ImageView tabImg = new ImageView(TabsActivity.this);
                tabImg.setImageResource(R.drawable.star);
                TextView tabTitle = new TextView(TabsActivity.this);
                tabTitle.setText(tabs.getString(0));
                TextView tabUrl = new TextView(TabsActivity.this);
                tabUrl.setText(tabs.getString(1));
                tabLayout.addView(tabImg);
                tabLayout.addView(tabTitle);
                tabLayout.addView(tabUrl);
                layoutOfTabs.addView(tabLayout);
                if (tabIdx < DatabaseUtils.queryNumEntries(db, "tabs") - 1) {
                    tabs.moveToNext();
                }
            }
        } else {
            LinearLayout tabLayout = new LinearLayout(TabsActivity.this);
            TextView notFoundOfTabs = new TextView(TabsActivity.this);
            notFoundOfTabs.setText("Вкладок Нет");
            layoutOfTabs.addView(tabLayout);
        }

        TextView tabAddBtn = findViewById(R.id.tabAddBtn);
        tabAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("INSERT INTO \"tabs\"(title, url) VALUES (\"" + "Google" + "\", \"" + "https://google.com" + "\");");
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

        ImageButton exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TabsActivity.this, MainActivity.class);
                TabsActivity.this.startActivity(intent);
            }
        });

    }
}
