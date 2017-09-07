package com.example.jcd.qryesno;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by JCD on 06.09.2017.
 */

public class ManualCodeListener implements View.OnKeyListener, TextWatcher {

    private Context context;
    private EditText edit;

    public ManualCodeListener(Context context, EditText edit) {
        this.context = context;
        this.edit = edit;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            Log.i("JCD", "ENTER " + edit.getText().toString());
            new DownloadTask(MainApp.getUrl(edit.getText().toString()), new DownloadTask.DownloadCallback() {
                @Override
                public void onDownload(DownloadTask.DownloadResult result) {
                    if (result.isOk) {
                        edit.getText().clear();
                        if(result.confirmed.equals("0")) {
                            ColoredAlert.show(context, result.type, result.type, result.nick + "\n" + result.email, null);
                        } else {
                            ColoredAlert.show(context, "ERROR", "Повтор", result.nick + "\n" + result.email, null);
                        }
                    } else {
                        edit.setBackgroundColor(Color.parseColor("#FF8888"));
                    }
                }
            });
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        edit.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //
    }
}
