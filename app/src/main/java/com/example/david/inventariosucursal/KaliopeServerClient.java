package com.example.david.inventariosucursal;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class KaliopeServerClient {
    private static final String BASE_URL = "https://www.kaliopeadmin.com.mx/";
    //private static final String BASE_URL = "http://192.168.1.10:8080/kaliope/";


    //http://khbrainh.com/index.php/2018/12/11/android-javax-net-ssl-sslexception-hostname-in-certificate-didnt-match/
    //PARA ELIMINAR LA EXCEPCION DE android javax.net.ssl.SSLException hostname in certificate didn’t match
//    In android when you are using http://loopj.com/android-async-http/ async libray . you can face android javax.net.ssl.SSLException hostname in certificate didn’t match exception you can ressolve this exception by replacing
//    AsyncHttpClient client = new AsyncHttpClient();
//    with
//    AsyncHttpClient client = new AsyncHttpClient(true,80,443);




    private static AsyncHttpClient client = new AsyncHttpClient(true,80,443);


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        //client.setMaxRetriesAndTimeout(3,15000); //definimos de maximos reatrys 3 por default son 5
        client.get(getAbsoluteUrl(url), params, responseHandler);
        client.setAuthenticationPreemptive(true);

    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);

    }


    public static void postNumeroIntentosTimeOut(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setMaxRetriesAndTimeout(1,5000);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
