package actiknow.com.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.actiknow.happinessindexfinal.MainActivity;
import actiknow.com.utils.NetworkConnection;
import com.actiknow.happinessindexfinal.R;
import com.actiknow.happinessindexfinal.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import actiknow.com.utils.AppConfigURL;
import actiknow.com.utils.Constants;


/**
 * Created by SUDHANSHU SHARMA on 25-02-2016.
 */
public class LoginFragment3 extends Fragment {
    JSONArray jsonArrayOTPUsed = null;
    JSONArray jsonArrayResendOTP = null;
    JSONArray jsonArrayCheckOTP = null;
    JSONArray jsonArraySubmitEmployee = null;
    ArrayList<HashMap<String, Integer>> arrayListCheckOTP = new ArrayList<HashMap<String, Integer>>();
    ArrayList<HashMap<String, Integer>> arrayListResendOTP = new ArrayList<HashMap<String, Integer>>();
    ArrayList<HashMap<String, String>> arrayListSubmitEmployee = new ArrayList<HashMap<String, String>>();
    ProgressDialog progressDialog;
    String value;
    EditText etmobile;
    EditText etCode;
    TextView rlResendOTP;
    TextView otpinsert;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_3, null);
        value = getArguments().getString("mobile");
        Log.d("fasdnkds", "" + value);
        etCode = (EditText) v.findViewById(R.id.editTextcode);
        etmobile = (EditText) v.findViewById(R.id.etmobile);
        rlResendOTP = (TextView) v.findViewById(R.id.resend);

      //  otpinsert = (TextView) v.findViewById(R.id.textView2);

        etmobile.setText(value);
        etCode.setMaxEms(6);
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable mEdit) {
                Constants.otp_entered = mEdit.toString();
                if (mEdit.toString().length() == 6) {
                    new CheckOTP().execute(Constants.mobile_number, Constants.otp_entered);
                    if (Constants.otp == Integer.parseInt(Constants.otp_entered)) {
                        View view2 = getActivity().getCurrentFocus();
                        if (view2 != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                        }
                        new OTPUsed().execute(Constants.mobile_number, Constants.otp_entered);
                        if (NetworkConnection.isNetworkAvailable(getActivity()))

                            new SubmitEmployee().execute(Constants.mobile_number, Constants.companyID, Constants.regid);
                        Log.d("mobile_number", "" + Constants.mobile_number);
                        Log.d("companyID", "" + Constants.companyID);
//                        Intent intent = new Intent (getActivity (), LoginFinalActivity.class);
//                        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity (intent);
                    } else {
                        Toast.makeText(getActivity(), "OTP entered is not correct", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
       //  etmobile.setText(MainActivity.mobile_number);
        rlResendOTP.setVisibility(View.VISIBLE);
        rlResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResendOTP().execute(Constants.mobile_number);
            }
        });
        return v;
    }

    private class OTPUsed extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg) {
            String mobile = arg[0];
            String otp = arg[1];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobile", mobile));
            params.add(new BasicNameValuePair("otp", otp));
            ServiceHandler serviceClient = new ServiceHandler();
            Log.d("url: ", "> " + AppConfigURL.URL_OTPUSED);
            String json = serviceClient.makeServiceCall(AppConfigURL.URL_OTPUSED, ServiceHandler.POST, params);
            Log.d("Get match fixture response: ", "> " + json);
            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");
                    jsonArrayOTPUsed = jsonObj.getJSONArray("details");
                    Log.d("json aray", "" + jsonArrayOTPUsed);
                    int len = jsonArrayOTPUsed.length();
                    Log.d("len", "get array length");
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
        }
    }

    private class ResendOTP extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... arg) {
            String mobile = arg[0];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobile", mobile));
            ServiceHandler serviceClient = new ServiceHandler();
            Log.d("url: ", "> " + AppConfigURL.URL_RESENDOTP);
            String json = serviceClient.makeServiceCall(AppConfigURL.URL_RESENDOTP, ServiceHandler.POST, params);
            Log.d("Get match fixture response: ", "> " + json);
            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");
                    jsonArrayResendOTP = jsonObj.getJSONArray("details");
                    Log.d("json aray", "" + jsonArrayResendOTP);
                    int len = jsonArrayResendOTP.length();
                    Log.d("len", "get array length");
                    for (int i = 0; i < jsonArrayResendOTP.length(); i++) {
                        HashMap<String, Integer> map2 = new HashMap<String, Integer>();
                        JSONObject c = jsonArrayResendOTP.getJSONObject(i);
                        Constants.otp = c.getInt("otp");
                        map2.put("otp", Constants.otp);
                        Log.d("otp", "" + Constants.otp);
                        arrayListResendOTP.add(map2);
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
            Toast.makeText(getActivity(), "otp:" + Constants.otp, Toast.LENGTH_LONG).show();
            for (HashMap<String, Integer> map : arrayListResendOTP) {
                for (Map.Entry<String, Integer> mapEntry : map.entrySet()) {
                    String key = mapEntry.getKey();
                    int value = mapEntry.getValue();
                    if (key == "otp") {
                        Constants.otp = value;
                    }
                }
            }
            //    Log.d ("OTP : ", ""+ Constants.otp);
            //   tvOTP.setText ("OTP : " + Constants.otp);
        }
    }

    private class CheckOTP extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg) {
            String mobile = arg[0];
            String otp = arg[1];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobile", mobile));
            params.add(new BasicNameValuePair("otp", otp));
            ServiceHandler serviceClient = new ServiceHandler();
            Log.d("url: ", "> " + AppConfigURL.URL_CHECKOTP);
            String json = serviceClient.makeServiceCall(AppConfigURL.URL_CHECKOTP, ServiceHandler.POST, params);
            Log.d("Get match fixture response: ", "> " + json);
            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");
                    jsonArrayCheckOTP = jsonObj.getJSONArray("details");
                    Log.d("json aray", "" + jsonArrayCheckOTP);
                    int len = jsonArrayCheckOTP.length();
                    Log.d("len", "get array length");
                    for (int i = 0; i < jsonArrayCheckOTP.length(); i++) {
                        HashMap<String, Integer> map2 = new HashMap<String, Integer>();
                        JSONObject c = jsonArrayCheckOTP.getJSONObject(i);
                        Constants.match = c.getInt("match");
                        map2.put("match", Constants.match);
                        Log.d("match", "" + Constants.match);
                        arrayListCheckOTP.add(map2);
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

        }
    }

    private class SubmitEmployee extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... arg) {
            //String employee_id = arg[0];
            String employee_mobile = arg[0];
            String company_id = arg[1];
            String gcm_reg_id = arg[2];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add (new BasicNameValuePair ("employee_id", employee_id));
            params.add(new BasicNameValuePair("employee_mobile", employee_mobile));
            params.add(new BasicNameValuePair("company_id", company_id));
            params.add(new BasicNameValuePair("gcm_reg_id", gcm_reg_id));
            ServiceHandler serviceClient = new ServiceHandler();
            Log.d("url: ", "> " + AppConfigURL.URL_SUBMITEMPLOYEE);
            String json = serviceClient.makeServiceCall(AppConfigURL.URL_SUBMITEMPLOYEE, ServiceHandler.POST, params);
            Log.d("Get match fixture response: ", "> " + json);
            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");
                    jsonArraySubmitEmployee = jsonObj.getJSONArray("details");
                    Log.d("json aray", "" + jsonArraySubmitEmployee);
                    int len = jsonArraySubmitEmployee.length();
                    Log.d("len", "get array length");
                    for (int i = 0; i < jsonArraySubmitEmployee.length(); i++) {
                        HashMap<String, String> map2 = new HashMap<String, String>();
                        JSONObject c = jsonArraySubmitEmployee.getJSONObject(i);
                        Constants.employee_id_main = c.getInt("employee_id");
                        String employee_status = c.getString("employee_status");
                        map2.put("employee_id", "" + Constants.employee_id_main);
                        Log.d("employee_id", "" + Constants.employee_id_main);
                        map2.put("employee_status", employee_status);
                        Log.d("employee_status", employee_status);
                        arrayListSubmitEmployee.add(map2);
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
        protected void onPostExecute (Void result) {
            progressDialog.dismiss();

            for (HashMap<String, String> map : arrayListSubmitEmployee) {
                for (Map.Entry<String, String> mapEntry : map.entrySet ()) {
                    String key = mapEntry.getKey ();
                    String value = mapEntry.getValue ();
                    if (key == "employee_id") {
                        Constants.employee_id_main = Integer.parseInt(value);
                    }
                    else if (key == "employee_status") {
                        //     Toast.makeText (getActivity (), "user status : " + value, Toast.LENGTH_SHORT).show ();
                    }
                }
            }
            SharedPreferences preferences = getActivity ().getSharedPreferences ("EMPLOYEE_DETAIL", Context.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editor = preferences.edit ();
            editor.putString("company_id", Constants.companyCode);
            Log.d("company_id", "" + Constants.companyCode);
            editor.putString("employee_mobile", Constants.mobile_number);
            Log.d("employee_mobile", "" + Constants.mobile_number);
           // editor.putString("employee_id", MainActivity.employeeID);
            editor.putInt("employee_id_main", Constants.employee_id_main);
            Log.d("employee_id_main", "" + Constants.employee_id_main);
            editor.putString("gcm_reg_id", Constants.regid);
            Log.d("gcm_reg_id", Constants.regid);

            editor.apply();
            Intent intent = new Intent(getActivity (),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity (intent);
        }
    }
}