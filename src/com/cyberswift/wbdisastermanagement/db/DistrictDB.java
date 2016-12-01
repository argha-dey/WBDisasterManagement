package com.cyberswift.wbdisastermanagement.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cyberswift.wbdisastermanagement.model.District;

public class DistrictDB implements DBConstants {

	private static DistrictDB obj = null;

	public synchronized static DistrictDB obj() {

		if (obj == null)
			obj = new DistrictDB();
		return obj;

	}

	public Boolean saveDistrictData(Context context, ContentValues cv) {

		System.out.println(" ----------  ADD DISTRICT DATA IN DISTRICT  TABLE  --------- ");
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.insert(DISTRICT_TABLE, null, cv);
			mdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			mdb.endTransaction();
			return true;
		}

	}

	public ArrayList<District> getDistrict(Context context, String projectType) {

		Log.d("getDistrict", "Project Type: "+projectType);
		String[] columns = { _ID, DISTRICT_ID, DISTRICT_NAME, DISTRICT_PROJ_TYPE };
		ArrayList<District> distrtictList = new ArrayList<District>();
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getReadableDatabase();
		Cursor cur = mdb.query(DISTRICT_TABLE, columns, DISTRICT_PROJ_TYPE + "=?", new String[] { projectType }, null, null, null);

		if (!isDatabaseEmpty(cur)) {

			// When there exists form data
			try {

				if (cur.moveToFirst()) {
					
					do {
						District district = new District();
						Log.i("DistrictDB", ""+cur.getString(cur.getColumnIndex(DISTRICT_ID)));
						Log.i("DistrictDB", ""+cur.getString(cur.getColumnIndex(DISTRICT_NAME)));
						district.DIS_ID = cur.getString(cur.getColumnIndex(DISTRICT_ID));
						district.DIS_NAME = cur.getString(cur.getColumnIndex(DISTRICT_NAME));

						distrtictList.add(district);
					} while (cur.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return distrtictList;
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

	public void clearAllDBTables(Context mcContext) {

		System.out.println(" ----------  CLEAR ALL TABLES  --------- ");
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(mcContext).getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.execSQL("DELETE FROM " + DISTRICT_TABLE);
			mdb.execSQL("DELETE FROM " + BLOCK_TABLE);
			mdb.execSQL("DELETE FROM " + MPCS_PROJECT_TABLE);
			mdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mdb.endTransaction();
		}
	}

}
