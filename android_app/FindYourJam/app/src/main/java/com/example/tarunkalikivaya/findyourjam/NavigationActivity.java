package com.example.tarunkalikivaya.findyourjam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tarunkalikivaya.findyourjam.eventlistattend.DisplayEventListAttendActivity;
import com.example.tarunkalikivaya.findyourjam.eventlistattend.EventAttendObject;
import com.example.tarunkalikivaya.findyourjam.eventlistscreated.DisplayEventListActivity;
import com.example.tarunkalikivaya.findyourjam.session.LoginActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.tarunkalikivaya.findyourjam.R.id.map;


public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private HashMap<Marker,String> mHash;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("LITT");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Get the map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        mHash = new HashMap<>();

    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        if(mMap!=null) {
            if (ActivityCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(NavigationActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                ActivityCompat.requestPermissions(NavigationActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
                return;
            }
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if(mMap.getMyLocation() == null){
                        return false;
                    }
                    mMap.getMyLocation().getLatitude();
                    mMap.getMyLocation().getLongitude();
                    new GetMarkers().execute(Constants.getToken(NavigationActivity.this),String.valueOf(mMap.getMyLocation().getLatitude()),String.valueOf(mMap.getMyLocation().getLongitude()),"1000");
                    return false;
                }
            });
        }
    }

    class markerThing{
        double lat;
        double lng;
        String id;
        String title;
        public markerThing(double l,double lg, String id, String title){
            this.lat = l;
            this.lng = lg;
            this.id = id;
            this.title = title;
        }

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        String t  = (String)mHash.get(marker);
        Intent intent = new Intent(NavigationActivity.this,EventDisplay.class);
        intent.putExtra("id",t);
        startActivity(intent);
        return false;
    }
    /**
     * Logout
     */
    private class GetMarkers extends AsyncTask<String, Void, List> {
        @Override
        protected List doInBackground(String ... str){
            String token = str[0];
            Float lat = Float.valueOf(str[1]);
            Float lng = Float.valueOf(str[2]);
            Float radius = Float.valueOf(str[3]);
            String status = "";
            ArrayList<markerThing> list = null;
            String urlEndPoint = Constants.WEB_URL + "/api/v1/events/get_multiple_events" ;

            JSONObject credentials = new JSONObject();
            try {
                credentials.put("lat",lat);
                credentials.put("long",lng);
                credentials.put("radius",radius);
            }catch (Exception e){
                e.printStackTrace();
            }
            //Run the api call
            HttpURLConnection client = null;
            try {
                URL url = new URL(urlEndPoint);
                client =(HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setConnectTimeout(10000);
                client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                client.setRequestProperty("Authorization", " Token " + token);
                client.setDoOutput(true);
                OutputStream os = client.getOutputStream();
                os.write(credentials.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
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
                    list.add(new markerThing(obj.getDouble("lat"),obj.getDouble("long"),obj.getString("event_id"),obj.getString("title")));
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
                for(int i = 0; i< list.size();i++){
                    markerThing obj = (markerThing)list.get(i);
                    mHash.put(
                    mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(obj.lat, obj.lng))
                                    .title(obj.title)),obj.id);
                }
                //Toast.makeText(getApplicationContext(), "Test 1", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Error Loading Events", Toast.LENGTH_SHORT).show();
                //displayErrorMessage();
                //stopLoading();
            }
        }
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
        Intent intent = new Intent(NavigationActivity.this, CreateEvent.class);
        startActivity(intent);
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
