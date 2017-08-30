package com.example.jcd.qryesno;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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
        JSONObject json = null;
        try {
//            String result = (String)new URL(this.urlString).openConnection().getContent();
            InputStream input = new URL(this.urlString).openConnection().getInputStream();
            String result = fromInputStream(input);
            Log.i("QReader", "RESULT: " + result);
            json = new JSONObject(result);
            Log.i("QReader", "RESPONSE TYPE: " + json.getString("responseType"));
            JSONObject response = json.getJSONObject("response");
            Log.i("QReader", "ID: " + response.getString("id"));
            Log.i("QReader", "TICKET: " + response.getString("ticket"));
            Log.i("QReader", "NICK: " + response.getString("nick"));
            Log.i("QReader", "EMAIL: " + response.getString("email"));
            Log.i("QReader", "REG_DATE: " + response.getString("reg_date"));
            Log.i("QReader", "CONFIRMED: " + response.getString("confirmed"));
            Log.i("QReader", "NAME: " + response.getString("name"));
            Log.i("QReader", "AGE: " + response.getString("age"));
            Log.i("QReader", "TYPE: " + response.getString("type"));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("QReader", "Cannot download url '" + urlString + "'", e);
            return "Error: " + e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("QReader", "Cannot parse json: '" + json + "'", e);
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
