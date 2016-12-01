package com.cyberswift.wbdisastermanagement.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationBroadcastReceiver extends BroadcastReceiver {

	private double latitude = 0.00;
	private double longitude = 0.00;
	@SuppressWarnings("unused")
	private String Tag;

	public LocationBroadcastReceiver(String Tag) {
		this.Tag = Tag;
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		latitude = arg1.getDoubleExtra("LATITUDE", 0.00);
		longitude = arg1.getDoubleExtra("LONGITUDE", 0.00);
	}

	public double getLatitude() {
		return latitude;

	}

	public double getLongitude() {
		return longitude;

	}

	/** Reinitialize Location Variables **/
	public void resetLatitudeLongitude() {
		latitude = 0.00;
		longitude = 0.00;
	}
}