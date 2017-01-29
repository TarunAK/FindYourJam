package com.example.tarunkalikivaya.findyourjam;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarunkalikivaya.findyourjam.eventlistattend.DisplayEventListAttendActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chrisexn on 1/29/2017.
 */

public class EventDisplay extends AppCompatActivity {

    private TextView mTitle;
    private TextView mDescription;
    private TextView mDate;
    private TextView mLocation;
    private Button mAttend;

    @Override
    public void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_event_display);

        mTitle = (TextView)findViewById(R.id.ev_title);
        mDescription = (TextView)findViewById(R.id.ev_description);
        mDate = (TextView)findViewById(R.id.ev_date);
        mLocation = (TextView)findViewById(R.id.ev_location);
        mAttend = (Button)findViewById(R.id.attend);
        mAttend.setVisibility(View.GONE);
        mAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Attend().execute(Constants.getToken(getBaseContext()),getIntent().getStringExtra("id"));
            }
        });

        new GetEvent().execute(Constants.getToken(this),getIntent().getStringExtra("id"));
    }

    /**
     * Start a login network call
     */
    private class GetEvent extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String ... str){
            String token = str[0];
            String id = str[1];
            String status = "";
            String urlEndPoint = Constants.WEB_URL + "/api/v1/events/get_event" ;
            JSONObject obj = new JSONObject();
            try {
                obj.put("event_id",id);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            //Run the api call
            HttpURLConnection client = null;
            try {
                URL url = new URL(urlEndPoint);
                client =(HttpURLConnection) url.openConnection();
                client.setConnectTimeout(10000);
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                client.setRequestProperty("Authorization", " Token " + token);
                client.setDoOutput(true);
                OutputStream os = client.getOutputStream();
                os.write(obj.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
                int t = client.getResponseCode();
                if(t!= 200){
                    return null;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String text = br.readLine();
                JSONObject response= new JSONObject(text);

                status = response.getString("status");
                if (!status.equals("success")){
                    return   null;
                }
                String[] array = new String[5];
                array[0] = response.getString("title");
                array[1] = response.getString("description");
                array[2] = response.getString("date");
                array[3] = response.getString("location");
                array[4] = response.getString("same");
                return  array;
            }catch (Exception e){
                e.printStackTrace();
                if(client!=null){
                    client.disconnect();
                }
                return null;
            }finally {
                if(client!=null){
                    client.disconnect();
                }
            }

        }
        @Override
        protected void onPostExecute(String[] output){
            if(output!=null){
                mTitle.setText(output[0]);
                mDescription.setText(output[1]);
                mDate.setText(output[2]);
                mLocation.setText(output[3]);
                if(output[4].equals("false")){
                    mAttend.setVisibility(View.VISIBLE);
                }
            }else{
                Toast.makeText(getApplicationContext(),"Error Loading Event",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * Start a login network call
     */
    private class Attend extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String ... str){
            String token = str[0];
            String id = str[1];
            String status = "";
            String urlEndPoint = Constants.WEB_URL + "/api/v1/events/attend_event" ;
            JSONObject obj = new JSONObject();
            try {
                obj.put("event_id",id);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            //Run the api call
            HttpURLConnection client = null;
            try {
                URL url = new URL(urlEndPoint);
                client =(HttpURLConnection) url.openConnection();
                client.setConnectTimeout(10000);
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                client.setRequestProperty("Authorization", " Token " + token);
                client.setDoOutput(true);
                OutputStream os = client.getOutputStream();
                os.write(obj.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
                int t = client.getResponseCode();
                if(t!= 200){
                    return null;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String text = br.readLine();
                JSONObject response= new JSONObject(text);

                status = response.getString("status");
                if (!status.equals("success")){
                    return   null;
                }
                return  true;
            }catch (Exception e){
                e.printStackTrace();
                if(client!=null){
                    client.disconnect();
                }
                return null;
            }finally {
                if(client!=null){
                    client.disconnect();
                }
            }

        }
        @Override
        protected void onPostExecute(Boolean output){
            if(output){
                Toast.makeText(getApplicationContext(),"Attending Event",Toast.LENGTH_SHORT).show();
                finish();

            }else{
                Toast.makeText(getApplicationContext(),"Error Attending Event",Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }
}
