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

public class DownloadTask extends AsyncTask<String, Void, DownloadTask.DownloadResult> {

    private String urlString = "";
    private DownloadCallback callback;

    public class DownloadResult {
        public String result;
        public String error;
        public DownloadResult(String result, String error) {
            this.result = result;
            this.error = error;
        }
    }
    public interface DownloadCallback {
        public void onDownload(DownloadResult result);
    }

    public DownloadTask(String url, DownloadCallback callback) {
        this.urlString = url;
        this.callback = callback;
        this.execute(url);
    }

    @Override
    protected DownloadResult doInBackground(String... strings) {
        try {
//            String result = (String)new URL(this.urlString).openConnection().getContent();
            if(MainApp.isDebug()) {
                return new DownloadResult("Debug result", "");
            }
            InputStream input = new URL(this.urlString).openConnection().getInputStream();
            String result = fromInputStream(input);
            Log.i("QReader", "RESULT: " + result);
            return new DownloadResult(result, "");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("QReader", "Cannot download url '" + urlString + "'", e);
            return new DownloadResult("", "Error: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(DownloadResult s) {
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
        if(MainApp.isDebug()) {
            return true;
        }
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
