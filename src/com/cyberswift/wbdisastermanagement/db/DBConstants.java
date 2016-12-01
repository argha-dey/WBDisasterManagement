package com.cyberswift.wbdisastermanagement.db;

public interface DBConstants {

	public static final int DB_VERSION = 1;

	public static final String DB_NAME = "WBDisaster.db";
	//public static final String DB_NAME = Environment.getExternalStorageDirectory() + "/WBDisaster.db";

	public static final String _ID = "_id";

	final String CREATE_TABLE_BASE = "CREATE TABLE IF NOT EXISTS ";

	final String ON = " ON ";

	final String PRIMARY_KEY = " PRIMARY KEY";

	final String INTEGER = " Integer";

	final String TEXT = " TEXT";

	final String DATE_TIME = " DATETIME";

	final String BLOB = " BLOB";

	final String AUTO_ICNREMENT = " AUTOINCREMENT";

	final String UNIQUE = "UNIQUE";

	final String START_COLUMN = " ( ";

	final String FINISH_COLUMN = " ) ";

	final String COMMA = ",";

	final String ON_CONFLICT_REPLACE = "ON CONFLICT REPLACE";

	// Project Table
	public static final String PROJECT_TABLE = " projectTable";

	public static final String PROJECT_ID = "projectID";

	public static final String PROJECT_TYPE = "projectType";

	// District Table
	public static final String DISTRICT_TABLE = " districtTable";

	public static final String DISTRICT_ID = "districtID";

	public static final String DISTRICT_NAME = "districtName";

	public static final String DISTRICT_PROJ_TYPE = "districtProjType";

	// Block Table
	public static final String BLOCK_TABLE = " blockTable";

	public static final String BLOCK_ID = "blockID";

	public static final String BLOCK_NAME = "blockName";

	public static final String BLOCK_DISTRICT_ID = "districtId";

	public static final String BLOCK_PROJ_TYPE = "blockProjType";

	// MPS Projects Table
	public static final String MPCS_PROJECT_TABLE = " mpcsProjectTable";

	public static final String MPCS_BLOCK_ID = "mpcsBlocksId";

	public static final String MPCS_DIST_ID = "mpcsDistId";

	public static final String MPCS_PROJ_ID = "mpcsProjId";

	public static final String MPCS_PROJ_NAME = "mpcsProjName";

	public static final String MPCS_PROJ_PACKAGE_NUMBER = "mpcsProjPackageNumber";

	public static final String MPCS_PROJ_TYPE = "mpcsProjType";

	public static final String MPCS_PROJ_TARGET_COMPLETION_DATE = "mpcsProjTargetDate";

	public static final String MPCS_PROJ_DATE_OF_REPORT = "mpcsProjReportDate";

	public static final String MPCS_PROJ_CONTRACT_AMOUNT = "mpcsProjContractAmt";

	// Form Data Table
	public static final String FORM_DATA_TABLE = " fromDataTable";

	public static final String FORM_ONGOING_PROJECT_ID = "onGoingProjectId";

	public static final String FORM_STAGE1 = "stage1";

	public static final String FORM_STAGE1_ABSOLUTE = "stage1_absolute";

	public static final String FORM_STAGE2 = "stage2";

	public static final String FORM_STAGE2_ABSOLUTE = "stage2_absolute";

	public static final String FORM_STAGE3 = "stage3";

	public static final String FORM_STAGE3_ABSOLUTE = "stage3_absolute";

	public static final String FORM_STAGE4 = "stage4";

	public static final String FORM_STAGE4_ABSOLUTE = "stage4_absolute";

	public static final String FORM_STAGE5 = "stage5";

	public static final String FORM_STAGE5_ABSOLUTE = "stage5_absolute";

	public static final String FORM_STAGE6 = "stage6";

	public static final String FORM_STAGE6_ABSOLUTE = "stage6_absolute";

	public static final String FORM_STAGE7 = "stage7";

	public static final String FORM_STAGE7_ABSOLUTE = "stage7_absolute";

	public static final String FORM_STAGE8 = "stage8";

	public static final String FORM_STAGE8_ABSOLUTE = "stage8_absolute";

	public static final String FORM_STAGE9 = "stage9";

	public static final String FORM_STAGE9_ABSOLUTE = "stage9_absolute";

	public static final String FORM_STAGE10 = "stage10";

	public static final String FORM_STAGE10_ABSOLUTE = "stage10_absolute";

	public static final String FORM_STAGE11 = "stage11";

	public static final String FORM_STAGE11_ABSOLUTE = "stage11_absolute";

	public static final String FORM_USER_ID = "userID";

	public static final String FORM_COMMENT = "comment";

	public static final String FORM_LAT = "latitude";

	public static final String FORM_LON = "longitude";

	public static final String FORM_ISM_HARD_BARRICADE = "ismHardBarricade";

	public static final String FORM_ISM_PROTECTIVE_HELMETS_SHOES = "ismProtectiveHelmetShoes";

	public static final String FORM_ISM_BARRICADE_OF_SITE = "ismBarricadeOfSite";

	public static final String FORM_ISM_ELECTRICAL_WIRING = "ismElectricalWiring";

	public static final String FORM_ISM_HARD_HAT_AND_SHOE = "ismHardHatShoes";

	public static final String FORM_ISM_FIRE_SAFETY_DEVICES = "ismFireSafetyDevices";

	public static final String FORM_ILW_LABOURCAMP_SANITATION_WATER_SUPPLY = "ilw_labourCamp_sanitation_waterSupply";

	public static final String FORM_ILW_LABOURCAMP_STRUCTURE_SATISFACTORY = "ilw_labourcampStructureSatisfactory";

	public static final String FORM_ILW_LPG_FOR_LABOUR = "ilw_lpg";

	public static final String FORM_ILW_SEPARATE_TOILETS = "ilw_seperateToilet";

	public static final String FORM_ILW_DRINKING_WATER_FOR_WORKER = "ilw_drinkingWater";

	public static final String FORM_ID_MPCS_INFORMATION_BOARD = "id_mpcsInfoBoard";

	public static final String FORM_DATE_OF_REPORT = "dateOfReport";

	public static final String FORM_IMAGE_1 = "firstImage";

	public static final String FORM_IMAGE_2 = "secondImage";

	public static final String FORM_IMAGE_3 = "thirdImage";

}
