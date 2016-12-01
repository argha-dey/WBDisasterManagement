package com.cyberswift.wbdisastermanagement.connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BandWidthQuality extends Service {

	private String mURL = "http://connectionclass.parseapp.com/m100_hubble_4060.jpg";
	private static final String TAG = "BandWidthQuality";
	private ConnectionClassManager mConnectionClassManager;
	private DeviceBandwidthSampler mDeviceBandwidthSampler;
	private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;
	private ConnectionChangedListener mListener;
	private int mTries = 0;
	public final static String MY_ACTION = "MY_ACTION";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mConnectionClassManager = ConnectionClassManager.getInstance();
		mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
		mListener = new ConnectionChangedListener();
		mConnectionClassManager.register(mListener);
		Toast.makeText(getApplicationContext(), "onCreate called", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//		Toast.makeText(SpeedTestService.this, "onStartCommand called", Toast.LENGTH_SHORT).show();

		new DownloadImage().execute(mURL);

		return START_NOT_STICKY;
	}

	/**
	 * AsyncTask for handling downloading and making calls to the timer.
	 */
	private class DownloadImage extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			System.out.println("On pre execute.");
		}

		@Override
		protected Void doInBackground(String... url) {

			System.out.println("do in Background");
			Log.d("do in background: ", "" + url[0]);

			String imageURL = url[0];
			try {
				// Open a stream to download the image from our URL.
				Log.e("Tag", "Open a stream to download the image from our URL.");
				URLConnection connection = new URL(imageURL).openConnection();
				connection.setUseCaches(false);
				connection.connect();
				InputStream input = connection.getInputStream();
				try {
					byte[] buffer = new byte[1024];
					System.out.println(" Do some busy waiting while the stream is open.");
					// Do some busy waiting while the stream is open.
					while (input.read(buffer) != -1) {
					}
				} finally {
					input.close();
					Log.v("Tag", "Input closed");
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Error while downloading image.");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			System.out.println("On post exectue");
			System.out.println("Connection Class" + mConnectionClass.toString());
			mDeviceBandwidthSampler.stopSampling();
			// Retry for up to 10 times until we find a ConnectionClass.
			if (mConnectionClass == ConnectionQuality.UNKNOWN && mTries < 10) {
				mTries++;
				new DownloadImage().execute(mURL);
			}
			/*if (!mDeviceBandwidthSampler.isSampling()) {
				//mRunningBar.setVisibility(View.GONE);
			}*/
		}
	}

	@Override
	public void onDestroy() {
		Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show();
		mConnectionClassManager.remove(mListener);
		stopSelf();
	}

	/**
	 * Listener to update the UI upon connection class change.
	 */
	private class ConnectionChangedListener implements ConnectionClassManager.ConnectionClassStateChangeListener {

		@Override
		public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
			mConnectionClass = bandwidthState;
			System.out.println("------------------>> Banwidth changed <<-------------------------------");
			/*((Activity) getApplicationContext()).runOnUiThread(new Runnable() {
				@Override
				public void run() {

					//Keep checking here and return broadcast

					System.out.println("------------->><<-----------------");
					System.out.println("" + mConnectionClass.toString());
					//	sendingBroadcast(mConnectionClass.toString(), "Strong Connection");
					//mTextView.setText(mConnectionClass.toString());
				}
			});*/
		}
	}

	private void sendingBroadcast(String networkStatus, String MSG) {
		Intent intent = new Intent();
		intent.setAction(MY_ACTION);

		intent.putExtra("NETWORK_CHECK", networkStatus);
		intent.putExtra("MSG", MSG);
		sendBroadcast(intent);

		stopSelf();
	}
}
