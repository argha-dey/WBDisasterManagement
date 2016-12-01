package com.cyberswift.wbdisastermanagement.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class OfflineDataSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;

	private String serviceType = "";
	private HashMap<String, String> paramsMap = new HashMap<String, String>();
	private ArrayList<ImageClass> imagesList = new ArrayList<ImageClass>();
	private int imagePosition = 0;
	private String returnId = "";
	private boolean isDataPostedToserver = false;
	private boolean isAllImagesPostedToserver = false;

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public HashMap<String, String> getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(HashMap<String, String> paramsMap) {
		this.paramsMap = paramsMap;
	}

	public ArrayList<ImageClass> getImagesList() {
		return imagesList;
	}

	public void setImagesList(ArrayList<ImageClass> imagesList) {
		this.imagesList = imagesList;
	}

	public int getImagePosition() {
		return imagePosition;
	}

	public void setImagePosition(int imagePosition) {
		this.imagePosition = imagePosition;
	}

	public String getReturnId() {
		return returnId;
	}

	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	public boolean getIsDataPostedToserver() {
		return isDataPostedToserver;
	}

	public void setIsDataPostedToserver(boolean isDataPostedToserver) {
		this.isDataPostedToserver = isDataPostedToserver;
	}

	public boolean getIsAllImagesPostedToserver() {
		return isAllImagesPostedToserver;
	}

	public void setIsAllImagesPostedToserver(boolean isAllImagesPostedToserver) {
		this.isAllImagesPostedToserver = isAllImagesPostedToserver;
	}
}
