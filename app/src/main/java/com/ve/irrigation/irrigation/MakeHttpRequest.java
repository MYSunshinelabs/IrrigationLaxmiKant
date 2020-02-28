package com.ve.irrigation.irrigation;

import android.util.Log;

import com.ve.irrigation.DataValues.HeartBeat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * Created by laxmi on 13/4/18.
 */

public class MakeHttpRequest {


    private static MakeHttpRequest makeHttpRequest;

    private MakeHttpRequest() {

    }


    public static MakeHttpRequest getMakeHttpRequest() {
        if (makeHttpRequest == null)
            makeHttpRequest = new MakeHttpRequest();

        return makeHttpRequest;
    }


    public String makeRequest(String string) throws IOException {
        //URL url = new URL("http://192.168.43.252:80/wtget/0");
        URL url = new URL(string);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);
        } catch (Exception e) {
            Log.i("Exception: ", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return "";

    }


    private String readStream(InputStream inputStream) throws IOException {


        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null)

        {
            total.append(line).append('\n');

        }

        Log.i("output: ", total.toString());
        return String.valueOf(total);
    }


    public Observable<String> sendRequest(final String url) {
        return Observable.create(new Observable.OnSubscribe<String>() {

                                     @Override
                                     public void call(Subscriber<? super String> subscriber) {

                                         try {
                                             String message = makeRequest(url);
                                             subscriber.onNext(message);

                                         } catch (IOException e) {
                                             e.printStackTrace();
                                             subscriber.onError(new Exception("No Data Found"));
                                         }
                                     }
                                 }
        );
    }






}
