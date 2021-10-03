package com.example.browser;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;

public class DetectedWebViewClient extends WebViewClient {

    public EditText keywords;
    public ImageView bookmark;

    public DetectedWebViewClient(EditText keywords, ImageView bookmark){
        this.keywords = keywords;
        this.bookmark = bookmark;
    }

    @Override
    public void onPageStarted (WebView view, String url, Bitmap favicon){
        this.keywords.setText(url);
        this.bookmark.setImageResource(R.drawable.emptystar);
    }

}
