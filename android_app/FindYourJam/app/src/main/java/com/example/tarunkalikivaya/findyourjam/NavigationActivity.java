package com.example.tarunkalikivaya.findyourjam;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tarunkalikivaya.findyourjam.eventlistattend.DisplayEventListAttendActivity;
import com.example.tarunkalikivaya.findyourjam.eventlistscreated.DisplayEventListActivity;
import com.example.tarunkalikivaya.findyourjam.session.LoginActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Get the map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //map.setMyLocationEnabled(true);
        //map.addMarker(new MarkerOptions()
         //       .position(new LatLng(0, 0))
         //       .title("Marker"));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_Events_attending) {
            EventsAttending();

        } else if (id == R.id.nav_Create_Event) {
            CreateEvent();


        } else if (id == R.id.nav_Logout) {
            Logout();


        } else if (id == R.id.nav_Events_Owned) {
            EventsOwned();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
    public void EventsAttending(){

        Intent intent = new Intent(NavigationActivity.this,DisplayEventListAttendActivity.class);
        startActivity(intent);
    }
    public void EventsOwned(){
        Intent intent = new Intent(NavigationActivity.this,DisplayEventListActivity.class);
        startActivity(intent);
    }
    public void CreateEvent(){

    }
    public void Logout(){
        new Logout().execute(Constants.getToken(this));
    }

    /**
     * Logout
     */
    private class Logout extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String ... str){
            String token = str[0];
            String status = "";
            String urlEndPoint = Constants.WEB_URL + "/api/v1/auth/logout" ;

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
                    return false;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String text = br.readLine();
                JSONObject response= new JSONObject(text);

                status = response.getString("status");
            }catch (Exception e){
                e.printStackTrace();
                if(client!=null){
                    client.disconnect();
                }
                return false;
            }finally {
                if (client != null) {
                    client.disconnect();
                }
            }
            if(!status.equals("success")){
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean bool){
            if(bool){
                Intent intent = new Intent(NavigationActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

                //Toast.makeText(getApplicationContext(), "Test 1", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Logout Unsuccessful", Toast.LENGTH_SHORT).show();
                //displayErrorMessage();
                //stopLoading();
            }
        }
    }
}
