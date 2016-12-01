package com.cyberswift.wbdisastermanagement.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DisaterManagementDatabase extends SQLiteOpenHelper implements DBConstants {

	private static final String TAG = "DisaterManagementDatabase";
	private static DisaterManagementDatabase mDatabase;
	private SQLiteDatabase mDb;

	public DisaterManagementDatabase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public static final DisaterManagementDatabase getInstance(Context context) {
		if (mDatabase == null) {
			mDatabase = new DisaterManagementDatabase(context);
			mDatabase.getWritableDatabase();
		}
		return mDatabase;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		Log.i(TAG, "oncreate tables");
		// create table
		String[] createStatements = getCreatetableStatements();
		int total = createStatements.length;
		for (int i = 0; i < total; i++) {
			Log.i(TAG, "executing create query " + createStatements[i]);
			Log.i("Database", "Database created");
			db.execSQL(createStatements[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("Tag", "Old version" + oldVersion + " New version: " + newVersion + "Constant variable version name: " + DB_VERSION);

		db.execSQL("DROP TABLE IF EXISTS " + PROJECT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DISTRICT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + BLOCK_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + MPCS_PROJECT_TABLE);
		db.setVersion(DB_VERSION);

		// create new tables
		onCreate(db);
	}

	private String[] getCreatetableStatements() {

		String[] create = new String[5];

		// MPCS Project table -> _id , projectID, projectType
		String PROJECT_TABLE_ST = CREATE_TABLE_BASE + PROJECT_TABLE + START_COLUMN + _ID + INTEGER + PRIMARY_KEY + AUTO_ICNREMENT + COMMA
				+ PROJECT_ID + TEXT + COMMA + PROJECT_TYPE + TEXT + COMMA + UNIQUE + START_COLUMN + PROJECT_ID + FINISH_COLUMN
				+ ON_CONFLICT_REPLACE + FINISH_COLUMN;

		create[0] = PROJECT_TABLE_ST;

		// District table -> _id , districtID, districtProjType, districtName
		String DISTRICT_TABLE_ST = CREATE_TABLE_BASE + DISTRICT_TABLE + START_COLUMN + _ID + INTEGER + PRIMARY_KEY + AUTO_ICNREMENT + COMMA
				+ DISTRICT_ID + TEXT + COMMA + DISTRICT_PROJ_TYPE + TEXT + COMMA + DISTRICT_NAME + TEXT + COMMA + UNIQUE + START_COLUMN
				+ DISTRICT_ID + COMMA + DISTRICT_PROJ_TYPE + FINISH_COLUMN + ON_CONFLICT_REPLACE + FINISH_COLUMN;

		create[1] = DISTRICT_TABLE_ST;

		// Block table -> _id , blockID, blockName, blockProjType , districtId
		String BLOCK_TABLE_ST = CREATE_TABLE_BASE + BLOCK_TABLE + START_COLUMN + _ID + INTEGER + PRIMARY_KEY + AUTO_ICNREMENT + COMMA
				+ BLOCK_ID + TEXT + COMMA + BLOCK_NAME + TEXT + COMMA + BLOCK_PROJ_TYPE + TEXT + COMMA + BLOCK_DISTRICT_ID + TEXT + COMMA
				+ UNIQUE + START_COLUMN + BLOCK_ID + COMMA + BLOCK_DISTRICT_ID + COMMA + BLOCK_PROJ_TYPE + FINISH_COLUMN
				+ ON_CONFLICT_REPLACE + FINISH_COLUMN;

		create[2] = BLOCK_TABLE_ST;

		// MPCS Project Details table -> _id , mpcsBlocksId, mpcsDistId, mpcsProjId , mpcsProjName, mpcsProjPackageNumber
		//, mpcsProjType, mpcsProjTargetDate, mpcsProjReportDate, mpcsProjContractAmt
		String MPCS_PROJECT_TABLE_ST = CREATE_TABLE_BASE + MPCS_PROJECT_TABLE + START_COLUMN + _ID + INTEGER + PRIMARY_KEY + AUTO_ICNREMENT
				+ COMMA + MPCS_BLOCK_ID + TEXT + COMMA + MPCS_DIST_ID + TEXT + COMMA + MPCS_PROJ_ID + TEXT + COMMA + MPCS_PROJ_NAME + TEXT
				+ COMMA + MPCS_PROJ_PACKAGE_NUMBER + TEXT + COMMA + MPCS_PROJ_TYPE + TEXT + COMMA + MPCS_PROJ_TARGET_COMPLETION_DATE + TEXT
				+ COMMA + MPCS_PROJ_DATE_OF_REPORT + TEXT + COMMA + MPCS_PROJ_CONTRACT_AMOUNT + TEXT + COMMA + UNIQUE + START_COLUMN
				+ MPCS_PROJ_ID + FINISH_COLUMN + ON_CONFLICT_REPLACE + FINISH_COLUMN;
		// MPCS Project Details table
		create[3] = MPCS_PROJECT_TABLE_ST;

		// Form Offline Data table -> _id 
		String FORM_DATA_TABLE_ST = CREATE_TABLE_BASE + FORM_DATA_TABLE + START_COLUMN + _ID + INTEGER + PRIMARY_KEY + AUTO_ICNREMENT
				+ COMMA + FORM_ONGOING_PROJECT_ID + TEXT + COMMA + FORM_STAGE1 + TEXT + FORM_STAGE1_ABSOLUTE + TEXT + FORM_STAGE2 + TEXT
				+ FORM_STAGE2_ABSOLUTE + TEXT + FORM_STAGE3 + TEXT + FORM_STAGE3_ABSOLUTE + TEXT + FORM_STAGE4 + TEXT
				+ FORM_STAGE4_ABSOLUTE + TEXT + FORM_STAGE5 + TEXT + FORM_STAGE5_ABSOLUTE + TEXT + FORM_STAGE6 + TEXT
				+ FORM_STAGE6_ABSOLUTE + TEXT + FORM_STAGE7 + TEXT + FORM_STAGE7_ABSOLUTE + TEXT + FORM_STAGE8 + TEXT
				+ FORM_STAGE8_ABSOLUTE + TEXT + FORM_STAGE9 + TEXT + FORM_STAGE9_ABSOLUTE + TEXT + FORM_STAGE10 + TEXT
				+ FORM_STAGE10_ABSOLUTE + TEXT + FORM_STAGE11 + TEXT + FORM_STAGE11_ABSOLUTE + TEXT + FORM_USER_ID + TEXT + FORM_COMMENT
				+ TEXT + FORM_LAT + TEXT + FORM_LON + TEXT + FORM_ISM_HARD_BARRICADE + TEXT + FORM_ISM_PROTECTIVE_HELMETS_SHOES + TEXT
				+ FORM_ISM_BARRICADE_OF_SITE + TEXT + FORM_ISM_ELECTRICAL_WIRING + TEXT + FORM_ISM_HARD_HAT_AND_SHOE + TEXT
				+ FORM_ISM_FIRE_SAFETY_DEVICES + TEXT + FORM_ILW_LABOURCAMP_SANITATION_WATER_SUPPLY + TEXT
				+ FORM_ILW_LABOURCAMP_STRUCTURE_SATISFACTORY + TEXT + FORM_ILW_LPG_FOR_LABOUR + TEXT + FORM_ILW_SEPARATE_TOILETS + TEXT
				+ FORM_ILW_DRINKING_WATER_FOR_WORKER + TEXT + FORM_ID_MPCS_INFORMATION_BOARD + TEXT + FORM_DATE_OF_REPORT + TEXT
				+ FORM_IMAGE_1 + TEXT + FORM_IMAGE_2 + TEXT + FORM_IMAGE_3 + TEXT + FINISH_COLUMN;

		create[4] = FORM_DATA_TABLE_ST;

		return create;
	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {

		return mDb != null ? mDb : (mDb = super.getWritableDatabase());
	}

	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {

		return mDb != null ? mDb : (mDb = super.getReadableDatabase());
	}

	public void startmanagingcursor() {
		mDatabase.startmanagingcursor();
	}

}
