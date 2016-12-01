package com.cyberswift.wbdisastermanagement.model;

public class FinancialInfo {

	private String COLUMN_NAME = "";

	private String COLUMN_VALUE = "";

	private String PROJECT_TYPE_ID = "";

	private String SLNO = "";

	public String getCOLUMN_NAME() {

		return COLUMN_NAME;
	}

	public void setCOLUMN_NAME(String cOLUMN_NAME) {

		COLUMN_NAME = cOLUMN_NAME;
	}

	public String getCOLUMN_VALUE() {

		return COLUMN_VALUE;
	}

	public void setCOLUMN_VALUE(String cOLUMN_VALUE) {

		COLUMN_VALUE = cOLUMN_VALUE;
	}

	public String getPROJECT_TYPE_ID() {

		return PROJECT_TYPE_ID;
	}

	public void setPROJECT_TYPE_ID(String pROJECT_TYPE_ID) {

		PROJECT_TYPE_ID = pROJECT_TYPE_ID;
	}

	public String getSLNO() {

		return SLNO;
	}

	public void setSLNO(String sLNO) {

		SLNO = sLNO;
	}

}
