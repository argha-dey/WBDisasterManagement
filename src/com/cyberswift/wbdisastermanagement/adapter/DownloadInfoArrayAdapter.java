package com.cyberswift.wbdisastermanagement.adapter;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberswift.wbdisastermanagement.R;
import com.cyberswift.wbdisastermanagement.async.FileDownloadTask;
import com.cyberswift.wbdisastermanagement.model.DownloadInfo;
import com.cyberswift.wbdisastermanagement.model.DownloadInfo.DownloadState;

public class DownloadInfoArrayAdapter extends ArrayAdapter<DownloadInfo> {
	// Simple class to make it so that we don't have to call findViewById frequently
	private static class ViewHolder {
		TextView textView;
		ProgressBar progressBar;
		ImageButton button;

		DownloadInfo info;
	}

	private static final String TAG = DownloadInfoArrayAdapter.class.getSimpleName();
	private Context mContext;

	public DownloadInfoArrayAdapter(Context context, int textViewResourceId, List<DownloadInfo> objects) {
		super(context, textViewResourceId, objects);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		final DownloadInfo info = getItem(position);
		// We need to set the convertView's progressBar to null.

		ViewHolder holder = null;

		if (null == row) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.file_download_row, parent, false);

			holder = new ViewHolder();
			holder.textView = (TextView) row.findViewById(R.id.downloadFileName);
			holder.progressBar = (ProgressBar) row.findViewById(R.id.downloadProgressBar);
			holder.button = (ImageButton) row.findViewById(R.id.downloadButton);
			holder.info = info;

			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();

			holder.info.setProgressBar(null);
			holder.info = info;
			holder.info.setProgressBar(holder.progressBar);
		}

		holder.textView.setText("Project Name: "+info.getFilename());
		holder.progressBar.setProgress(info.getProgress());
		holder.progressBar.setMax(info.getFileSize());
		info.setProgressBar(holder.progressBar);

		holder.button.setEnabled(info.getDownloadState() == DownloadState.NOT_STARTED);
		final ImageButton button = holder.button;
		holder.button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Toast.makeText(mContext, "click	", Toast.LENGTH_SHORT).show();
				info.setDownloadState(DownloadState.QUEUED);
				button.setEnabled(false);
				button.invalidate();
				FileDownloadTask task = new FileDownloadTask(info);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});

		//TODO: When reusing a view, invalidate the current progressBar.

		return row;
	}

}
