package com.example.browser;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.UnicodeSet;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    public SQLiteDatabase db;
    public Button rightArrowBtn;
    public Button leftArrowBtn;
    public View.OnClickListener rightArrowBtnListener;
    public View.OnClickListener leftArrowBtnListener;
    public boolean findControlsVisible = false;
    public Button findPreviousBtn;
    public Button findNextBtn;
    public EditText searchText;
    public Button hideFindingControlsBtn;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("bowser.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS history (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarks (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS tabs (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT);");

        WebView myWebView = (WebView) findViewById(R.id.htmlContent);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("https://google.com");

        EditText keywords = findViewById(R.id.keywords);
        ImageButton searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchKeywords = keywords.getText().toString();
//                try {
//                    InetAddress ip = InetAddress.getByName(searchKeywords);
//                    searchKeywords = keywords.getText().toString();
//                    Log.d("mytag", "ищу по хосту");
//                    myWebView.loadUrl(searchKeywords);
//                } catch (UnknownHostException e) {
//                    searchKeywords = "https://yandex.ru/search/?lr=10765&text=" + keywords.getText().toString();
//                    Log.d("mytag", "ищу по поиску");
//                    myWebView.loadUrl(searchKeywords);
//                }
                myWebView.loadUrl(searchKeywords);

                db.execSQL("INSERT INTO \"history\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");

                if(myWebView.canGoBack()){
                    leftArrowBtn.setTextColor(Color.rgb(0, 0, 0));
                }

            }
        });

        ImageButton burgerBtn = findViewById(R.id.burgerBtn);
        burgerBtn.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(Menu.NONE, 101, Menu.NONE, "Загрузки");
                MenuItem history = menu.add(Menu.NONE, 102, Menu.NONE, "Журнал");
                history.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                        MainActivity.this.startActivity(intent);
                        return false;
                    }
                });
                menu.add(Menu.NONE, 103, Menu.NONE, "Сохранённые страницы");
                menu.add(Menu.NONE, 104, Menu.NONE, "Добавить страницу");
                menu.add(Menu.NONE, 105, Menu.NONE, "Поделиться");
                menu.add(Menu.NONE, 106, Menu.NONE, "Режим затемнения");
                menu.add(Menu.NONE, 107, Menu.NONE, "Блокировка рекламы");
                MenuItem findOnPage = menu.add(Menu.NONE, 102, Menu.NONE, "Найти на странице");
                findOnPage.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        searchText.setVisibility(View.VISIBLE);
                        findPreviousBtn.setVisibility(View.VISIBLE);
                        findNextBtn.setVisibility(View.VISIBLE);
                        hideFindingControlsBtn.setVisibility(View.VISIBLE);

                        myWebView.findAllAsync("язык");
                        myWebView.findAllAsync(searchText.getText().toString());
                        return false;
                    }
                });
                menu.add(Menu.NONE, 108, Menu.NONE, "Версия для ПК");
                menu.add(Menu.NONE, 109, Menu.NONE, "Размер шрифта");
                menu.add(Menu.NONE, 110, Menu.NONE, "Дополнения");
                menu.add(Menu.NONE, 111, Menu.NONE, "Печать/PDF");
                menu.add(Menu.NONE, 112, Menu.NONE, "Настройки");
            }
         });

        ImageButton tabsBtn = findViewById(R.id.tabsBtn);
        tabsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TabsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        ImageButton bookmarkBtn = findViewById(R.id.bookmarkBtn);
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookmarksActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        ImageButton homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.loadUrl("https://google.com");

            }
        });

        ImageButton bookmarkAddBtn = findViewById(R.id.bookmarkAddBtn);
        bookmarkAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("INSERT INTO \"bookmarks\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");
            }
        });

        ImageButton refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.reload();
                Log.d("mytag", "обновить страницу");
            }
        });

        hideFindingControlsBtn = findViewById(R.id.hideFindingControlsBtn);
        hideFindingControlsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mytag", "cкрывай");
                findPreviousBtn.setVisibility(View.INVISIBLE);
                findNextBtn.setVisibility(View.INVISIBLE);
                searchText.setVisibility(View.INVISIBLE);
                hideFindingControlsBtn.setVisibility(View.INVISIBLE);
            }
        });

        findPreviousBtn = findViewById(R.id.findPreviousBtn);
        findPreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.findNext(false);
            }
        });

        findNextBtn = findViewById(R.id.findNextBtn);
        findNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.findNext(true);
            }
        });

        searchText = findViewById(R.id.searchText);

        hideFindingControlsBtn.setText("<");
        leftArrowBtn = findViewById(R.id.leftArrowBtn);
        rightArrowBtn = findViewById(R.id.rightArrowBtn);
        leftArrowBtn.setText("<");
        rightArrowBtn.setText(">");
        rightArrowBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                myWebView.goForward();
                if(!myWebView.canGoForward()){
                    rightArrowBtn.setTextColor(Color.rgb(215, 215, 215));
//                    rightArrowBtn.setOnClickListener(null);
                }
                if(myWebView.canGoBack()){
                    leftArrowBtn.setTextColor(Color.rgb(0, 0, 0));
//                    leftArrowBtn.setOnClickListener(null);
                }
            }
        };
        leftArrowBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.goBack();
                if(!myWebView.canGoBack()){
                    leftArrowBtn.setTextColor(Color.rgb(215, 215, 215));
//                    leftArrowBtn.setOnClickListener(null);
                }
                if(myWebView.canGoForward()){
                    rightArrowBtn.setTextColor(Color.rgb(0, 0, 0));
//                    rightArrowBtn.setOnClickListener(null);
                }
            }
        };

        rightArrowBtn.setOnClickListener(rightArrowBtnListener);
        leftArrowBtn.setOnClickListener(leftArrowBtnListener);

    }
}