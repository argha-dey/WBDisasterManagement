package com.cyberswift.wbdisastermanagement.location;

import java.text.DateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class CustomLocationService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = "CustomLocationService";

	private final long INTERVAL = 1000 * 30 * 1; // 30 seconds
	private final long FASTEST_INTERVAL = 1000 * 10 * 1; // 10 seconds

	private static Context mContext;
	private LocationRequest locationRequest;
	private GoogleApiClient googleApiClient;
	private FusedLocationProviderApi fusedLocationProviderApi;
	private Location mCurrentLocation;
	private AlertDialog systemAlertDialog;
	public double latitude; // latitude
	public double longitude; // longitude

	public final static String MY_ACTION = "MY_ACTION";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public CustomLocationService(Context mContext) {
		CustomLocationService.mContext = mContext;

	}

	public CustomLocationService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		checkConnection();
		return START_STICKY;

	}

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		mContext = this;

	}

	private void checkConnection() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.e(TAG, "NO LOCATION FOUND!");
			if (systemAlertDialog == null) {
				showSettingsAlert();
			} else if (!systemAlertDialog.isShowing()) {
				showSettingsAlert();
			}
		} else {
			Log.v("GPS Connection Found:", "true");
			if (mCurrentLocation == null) {
				getLocation();
			}
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy Service");
		super.onDestroy();
		try {
			if (googleApiClient != null) {
				googleApiClient.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		stopSelf();
	}

	public void getLocation() {
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);
		fusedLocationProviderApi = LocationServices.FusedLocationApi;
		googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
				.build();
		if (googleApiClient != null) {
			googleApiClient.connect();
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.v("onConnected", "Entering here.");
		try {
			fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
		} catch (Exception e) {
			Log.e(TAG, "-----------------Exception at on conected-------------------");
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.v("onConnectionSuspended", "Entering here.");

	}

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		DateFormat.getTimeInstance().format(new Date());
		long atTime = mCurrentLocation.getTime();
		DateFormat.getTimeInstance().format(new Date(atTime));
		Intent intent = new Intent();
		intent.setAction(MY_ACTION);
		intent.putExtra("LATITUDE", location.getLatitude());
		intent.putExtra("LONGITUDE", location.getLongitude());
		if (mCurrentLocation != null) {
			sendBroadcast(intent);
			onDestroy();

		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(mContext, "My Location :" + "onConnectionFailed", Toast.LENGTH_SHORT).show();
		Log.e(TAG, "ON CONNECTION FAILED!");
		showSettingsAlert();

	}

	public void showSettingsAlert() {

		Toast.makeText(getApplicationContext(), "Please make sure that your GPS is enabled or SIM Service is working.", Toast.LENGTH_LONG).show();

	}

}