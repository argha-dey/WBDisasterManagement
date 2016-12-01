package com.cyberswift.wbdisastermanagement.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cyberswift.wbdisastermanagement.model.Block;

public class BlockDB implements DBConstants {

	private static BlockDB obj = null;

	public synchronized static BlockDB obj() {

		if (obj == null)
			obj = new BlockDB();
		return obj;

	}

	public Boolean saveBlockData(Context context, ContentValues cv) {

		System.out.println(" ----------  ADD BLOCKS INTO BLOCK TABLE  --------- ");
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.insert(BLOCK_TABLE, null, cv);
			mdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			mdb.endTransaction();
			return true;
		}

	}

	public ArrayList<Block> getBlocks(Context context, String districtId, String projectType) {

		ArrayList<Block> blocksArray = new ArrayList<Block>();
		String[] columns = { _ID, BLOCK_ID, BLOCK_NAME, BLOCK_DISTRICT_ID };

		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(context).getReadableDatabase();
		Cursor cur = mdb.query(BLOCK_TABLE, columns, BLOCK_DISTRICT_ID + "=?" + "AND " + BLOCK_PROJ_TYPE + "=?", new String[] { districtId,
				projectType }, null, null, null);

		if (!isDatabaseEmpty(cur)) {
			try {
				if (cur.moveToFirst()) {
					do {
						Block image = new Block();

						image.BLOCK_ID = cur.getString(cur.getColumnIndex(BLOCK_ID));
						image.BLOCK_NAME = cur.getString(cur.getColumnIndex(BLOCK_NAME));
						image.DIST_ID = cur.getString(cur.getColumnIndex(BLOCK_DISTRICT_ID));

						blocksArray.add(image);
					} while (cur.moveToNext());
				}
				cur.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return blocksArray;
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

	public void clearDBTables(Context mcContext) {

		System.out.println(" ----------  CLEAR BLOCK TABLES  --------- ");
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(mcContext).getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.delete(BLOCK_TABLE, null, null);
			mdb.delete(MPCS_PROJECT_TABLE, null, null);
			mdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mdb.endTransaction();
		}
	}
}
