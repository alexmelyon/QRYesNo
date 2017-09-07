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

public class DownloadTask extends AsyncTask<String, Void, DownloadTask.DownloadResult> {

    private String urlString = "";
    private DownloadCallback callback;

    public class DownloadResult {
        public boolean isOk;
        public String errorMessage;
        public String ticket;
        public String type;
        public String nick;
        public String email;
        public String confirmed;
        public DownloadResult(boolean error, String errorMessage, String ticket, String type, String nick, String email, String confirmed) {
            this.isOk = error;
            this.errorMessage = errorMessage;
            this.ticket = ticket;
            this.type = type;
            this.nick = nick;
            this.email = email;
            this.confirmed = confirmed;
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
        String jsonString = "";
        try {
            if(MainApp.isDebug()) {
                return new DownloadResult(true, "", "TICKET", "STANDART", "NICK", "EMAIL", "0");
            }
            InputStream input = new URL(this.urlString).openConnection().getInputStream();
            String result = fromInputStream(input);
            Log.i("QReader", "RESULT: " + result);
            jsonString = result;
            JSONObject json = new JSONObject(result);
            String responseType = json.getString("responseType");
            JSONObject response = json.getJSONObject("response");
            String ticket = response.getString("ticket");
            String type = response.getString("type");
            String nick = response.getString("nick");
            String email = response.getString("email");
            String confirmed = response.getString("confirmed");
            if("ERROR".equals(responseType)) {
                String responseError = json.getString("response");
                return new DownloadResult(false, responseError, ticket, type, nick, email, confirmed);
            } else {
                return new DownloadResult(true, "", ticket, type, nick, email, confirmed);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("QReader", "Cannot download url '" + urlString + "'", e);
            return new DownloadResult(false, e.getLocalizedMessage(), "", "", "", "", "");
        } catch (JSONException e) {
            Log.e("QReader", "Cannot parse json '" + jsonString + "'", e);
            return new DownloadResult(false, e.getLocalizedMessage(), "", "", "", "", "");
        }
    }

    @Override
    protected void onPostExecute(DownloadResult res) {
        if(res.isOk) {
            MainActivity.instance.addItem(res.type, res.nick, res.email);
        }
        this.callback.onDownload(res);
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
