package kr.co.hyeok.webviewinjection.hack;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Random;

import kr.co.hyeok.webviewinjection.MainActivity;
import kr.co.hyeok.webviewinjection.data.SearchData;
import kr.co.hyeok.webviewinjection.hack.listener.OnNewWorkStartListener;
import kr.co.hyeok.webviewinjection.util.AppPreference;
import kr.co.hyeok.webviewinjection.util.ApplicationUtil;
import kr.co.hyeok.webviewinjection.util.ConnectivityUtil;
import kr.co.hyeok.webviewinjection.util.WebInterfaceUtil;

/**
 * Created by GwonHyeok on 2016. 4. 27..
 */
public abstract class WebInterface {
    private WebView mWebView;
    private View mResetView;
    private Activity mActivity;

    private ArrayList<SearchData> mSearchDatas;
    private int mCurrentSearchIndex = AppPreference.getInstance().getCurrentSearchIndex();
    private boolean isRunnable = false;

    private Handler handler = new Handler();
    private OnNewWorkStartListener onNewWorkStartListener;

    private final String TAG = getClass().getSimpleName();

    public WebInterface(Activity activity, WebView webView, ArrayList<SearchData> searchDatas) {
        this.mActivity = activity;
        this.mWebView = webView;
        this.mSearchDatas = searchDatas;
    }

    // 현재 상태를 저장한다
    protected enum WEBSTATUS {
        HELLO,                          // 초기 접속
        SEARCH_KEYWORDS,                // 키워드가 입력됨
        MAIN_SEARCH,                    // 메인에 키워드가 있는지 검색
        SEARCH_AS_PATTERN,              // 검색 버튼 누른 후 패턴에 맞게 버튼 클릭
        SCROLL_TO_MORE_BUTTON,          // 더보기 버튼까지 스크롤함
        CLICK_MORE_BUTTON,              // 더보기 버튼을 클릭함
        NEXT_PAGE_BUTTON,               // 다음 페이지로 이동함
        CLICK_TARGET_LINK,              // 해당 url을 클릭
        SHOW_TARGET_LINK_CONTENT,       // 해당 url 접속
        STOP                            // 작업이 완료됨
    }

    protected enum PATTERN {
        DEFAULT,                        // 기본 검색버튼을 이용한 검색
        UNIFIED,                        // 통합 검색
        BLOG,                           // 블로그 검색
        CAFE                            // 카페 검색
    }

    protected WEBSTATUS webstatus = WEBSTATUS.HELLO;
    protected PATTERN searchPattern = PATTERN.DEFAULT;

    /**
     * 웹의 jQuery가 로드 되었을때 호출한다
     */
    @JavascriptInterface
    public void onLoadJQuery() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isRunnable) return;
                if (mWebView != null) {
                    Log.d(TAG, "Native onLoadJQuery");

                    // JQuery가 로드 됬다면 현제 해야할 작업을 실행 요청한다
                    loadPortalHackJS();
                }
            }
        });
    }

    /**
     * 키워드로 웹에서 검색한다
     */
    public void searchKeyword() {
        WebInterfaceUtil.getInstance().evaluateScriptMethod(mWebView, "searchKeyword", getKeyword(), String.valueOf(getMainTTL()));
    }

    /**
     * 더보기 버튼이 있는 곳까지 스크롤 이베트를 준다
     */
    public void scrollToMoreButton() {
        WebInterfaceUtil.getInstance().evaluateScriptMethod(mWebView, "scrollToMoreButton");
    }

    /**
     * 더보기 버튼을 클릭한다
     */
    public void touchMoreButton() {
        WebInterfaceUtil.getInstance().evaluateScriptMethod(mWebView, "touchMoreButton", String.valueOf(getMoreTTL()));
    }

    /**
     * 현재 찾고자 하는 Url 이 있는지 확인한다
     */
    public void hasFindUrl() {
        WebInterfaceUtil.getInstance().evaluateScriptMethod(mWebView, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (webstatus != WEBSTATUS.CLICK_TARGET_LINK) {

                    boolean result = Boolean.parseBoolean(value);
                    Log.d(TAG, "HAS FIND URL REAL : " + value);
                    Log.d(TAG, "HAS FIND URL PARSER : " + result);

                    if (result) {
                        // Go To Target Link
                        webstatus = WEBSTATUS.CLICK_TARGET_LINK;
                    } else {
                        // Type To More Search
                        Log.d(TAG, "Not Find Url -- More Search");
                        if (webstatus == WEBSTATUS.MAIN_SEARCH) {
                            Log.d(TAG, "너가 범인이냐 ?");
                            webstatus = WEBSTATUS.SCROLL_TO_MORE_BUTTON;
                        } else {
                            webstatus = WEBSTATUS.NEXT_PAGE_BUTTON;
                        }
                    }
                }

                // 검색 후 값이 있다면 작업 없다면 다음페이지로 이동한다
                runCurrentStatusWork();
            }
        }, "hasFindUrl", getTargetUrl());
    }

    /**
     * 다음 페이지로 이동한다
     */
    public void goNextPage() {
        WebInterfaceUtil.getInstance().evaluateScriptMethod(mWebView, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (value != null && !value.equalsIgnoreCase("null")) {
                    Log.d(TAG, "goNextPage Real : " + value);
                    boolean result = Boolean.parseBoolean(value);
                    // 페이지의 끝이여서 더이상 진행 할 수 없다면 작업을 중지시킨다
                    if (!result) {
                        webstatus = WEBSTATUS.STOP;
                        runCurrentStatusWork();
                    }
                }
            }
        }, "goNextPage", String.valueOf(getFindTTL()));
    }

    /**
     * 내가 원했던 링크가 있으면 해당 링크로 들어간다
     */
    public void goTargetLink() {
        WebInterfaceUtil.getInstance().evaluateScriptMethod(mWebView, "goTargetLink", getTargetUrl());
    }

    /**
     * 현재의 검색 패턴에 맞춰서 글을 상세 검색 한다
     */
    private void touchAsPattern() {
        Log.d(TAG, "touchAsPattern");
        switch (searchPattern) {
            case DEFAULT:
                Log.d(TAG, "DEFAULT");
                runTouchAsPattern("touchSearch", String.valueOf(getDefaultTTL()));
                break;

            case UNIFIED:
                Log.d(TAG, "UNIFIED");
                runTouchAsPattern("touchUnified", String.valueOf(getUnifiedTTL()));
                break;

            case CAFE:
                Log.d(TAG, "CAFE");
                runTouchAsPattern("touchCafe", String.valueOf(getBlogCafeTTL()));
                break;

            case BLOG:
                Log.d(TAG, "BLOG");
                runTouchAsPattern("touchBlog", String.valueOf(getBlogCafeTTL()));
                break;
        }

    }

    private void scrollToBottom() {
        Log.d(TAG, "Scroll To Bottom : Begin");
        WebInterfaceUtil.getInstance().evaluateScriptMethod(mWebView, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.d(TAG, "Scroll To Bottom : Receive(" + value + ")");

                boolean isFinish = Boolean.parseBoolean(value);
                Log.d(TAG, "Scroll To Bottom : isFinish(" + isFinish + ")");
                Log.d(TAG, "Scroll To Bottom : isFinish(" + isFinish + ")");
                if (isFinish) {
                    webstatus = WEBSTATUS.STOP;
                    runCurrentStatusWork();
                }

                handler.removeCallbacks(scrollToBottomReWork);
                handler.postDelayed(scrollToBottomReWork, 500);
            }
        }, "scrollToBottom", String.valueOf(getStayTTL()), getTargetUrl());
    }

    private Runnable scrollToBottomReWork = new Runnable() {
        @Override
        public void run() {
            webstatus = WEBSTATUS.SHOW_TARGET_LINK_CONTENT;
            runCurrentStatusWork();
        }
    };

    private void runTouchAsPattern(final String method, final String delay) {
        WebInterfaceUtil.getInstance().evaluateScriptMethod(mWebView, method, delay);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                webstatus = WEBSTATUS.SEARCH_AS_PATTERN;
                runCurrentStatusWork();
            }
        }, Integer.parseInt(delay));
    }

    public void runCurrentStatusWork() {
        if (!isRunnable) return;

        switch (webstatus) {
            case HELLO:
                Log.d(TAG, "HELLO");
                searchKeyword();
                webstatus = WEBSTATUS.SEARCH_KEYWORDS;
                break;

            case SEARCH_KEYWORDS:
                Log.d(TAG, "SEARCH_KEYWORDS");
                webstatus = WEBSTATUS.MAIN_SEARCH;
                runCurrentStatusWork();
                break;

            case MAIN_SEARCH:
                Log.d(TAG, "MAIN_SEARCH");
                touchAsPattern();
                break;

            case SCROLL_TO_MORE_BUTTON:
                Log.d(TAG, "SCROLL_TO_MORE_BUTTON");
                scrollToMoreButton();
                touchMoreButton();
                webstatus = WEBSTATUS.CLICK_MORE_BUTTON;
                break;

            case SEARCH_AS_PATTERN:
            case CLICK_MORE_BUTTON:
                Log.d(TAG, "SEARCH_AS_PATTERN");
                hasFindUrl();
                break;

            case NEXT_PAGE_BUTTON:
                Log.d(TAG, "NEXT_PAGE_BUTTON");
                goNextPage();
                webstatus = WEBSTATUS.CLICK_MORE_BUTTON;
                break;

            case CLICK_TARGET_LINK:
                Log.d(TAG, "CLICK_TARGET_LINK");
                goTargetLink();
                webstatus = WEBSTATUS.SHOW_TARGET_LINK_CONTENT;
                break;

            case SHOW_TARGET_LINK_CONTENT:
                Log.d(TAG, "SHOW_TARGET_LINK_CONTENT");
                scrollToBottom();
                break;

            case STOP:
                Log.d(TAG, "FINISH ONE TERM");

                // 15 ~ 60 동안 머무른 후 새로운 index 로 작업
                isRunnable = false;
                scheduleNextWork();
                break;
        }
    }

    private void scheduleNextWork() {
        Log.d(TAG, "SCHEDULENEXTWORK");

        int stayTTL = getStayTTL();

        // TTL 시간만큼 유지하다가 Phone Data Status Change
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mResetView != null) {
                    mWebView.loadUrl("about:blank");
                    mResetView.setVisibility(View.VISIBLE);
                }

                // Data Off
                Log.d(TAG, "Data Off Now");
                ConnectivityUtil.getInstance().setMobileDataEnabled(getActivity(), false);
            }
        }, stayTTL);

        // TTL + 5초 만큼 있다가 데이터 연결
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Data Off On");
                ConnectivityUtil.getInstance().setMobileDataEnabled(getActivity(), true);
            }
        }, stayTTL + (5 * 1000));

        // TTL + 15초 만큼 있다가 작업 시작
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "SCHEDULENEXTWORK START");
                mCurrentSearchIndex = mCurrentSearchIndex > mSearchDatas.size() - 1 ? 0 : mCurrentSearchIndex + 1;
                AppPreference.getInstance().setCurrentSearchIndex(mCurrentSearchIndex);

                // App Restart
                Intent mStartActivity = new Intent(getActivity(), MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        }, stayTTL + (15 * 1000));
    }

    public void start() {
        webstatus = WEBSTATUS.HELLO;

        // Pattern 랜덤 설정
        Random random = new Random();
        int patternNum = random.nextInt(3);

        Log.d(TAG, "pattern Number  : " + patternNum);
        if (patternNum == 0) {
            searchPattern = PATTERN.DEFAULT;
        } else if (patternNum == 1) {
            searchPattern = PATTERN.UNIFIED;
        } else {
            String target = getTargetUrl();
            if (target.toLowerCase().contains("blog.naver.com")) {
                searchPattern = PATTERN.BLOG;
            } else if (target.toLowerCase().contains("cafe.naver.com")) {
                searchPattern = PATTERN.CAFE;
            } else {
                searchPattern = PATTERN.DEFAULT;
            }
        }

        String userAgent = ApplicationUtil.getInstance().getUserAgentRandomly().getUserAgent();
        if (onNewWorkStartListener != null) {
            onNewWorkStartListener.onWorkStart(userAgent, getKeyword(), getTargetUrl());
        }

        mWebView.getSettings().setUserAgentString(userAgent);

        mWebView.loadUrl(getPortalUrl());

        isRunnable = true;

        if (mResetView != null) {
            mResetView.setVisibility(View.GONE);
        }
    }

    private String getKeyword() {
        return this.mSearchDatas.get(getCurrentSearchIndex()).getKeyword();
    }

    private String getTargetUrl() {
        return this.mSearchDatas.get(getCurrentSearchIndex()).getUrl();
    }

    private int getMainTTL() {
        return this.mSearchDatas.get(getCurrentSearchIndex()).getMainTTL() * 1000;
    }

    private int getMoreTTL() {
        return this.mSearchDatas.get(getCurrentSearchIndex()).getMoreTTL() * 1000;
    }

    private int getFindTTL() {
        return this.mSearchDatas.get(getCurrentSearchIndex()).getFindTTL() * 1000;
    }

    private int getStayTTL() {
        return this.mSearchDatas.get(getCurrentSearchIndex()).getStayTTL() * 1000;
    }

    private int getDefaultTTL() {
        return this.mSearchDatas.get(getCurrentSearchIndex()).getDefaultTTL() * 1000;
    }

    private int getUnifiedTTL() {
        return this.mSearchDatas.get(getCurrentSearchIndex()).getUnifiedTTL() * 1000;
    }

    private int getBlogCafeTTL() {
        return this.mSearchDatas.get(getCurrentSearchIndex()).getBlogCafeTTL() * 1000;
    }

    private int getCurrentSearchIndex() {
        if (mSearchDatas.size() - 1 < mCurrentSearchIndex) {
            Log.d(TAG, "Search Index Reset");
            mCurrentSearchIndex = 0;
        }

        // 임시
        if (mCurrentSearchIndex > 3) {
            AppPreference.getInstance().setCurrentSearchIndex(0);
            return 0;
        }

        AppPreference.getInstance().setCurrentSearchIndex(mCurrentSearchIndex);
        return this.mCurrentSearchIndex;
    }

    private void loadPortalHackJS() {
        String script = WebInterfaceUtil.getInstance().getJavascriptData(getActivity(), getWebHookScriptName());

        mWebView.evaluateJavascript(script, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.d(TAG, "Native onLoadJQuery Response URL : " + mWebView.getUrl());
                Log.d(TAG, "Native onLoadJQuery Response : " + value);

                runCurrentStatusWork();
            }
        });
    }

    /**
     * 포털 사이트의 메인 주소를 가져온다
     *
     * @return 포털사이트의 메인 주소
     */
    protected abstract String getPortalUrl();

    /**
     * 각 포털 사이트별 자바스크립트 이름을 가져온다
     *
     * @return Javascript File Name
     */
    protected abstract String getWebHookScriptName();

    /**
     * 현재 액티비티를 리턴
     *
     * @return Activity
     */
    protected Activity getActivity() {
        return this.mActivity;
    }

    public void setOnNewWorkStartListener(OnNewWorkStartListener onNewWorkStartListener) {
        this.onNewWorkStartListener = onNewWorkStartListener;
    }

    public void finishLoadUrl(String url) {
        Log.d(TAG, "Receive URL : " + url);
        Log.d(TAG, "Target URL : " + getTargetUrl());
        Log.d(TAG, "URL MATCH : " + url.toLowerCase().contains(getTargetUrl().toLowerCase()));
    }

    public void setResetView(View resetView) {
        this.mResetView = resetView;
    }

    public void setWebView(WebView webView) {
        this.mWebView = webView;
    }
}