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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class DownloadsActivity extends AppCompatActivity {

    public SQLiteDatabase db;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        db = openOrCreateDatabase("bowser.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor downloads = db.rawQuery("Select * from downloads", null);
        downloads.moveToFirst();
        LinearLayout layoutOfDownloads = findViewById(R.id.layoutOfDownloads);
        if(DatabaseUtils.queryNumEntries(db, "downloads") >= 1) {
            while (true) {
                LinearLayout downloadLayout = new LinearLayout(DownloadsActivity.this);
                downloadLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 175));

//                TextView downloadType = new TextView(DownloadsActivity.this);
                ImageView downloadType = new ImageView(DownloadsActivity.this);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 250);
                layoutParams.setMarginEnd(250);
                downloadType.setLayoutParams(layoutParams);

                TextView downloadUrl = new TextView(DownloadsActivity.this);

                downloadUrl.setLayoutParams(layoutParams);

                TextView downloadSize = new TextView(DownloadsActivity.this);

                downloadSize.setLayoutParams(layoutParams);

//                downloadType.setText(downloads.getString(4));
//                downloadType.setText(downloads.getString(3));
//                downloadType.setText("image");
                downloadType.setImageResource(R.drawable.star);
                downloadType.setScaleType(ImageView.ScaleType.FIT_START);

                if(downloads.getString(1).length() > 30){
                    downloadUrl.setText(downloads.getString(1).substring(0, 30));
                } else {
                    downloadUrl.setText(downloads.getString(1));
                }


//                downloadSize.setText(downloads.getString(5));
                downloadSize.setText(String.valueOf(downloads.getInt(2)));

                downloadLayout.addView(downloadType);
                downloadLayout.addView(downloadUrl);
                downloadLayout.addView(downloadSize);
                layoutOfDownloads.addView(downloadLayout);
                if(!downloads.moveToNext()){
                    break;
                }
            }
        } else {
            LinearLayout downloadLayout = new LinearLayout(DownloadsActivity.this);
            downloadLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 175));
            TextView notFoundDownloads = new TextView(DownloadsActivity.this);
            notFoundDownloads.setText("Нет загрузок...");
            downloadLayout.addView(notFoundDownloads);
            layoutOfDownloads.addView(downloadLayout);
        }

        TextView dotmenu = findViewById(R.id.dotmenu);
        dotmenu.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(Menu.NONE, 101, Menu.NONE, "Блокировка рекламы");
                MenuItem clearDownloadsBtn = menu.add(Menu.NONE, 102, Menu.NONE, "Удалить файлы");
                clearDownloadsBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        db.execSQL("DELETE FROM downloads");
                        return false;
                    }
                });
            }
        });

        ImageButton feedback = findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownloadsActivity.this, MainActivity.class);
                DownloadsActivity.this.startActivity(intent);
            }
        });

    }

}
