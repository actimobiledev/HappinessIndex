package com.actiknow.happinessindexfinal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import actiknow.com.utils.AppConfigURL;
import actiknow.com.utils.Constants;
import actiknow.com.utils.NetworkConnection;


/**
 * Created by SUDHANSHU SHARMA on 19-02-2016.
 */
public class SplashScreenActivity extends AppCompatActivity {
    Context context;
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = -10.0f * 360.0f;
    ProgressDialog progressDialog;
    static int status = 0;
    JSONArray jsonArraySubmitResponse = null;
    ArrayList<HashMap<String, Integer>> arrayListSubmitResponse = new ArrayList<HashMap<String, Integer>>();
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        SharedPreferences prfs = getSharedPreferences("EMPLOYEE_DETAIL", Context.MODE_PRIVATE);
        Constants.employee_id_main = prfs.getInt("employee_id_main", 0);
        Log.d("Employee_id_main", "" + Constants.employee_id_main);


       if (NetworkConnection.isNetworkAvailable(SplashScreenActivity.this)) {
            new CheckSubmitResponse().execute(String.valueOf(Constants.employee_id_main));
       }

            new Handler().postDelayed(new Runnable() {


                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity


                    // close this activity

                }
            }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }
    private class CheckSubmitResponse extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg) {
            String employee_id = arg[0];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("employee_id", employee_id));
            Log.d("employee_id", employee_id);
            ServiceHandler serviceClient = new ServiceHandler();
            Log.d("url: ", "> " + AppConfigURL.URL_CHECKSUBMITRESPONSE);
            String json = serviceClient.makeServiceCall(AppConfigURL.URL_CHECKSUBMITRESPONSE, ServiceHandler.POST, params);
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
                        status = c.getInt("status");
                        map2.put("status", status);
                        Log.d("status", "" + status);
                        arrayListSubmitResponse.add(map2);
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
         //   progressDialog.dismiss();
            if ((status == 1)) {
                Intent intent1 = new Intent(SplashScreenActivity.this, SurveyActivity.class);
                startActivity(intent1);
            }    else if ((status == 2)) {
                Intent intent2 = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent2);
            } else if ((status == 0)) {
                Toast.makeText(SplashScreenActivity.this, "Sorry you have already submitted your response", Toast.LENGTH_SHORT).show();
            }
                finish();
        }
    }
}
