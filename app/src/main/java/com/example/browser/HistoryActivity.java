package com.example.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.concurrent.ExecutionException;

public class HistoryActivity extends AppCompatActivity {

    public SQLiteDatabase db;
    Typeface fontAwesome;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");

        db = openOrCreateDatabase("bowser.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor historyRecords = db.rawQuery("Select * from history", null);
        historyRecords.moveToFirst();
//        LinearLayout layoutOfHistoryRecords = findViewById(R.id.layoutOfHistoryRecords);
        ScrollView scrollOfHistoryRecords = findViewById(R.id.scrollOfHistoryRecords);
        LinearLayout scrollLayoutOfHistoryRecords = findViewById(R.id.scrollLayoutOfHistoryRecords);
        if(DatabaseUtils.queryNumEntries(db, "history") >= 1) {
            while (true) {
                LinearLayout historyRecordLayout = new LinearLayout(HistoryActivity.this);
                historyRecordLayout.setContentDescription(historyRecords.getString(2));
                historyRecordLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                        intent.putExtra("urlfromhistory", v.getContentDescription().toString());
//                        intent.putExtra("urlfromhistory", String.valueOf(historyRecords.getString(1)));
//                        intent.putExtra("urlfromhistory", "abc");
                        HistoryActivity.this.startActivity(intent);
                    }
                });
                ImageView historyRecordImg = new ImageView(HistoryActivity.this);
                historyRecordImg.setLayoutParams(new ConstraintLayout.LayoutParams(175, 175));
                historyRecordLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 175));

//                historyRecordImg.setImageResource(R.drawable.star);
                Bitmap uploadedImg = null;
                try {
                    uploadedImg = new FetchTask().execute(historyRecords.getString(3)).get();
                } catch (ExecutionException e) {
                    Log.d("mytag", "Не могу обратиться к картинке");
                } catch (InterruptedException e) {
                    Log.d("mytag", "Не могу обратиться к картинке");
                }
                historyRecordImg.setImageBitmap(uploadedImg);

                TextView historyRecordTitle = new TextView(HistoryActivity.this);
                historyRecordTitle.setText(historyRecords.getString(1));
                TextView historyRecordUrl = new TextView(HistoryActivity.this);
                historyRecordUrl.setText(historyRecords.getString(2));
                historyRecordLayout.addView(historyRecordImg);
                historyRecordLayout.addView(historyRecordTitle);
                historyRecordLayout.addView(historyRecordUrl);
                scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
//                layoutOfHistoryRecords.addView(historyRecordLayout);
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
//                        layoutOfHistoryRecords.removeAllViews();
                        scrollLayoutOfHistoryRecords.removeAllViews();
                        return false;
                    }
                });
            }
        });

//        ImageButton cancelBtn = findViewById(R.id.cancelBtn);
        TextView cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setTextSize(34f);
        cancelBtn.setTypeface(fontAwesome);
        cancelBtn.setText("<");
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                HistoryActivity.this.startActivity(intent);
            }
        });

        TextView findBtn = findViewById(R.id.findBtn);
//        historyLabel.setTextColor(Color.rgb(255, 0, 0));
        findBtn.setTextSize(34f);
        findBtn.setTypeface(fontAwesome);
//        historyLabel.setText("Š");
        findBtn.setText("s");

    }
}
