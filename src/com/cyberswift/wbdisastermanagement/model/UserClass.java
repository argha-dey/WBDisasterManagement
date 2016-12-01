package com.cyberswift.wbdisastermanagement.model;

import java.io.Serializable;

public class UserClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = "";
	private String id;
	private String userId = "";
	private String databaseId = "";
	private String phone = "";
	private String email = "";
	private String adharVoterId = "";
	private String password = "";
	private boolean isRemember = false;
	private boolean isLoggedin = false;
	private boolean isAllDataFetched = false;
	private long projectCountOnline = 0;

	public long getProjectCountOnline() {
		return projectCountOnline;
	}

	public void setProjectCountOnline(long projectCountOnline) {
		this.projectCountOnline = projectCountOnline;
	}

	private String loggedInDateTime = "";

	private String baseUrl = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAdharVoterId() {
		return adharVoterId;
	}

	public void setAdharVoterId(String adharVoterId) {
		this.adharVoterId = adharVoterId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getIsRemember() {
		return isRemember;
	}

	public void setIsRemember(boolean isRemember) {
		this.isRemember = isRemember;
	}

	public boolean getIsLoggedin() {
		return isLoggedin;
	}

	public void setIsLoggedin(boolean isLoggedin) {
		this.isLoggedin = isLoggedin;
	}

	public boolean getIsAllDataFetched() {
		return isAllDataFetched;
	}

	public void setIsAllDataFetched(boolean isAllDataFetched) {
		this.isAllDataFetched = isAllDataFetched;
	}

	public void setRemember(boolean isRemember) {
		this.isRemember = isRemember;
	}

	public void setLoggedin(boolean isLoggedin) {
		this.isLoggedin = isLoggedin;
	}

	public void setAllDataFetched(boolean isAllDataFetched) {
		this.isAllDataFetched = isAllDataFetched;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getLoggedInDateTime() {
		return loggedInDateTime;
	}

	public void setLoggedInDateTime(String loggedInDateTime) {
		this.loggedInDateTime = loggedInDateTime;
	}

}
