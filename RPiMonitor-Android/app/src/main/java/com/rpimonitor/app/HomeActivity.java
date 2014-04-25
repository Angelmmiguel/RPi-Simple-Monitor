package com.rpimonitor.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rpimonitor.app.models.Cpu;
import com.rpimonitor.app.models.Memory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;


public class HomeActivity extends ActionBarActivity {

    // Nombre de la variable en las preferencias
    public static final String PREFS_DEVICES = "SavedDevices";
    // Tiempo de espera de informacion
    public static final int TIMEOUT = 5000;
    public static final int CONNECTION_TIMEOUT = 4000;

    // Preferencias
    SharedPreferences settings;
    String devices;

    // Dialogo de carga
    ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Button connect = (Button)findViewById(R.id.connect);
        final EditText url = (EditText)findViewById(R.id.edit_ip);

        // Restore preferences
        settings = getPreferences(MODE_PRIVATE);
        devices = settings.getString(PREFS_DEVICES, "");

        if(devices.equals("")){
            // No hay dispositivos previos, mostramos el aviso
            TextView advice = (TextView)findViewById(R.id.no_devices);
            advice.setVisibility(View.VISIBLE);

            // Agregamos focus al edit text
            url.requestFocus();

        } else {
            // Hay dispositivos, iniciamos la lista!

            ListView deviceList = (ListView)findViewById(R.id.save_devices_list);
            deviceList.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.device_list_item, devices.split(",")));

            // Capturamos los clicks
            deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    // Mostramos el dialogo
                    pdialog.show();
                    new ConnectTask().execute(((TextView) view).getText().toString());

                }
            });

        }

        // Iniciamos el dialogo de carga
        // Set the progress dialog
        pdialog = new ProgressDialog(HomeActivity.this);
        //pdialog.setTitle(getResources().getString(R.string.home_dialog_title));
        pdialog.setMessage(getResources().getString(R.string.home_dialog_text));
        pdialog.setCancelable(false);
        pdialog.setIndeterminate(true);

        // Atendemos al click en conectar
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // FIXME comprobar si tenemos el campo vacio
                // Mostramos el dialogo
                pdialog.show();
                new ConnectTask().execute(url.getText().toString());

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    private class ConnectTask extends AsyncTask<String, String, Void> {

        InputStream inputStream = null;
        String result = "";
        String url;
        String url_base;

        int error = 0;

        @Override
        protected Void doInBackground(String... urls) {

            url_base = urls[0];
            url = url_base + ":61456/stats.json";

            // Anyadimos el http
            if(!url.startsWith("http://")){
                url = "http://" + url;
            }

            /*try {
                url = URLEncoder.encode(urls[0] + ":61456/stats.json", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
            try {
                // Set up HTTP get

                HttpGet httpGet = new HttpGet(url);
                // Reducimos el TIMEOUT
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT);

                // Iniciamos el cliente http
                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

                // Ejecutamos la llamada
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();

                // Read content & Log
                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                pdialog.dismiss();
                error = 1;
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                pdialog.dismiss();
                error = 2;
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                pdialog.dismiss();
                error = 3;
                e3.printStackTrace();
            } catch (IOException e4) {
                pdialog.dismiss();
                Log.e("IOException", e4.toString());
                error = 4;
                e4.printStackTrace();
            }
            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

            } catch (Exception e) {

                pdialog.dismiss();

                // Si no es el 4 hemos tenido problemas para recibir los datos
                if(error != 4){
                    error = 5;
                }
            }

            return null;
        }

        protected void onPostExecute(Void v) {

            pdialog.dismiss();

            if(error == 0) {

                //parse JSON data
                try {

                    JSONObject response = new JSONObject(result);

                    if (response.has("status") && response.getBoolean("status")) {

                        JSONObject cpuJson = response.getJSONObject("result").getJSONObject("cpu");

                        // Obtenemos los objetos y los guardamos para enviarlos a la siguiente actividad
                        Cpu cpu = new Cpu(
                                (float) cpuJson.getDouble("user"),
                                (float) cpuJson.getDouble("system"),
                                (float) cpuJson.getDouble("free"));

                        JSONObject memoryJson = response.getJSONObject("result").getJSONObject("memory");

                        JSONObject physicalMemory = memoryJson.getJSONObject("physical");

                        Memory physical = new Memory(
                                (float) physicalMemory.getDouble("used"),
                                (float) physicalMemory.getDouble("free"),
                                (float) physicalMemory.getDouble("percent"),
                                (float) physicalMemory.getDouble("buffered")
                        );

                        JSONObject swapMemory = memoryJson.getJSONObject("swap");

                        Memory swap = new Memory(
                                (float) swapMemory.getDouble("used"),
                                (float) swapMemory.getDouble("free"),
                                (float) swapMemory.getDouble("percent"),
                                (float) swapMemory.getDouble("cached")
                        );

                        // Guardamos la nueva IP!!!
                        saveDevice(url_base);

                        // Iniciamos la info
                        Intent info = new Intent(getBaseContext(), InfoActivity.class);
                        info.putExtra("Cpu", cpu);
                        info.putExtra("MemoryPhysical", physical);
                        info.putExtra("MemorySwap", swap);
                        info.putExtra("Hostname", response.getJSONObject("result").getString("name"));
                        info.putExtra("IP", url_base);
                        startActivity(info);

                    } else {

                        pdialog.dismiss();

                        // Comprobamos si el servidor ha devuelto un error
                        if(response.has("reason")){
                            Toast.makeText(getApplicationContext(), "El servidor no ha podido obtener los datos: " + response.getString("reason"), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "El servidor no ha podido obtener los datos", Toast.LENGTH_LONG).show();
                        }

                    }

                } catch (JSONException e) {
                    // Error al parsear el JSON
                    pdialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Los datos no se han recibido correctamente", Toast.LENGTH_LONG).show();

                } // catch (JSONException e)
            } else {

                // Hay error
                switch (error){
                    case 4:
                        Toast.makeText(getApplicationContext(), "El dispositivo no tiene instalado el servidor o no responde", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Los datos no se han recibido correctamente", Toast.LENGTH_LONG).show();
                        break;
                }

            }

        }
    }

    /**
     * Guarda la ip en el storage para acceder mas rapido al dispositivo
     * */
    private void saveDevice(String device){

        // Comprobamos si existe la entrada
        if(devices.contains(device)) {
            // Existe, la colocamos la primera para tenerla por orden cronologico
            devices = devices.replace(device + ",", "");
            devices = devices.replace(device, "");
        }

        if(devices.equals("")){
            settings.edit().putString(PREFS_DEVICES,device).commit();
        } else {
            // Guardamos
            settings.edit().putString(PREFS_DEVICES, device + "," + devices).commit();
        }
    }

}
