package kr.co.hyeok.webviewinjection.hack;

import android.app.Activity;
import android.webkit.WebView;

import java.util.ArrayList;

import kr.co.hyeok.webviewinjection.data.SearchData;

/**
 * Created by GwonHyeok on 2016. 4. 27..
 */
public class NaverWebInterface extends WebInterface {

    public NaverWebInterface(Activity activity, WebView webView, ArrayList<SearchData> searchData) {
        super(activity, webView, searchData);
    }

    @Override
    protected String getPortalUrl() {
        return "http://m.naver.com";
    }

    @Override
    protected String getWebHookScriptName() {
        return "helloNaver.js";
    }
}