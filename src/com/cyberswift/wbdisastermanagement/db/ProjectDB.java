package com.cyberswift.wbdisastermanagement.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cyberswift.wbdisastermanagement.model.Project;

public class ProjectDB implements DBConstants {
	private static ProjectDB obj = null;

	public synchronized static ProjectDB obj() {

		if (obj == null)
			obj = new ProjectDB();
		return obj;

	}

	/**
	 * Inserts the Projects data in the Off-line Database table.
	 * 
	 * @param context
	 *            --> {@link Context}
	 * @param cv
	 *            --> {@link ContentValues}
	 * @return Boolean
	 * */
	public Boolean saveMPCSProjects(Context context, ContentValues cv) {

		System.out.println(" ----------  ADD MPCS PROJECT DATA IN MPCS PROJECT  TABLE  --------- ");
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.insert(PROJECT_TABLE, null, cv);
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
	public ArrayList<Project> getMPCSProjects(Context context) {

		ArrayList<Project> projectList = new ArrayList<Project>();
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getReadableDatabase();
		Cursor cur = mdb.query(PROJECT_TABLE, null, null, null, null, null, null);

		if (!isDatabaseEmpty(cur)) {

			// When there exists projects data
			try {

				if (cur.moveToFirst()) {
					do {
						Project project = new Project();
						Log.i("ProjectDB", ""+cur.getString(cur.getColumnIndex(PROJECT_ID)));
						Log.i("ProjectDB", ""+cur.getString(cur.getColumnIndex(PROJECT_TYPE)));
						project.projectId = cur.getString(cur.getColumnIndex(PROJECT_ID));
						project.projectType = cur.getString(cur.getColumnIndex(PROJECT_TYPE));

						projectList.add(project);
					} while (cur.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return projectList;
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

}
