package kr.co.hyeok.webviewinjection.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import kr.co.hyeok.webviewinjection.data.SearchData;
import kr.co.hyeok.webviewinjection.data.UserAgentData;

/**
 * Created by GwonHyeok on 2016. 4. 28..
 */
public class ApplicationUtil {
    private final String TAG = getClass().getSimpleName();

    private final String APP_DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "InjectWebView";
    private final String USER_AGENT_FILE_NAME = "user-agent.xls";
    private final String PORTAL_URL_FILE_NAME = "portal-data.xls";

    private ArrayList<UserAgentData> userAgentDatas = new ArrayList<>();
    private ArrayList<SearchData> searchDatas = new ArrayList<>();

    private static ApplicationUtil ourInstance = new ApplicationUtil();

    public static ApplicationUtil getInstance() {
        return ourInstance;
    }

    private ApplicationUtil() {
        initApplicationData();
    }

    private void initApplicationData() {
        loadPortalData();
        loadUserAgentData();
    }

    private void loadPortalData() {
        String portalDataPath = APP_DATA_PATH + "/" + PORTAL_URL_FILE_NAME;
        Log.d(TAG, portalDataPath);

        Workbook workbook;
        Sheet sheet;

        try {
            File file = new File(portalDataPath);
            Log.d(TAG, "CAN READ : " + file.canRead());
            workbook = Workbook.getWorkbook(file);
            sheet = workbook.getSheet(0);

            Log.d(TAG, "Sheet Rows : " + sheet.getRows());
            Log.d(TAG, "SHEET : " + sheet.toString());

            for (int i = 1; i < sheet.getRows(); i++) {
                String keyword = sheet.getCell(0, i).getContents();
                String url = sheet.getCell(1, i).getContents();
                int[] mainTTL = parseTTL(sheet.getCell(2, i).getContents());
                int[] findTTL = parseTTL(sheet.getCell(3, i).getContents());
                int[] moreTTL = parseTTL(sheet.getCell(4, i).getContents());
                int[] stayTTL = parseTTL(sheet.getCell(5, i).getContents());
                int[] defaultTTL = parseTTL(sheet.getCell(6, i).getContents());
                int[] unifiedTTL = parseTTL(sheet.getCell(7, i).getContents());
                int[] blogCafeTTL = parseTTL(sheet.getCell(8, i).getContents());

                searchDatas.add(new SearchData(keyword, url, mainTTL, moreTTL, findTTL, stayTTL, defaultTTL, unifiedTTL, blogCafeTTL));
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private int[] parseTTL(String strTTL) {
        String[] ttlSet = strTTL.split("~");

        int ttl[] = new int[2];
        ttl[0] = Integer.parseInt(ttlSet[0]);
        ttl[1] = Integer.parseInt(ttlSet[1]);

        return ttl;
    }

    public ArrayList<SearchData> getSearchDatas() {
        return this.searchDatas;
    }

    public UserAgentData getUserAgentRandomly() {
        if (userAgentDatas.size() == 0) {
            initSampleUserAgent();
        }

        Random random = new Random();
        int randomIndex = random.nextInt(userAgentDatas.size() - 1);

        return userAgentDatas.get(randomIndex);
    }

    private void loadUserAgentData() {
        String userAgentDataPath = APP_DATA_PATH + "/" + USER_AGENT_FILE_NAME;
        Log.d(TAG, userAgentDataPath);

        Workbook workbook;
        Sheet sheet;

        try {
            File file = new File(userAgentDataPath);
            Log.d(TAG, "CAN READ : " + file.canRead());
            workbook = Workbook.getWorkbook(file);
            sheet = workbook.getSheet(0);

            Log.d(TAG, "Sheet Rows : " + sheet.getRows());

            for (int i = 1; i < sheet.getRows(); i++) {
                Cell cell = sheet.getCell(0, i);
                userAgentDatas.add(new UserAgentData(cell.getContents()));
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            initSampleUserAgent();
        }
    }

    private void initSampleUserAgent() {
        userAgentDatas.clear();
        userAgentDatas.add(new UserAgentData("Mozilla/5.0 (X11; U; UNICOS lcLinux; en-US) Gecko/20140730 (KHTML, like Gecko, Safari/419.3) Arora/0.8.0"));
    }

    public void deleteCache(Context context) {
        try {
            // Clear Cache Dir
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Clear app_webview Dir
            String path = context.getFilesDir().getParent() + "/app_webview";
            Log.d(TAG, "PATH : " + path);
            File dir = new File(path);
            if (dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Delete Files Dir
            File dir = context.getFilesDir();
            if (dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else {
            return dir != null && dir.isFile() && dir.delete();
        }
    }
}