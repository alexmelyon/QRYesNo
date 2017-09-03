package com.example.jcd.qryesno;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class CameraActivity extends AppCompatActivity {

    // QREader
    private SurfaceView mySurfaceView;
    private QREader qrEader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup SurfaceView
        // -----------------
        mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);

        // Init QREader
        // ------------
        qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Log.d("QReader", "QR: " + data);
                String url = ""; // "http://tankionline.com/pages/moscow/get_info/?code=" + data;
                if(BuildConfig.DEBUG) {
//                    url = "http://web-temp.tankionline.com/pages/moscow/get_info/?code=" + data;
                    url = "https://raw.githubusercontent.com/alexmelyon/QRYesNo/master/test_query.json";
                }
                if (!BuildConfig.DEBUG && !DownloadTask.isNetworkConnected(CameraActivity.this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "NETWORK UNAVAILABLE", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    new DownloadTask(url, new DownloadTask.DownloadCallback() {
                        @Override
                        public void onDownload(DownloadTask.DownloadResult result) {
//                            Intent resultIntent = new Intent();
//                            resultIntent.putExtra("text", result);
//                            setResult(RESULT_OK, resultIntent);
//                            finish();
                            if ("".equals(result.error)) {
                                alert(result.result);
                            } else {
                                Toast.makeText(CameraActivity.this, result.error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }).facing(QREader.BACK_CAM)
            .enableAutofocus(true)
            .height(mySurfaceView.getHeight())
            .width(mySurfaceView.getWidth())
            .build();
    }

    private void alert(String jsonString) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(CameraActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(CameraActivity.this);
        }
        try {
            JSONObject json = null;
            json = new JSONObject(jsonString);
            JSONObject response = json.getJSONObject("response");
//            Log.i("QReader", "CONFIRMED: " + (response.has("confirmed") ? response.getString("confirmed") : ""));
            Log.i("QReader", "TICKET: " + response.getString("ticket"));
            Log.i("QReader", "TYPE: " + response.getString("type"));
            Log.i("QReader", "NICK: " + response.getString("nick"));
            Log.i("QReader", "EMAIL: " + response.getString("email"));
//            Log.i("QReader", "REG_DATE: " + (response.has("reg_date") ? response.getString("reg_date") : ""));
//            Log.i("QReader", "ID: " + (response.has("id") ? response.getString("id") : ""));
//            Log.i("QReader", "NAME: " + (response.has("name") ? response.getString("name") : ""));
//            Log.i("QReader", "AGE: " + (response.has("age") ? response.getString("age") : ""));

            builder.setIcon(response.getString("confirmed").equals("0")
                ? R.drawable.ic_check_circle_white_24dp
                : R.drawable.ic_highlight_off_white_24dp);
            builder.setTitle(response.getString("type"));
            String reg_date = response.getString("confirmed").equals("0")
                ? ""
                : "\n" + response.getString("reg_date");
            builder.setMessage(response.getString("nick") + "\n" + response.getString("email") + reg_date);
            builder.show();

        } catch (JSONException e) {
            Toast.makeText(CameraActivity.this, "ERROR JSON", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.e("JCD", "ERROR JSON '" + jsonString + "'", e);

            builder.setIcon(R.drawable.ic_error_white_24dp);
            builder.setMessage(jsonString);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Init and Start with SurfaceView
        // -------------------------------
        qrEader.initAndStart(mySurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Cleanup in onPause()
        // --------------------
        qrEader.releaseAndCleanup();
    }
}
