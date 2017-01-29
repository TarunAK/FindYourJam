package com.example.tarunkalikivaya.findyourjam;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * A login screen that offers login via email/password.
 */
public class CreateEvent extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private EventCreationTask mAuthTask = null;

    // UI references.
    private TextView title;
    private TextView time;
    private TextView place;
    private TextView description;
    private View mProgressView;
    private View mLoginFormView;
    String address;
    String fTitle;
    String fTime;
    String fPlace;
    String fDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Create Event");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createevent);
        // Set up the login form.
        title = (EditText) findViewById(R.id.title);
        //populateAutoComplete();

        time = (EditText) findViewById(R.id.time);
        place = (EditText) findViewById(R.id.place);
        description = (EditText) findViewById(R.id.description);
        /*title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    //attemptLogin();
                    return true;
                }
                return false;
            }
        });*/

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /*private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }*/

    /**
     * Callback received when a permissions request has been completed.
     */
    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }*/


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        title.setError(null);
        time.setError(null);
        place.setError(null);
        description.setError(null);

        // Store values at the time of the login attempt.
        String sTitle = title.getText().toString();
        String sTime = time.getText().toString();
        String sPlace = place.getText().toString();
        String sDescription = description.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(sTime)) {
            time.setError(getString(R.string.error_field_required));
            focusView = time;
            cancel = true;
        }

        if (TextUtils.isEmpty(sPlace)) {
            place.setError(getString(R.string.error_field_required));
            focusView = place;
            cancel = true;
        }

        if (TextUtils.isEmpty(sDescription)) {
            description.setError(getString(R.string.error_field_required));
            focusView = description;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(sTitle)) {
            title.setError(getString(R.string.error_field_required));
            focusView = title;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            fTitle = setAndGetTitle(sTitle);
            fTime = setAndGetTime(sTime);
            fPlace = setAndGetPlace(sPlace);
            fDescription = setAndGetDescription(sDescription);
            //setAndGetLocation(sPlace);
            //mAuthTask = new UserLoginTask(sTitle, sTime);
            //mAuthTask.execute((Void) null)
            new EventCreationTask().execute(fTitle, fTime, fPlace, fDescription);
        }
    }

    public String setAndGetTitle(String title) {
        return title;
    }

    public String setAndGetTime(String time) {
        return time;
    }

    public String setAndGetPlace(String place) {
        return place;
    }

    public String setAndGetDescription(String description) {
        return description;
    }

    public float[] setAndGetLocation(String location) {
        String[] a;
        float[] b = {0, 0};
        if (location.contains(",")) {
            a = location.split(",");
            for (int i = 0; i < a.length; i++) {
                b[i] = Float.valueOf(a[i]);
            }
        }
        return b;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /*@Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }
*/
    /*@Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }*/

    /*private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(CreateEvent.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
*/
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class EventCreationTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... str) {
            // TODO: attempt authentication against a network service.
            //String address = setAndGetAddress();

            //fTitle, fTime, fPlace, fDescription

            String title = str[0];
            String date  = str[1];
            String location = str[2];
            String description = str[3];

            //String date  = str[5];
            URL url;
            HttpsURLConnection urlConnection = null;
            double lat = 0;
            double lng = 0;
            try {
                url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + location.replace(" ", "+") + "&key=AIzaSyBun-QP3dFfQz59waRq1Lp4YxKM29fvubc");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String text = "";
                String line;
                while ((line = br.readLine()) != null) {
                    text += line;
                }
                JSONObject response = new JSONObject(text);
                lat = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lng = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                //readStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }
            //return coordinates;
            String token = Constants.getToken(getBaseContext());;
            String urlEndPoint = Constants.WEB_URL + "/api/v1/events/create_event" ;
            String status = "";
            JSONObject output = new JSONObject();

            try {
                output.put("title",title);
                output.put("description",description);
                output.put("lat", lat);
                output.put("long", lng);
                output.put("location",location);
                output.put("date",date);

            } catch (Exception e){
                e.printStackTrace();
                return false;
            }

            //Run the api call

            HttpURLConnection client = null;

            try {
                URL url1 = new URL(urlEndPoint);
                client =(HttpURLConnection) url1.openConnection();
                client.setRequestMethod("POST");
                client.setConnectTimeout(10000);
                client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                client.setRequestProperty("Authorization", " Token " + token);
                client.setDoOutput(true);
                OutputStream os = client.getOutputStream();
                os.write(output.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
                int response_code = client.getResponseCode();
                if (response_code != 200){
                    return false;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String text = br.readLine();
                JSONObject response= new JSONObject(text);
                status = response.getString("status");

            } catch (Exception e){
                e.printStackTrace();
                if(client!=null){
                    client.disconnect();
                }
                return false;
            }finally {
                if(client!=null){
                    client.disconnect();
                }
            }

            if(!status.equals("success")){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean b) {
            if (b) {
                Toast.makeText(getApplicationContext(), "Event Created", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error, Event Not Created", Toast.LENGTH_SHORT).show();
                title.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
                place.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                mProgressView.setVisibility(View.GONE);
                mLoginFormView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

