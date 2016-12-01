package com.cyberswift.wbdisastermanagement.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cyberswift.wbdisastermanagement.model.FormData;

public class FormDB implements DBConstants {
	private static FormDB obj = null;

	public synchronized static FormDB obj() {

		if (obj == null)
			obj = new FormDB();
		return obj;

	}

	/**
	 * Inserts the Form data in the Off-line Database table.
	 * 
	 * @param context
	 *            --> {@link Context}
	 * @param cv
	 *            --> {@link ContentValues}
	 * @return Boolean
	 * */
	public Boolean saveFormData(Context context, ContentValues cv) {

		System.out.println(" ----------  ADD FORM DATA IN FORM DATA TABLE  --------- ");
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.insert(FORM_DATA_TABLE, null, cv);
			mdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			mdb.endTransaction();
			return true;
		}

	}

	/**
	 * Fetch projects
	 * 
	 * @return {@link ArrayList}
	 * */
	public ArrayList<FormData> getFormData(Context context) {

		ArrayList<FormData> formList = new ArrayList<FormData>();
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getReadableDatabase();
		Cursor cur = mdb.query(FORM_DATA_TABLE, null, null, null, null, null, null);

		if (!isDatabaseEmpty(cur)) {

			// When there exists projects data
			try {

				if (cur.moveToFirst()) {
					do {
						FormData formData = new FormData();
						formData.FORM_ONGOING_PROJECT_ID = cur.getString(cur.getColumnIndex(FORM_ONGOING_PROJECT_ID));
						formData.FORM_STAGE1 = cur.getString(cur.getColumnIndex(FORM_STAGE1));
						formData.FORM_STAGE1_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE1_ABSOLUTE));
						formData.FORM_STAGE2 = cur.getString(cur.getColumnIndex(FORM_STAGE2));
						formData.FORM_STAGE2_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE2_ABSOLUTE));
						formData.FORM_STAGE3 = cur.getString(cur.getColumnIndex(FORM_STAGE3));
						formData.FORM_STAGE3_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE3_ABSOLUTE));
						formData.FORM_STAGE4 = cur.getString(cur.getColumnIndex(FORM_STAGE4));
						formData.FORM_STAGE4_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE4_ABSOLUTE));
						formData.FORM_STAGE5 = cur.getString(cur.getColumnIndex(FORM_STAGE5));
						formData.FORM_STAGE5_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE5_ABSOLUTE));
						formData.FORM_STAGE6 = cur.getString(cur.getColumnIndex(FORM_STAGE6));
						formData.FORM_STAGE6_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE6_ABSOLUTE));
						formData.FORM_STAGE7 = cur.getString(cur.getColumnIndex(FORM_STAGE7));
						formData.FORM_STAGE7_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE7_ABSOLUTE));
						formData.FORM_STAGE8 = cur.getString(cur.getColumnIndex(FORM_STAGE8));
						formData.FORM_STAGE8_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE8_ABSOLUTE));
						formData.FORM_STAGE9 = cur.getString(cur.getColumnIndex(FORM_STAGE9));
						formData.FORM_STAGE9_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE9_ABSOLUTE));
						formData.FORM_STAGE10 = cur.getString(cur.getColumnIndex(FORM_STAGE10));
						formData.FORM_STAGE10_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE10_ABSOLUTE));
						formData.FORM_STAGE11 = cur.getString(cur.getColumnIndex(FORM_STAGE11));
						formData.FORM_STAGE11_ABSOLUTE = cur.getString(cur.getColumnIndex(FORM_STAGE11_ABSOLUTE));
						formData.FORM_USER_ID = cur.getString(cur.getColumnIndex(FORM_USER_ID));
						formData.FORM_COMMENT = cur.getString(cur.getColumnIndex(FORM_COMMENT));

						formData.FORM_LAT = cur.getString(cur.getColumnIndex(FORM_LAT));
						formData.FORM_LON = cur.getString(cur.getColumnIndex(FORM_LON));
						formData.FORM_ISM_HARD_BARRICADE = cur.getString(cur.getColumnIndex(FORM_ISM_HARD_BARRICADE));
						formData.FORM_ISM_PROTECTIVE_HELMETS_SHOES = cur.getString(cur.getColumnIndex(FORM_ISM_PROTECTIVE_HELMETS_SHOES));
						formData.FORM_ISM_BARRICADE_OF_SITE = cur.getString(cur.getColumnIndex(FORM_ISM_BARRICADE_OF_SITE));
						formData.FORM_ISM_ELECTRICAL_WIRING = cur.getString(cur.getColumnIndex(FORM_ISM_ELECTRICAL_WIRING));
						formData.FORM_ISM_HARD_HAT_AND_SHOE = cur.getString(cur.getColumnIndex(FORM_ISM_HARD_HAT_AND_SHOE));
						formData.FORM_ISM_FIRE_SAFETY_DEVICES = cur.getString(cur.getColumnIndex(FORM_ISM_FIRE_SAFETY_DEVICES));
						formData.FORM_ILW_LABOURCAMP_SANITATION_WATER_SUPPLY = cur.getString(cur
								.getColumnIndex(FORM_ILW_LABOURCAMP_SANITATION_WATER_SUPPLY));
						formData.FORM_ILW_LABOURCAMP_STRUCTURE_SATISFACTORY = cur.getString(cur
								.getColumnIndex(FORM_ILW_LABOURCAMP_STRUCTURE_SATISFACTORY));
						formData.FORM_ILW_LPG_FOR_LABOUR = cur.getString(cur.getColumnIndex(FORM_ILW_LPG_FOR_LABOUR));
						formData.FORM_ILW_SEPARATE_TOILETS = cur.getString(cur.getColumnIndex(FORM_ILW_SEPARATE_TOILETS));
						formData.FORM_ILW_DRINKING_WATER_FOR_WORKER = cur.getString(cur.getColumnIndex(FORM_ILW_DRINKING_WATER_FOR_WORKER));
						formData.FORM_ID_MPCS_INFORMATION_BOARD = cur.getString(cur.getColumnIndex(FORM_ID_MPCS_INFORMATION_BOARD));
						formData.FORM_DATE_OF_REPORT = cur.getString(cur.getColumnIndex(FORM_DATE_OF_REPORT));
						formData.FORM_IMAGE_1 = cur.getString(cur.getColumnIndex(FORM_IMAGE_1));
						formData.FORM_IMAGE_2 = cur.getString(cur.getColumnIndex(FORM_IMAGE_2));
						formData.FORM_IMAGE_3 = cur.getString(cur.getColumnIndex(FORM_IMAGE_3));
						formData.FORM_ID = cur.getString(cur.getColumnIndex(_ID));
						formList.add(formData);
					} while (cur.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return formList;
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

	public boolean deleteFormRow(Context context, String _ID) {
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getWritableDatabase();
		return mdb.delete(FORM_DATA_TABLE, _ID + "= ?", new String[] { String.valueOf(_ID) }) > 0;
	}
}
