package com.example.jcd.qryesno;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class MainActivity extends AppCompatActivity {

//    private TextView text;

    // QREader
    private SurfaceView mySurfaceView;
    private QREader qrEader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        ((ToggleButton)findViewById(R.id.toggleButton)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    qrEader.start();
                } else {
                    qrEader.stop();
                }
            }
        });

        // Setup SurfaceView
        // -----------------
        mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);

        // Init QREader
        // ------------
        qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                Log.d("QREader", "Value : " + data);
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG);
//                text.post(new Runnable() {
//                    @Override
//                    public void run() {
////                        text.setText(data);
//                    }
//                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();
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
