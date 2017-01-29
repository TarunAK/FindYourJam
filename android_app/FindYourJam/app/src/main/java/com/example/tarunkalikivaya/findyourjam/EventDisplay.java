package com.example.tarunkalikivaya.findyourjam;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

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


    @Override
    public void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_event_display);

        setTitle("");

        mTitle = (TextView)findViewById(R.id.ev_title);
        mDescription = (TextView)findViewById(R.id.ev_description);
        mDate = (TextView)findViewById(R.id.ev_date);
        mLocation = (TextView)findViewById(R.id.ev_location);

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
            String urlEndPoint = Constants.WEB_URL + "/api/v1/auth/get_event" ;
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
                client.setDoOutput(true);
                OutputStream os = client.getOutputStream();
                os.write(obj.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
                if(client.getResponseCode()!= 200){
                    return null;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String text = br.readLine();
                JSONObject response= new JSONObject(text);

                status = response.getString("status");
                if (!status.equals("success")){
                    return   null;
                }
                String[] array = new String[4];
                array[0] = response.getString("title");
                array[1] = response.getString("description");
                array[2] = response.getString("date");
                array[3] = response.getString("location");
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
            }else{
                Toast.makeText(getApplicationContext(),"Error Loading Event",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
