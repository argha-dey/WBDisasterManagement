package com.cyberswift.wbdisastermanagement;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cyberswift.wbdisastermanagement.connection.CheckNetworkQuality;
import com.cyberswift.wbdisastermanagement.connection.NetworkListener;

public class Main extends Activity implements NetworkListener {

	CheckNetworkQuality mConnectionStateActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		//Log.v(null, null);

	}

	public void onTestClick(View v) {

		mConnectionStateActivity = new CheckNetworkQuality(Main.this);

		mConnectionStateActivity.registerListener();

		mConnectionStateActivity.checkConnectionSpeed();

		mConnectionStateActivity.networkListener = this;

	}

	@Override
	public void getConnectionType(boolean isStrongConnection) {

		if (isStrongConnection)
			Toast.makeText(Main.this, "Strong Connection", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(Main.this, "Poor Connection", Toast.LENGTH_SHORT).show();
	}

}
