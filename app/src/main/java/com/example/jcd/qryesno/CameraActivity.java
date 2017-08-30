package com.example.jcd.qryesno;

import android.content.Intent;
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
                Intent resultIntent = new Intent();
                resultIntent.putExtra("text", data);
                setResult(RESULT_OK, resultIntent);
                finish();
                String url = "http://web-temp.tankionline.com/pages/moscow/get_info/?code=" + data;
                if (!DownloadTask.isNetworkConnected(CameraActivity.this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "NETWORK UNAVAILABLE", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    new DownloadTask(url, new DownloadTask.DownloadCallback() {
                        @Override
                        public void onDownload(String data) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("text", data);
                            setResult(RESULT_OK, resultIntent);
                            finish();
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
