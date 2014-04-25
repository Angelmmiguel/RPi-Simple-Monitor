package com.rpimonitor.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.rpimonitor.app.models.Cpu;
import com.rpimonitor.app.models.Memory;


public class InfoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent i = getIntent();

        // Introducimos los valores
        initValues(i);

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

    /**
     * Funcion que introduce los valores en las referencias de la interfaz
     * */
    private void initValues(Intent i){

        // Referencias de CPU
        final TextView cpuUser = (TextView) findViewById(R.id.cpu_user);
        final TextView cpuSystem = (TextView) findViewById(R.id.cpu_system);
        final TextView cpuOther = (TextView) findViewById(R.id.cpu_other);
        final TextView cpuFree = (TextView) findViewById(R.id.cpu_free);

        // Referencias de memoria fisica
        final TextView memoryUsed = (TextView) findViewById(R.id.memory_used);
        final TextView memoryFree = (TextView) findViewById(R.id.memory_free);
        final TextView memoryPercent = (TextView) findViewById(R.id.memory_percent);
        final TextView memoryBuffered = (TextView) findViewById(R.id.memory_buffered);

        // Referencias de memoria swap
        final TextView swapUsed = (TextView) findViewById(R.id.swap_used);
        final TextView swapFree = (TextView) findViewById(R.id.swap_free);
        final TextView swapPercent = (TextView) findViewById(R.id.swap_percent);
        final TextView swapBuffered = (TextView) findViewById(R.id.swap_buffered);

        Cpu cpu = (Cpu) i.getSerializableExtra("Cpu");

        cpuUser.setText("Usuario: " + String.valueOf(cpu.getUser()) + "%");
        cpuSystem.setText("Sistema: " + String.valueOf(cpu.getSystem())+ "%");
        cpuOther.setText("Otros: " + String.valueOf(cpu.getOther())+ "%");
        cpuFree.setText("Libre: " + String.valueOf(cpu.getFree())+ "%");

        Memory physical = (Memory) i.getSerializableExtra("MemoryPhysical");

        memoryUsed.setText("En uso: " + String.format("%.2f",physical.getMBUsed()) + "MB");
        memoryFree.setText("Libre: " + String.format("%.2f",physical.getMBFree()) + "MB");
        memoryBuffered.setText("En buffer: " + String.format("%.2f",physical.getMBCached()) + "MB");
        memoryPercent.setText("Porcentaje de uso: " + String.valueOf(physical.getPercent()) + "%");

        Memory swap = (Memory) i.getSerializableExtra("MemorySwap");

        swapUsed.setText("En uso: " + String.format("%.2f",swap.getMBUsed()) + "MB");
        swapFree.setText("Libre: " + String.format("%.2f",swap.getMBFree()) + "MB");
        swapBuffered.setText("En caché: " + String.format("%.2f",swap.getMBCached()) + "MB");
        swapPercent.setText("Porcentaje de uso: " + String.valueOf(swap.getPercent()) + "%");


    }
}
