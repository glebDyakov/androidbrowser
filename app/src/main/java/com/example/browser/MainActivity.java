package com.example.browser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Picture;
import android.icu.text.UnicodeSet;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public DownloadManager downloadManager;
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
//        db.execSQL("CREATE TABLE IF NOT EXISTS downloads (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, useragent TEXT, contentDisposition TEXT, mimetype TEXT, contentlength INTEGER);");
        db.execSQL("DROP TABLE IF EXISTS downloads");
        db.execSQL("CREATE TABLE IF NOT EXISTS downloads (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT);");

        WebView myWebView = (WebView) findViewById(R.id.htmlContent);
//        DetectedWebView myWebView = new DetectedWebView(MainActivity.this);

        Log.d("mytag", "node element: " + String.valueOf(myWebView.getHitTestResult().getType()).contains(String.valueOf(myWebView.getHitTestResult().IMAGE_TYPE)));
        myWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("mytag", "node element: " + String.valueOf(myWebView.getHitTestResult().getType()).contains(String.valueOf(myWebView.getHitTestResult().IMAGE_TYPE)));
            }
        });

//        myWebView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.d("mytag", "node element: " + String.valueOf(myWebView.getHitTestResult().getType()).contains(String.valueOf(myWebView.getHitTestResult().IMAGE_TYPE)));
//            }
//        });

//        myWebView.evaluateJavascript("document.body.onclick = function() { alert('click'); };\n" +
//                "document.body.onfocusout = function() { alert('focus'); };", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                Log.d("mytag", "focusChange: " + value);
//            }
//        });

        myWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("mytag", "node element: " + String.valueOf(myWebView.getHitTestResult().getType()).contains(String.valueOf(myWebView.getHitTestResult().IMAGE_TYPE)));
                if(String.valueOf(myWebView.getHitTestResult().getType()).contains(String.valueOf(myWebView.getHitTestResult().IMAGE_TYPE))) {
                    v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                            MenuItem downloadBtn = menu.add(Menu.NONE, 401, Menu.NONE, "Загрузки");
                            MenuItem downloadBtn = menu.add(Menu.NONE, 501, Menu.NONE, "Скачать");
                            downloadBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    Log.d("mytag", "Рисуем контекстное меню на картинке: " + String.valueOf(myWebView.getHitTestResult().getExtra()));
                                    downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                    Uri Download_Uri = Uri.parse(String.valueOf(myWebView.getHitTestResult().getExtra()));
                                    DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                                    request.setAllowedOverRoaming(false);
                                    request.setTitle("My Data Download");
                                    request.setDescription("Android Data download using DownloadManager.");
//                    request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,"abc.png");
                                    request.setDestinationInExternalFilesDir(getApplicationContext(), getCacheDir().getPath(),"abc.png");
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setVisibleInDownloadsUi(true);
                                    long downloadReference = downloadManager.enqueue(request);
                                    Log.d("mytag", "поставил закачку");
                                    try {
                                        downloadManager.openDownloadedFile(downloadReference);
                                        Log.d("mytag", "файл есть");
                                    } catch (FileNotFoundException e) {
                                        Log.d("mytag", "файла нет");
                                    }
                                    db.execSQL("INSERT INTO \"downloads\"(url) VALUES (\"" + Download_Uri + "\"");
//                                    DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
//                                    myDownloadQuery.setFilterById(downloadReference);
//                                    Cursor cursor = downloadManager.query(myDownloadQuery);
//                                    if(cursor.moveToFirst()){
//                                        downloadManager.remove(downloadReference);
//                                    }
                                    return false;
                                }
                            });
                        }
                    });

                }

                return false;

            }
        });

//        myWebView.requestFocusNodeHref();

//        myWebView.setPictureListener(new WebView.PictureListener() {
//            @Override
//            public void onNewPicture(WebView webView, @Nullable Picture picture) {
//                Log.d("mytag", "Cменилась картинка: " + String.valueOf(picture.getHeight()));
//            }
//        });

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("https://google.com");

        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                db.execSQL("INSERT INTO \"downloads\"(url, useragent, contentdisposition, mimetype, contentlength) VALUES (\"" + url + "\", \"" + userAgent + "\", \"" + contentDisposition + "\", \"" + mimetype + "\", " + contentLength);
            }
        });

        EditText keywords = findViewById(R.id.keywords);
        ImageButton searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchKeywords = keywords.getText().toString();
//                try {
//                    InetAddress ip = InetAddress.getByName(new URL(searchKeywords).getHost());
//                    searchKeywords = keywords.getText().toString();
//                    Log.d("mytag", "ищу по хосту");
////                    myWebView.loadUrl(searchKeywords);
//                } catch(UnknownHostException e) {
//                    searchKeywords = "https://yandex.ru/search/?lr=10765&text=" + keywords.getText().toString();
//                    Log.d("mytag", "ищу по поиску");
//                } catch (MalformedURLException e) {
//                    searchKeywords = "https://yandex.ru/search/?lr=10765&text=" + keywords.getText().toString();
//                    Log.d("mytag", "ищу по поиску");
//                } catch (Exception e){
//                    Log.d("mytag", e.getClass().toString());
//                }

                String rightUrl = keywords.getText().toString();
                try {
                    rightUrl = new HostCheckTask().execute(keywords.getText().toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Log.d("mytag", "ошибка AsyncTask");
                }
                myWebView.loadUrl(rightUrl);

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
                MenuItem downloads = menu.add(Menu.NONE, 101, Menu.NONE, "Загрузки");
                downloads.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this, DownloadsActivity.class);
                        MainActivity.this.startActivity(intent);
                        return false;
                    }
                });
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