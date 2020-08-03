package com.example.ecoleenligne.util;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AutoPlayVideoWebViewClient extends WebViewClient {

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // mimic onClick() event on the center of the WebView
        long delta = 100;
        long downTime = SystemClock.uptimeMillis();
        float x = view.getLeft() + (view.getWidth()/2);
        float y = view.getTop() + (view.getHeight()/2);

        MotionEvent tapDownEvent = MotionEvent.obtain(downTime, downTime + delta, MotionEvent.ACTION_DOWN, x, y, 0);
        tapDownEvent.setSource(InputDevice.SOURCE_CLASS_POINTER);
        MotionEvent tapUpEvent = MotionEvent.obtain(downTime, downTime + delta + 2, MotionEvent.ACTION_UP, x, y, 0);
        tapUpEvent.setSource(InputDevice.SOURCE_CLASS_POINTER);

        view.dispatchTouchEvent(tapDownEvent);
        view.dispatchTouchEvent(tapUpEvent);
    }
}