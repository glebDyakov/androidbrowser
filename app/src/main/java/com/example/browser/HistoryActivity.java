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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        Log.d("mytag", "дата: " + String.valueOf(new Date().before(new Date(Integer.valueOf(historyRecords.getString(4).split("/")[0]), Integer.valueOf(historyRecords.getString(4).split("/")[1]), Integer.valueOf(historyRecords.getString(4).split("/")[2])))));

        if(DatabaseUtils.queryNumEntries(db, "history") >= 1) {
            LinearLayout historyRecordLayout = new LinearLayout(HistoryActivity.this);
            historyRecordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("mytag", "переключаю сегодня");
                }
            });

            TextView historyRecordFilter = new TextView(HistoryActivity.this);
            historyRecordFilter.setText("Сегодня");
            historyRecordLayout.addView(historyRecordFilter);
            scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
            while (true) {
                historyRecordLayout = new LinearLayout(HistoryActivity.this);
                historyRecordLayout.setContentDescription(historyRecords.getString(2));
                historyRecordLayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                                menu.add(Menu.NONE, 601, Menu.NONE, "удаление и поделиться");
                        historyRecords.moveToFirst();
                        String historyDate = historyRecords.getString(4);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        menu.add(Menu.NONE, 601, Menu.NONE, String.valueOf(historyDate.split("/")[0].toString()) + "/" + String.valueOf(String.valueOf(historyDate.split("/")[1].toString())) + "/" + String.valueOf(historyDate.split("/")[2].toString()));

//                        int result = 0;
//                        try {
//                            result = new Date().compareTo(sdf.parse(historyRecords.getString(4).toString()));
//                            menu.add(Menu.NONE, 601, Menu.NONE, String.valueOf(result));
//                            Log.d("mytag", "date compare to:" + String.valueOf(result));
//                        } catch (ParseException e) {
//                            menu.add(Menu.NONE, 601, Menu.NONE, "ОШИБКА");
//                            Log.d("mytag", "ошибка парсинга даты");
//                        }

                    }
                });
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
                CheckBox checkbox = new CheckBox(HistoryActivity.this);
                ImageView historyRecordImg = new ImageView(HistoryActivity.this);
                historyRecordImg.setLayoutParams(new ConstraintLayout.LayoutParams(175, 175));
                historyRecordLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 175));

//                historyRecordImg.setImageResource(R.drawable.star);
                Bitmap uploadedImg = null;
                try {
                    uploadedImg = new FetchTask().execute(historyRecords.getString(3)).get();
                } catch (Exception e) {
                    Log.d("mytag", "Не могу обратиться к картинке");
                }
                historyRecordImg.setImageBitmap(uploadedImg);

                TextView historyRecordTitle = new TextView(HistoryActivity.this);
                historyRecordTitle.setText(historyRecords.getString(1));
                TextView historyRecordUrl = new TextView(HistoryActivity.this);
                historyRecordUrl.setText(historyRecords.getString(2));
                historyRecordLayout.addView(checkbox);
                historyRecordLayout.addView(historyRecordImg);
                historyRecordLayout.addView(historyRecordTitle);
                historyRecordLayout.addView(historyRecordUrl);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    String historyDate = historyRecords.getString(4).toString();
                    Date currentDate = sdf.parse(historyDate);
                    int result = new Date().compareTo(currentDate);
                    if (result == 0) {
//                        System.out.println("Date1 is equal to Date2");
                    } else if (result > 0) {
//                        System.out.println("Date1 is after Date2");
                        scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
//                        layoutOfHistoryRecords.addView(historyRecordLayout);
                    } else if (result < 0) {
//                        System.out.println("Date1 is before Date2");
                    }
                } catch (ParseException e) {
                    Log.d("mytag", "ошибка парсинга даты");
                }

                if (!historyRecords.moveToNext()) {
                    break;
                }

            }
            historyRecordLayout = new LinearLayout(HistoryActivity.this);
            historyRecordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("mytag", "переключаю сегодня");
                }
            });
            Spinner today = findViewById(R.id.today);
            today.setAdapter(new ArrayAdapter<>(HistoryActivity.this, R.layout.dialog_fontsize));
            historyRecordFilter = new TextView(HistoryActivity.this);
            historyRecordFilter.setText("Вчера");
            historyRecordLayout.addView(historyRecordFilter);
            scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
            historyRecords.moveToFirst();
            while (true) {
                historyRecordLayout = new LinearLayout(HistoryActivity.this);
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
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    String historyDate = historyRecords.getString(4).toString();
                    Date currentDate = sdf.parse(historyDate);
//                    int result = sdf.parse(String.valueOf(currentDate.getYear()) + "/" + String.valueOf(currentDate.getMonth() - 1) + "/" + String.valueOf(currentDate.getDate())).compareTo(currentDate);
//                    int result = sdf.parse(String.valueOf(historyDate.split("/")[2].toString()) + "/" + String.valueOf(String.valueOf(Integer.valueOf(historyDate.split("/")[1].toString()) - 1)) + "/" + String.valueOf(historyDate.split("/")[0].toString())).compareTo(currentDate);
//                    int result = sdf.parse(String.valueOf(currentDate.getYear()) + "/" + String.valueOf(currentDate.getMonth()) + "/" + String.valueOf(currentDate.getDate())).compareTo(currentDate);
                    int result = sdf.parse(String.valueOf(historyDate.split("/")[0].toString()) + "/" + String.valueOf(String.valueOf(historyDate.split("/")[1].toString())) + "/" + String.valueOf(historyDate.split("/")[2].toString())).compareTo(currentDate);
//                    int result = new Date().compareTo(currentDate);

                    if (result == 0) {

                    } else if (result > 0) {
                        scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
                    } else if (result < 0) {

                    }
                } catch (ParseException e) {
                    Log.d("mytag", "ошибка парсинга даты");
                }
                if (!historyRecords.moveToNext()) {
                    break;
                }
            }

            historyRecordLayout = new LinearLayout(HistoryActivity.this);
            historyRecordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("mytag", "переключаю сегодня");
                }
            });

            historyRecordFilter = new TextView(HistoryActivity.this);
            historyRecordFilter.setText("За послдение 7 дня");
            historyRecordLayout.addView(historyRecordFilter);
            scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
            historyRecords.moveToFirst();
            while (true) {
                historyRecordLayout = new LinearLayout(HistoryActivity.this);
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
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    String historyDate = historyRecords.getString(4).toString();
                    Date currentDate = sdf.parse(historyDate);
                    int result = sdf.parse(String.valueOf(currentDate.getYear()) + "/" + String.valueOf(currentDate.getMonth()) + "/" + String.valueOf(currentDate.getDate())).compareTo(currentDate);
                    if (result == 0) {

                    } else if (result > 0) {
                        scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
                    } else if (result < 0) {

                    }
                } catch (ParseException e) {
                    Log.d("mytag", "ошибка парсинга даты");
                }
                if (!historyRecords.moveToNext()) {
                    break;
                }

            }
            historyRecordLayout = new LinearLayout(HistoryActivity.this);
            historyRecordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("mytag", "переключаю сегодня");
                }
            });

            historyRecordFilter = new TextView(HistoryActivity.this);
            historyRecordFilter.setText("Прошлый месяц");
            historyRecordLayout.addView(historyRecordFilter);
            scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
            historyRecords.moveToFirst();
            while (true) {
                historyRecordLayout = new LinearLayout(HistoryActivity.this);
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
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    String historyDate = historyRecords.getString(4).toString();
                    Date currentDate = sdf.parse(historyDate);
                    int result = sdf.parse(String.valueOf(currentDate.getYear()) + "/" + String.valueOf(currentDate.getMonth() - 1) + "/" + String.valueOf(currentDate.getDate())).compareTo(currentDate);
                    if (result == 0) {

                    } else if (result > 0) {
                        scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
                    } else if (result < 0) {

                    }
                } catch (ParseException e) {
                    Log.d("mytag", "ошибка парсинга даты");
                }
                if (!historyRecords.moveToNext()) {
                    break;
                }
            }
            historyRecordLayout = new LinearLayout(HistoryActivity.this);
            historyRecordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("mytag", "переключаю сегодня");
                }
            });
            historyRecordFilter = new TextView(HistoryActivity.this);
            historyRecordFilter.setText("Старые");
            historyRecordLayout.addView(historyRecordFilter);
            scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
            historyRecords.moveToFirst();
            while (true) {
                historyRecordLayout = new LinearLayout(HistoryActivity.this);
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
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    String historyDate = historyRecords.getString(4).toString();
                    Date currentDate = sdf.parse(historyDate);
                    int result = sdf.parse(String.valueOf(currentDate.getYear() - 1) + "/" + String.valueOf(currentDate.getMonth()) + "/" + String.valueOf(currentDate.getDate())).compareTo(currentDate);
                    if (result == 0) {

                    } else if (result > 0) {
                        scrollLayoutOfHistoryRecords.addView(historyRecordLayout);
                    } else if (result < 0) {

                    }
                } catch (ParseException e) {
                    Log.d("mytag", "ошибка парсинга даты");
                }
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
