package com.example.jcd.qryesno;

import android.content.DialogInterface;
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
    private volatile boolean isDetected = false;

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
            public void onDetected(final String code) {
                Log.d("QReader", "QR: " + code);
                if(isDetected) {
                    Log.i("JCD", "Already showing alert");
                    return;
                }
                isDetected = true;

                if (!DownloadTask.isNetworkConnected(CameraActivity.this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "NETWORK UNAVAILABLE", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    String url = MainApp.getUrl(code);
                    new DownloadTask(url, new DownloadTask.DownloadCallback() {
                        @Override
                        public void onDownload(DownloadTask.DownloadResult result) {
                            if (MainApp.isDebug()) {
                                createAlert(code);
                            } else if ("".equals(result.error)) {
                                createAlert(result.result);
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

    private void createAlert(String jsonString) {
        MainActivity.instance.addItem(jsonString);

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(CameraActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(CameraActivity.this);
        }
        try {
            JSONObject json = new JSONObject(jsonString);
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

            if (response.has("confirmed") && response.getString("confirmed").equals("0")) {
                builder.setIcon(R.drawable.ic_check_circle_white_24dp);
                builder.setTitle(response.getString("type"));
                builder.setMessage(response.getString("nick") + "\n" + response.getString("email"));
            } else if (response.has("confirmed") && response.getString("confirmed").equals("1")) {
                builder.setIcon(R.drawable.ic_highlight_off_white_24dp);
                builder.setTitle("Повтор");
                String reg_date = "";
                if (response.has("reg_date")) {
                    reg_date = response.getString("reg_date");
                }
                builder.setMessage("Уже просканирован\n'" + reg_date + "'");
            } else {
                builder.setIcon(R.drawable.ic_help_outline_white_24dp);
                builder.setTitle(response.getString("type"));
                builder.setMessage(response.getString("nick") + "\n" + response.getString("email"));
            }

        } catch (JSONException e) {
            Toast.makeText(CameraActivity.this, "ERROR JSON", Toast.LENGTH_LONG).show();
            Log.e("JCD", "ERROR JSON '" + jsonString + "'", e);

            builder.setIcon(R.drawable.ic_error_white_24dp);
            builder.setTitle("Error json");
            builder.setMessage(jsonString);
        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isDetected = false;
            }
        });
        builder.create().show();
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
