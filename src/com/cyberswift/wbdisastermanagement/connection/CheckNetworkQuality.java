package com.cyberswift.wbdisastermanagement.connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class CheckNetworkQuality {

	private static final String TAG = "ConnectionClass-Sample";

	private ConnectionClassManager mConnectionClassManager;
	private DeviceBandwidthSampler mDeviceBandwidthSampler;
	private ConnectionChangedListener mListener;

	private String mURL = "http://connectionclass.parseapp.com/m100_hubble_4060.jpg";
	private int mTries = 0;
	private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;
	private Context mContext;
	public NetworkListener networkListener;
	DownloadImage async;

	public CheckNetworkQuality(Context mContext) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		async = new DownloadImage();
		mConnectionClassManager = ConnectionClassManager.getInstance();
		mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
		//mTextView.setText(mConnectionClassManager.getCurrentBandwidthQuality().toString());
		mListener = new ConnectionChangedListener();
	}

	public void registerListener() {
		mConnectionClassManager.register(mListener);
	}

	public void removeListener() {
		mConnectionClassManager.remove(mListener);
	}

	public void checkConnectionSpeed() {
		async.execute(mURL);
	}

	/**
	 * Listener to update the UI upon connectionclass change.
	 */
	private class ConnectionChangedListener implements ConnectionClassManager.ConnectionClassStateChangeListener {

		@Override
		public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
			mConnectionClass = bandwidthState;
			/*runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mTextView.setText(mConnectionClass.toString());
				}
			});*/

			System.out.println("----Connection Type: --- \n" + mConnectionClass.toString());
			if (!(mConnectionClass == ConnectionQuality.UNKNOWN)) {
				
				mDeviceBandwidthSampler.stopSampling();
				if (mConnectionClass == ConnectionQuality.POOR)
					networkListener.getConnectionType(false);
				else
					networkListener.getConnectionType(true);
				async.cancel(true);
				
			}
		}
	}

	/**
	 * AsyncTask for handling downloading and making calls to the timer.
	 */
	private class DownloadImage extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			mDeviceBandwidthSampler.startSampling();
		}

		@Override
		protected Void doInBackground(String... url) {
			String imageURL = url[0];
			try {
				// Open a stream to download the image from our URL.
				URLConnection connection = new URL(imageURL).openConnection();
				connection.setUseCaches(false);
				connection.connect();
				InputStream input = connection.getInputStream();
				try {
					byte[] buffer = new byte[1024];

					// Do some busy waiting while the stream is open.
					while (input.read(buffer) != -1) {
					}
				} finally {
					input.close();
				}
			} catch (IOException e) {
				Log.e(TAG, "Error while downloading image.");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			System.out.println("On Post Execute");
			mDeviceBandwidthSampler.stopSampling();

			// Retry for up to 10 times until we find a ConnectionClass.
			if (mConnectionClass == ConnectionQuality.UNKNOWN) {
				//async.execute(mURL);
			}
			if (!mDeviceBandwidthSampler.isSampling()) {

				System.out.println("-----Complete---");
				System.out.println("Connection type Result: " + mConnectionClass);
				//mRunningBar.setVisibility(View.GONE);

			}
		}
	}
}
