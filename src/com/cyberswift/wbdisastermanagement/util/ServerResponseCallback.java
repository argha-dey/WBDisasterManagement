package com.cyberswift.wbdisastermanagement.util;

import org.json.JSONObject;

public interface ServerResponseCallback {
	
	public void onSuccess(JSONObject resultJsonObject);

	public void onSuccess(String resultJsonObject);

	public void onError();

}
