package com.actiknow.happinessindexfinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import actiknow.com.gcm.GcmIntentService;
import actiknow.com.utils.AppConfigURL;
import actiknow.com.utils.Config;
import actiknow.com.utils.Constants;
import actiknow.com.utils.NetworkConnection;


public class MainActivity extends Activity implements View.OnClickListener {
    Context context;
  //  static String host = "10.0.3.2";

    JSONArray jsonArraySubmitResponse = null;
    ArrayList<HashMap<String, Integer>> arrayListSubmitResponse = new ArrayList<HashMap<String, Integer>>();
    ProgressDialog progressDialog;

    Button btSubmit;
    ImageView ivHappy;
    ImageView ivNeutral;
    ImageView ivSad;

    private String TAG = MainActivity.class.getSimpleName ();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btSubmit = (Button) findViewById(R.id.buttonsubmit);
        SharedPreferences prfs = getSharedPreferences("EMPLOYEE_DETAIL", Context.MODE_PRIVATE);
        Constants.companyCode = prfs.getString("company_id", "");
        Constants.mobile_number = prfs.getString("employee_mobile", "");
        Log.d("hafisodklsanlkajskljfla", "" + Constants.mobile_number);
        Constants.employeeID = prfs.getString("employee_id", "");
        Constants.employee_id_main = prfs.getInt("employee_id_main", 0);


        if (Constants.companyCode == "") {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        } else if (Constants.employee_id_main == 0) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        } else if (Constants.mobile_number == "") {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        }
        if (Constants.companyCode == "" || Constants.mobile_number == "")
            finish();


        ivHappy = (ImageView) findViewById(R.id.imageViewhappy3);
        ivNeutral = (ImageView) findViewById(R.id.imageViewneutral3);
        ivSad = (ImageView) findViewById(R.id.imageViewsad3);
        ivHappy.setOnClickListener(this);
        ivNeutral.setOnClickListener(this);
        ivSad.setOnClickListener(this);
        btSubmit.setOnClickListener(this);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive (Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction ().equals (Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = intent.getStringExtra ("token");

//                    Toast.makeText (getApplicationContext (), "GCM registration token: " + token, Toast.LENGTH_LONG).show ();

                } else if (intent.getAction ().equals (Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

//                    Toast.makeText (getApplicationContext (), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show ();

                } else if (intent.getAction ().equals (Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    Toast.makeText (getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show ();
                }
            }
        };

        if (checkPlayServices ()) {
            registerGCM();
            Log.d(TAG, "check play services");
        }

    }

    // starting the service to register with GCM
    private void registerGCM () {
        Log.d(TAG, "in register gcm");
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra ("key", "register");
        startService (intent);
    }

    private boolean checkPlayServices () {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance ();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable (this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError (resultCode)) {
                apiAvailability.getErrorDialog (this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show ();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText (getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show ();
                finish ();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume () {
        super.onResume ();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver (mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause () {
        LocalBroadcastManager.getInstance(this).unregisterReceiver (mRegistrationBroadcastReceiver);
        super.onPause ();
    }

    @Override
    public void onClick(View v) {
        btSubmit = (Button) findViewById(R.id.buttonsubmit);
        // etRemark = (EditText)findViewById(R.id.editTextremarks);
        ImageView bhappy = (ImageView) findViewById(R.id.imageViewhappy3);
        ImageView bneutral = (ImageView) findViewById(R.id.imageViewneutral3);
        ImageView bsad = (ImageView) findViewById(R.id.imageViewsad3);

        switch (v.getId()) {
            case R.id.imageViewhappy3:
                Constants.response = 1;
                bhappy.setImageResource(R.drawable.ic_happyselected);
                bneutral.setImageResource(R.drawable.ic_neutral2);
                bsad.setImageResource(R.drawable.ic_sad2);
                break;
            case R.id.imageViewneutral3:
                Constants.response = 2;
                bhappy.setImageResource(R.drawable.ic_happy2);
                bneutral.setImageResource(R.drawable.ic_neutralselected);
                bsad.setImageResource(R.drawable.ic_sad2);
                break;
            case R.id.imageViewsad3:
                Constants.response = 3;
                bhappy.setImageResource(R.drawable.ic_happy2);
                bneutral.setImageResource(R.drawable.ic_neutral2);
                bsad.setImageResource(R.drawable.ic_sadselected);
                break;
            case R.id.buttonsubmit:
                if (Constants.response == 1 || Constants.response == 2 || Constants.response == 3) {
                    if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {
                        new SubmitResponse().execute(String.valueOf(Constants.employee_id_main), (String.valueOf(Constants.response)));
                    }
                }
                else
                {
                    Toast.makeText(this, "Please select a response first", Toast.LENGTH_LONG).show();
                    break;
                }
        }
    }

    private class SubmitResponse extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(String... arg) {
            String employee_id = arg[0];
            String response = arg[1];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("employee_id", employee_id));
            Log.d("employee_id", employee_id);
            params.add (new BasicNameValuePair ("response", response));
            Log.d("response", response);

            ServiceHandler serviceClient = new ServiceHandler();
            Log.d("url: ", "> " + AppConfigURL.URL_SUBMITRESPONSE);
            String json = serviceClient.makeServiceCall(AppConfigURL.URL_SUBMITRESPONSE, ServiceHandler.POST, params);
            Log.d("Get match fixture response: ", "> " + json);
            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");
                    jsonArraySubmitResponse = jsonObj.getJSONArray("details");
                    Log.d("json array", "" + jsonArraySubmitResponse);
                    for (int i = 0; i < jsonArraySubmitResponse.length(); i++) {
                        HashMap<String, Integer> map2 = new HashMap<String, Integer>();
                        JSONObject c = jsonArraySubmitResponse.getJSONObject(i);
                        Constants.status = c.getInt ("status");

                        map2.put("status", Constants.status);
                        Log.d("status", "" + Constants.status);
                        arrayListSubmitResponse.add (map2);
                    }
                } catch (JSONException e) {
                    Log.d("catch", "in the catch");
                    e.printStackTrace();
                }
            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();

           if ((Constants.status == 1) && (Constants.response == 3)) {
                Intent intent1 = new Intent(MainActivity.this, SurveyActivity.class);
                intent1.putExtra("employee_id_main", Constants.employee_id_main);
                startActivity(intent1);
                Log.d("fdvhkjfdvkvefnkvd", "" + Constants.response);
            }
            else if ((Constants.status == 2) && (Constants.response == 1 || Constants.response == 2 || Constants.response == 3)) {
               Toast.makeText(MainActivity.this, "Thanks for Submitting Your response", Toast.LENGTH_SHORT).show();
            }
            else if ((Constants.status == 1) && (Constants.response == 1 || Constants.response == 2)) {
               Toast.makeText(MainActivity.this, "Thanks for Submitting Your response", Toast.LENGTH_SHORT).show();
            }
            else if ((Constants.status == 0) && (Constants.response == 1 || Constants.response == 2 || Constants.response == 3)) {
                Toast.makeText(MainActivity.this, "Sorry you have already submitted your response", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
 }

