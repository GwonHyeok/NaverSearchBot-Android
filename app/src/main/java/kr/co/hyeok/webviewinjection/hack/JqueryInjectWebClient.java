package kr.co.hyeok.webviewinjection.hack;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.co.hyeok.webviewinjection.util.WebInterfaceUtil;

/**
 * Created by GwonHyeok on 2016. 4. 27..
 */
public class JqueryInjectWebClient extends WebViewClient {
    private final String TAG = getClass().getSimpleName();
    private WebInterface webInterface;

    public JqueryInjectWebClient(WebInterface webInterface) {
        super();
        this.webInterface = webInterface;
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        Log.i("WebView", "History: " + url);
        super.doUpdateVisitedHistory(view, "", isReload);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "onPageFinished : " + url);

        String script = WebInterfaceUtil.getInstance().getJavascriptData(view.getContext(), "jquery.js");
        view.evaluateJavascript(script, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.d(TAG, " HERE");
                Log.d(TAG, "onReceiveValue : " + value);
            }
        });

        if (webInterface != null) {
            webInterface.finishLoadUrl(url);
        }

        super.onPageFinished(view, url);

        Log.d(TAG, "history");
        WebBackForwardList wbfl = view.copyBackForwardList();
        for (int i = 0; i < wbfl.getSize(); i++) {
            Log.d(TAG, wbfl.getItemAtIndex(i).getUrl());
        }

        view.clearHistory();

        Log.d(TAG, "cleared history");

        wbfl = view.copyBackForwardList();
        for (int i = 0; i < wbfl.getSize(); i++) {
            Log.d(TAG, wbfl.getItemAtIndex(i).getUrl());
        }
    }
}