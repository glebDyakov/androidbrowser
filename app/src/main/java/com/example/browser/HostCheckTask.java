package com.example.browser;

import android.os.AsyncTask;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class HostCheckTask extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... strings) {
        try {
            InetAddress ip = InetAddress.getByName(new URL(strings[0]).getHost());
            return strings[0];
        } catch (MalformedURLException e) {
            return "https://yandex.ru/search/?lr=10765&text=" + strings[0];
        } catch (UnknownHostException e) {
            return "https://yandex.ru/search/?lr=10765&text=" + strings[0];
        }
    }

}
