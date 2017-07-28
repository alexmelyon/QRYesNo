package com.example.jcd.qryesno;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final int REQ_SCAN = 0;
    private final String KEY_NAME = "name";
    private final String KEY_JOB_TITLE = "jobTitle";

    private ListView members_list;
    private List<Map<String, String>> members = new ArrayList<>();
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        members_list = (ListView) findViewById(R.id.members_list);
        adapter = new SimpleAdapter(MainActivity.this,
                members,
                android.R.layout.simple_list_item_2,
                new String[]{KEY_NAME, KEY_JOB_TITLE},
                new int[]{android.R.id.text1, android.R.id.text2});
        members_list.setAdapter(adapter);
    }

    public void onClick(View onClick) {
        if (onClick.getId() == R.id.scan_button) {
            startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), REQ_SCAN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SCAN && resultCode == RESULT_OK) {
            String text = data.getStringExtra("text");
            Map<String, String> person = new HashMap<>();
            person.put(KEY_NAME, text);
            person.put(KEY_JOB_TITLE, KEY_JOB_TITLE);
            members.add(0, person);
            adapter.notifyDataSetChanged();
        }
    }
}
