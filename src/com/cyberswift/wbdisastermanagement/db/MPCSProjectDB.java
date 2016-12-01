package com.cyberswift.wbdisastermanagement.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cyberswift.wbdisastermanagement.model.MPCSProject;

public class MPCSProjectDB implements DBConstants {

	private static MPCSProjectDB obj = null;

	public synchronized static MPCSProjectDB obj() {

		if (obj == null)
			obj = new MPCSProjectDB();
		return obj;

	}

	public Boolean saveMPCSProjData(Context context, ContentValues cv) {

		System.out.println(" ----------  ADD MPCS PROJECT DATA IN MPCS-PROJECT  TABLE  --------- ");
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.insert(MPCS_PROJECT_TABLE, null, cv);
			mdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			mdb.endTransaction();
			return true;
		}

	}

	public ArrayList<MPCSProject> getMPCSProjects(Context context, String districtID, String blockID, String projectType) {

		Log.d("getMPCSProjects", "District id: " + districtID + "Block id: " + blockID);
		ArrayList<MPCSProject> mpcsProjArray = new ArrayList<MPCSProject>();
		String[] columns = { _ID, MPCS_BLOCK_ID, MPCS_DIST_ID, MPCS_PROJ_ID, MPCS_PROJ_NAME, MPCS_PROJ_PACKAGE_NUMBER,
				MPCS_PROJ_TARGET_COMPLETION_DATE, MPCS_PROJ_DATE_OF_REPORT, MPCS_PROJ_CONTRACT_AMOUNT };

		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getReadableDatabase();
		Cursor cur = mdb.query(MPCS_PROJECT_TABLE, columns, MPCS_DIST_ID + "=?" + "AND " + MPCS_BLOCK_ID + "=?"
				+ "AND " + MPCS_PROJ_TYPE + "=?", new String[] { districtID, blockID, projectType }, null, null, null);

		if (!isDatabaseEmpty(cur)) {
			try {
				if (cur.moveToFirst()) {
					do {
						MPCSProject mpcsProject = new MPCSProject();

						mpcsProject.blockId = cur.getString(cur.getColumnIndex(MPCS_BLOCK_ID));
						mpcsProject.districtId = cur.getString(cur.getColumnIndex(MPCS_DIST_ID));
						mpcsProject.id = cur.getString(cur.getColumnIndex(MPCS_PROJ_ID));
						mpcsProject.mpcspProject = cur.getString(cur.getColumnIndex(MPCS_PROJ_NAME));
						mpcsProject.packageNo = cur.getString(cur.getColumnIndex(MPCS_PROJ_PACKAGE_NUMBER));
						mpcsProject.dateTargetCompletion = cur.getString(cur
								.getColumnIndex(MPCS_PROJ_TARGET_COMPLETION_DATE));
						mpcsProject.dateOfReport = cur.getString(cur.getColumnIndex(MPCS_PROJ_DATE_OF_REPORT));
						mpcsProject.contractAmount = cur.getString(cur.getColumnIndex(MPCS_PROJ_CONTRACT_AMOUNT));

						mpcsProjArray.add(mpcsProject);
					} while (cur.moveToNext());
				}
				cur.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.e("Cursor", "Cursor is empty");
		}
		Log.v("getMPCSProjects", "getMPCSProjects SIZE: " + mpcsProjArray.size());
		return mpcsProjArray;
	}

	private Boolean isDatabaseEmpty(Cursor mCursor) {

		if (mCursor.moveToFirst()) {
			// NOT EMPTY
			return false;

		} else {
			// IS EMPTY
			return true;
		}

	}

	/**
	 * Checks whether the Offline MPCS Project Table contains any entries or not
	 * */
	public Boolean isMPCSProjectAvailableOffline(Context context) {

		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getReadableDatabase();
		Cursor cur = mdb.query(MPCS_PROJECT_TABLE, null, null, null, null, null, null);
		return !isDatabaseEmpty(cur);
	}

	/**
	 * Returns the database entry count
	 * */
	public long getProfilesCount(Context context) {
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getReadableDatabase();
		long count = DatabaseUtils.queryNumEntries(mdb, MPCS_PROJECT_TABLE);
		return count;
	}

}
