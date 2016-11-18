package com.issac.mystepcounter.utils;

/**
 * Created by zhans on 2016/11/11.
 */
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.Locale;

import cz.msebera.android.httpclient.client.params.ClientPNames;

public class HttpUtil {
    public final static String HOST = "v.juhe.cn";
    private static String API_URL = "http://v.juhe.cn/toutiao/index%s&%s";
    private static String APPKEY = "key=8eba055f5d5a9970bdef54d86426f473";
    public static AsyncHttpClient client;

    public HttpUtil() {
    }

    public static AsyncHttpClient getClient() {
        return client;
    }

    public static void setClient(AsyncHttpClient c) {
        client = c;
        client.addHeader("Accept-Language", Locale.getDefault().toString());
        client.addHeader("Host", HOST);
        client.addHeader("Connection", "Keep-Alive");
        client.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.setUserAgent("Android");
    }

    public static void get(String partUrl, TextHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(partUrl), handler);
    }

    private static String getAbsoluteApiUrl(String partUrl) {
        String url = partUrl;
        if (!partUrl.startsWith("http:") && !partUrl.startsWith("https:")) {
            url = String.format(API_URL, partUrl,APPKEY);
        }
        Log.d("BASE_CLIENT", "request:" + url);
        return url;
    }
}
