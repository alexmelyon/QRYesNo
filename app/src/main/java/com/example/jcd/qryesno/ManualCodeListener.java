package com.example.jcd.qryesno;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by JCD on 06.09.2017.
 */

public class ManualCodeListener implements View.OnKeyListener {

    private Context context;

    public ManualCodeListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            final EditText edit = (EditText) view;
            Log.i("JCD", "ENTER " + edit.getText().toString());
            new DownloadTask(MainApp.getUrl(edit.getText().toString()), new DownloadTask.DownloadCallback() {
                @Override
                public void onDownload(DownloadTask.DownloadResult result) {
                    if (result.isOk) {
                        edit.getText().clear();
                        ColoredAlert.show(context, result.type, result.type, result.nick + "\n" + result.email, null);
                    } else {
                        edit.setBackgroundColor(Color.parseColor("#FF8888"));
                    }
                }
            });
        } else {
            view.setBackgroundColor(Color.WHITE);
        }
        return false;
    }
}
