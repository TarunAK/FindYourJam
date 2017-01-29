package com.example.tarunkalikivaya.findyourjam.eventlistscreated;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarunkalikivaya.findyourjam.Constants;
import com.example.tarunkalikivaya.findyourjam.EventDisplay;
import com.example.tarunkalikivaya.findyourjam.R;
import com.example.tarunkalikivaya.findyourjam.eventlistattend.DisplayEventListAttendActivity;
import com.example.tarunkalikivaya.findyourjam.eventlistattend.EventAttendObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chrisexn on 1/28/2017.
 */

public class DisplayEventListActivity extends AppCompatActivity {

    private ListView mListView;
    private TextView mTextView;
    private EventListAdapter mAdapater;
    private ArrayList<EventObject> mList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_event_list);
        setTitle("Hosted Events");
        mListView = (ListView)findViewById(R.id.list_view);
        mTextView = (TextView)findViewById(R.id.empty);
        mListView.setEmptyView(mTextView);

        mAdapater = new EventListAdapter(this,mList);

        mListView.setAdapter(mAdapater);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventObject obj = mList.get(position);
                Intent intent = new Intent(DisplayEventListActivity.this,EventDisplay.class);
                intent.putExtra("id",obj.getId());
                startActivity(intent);
            }
        });
        new GetEventsCreated().execute(Constants.getToken(this));
    }


    /**
     * Logout
     */
    private class GetEventsCreated extends AsyncTask<String, Void, List> {
        @Override
        protected List doInBackground(String ... str){
            String token = str[0];
            String status = "";
            ArrayList<EventObject> list = null;
            String urlEndPoint = Constants.WEB_URL + "/api/v1/events/get_user_events" ;

            //Run the api call
            HttpURLConnection client = null;
            try {
                URL url = new URL(urlEndPoint);
                client =(HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");
                client.setConnectTimeout(10000);
                client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                client.setRequestProperty("Authorization", " Token " + token);
                client.connect();
                int t  =client.getResponseCode();
                if(t!= 200){
                    return null;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String text = br.readLine();
                JSONObject response= new JSONObject(text);
                status = response.getString("status");
                if(!status.equals("success")){
                    return null;
                }
                list = new ArrayList<>();
                JSONArray array = response.getJSONArray("array");
                for(int i = 0; i < array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    list.add(new EventObject(obj.getString("title"),obj.getString("description"),obj.getString("event_id")));
                }



            }catch (Exception e){
                e.printStackTrace();
                if(client!=null){
                    client.disconnect();
                }
                return null;
            }finally {
                if (client != null) {
                    client.disconnect();
                }
            }
            return list;
        }
        @Override
        protected void onPostExecute(List list){
            if(list != null){
                mList.clear();
                mList.addAll(list);
                mAdapater.notifyDataSetChanged();
                //Toast.makeText(getApplicationContext(), "Test 1", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Error Loading Events", Toast.LENGTH_SHORT).show();
                //displayErrorMessage();
                //stopLoading();
            }
        }
    }
}
