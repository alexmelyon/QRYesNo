package com.example.jcd.qryesno;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;

/**
 * Created by JCD on 06.09.2017.
 */

public class ColoredAlert {

    public static void show(Context context, String type, String title, String message, DialogInterface.OnClickListener listener) {

        int theme = getTheme(type);
        int icon = getIcon(type);

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, theme);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setIcon(icon);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", listener);

        final AlertDialog alert = builder.create();
        final int finalTheme = theme;
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if(finalTheme == R.style.YellowAlert) {
                    alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                } else {
                    alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                }
            }
        });
        alert.show();
    }

    private static int getIcon(String type) {
        String first = type.substring(0, 1).toUpperCase();
        if(first.equals("E")) {
            return R.drawable.ic_error_outline_white_24dp;
        }
        return R.drawable.ic_check_circle_white_24dp;
    }

    private static int getTheme(String type) {
        String first = type.substring(0, 1).toUpperCase();
        if (first.equals("S")) {
            return R.style.GreenAlert;
        } else if (first.equals("A")) {
            return R.style.BlueAlert;
        } else if (first.equals("V")) {
            return R.style.YellowAlert;
        }
        return android.R.style.Theme_Dialog;
    }
}
