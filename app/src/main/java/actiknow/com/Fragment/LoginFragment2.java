package actiknow.com.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
public class LoginFragment2  extends Fragment {

    JSONArray jsonArrayOTP = null;
    ArrayList<HashMap<String, Integer>> arrayListOTP = new ArrayList<HashMap<String, Integer>>();


    EditText etMobile;
    Button btlogin;
    ProgressDialog progressDialog;

    int status = 0; //  0 => defasult
    String saved_mobile_number = "";
    String saved_employeeID = "";
    String saved_companyID = "";
    int saved_mobile_status = 0;  // 0 => default (nothing entered), 1 => incomplete, 2 => incorrect, 3 => correct

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_2, null);
        //   rlLogin = (RelativeLayout)v.findViewById (R.id.relativeLayoutlogin);
        etMobile = (EditText) v.findViewById(R.id.editTextmobilenumber);
        btlogin = (Button) v.findViewById(R.id.btnext);
      //  saved_mobile_number = getArguments().getString("mobilenumber");
     //   saved_mobile_status = getArguments().getInt("number_status");

        switch (saved_mobile_status) {
            case 1:
                etMobile.setError("Enter a complete Mobile number");
                break;
            case 2:
                etMobile.setError("Enter a valid Mobile number");
                break;
            case 4:
                etMobile.setError("Enter a Mobile number");
                break;
        }
        etMobile.setMaxEms(10);
        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable mEdit) {
                Constants.mobile_number = mEdit.toString();
                int valid;
                valid = isValidMobile(Constants.mobile_number);
                switch (valid) {
                    case 1:
                        etMobile.setError("Enter a complete Mobile number");
                        break;
                    case 2:
                        etMobile.setError("Enter a valid Mobile number");
                        break;
                    case 3:
         /*               View view2 = getActivity ().getCurrentFocus ();
                        if (view2 != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity ().getSystemService (Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow (view2.getWindowToken (), 0);
                        }
             */
                        break;
                    case 4:
                        etMobile.setError("Enter a Mobile number");
                        break;
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view2 = getActivity().getCurrentFocus();
                if (view2 != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                }

                if(Constants.mobile_number.equals("")){
                    etMobile.setError("Empty Mobile Number");

                }
                  else {
                    if (NetworkConnection.isNetworkAvailable(getActivity()))
                    new GetOTP().execute(Constants.mobile_number);
                }
            }
        });
        return v;
    }

    private int isValidMobile(String phone2) {
        if (Constants.mobile_number.length() == 0)
            Constants.first_char = "0";
        else
            Constants.first_char = Constants.mobile_number.substring(0, 1);
        if (phone2.length() == 10 && Integer.parseInt(Constants.first_char) > 6) {
            Constants.number_status = 3;
        } else if (phone2.length() < 10 && Integer.parseInt(Constants.first_char) > 6) {
            Constants.number_status = 1;
        } else if (Integer.parseInt(Constants.first_char) <= 6 && Integer.parseInt(Constants.first_char) > 0 && phone2.length() <= 10 && phone2.length() > 1) {
            Constants.number_status = 2;
        } else if (Constants.first_char == "0") {
            Constants.number_status = 4;
        }
        return Constants.number_status;
    }

    private class GetOTP extends AsyncTask<String, Void, Void> {
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
            Log.d("url: ", "> " + AppConfigURL.URL_GETOTP);
            String json = serviceClient.makeServiceCall(AppConfigURL.URL_GETOTP, ServiceHandler.POST, params);
            Log.d("Get match fixture response: ", "> " + json);
            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");
                    jsonArrayOTP = jsonObj.getJSONArray("details");
                    Log.d("json array", "" + jsonArrayOTP);
                    int len = jsonArrayOTP.length();
                    Log.d("len", "get array length");
                    for (int i = 0; i < jsonArrayOTP.length(); i++) {
                        HashMap<String, Integer> map2 = new HashMap<String, Integer>();
                        JSONObject c = jsonArrayOTP.getJSONObject(i);
                        Constants.otp = c.getInt("otp");
                        map2.put("otp", Constants.otp);
                        Log.d("otp", "" + Constants.otp);
                        arrayListOTP.add(map2);
                       // Toast.makeText(LoginFragment2.this, "" +  Constants.otp, Toast.LENGTH_LONG).show();
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
            FragmentManager fragmentManager = getFragmentManager ();
            FragmentTransaction fragmentTransaction;
            fragmentTransaction = fragmentManager.beginTransaction ();
            LoginFragment3 f3 = new LoginFragment3();
            Bundle args = new Bundle();
            args.putString("mobile", Constants.mobile_number);
            f3.setArguments(args);
            // fragmentTransaction.setCustomAnimations (R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.fragment_container, f3, "fragment2");
            fragmentTransaction.commit();
            Toast.makeText(getActivity(), "otp:" + Constants.otp, Toast.LENGTH_LONG).show();
            for (HashMap<String, Integer> map : arrayListOTP) {
                for (Map.Entry<String, Integer> mapEntry : map.entrySet()) {
                    String key = mapEntry.getKey();
                    int value = mapEntry.getValue();
                    if (key == "otp") {
                        Constants.otp = value;
                    }
                }
            }

        }


    }
}