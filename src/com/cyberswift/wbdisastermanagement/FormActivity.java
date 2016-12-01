package com.cyberswift.wbdisastermanagement;
/*package com.cyberswift.wbdisastermanagement;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberswift.wbdisastermanagement.adapter.DropDownViewForXML;
import com.cyberswift.wbdisastermanagement.adapter.ImageAdapter;
import com.cyberswift.wbdisastermanagement.db.BlockDB;
import com.cyberswift.wbdisastermanagement.db.DBConstants;
import com.cyberswift.wbdisastermanagement.db.DistrictDB;
import com.cyberswift.wbdisastermanagement.db.OngoingProjectsDB;
import com.cyberswift.wbdisastermanagement.db.StageDB;
import com.cyberswift.wbdisastermanagement.model.Block;
import com.cyberswift.wbdisastermanagement.model.District;
import com.cyberswift.wbdisastermanagement.model.ImageClass;
import com.cyberswift.wbdisastermanagement.model.OngoingProjects;
import com.cyberswift.wbdisastermanagement.model.Stages;
import com.cyberswift.wbdisastermanagement.service.VolleyTaskManager;
import com.cyberswift.wbdisastermanagement.util.AlertDialogCallBack;
import com.cyberswift.wbdisastermanagement.util.ServerResponseCallback;
import com.cyberswift.wbdisastermanagement.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LandingPageActivity extends Activity implements ServerResponseCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private ProgressDialog mProgressDialog;
	private VolleyTaskManager volleyTaskManager;
	private DropDownViewForXML dropDown_district, dropDown_block, dropDown_ongoingProjects, dropDown_stages;
	private ArrayList<District> districtList = new ArrayList<District>();
	private ArrayList<Block> blockList = new ArrayList<Block>();
	private ArrayList<OngoingProjects> onGoingProjectList = new ArrayList<OngoingProjects>();
	private ArrayList<Stages> stageList = new ArrayList<Stages>();
	private boolean isOnGoingProjectService = false;
	private boolean isGetStagesService = false;
	private boolean isImageService = false;
	private ArrayList<ImageClass> imagesList = new ArrayList<ImageClass>();
	private static final int DATE_DIALOG_ID = 316, PICTURE_GALLERY_REQUEST = 2572, CAMERA_PIC_REQUEST = 1337;

	// TODO remember to reset before closing the activity
	private Uri mCapturedImageURI;

	private ViewPager vp_selectedImages;
	private TextView tv_imageProgress;
	private View v_swipeLeft, v_swipeRight;
	private String districtId = "", blockId = "", projectId = "", stageId = "";

	// ============LOCATION

	private Location mCurrentLocation;
	private LocationRequest mLocationRequest;
	private AlertDialog systemAlertDialog;
	private FusedLocationProviderApi fusedLocationProviderApi;
	private GoogleApiClient mGoogleApiClient;

	private TextView tv_latitude, tv_longitude;

	private boolean isSubmitService = false;

	private EditText etComments;

	private Context mContext;
	private ArrayList<ImageClass> allImagesList = new ArrayList<ImageClass>();
	private int imagePosition;
	private String ImageID = "";
	private PopupWindow popupWindow;
	private static final int MY_PERMISSIONS_REQUEST_CAMERA = 34373;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_landing_layout);

		mContext = LandingPageActivity.this;
		initView();

	}

	private void initView() {

		vp_selectedImages = (ViewPager) findViewById(R.id.vp_selectedImages);
		tv_imageProgress = (TextView) findViewById(R.id.tv_imageProgress);
		v_swipeLeft = (View) findViewById(R.id.v_swipeLeft);
		v_swipeRight = (View) findViewById(R.id.v_swipeRight);

		dropDown_district = (DropDownViewForXML) findViewById(R.id.dropDown_district);
		dropDown_block = (DropDownViewForXML) findViewById(R.id.dropDown_block);
		dropDown_ongoingProjects = (DropDownViewForXML) findViewById(R.id.dropDown_ongoingProjects);
		dropDown_stages = (DropDownViewForXML) findViewById(R.id.dropDown_stages);

		etComments = (EditText) findViewById(R.id.etComments);

		volleyTaskManager = new VolleyTaskManager(LandingPageActivity.this);

		dropDown_district.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String districtID = districtList.get(position).DIS_ID;
				districtId = districtID;
				populateBlockDropdown(districtID);
				dropDown_ongoingProjects.setText("");
				dropDown_ongoingProjects.setHint("");
				dropDown_stages.setText("");
				dropDown_ongoingProjects.setEnabled(false);
			}

		});

		dropDown_block.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String blockID = blockList.get(position).BLOCK_ID;

				blockId = blockID;
				populateOngoingProjectsDropdown(blockID);
			}

		});

		dropDown_ongoingProjects.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				projectId = onGoingProjectList.get(position).ID;

			}

		});
		dropDown_stages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				stageId = stageList.get(position).ID;

			}

		});
		fetchDistricts();
		// ============LOCATION

		tv_latitude = (TextView) findViewById(R.id.tv_latitude);
		tv_longitude = (TextView) findViewById(R.id.tv_longitude);

		mProgressDialog = new ProgressDialog(LandingPageActivity.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);

	}

	private void fetchDistricts() {

		HashMap<String, String> paramsMap = new HashMap<String, String>();

		volleyTaskManager.doPostFetchDistrictList(paramsMap);

	}

	private void getOnGoingProjects() {
		HashMap<String, String> paramsMap = new HashMap<String, String>();

		volleyTaskManager.doPostFetchOngoingProjects(paramsMap);

	}

	private void getStages() {
		HashMap<String, String> paramsMap = new HashMap<String, String>();

		volleyTaskManager.doPostFetchStages(paramsMap);

	}

	public void onOptionsClicked(View v) {

		showpopup(v);

	}

	public void onSubmitClick(View v) {

		if (tv_latitude.getText().toString().trim().isEmpty()) {

			Util.showMessageWithOk(LandingPageActivity.this, "Please supply your location.");

		} else if (!Util.checkConnectivity(mContext)) {

			Util.showMessageWithOk(LandingPageActivity.this, "No internet connection.");
		} else if (dropDown_district.getText().toString().trim().isEmpty() || dropDown_block.getText().toString().trim().isEmpty()
				|| dropDown_ongoingProjects.getText().toString().trim().isEmpty() || dropDown_stages.getText().toString().trim().isEmpty()) {
			Util.showMessageWithOk(LandingPageActivity.this, "Please supply all fields.");

		} else {

			HashMap<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("DISTRICT_ID", districtId);
			paramsMap.put("BLOCK_ID", blockId);
			paramsMap.put("PROJECT_ID", projectId);
			paramsMap.put("STAGE_ID", stageId);
			paramsMap.put("COMMENT", etComments.getText().toString().trim());
			paramsMap.put("LAT", tv_latitude.getText().toString().trim());
			paramsMap.put("LON", tv_longitude.getText().toString().trim());

			isSubmitService = true;
			volleyTaskManager.doPostProjectSurveySaveData(paramsMap);

			allImagesList.addAll(imagesList);
		}

	}

	private void populateDistrictDropdown() {
		dropDown_district.setText("");
		dropDown_district.setHint("");

		if (districtList.size() > 0) {

			dropDown_district.setEnabled(true);
			dropDown_district.setHint(" Select ");
			String[] array = new String[districtList.size()];
			int index = 0;
			for (District value : districtList) {
				array[index] = (String) value.DIS_NAME;
				index++;
			}
			dropDown_district.setItems(array);

		} else {
			dropDown_district.setEnabled(false);
		}

		isOnGoingProjectService = true;
		getOnGoingProjects();
	}

	private void populateStageDropdown() {

		dropDown_stages.setText("");
		dropDown_stages.setHint("");

		if (stageList.size() > 0) {

			dropDown_stages.setEnabled(true);
			dropDown_stages.setHint(" Select ");
			String[] array = new String[stageList.size()];
			int index = 0;
			for (Stages value : stageList) {
				array[index] = (String) value.STAGE;
				index++;
			}
			dropDown_stages.setItems(array);

		} else {
			dropDown_stages.setEnabled(false);
		}

	}

	private void populateBlockDropdown(String districtId) {
		// Fetch Blocks with respect to District ID
		blockList = new BlockDB().getBlocks(LandingPageActivity.this, districtId);
		dropDown_block.setText("");
		dropDown_block.setHint("");

		if (blockList.size() > 0) {

			dropDown_block.setEnabled(true);
			dropDown_block.setHint(" Select ");
			String[] array = new String[blockList.size()];
			int index = 0;
			for (Block value : blockList) {
				array[index] = (String) value.BLOCK_NAME;
				index++;
			}
			dropDown_block.setItems(array);

		} else {
			dropDown_block.setEnabled(false);
		}

	}

	private void populateOngoingProjectsDropdown(String blockId) {
		// Fetch Blocks with respect to District ID
		onGoingProjectList = new OngoingProjectsDB().getOnGoingProjects(LandingPageActivity.this, blockId);
		dropDown_ongoingProjects.setText("");
		dropDown_ongoingProjects.setHint("");

		System.out.println("------------ONGOING----------------");

		System.out.println("----- ONGOING PRoject size: " + onGoingProjectList.size() + "------------------");
		if (onGoingProjectList.size() > 0) {

			System.out.println("------------SIZE GREATER THAN 0----------------");
			dropDown_ongoingProjects.setEnabled(true);
			dropDown_ongoingProjects.setHint(" Select ");
			String[] array = new String[onGoingProjectList.size()];
			int index = 0;
			for (OngoingProjects value : onGoingProjectList) {
				array[index] = (String) value.NAME;
				index++;
			}
			dropDown_ongoingProjects.setItems(array);

		} else {
			dropDown_ongoingProjects.setEnabled(false);
		}

	}

	public void onPictureClick(View v) {
		if (imagesList.size() < 3) {
			final Dialog customDialog = new Dialog(LandingPageActivity.this);

			customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

			LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.image_select_dialog, null);

			Button btn_camera = (Button) view.findViewById(R.id.btn_camera);
			btn_camera.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					customDialog.dismiss();

					// --> For Camera
					mProgressDialog.setMessage("Please wait...");

					// TODO Set setCancelable false
					mProgressDialog.setCancelable(true);
					showProgressDialog();
					cameraSelectedPic();
				}
			});

			Button btn_album = (Button) view.findViewById(R.id.btn_album);
			btn_album.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					customDialog.dismiss();

					// --> For Album
					mProgressDialog.setMessage("Please wait...");
					mProgressDialog.setCancelable(false);
					showProgressDialog();
					populatingSelectedPic();
				}
			});

			Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
			btn_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					customDialog.dismiss();
				}
			});

			customDialog.setCancelable(false);
			customDialog.setContentView(view);
			customDialog.setCanceledOnTouchOutside(false);
			// Start AlertDialog
			customDialog.show();
		} else {

			Util.showMessageWithOk(LandingPageActivity.this, "Maximum number of images has already been selected!");
		}

	}

	private void showProgressDialog() {
		if (!mProgressDialog.isShowing())
			mProgressDialog.show();
	}

	private void hideProgressDialog() {
		if (mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	protected void populatingSelectedPic() {
		Intent albumIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		albumIntent.setType("image/*");
		startActivityForResult(albumIntent, PICTURE_GALLERY_REQUEST);
	}

	protected void cameraSelectedPic() {

		Log.e("cameraSelectedPic", "");
		// Util.checkPreviousPermission(LandingPageActivity.this,
		// MY_PERMISSIONS_REQUEST_CAMERA);

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "Image File name");
		mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		System.out.println("Actio image capture uri: " + mCapturedImageURI.getPath());
		Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
		startActivityForResult(intentPicture, CAMERA_PIC_REQUEST);

	}

	public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {

		hideProgressDialog();

		if (resultCode == RESULT_OK) {

			Log.v("DialogChoosePicture", " Selected Uri: " + mCapturedImageURI.getPath());

			Uri selectedUri = null;

			switch (requestCode) {
			case CAMERA_PIC_REQUEST:
				System.out.println("CAMERA PIC REQUEST");
				selectedUri = mCapturedImageURI;
				break;

			case PICTURE_GALLERY_REQUEST:
				selectedUri = data.getData();
				break;

			}

			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedUri, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);

			cursor.close();

			Log.v("IMAGE PATH:", picturePath);

			processImagePath(picturePath);
		} else {
			Log.w("DialogChoosePicture", "Warning: activity result not ok");

			// Camera Click action
			Toast.makeText(LandingPageActivity.this, "No image selected", Toast.LENGTH_LONG).show();

		}
	}

	private void processImagePath(String picturePath) {
		Bitmap bitmap = null;

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inScaled = true;

		int bitWidth = BitmapFactory.decodeFile(picturePath).getWidth();
		int bitHeight = BitmapFactory.decodeFile(picturePath).getHeight();

		System.out.println("width : " + bitWidth + " bitHeight : " + bitHeight);

		if (bitWidth <= 2048 || bitHeight <= 1536) {
			opt.inSampleSize = 4;
		} else if ((bitHeight > 1536 && bitHeight <= 1944) || (bitWidth > 2048 && bitWidth <= 2592)) {
			opt.inSampleSize = 6;
		} else {
			opt.inSampleSize = 8;
		}

		bitmap = BitmapFactory.decodeFile(picturePath, opt);

		if (bitmap != null) {
			try {
				ExifInterface exif = new ExifInterface(picturePath);
				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

				Log.e("orientation", orientation + "<<<");
				Matrix matrix = new Matrix();

				switch (orientation) {
				case 1:
					Log.v("Case:", "1");
					break;
				case 2:
					Log.v("Case:", "2");
					break;
				case 3:
					Log.v("Case:", "3");
					matrix.postRotate(180);
					break;
				case 4:
					Log.v("Case:", "4");
					break;
				case 5:
					Log.v("Case:", "5");
					break;
				case 6:
					Log.v("Case:", "6");
					matrix.postRotate(90);
					break;
				case 7:
					Log.v("Case:", "7");
					break;
				case 8:
					Log.v("Case:", "8");
					matrix.postRotate(-90);
					break;
				}

				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

			} catch (IOException e) {
				e.printStackTrace();
			}
			ImageClass imageClass = new ImageClass();
			// imageClass.setBitmap(bitmap);
			imageClass.setBase64value(Util.getBase64StringFromBitmap(bitmap)); // <--
																				// TODO
																				// Use
																				// this
																				// bitmap

			imagesList.add(imageClass);

		} else {

			Toast.makeText(this, picturePath + "not found", Toast.LENGTH_LONG).show();
		}

		imageUpdateOnView();
	}

	private void imageUpdateOnView() {

		// ============ Construction of Zonal OH reservoir / Zonal Head Work
		// Site
		ImageAdapter constructionZonalAdapter = new ImageAdapter(this, imagesList);
		vp_selectedImages.setAdapter(constructionZonalAdapter);

		if (imagesList.size() == 0) {
			vp_selectedImages.setBackgroundResource(R.drawable.default_empty);
		} else {
			vp_selectedImages.setBackgroundColor(Color.parseColor("#D7D7D7"));

			vp_selectedImages.setCurrentItem(imagesList.size() - 1);
		}

		if (imagesList.size() <= 1) {
			tv_imageProgress.setText("[Image added " + imagesList.size() + "/3]");
			v_swipeRight.setVisibility(View.INVISIBLE);
			v_swipeLeft.setVisibility(View.INVISIBLE);
		} else {
			tv_imageProgress.setText("Slide to view other images\n[Images added " + imagesList.size() + "/3]");
			v_swipeRight.setVisibility(View.VISIBLE);
			v_swipeLeft.setVisibility(View.VISIBLE);
		}

	}

	public void onLocationClicked(View v) {
		mCurrentLocation = null; // Setting present location null, so that new
		// location value can be fetched on each
		// button click.
		checkingLocation();

	}

	private void checkingLocation() {

		// if (Util.checkConnectivity(EntryFormActivity.this)) {

		if (!isGooglePlayServicesAvailable()) {
			int requestCode = 10;
			int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(LandingPageActivity.this);
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, LandingPageActivity.this, requestCode);
			dialog.show();
		} else {
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
					&& !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				Log.e("EntryFormActivity", "NO LOCATION FOUND!");
				// Util.buildAlertMessageNoGps( StartJourneyActivity.this );
				if (systemAlertDialog == null) {
					systemAlertDialog = Util.showSettingsAlert(getApplicationContext(), systemAlertDialog);
				} else if (!systemAlertDialog.isShowing()) {
					systemAlertDialog = Util.showSettingsAlert(getApplicationContext(), systemAlertDialog);
				}
			} else {
				Log.v("GPS Connection Found:", "true");
				if (mCurrentLocation == null) {
					mProgressDialog.setMessage("Fetching present location...");
					mProgressDialog.setCancelable(true);
					showProgressDialog();
					createLocationRequest();
				} else {
					// Toast.makeText(EntryFormActivity.this,
					// "Location already found.", Toast.LENGTH_SHORT).show();
				}
			}
		}
		// } else {
		// // Util.showMessageWithOk(EntryFormActivity.this,
		// getString(R.string.no_internet));
		// Log.v("Connection Status:", "No internet");
		// }

	}

	private boolean isGooglePlayServicesAvailable() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == status) {
			return true;
		} else {
			return false;
		}
	}

	protected void createLocationRequest() {
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setNumUpdates(1); // TODO New line added
		mLocationRequest.setInterval(5000); // TODO New line added
		mLocationRequest.setFastestInterval(1000); // TODO New line added
		fusedLocationProviderApi = LocationServices.FusedLocationApi;
		mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("EntryFormActivity", "Connection failed: " + connectionResult.toString());
		Toast.makeText(LandingPageActivity.this, "Connection failed: " + connectionResult.toString(), Toast.LENGTH_LONG).show();
		hideProgressDialog();
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.d("EntryFormActivity", "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
		startLocationUpdates();

	}

	protected void startLocationUpdates() {
		// PendingResult<Status> pendingResult =
		// LocationServices.FusedLocationApi.requestLocationUpdates(
		// mGoogleApiClient, mLocationRequest, this);
		fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		Log.d("EntryFormActivity", "Location update started ..............: ");
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		hideProgressDialog();
		Log.d("EntryFormActivity", "Firing onLocationChanged..............................................");
		mCurrentLocation = location;
		// mLastUpdateTime = postFormater.format(new Date());
		updateUI();
	}

	private void updateUI() {

		Log.d("EntryFormActivity", "UI update initiated .............");

		SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		if (null != mCurrentLocation) {
			String lat = String.valueOf(mCurrentLocation.getLatitude());
			String lng = String.valueOf(mCurrentLocation.getLongitude());
			Log.v("LOCATION", "At Time: " + postFormater.format(new Date()) + "\n" + "Latitude: " + lat + "\n" + "Longitude: " + lng + "\n"
					+ "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" + "Provider: " + mCurrentLocation.getProvider());

			tv_latitude.setText(lat);
			tv_longitude.setText(lng);
		} else {
			Log.d("EntryFormActivity", "location is null ...............");
		}
	}

	private void sendingImages(final int position) {

		imagePosition = position;
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		// paramsMap.put("ID", user.getSurfaceLayingOfClearWaterMainUpdateId());
		paramsMap.put("Id", ImageID);
		paramsMap.put("Mode", "ad");
		paramsMap.put("Id", ImageID);
		paramsMap.put("ImageNo", String.valueOf(allImagesList.get(position).getImageCount()));
		paramsMap.put("ImageData", allImagesList.get(position).getBase64value());

		isImageService = true;

		// TODO
		volleyTaskManager.doPostImage(paramsMap);
	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {

		Log.v("resultJsonObject", resultJsonObject.toString());

		if (!isOnGoingProjectService && !isGetStagesService && !isSubmitService && !isImageService) {
			// isGetFormNameService = false;

			parseDistrictandBlocks(resultJsonObject);
		} else if (isOnGoingProjectService) {

			isOnGoingProjectService = false;
			parseOngoinGProjects(resultJsonObject);

		} else if (isGetStagesService) {
			isGetStagesService = false;
			parseStages(resultJsonObject);
		} else if (isSubmitService) {
			isSubmitService = false;
			parseSubmitService(resultJsonObject);
		} else if (isImageService) {
			isImageService = false;

			Log.d("Sent position:", imagePosition + "");

			String FDSaveSchoolImageDataResult = resultJsonObject.optString("SaveSurfaceWaterTreatmentPlantWTPMainImageResult");
			// UserClass user = Util.fetchUserClass(mContext);

			// if
			// (user.getSurfaceWaterTreatmentPlantUpdateId().equalsIgnoreCase(FDSaveSchoolImageDataResult))
			// {
			allImagesList.get(imagePosition).setIsPosted(true);
			// }
			if (imagePosition < allImagesList.size() - 1) {

				sendingImages(imagePosition + 1);
			} else {

				Log.e("Image Sending", "All Completed");

				Util.showCallBackMessageWithOk(mContext, "Data Posted successfully.", new AlertDialogCallBack() {

					@Override
					public void onSubmit() {
						clearForm();
					}

					@Override
					public void onCancel() {

					}
				});
			}

		}
		volleyTaskManager.hideProgressDialog();
	}

	@Override
	public void onError() {

	}

	// -----------------------PARSING
	private void parseDistrictandBlocks(JSONObject resultJsonObject) {

		JSONArray districtArray = resultJsonObject.optJSONArray("objDisLst");
		for (int i = 0; i < districtArray.length(); i++) {
			JSONObject districtJsonObject = districtArray.optJSONObject(i);
			District district = new District();
			district.DIS_ID = districtJsonObject.optString("DIS_ID");
			district.DIS_NAME = districtJsonObject.optString("DIS_NAME");
			districtList.add(district);

		}

		JSONArray blockArray = resultJsonObject.optJSONArray("objBlockLst");

		for (int i = 0; i < blockArray.length(); i++) {
			JSONObject blockJsonObject = blockArray.optJSONObject(i);
			Block block = new Block();
			block.BLOCK_ID = blockJsonObject.optString("BLOCK_ID");
			block.BLOCK_NAME = blockJsonObject.optString("BLOCK_NAME");
			block.DIST_ID = blockJsonObject.optString("DIST_ID");
			blockList.add(block);

		}
		saveData(districtList, blockList);
		populateDistrictDropdown();

	}

	private void parseOngoinGProjects(JSONObject resultJsonObject) {

		JSONArray ongoingProjectArray = resultJsonObject.optJSONArray("GetOnGoingProjectResult");
		for (int i = 0; i < ongoingProjectArray.length(); i++) {

			JSONObject ongoingProjectJsonObject = ongoingProjectArray.optJSONObject(i);
			OngoingProjects ongoingProjects = new OngoingProjects();
			ongoingProjects.ID = ongoingProjectJsonObject.optString("ID");
			ongoingProjects.NAME = ongoingProjectJsonObject.optString("NAME");
			ongoingProjects.BLOCK_ID = ongoingProjectJsonObject.optString("BLOCK_ID");
			onGoingProjectList.add(ongoingProjects);

		}
		saveOnGoingProjectData(onGoingProjectList);

		isGetStagesService = true;
		getStages();

	}

	private void parseStages(JSONObject resultJsonObject) {

		JSONArray ongoingProjectArray = resultJsonObject.optJSONArray("GetStagesResult");
		for (int i = 0; i < ongoingProjectArray.length(); i++) {
			JSONObject districtJsonObject = ongoingProjectArray.optJSONObject(i);
			Stages stage = new Stages();
			stage.ID = districtJsonObject.optString("ID");
			stage.STAGE = districtJsonObject.optString("STAGE");
			stage.STAGE_VALUE = districtJsonObject.optString("STAGE_VALUE");
			stageList.add(stage);

		}

		saveOnStage(stageList);
		populateStageDropdown();
	}

	private void parseSubmitService(JSONObject resultJsonObject) {

		Log.d("parseSubmitService", "" + resultJsonObject);

		if (resultJsonObject.toString() != null && !resultJsonObject.toString().trim().isEmpty()) {

			try {

				String result = "";

				result = resultJsonObject.optString("RES");
				ImageID = resultJsonObject.optString("ID");

				Log.v("TAG", "" + result);

				if (!result.equalsIgnoreCase("0")) {

					if (imagesList.size() > 0) {
						sendingImages(0);
					} else {
						Util.showCallBackMessageWithOk(LandingPageActivity.this, "Data Posted successfully.", new AlertDialogCallBack() {

							@Override
							public void onSubmit() {
								clearForm();
							}

							@Override
							public void onCancel() {

							}
						});
					}

				} else
					Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
		}

	}

	// ---------------------------------------------------------------
	// =================================== SAVE DATA IN DATABASE
	// save District and Blocks in database
	private void saveData(ArrayList<District> district, ArrayList<Block> block) {

		for (int i = 0; i < district.size(); i++) {

			ContentValues districtValues = new ContentValues();
			districtValues.put(DBConstants.DISTRICT_ID, district.get(i).DIS_ID);
			districtValues.put(DBConstants.DISTRICT_NAME, district.get(i).DIS_NAME);
			new DistrictDB().saveDistrictData(LandingPageActivity.this, districtValues);
		}
		for (int i = 0; i < block.size(); i++) {

			ContentValues blockValues = new ContentValues();
			blockValues.put(DBConstants.BLOCK_ID, block.get(i).BLOCK_ID);
			blockValues.put(DBConstants.BLOCK_NAME, block.get(i).BLOCK_NAME);
			blockValues.put(DBConstants.BLOCK_DISTRICT_ID, block.get(i).DIST_ID);
			new BlockDB().saveBlockData(LandingPageActivity.this, blockValues);
		}

	}

	// save ongoing projects in database
	private void saveOnGoingProjectData(ArrayList<OngoingProjects> district) {

		for (int i = 0; i < district.size(); i++) {

			ContentValues ongoingProjectsValue = new ContentValues();
			ongoingProjectsValue.put(DBConstants.ONGOING_PROJECT_ID, district.get(i).ID);
			ongoingProjectsValue.put(DBConstants.ONGOING_PROJECT_NAME, district.get(i).NAME);
			ongoingProjectsValue.put(DBConstants.ONGOING_PROJECT_BLOCK_ID, district.get(i).BLOCK_ID);
			new OngoingProjectsDB().saveOnGoingProjectsData(LandingPageActivity.this, ongoingProjectsValue);
		}

	}

	// save stages in database
	private void saveOnStage(ArrayList<Stages> stages) {

		for (int i = 0; i < stages.size(); i++) {

			ContentValues stagesValue = new ContentValues();
			stagesValue.put(DBConstants.STAGES_ID, stages.get(i).ID);
			stagesValue.put(DBConstants.STAGES_NAME, stages.get(i).STAGE);
			stagesValue.put(DBConstants.STAGES_VALUE, stages.get(i).STAGE_VALUE);
			new StageDB().saveStagesData(LandingPageActivity.this, stagesValue);
		}

	}

	// =====================================================================

	protected void stopLocationUpdates() {

		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
		Log.d("EntryFormActivity", "Location update stopped .......................");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCurrentLocation != null) {
			stopLocationUpdates();
		}
		Util.trimCache(LandingPageActivity.this);
	}

	private void showpopup(View v) {

		if (popupWindow == null || !popupWindow.isShowing()) {
			LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			View popupView = layoutInflater.inflate(R.layout.option_menu, null);
			popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());

			TextView tv_logout = (TextView) popupView.findViewById(R.id.tv_logout);
			TextView tv_reload = (TextView) popupView.findViewById(R.id.tv_reload);
			TextView tv_sync = (TextView) popupView.findViewById(R.id.tv_sync);

			tv_logout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popupWindow.dismiss();

					startActivity(new Intent(mContext, LoginActivity.class));
					finish();

				}
			});

			tv_reload.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					Util.showCallBackMessageWithOkCancel(mContext, "Do you want to reload?", new AlertDialogCallBack() {

						@Override
						public void onSubmit() {

							initView();

						}

						@Override
						public void onCancel() {

						}
					});

				}
			});

			tv_sync.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					popupWindow.dismiss();

					Toast.makeText(mContext, "Offline Sync ( )", Toast.LENGTH_SHORT).show();

				}
			});

			popupWindow.setOutsideTouchable(true);
			popupWindow.showAsDropDown(v, 100, 0);
		} else {
			popupWindow.dismiss();
		}

	}

	*//** Method that clears the form *//*
	private void clearForm() {

		dropDown_district.setText("");
		dropDown_block.setText("");
		dropDown_ongoingProjects.setText("");
		dropDown_stages.setText("");

		dropDown_district.setHint("");
		dropDown_block.setHint("");
		dropDown_ongoingProjects.setHint("");
		dropDown_stages.setHint("");

		dropDown_block.setEnabled(false);
		dropDown_ongoingProjects.setEnabled(false);
		dropDown_stages.setEnabled(false);

		etComments.setText("");

		tv_imageProgress.setText("[Image added 0/3]");
		imagesList.clear();
		imageUpdateOnView();
		tv_latitude.setText("");
		tv_longitude.setText("");

	}

}
*/