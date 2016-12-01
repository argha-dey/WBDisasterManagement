package com.cyberswift.wbdisastermanagement.service;

import org.json.JSONObject;

abstract class ServiceConnector {

	//protected static String baseURL = "http://csusazurevmtest.cloudapp.net/DisasterWebGIS_Services/Service1.svc/"; // <-- LIVE Server
	protected static String baseURL = "http://192.168.1.159/DisasterWebGIS_Services/Service1.svc/"; // <-- Singham's machine

	protected JSONObject outputJson;

	public static String getRedirectionURL() {

		return baseURL;
	}

	public JSONObject getOutputJson() {

		return outputJson;
	}

	public void setOutputJson(JSONObject outputJson) {

		this.outputJson = outputJson;
	}

}
