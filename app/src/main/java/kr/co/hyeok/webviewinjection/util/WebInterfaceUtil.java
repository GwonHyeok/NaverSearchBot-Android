package kr.co.hyeok.webviewinjection.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by GwonHyeok on 2016. 4. 27..
 */
public class WebInterfaceUtil {
    public static WebInterfaceUtil instance;
    public final String TAG = getClass().getSimpleName();

    public synchronized static WebInterfaceUtil getInstance() {
        if (instance == null) {
            instance = new WebInterfaceUtil();
        }
        return instance;
    }

    private WebInterfaceUtil() {
        super();
    }

    public String getJavascriptData(Context context, String name) {
        StringBuilder builder = new StringBuilder();

        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(name);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public void evaluateScriptMethod(WebView webView, String method, String... parameter) {
        evaluateScriptMethod(webView, null, method, parameter);
    }

    public void evaluateScriptMethod(WebView webView, ValueCallback<String> callback, String method, String... parameter) {
        StringBuilder builder = new StringBuilder();
        builder.append(method);
        builder.append("(");
        for (int i = 0; i < parameter.length; i++) {
            builder.append("\"");
            builder.append(parameter[i]);
            builder.append("\"");
            if (i != parameter.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");
        builder.append(";");

        if (callback == null) {
            callback = new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {

                }
            };
        }

        if (webView != null) {
            Log.d(TAG, builder.toString());
            webView.evaluateJavascript(builder.toString(), callback);
        }
    }
}
