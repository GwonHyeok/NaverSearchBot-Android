package kr.co.hyeok.webviewinjection.hack;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

/**
 * Created by GwonHyeok on 2016. 4. 27..
 */
public class JqueryWebChromeClient extends WebChromeClient {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        // called during webview initialization, original implementation does strictly nothing
        // and defaults to the native method WebViewCore.nativeProvideVisitedHistory()
        Log.d(TAG, "getVisitedHistory");

    }


    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        super.onConsoleMessage(consoleMessage);

        return true;
    }
}
