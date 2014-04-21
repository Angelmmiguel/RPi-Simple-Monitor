package com.rpimonitor.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rpimonitor.app.models.Cpu;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Button connect = (Button)findViewById(R.id.connect);
        final EditText url = (EditText)findViewById(R.id.edit_ip);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // FIXME comprobar si tenemos el campo vacio
                new ConnectTask().execute(url.getText().toString());

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    private class ConnectTask extends AsyncTask<String, String, Void> {

        InputStream inputStream = null;
        String result = "";
        String url;
        String url_base;

        @Override
        protected Void doInBackground(String... urls) {

            url_base = urls[0];
            url = url_base + ":61456/stats.json";

            if(!url.startsWith("http://")){
                url = "http://" + url;
            }

            /*try {
                url = URLEncoder.encode(urls[0] + ":61456/stats.json", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/

            try {
                // Set up HTTP post

                // HttpClient is more then less deprecated. Need to change to URLConnection
                HttpClient httpClient = new DefaultHttpClient();

                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();

                // Read content & Log
                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                Log.e("UnsupportedEncodingException", e1.toString());
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
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
                Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
            }

            return null;
        }

        protected void onPostExecute(Void v) {

            //parse JSON data
            try {

                JSONObject response = new JSONObject(result);

                if(response.has("status") && response.getBoolean("status")){

                    JSONObject cpuJson = response.getJSONObject("result").getJSONObject("cpu");

                    Cpu cpu = new Cpu(
                            (float)cpuJson.getDouble("user" ),
                            (float)cpuJson.getDouble("system"),
                            (float)cpuJson.getDouble("free"));

                    Intent info = new Intent(getBaseContext(), InfoActivity.class);
                    info.putExtra("CPU", cpu);
                    info.putExtra("Hostname", response.getJSONObject("result").getString("name"));
                    info.putExtra("IP", url_base);
                    startActivity(info);

                } else {

                    // FIXME Mostrar un error, coger el valor de reason si lo hay

                }

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)

        }
    }

}
