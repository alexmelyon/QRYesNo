package com.example.jcd.qryesno;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.Toast;

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
                if (isDetected) {
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
                                createAlert("OK", "STANDART", "NICK\nEMAIL");
                            } else if (result.isOk) {
                                createAlert(result.type, result.type, result.nick + "\n" + result.email);
                            } else {
                                createAlert("ERROR", "Ошибка", result.errorMessage);
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

    private void createAlert(String type, String title, String message) {

        ColoredAlert.show(CameraActivity.this, type, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isDetected = false;
            }
        });
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
