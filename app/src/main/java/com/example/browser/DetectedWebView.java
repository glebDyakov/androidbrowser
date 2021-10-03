package com.example.browser;

import android.content.Context;
import android.util.Log;
import android.view.ViewStructure;
import android.webkit.WebView;

import androidx.annotation.NonNull;

public class DetectedWebView extends WebView {

    public DetectedWebView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onProvideAutofillVirtualStructure(ViewStructure structure, int flags) {
        super.onProvideAutofillVirtualStructure(structure, flags);
        Log.d("mytag", "onProvideAutofillVirtualStructure: ");
    }

    @Override
    public void onProvideContentCaptureStructure(@NonNull ViewStructure structure, int flags) {
        super.onProvideContentCaptureStructure(structure, flags);
        Log.d("mytag", "onProvideContentCaptureStructure: ");
    }

    @Override
    public void onProvideVirtualStructure(ViewStructure structure) {
        super.onProvideVirtualStructure(structure);
        Log.d("mytag", "onProvideVirtualStructure: ");
    }
}
