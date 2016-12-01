package com.cyberswift.wbdisastermanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.cyberswift.wbdisastermanagement.util.Util;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		//Log.v(null, null);

		/*
		 * // Log.v(null, null); new Handler().postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { startActivity(new
		 * Intent(SplashActivity.this, LoginActivity.class)); finish(); } },
		 * 3000);
		 */
		new SplashTimerTask().execute();

	}

	private class SplashTimerTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(2345);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (Util.fetchUserClass(SplashActivity.this) == null) {

				openLoginActivity();

			} else {

				Log.e("User logged in:", Util.fetchUserClass(SplashActivity.this).getIsLoggedin() + "");
				
				if (Util.fetchUserClass(SplashActivity.this).getIsLoggedin()) {
					long currentDate = Util.getDateInMilliSeconds(Util.getCurrentFormattedDate());
					long previousLoggedInDate = Util.getDateInMilliSeconds(Util.fetchUserClass(SplashActivity.this)
							.getLoggedInDateTime());
					Log.i("Current Date", "" + currentDate);
					Log.i("Previous Date", "" + previousLoggedInDate);
					
					if (currentDate > previousLoggedInDate) {
						
						openLoginActivity();
					} else {
						
						openFormActivity();
					}
				} else {
					openLoginActivity();
				}
			}

		}

	}

	public void openLoginActivity() {

		Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();

	}

	private void openFormActivity() {

		Intent intent = new Intent(SplashActivity.this, NewFormActivity.class);
		startActivity(intent);
		finish();
	}

}
