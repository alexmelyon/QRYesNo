package com.example.jcd.qryesno;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by melekhin on 30.08.2017.
 */

public class DownloadTask extends AsyncTask<String, Void, String> {

    private String urlString = "";
    private DownloadCallback callback;

    public interface DownloadCallback {
        public void onDownload(String result);
    }

    public DownloadTask(String url, DownloadCallback callback) {
        this.urlString = url;
        this.callback = callback;
        this.execute(url);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
//            String result = (String)new URL(this.urlString).openConnection().getContent();
            InputStream input = new URL(this.urlString).openConnection().getInputStream();
            String result = fromInputStream(input);
            Log.i("QReader", "RESULT: " + result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("QReader", "Cannot download url '" + urlString + "'", e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        this.callback.onDownload(s);
    }

    private String fromInputStream(InputStream in) throws IOException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        return responseStrBuilder.toString();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
