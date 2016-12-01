package com.cyberswift.wbdisastermanagement;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberswift.wbdisastermanagement.db.BlockDB;
import com.cyberswift.wbdisastermanagement.db.DBConstants;
import com.cyberswift.wbdisastermanagement.db.DistrictDB;
import com.cyberswift.wbdisastermanagement.db.MPCSProjectDB;
import com.cyberswift.wbdisastermanagement.db.ProjectDB;
import com.cyberswift.wbdisastermanagement.model.Block;
import com.cyberswift.wbdisastermanagement.model.District;
import com.cyberswift.wbdisastermanagement.model.MPCSProject;
import com.cyberswift.wbdisastermanagement.model.Project;
import com.cyberswift.wbdisastermanagement.model.UserClass;
import com.cyberswift.wbdisastermanagement.service.VolleyTaskManager;
import com.cyberswift.wbdisastermanagement.static_constants.ServiceTags;
import com.cyberswift.wbdisastermanagement.util.AlertDialogCallBack;
import com.cyberswift.wbdisastermanagement.util.ServerResponseCallback;
import com.cyberswift.wbdisastermanagement.util.Util;

public class LoginActivity extends Activity implements ServerResponseCallback, ServiceTags {

	private Context mContext;
	private EditText et_userName, et_password;
	private TextView tv_version;

	private CheckBox chkbx_rememberMe;
	private UserClass user = new UserClass();
	private VolleyTaskManager volleyTaskManager;

	private boolean isGetBaseUrl = false;
	private boolean isLoginService = false;
	private boolean isFetchProjectService = false;
	private boolean isFetchAllProjectDataService = false;
	private boolean isForgotPasswordService = false;

	private ArrayList<Project> projectList = new ArrayList<Project>();
	private ArrayList<District> districtList = new ArrayList<District>();;
	private ArrayList<Block> blockList = new ArrayList<Block>();
	private ArrayList<MPCSProject> mpcsProjList = new ArrayList<MPCSProject>();

	private int projectCount = 0;
	private String forgetPasswordEmail = "";
	private Dialog customDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		mContext = LoginActivity.this;
		initView();

	}

	private void initView() {

		et_userName = (EditText) findViewById(R.id.et_userName);

		et_password = (EditText) findViewById(R.id.et_password);

		chkbx_rememberMe = (CheckBox) findViewById(R.id.chkbx_rememberMe);

		tv_version = (TextView) findViewById(R.id.tv_version);

		if (Util.fetchUserClass(LoginActivity.this) != null) {

			user = Util.fetchUserClass(LoginActivity.this);
		}
		Util.getTimeFromNetwork(LoginActivity.this);

		Log.d("Save user Preference: ", "IS REMEMBERED: " + user.getIsRemember());

		Log.d("Save user Preference: ", "UserName: " + user.getUserId());

		Log.d("Save user Preference: ", "Password: " + user.getPassword());

		if (user.getIsRemember()) {

			et_userName.setText(user.getUserId());

			et_password.setText(user.getPassword());

			chkbx_rememberMe.setChecked(user.getIsRemember());
		}

		volleyTaskManager = new VolleyTaskManager(LoginActivity.this);

		showAppVersion();

		fixRememberMeView();
	}

	private void showAppVersion() {

		try {
			tv_version.setVisibility(View.VISIBLE);
			tv_version.setText(mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			tv_version.setVisibility(View.GONE);
			e.printStackTrace();
		}
	}

	private void fixRememberMeView() {

		final float scale = this.getResources().getDisplayMetrics().density;
		chkbx_rememberMe.setPadding(chkbx_rememberMe.getPaddingLeft() + (int) (10.0f * scale + 0.5f), chkbx_rememberMe.getPaddingTop(),
				chkbx_rememberMe.getPaddingRight(), chkbx_rememberMe.getPaddingBottom());
	}

	public void onSignUpClicked(View v) {

		startActivity(new Intent(mContext, RegistrationActivity.class));
	}

	public void onForgotPassword(View v) {

		showForgotPassword();
	}

	public void onSignInClicked(View v) {

		if (et_userName.getText().toString().length() == 0 || et_password.getText().toString().length() == 0) {
			Util.showMessageWithOk(LoginActivity.this, "Please fill all the required fields!");

		} else if (et_password.getText().toString().length() < 4) {
			Util.showMessageWithOk(LoginActivity.this, "Password should be at least 4 characters long!");
		}

		else if (Util.checkConnectivity(LoginActivity.this)) {
			// Checks whether the typed user has already logged in
			if (Util.fetchUserClass(mContext) != null
					&& (et_userName.getText().toString().trim().equalsIgnoreCase(Util.fetchUserClass(mContext).getUserId()))) {

				// Already logged in user
				if (Util.fetchUserClass(mContext).getPassword().equalsIgnoreCase(et_password.getText().toString().trim())) {
					// Password is correct
					// TODO Now check if the previous load was successful (Check count)

					boolean isOfflineDataComplete = new MPCSProjectDB().isMPCSProjectAvailableOffline(mContext);
					System.out.println("Offline Data completed: " + isOfflineDataComplete);
					if (isOfflineDataComplete)
						// Offline data was intact
						openHomeActivity();
					else
						// Previous Download was not complete-- fetch from internet
						getBaseUrl();

				} else {

					Util.showMessageWithOk(LoginActivity.this, "Please enter the correct password.");
				}
			} else
				// New user
				getBaseUrl();
		}

		else if (!Util.checkConnectivity(LoginActivity.this)) {

			// Offline Login
			if (Util.fetchUserClass(mContext) != null
					&& (et_userName.getText().toString().trim().equalsIgnoreCase(Util.fetchUserClass(mContext).getUserId()))) {

				// Already logged in user
				if (Util.fetchUserClass(mContext).getPassword().equalsIgnoreCase(et_password.getText().toString().trim())) {

					startActivity(new Intent(mContext, NewFormActivity.class));
					finish();

				} else {

					Util.showMessageWithOk(LoginActivity.this, "Please enter the correct password.");
				}
			} else if (Util.fetchUserClass(mContext) == null)
				// New user
				Toast.makeText(LoginActivity.this, "No Internet Access.", Toast.LENGTH_SHORT).show();
		}

	}

	private void getBaseUrl() {

		HashMap<String, String> request = new HashMap<String, String>();

		isGetBaseUrl = true;

		volleyTaskManager.doGetBaseUrl(request, true);

	}

	private void loginWebserviceCalling() {

		HashMap<String, String> paramsMap = new HashMap<String, String>();

		paramsMap.put("User_Id", et_userName.getText().toString());

		paramsMap.put("Password", et_password.getText().toString());

		isLoginService = true;

		volleyTaskManager.doLogin(paramsMap, false);

	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {

		Log.v("resultJsonObject", resultJsonObject.toString());

		if (isGetBaseUrl) {

			isGetBaseUrl = false;

			if (resultJsonObject != null) {

				JSONObject baseUrlObject = resultJsonObject.optJSONObject("GetRedirectionURLResult");

				user.setBaseUrl(baseUrlObject.optString("BASE_URL"));
				System.out.println("Json Base url: " + baseUrlObject.optString("BASE_URL"));
				System.out.println("Get base url: " + user.getBaseUrl());

				Util.saveUserClass(mContext, user);
				if (isForgotPasswordService) {

					HashMap<String, String> requestMap = new HashMap<String, String>();
					requestMap.put("USERNAME", forgetPasswordEmail);

					volleyTaskManager.doPostForgetPassword(requestMap, true);
				} else

					loginWebserviceCalling();
			}

		} else if (isLoginService) {

			isLoginService = false;

			if (resultJsonObject.optString("Status").equalsIgnoreCase("1")) {

				user.setName(resultJsonObject.optString("Name"));

				user.setUserId(resultJsonObject.optString("User_Id"));

				user.setDatabaseId(resultJsonObject.optString("Id"));

				user.setProjectCountOnline(Integer.parseInt(resultJsonObject.optString("MPCS_Count")));

				user.setPassword(et_password.getText().toString());

				user.setIsRemember(chkbx_rememberMe.isChecked());

				user.setLoggedInDateTime(Util.getCurrentFormattedDate());

				user.setIsLoggedin(true);

				// FETCHED DATA FOR OFFLINE MODE
				//ArrayList<Project> projectList = new ProjectDB().getMPCSProjects(mContext);

				long projectCountOffline = new MPCSProjectDB().getProfilesCount(mContext);

				// Check the number of rows in  
				if (projectCountOffline == Util.fetchUserClass(mContext).getProjectCountOnline()) {

					Util.saveUserClass(LoginActivity.this, user);
					volleyTaskManager.hideProgressDialog();
					openHomeActivity();
				} else
					callDataForOfflineService();

			} else if (resultJsonObject.optString("Status").equalsIgnoreCase("0")
					|| resultJsonObject.optString("Status").equalsIgnoreCase("-1")) {
				volleyTaskManager.hideProgressDialog();
				Toast.makeText(LoginActivity.this, resultJsonObject.optString("Message"), Toast.LENGTH_SHORT).show();
			} else {
				volleyTaskManager.hideProgressDialog();
				Toast.makeText(LoginActivity.this, resultJsonObject.optString("Message"), Toast.LENGTH_SHORT).show();
			}
		}

		else if (isFetchProjectService) {

			isFetchProjectService = false;
			parseMPCSProjects(resultJsonObject);

		} else if (isFetchAllProjectDataService) {

			projectCount = projectCount - 1;

			isFetchAllProjectDataService = false;
			parseAllProjectData(resultJsonObject);

		} else if (isForgotPasswordService) {

			volleyTaskManager.hideProgressDialog();
			isForgotPasswordService = false;

			JSONObject forgetPasswordJson = resultJsonObject.optJSONObject("ForgetPasswordMobResult");
			if (forgetPasswordJson.optString("RES").equalsIgnoreCase("1")) {
				Util.showSuccessMessageWithOk(mContext, "New password has been sent to your email.", new AlertDialogCallBack() {
					@Override
					public void onSubmit() {
						customDialog.dismiss();
					}

					@Override
					public void onCancel() {
					}
				});
			} else
				Toast.makeText(mContext, "Please check your email address and try again.", Toast.LENGTH_SHORT).show();
		}
		// volleyTaskManager.hideProgressDialog();
	}

	@Override
	public void onError() {

	}

	@Override
	public void onSuccess(String resultJsonObject) {

	}

	private void callDataForOfflineService() {

		isFetchProjectService = true;

		HashMap<String, String> paramsMap = new HashMap<String, String>();

		volleyTaskManager.doPostFetchProject(paramsMap);
	}

	// ---------------------- -PARSING

	private void parseMPCSProjects(JSONObject resultJsonObject) {

		projectList.clear();

		JSONArray projectArray = resultJsonObject.optJSONArray("GetProjectTypesResult");

		for (int i = 0; i < projectArray.length(); i++) {
			JSONObject districtJsonObject = projectArray.optJSONObject(i);
			Project project = new Project();
			project.projectType = districtJsonObject.optString("MPCS_PROJECT_TYPE");
			project.projectId = districtJsonObject.optString("MPCS_PROJECT_TYPE_ID");
			projectList.add(project);
		}

		projectCount = projectList.size();

		saveProjectData(projectList);

	}

	private void parseAllProjectData(JSONObject resultJsonObject) {

		JSONArray districtArray = resultJsonObject.optJSONArray("objDistList");
		JSONArray blockArray = resultJsonObject.optJSONArray("objBlockList");
		JSONArray mpcsProjArray = resultJsonObject.optJSONArray("objProjectList");
		for (int i = 0; i < districtArray.length(); i++) {
			JSONObject districtJsonObject = districtArray.optJSONObject(i);
			District district = new District();
			district.DIS_ID = districtJsonObject.optString("DIS_ID");
			district.DIS_NAME = districtJsonObject.optString("DIS_NAME");
			district.PROJECT_TYPE = districtJsonObject.optString("PROJECT_TYPE");
			districtList.add(district);

		}
		for (int i = 0; i < blockArray.length(); i++) {
			JSONObject blockJsonObject = blockArray.optJSONObject(i);
			Block block = new Block();
			block.BLOCK_ID = blockJsonObject.optString("BLOCK_ID");
			block.BLOCK_NAME = blockJsonObject.optString("BLOCK_NAME");
			block.DIST_ID = blockJsonObject.optString("DIST_ID");
			block.PROJECT_TYPE = blockJsonObject.optString("PROJECT_TYPE").trim();
			blockList.add(block);
		}
		for (int i = 0; i < mpcsProjArray.length(); i++) {
			JSONObject mpcsProjJsonObject = mpcsProjArray.optJSONObject(i);
			MPCSProject mpcsProject = new MPCSProject();
			mpcsProject.id = mpcsProjJsonObject.optString("ID");
			mpcsProject.blockId = mpcsProjJsonObject.optString("BLOCK_ID");
			mpcsProject.districtId = mpcsProjJsonObject.optString("DISTRICT_ID");
			mpcsProject.mpcspProject = mpcsProjJsonObject.optString("MPCSP_PROJECT");
			mpcsProject.packageNo = mpcsProjJsonObject.optString("PACKAGE_NO");
			mpcsProject.projectType = mpcsProjJsonObject.optString("PROJECT_TYPE").trim();
			mpcsProject.dateTargetCompletion = mpcsProjJsonObject.optString("TAGET_COMPLETION_DATE");
			// mpcsProject.dateOfReport =
			// mpcsProjJsonObject.optString("DATE_OF_REPORT");
			mpcsProject.contractAmount = mpcsProjJsonObject.optString("CONTRACT_AMOUNT");
			mpcsProjList.add(mpcsProject);
		}

		saveData(districtList, blockList, mpcsProjList);
		districtList.clear();
		blockList.clear();
		mpcsProjList.clear();

	}

	// ============= @ SAVE DATA IN DATABASE @ ==========

	private void saveProjectData(ArrayList<Project> projects) {

		for (int i = 0; i < projects.size(); i++) {

			ContentValues cv = new ContentValues();
			cv.put(DBConstants.PROJECT_ID, projects.get(i).projectId);
			cv.put(DBConstants.PROJECT_TYPE, projects.get(i).projectType);
			new ProjectDB().saveMPCSProjects(LoginActivity.this, cv);
		}
		fetchDistrictsBlocksProjects();
	}

	private void saveData(ArrayList<District> district, ArrayList<Block> block, ArrayList<MPCSProject> mpcsProject) {

		for (int i = 0; i < district.size(); i++) {

			ContentValues cv = new ContentValues();
			cv.put(DBConstants.DISTRICT_ID, district.get(i).DIS_ID);
			cv.put(DBConstants.DISTRICT_NAME, district.get(i).DIS_NAME);
			cv.put(DBConstants.DISTRICT_PROJ_TYPE, district.get(i).PROJECT_TYPE);
			new DistrictDB().saveDistrictData(LoginActivity.this, cv);
		}

		for (int i = 0; i < block.size(); i++) {

			ContentValues cv = new ContentValues();
			cv.put(DBConstants.BLOCK_ID, block.get(i).BLOCK_ID);
			cv.put(DBConstants.BLOCK_NAME, block.get(i).BLOCK_NAME);
			cv.put(DBConstants.BLOCK_DISTRICT_ID, block.get(i).DIST_ID);
			cv.put(DBConstants.BLOCK_PROJ_TYPE, block.get(i).PROJECT_TYPE);
			new BlockDB().saveBlockData(LoginActivity.this, cv);
		}

		for (int i = 0; i < mpcsProjList.size(); i++) {

			ContentValues cv = new ContentValues();
			cv.put(DBConstants.MPCS_BLOCK_ID, mpcsProject.get(i).blockId);
			cv.put(DBConstants.MPCS_DIST_ID, mpcsProject.get(i).districtId);
			cv.put(DBConstants.MPCS_PROJ_ID, mpcsProject.get(i).id);
			cv.put(DBConstants.MPCS_PROJ_NAME, mpcsProject.get(i).mpcspProject);
			cv.put(DBConstants.MPCS_PROJ_PACKAGE_NUMBER, mpcsProject.get(i).packageNo);
			cv.put(DBConstants.MPCS_PROJ_TYPE, mpcsProject.get(i).projectType);
			cv.put(DBConstants.MPCS_PROJ_TARGET_COMPLETION_DATE, mpcsProject.get(i).dateTargetCompletion);
			cv.put(DBConstants.MPCS_PROJ_DATE_OF_REPORT, mpcsProject.get(i).dateOfReport);
			cv.put(DBConstants.MPCS_PROJ_CONTRACT_AMOUNT, mpcsProject.get(i).contractAmount);
			new MPCSProjectDB().saveMPCSProjData(LoginActivity.this, cv);

		}
		fetchDistrictsBlocksProjects();
	}

	private void fetchDistrictsBlocksProjects() {

		System.out.println("" + projectCount);
		if (projectCount > 0) {

			isFetchAllProjectDataService = true;
			HashMap<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put(MPCS_PROJECT_TYPE_ID, projectList.get(projectCount - 1).projectId);
			volleyTaskManager.doPostFetchAllProjectData(paramsMap);

		} else {

			Util.saveUserClass(LoginActivity.this, user);
			volleyTaskManager.hideProgressDialog();
			openHomeActivity();
		}

	}

	private void openHomeActivity() {

		Intent intent = new Intent(LoginActivity.this, NewFormActivity.class);
		startActivity(intent);
		finish();

	}

	private void showForgotPassword() {
		customDialog = new Dialog(mContext);

		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.dialog_forgot_password, null);

		ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				customDialog.dismiss();

			}
		});

		final EditText et_email = (EditText) view.findViewById(R.id.et_email);
		Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = et_email.getText().toString().trim();

				if (email.isEmpty()) {
					Toast.makeText(mContext, "Please enter your registered email address.", Toast.LENGTH_SHORT).show();
				} else {
					forgetPasswordEmail = email;
					isForgotPasswordService = true;
					getBaseUrl();
				}
			}
		});
		customDialog.setCancelable(false);
		customDialog.setContentView(view);
		customDialog.setCanceledOnTouchOutside(false);
		// Start AlertDialog
		customDialog.show();
	}

}
