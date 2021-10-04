package com.example.browser;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.icu.text.UnicodeSet;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public String progressBarDirection = "right";
    public float previousCoordX = 0f;
    public int fontSize = 0;
    public String previousFindingSearch;
    public EditText keywords;
    public ImageButton bookmarkAddBtn;
    public String url;
    public long downloadReference;
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

        searchText = findViewById(R.id.searchText);

        BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Log.d("mytag", "загрузка завершена");
                try {
                    ParcelFileDescriptor pfl = downloadManager.openDownloadedFile(downloadReference);
    //                db.execSQL("INSERT INTO \"downloads\"(url) VALUES (\"" + url + "\")");
                    db.execSQL("INSERT INTO \"downloads\"(url, contentlength) VALUES (\"" + url + "\"," + pfl.getStatSize() + ")");
//                    String mimetype = getMimeType(url);
//                    db.execSQL("INSERT INTO \"downloads\"(url, contentlength, mime) VALUES (\"" + url + "\"," + pfl.getStatSize() + "\"" + mimetype + "\")");

                    Log.d("mytag", "файл есть");
                } catch (FileNotFoundException e) {
                    Log.d("mytag", "файла нет");
                }

                DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
                myDownloadQuery.setFilterById(downloadReference);
                Cursor cursor = downloadManager.query(myDownloadQuery);
                if(cursor.moveToFirst()){
                    downloadManager.remove(downloadReference);
                }
            }
        };
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        db = openOrCreateDatabase("bowser.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS history (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarks (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS tabs (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS usercache (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT);");

        if(DatabaseUtils.queryNumEntries(db, "usercache") <= 0) {
            db.execSQL("INSERT INTO \"usercache\"(name, value) VALUES (\"fontsize\", 100);");
        }

//        db.execSQL("CREATE TABLE IF NOT EXISTS downloads (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT);");
//        db.execSQL("CREATE TABLE IF NOT EXISTS downloads (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, useragent TEXT, contentDisposition TEXT, mimetype TEXT, contentlength INTEGER);");
//        db.execSQL("DROP TABLE IF EXISTS downloads");
        db.execSQL("CREATE TABLE IF NOT EXISTS downloads (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, contentlength INTEGER);");
//        db.execSQL("CREATE TABLE IF NOT EXISTS downloads (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, contentlength INTEGER, mime TEXT);");

        WebView myWebView = (WebView) findViewById(R.id.htmlContent);

        myWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("mytag", "node element: " + String.valueOf(myWebView.getHitTestResult().getType()).contains(String.valueOf(myWebView.getHitTestResult().IMAGE_TYPE)));
                if(String.valueOf(myWebView.getHitTestResult().getType()).contains(String.valueOf(myWebView.getHitTestResult().IMAGE_TYPE))) {
                    v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                            MenuItem downloadBtn = menu.add(Menu.NONE, 501, Menu.NONE, "Скачать");
                            downloadBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    url = myWebView.getHitTestResult().getExtra();
                                    Log.d("mytag", "Рисуем контекстное меню на картинке: " + String.valueOf(myWebView.getHitTestResult().getExtra()));
                                    downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                    Uri Download_Uri = Uri.parse(String.valueOf(myWebView.getHitTestResult().getExtra()));
                                    DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                                    request.setAllowedOverRoaming(false);
                                    request.setTitle("Изображение загружено.");
                                    request.setDescription("Загружено новое изображение.");

                                    request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,"abc.png");
//                                    request.setDestinationInExternalFilesDir(getApplicationContext(), getCacheDir().getPath(),"abc.png");

                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setVisibleInDownloadsUi(true);
                                    downloadReference = downloadManager.enqueue(request);
                                    Log.d("mytag", "поставил закачку");
//                                    try {
//                                        downloadManager.openDownloadedFile(downloadReference);
//                                        Log.d("mytag", "файл есть");
//                                    } catch (FileNotFoundException e) {
//                                        Log.d("mytag", "файла нет");
//                                    }
//                                    db.execSQL("INSERT INTO \"downloads\"(url) VALUES (\"" + url + "\")");
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

//        myWebView.setWebViewClient(new WebViewClient());
//        myWebView.setWebViewClient(new DetectedWebViewClient(searchText, bookmarkAddBtn));
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("mytag", "onPageStarted: with url " + url);

                db.execSQL("INSERT INTO \"history\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");

                isBookmarkPage(url);

                if(myWebView.canGoBack()){
                    leftArrowBtn.setTextColor(Color.rgb(0, 0, 0));
                }

                keywords.setText(myWebView.getTitle().toString());

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("mytag", "onPageFinished: for " + url);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                Log.d("mytag", "onPageCommitVisible: for " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("mytag", "shouldOverrideUrlLoading: for " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d("mytag", "shouldOverrideUrlLoading: for " +  request.getUrl());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.d("mytag", "onLoadResource: " + url);
            }
        });

        myWebView.loadUrl("https://google.com");

        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                db.execSQL("INSERT INTO \"downloads\"(url, useragent, contentdisposition, mimetype, contentlength) VALUES (\"" + url + "\", \"" + userAgent + "\", \"" + contentDisposition + "\", \"" + mimetype + "\", " + contentLength);
            }
        });

        keywords = findViewById(R.id.keywords);
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

                isBookmarkPage(myWebView.getUrl());

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
                        if(String.valueOf(hideFindingControlsBtn.getVisibility()).contains(String.valueOf(View.INVISIBLE))) {
                            searchText.setVisibility(View.VISIBLE);
                            findPreviousBtn.setVisibility(View.VISIBLE);
                            findNextBtn.setVisibility(View.VISIBLE);
                            hideFindingControlsBtn.setVisibility(View.VISIBLE);

                            //                        myWebView.findAllAsync("язык");
                            myWebView.findAllAsync(searchText.getText().toString());
                            previousFindingSearch = searchText.getText().toString();
                        } else {
                            String toastMessage = "Поиск уже включен, сперва отключите его";
                            Toast toast = Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        return false;
                    }
                });
                MenuItem pcVersionBtn = menu.add(Menu.NONE, 108, Menu.NONE, "Версия для ПК");
                MenuItem fontSizeBtn = menu.add(Menu.NONE, 109, Menu.NONE, "Размер шрифта");
                fontSizeBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        LinearLayout.LayoutParams fontSizeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);

                        builder.setTitle("Размер шрифта");
                        LinearLayout fontSizeLayout = new LinearLayout(MainActivity.this);
                        fontSizeLayout.setOrientation(LinearLayout.VERTICAL);
                        TextView fontSizePercent = new TextView(MainActivity.this);
                        fontSizePercent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        fontSizePercent.setLayoutParams(fontSizeLayoutParams);
                        fontSizePercent.setText("100%");
//                        builder.setView(fontSizePercent);
//                        TextView fontSizeProgressBar = new TextView(MainActivity.this);
                        ProgressBar fontSizeProgressBar = new ProgressBar(MainActivity.this, null, 0, R.style.Widget_AppCompat_ProgressBar_Horizontal);
                        fontSizeProgressBar.setMin(50);
                        fontSizeProgressBar.setProgress(100);
                        fontSizeProgressBar.setMax(200);
                        fontSizeProgressBar.setLayoutParams(fontSizeLayoutParams);
                        fontSizeProgressBar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] percents = fontSizePercent.getText().toString().split("%");
                                if(fontSizeProgressBar.getProgress() < 200 && progressBarDirection.contains("right")) {
                                    fontSizePercent.setText(String.valueOf(Integer.valueOf(percents[0]) + 1) + "%");
                                    fontSizeProgressBar.setProgress(fontSizeProgressBar.getProgress() + 1);
                                    if(String.valueOf(fontSizeProgressBar.getProgress()).contains(String.valueOf(200))){
                                        progressBarDirection = "left";
                                    }
                                } else if(fontSizeProgressBar.getProgress() > 50 && progressBarDirection.contains("left")) {
                                    fontSizePercent.setText(String.valueOf(Integer.valueOf(percents[0]) - 1) + "%");
                                    fontSizeProgressBar.setProgress(fontSizeProgressBar.getProgress() - 1);
                                    if(String.valueOf(50).contains(String.valueOf(fontSizeProgressBar.getProgress()))){
                                        progressBarDirection = "right";
                                    }
                                }
                            }
                        });
//                        fontSizeLayout.setOnDragListener(new View.OnDragListener() {
//                            @Override
//                            public boolean onDrag(View v, DragEvent event) {
//                                String[] percents = fontSizePercent.getText().toString().split("%");
//                                if(event.getX() > previousCoordX){
//                                    fontSizePercent.setText(String.valueOf(Integer.valueOf(percents[0]) + 1) + "%");
//                                    fontSizeProgressBar.setProgress(fontSizeProgressBar.getProgress() + 1);
//                                } else if(event.getX() < previousCoordX){
//                                    fontSizePercent.setText(String.valueOf(Integer.valueOf(percents[0]) - 1) + "%");
//                                    fontSizeProgressBar.setProgress(fontSizeProgressBar.getProgress() - 1);
//                                }
//                                previousCoordX = event.getX();
//                                return false;
//                            }
//                        });

                        fontSizeLayout.addView(fontSizePercent);
                        fontSizeLayout.addView(fontSizeProgressBar);
                        builder.setView(fontSizeLayout);
                        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fontSize = 0;
                            }
                        });
                        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

//                        builder.setView(R.layout.dialog_fontsize);

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return false;
                    }
                });
                MenuItem extensionsBtn = menu.add(Menu.NONE, 110, Menu.NONE, "Дополнения");
                MenuItem printPDFBtn = menu.add(Menu.NONE, 111, Menu.NONE, "Печать/PDF");
                MenuItem settingsBtn = menu.add(Menu.NONE, 112, Menu.NONE, "Настройки");
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

        bookmarkAddBtn = findViewById(R.id.bookmarkAddBtn);
        bookmarkAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("INSERT INTO \"bookmarks\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");
                bookmarkAddBtn.setImageResource(R.drawable.star);
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
                myWebView.findAllAsync("");
            }
        });

        findPreviousBtn = findViewById(R.id.findPreviousBtn);
        findPreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!previousFindingSearch.contains(searchText.getText().toString())){
                    myWebView.findAllAsync(searchText.getText().toString());
                } else {
                    myWebView.findNext(false);
                }
            }
        });

        findNextBtn = findViewById(R.id.findNextBtn);
        findNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!previousFindingSearch.contains(searchText.getText().toString())){
                    myWebView.findAllAsync(searchText.getText().toString());
                } else {
                    myWebView.findNext(true);
                }
            }
        });

//        searchText = findViewById(R.id.searchText);

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

                isBookmarkPage(myWebView.getUrl());

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

                isBookmarkPage(myWebView.getUrl());

            }
        };

        rightArrowBtn.setOnClickListener(rightArrowBtnListener);
        leftArrowBtn.setOnClickListener(leftArrowBtnListener);

    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void isBookmarkPage(String currentUrl) {
        bookmarkAddBtn.setImageResource(R.drawable.emptystar);
        Cursor bookmarks = db.rawQuery("Select * from bookmarks", null);
        bookmarks.moveToFirst();
        while (true) {
            if (bookmarks.getString(2).contains(currentUrl)) {
                bookmarkAddBtn.setImageResource(R.drawable.star);
                break;
            }
            if (!bookmarks.moveToNext()) {
                break;
            }
        }
    }

}