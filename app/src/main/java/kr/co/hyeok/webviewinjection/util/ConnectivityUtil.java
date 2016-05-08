package kr.co.hyeok.webviewinjection.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by GwonHyeok on 2016. 4. 28..
 */
public class ConnectivityUtil {
    private final String TAG = getClass().getSimpleName();

    private static ConnectivityUtil ourInstance = new ConnectivityUtil();

    public static ConnectivityUtil getInstance() {
        return ourInstance;
    }

    private ConnectivityUtil() {
    }

    public void setMobileDataEnabled(Context context, boolean enable) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class classVar = Class.forName(cm.getClass().getName());
            Method localMethod = classVar.getDeclaredMethod("getMobileDataEnabled");

            boolean isEnable = ((Boolean) localMethod.invoke(cm)).booleanValue();

            classVar.getDeclaredMethod("setMobileDataEnabled", new Class[]{Boolean.TYPE})
                    .invoke(cm, new Object[]{Boolean.valueOf(!isEnable)});

            Log.d(TAG, "Success setMobileDataEnabled To : " + enable);
            Toast.makeText(context, "데이터 변경 성공 : " + enable, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "Fail setMobileDataEnabled To : " + enable);

            Toast.makeText(context, "데이터 변경 실패 새로운 메소드 사용 : " + enable + " / " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();

            newSetMobileDataEnabled(context, enable);
        }
    }

    private void newSetMobileDataEnabled(Context context, boolean isEnabled) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getITelephony = Class.forName(tm.getClass().getName()).getDeclaredMethod("getITelephony");

            getITelephony.setAccessible(true);
            ITelephony telephony = (ITelephony) getITelephony.invoke(tm);
            if (isEnabled) {
                telephony.enableDataConnectivity();
            } else {
                telephony.disableDataConnectivity();
            }

            Toast.makeText(context, "새로ㅜㄴ 메소드 데이터 변경 성공 : " + isEnabled, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "새로운 메소드 데이터 변경 실패 : " + isEnabled + " / " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}