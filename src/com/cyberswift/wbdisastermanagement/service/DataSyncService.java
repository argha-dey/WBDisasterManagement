package com.cyberswift.wbdisastermanagement.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.cyberswift.wbdisastermanagement.model.OfflineDataSet;
import com.cyberswift.wbdisastermanagement.util.ServerResponseCallback;
import com.cyberswift.wbdisastermanagement.util.Util;

public class DataSyncService extends Service implements ServerResponseCallback {

	private boolean isDataPostingService = false, isImageService = false;
	private static int serviceListPosition = 0;

	private VolleyTaskManager volleyTaskManager;

	public final static String DATASYNC_ACTION = "DATASYNC_ACTION";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		volleyTaskManager = new VolleyTaskManager(DataSyncService.this);
		//		Toast.makeText(DataSyncService.this, "onCreate called", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//		Toast.makeText(DataSyncService.this, "onStartCommand called", Toast.LENGTH_SHORT).show();

		checkDataForSyncing();

		return START_NOT_STICKY;
	}

	private void checkDataForSyncing() {

		Log.d("checkDataForSyncing", "*************************************************\n");
		if (Util.fetchOfflineDataSetList(DataSyncService.this) != null && Util.fetchOfflineDataSetList(DataSyncService.this).size() > 0) {
			if (Util.fetchOfflineDataSetList(DataSyncService.this).get(serviceListPosition).getIsDataPostedToserver()
					&& Util.fetchOfflineDataSetList(DataSyncService.this).get(serviceListPosition).getIsAllImagesPostedToserver()) {

				ArrayList<OfflineDataSet> tempOfflineDataList = Util.fetchOfflineDataSetList(DataSyncService.this);
				tempOfflineDataList.remove(serviceListPosition);

				Util.saveOfflineDataSetList(DataSyncService.this, tempOfflineDataList);
			}

			if (Util.fetchOfflineDataSetList(DataSyncService.this).size() == 0) {
				//				Toast.makeText(DataSyncService.this, "All offline data has been synced!", Toast.LENGTH_LONG).show();

				Intent intent = new Intent();
				intent.setAction(DATASYNC_ACTION);

				intent.putExtra("DATA_SYNCED", true);
				intent.putExtra("MSG", "All offline data has been synced!");

				sendBroadcast(intent);
				stopSelf();
			} else {
				OfflineDataSet offlineDataSet = Util.fetchOfflineDataSetList(DataSyncService.this).get(serviceListPosition);

				if (!offlineDataSet.getIsDataPostedToserver()) {
					isDataPostingService = true;
					volleyTaskManager.doInsertUpdateMPCSProjectsPhysicalProgress(offlineDataSet.getParamsMap(), false);
				} else {
					Log.v("Id", "" + offlineDataSet.getReturnId());
					Log.v("Image Position", "" + offlineDataSet.getImagePosition());
					HashMap<String, String> paramsMap = new HashMap<String, String>();
					paramsMap.put("Mode", "ad");
					paramsMap.put("Id", offlineDataSet.getReturnId());
					paramsMap.put("ImageNo", "" + (offlineDataSet.getImagePosition() + 1));
					paramsMap.put("ImageData", offlineDataSet.getImagesList().get(offlineDataSet.getImagePosition()).getBase64value());

					isImageService = true;
					volleyTaskManager.doPostImage(paramsMap, false);
				}

			}
		} else {
			Intent intent = new Intent();
			intent.setAction(DATASYNC_ACTION);

			intent.putExtra("DATA_SYNCED", true);
			intent.putExtra("MSG", "No data to sync!");

			sendBroadcast(intent);
			stopSelf();
			//			Toast.makeText(DataSyncService.this, "No data to sync!", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {
		// --> TODO After successful webservice calling
		Log.v("resultJsonObject", resultJsonObject.toString());
		ArrayList<OfflineDataSet> tempOfflineDataList = Util.fetchOfflineDataSetList(DataSyncService.this);

		if (isDataPostingService) {
			isDataPostingService = false;

			String id = resultJsonObject.optString("ID");
			String result = resultJsonObject.optString("RES");
			if (!result.trim().equalsIgnoreCase("1")) {
				Toast.makeText(DataSyncService.this, resultJsonObject.optString("MESSAGE"), Toast.LENGTH_LONG).show();
			} else {
				//				successfulPostMsg = fDSavePittingDataResultJsonObject.optString("Message");
				tempOfflineDataList.get(serviceListPosition).setReturnId(id);
				tempOfflineDataList.get(serviceListPosition).setIsDataPostedToserver(true);

				Log.i("TempOfflineDataSet", "Image List Size: " + tempOfflineDataList.get(serviceListPosition).getImagesList().size());

				if (tempOfflineDataList.get(serviceListPosition).getImagesList().size() > 0) {
					tempOfflineDataList.get(serviceListPosition).setImagePosition(0);
				} else {
					tempOfflineDataList.get(serviceListPosition).setIsAllImagesPostedToserver(true);
				}

				Util.saveOfflineDataSetList(DataSyncService.this, tempOfflineDataList);
				Log.v("OnSuccess", ">>----------------------------<<\nCheckingDaraSyncing");
				checkDataForSyncing();
			}

		} else if (isImageService) {
			isImageService = false;

			Log.d("Sent position:", tempOfflineDataList.get(serviceListPosition).getImagePosition() + "");

			String FDSaveSchoolImageDataResult = resultJsonObject.optString("FDSaveSchoolImageDataResult");
			if (FDSaveSchoolImageDataResult.equalsIgnoreCase("1")) {
				tempOfflineDataList.get(serviceListPosition).getImagesList()
						.get(tempOfflineDataList.get(serviceListPosition).getImagePosition()).setIsSelected(true);
			}

			if (tempOfflineDataList.get(serviceListPosition).getImagePosition() < tempOfflineDataList.get(serviceListPosition)
					.getImagesList().size() - 1) {

				tempOfflineDataList.get(serviceListPosition).setImagePosition(
						tempOfflineDataList.get(serviceListPosition).getImagePosition() + 1);
				//				sendingImages(imagePosition + 1);
			} else {
				Log.e("Image Sending", "All Completed");

				tempOfflineDataList.get(serviceListPosition).setIsAllImagesPostedToserver(true);
			}

			Util.saveOfflineDataSetList(DataSyncService.this, tempOfflineDataList);
			checkDataForSyncing();

		}

	}

	@Override
	public void onError() {
		Intent intent = new Intent();
		intent.setAction(DATASYNC_ACTION);

		intent.putExtra("DATA_SYNCED", false);
		intent.putExtra("MSG", "Error occured!");

		sendBroadcast(intent);
		stopSelf();
	}

	@Override
	public void onSuccess(String resultJsonObject) {
		// TODO Auto-generated method stub

	}

}
