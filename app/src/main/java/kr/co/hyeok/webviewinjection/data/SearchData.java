package kr.co.hyeok.webviewinjection.data;

import java.util.Locale;
import java.util.Random;

/**
 * Created by GwonHyeok on 2016. 4. 27..
 */
public class SearchData {
    private String keyword, url;
    private int mainTTL[], moreTTL[], findTTL[], stayTTL[];

    private int unifiedTTL[], defaultTTL[], blogCafeTTL[];

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public SearchData(String keyword, String url, int mainTTL[], int moreTTL[], int findTTL[], int stayTTL[],
                      int defaultTTL[], int unifiedTTL[], int blogCafeTTL[]) {
        this.keyword = keyword;
        this.url = url;
        this.mainTTL = mainTTL;
        this.moreTTL = moreTTL;
        this.findTTL = findTTL;
        this.stayTTL = stayTTL;
        this.unifiedTTL = unifiedTTL;
        this.defaultTTL = defaultTTL;
        this.blogCafeTTL = blogCafeTTL;
    }

    public String toString() {
        return String.format(Locale.getDefault(), "keyword : %s, url : %s, time to live %d ~ %d",
                getKeyword(), getUrl(), mainTTL[0], mainTTL[1]);
    }

    public int getMainTTL() {
        return getRandomValue(this.mainTTL);
    }

    public int getMoreTTL() {
        return getRandomValue(this.moreTTL);
    }

    public int getFindTTL() {
        return getRandomValue(this.findTTL);
    }

    public int getStayTTL() {
        return getRandomValue(this.stayTTL);
    }

    public int getUnifiedTTL() {
        return getRandomValue(this.unifiedTTL);
    }

    public int getDefaultTTL() {
        return getRandomValue(this.defaultTTL);
    }

    public int getBlogCafeTTL() {
        return getRandomValue(this.blogCafeTTL);
    }

    private int getRandomValue(int[] values) {
        int max = values[1];
        int min = values[0];

        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}