package com.rpimonitor.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.rpimonitor.app.models.Cpu;

import org.json.JSONException;
import org.json.JSONObject;


public class InfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        final TextView cpuUser = (TextView) findViewById(R.id.cpu_user);
        final TextView cpuSystem = (TextView) findViewById(R.id.cpu_system);
        final TextView cpuOther = (TextView) findViewById(R.id.cpu_other);
        final TextView cpuFree = (TextView) findViewById(R.id.cpu_free);

        Intent i = getIntent();

        Cpu cpu = (Cpu) i.getSerializableExtra("CPU");

        cpuUser.setText("User: " + String.valueOf(cpu.getUser()) + "%");
        cpuSystem.setText("System: " + String.valueOf(cpu.getSystem())+ "%");
        cpuOther.setText("Other: " + String.valueOf(cpu.getOther())+ "%");
        cpuFree.setText("Free: " + String.valueOf(cpu.getFree())+ "%");

        // Iniciamos el actionBar
        getSupportActionBar().setTitle(i.getStringExtra("Hostname"));
        getSupportActionBar().setSubtitle(i.getStringExtra("IP"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
