package crixec.app.opengappshelper;

/**
 * Created by crixec on 16-8-21.
 */

import android.util.AndroidRuntimeException;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenGappsHelper {
    public static final String ARCH_ARM = "arm";
    public static final String ARCH_ARM_64 = "arm64";
    public static final String ARCH_X86 = "x86";
    public static final String ARCH_X86_64 = "x86_64";
    public static final String API_44 = "4.4";
    public static final String API_50 = "5.0";
    public static final String API_51 = "5.1";
    public static final String API_60 = "6.0";
    public static final String VARIANT_AROMA = "aroma";
    public static final String VARIANT_SUPER = "super";
    public static final String VARIANT_STOCK = "stock";
    public static final String VARIANT_FULL = "full";
    public static final String VARIANT_MINI = "mini";
    public static final String VARIANT_MICRO = "micro";
    public static final String VARIANT_NAMO = "nano";
    public static final String VARIANT_PICO = "pico";

    private static String getDate() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));
    }

    public static String obtainDownloadUrl(String arch, String api, String variant, String date) {
        String filename = "open_gapps-" + arch + "-" + api + "-" + variant + "-" + date + ".zip";
        // https://github.com/opengapps/arm/releases/download/20160821/open_gapps-arm-4.4-aroma-20160821.zip
        return String.format("https://github.com/opengapps/%s/releases/download/%s/%s", arch, date, filename);
    }

    public static String obtainDownloadUrl(String arch, String api, String variant) {
        return obtainDownloadUrl(arch, api, variant, getDate());
    }

//
//    public static String queryDate(String date) {
//        String url = obtainDownloadUrl(ARCH_ARM, API_60, VARIANT_PICO, date);
//        HttpClient httpClient = new DefaultHttpClient();
//        Log.i("OpenGappsHelper", url);
//        HttpGet httpGet = new HttpGet(url);
//        try {
//            HttpResponse response = httpClient.execute(httpGet);
//            int requestCode = response.getStatusLine().getStatusCode();
//            if (requestCode == 200) {
//                return date;
//            }
//        } catch (IOException e) {
//            throw new AndroidRuntimeException(e);
//        }
//        return null;
//    }
//
//    public static String queryDate() {
//        return queryDate(getDate());
//    }
}

