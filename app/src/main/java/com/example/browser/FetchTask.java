package com.example.browser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

public class FetchTask  extends AsyncTask<String, Integer, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap output = null;
        try {
            InputStream in = new java.net.URL(urls[0]).openStream();
            output = (Bitmap) BitmapFactory.decodeStream(in);

        } catch(Exception e) {
            Log.d("mytag", "ошибка запроса: " + e);
        }
        return output;
    }
}
