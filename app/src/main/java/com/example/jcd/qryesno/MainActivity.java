package com.example.jcd.qryesno;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final int REQ_SCAN = 0;
    private final int REQ_CAMERA = 1;
    private final String KEY_TYPE = "type";
    private final String KEY_NICK = "name";
    private final String KEY_EMAIL = "email";
    private final String KEY_TIME = "time";

    public static MainActivity instance;

    private ListView members_list;
    private List<Map<String, String>> members = new ArrayList<>();
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        members_list = (ListView) findViewById(R.id.members_list);
        adapter = new SimpleAdapter(MainActivity.this,
                members,
                R.layout.member_item,
                new String[]{KEY_TYPE, KEY_NICK, KEY_EMAIL, KEY_TIME},
                new int[]{R.id.item_type, R.id.item_name, R.id.item_job_title, R.id.item_time});
        members_list.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
        }
    }

    public void onClick(View onClick) {
        if (onClick.getId() == R.id.scan_button) {
            startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), REQ_SCAN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("JCD", "ON ACTIVITY RESULT: req=" + requestCode + ", res=" + resultCode);
        if (requestCode == REQ_SCAN && resultCode == RESULT_OK) {
            String jsonString = data.getStringExtra("text");
            addItem(jsonString);
        }
    }

    public void addItem(String jsonString) {
        String type = "";
        String nick = "";
        String email = "";
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONObject response = json.getJSONObject("response");
            type = response.getString("type");
            if (response.has("confirmed") && response.getString("confirmed").equals("1")) {
                type = "ERROR";
            }
            nick = response.getString("nick");
            email = response.getString("email");

        } catch (JSONException e) {
            Log.e("JCD", "ERROR JSON '" + jsonString + "'", e);
            Toast.makeText(MainActivity.this, "ERROR JSON", Toast.LENGTH_LONG).show();
            type = "ERROR";
        }

        Map<String, String> person = new HashMap<>();
//            person.put(KEY_ICON, R.drawable.ic_check_circle_white_24dp);
        person.put(KEY_TYPE, type.substring(0, 1).toUpperCase());
        person.put(KEY_NICK, nick);
        person.put(KEY_EMAIL, email);
        person.put(KEY_TIME, getCurrentHHmm());
        members.add(0, person);
        adapter.notifyDataSetChanged();
    }

    private String getCurrentHHmm() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String HHmm = format.format(new Date());
        return HHmm;
    }
}
