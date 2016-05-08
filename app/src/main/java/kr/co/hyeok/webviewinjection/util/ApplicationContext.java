package kr.co.hyeok.webviewinjection.util;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import kr.co.hyeok.webviewinjection.R;

/**
 * Created by GwonHyeok on 2016. 5. 5..
 */
@ReportsCrashes(
        resToastText = R.string.bug_report,
        mode = ReportingInteractionMode.SILENT,
        resDialogIcon = R.mipmap.ic_launcher,
        resDialogTitle = R.string.bug_report,
        resDialogText = R.string.bug_report_message,
        resDialogOkToast = R.string.bug_report,
        mailTo = "ghyeok@daycore.co.kr"
)
public class ApplicationContext extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}