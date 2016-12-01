package com.cyberswift.wbdisastermanagement.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class FetchCordinates extends AsyncTask<String, Integer, String> {

	private ProgressDialog progDailog = null;

	public double lati = 0.0;
	public double longi = 0.0;
	public Location mLocation = new Location("0");

	public LocationManager mLocationManager;

	public GPSLocationListener gpsLocationListener;

	private Context mContext;
	public LocationCallbackListener mListener;

	public FetchCordinates(Context mContext) {

		this.mContext = mContext;
	}

	@Override
	protected void onPreExecute() {
		gpsLocationListener = new GPSLocationListener();
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);

		progDailog = new ProgressDialog(mContext);

		progDailog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				FetchCordinates.this.cancel(true);
			}
		});

		progDailog.setMessage("Fetching your Location! Please wait...");
		progDailog.setIndeterminate(true);
		progDailog.setCancelable(false);
		progDailog.show();

	}

	@Override
	protected void onCancelled() {

		System.out.println("Cancelled by user!");
		progDailog.dismiss();
		mLocationManager.removeUpdates(gpsLocationListener);
	}

	@Override
	protected void onPostExecute(String result) {

		progDailog.dismiss();
		mListener.getLocation(this.mLocation);
		//Toast.makeText(mContext, "Latitude :" + lati + "\nLongitude :" + longi, Toast.LENGTH_LONG).show();
	}

	@Override
	protected String doInBackground(String... params) {

		while (this.mLocation.getLatitude() == 0.0) {

		}
		return null;
	}

	public class GPSLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			int lat = (int) location.getLatitude(); // * 1E6);
			int log = (int) location.getLongitude(); // * 1E6);
			int acc = (int) (location.getAccuracy());

			String info = location.getProvider();
			try {

				lati = location.getLatitude();
				longi = location.getLongitude();
				mLocation = location;

			} catch (Exception e) {
				// progDailog.dismiss();
				// Toast.makeText(getApplicationContext(),"Unable to get Location", Toast.LENGTH_LONG).show();
			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("OnProviderDisabled", "OnProviderDisabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("onProviderEnabled", "onProviderEnabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i("onStatusChanged", "onStatusChanged");

		}

	}

}
