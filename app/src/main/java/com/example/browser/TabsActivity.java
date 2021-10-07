package com.example.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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

public class TabsActivity extends AppCompatActivity {

    public SQLiteDatabase db;
    public LinearLayout layoutOfTabs;
    public Typeface fontAwesome;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");

        db = openOrCreateDatabase("bowser.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        layoutOfTabs = findViewById(R.id.layoutOfTabs);

        drawTabs();

        TextView tabAddBtn = findViewById(R.id.tabAddBtn);
        tabAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("INSERT INTO \"tabs\"(title, url) VALUES (\"" + "Google" + "\", \"" + "https://google.com" + "\");");
                drawTabs();
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
                        LinearLayout tabLayout = new LinearLayout(TabsActivity.this);
                        TextView notFoundOfTabs = new TextView(TabsActivity.this);
                        notFoundOfTabs.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 175));
                        notFoundOfTabs.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        notFoundOfTabs.setText("Вкладок Нет");
                        tabLayout.addView(notFoundOfTabs);
                        layoutOfTabs.addView(tabLayout);
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

    public void drawTabs(){
        layoutOfTabs.removeAllViews();
        Cursor tabs = db.rawQuery("Select * from tabs", null);
        tabs.moveToFirst();
        if(DatabaseUtils.queryNumEntries(db, "tabs") >= 1) {
            for (int tabIdx = 0; tabIdx < DatabaseUtils.queryNumEntries(db, "tabs"); tabIdx++) {
                LinearLayout tabLayout = new LinearLayout(TabsActivity.this);
                tabLayout.setBackgroundColor(Color.rgb(225, 225, 225));
                tabLayout.setContentDescription(String.valueOf(tabs.getInt(0)));
//                tabLayout.setContentDescription(String.valueOf(tabs.getString(2)));
                tabLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TabsActivity.this, MainActivity.class);
//                        intent.putExtra("tabId", v.getContentDescription());
//                        intent.putExtra("urlfromhistory", v.getContentDescription());
                        db.execSQL("UPDATE \"usercache\" SET value = " + Integer.valueOf(v.getContentDescription().toString()) + " WHERE name = \"currenttab\";");
                        TabsActivity.this.startActivity(intent);
                    }
                });
                ConstraintLayout.LayoutParams tabLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 175);
                tabLayoutParams.setMargins(0, 50, 0, 50);
                tabLayout.setLayoutParams(tabLayoutParams);
                ImageView tabImg = new ImageView(TabsActivity.this);
                tabImg.setLayoutParams(new ConstraintLayout.LayoutParams(175, 175));
                tabImg.setImageResource(R.drawable.star);
                TextView tabTitle = new TextView(TabsActivity.this);
                tabTitle.setLayoutParams(new ConstraintLayout.LayoutParams(175, 175));
                tabTitle.setText(tabs.getString(1));
                TextView tabUrl = new TextView(TabsActivity.this);
                tabUrl.setLayoutParams(new ConstraintLayout.LayoutParams(925, 175));
                tabUrl.setText(tabs.getString(2));

                TextView removeTabImg = new TextView(TabsActivity.this);
                removeTabImg.setLayoutParams(new ConstraintLayout.LayoutParams(175, 175));
                removeTabImg.setText("*");
                removeTabImg.setTypeface(fontAwesome);
                removeTabImg.setTextSize(34f);
                removeTabImg.setTextColor(Color.rgb(255, 0, 0));
                removeTabImg.setContentDescription(String.valueOf(layoutOfTabs.getChildCount()));

//                ImageView removeTabImg = new ImageView(TabsActivity.this);
//                removeTabImg.setLayoutParams(new ConstraintLayout.LayoutParams(175, 175));
//                removeTabImg.setImageResource(R.drawable.star);
//                removeTabImg.setContentDescription(String.valueOf(layoutOfTabs.getChildCount()));

                removeTabImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("mytag", "удаление вкладки: " + layoutOfTabs.getChildAt(Integer.valueOf(v.getContentDescription().toString())).getContentDescription().toString());
                        Log.d("mytag", "индекс вкладки: " + removeTabImg.getContentDescription().toString());
                        db.execSQL("DELETE FROM \"tabs\" WHERE _id = " + Integer.valueOf(layoutOfTabs.getChildAt(Integer.valueOf(v.getContentDescription().toString())).getContentDescription().toString()) + ";");
                        if(tabs.moveToFirst()) {
                            db.execSQL("UPDATE \"usercache\" SET value = " + tabs.getInt(0) + " WHERE name = \"currenttab\";");
                        }
                        layoutOfTabs.removeViewAt(Integer.valueOf(v.getContentDescription().toString()));
//                        layoutOfTabs.removeViewAt(Integer.valueOf(removeTabImg.getContentDescription().toString()));
                        if(String.valueOf(layoutOfTabs.getChildCount()).contains("0")){
                            drawTabs();
//                            LinearLayout tabLayout = new LinearLayout(TabsActivity.this);
//                            TextView notFoundOfTabs = new TextView(TabsActivity.this);
//                            notFoundOfTabs.setText("Вкладок Нет");
//                            tabLayout.addView(notFoundOfTabs);
//                            layoutOfTabs.addView(tabLayout);
                        }
                    }
                });
                tabLayout.addView(tabImg);
                tabLayout.addView(tabTitle);
                tabLayout.addView(tabUrl);
                tabLayout.addView(removeTabImg);
                layoutOfTabs.addView(tabLayout);
                if (tabIdx < DatabaseUtils.queryNumEntries(db, "tabs") - 1) {
                    tabs.moveToNext();
                }
            }
        } else {
            LinearLayout tabLayout = new LinearLayout(TabsActivity.this);
            TextView notFoundOfTabs = new TextView(TabsActivity.this);
            notFoundOfTabs.setText("Вкладок Нет");
            tabLayout.addView(notFoundOfTabs);
            layoutOfTabs.addView(tabLayout);
        }
    }

}
