package com.cyberswift.wbdisastermanagement;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cyberswift.wbdisastermanagement.service.VolleyTaskManager;
import com.cyberswift.wbdisastermanagement.util.AlertDialogCallBack;
import com.cyberswift.wbdisastermanagement.util.ServerResponseCallback;
import com.cyberswift.wbdisastermanagement.util.Util;

public class RegistrationActivity extends Activity implements ServerResponseCallback {

	private Context mContext;
	private EditText et_fullname, et_phone, et_email, et_designation;
	private EditText et_password, et_confirm_password;
	private VolleyTaskManager volleyTaskManager;
	private boolean isGetBaseUrl = false, isRegistrationService = false;
	private String baseUrl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_registration);
		mContext = RegistrationActivity.this;
		initView();

	}

	private void initView() {

		et_fullname = (EditText) findViewById(R.id.et_fullname);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_email = (EditText) findViewById(R.id.et_email);
		et_designation = (EditText) findViewById(R.id.et_designation);
		et_password = (EditText) findViewById(R.id.et_password);
		et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);
		volleyTaskManager = new VolleyTaskManager(mContext);

	}

	public void onRegisterClick(View v) {

		if (et_fullname.getText().toString().trim().isEmpty()) {
			Toast.makeText(mContext, "Please enter a name.", Toast.LENGTH_SHORT).show();

		} else if (et_phone.getText().toString().trim().isEmpty()) {
			Toast.makeText(mContext, "Please enter a phone number.", Toast.LENGTH_SHORT).show();

		} else if (et_email.getText().toString().trim().isEmpty()) {
			Toast.makeText(mContext, "Please enter an email address or user name.", Toast.LENGTH_SHORT).show();

		} else if (et_designation.getText().toString().trim().isEmpty()) {
			Toast.makeText(mContext, "Please enter your designation.", Toast.LENGTH_SHORT).show();

		} else if (et_password.getText().toString().trim().isEmpty()) {
			Toast.makeText(mContext, "Please enter a password.", Toast.LENGTH_SHORT).show();

		} else if (et_password.getText().toString().trim().length() < 4) {
			Toast.makeText(mContext, "Password length too short.", Toast.LENGTH_SHORT).show();

		} else if (!(et_confirm_password.getText().toString().trim().equalsIgnoreCase(et_password.getText().toString().trim()))) {
			Toast.makeText(mContext, "Your passwords do not match.", Toast.LENGTH_SHORT).show();

		} else if (et_phone.getText().toString().trim().length() != 10)
			Toast.makeText(mContext, "Please enter correct phone number.", Toast.LENGTH_SHORT).show();

		else {
			getBaseUrl();
		}

	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {

		if (isGetBaseUrl) {
			isGetBaseUrl = false;
			if (resultJsonObject != null) {
				JSONObject baseUrlObject = resultJsonObject.optJSONObject("GetRedirectionURLResult");
				baseUrl = baseUrlObject.optString("BASE_URL");
				System.out.println("Json Base url: " + baseUrlObject.optString("BASE_URL"));
				callRegistrationService();
			}
		} else if (isRegistrationService) {
			if (!resultJsonObject.optString("RES").trim().equalsIgnoreCase("0")) {
				Util.showSuccessMessageWithOk(mContext, "User registered successfully.", new AlertDialogCallBack() {

					@Override
					public void onSubmit() {
						finish();
					}

					@Override
					public void onCancel() {

					}
				});
			}
		}
	}

	@Override
	public void onSuccess(String resultJsonObject) {

	}

	@Override
	public void onError() {

	}

	private void getBaseUrl() {
		HashMap<String, String> request = new HashMap<String, String>();
		isGetBaseUrl = true;
		volleyTaskManager.doGetBaseUrl(request, true);

	}

	/**
	 * Calls the registration service.
	 * */
	private void callRegistrationService() {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("NAME", et_fullname.getText().toString().trim());
		requestMap.put("USER_ID", et_email.getText().toString().trim());
		requestMap.put("NEW_PASSWORD", et_password.getText().toString().trim());
		requestMap.put("MOBILE", et_phone.getText().toString().trim());
		requestMap.put("DESIGNATION", et_designation.getText().toString().trim());
		requestMap.put("EMAIL_ID", "");
		requestMap.put("IS_ACTIVE", "False");
		if (baseUrl.trim().length() > 0) {
			isRegistrationService = true;
			volleyTaskManager.doRegistration(baseUrl, requestMap, true);
		} else
			Toast.makeText(mContext, "Request failed try again...", Toast.LENGTH_SHORT).show();

	}
}
