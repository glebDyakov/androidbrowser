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
import android.graphics.Typeface;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public String faviconUrl;
    public Typeface fontAwesome;
    public int currentTab = 7;
    public AlertDialog dialog;
    public String browserVersion = "???????????? ?????? ????";
    public boolean isMobileVersion = true;
    public String progressBarDirection = "right";
    public float previousCoordX = 0f;
    public int fontSize = 0;
    public String previousFindingSearch;
    public EditText keywords;

//    public ImageButton bookmarkAddBtn;
    public TextView bookmarkAddBtn;

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

        fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");

        searchText = findViewById(R.id.searchText);

        BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Log.d("mytag", "???????????????? ??????????????????");
                try {
                    ParcelFileDescriptor pfl = downloadManager.openDownloadedFile(downloadReference);
    //                db.execSQL("INSERT INTO \"downloads\"(url) VALUES (\"" + url + "\")");
                    db.execSQL("INSERT INTO \"downloads\"(url, contentlength) VALUES (\"" + url + "\"," + pfl.getStatSize() + ")");
//                    String mimetype = getMimeType(url);
//                    db.execSQL("INSERT INTO \"downloads\"(url, contentlength, mime) VALUES (\"" + url + "\"," + pfl.getStatSize() + "\"" + mimetype + "\")");

                    Log.d("mytag", "???????? ????????");
                } catch (FileNotFoundException e) {
                    Log.d("mytag", "?????????? ??????");
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

//        db.execSQL("CREATE TABLE IF NOT EXISTS history (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT);");
//        db.execSQL("CREATE TABLE IF NOT EXISTS history (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT, favicon TEXT);");
//        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL("CREATE TABLE IF NOT EXISTS history (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT, favicon TEXT, date TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarks (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT);");

//        db.execSQL("DROP TABLE IF EXISTS tabs");
        db.execSQL("CREATE TABLE IF NOT EXISTS tabs (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT);");

//        db.execSQL("DROP TABLE IF EXISTS usercache");
        db.execSQL("CREATE TABLE IF NOT EXISTS usercache (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT);");

//        db.execSQL("CREATE TABLE IF NOT EXISTS homepages (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS savepages (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT);");

//        if(DatabaseUtils.queryNumEntries(db, "homepages") <= 0) {
//            db.execSQL("INSERT INTO \"homepages\"(url) VALUES (\"https://google.com\");");
//        }

        if(DatabaseUtils.queryNumEntries(db, "usercache") <= 0) {
            db.execSQL("INSERT INTO \"usercache\"(name, value) VALUES (\"fontsize\", 100);");
            if(DatabaseUtils.queryNumEntries(db, "tabs") <= 0) {
                db.execSQL("INSERT INTO \"tabs\"(title, url) VALUES (\"Google\", \"https://google.com\");");
                Cursor tabs = db.rawQuery("Select * from tabs", null);
                tabs.moveToFirst();
                currentTab = tabs.getInt(0);
                db.execSQL("INSERT INTO \"usercache\"(name, value) VALUES (\"currenttab\", " + currentTab + ");");
            }

        } else {
            if(DatabaseUtils.queryNumEntries(db, "tabs") <= 0) {
                db.execSQL("INSERT INTO \"tabs\"(title, url) VALUES (\"Google\", \"https://google.com\");");

                Cursor tabs = db.rawQuery("Select * from tabs", null);
//                Cursor tabs = db.rawQuery("Select * from tabs where _id = " + currentTab, null);

                tabs.moveToFirst();
                currentTab = tabs.getInt(0);
                db.execSQL("UPDATE \"usercache\" SET value = " + currentTab + " WHERE name = \"currenttab\";");
            }
        }
        Cursor usercache = db.rawQuery("Select * from usercache", null);
        usercache.moveToFirst();
        usercache.moveToNext();
        currentTab = usercache.getInt(2);
        Log.d("mytag", "currentTab: " + String.valueOf(currentTab));

//        db.execSQL("CREATE TABLE IF NOT EXISTS downloads (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT);");
//        db.execSQL("CREATE TABLE IF NOT EXISTS downloads (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, useragent TEXT, contentDisposition TEXT, mimetype TEXT, contentlength INTEGER);");
//        db.execSQL("DROP TABLE IF EXISTS downloads");
        db.execSQL("CREATE TABLE IF NOT EXISTS downloads (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, contentlength INTEGER);");
//        db.execSQL("CREATE TABLE IF NOT EXISTS downloads (_id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, contentlength INTEGER, mime TEXT);");

        WebView myWebView = (WebView) findViewById(R.id.htmlContent);

        Log.d("mytag", "mobile version: " + myWebView.getSettings().getUserAgentString());

        Button tabsCount = findViewById(R.id.tabsCount);
        tabsCount.setText(String.valueOf(DatabaseUtils.queryNumEntries(db, "tabs")));

        myWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("mytag", "node element: " + String.valueOf(myWebView.getHitTestResult().getType()).contains(String.valueOf(myWebView.getHitTestResult().IMAGE_TYPE)));
                if(String.valueOf(myWebView.getHitTestResult().getType()).contains(String.valueOf(myWebView.getHitTestResult().IMAGE_TYPE))) {
                    v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                            MenuItem downloadBtn = menu.add(Menu.NONE, 501, Menu.NONE, "??????????????");
                            downloadBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    url = myWebView.getHitTestResult().getExtra();
                                    Log.d("mytag", "???????????? ?????????????????????? ???????? ???? ????????????????: " + String.valueOf(myWebView.getHitTestResult().getExtra()));
                                    downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                    Uri Download_Uri = Uri.parse(String.valueOf(myWebView.getHitTestResult().getExtra()));
                                    DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                                    request.setAllowedOverRoaming(false);
                                    request.setTitle("?????????????????????? ??????????????????.");
                                    request.setDescription("?????????????????? ?????????? ??????????????????????.");

                                    request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,"abc.png");
//                                    request.setDestinationInExternalFilesDir(getApplicationContext(), getCacheDir().getPath(),"abc.png");

                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setVisibleInDownloadsUi(true);
                                    downloadReference = downloadManager.enqueue(request);
                                    Log.d("mytag", "???????????????? ??????????????");
                                    return false;
                                }
                            });
                        }
                    });

                }

                return false;

            }
        });

        Cursor userCache = db.rawQuery("Select * from usercache", null);
        userCache.moveToFirst();

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setGeolocationEnabled(false);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSaveFormData(false);

        webSettings.setDefaultFontSize((int) (0.14f * Float.valueOf(String.valueOf(userCache.getInt(2)))));
        fontSize = userCache.getInt(2);

        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("mytag", "onPageStarted: with url " + url);

                // ???????????????? ???? ?????????????????????????????? ?????????? ?????????????????? ????????????????
//                db.execSQL("INSERT INTO \"history\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");

                isBookmarkPage(url);

                if(myWebView.canGoBack()){
                    leftArrowBtn.setTextColor(Color.rgb(0, 0, 0));
                }

                keywords.setText(myWebView.getTitle().toString());

                db.execSQL("UPDATE \"tabs\" SET title = \"" + view.getTitle() + "\", url = \"" + url + "\" WHERE _id = " + currentTab +  ";");

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

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.d("mytag", "onLoadResource: " + url);
                if(url.contains(".ico")) {
                    faviconUrl = url;
                    Log.d("mytag", "onLoadResource ?????????????????? favicon url:" + url + ", faviconUrl: " + faviconUrl);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDateTime now = LocalDateTime.now();
                    db.execSQL("INSERT INTO \"history\"(title, url, favicon, date) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\", \"" + faviconUrl + "\", \"" + dtf.format(now) + "\");");
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String urlFromHistory = extras.getString("urlfromhistory");
            Log.d("mytag", "?????? ???? ??????????????: " + urlFromHistory);
            myWebView.loadUrl(urlFromHistory);
        } else {
            Log.d("mytag", "???????????????? ?????????????????? ????????????????");

//            Cursor currentTabRecord = db.rawQuery("Select * from usercache", null);
//            currentTabRecord.moveToFirst();
//            currentTabRecord.moveToNext();
//            Log.d("mytag", "currentTabRecord.getInt(2)): " + String.valueOf(currentTabRecord.getInt(2)));
            Cursor tabsCursor = db.rawQuery("Select * from tabs where _id = " + currentTab, null);
            tabsCursor.moveToFirst();
            Log.d("mytag", "activeTab: " + String.valueOf(tabsCursor.getInt(0)));

//            myWebView.loadUrl("https://google.com");
            myWebView.loadUrl(tabsCursor.getString(2));

        }

        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                db.execSQL("INSERT INTO \"downloads\"(url, useragent, contentdisposition, mimetype, contentlength) VALUES (\"" + url + "\", \"" + userAgent + "\", \"" + contentDisposition + "\", \"" + mimetype + "\", " + contentLength);
            }
        });

        keywords = findViewById(R.id.keywords);

//        ImageButton searchBtn = findViewById(R.id.searchBtn);
        TextView searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setTypeface(fontAwesome);
        searchBtn.setTextSize(34f);
//        searchBtn.setTextColor(Color.rgb(255, 150, 0));
        searchBtn.setText("s");

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchKeywords = keywords.getText().toString();
//                try {
//                    InetAddress ip = InetAddress.getByName(new URL(searchKeywords).getHost());
//                    searchKeywords = keywords.getText().toString();
//                    Log.d("mytag", "?????? ???? ??????????");
////                    myWebView.loadUrl(searchKeywords);
//                } catch(UnknownHostException e) {
//                    searchKeywords = "https://yandex.ru/search/?lr=10765&text=" + keywords.getText().toString();
//                    Log.d("mytag", "?????? ???? ????????????");
//                } catch (MalformedURLException e) {
//                    searchKeywords = "https://yandex.ru/search/?lr=10765&text=" + keywords.getText().toString();
//                    Log.d("mytag", "?????? ???? ????????????");
//                } catch (Exception e){
//                    Log.d("mytag", e.getClass().toString());
//                }

                String rightUrl = keywords.getText().toString();
                try {
                    rightUrl = new HostCheckTask().execute(keywords.getText().toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Log.d("mytag", "???????????? AsyncTask");
                }
                myWebView.loadUrl(rightUrl);

//                db.execSQL("INSERT INTO \"history\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");

                if(myWebView.canGoBack()){
                    leftArrowBtn.setTextColor(Color.rgb(0, 0, 0));
                }

                isBookmarkPage(myWebView.getUrl());

            }
        });

//        ImageButton burgerBtn = findViewById(R.id.burgerBtn);
        TextView burgerBtn = findViewById(R.id.burgerBtn);
        burgerBtn.setTextSize(34f);
        burgerBtn.setTypeface(fontAwesome);
        burgerBtn.setText("l");
        burgerBtn.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem downloads = menu.add(Menu.NONE, 101, Menu.NONE, "????????????????");
                downloads.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this, DownloadsActivity.class);
                        MainActivity.this.startActivity(intent);
                        return false;
                    }
                });
                MenuItem history = menu.add(Menu.NONE, 102, Menu.NONE, "????????????");
                history.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                        MainActivity.this.startActivity(intent);
                        return false;
                    }
                });
                MenuItem savePagesBtn = menu.add(Menu.NONE, 103, Menu.NONE, "?????????????????????? ????????????????");
                savePagesBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this, SavePages.class);
                        MainActivity.this.startActivity(intent);
                        return false;
                    }
                });
                MenuItem addPageBtn = menu.add(Menu.NONE, 104, Menu.NONE, "???????????????? ????????????????");
                addPageBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        LinearLayout.LayoutParams addPageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
                        builder.setTitle("???????????????????? ?????????????? ??????-????????????????");
                        LinearLayout addPageLayout = new LinearLayout(MainActivity.this);
                        addPageLayout.setOrientation(LinearLayout.VERTICAL);
                        TextView addPageBookmarks = new TextView(MainActivity.this);
                        addPageBookmarks.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        addPageBookmarks.setLayoutParams(addPageLayoutParams);
                        addPageBookmarks.setText("????????????????");
                        addPageBookmarks.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.execSQL("INSERT INTO \"bookmarks\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");
                                dialog.hide();
                            }
                        });
                        TextView addPageFastAccess = new TextView(MainActivity.this);
                        addPageFastAccess.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        addPageFastAccess.setLayoutParams(addPageLayoutParams);
                        addPageFastAccess.setText("?????????????? ????????????");
                        addPageFastAccess.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.execSQL("INSERT INTO \"savepages\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");
                                dialog.hide();
                            }
                        });
                        TextView addPageScreenApps = new TextView(MainActivity.this);
                        addPageScreenApps.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        addPageScreenApps.setLayoutParams(addPageLayoutParams);
                        addPageScreenApps.setText("?????????? ????????????????????");
                        addPageScreenApps.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.execSQL("INSERT INTO \"savepages\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");
                                dialog.hide();
                            }
                        });
                        TextView addPageSavePages = new TextView(MainActivity.this);
                        addPageSavePages.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        addPageSavePages.setLayoutParams(addPageLayoutParams);
                        addPageSavePages.setText("?????????????????????? ????????????????");
                        addPageSavePages.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.execSQL("INSERT INTO \"savepages\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");
                                dialog.hide();
                            }
                        });
                        addPageLayout.addView(addPageBookmarks);
                        addPageLayout.addView(addPageFastAccess);
                        addPageLayout.addView(addPageScreenApps);
                        addPageLayout.addView(addPageSavePages);
                        builder.setView(addPageLayout);
                        builder.setNegativeButton("????????????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        dialog = builder.create();
                        dialog.show();
                        return false;
                    }
                });
                MenuItem shareBtn = menu.add(Menu.NONE, 105, Menu.NONE, "????????????????????");
                shareBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/jpeg"); // might be text, sound, whatever
                        startActivity(Intent.createChooser(share, "share"));
                        return false;
                    }
                });
                MenuItem darkModeBtn = menu.add(Menu.NONE, 106, Menu.NONE, "?????????? ????????????????????");
                darkModeBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
                MenuItem adsBlockBtn = menu.add(Menu.NONE, 107, Menu.NONE, "???????????????????? ??????????????");
                adsBlockBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this, AdsBlock.class);
                        MainActivity.this.startActivity(intent);
                        return false;
                    }
                });
                MenuItem findOnPage = menu.add(Menu.NONE, 102, Menu.NONE, "?????????? ???? ????????????????");
                findOnPage.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(String.valueOf(hideFindingControlsBtn.getVisibility()).contains(String.valueOf(View.INVISIBLE))) {
                            searchText.setVisibility(View.VISIBLE);
                            findPreviousBtn.setVisibility(View.VISIBLE);
                            findNextBtn.setVisibility(View.VISIBLE);
                            hideFindingControlsBtn.setVisibility(View.VISIBLE);

                            //                        myWebView.findAllAsync("????????");
                            myWebView.findAllAsync(searchText.getText().toString());
                            previousFindingSearch = searchText.getText().toString();
                        } else {
                            String toastMessage = "?????????? ?????? ??????????????, ???????????? ?????????????????? ??????";
                            Toast toast = Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        return false;
                    }
                });
                if(isMobileVersion) {
                    browserVersion = "???????????? ?????? ????";
                } else if(!isMobileVersion){
                    browserVersion = "?????????????????? ????????????";
                }
                MenuItem pcVersionBtn = menu.add(Menu.NONE, 108, Menu.NONE, browserVersion);
                pcVersionBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        isMobileVersion = !isMobileVersion;
                        String newUA= "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
                        if(isMobileVersion) {
                            newUA= "Mozilla/5.0 (Linux; Android 11; sdk_gphone_x86_arm Build/RSR1.200819.001.A1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/83.0.4103.106 Mobile Safari/537.36";
                        } else if(!isMobileVersion) {
                            newUA= "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
                        }
                        myWebView.getSettings().setUserAgentString(newUA);
                        myWebView.reload();
                        return false;
                    }
                });
                MenuItem fontSizeBtn = menu.add(Menu.NONE, 109, Menu.NONE, "???????????? ????????????");
                fontSizeBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        LinearLayout.LayoutParams fontSizeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
                        builder.setTitle("???????????? ????????????");
                        LinearLayout fontSizeLayout = new LinearLayout(MainActivity.this);
                        fontSizeLayout.setOrientation(LinearLayout.VERTICAL);
                        TextView fontSizePercent = new TextView(MainActivity.this);
                        fontSizePercent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        fontSizePercent.setLayoutParams(fontSizeLayoutParams);
                        fontSizePercent.setText(String.valueOf(fontSize) + "%");
                        ProgressBar fontSizeProgressBar = new ProgressBar(MainActivity.this, null, 0, R.style.Widget_AppCompat_ProgressBar_Horizontal);
                        fontSizeProgressBar.setMin(50);
                        userCache.moveToFirst();
                        fontSizeProgressBar.setMax(200);
                        fontSizeProgressBar.setProgress(fontSize);
                        fontSizeProgressBar.setLayoutParams(fontSizeLayoutParams);
                        fontSizeProgressBar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] percents = fontSizePercent.getText().toString().split("%");
                                if(fontSizeProgressBar.getProgress() < 200 && progressBarDirection.contains("right")) {
                                    fontSize += 1;
                                    fontSizePercent.setText(String.valueOf(Integer.valueOf(percents[0]) + 1) + "%");
                                    fontSizeProgressBar.setProgress(fontSizeProgressBar.getProgress() + 1);
                                    if(String.valueOf(fontSizeProgressBar.getProgress()).contains(String.valueOf(200))){
                                        progressBarDirection = "left";
                                    }
                                } else if(fontSizeProgressBar.getProgress() > 50 && progressBarDirection.contains("left")) {
                                    fontSize -= 1;
                                    fontSizePercent.setText(String.valueOf(Integer.valueOf(percents[0]) - 1) + "%");
                                    fontSizeProgressBar.setProgress(fontSizeProgressBar.getProgress() - 1);
                                    if(String.valueOf(50).contains(String.valueOf(fontSizeProgressBar.getProgress()))){
                                        progressBarDirection = "right";
                                    }
                                }
//                                fontSize = fontSizeProgressBar.getProgress();
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
                        builder.setPositiveButton("????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.execSQL("UPDATE \"usercache\" SET value = " + fontSize + " WHERE _id = 1;");
                                webSettings.setDefaultFontSize((int) (0.14f * Float.valueOf(String.valueOf(fontSize))));
                            }
                        });
                        builder.setNegativeButton("????????????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return false;
                    }
                });
                MenuItem extensionsBtn = menu.add(Menu.NONE, 110, Menu.NONE, "????????????????????");
                extensionsBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this, Extensions.class);
                        MainActivity.this.startActivity(intent);
                        return false;
                    }
                });
                MenuItem printPDFBtn = menu.add(Menu.NONE, 111, Menu.NONE, "????????????/PDF");
                printPDFBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this, PrintPDF.class);
                        MainActivity.this.startActivity(intent);
                        return false;
                    }
                });
                MenuItem settingsBtn = menu.add(Menu.NONE, 112, Menu.NONE, "??????????????????");
                settingsBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        MainActivity.this.startActivity(intent);
                        return false;
                    }
                });
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


//        ImageButton bookmarkBtn = findViewById(R.id.bookmarkBtn);
        TextView bookmarkBtn = findViewById(R.id.bookmarkBtn);
        bookmarkBtn.setTypeface(fontAwesome);
        bookmarkBtn.setTextSize(34f);
        bookmarkBtn.setTextColor(Color.rgb(255, 150, 0));
        bookmarkBtn.setText("??");
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookmarksActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

//        ImageButton homeBtn = findViewById(R.id.homeBtn);
        TextView homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setTypeface(fontAwesome);
        homeBtn.setTextSize(34f);
        homeBtn.setText("\u0014");
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.loadUrl("https://google.com");
//                Cursor homepages = db.rawQuery("Select * from homepages", null);
//                homepages.moveToFirst();
//                myWebView.loadUrl(homepages.getString(1));
            }
        });

        bookmarkAddBtn = findViewById(R.id.bookmarkAddBtn);
        bookmarkAddBtn.setTypeface(fontAwesome);
        bookmarkAddBtn.setTextSize(34f);
        bookmarkAddBtn.setText("J");
        bookmarkAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("INSERT INTO \"bookmarks\"(title, url) VALUES (\"" + myWebView.getTitle() + "\", \"" + myWebView.getUrl() + "\");");

//                bookmarkAddBtn.setImageResource(R.drawable.star);
                bookmarkAddBtn.setTextColor(Color.rgb(255, 150, 0));
                bookmarkAddBtn.setText("??");

            }
        });

//        ImageButton refreshBtn = findViewById(R.id.refreshBtn);
        TextView refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setTypeface(fontAwesome);
        refreshBtn.setTextSize(34f);
        refreshBtn.setText("0");

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.reload();
                Log.d("mytag", "???????????????? ????????????????");
            }
        });

        hideFindingControlsBtn = findViewById(R.id.hideFindingControlsBtn);
        hideFindingControlsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mytag", "c????????????");
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
//        bookmarkAddBtn.setImageResource(R.drawable.emptystar);
        bookmarkAddBtn.setTextColor(Color.rgb(0, 0, 0));
        bookmarkAddBtn.setText("J");

        Cursor bookmarks = db.rawQuery("Select * from bookmarks", null);
        bookmarks.moveToFirst();
        while (true) {
            if (bookmarks.getString(2).contains(currentUrl)) {

//                bookmarkAddBtn.setImageResource(R.drawable.star);
                bookmarkAddBtn.setTextColor(Color.rgb(255, 150, 0));
                bookmarkAddBtn.setText("??");

                break;
            }
            if (!bookmarks.moveToNext()) {
                break;
            }
        }
    }

}