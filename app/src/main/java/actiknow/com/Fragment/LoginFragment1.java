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
import android.widget.RelativeLayout;
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
 * Created by SUDHANSHU SHARMA on 24-02-2016.
 */

public class LoginFragment1 extends Fragment {
    JSONArray jsonArrayOTP = null;
    JSONArray jsonArrayCheckLogin = null;
    ArrayList<HashMap<String, Integer>> arrayListOTP = new ArrayList<HashMap<String, Integer>>();
    ArrayList<HashMap<String, Integer>> arrayListCheckLogin = new ArrayList<HashMap<String, Integer>>();

    Context context;
    RelativeLayout rlLogin;
    EditText etCompanyID;
    Button btsubmit;
    EditText etEmployeeID;
    EditText etMobile;
    ProgressDialog progressDialog;
    int status = 0; //  0 => defasult
    String saved_mobile_number = "";
    String saved_employeeID = "";
    String saved_companyID = "";
    int saved_mobile_status = 0;  // 0 => default (nothing entered), 1 => incomplete, 2 => incorrect, 3 => correct

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_1, null);
        etCompanyID = (EditText) v.findViewById(R.id.editTextcompanyid);
        btsubmit = (Button) v.findViewById(R.id.button);
        saved_companyID = getArguments().getString("companyid");
        etCompanyID.addTextChangedListener (new TextWatcher() {
            @Override
            public void afterTextChanged (Editable mEdit) {
                Constants.companyCode = mEdit.toString ();
            }
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged (CharSequence s, int start, int before, int count) {
            }
        });
        btsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view2 = getActivity().getCurrentFocus();
                if (view2 != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                }

                if (Constants.companyCode.length() == 0) {
                    etCompanyID.setError("Enter Company ID");
                }
                if (Constants.companyCode.length() != 0) {
                    if (NetworkConnection.isNetworkAvailable(getActivity()))
                        new checkLogin().execute(Constants.companyCode);
                        Log.d("CompanyCode", "" + Constants.companyCode);

                }
            }
        });
        return v;
    }

    private class checkLogin extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg) {
            String company_code = arg[0];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("company_code", company_code));
            ServiceHandler serviceClient = new ServiceHandler();
            Log.d("url: ", "> " + AppConfigURL.URL_CHECKLOGIN);
            String json = serviceClient.makeServiceCall(AppConfigURL.URL_CHECKLOGIN, ServiceHandler.POST, params);
            Log.d("Get match fixture response: ", "> " + json);
            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");
                    jsonArrayCheckLogin = jsonObj.getJSONArray("details");
                    Log.d("json array", "" + jsonArrayCheckLogin);
                    int len = jsonArrayCheckLogin.length();
                    Log.d("len", "get array length");
                    for (int i = 0; i < jsonArrayCheckLogin.length(); i++) {
                        HashMap<String, Integer> map2 = new HashMap<String, Integer>();
                        JSONObject c = jsonArrayCheckLogin.getJSONObject(i);
                        status = c.getInt("status");
                        Constants.companyID = c.getString("company_id");
                        map2.put("status", status);
                        Log.d("status", "" + status);
                        map2.put("company_id", Integer.parseInt(Constants.companyID));
                        Log.d("company_id", "" + Constants.companyID);
                        if (status == 0) {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Company code entered is not valid", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        arrayListCheckLogin.add(map2);
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
            for (HashMap<String, Integer> map : arrayListCheckLogin) {
                for (Map.Entry<String, Integer> mapEntry : map.entrySet()) {
                    String key = mapEntry.getKey();
                    int value = mapEntry.getValue();
                    if (key == "status") {
                        status = value;
                    } else if (key == "company_id") {
                        Constants.companyID = String.valueOf(value);
                    }
                }
            }
            if (status == 1) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction;
                fragmentTransaction = fragmentManager.beginTransaction();
                LoginFragment2 f2 = new LoginFragment2();
                fragmentTransaction.replace(R.id.fragment_container, f2, "fragment2");
                fragmentTransaction.commit();
            }
//           }
        }
    }
}
