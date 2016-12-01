package com.cyberswift.wbdisastermanagement.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cyberswift.wbdisastermanagement.R;
import com.cyberswift.wbdisastermanagement.app.AppController;
import com.cyberswift.wbdisastermanagement.util.ServerResponseCallback;
import com.cyberswift.wbdisastermanagement.util.Util;

@SuppressLint("ShowToast")
public class VolleyTaskManager extends ServiceConnector {
	private Context mContext;
	private ProgressDialog mProgressDialog;
	private String TAG = "";
	private String tag_json_obj = "jobj_req";
	private boolean isToShowDialog = true, isToHideDialog = true;

	private static int mStatusCode;
	private static ProgressDialog progressdialog;
	private static Activity activity;

	public VolleyTaskManager(Context context) {
		mContext = context;

		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage("Loading...");

		TAG = mContext.getClass().getSimpleName();
		Log.d("tag", TAG);
	}

	public void showProgressDialog() {
		if (!mProgressDialog.isShowing())
			mProgressDialog.show();
	}

	public void hideProgressDialog() {
		if (mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	/**
	 * 
	 * Making json object request
	 * */
	private void makeJsonObjReq(int method, String url, final Map<String, String> paramsMap) {
		if (isToShowDialog) {
			showProgressDialog();
		}

		Log.v("JSONObject", new JSONObject(paramsMap).toString());

		JsonObjectRequest jsonObjReq = new JsonObjectRequest(method, url, new JSONObject(paramsMap), new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				Log.d(TAG, response.toString());
				// msgResponse.setText(response.toString());
				if (isToHideDialog) {
					hideProgressDialog();
				}
				// TODO On getting successful result:
				((ServerResponseCallback) mContext).onSuccess(response);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				hideProgressDialog();
				VolleyLog.d(TAG, "Error: " + error.getMessage());

				if (error instanceof TimeoutError || error instanceof NoConnectionError) {
					Log.d("error ocurred", "TimeoutError");
					Toast.makeText(mContext, mContext.getString(R.string.response_timeout), Toast.LENGTH_LONG).show();
				} else if (error instanceof AuthFailureError) {
					Log.d("error ocurred", "AuthFailureError");
					Toast.makeText(mContext, mContext.getString(R.string.auth_failure), Toast.LENGTH_LONG).show();
				} else if (error instanceof ServerError) {
					Log.d("error ocurred", "ServerError");
					Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_LONG).show();
				} else if (error instanceof NetworkError) {
					Log.d("error ocurred", "NetworkError");
					Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_LONG).show();
				} else if (error instanceof ParseError) {
					Log.d("error ocurred", "ParseError");
					Toast.makeText(mContext, mContext.getString(R.string.parse_error), Toast.LENGTH_LONG).show();
				}

				((ServerResponseCallback) mContext).onError();
			}
		}) {

			/**
			 * Passing some request headers
			 * */
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/json; charset=utf-8");
				return headers;
			}

			@Override
			protected Map<String, String> getParams() {

				return paramsMap;
			}

		};

		jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

		// Cancelling request
		// ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
	}

	// ============== @ GET @ =========

	public void doGet(String value) {
		String url = getRedirectionURL() + "Replace here with Url" + value;
		int method = Method.GET;
		Log.i("url", url);
		makeJsonObjReq(method, url, new HashMap<String, String>());
		
	}

	// ============== @ POST @ =========

	/**
	 * Service method calling for Getting Base url -->
	 **/

	public void doGetBaseUrl(HashMap<String, String> paramsMap, boolean isToHideDialog) {
		this.isToHideDialog = isToHideDialog;
		String url = getRedirectionURL() + "GetRedirectionURL";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(paramsMap);
		makeJsonObjReq(method, url, paramsMap);
		
	}

	/**
	 * Service method calling for Login -->
	 **/

	public void doLogin(HashMap<String, String> paramsMap, boolean isToHideDialog) {

		this.isToHideDialog = isToHideDialog;
		String url = Util.fetchUserClass(mContext).getBaseUrl() + "MobileUserLogIn";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(paramsMap);
		makeJsonObjReq(method, url, paramsMap);
		
	}

	/**
	 * Service method calling for Registration -->
	 **/

	public void doRegistration(String baseUrl, HashMap<String, String> paramsMap, boolean isToHideDialog) {

		this.isToHideDialog = isToHideDialog;
		String url = baseUrl + "SaveMobileUser";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(paramsMap);
		makeJsonObjReq(method, url, paramsMap);
	}

	/**
	 * Service method calling for Forget Password -->
	 **/

	public void doPostForgetPassword(HashMap<String, String> paramsMap, boolean isToHideDialog) {

		this.isToHideDialog = isToHideDialog;
		String url = Util.fetchUserClass(mContext).getBaseUrl() + "ForgetPasswordMob";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(paramsMap);
		makeJsonObjReq(method, url, paramsMap);
	}

	/**
	 * Fetch MPCS projects
	 */
	public void doPostFetchProject(HashMap<String, String> requestMap) {

		String url = Util.fetchUserClass(mContext).getBaseUrl() + "GetProjectTypes";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(requestMap);
		makeJsonObjReq(method, url, requestMap);
	}

	/**
	 * Fetch MPCS projects Details
	 */
	public void doPostFetchProjectDetails(HashMap<String, String> requestMap) {

		String url = Util.fetchUserClass(mContext).getBaseUrl() + "GetProjectFinanceInfo";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(requestMap);
		makeJsonObjReq(method, url, requestMap);
	}

	/**
	 * Fetch All project data
	 */
	public void doPostFetchAllProjectData(HashMap<String, String> requestMap) {

		String url = Util.fetchUserClass(mContext).getBaseUrl() + "GetMPCSProjectsByProjectType";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(requestMap);
		makeJsonObjReq(method, url, requestMap);
	}

	/**
	 * Fetch time-line
	 */
	public void doPostFetchTimeLine(HashMap<String, String> requestMap) {

		String url = Util.fetchUserClass(mContext).getBaseUrl() + "GetProjectSurveyPreviousDates";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(requestMap);
		makeJsonObjReq(method, url, requestMap);
	}

	/**
	 * Fetch Milestone
	 */
	public void doPostFetchMilestone(HashMap<String, String> requestMap) {

		String url = Util.fetchUserClass(mContext).getBaseUrl() + "GetProjectPhysicalProgressLog";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(requestMap);
		makeJsonObjReq(method, url, requestMap);
	}

	/**
	 * Post form data
	 **/
	public void doInsertUpdateMPCSProjectsPhysicalProgress(HashMap<String, String> requestMap, boolean isToShowDialog) {
		this.isToShowDialog = isToShowDialog;
		String url = Util.fetchUserClass(mContext).getBaseUrl() + "InsertUpdateMPCSProjectsPhysicalProgress";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(requestMap);
		makeJsonObjReq(method, url, requestMap);

	}

	/**
	 * Post Pictures
	 */
	public void doPostImage(HashMap<String, String> requestMap, boolean isToShowDialog) {
		this.isToShowDialog = isToShowDialog;
		String url = Util.fetchUserClass(mContext).getBaseUrl() + "FDSaveImageData";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(requestMap);
		makeJsonObjReq(method, url, requestMap);
	}

	/**
	 * Post Feedback
	 */
	public void doPostFeedback(HashMap<String, String> requestMap, boolean isToShowDialog) {
		this.isToShowDialog = isToShowDialog;
		String url = Util.fetchUserClass(mContext).getBaseUrl() + "FeedBackSave";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(requestMap);
		makeJsonObjReq(method, url, requestMap);
	}

	/**
	 * Change Password
	 */
	public void doPostChangePassword(HashMap<String, String> requestMap, boolean isToShowDialog) {
		this.isToShowDialog = isToShowDialog;
		String url = Util.fetchUserClass(mContext).getBaseUrl() + "ChangePasswordMobile";
		int method = Method.POST;

		Log.i("url", url);
		System.out.println(requestMap);
		makeJsonObjReq(method, url, requestMap);
	}

}
