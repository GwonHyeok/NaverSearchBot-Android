package kr.co.hyeok.webviewinjection;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.TextView;

import java.lang.reflect.Field;

import kr.co.hyeok.webviewinjection.hack.JqueryInjectWebClient;
import kr.co.hyeok.webviewinjection.hack.JqueryWebChromeClient;
import kr.co.hyeok.webviewinjection.hack.NaverWebInterface;
import kr.co.hyeok.webviewinjection.hack.listener.OnNewWorkStartListener;
import kr.co.hyeok.webviewinjection.util.ApplicationUtil;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private ViewGroup mWebViewRoot;

    private View mReset;
    private TextView mUserAgent, mKeyword, mTargetUrl;

    private NaverWebInterface mNaverWebInterface;
    private final String TAG = "MainActivityWebView";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApplicationUtil.getInstance().deleteCache(this);

        enableWebViewDebugMode(null);
        showWebViewDebugMode();

        mWebViewRoot = (ViewGroup) findViewById(R.id.webview_root);
        mReset = findViewById(R.id.reset);
        mUserAgent = (TextView) findViewById(R.id.user_agent);
        mKeyword = (TextView) findViewById(R.id.keyword);
        mTargetUrl = (TextView) findViewById(R.id.target_url);

        mNaverWebInterface = new NaverWebInterface(this, mWebView, ApplicationUtil.getInstance().getSearchDatas());
        mNaverWebInterface.setResetView(mReset);
        mNaverWebInterface.setOnNewWorkStartListener(new OnNewWorkStartListener() {
            @Override
            public void onWorkStart(String userAgent, String keyword, String url) {
                if (mUserAgent != null) mUserAgent.setText(userAgent);
                if (mKeyword != null) mKeyword.setText(keyword);
                if (mTargetUrl != null) mTargetUrl.setText(url);

                initWebView();
            }
        });

        initWebView();
        mNaverWebInterface.start();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.freeMemory();
            mWebView.clearView();
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.pauseTimers();
            mWebView.destroy();

            WebStorage.getInstance().deleteAllData();

            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
            cookieSyncManager.startSync();
            CookieManager.getInstance().removeAllCookie();
            CookieManager.getInstance().removeSessionCookie();
            cookieSyncManager.stopSync();

            Log.d(TAG, "BEGIN RESET INIT WEBVIEW");
            showWebViewFactoryProvider(mWebView);
            Log.d(TAG, "START RESET INIT WEBVIEW");
            resetWebViewFactoryProvider(mWebView);
            Log.d(TAG, "FINISH RESET INIT WEBVIEW");
            showWebViewFactoryProvider(mWebView);

            mWebView = null;
        }

        ApplicationUtil.getInstance().deleteCache(getApplicationContext());

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mWebViewRoot.getLayoutParams());
        mWebView = new WebView(MainActivity.this);
        Log.d(TAG, "New WebViewFactoryProvider : ");
        showWebViewFactoryProvider(mWebView);

        mWebView.resumeTimers();
        mWebView.setLayoutParams(layoutParams);
        mWebView.requestLayout();

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebViewClient(new JqueryInjectWebClient(mNaverWebInterface));
        mWebView.setWebChromeClient(new JqueryWebChromeClient());

        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebViewRoot.removeAllViews();
        mWebViewRoot.addView(mWebView);

        if (mNaverWebInterface != null) {
            mNaverWebInterface.setWebView(mWebView);
            mWebView.addJavascriptInterface(mNaverWebInterface, "HelloNaver");
        }
    }

    /**
     * WebView의 WebViewFactory 를 가져와서
     * WebViewFactoryProvider를 null로 변경 한다
     */
    private void resetWebViewFactoryProvider(WebView webView) {
        try {
            Class clazz = Class.forName("android.webkit.WebViewFactory");
            Field sProviderInstance = clazz.getDeclaredField("sProviderInstance");
            sProviderInstance.setAccessible(true);
            sProviderInstance.set(null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWebViewFactoryProvider(WebView webView) {
        try {
            Class clazz = Class.forName("android.webkit.WebViewFactory");
            Field sProviderInstance = clazz.getDeclaredField("sProviderInstance");
            sProviderInstance.setAccessible(true);

            Object object = sProviderInstance.get(null);
            Log.d(TAG, String.valueOf(object));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableWebViewDebugMode(WebView webView) {
        try {
            Class clazz = Class.forName("android.webkit.WebViewFactory");
            Field DEBUG = clazz.getDeclaredField("DEBUG");
            DEBUG.setAccessible(true);
            DEBUG.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWebViewDebugMode() {
        try {
            Class clazz = Class.forName("android.webkit.WebViewFactory");
            Field DEBUG = clazz.getDeclaredField("DEBUG");
            DEBUG.setAccessible(true);
            Log.d(TAG, String.valueOf(DEBUG.get(null)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}