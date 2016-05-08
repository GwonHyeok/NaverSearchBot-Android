package kr.co.hyeok.webviewinjection.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by GwonHyeok on 2016. 5. 5..
 */
public class AppPreference {
    private SharedPreferences sharedPreferences;

    private static AppPreference ourInstance = new AppPreference();

    public static AppPreference getInstance() {
        return ourInstance;
    }

    private final String KEY_SEARCH_INDEX = "key_search_index";

    private AppPreference() {
        Context context = ApplicationContext.getContext();
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public synchronized void setCurrentSearchIndex(int index) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SEARCH_INDEX, index);
        Log.d(getClass().getSimpleName(), "Search Index : " + index);
        editor.commit();
    }

    public synchronized int getCurrentSearchIndex() {
        int searchIndex = sharedPreferences.getInt(KEY_SEARCH_INDEX, 0);
        Log.d(getClass().getSimpleName(), "Get Search Index : " + searchIndex);
        return searchIndex;
    }
}
