package kr.co.hyeok.webviewinjection.data;

/**
 * Created by GwonHyeok on 2016. 4. 28..
 */
public class UserAgentData {
    private String mUserAgent;

    public UserAgentData(String userAgent) {
        this.mUserAgent = userAgent;
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public void setUserAgent(String mUserAgent) {
        this.mUserAgent = mUserAgent;
    }
}
