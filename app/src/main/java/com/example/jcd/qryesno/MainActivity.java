package com.example.jcd.qryesno;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private EditText manualEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        manualEdit = (EditText)findViewById(R.id.manual_code);
        ManualCodeListener manualCodeListener = new ManualCodeListener(MainActivity.this, manualEdit);
        manualEdit.setOnKeyListener(manualCodeListener);
        manualEdit.addTextChangedListener(manualCodeListener);
        members_list = (ListView) findViewById(R.id.members_list);
        adapter = new SimpleAdapter(MainActivity.this,
            members,
            R.layout.member_item,
            new String[]{KEY_TYPE, KEY_NICK, KEY_EMAIL, KEY_TIME},
            new int[]{R.id.item_type, R.id.item_name, R.id.item_job_title, R.id.item_time});
        members_list.setAdapter(adapter);
        loadPrefs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // TODO manual code

    public void onClick(View onClick) {
        if (onClick.getId() == R.id.scan_button) {
            startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), REQ_SCAN);
        }
    }

    public void addItem(String type, String nick, String email) {

        Map<String, String> person = new HashMap<>();
        person.put(KEY_TYPE, type.substring(0, 1).toUpperCase());
        person.put(KEY_NICK, nick);
        person.put(KEY_EMAIL, email);
        person.put(KEY_TIME, getCurrentHHmm());
        members.add(0, person);
        adapter.notifyDataSetChanged();

        savePrefs();
    }

    private String getCurrentHHmm() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String HHmm = format.format(new Date());
        return HHmm;
    }

    private void savePrefs() {
        Log.i("JCD", "SAVE PREFS");
        Set<String> membersSet = new ArraySet<>();
        try {
            for (Map<String, String> item : this.members) {
                JSONObject jsonMember = new JSONObject();
                for (String key : item.keySet()) {
                    jsonMember.put(key, item.get(key));
                }
                membersSet.add(jsonMember.toString());
            }
        } catch (JSONException e) {
            Log.e("JCD", "ERROR SAVE PREFS", e);
        }
        getPreferences(MODE_PRIVATE).edit().putStringSet("members", membersSet).apply();
    }

    private void loadPrefs() {
        Log.i("JCD", "LOAD PREFS");
        Set<String> _default = new ArraySet<>();
        Set<String> membersSet = getPreferences(MODE_PRIVATE).getStringSet("members", _default);
        try {
            this.members.clear();
            for (String item : membersSet) {
                Map<String, String> member = new HashMap<>();
                JSONObject json = new JSONObject(item);
                Iterator<String> iter = json.keys();
                while(iter.hasNext()) {
                    String key = iter.next();
                    String value = json.getString(key);
                    member.put(key, value);
                }
                this.members.add(member);
            }
        } catch (JSONException e) {
            Log.e("JCD", "ERROR LOAD PREFS", e);
        }
    }
}
