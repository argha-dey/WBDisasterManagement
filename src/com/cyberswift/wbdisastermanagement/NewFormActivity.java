package com.cyberswift.wbdisastermanagement;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberswift.wbdisastermanagement.adapter.DownloadInfoArrayAdapter;
import com.cyberswift.wbdisastermanagement.adapter.DropDownViewForXML;
import com.cyberswift.wbdisastermanagement.adapter.ImageAdapter;
import com.cyberswift.wbdisastermanagement.connection.CheckNetworkQuality;
import com.cyberswift.wbdisastermanagement.connection.NetworkListener;
import com.cyberswift.wbdisastermanagement.db.BlockDB;
import com.cyberswift.wbdisastermanagement.db.DBConstants;
import com.cyberswift.wbdisastermanagement.db.DistrictDB;
import com.cyberswift.wbdisastermanagement.db.MPCSProjectDB;
import com.cyberswift.wbdisastermanagement.db.ProjectDB;
import com.cyberswift.wbdisastermanagement.location.FetchCordinates;
import com.cyberswift.wbdisastermanagement.location.GPSTracker;
import com.cyberswift.wbdisastermanagement.location.LocationCallbackListener;
import com.cyberswift.wbdisastermanagement.model.Block;
import com.cyberswift.wbdisastermanagement.model.District;
import com.cyberswift.wbdisastermanagement.model.DownloadInfo;
import com.cyberswift.wbdisastermanagement.model.FinancialInfo;
import com.cyberswift.wbdisastermanagement.model.ImageClass;
import com.cyberswift.wbdisastermanagement.model.MPCSProject;
import com.cyberswift.wbdisastermanagement.model.Milestone;
import com.cyberswift.wbdisastermanagement.model.OfflineDataSet;
import com.cyberswift.wbdisastermanagement.model.Project;
import com.cyberswift.wbdisastermanagement.model.Stages;
import com.cyberswift.wbdisastermanagement.model.UserClass;
import com.cyberswift.wbdisastermanagement.service.DataSyncService;
import com.cyberswift.wbdisastermanagement.service.VolleyTaskManager;
import com.cyberswift.wbdisastermanagement.static_constants.ServiceTags;
import com.cyberswift.wbdisastermanagement.util.AlertDialogCallBack;
import com.cyberswift.wbdisastermanagement.util.PopUpView;
import com.cyberswift.wbdisastermanagement.util.ServerResponseCallback;
import com.cyberswift.wbdisastermanagement.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class NewFormActivity extends Activity implements ServerResponseCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, ServiceTags, DBConstants, LocationCallbackListener, NetworkListener {

	private ProgressDialog mProgressDialog;
	private VolleyTaskManager volleyTaskManager;
	private DropDownViewForXML dropDown_district, dropDown_block, dropDown_mpsProject, dropDown_mpsName, dropDown_milestone;//, tv_reportDate
	private LinearLayout llMilestoneView, ll_cumulative_physical_prog;
	private TextView tv_packageNumber, tv_target_completionDate, tv_contractAmount, tv_cumulative_physical_prog;
	private RadioGroup rg_protective_helmets_amps_shoes, rg_barricadingOfSite, rg_electric_wiring, rg_hatShoes, rg_fireSafetyDevices,
			rg_labourCampStructureSatisfactory, rg_lpg, rg_seperateToilet, rg_drinkingWater, rg_mpcs_info_board;
	private boolean hardBarricade = false, protectiveHelmets = false, barricadingOfSite = false, electricWiring = false,
			hardHatShoes = false, fireSafetyDevices = false, labourWelfare = false, labourCampStructure = false, lpg = false,
			seperateSanitation = false, drinkingWater = false, mpcsInfo = false;
	private Button bt_financial_info;

	private ArrayList<Project> projectList;
	private ArrayList<District> districtList;
	private ArrayList<Block> blockList;
	private ArrayList<MPCSProject> mpcsProjList;
	private ArrayList<Milestone> previousDateList;

	private boolean isFetchProjectService = false;
	private boolean isFetchAllProjectDataService = false;
	private boolean isImageService = false;
	private boolean isHistoryTimelineService = false;
	private boolean isFetchMilestoneService = false;
	private boolean isFetchProjectDetailsService = false;

	private boolean isMilestoneVisible = false;
	private ArrayList<ImageClass> imagesList;
	private static final int PICTURE_GALLERY_REQUEST = 2572, CAMERA_PIC_REQUEST = 1337;
	//private String packageContractAmount = "0.0";

	// TODO remember to reset before closing the activity
	private static Uri mCapturedImageURI;

	private ViewPager vp_selectedImages;
	private TextView tv_imageProgress;
	private View v_swipeLeft, v_swipeRight;
	private String districtId = "", projectType = "";

	// ============ LOCATION 
	private Location mCurrentLocation;
	private LocationRequest mLocationRequest;
	private AlertDialog systemAlertDialog;
	private FusedLocationProviderApi fusedLocationProviderApi;
	private GoogleApiClient mGoogleApiClient;

	private TextView tv_latitude, tv_longitude;

	private boolean isSubmitService = false;
	private boolean isDataSync = false;
	private boolean isFeedBackService = false;
	private boolean isChangePasswordService = false;

	private EditText etComments;

	private Context mContext;

	// =========== FORM POST DATA FIELD
	private String mpcsProjectId;

	// =========== IMAGE FIELD
	private ArrayList<ImageClass> allImagesList = new ArrayList<ImageClass>();
	private int imagePosition;
	private String ImageID = "";
	private PopupWindow popupWindow;
	public static long back_pressed;
	private Stages currentStageValue;//, previousStageValue
	private int imageCount = 0;

	private DataSyncReceiver dataSyncReceiver;

	private Dialog customDialog;
	private GPSTracker gps;

	//=======TODO OPTIMIZATION
	private LocationManager locManager;
	private LocationListener locListener;
	private Location mobileLocation;
	private String changedPassword = "";
	ProgressDialog offlineDataSyncProgressDialog;
	CheckNetworkQuality mConnectionStateActivity;
	private ImageButton btn_options;

	// Shows the number of forms saved Off-line
	private int offlinedatasize = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_landing_layout);

		mContext = NewFormActivity.this;

		initView();

	}

	private void initView() {

		bt_financial_info = (Button) findViewById(R.id.bt_financial_info);

		vp_selectedImages = (ViewPager) findViewById(R.id.vp_selectedImages);
		tv_imageProgress = (TextView) findViewById(R.id.tv_imageProgress);
		v_swipeLeft = (View) findViewById(R.id.v_swipeLeft);
		v_swipeRight = (View) findViewById(R.id.v_swipeRight);

		tv_packageNumber = (TextView) findViewById(R.id.tv_packageNumber);
		dropDown_mpsProject = (DropDownViewForXML) findViewById(R.id.dropDown_mpsProject);
		dropDown_district = (DropDownViewForXML) findViewById(R.id.dropDown_district);
		dropDown_block = (DropDownViewForXML) findViewById(R.id.dropDown_block);
		dropDown_mpsName = (DropDownViewForXML) findViewById(R.id.dropDown_mpsName);

		dropDown_milestone = (DropDownViewForXML) findViewById(R.id.dropDown_milestone);

		//dropDown_previousVisit = (DropDownViewForXML) findViewById(R.id.dropDown_previousVisit);

		llMilestoneView = (LinearLayout) findViewById(R.id.llMilestoneView);
		llMilestoneView.setVisibility(View.GONE);
		populateMilestoneDropdown();
		ll_cumulative_physical_prog = (LinearLayout) findViewById(R.id.ll_cumulative_physical_prog);

		tv_target_completionDate = (TextView) findViewById(R.id.tv_target_completionDate);

		tv_cumulative_physical_prog = (TextView) findViewById(R.id.tv_cumulative_physical_prog);

		//tv_reportDate = (TextView) findViewById(R.id.tv_reportDate);

		tv_contractAmount = (TextView) findViewById(R.id.tv_contractAmount);

		btn_options = (ImageButton) findViewById(R.id.btn_options);

		initViewEMP_ESMF();

		dropDown_mpsProject.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				resetOnMPCSProjectSelection();
				String mpcProjectId = projectList.get(position).projectId;
				projectType = projectList.get(position).projectId;

				//TODO- UNCHECK FOR ONLINE FEATURE
				/*	if (Util.checkConnectivity(mContext)) {

						isFetchAllProjectDataService = true;
						fetchAllMPCSProjectsData(mpcProjectId);
					} else {
						populateDistrictDropdown();
					}*/

				//OFFLINE PROJECT LOADING
				populateDistrictDropdown();

				//Enable Popup View
				bt_financial_info.setVisibility(View.VISIBLE);
				bt_financial_info.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (Util.checkConnectivity(mContext)) {

							isFetchProjectDetailsService = true;
							HashMap<String, String> requestMap = new HashMap<String, String>();
							requestMap.put("PROJECT_TYPE_ID", projectList.get(position).projectId);
							volleyTaskManager.doPostFetchProjectDetails(requestMap);
						} else
							Util.showMessageWithOk(mContext, "No Internet Connection.");
					}
				});
			}
		});

		dropDown_milestone.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				View childView;
				llMilestoneView.removeAllViews();
				hideMilestone();
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				switch (position) {
				case 0:
					showMilestone();
					childView = inflater.inflate(R.layout.milestone_one_layout, null);
					llMilestoneView.addView(childView);
					initMilestoneI(childView);
					break;
				case 1:
					showMilestone();
					childView = inflater.inflate(R.layout.milestone_two_layout, null);
					llMilestoneView.addView(childView);
					initMilestoneII(childView);
					break;
				case 2:
					showMilestone();
					childView = inflater.inflate(R.layout.milestone_three_layout, null);
					llMilestoneView.addView(childView);
					initMilestoneIII(childView);
					break;
				default:
					hideMilestone();
					break;
				}
			}

		});

		dropDown_district.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				resetOnDistrictSelection();
				String districtID = districtList.get(position).DIS_ID;
				districtId = districtID;
				populateBlockDropdown(districtID);

			}
		});

		dropDown_block.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				resetOnBlockSelection();
				String blockID = blockList.get(position).BLOCK_ID;
				populateMPCSDropdown(districtId, blockID, projectType);
			}
		});

		dropDown_mpsName.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				resetOnMPCSNameSelection();

				MPCSProject mpcsProject = mpcsProjList.get(position);

				mpcsProjectId = mpcsProject.id;

				tv_packageNumber.setText(mpcsProject.packageNo);

				tv_target_completionDate.setText(mpcsProject.dateTargetCompletion);

				//tv_reportDate.setText(Util.getCurrentFormattedDate());

				tv_contractAmount.setText(mpcsProject.contractAmount);
				//getTimeLine();
			}
		});

		etComments = (EditText) findViewById(R.id.etComments);

		volleyTaskManager = new VolleyTaskManager(NewFormActivity.this);

		projectList = new ArrayList<Project>();
		districtList = new ArrayList<District>();
		blockList = new ArrayList<Block>();
		mpcsProjList = new ArrayList<MPCSProject>();
		imagesList = new ArrayList<ImageClass>();
		previousDateList = new ArrayList<Milestone>();

		currentStageValue = new Stages();
		//previousStageValue = new Stages();

		populateProjectDropdown();
		//fetchMPCSProjects();

		// ============ @ LOCATION @ ===========

		tv_latitude = (TextView) findViewById(R.id.tv_latitude);
		tv_longitude = (TextView) findViewById(R.id.tv_longitude);

		mProgressDialog = new ProgressDialog(NewFormActivity.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		if (Util.fetchOfflineDataSetList(mContext) != null)
			offlinedatasize = Util.fetchOfflineDataSetList(mContext).size();
		btn_options.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showpopup(v);
			}
		});

	}

	/**
	 * Initialize the EMP & ESMF view
	 * */
	private void initViewEMP_ESMF() {

		rg_protective_helmets_amps_shoes = (RadioGroup) findViewById(R.id.rg_protective_helmets_amps_shoes);

		rg_barricadingOfSite = (RadioGroup) findViewById(R.id.rg_barricadingOfSite);

		rg_electric_wiring = (RadioGroup) findViewById(R.id.rg_electric_wiring);

		rg_hatShoes = (RadioGroup) findViewById(R.id.rg_hatShoes);

		rg_fireSafetyDevices = (RadioGroup) findViewById(R.id.rg_fireSafetyDevices);

		rg_labourCampStructureSatisfactory = (RadioGroup) findViewById(R.id.rg_labourCampStructureSatisfactory);

		rg_lpg = (RadioGroup) findViewById(R.id.rg_lpg);

		rg_seperateToilet = (RadioGroup) findViewById(R.id.rg_seperateToilet);

		rg_drinkingWater = (RadioGroup) findViewById(R.id.rg_drinkingWater);

		rg_mpcs_info_board = (RadioGroup) findViewById(R.id.rg_mpcs_info_board);

		rg_protective_helmets_amps_shoes.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_protective_helmets_amps_shoes_complete:
					protectiveHelmets = true;
					break;
				case R.id.rb_protective_helmets_amps_shoes_incomplete:
					protectiveHelmets = false;
					break;
				default:
					protectiveHelmets = false;
					break;
				}
			}
		});
		rg_barricadingOfSite.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb__barricadingOfSite_yes:
					barricadingOfSite = true;
					break;
				case R.id.rb__barricadingOfSite_no:
					barricadingOfSite = false;
					break;
				default:
					barricadingOfSite = false;
					break;

				}
			}
		});

		rg_electric_wiring.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_electric_wiring_yes:
					electricWiring = true;
					break;
				case R.id.rb_electric_wiring_no:
					electricWiring = false;
					break;
				default:
					electricWiring = false;
					break;
				}
			}
		});
		rg_hatShoes.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_hatShoes_yes:
					hardHatShoes = true;
					break;
				case R.id.rb_hatShoes_no:
					hardHatShoes = false;
					break;
				default:
					hardHatShoes = false;
					break;
				}
			}
		});

		rg_fireSafetyDevices.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb__fireSafetyDevices_yes:
					fireSafetyDevices = true;
					break;
				case R.id.rb__fireSafetyDevices_no:
					fireSafetyDevices = false;
					break;
				default:
					fireSafetyDevices = false;
					break;
				}
			}
		});

		rg_labourCampStructureSatisfactory.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_labourCampStructureSatisfactory_yes:
					labourCampStructure = true;
					break;
				case R.id.rb_labourCampStructureSatisfactory_no:
					labourCampStructure = false;
					break;
				default:
					labourCampStructure = false;
					break;
				}
			}
		});

		rg_lpg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_lpg_yes:

					lpg = true;

					break;
				case R.id.rb_lpg_no:

					lpg = false;
					break;

				default:
					lpg = false;
					break;
				}
			}
		});

		rg_seperateToilet.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radiogroup, int checkedId) {
				switch (checkedId) {
				case R.id.rb_seperateToilet_yes:
					seperateSanitation = true;
					break;
				case R.id.rb_seperateToilet_no:
					seperateSanitation = false;
					break;
				default:
					seperateSanitation = false;
					break;
				}
			}
		});

		rg_drinkingWater.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_drinkingWater_yes:
					drinkingWater = true;
					break;
				case R.id.rb_drinkingWater_no:
					drinkingWater = false;
					break;
				default:
					drinkingWater = false;
					break;
				}
			}
		});
		rg_mpcs_info_board.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_mpcs_info_board_yes:
					mpcsInfo = true;
					break;
				case R.id.rb_mpcs_info_board_no:
					mpcsInfo = false;
					break;
				default:
					mpcsInfo = false;
					break;
				}
			}
		});
	}

	private void resetEmpEsmf() {

		rg_protective_helmets_amps_shoes.clearCheck();
		rg_barricadingOfSite.clearCheck();
		rg_electric_wiring.clearCheck();
		rg_hatShoes.clearCheck();
		rg_fireSafetyDevices.clearCheck();
		rg_labourCampStructureSatisfactory.clearCheck();
		rg_lpg.clearCheck();
		rg_seperateToilet.clearCheck();
		rg_drinkingWater.clearCheck();
		rg_mpcs_info_board.clearCheck();

		hardBarricade = false;
		protectiveHelmets = false;
		barricadingOfSite = false;
		electricWiring = false;
		hardHatShoes = false;
		fireSafetyDevices = false;
		labourWelfare = false;
		labourCampStructure = false;
		lpg = false;
		seperateSanitation = false;
		drinkingWater = false;
		mpcsInfo = false;
	}

	private void showMilestone() {

		isMilestoneVisible = true;
		llMilestoneView.setVisibility(View.VISIBLE);
		ll_cumulative_physical_prog.setVisibility(View.VISIBLE);

	}

	private void hideMilestone() {

		isMilestoneVisible = false;
		llMilestoneView.setVisibility(View.GONE);
		ll_cumulative_physical_prog.setVisibility(View.GONE);

	}

	private void initMilestoneI(View childView) {

		final CheckBox chkbx_stageI = (CheckBox) findViewById(R.id.chkbx_stageI);
		final CheckBox chkbx_stageII = (CheckBox) findViewById(R.id.chkbx_stageII);
		final CheckBox chkbx_stageIII = (CheckBox) findViewById(R.id.chkbx_stageIII);
		final CheckBox chkbx_stageIV = (CheckBox) findViewById(R.id.chkbx_stageIV);

		chkbx_stageI.setChecked(currentStageValue.stageI_absolute);
		chkbx_stageII.setChecked(currentStageValue.stageII_absolute);
		chkbx_stageIII.setChecked(currentStageValue.stageIII_absolute);
		chkbx_stageIV.setChecked(currentStageValue.stageIV_absolute);

		RelativeLayout rl_foundation = (RelativeLayout) findViewById(R.id.rl_foundation);
		RelativeLayout rl_foundation_complete = (RelativeLayout) findViewById(R.id.rl_foundation_complete);
		RelativeLayout rl_firstFloor = (RelativeLayout) findViewById(R.id.rl_firstFloor);
		RelativeLayout rl_second_floor = (RelativeLayout) findViewById(R.id.rl_second_floor);

		expandTouchArea(rl_foundation, chkbx_stageI, 100);
		expandTouchArea(rl_foundation_complete, chkbx_stageII, 100);
		expandTouchArea(rl_firstFloor, chkbx_stageIII, 100);
		expandTouchArea(rl_second_floor, chkbx_stageIV, 100);

		chkbx_stageI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				chkbx_stageII.setChecked(false);
				chkbx_stageIII.setChecked(false);
				chkbx_stageIV.setChecked(false);

				currentStageValue.stageI_absolute = chkbx_stageI.isChecked();
				if (chkbx_stageI.isChecked()) {
					chkbx_stageII.setChecked(false);
					chkbx_stageIII.setChecked(false);
					chkbx_stageIV.setChecked(false);

				}
				currentStageValue.stageII_absolute = false;
				currentStageValue.stageIII_absolute = false;
				currentStageValue.stageIV_absolute = false;
				currentStageValue.stageV_absolute = false;
				currentStageValue.stageVI_absolute = false;
				currentStageValue.stageVII_absolute = false;
				currentStageValue.stageVIII_absolute = false;
				currentStageValue.stageIX_absolute = false;
				currentStageValue.stageX_absolute = false;

				updateProgressRealtime();
			}
		});

		chkbx_stageII.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				chkbx_stageIII.setChecked(false);
				chkbx_stageIV.setChecked(false);
				currentStageValue.stageII_absolute = chkbx_stageII.isChecked();
				Log.v("Tag", "Is checked: " + chkbx_stageII.isChecked());

				if (chkbx_stageII.isChecked()) {
					currentStageValue.stageI_absolute = true;
					chkbx_stageI.setChecked(true);
					chkbx_stageIII.setChecked(false);
					chkbx_stageIV.setChecked(false);

				}

				currentStageValue.stageIII_absolute = false;
				currentStageValue.stageIV_absolute = false;
				currentStageValue.stageV_absolute = false;
				currentStageValue.stageVI_absolute = false;
				currentStageValue.stageVII_absolute = false;
				currentStageValue.stageVIII_absolute = false;
				currentStageValue.stageIX_absolute = false;
				currentStageValue.stageX_absolute = false;

				updateProgressRealtime();
			}
		});

		chkbx_stageIII.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				chkbx_stageIV.setChecked(false);
				currentStageValue.stageIII_absolute = chkbx_stageIII.isChecked();
				if (chkbx_stageIII.isChecked()) {
					currentStageValue.stageI_absolute = true;
					currentStageValue.stageII_absolute = true;
					chkbx_stageI.setChecked(true);
					chkbx_stageII.setChecked(true);
					chkbx_stageIV.setChecked(false);

				}
				currentStageValue.stageIV_absolute = false;
				currentStageValue.stageV_absolute = false;
				currentStageValue.stageVI_absolute = false;
				currentStageValue.stageVII_absolute = false;
				currentStageValue.stageVIII_absolute = false;
				currentStageValue.stageIX_absolute = false;
				currentStageValue.stageX_absolute = false;
				updateProgressRealtime();
			}
		});

		chkbx_stageIV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				currentStageValue.stageIV_absolute = chkbx_stageIV.isChecked();
				if (chkbx_stageIV.isChecked()) {
					currentStageValue.stageI_absolute = true;
					currentStageValue.stageII_absolute = true;
					currentStageValue.stageIII_absolute = true;
					chkbx_stageI.setChecked(true);
					chkbx_stageII.setChecked(true);
					chkbx_stageIII.setChecked(true);

				}
				currentStageValue.stageV_absolute = false;
				currentStageValue.stageVI_absolute = false;
				currentStageValue.stageVII_absolute = false;
				currentStageValue.stageVIII_absolute = false;
				currentStageValue.stageIX_absolute = false;
				currentStageValue.stageX_absolute = false;
				updateProgressRealtime();
			}
		});

	}

	private void initMilestoneII(View childView) {

		final CheckBox chkbx_stageV = (CheckBox) findViewById(R.id.chkbx_stageV);
		final CheckBox chkbx_stageVI = (CheckBox) findViewById(R.id.chkbx_stageVI);
		final CheckBox chkbx_stageVII = (CheckBox) findViewById(R.id.chkbx_stageVII);

		chkbx_stageV.setChecked(currentStageValue.stageV_absolute);
		chkbx_stageVI.setChecked(currentStageValue.stageVI_absolute);
		chkbx_stageVII.setChecked(currentStageValue.stageVII_absolute);

		RelativeLayout rl_brickwork = (RelativeLayout) findViewById(R.id.rl_brickwork);
		RelativeLayout rl_plastering = (RelativeLayout) findViewById(R.id.rl_plastering);
		RelativeLayout rl_civil_work = (RelativeLayout) findViewById(R.id.rl_civil_work);

		expandTouchArea(rl_brickwork, chkbx_stageV, 100);
		expandTouchArea(rl_plastering, chkbx_stageVI, 100);
		expandTouchArea(rl_civil_work, chkbx_stageVII, 100);

		chkbx_stageV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				chkbx_stageVI.setChecked(false);
				chkbx_stageVII.setChecked(false);
				currentStageValue.stageV_absolute = chkbx_stageV.isChecked();
				if (chkbx_stageV.isChecked()) {
					currentStageValue.stageI_absolute = true;
					currentStageValue.stageII_absolute = true;
					currentStageValue.stageIII_absolute = true;
					currentStageValue.stageIV_absolute = true;
					chkbx_stageVI.setChecked(false);
					chkbx_stageVII.setChecked(false);
				}
				currentStageValue.stageVI_absolute = false;
				currentStageValue.stageVII_absolute = false;
				currentStageValue.stageVIII_absolute = false;
				currentStageValue.stageIX_absolute = false;
				currentStageValue.stageX_absolute = false;
				updateProgressRealtime();
			}
		});
		chkbx_stageVI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				chkbx_stageVII.setChecked(false);
				currentStageValue.stageVI_absolute = chkbx_stageVI.isChecked();
				if (chkbx_stageVI.isChecked()) {
					currentStageValue.stageI_absolute = true;
					currentStageValue.stageII_absolute = true;
					currentStageValue.stageIII_absolute = true;
					currentStageValue.stageIV_absolute = true;
					currentStageValue.stageV_absolute = true;
					chkbx_stageV.setChecked(true);
					chkbx_stageVII.setChecked(false);
				}
				currentStageValue.stageVII_absolute = false;
				currentStageValue.stageVIII_absolute = false;
				currentStageValue.stageIX_absolute = false;
				currentStageValue.stageX_absolute = false;
				updateProgressRealtime();
			}
		});
		chkbx_stageVII.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				currentStageValue.stageVII_absolute = chkbx_stageVII.isChecked();
				if (chkbx_stageVII.isChecked()) {
					currentStageValue.stageI_absolute = true;
					currentStageValue.stageII_absolute = true;
					currentStageValue.stageIII_absolute = true;
					currentStageValue.stageIV_absolute = true;
					currentStageValue.stageV_absolute = true;
					currentStageValue.stageVI_absolute = true;

					chkbx_stageV.setChecked(true);
					chkbx_stageVI.setChecked(true);
				}
				currentStageValue.stageVIII_absolute = false;
				currentStageValue.stageIX_absolute = false;
				currentStageValue.stageX_absolute = false;
				updateProgressRealtime();
			}
		});

	}

	private void initMilestoneIII(View childView) {

		final CheckBox chkbx_stageVIII = (CheckBox) findViewById(R.id.chkbx_stageVIII);
		final CheckBox chkbx_stageIX = (CheckBox) findViewById(R.id.chkbx_stageIX);
		final CheckBox chkbx_stageX = (CheckBox) findViewById(R.id.chkbx_stageX);

		chkbx_stageVIII.setChecked(currentStageValue.stageVIII_absolute);
		chkbx_stageIX.setChecked(currentStageValue.stageIX_absolute);
		chkbx_stageX.setChecked(currentStageValue.stageX_absolute);

		RelativeLayout rl_sanitory_work = (RelativeLayout) findViewById(R.id.rl_sanitory_work);
		RelativeLayout rl_electrical_work = (RelativeLayout) findViewById(R.id.rl_electrical_work);
		RelativeLayout rl_remaining = (RelativeLayout) findViewById(R.id.rl_remaining);

		expandTouchArea(rl_sanitory_work, chkbx_stageVIII, 100);
		expandTouchArea(rl_electrical_work, chkbx_stageIX, 100);
		expandTouchArea(rl_remaining, chkbx_stageX, 100);

		chkbx_stageVIII.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				chkbx_stageIX.setChecked(false);
				chkbx_stageX.setChecked(false);
				currentStageValue.stageVIII_absolute = chkbx_stageVIII.isChecked();
				if (chkbx_stageVIII.isChecked()) {
					currentStageValue.stageI_absolute = true;
					currentStageValue.stageII_absolute = true;
					currentStageValue.stageIII_absolute = true;
					currentStageValue.stageIV_absolute = true;
					currentStageValue.stageV_absolute = true;
					currentStageValue.stageVI_absolute = true;
					currentStageValue.stageVII_absolute = true;

					chkbx_stageIX.setChecked(false);
					chkbx_stageX.setChecked(false);
					currentStageValue.stageIX_absolute = false;
					currentStageValue.stageX_absolute = false;
				}

				updateProgressRealtime();
			}
		});
		chkbx_stageIX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				chkbx_stageX.setChecked(false);
				currentStageValue.stageIX_absolute = chkbx_stageIX.isChecked();
				if (chkbx_stageIX.isChecked()) {
					currentStageValue.stageI_absolute = true;
					currentStageValue.stageII_absolute = true;
					currentStageValue.stageIII_absolute = true;
					currentStageValue.stageIV_absolute = true;
					currentStageValue.stageV_absolute = true;
					currentStageValue.stageVI_absolute = true;
					currentStageValue.stageVII_absolute = true;
					currentStageValue.stageVIII_absolute = true;
					chkbx_stageVIII.setChecked(true);

					chkbx_stageX.setChecked(false);
				}
				currentStageValue.stageX_absolute = false;
				updateProgressRealtime();
			}
		});
		chkbx_stageX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				currentStageValue.stageX_absolute = chkbx_stageX.isChecked();

				if (chkbx_stageX.isChecked()) {

					currentStageValue.stageI_absolute = true;
					currentStageValue.stageII_absolute = true;
					currentStageValue.stageIII_absolute = true;
					currentStageValue.stageIV_absolute = true;
					currentStageValue.stageV_absolute = true;
					currentStageValue.stageVI_absolute = true;
					currentStageValue.stageVII_absolute = true;
					currentStageValue.stageVIII_absolute = true;
					currentStageValue.stageIX_absolute = true;
					chkbx_stageVIII.setChecked(true);
					chkbx_stageIX.setChecked(true);

				}
				updateProgressRealtime();
			}
		});

	}

	/*
	*//**
	 * Fetch MPCS Projects
	 */
	/*
	 * private void fetchMPCSProjects() {
	 * 
	 * isFetchProjectService = true;
	 * 
	 * HashMap<String, String> paramsMap = new HashMap<String, String>();
	 * 
	 * volleyTaskManager.doPostFetchProject(paramsMap);
	 * 
	 * }
	 */

	/**
	 * Fetch MPCS Projects Data
	 */
	private void fetchAllMPCSProjectsData(String mpcProjectId) {

		isFetchAllProjectDataService = true;

		HashMap<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put(MPCS_PROJECT_TYPE_ID, mpcProjectId);
		volleyTaskManager.doPostFetchAllProjectData(paramsMap);

	}

	public static void expandTouchArea(final View bigView, final View smallView, final int extraPadding) {
		bigView.post(new Runnable() {
			@Override
			public void run() {
				Rect rect = new Rect();
				smallView.getHitRect(rect);
				rect.top -= extraPadding;
				rect.left -= extraPadding;
				rect.right += extraPadding;
				rect.bottom += extraPadding;
				bigView.setTouchDelegate(new TouchDelegate(rect, smallView));
			}
		});
	}

	/**
	 * Fetch Milestone Data
	 * */
	/*
	 * private void fetchMilestoneData(String date) {
	 * 
	 * //TODO isFetchMilestoneService = true;
	 * 
	 * // SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"); // String
	 * formattedDate = df.format(Calendar.getInstance().getTime());
	 * HashMap<String, String> requestMap = new HashMap<String, String>();
	 * requestMap.put("PROJECT_ID", mpcsProjectId);
	 * //requestMap.put("PROJECT_ID", "45"); requestMap.put("DATE_MODIFIED",
	 * date); volleyTaskManager.doPostFetchMilestone(requestMap); }
	 */

	public void onOptionsClicked(View v) {

		showpopup(v);

	}

	public void onSubmitClick(View v) {

		if (tv_latitude.getText().toString().trim().isEmpty()) {

			Util.showMessageWithOk(NewFormActivity.this, "Please supply your location.");
		}
		/*
		 * else if (!Util.checkConnectivity(mContext)) {
		 * 
		 * Util.showMessageWithOk(NewLandingPageActivity.this,
		 * "No internet connection.");
		 * 
		 * }
		 */

		else if (dropDown_district.getText().toString().trim().isEmpty() || dropDown_block.getText().toString().trim().isEmpty()
				|| dropDown_mpsName.getText().toString().trim().isEmpty()) {

			Util.showMessageWithOk(NewFormActivity.this, "Please supply all fields.");

		}

		/*
		 * else if (!isMilestoneVisible)
		 * 
		 * Util.showMessageWithOk(NewFormActivity.this,
		 * "Please supply milestone");
		 */

		else if (etComments.getText().toString().trim().isEmpty())

			Util.showMessageWithOk(NewFormActivity.this, "Please write a comment.");

		else if (imagesList.size() < 1) {

			Util.showMessageWithOk(NewFormActivity.this, "At least one picture needs to be provided.");

		} else {

			HashMap<String, String> paramsMap = new HashMap<String, String>();
			paramsMap.put("ONGOING_PROJECT_ID", mpcsProjectId);
			paramsMap.put("STAGE1_ABSOLUTE", String.valueOf(currentStageValue.stageI_absolute));
			paramsMap.put("STAGE2_ABSOLUTE", String.valueOf(currentStageValue.stageII_absolute));
			paramsMap.put("STAGE3_ABSOLUTE", String.valueOf(currentStageValue.stageIII_absolute));
			paramsMap.put("STAGE4_ABSOLUTE", String.valueOf(currentStageValue.stageIV_absolute));
			paramsMap.put("STAGE5_ABSOLUTE", String.valueOf(currentStageValue.stageV_absolute));
			paramsMap.put("STAGE6_ABSOLUTE", String.valueOf(currentStageValue.stageVI_absolute));
			paramsMap.put("STAGE7_ABSOLUTE", String.valueOf(currentStageValue.stageVII_absolute));
			paramsMap.put("STAGE8_ABSOLUTE", String.valueOf(currentStageValue.stageVIII_absolute));
			paramsMap.put("STAGE9_ABSOLUTE", String.valueOf(currentStageValue.stageIX_absolute));
			paramsMap.put("STAGE10_ABSOLUTE", String.valueOf(currentStageValue.stageX_absolute));
			//paramsMap.put("STAGE11", currentStageValue.stageXI);
			//paramsMap.put("STAGE11_ABSOLUTE", currentStageValue.stageXI_absolute);
			paramsMap.put("USER_ID", Util.fetchUserClass(mContext).getDatabaseId());
			paramsMap.put("COMMENT", etComments.getText().toString().trim());
			paramsMap.put("LAT", tv_latitude.getText().toString().trim());
			paramsMap.put("LON", tv_longitude.getText().toString().trim());
			paramsMap.put("ISM_HARD_BARRICADE", String.valueOf(hardBarricade));
			paramsMap.put("ISM_PROTECTIVE_HELMETS_SHOES", String.valueOf(protectiveHelmets));
			paramsMap.put("ISM_BARRICADE_OF_SITE", String.valueOf(barricadingOfSite));
			paramsMap.put("ISM_ELECTRICAL_WIRING", String.valueOf(electricWiring));
			paramsMap.put("ISM_HARD_HAT_AND_SHOE", String.valueOf(hardHatShoes));
			paramsMap.put("ISM_FIRE_SAFETY_DEVICES", String.valueOf(fireSafetyDevices));
			paramsMap.put("ILW_LABOURCAMP_SANITATION_WATER_SUPPLY", String.valueOf(labourWelfare));
			paramsMap.put("ILW_LABOURCAMP_STRUCTURE_SATISFACTORY", String.valueOf(labourCampStructure));
			paramsMap.put("ILW_LPG_FOR_LABOUR", String.valueOf(lpg));
			paramsMap.put("ILW_SEPARATORS_TOILETS", String.valueOf(seperateSanitation));
			paramsMap.put("ILW_DRINKING_WATER_FOR_WORKER", String.valueOf(drinkingWater));
			paramsMap.put("ID_MPCS_INFORMATION_BOARD", String.valueOf(mpcsInfo));
			//paramsMap.put("DATE_OF_REPORT", tv_reportDate.getText().toString().trim());
			paramsMap.put("DATE_OF_REPORT", Util.getCurrentFormattedDate());

			allImagesList.addAll(imagesList);
			if (Util.checkConnectivity(mContext)) {
				isSubmitService = true;
				volleyTaskManager.doInsertUpdateMPCSProjectsPhysicalProgress(paramsMap, true);
			} else
				saveOfflineData(paramsMap);
		}

	}

	public void onPictureClick(View v) {

		if (imagesList.size() < 5) {
			final Dialog customDialog = new Dialog(NewFormActivity.this);

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

					mProgressDialog.setCancelable(true);

					showProgressDialog();
					cameraClickedPicture();
				}
			});

			/*
			 * Button btn_album = (Button) view.findViewById(R.id.btn_album);
			 * btn_album.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { customDialog.dismiss();
			 * 
			 * // --> For Album mProgressDialog.setMessage("Please wait...");
			 * mProgressDialog.setCancelable(false); showProgressDialog();
			 * populatingSelectedPic(); } });
			 */

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

			Util.showMessageWithOk(NewFormActivity.this, "Maximum number of images has already been selected!");
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

		Log.v("gallery", "selected from gallery");
		Intent albumIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		albumIntent.setType("image/*");
		startActivityForResult(albumIntent, PICTURE_GALLERY_REQUEST);
	}

	protected void cameraClickedPicture() {

		Log.i("cameraSelectedPic", "");

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "Image File name");
		mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		System.out.println("Action image capture uri: " + mCapturedImageURI.getPath());
		Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
		startActivityForResult(intentPicture, CAMERA_PIC_REQUEST);

	}

	public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {

		hideProgressDialog();

		Log.v("onActivityResult", "" + mCapturedImageURI);

		if (resultCode == RESULT_OK) {

			Uri selectedUri = null;

			switch (requestCode) {
			case CAMERA_PIC_REQUEST:

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
			Toast.makeText(NewFormActivity.this, "No image selected", Toast.LENGTH_LONG).show();

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
			imageClass.setBase64value(Util.getBase64StringFromBitmap(bitmap));
			imageClass.setImageCount(imageCount + 1);
			imagesList.add(imageClass);
			imageCount++;

		} else {

			Toast.makeText(this, picturePath + "not found", Toast.LENGTH_LONG).show();
		}

		imageUpdateOnView();
	}

	private void imageUpdateOnView() {

		ImageAdapter adapter = new ImageAdapter(this, imagesList);
		vp_selectedImages.setAdapter(adapter);

		if (imagesList.size() == 0) {
			vp_selectedImages.setBackgroundResource(R.drawable.default_empty);
		} else {
			vp_selectedImages.setBackgroundColor(Color.parseColor("#D7D7D7"));

			vp_selectedImages.setCurrentItem(imagesList.size() - 1);
		}

		if (imagesList.size() <= 1) {
			tv_imageProgress.setText("[Image added " + imagesList.size() + "/5]");
			v_swipeRight.setVisibility(View.INVISIBLE);
			v_swipeLeft.setVisibility(View.INVISIBLE);
		} else {
			tv_imageProgress.setText("Slide to view other images\n[Images added " + imagesList.size() + "/5]");
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

		if (!isGooglePlayServicesAvailable()) {
			int requestCode = 10;
			int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(NewFormActivity.this);
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, NewFormActivity.this, requestCode);
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

					createLocationRequest();
				} else {
					// Toast.makeText(EntryFormActivity.this,
					// "Location already found.", Toast.LENGTH_SHORT).show();
				}
			}
		}

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

		if (Util.checkConnectivity(mContext)) {
			mProgressDialog.setMessage("Fetching present location...");
			mProgressDialog.setCancelable(true);

			showProgressDialog();
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setNumUpdates(1);
			mLocationRequest.setInterval(5000);
			mLocationRequest.setFastestInterval(1000);
			fusedLocationProviderApi = LocationServices.FusedLocationApi;
			mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
			if (mGoogleApiClient != null) {
				mGoogleApiClient.connect();
			}
		} else {

			//getOfflineLocation();
			//			gps = new GPSTracker(mContext);
			//			gps.getLocation();

			FetchCordinates mtask = new FetchCordinates(mContext);
			mtask.mListener = this;
			mtask.execute();
		}
	}

	/** Gets the current location and update the mobileLocation variable */
	private void getCurrentLocation() {
		locManager = (android.location.LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locListener = new LocationListener() {
			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			public void onProviderEnabled(String provider) {

			}

			public void onProviderDisabled(String provider) {

			}

			public void onLocationChanged(Location mLocation) {

				hideProgressDialog();
				mCurrentLocation = mLocation;
				Log.d("Latitude", "" + mLocation.getLatitude());
				Log.d("Longitude", "" + mLocation.getLongitude());
				updateUI();
			}
		};
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, (android.location.LocationListener) locListener);
	}

	@Override
	public void getLocation(Location mLocation) {
		mCurrentLocation = mLocation;
		Log.d("Latitude", "" + mLocation.getLatitude());
		Log.d("Longitude", "" + mLocation.getLongitude());
		updateUI();

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("EntryFormActivity", "Connection failed: " + connectionResult.toString());
		Toast.makeText(NewFormActivity.this, "Connection failed: " + connectionResult.toString(), Toast.LENGTH_LONG).show();
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

	}

	@Override
	public void onLocationChanged(Location location) {

		hideProgressDialog();
		Log.d("EntryFormActivity", "Firing onLocationChanged..............................................");
		mCurrentLocation = location;
		// mLastUpdateTime = postFormater.format(new Date());
		updateUI();
	}

	@SuppressLint("SimpleDateFormat")
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

	/**
	 * Post image with respect to position
	 * */
	private void sendingImages(final int position) {

		imagePosition = position;
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("Mode", "ad");
		paramsMap.put("Id", ImageID);
		paramsMap.put("ImageNo", String.valueOf(allImagesList.get(position).getImageCount()));

		Log.d("JSON", "" + new JSONObject(paramsMap));
		paramsMap.put("ImageData", allImagesList.get(position).getBase64value());
		System.out.println("Image No or " + String.valueOf(allImagesList.get(position).getImageCount()));

		isImageService = true;
		hideProgressDialog();
		mProgressDialog.setMessage("Please wait...");
		showProgressDialog();

		volleyTaskManager.doPostImage(paramsMap, true);
	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {

		Log.v("resultJsonObject", resultJsonObject.toString());

		if (isFetchProjectService) {

			isFetchProjectService = false;
			parseMPCSProjects(resultJsonObject);

		} else if (isFetchProjectDetailsService) {
			isFetchProjectDetailsService = false;
			parseProjectDetails(resultJsonObject);

		}

		else if (isFetchAllProjectDataService) {

			isFetchAllProjectDataService = false;
			parseAllProjectData(resultJsonObject);
			// parseStages(resultJsonObject);

		} else if (isHistoryTimelineService) {

			isHistoryTimelineService = false;
			Log.v("resultJsonObject", resultJsonObject.toString());
			parseHistoryTimelineService(resultJsonObject);

		} else if (isFetchMilestoneService) {

			isFetchMilestoneService = false;
			Log.v("resultJsonObject", resultJsonObject.toString());
			parseMilestoneService(resultJsonObject);

		} else if (isSubmitService) {

			isSubmitService = false;
			parseSubmitService(resultJsonObject);

		} else if (isImageService) {

			isImageService = false;
			Log.d("onSuccess Sent position: ", imagePosition + "");
			Log.d("onSuccess Image Array size: :", allImagesList.size() + "");
			// TODO After Image Posting
			String FDSaveImageDataResult = resultJsonObject.optString("FDSaveImageDataResult");

			System.out.println("FDSaveImageDataResult: " + FDSaveImageDataResult);
			if (FDSaveImageDataResult.equalsIgnoreCase("1"))

				allImagesList.get(imagePosition).setIsPosted(true);

			if (imagePosition < allImagesList.size() - 1) {

				sendingImages(imagePosition + 1);
			} else {
				hideProgressDialog();
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
		} else if (isFeedBackService) {
			isFeedBackService = false;

			String result = resultJsonObject.optString("RES");
			if (result.equalsIgnoreCase("1")) {
				Util.showSuccessMessageWithOk(mContext, "Thank you for your valuable feedback...", new AlertDialogCallBack() {
					@Override
					public void onSubmit() {

					}

					@Override
					public void onCancel() {

					}
				});
			}

			customDialog.dismiss();

		} else if (isChangePasswordService) {

			isChangePasswordService = false;
			String result = resultJsonObject.optString("RES");
			if (result.equalsIgnoreCase("1")) {
				UserClass user = Util.fetchUserClass(mContext);
				user.setPassword(changedPassword);
				Util.saveUserClass(mContext, user);
				Util.showSuccessMessageWithOk(mContext, "Your password has been changed successfully.", new AlertDialogCallBack() {
					@Override
					public void onSubmit() {

					}

					@Override
					public void onCancel() {

					}
				});
			} else
				Util.showMessageWithOk(mContext, "Change password failed...");

			customDialog.dismiss();
		}

		volleyTaskManager.hideProgressDialog();
	}

	@Override
	public void onSuccess(String resultJsonObject) {

	}

	@Override
	public void onError() {

	}

	// -----------------------PARSING

	private void parseMPCSProjects(JSONObject resultJsonObject) {

		JSONArray projectArray = resultJsonObject.optJSONArray("GetProjectTypesResult");
		for (int i = 0; i < projectArray.length(); i++) {
			JSONObject districtJsonObject = projectArray.optJSONObject(i);
			Project project = new Project();
			project.projectType = districtJsonObject.optString("MPCS_PROJECT_TYPE");
			project.projectId = districtJsonObject.optString("MPCS_PROJECT_TYPE_ID");
			projectList.add(project);
		}
		saveProjectData(projectList);

	}

	private void parseProjectDetails(JSONObject resultJsonObject) {

		JSONArray projectArray = resultJsonObject.optJSONArray("GetProjectFinanceInfoResult");
		ArrayList<FinancialInfo> arrayList = new ArrayList<FinancialInfo>();
		for (int i = 0; i < projectArray.length(); i++) {
			JSONObject financialInfoObject = projectArray.optJSONObject(i);
			FinancialInfo project = new FinancialInfo();
			project.setCOLUMN_NAME(financialInfoObject.optString("COLUMN_NAME"));
			project.setCOLUMN_VALUE(financialInfoObject.optString("COLUMN_VALUE"));
			project.setPROJECT_TYPE_ID(financialInfoObject.optString("PROJECT_TYPE_ID"));
			project.setSLNO(financialInfoObject.optString("SLNO"));
			arrayList.add(project);
		}

		PopUpView popUpView = new PopUpView(popupWindow, mContext, arrayList);
		popUpView.showDialogView();

	}

	private void parseAllProjectData(JSONObject resultJsonObject) {

		System.out.println("Parse data------------------------------------------------");
		JSONArray districtArray = resultJsonObject.optJSONArray("objDistList");
		JSONArray blockArray = resultJsonObject.optJSONArray("objBlockList");
		JSONArray mpcsProjArray = resultJsonObject.optJSONArray("objProjectList");

		System.out.println("District array length: " + districtArray.length());

		for (int i = 0; i < districtArray.length(); i++) {

			JSONObject districtJsonObject = districtArray.optJSONObject(i);
			District district = new District();
			district.DIS_ID = districtJsonObject.optString("DIS_ID");
			district.DIS_NAME = districtJsonObject.optString("DIS_NAME");
			district.PROJECT_TYPE = districtJsonObject.optString("PROJECT_TYPE");
			districtList.add(district);

		}

		for (int i = 0; i < blockArray.length(); i++) {

			JSONObject blockJsonObject = blockArray.optJSONObject(i);
			Block block = new Block();
			block.BLOCK_ID = blockJsonObject.optString("BLOCK_ID");
			block.BLOCK_NAME = blockJsonObject.optString("BLOCK_NAME");
			block.DIST_ID = blockJsonObject.optString("DIST_ID");
			block.PROJECT_TYPE = blockJsonObject.optString("PROJECT_TYPE").trim();
			blockList.add(block);
		}

		for (int i = 0; i < mpcsProjArray.length(); i++) {
			JSONObject mpcsProjJsonObject = mpcsProjArray.optJSONObject(i);
			MPCSProject mpcsProject = new MPCSProject();
			mpcsProject.id = mpcsProjJsonObject.optString("ID");
			mpcsProject.blockId = mpcsProjJsonObject.optString("BLOCK_ID");
			mpcsProject.districtId = mpcsProjJsonObject.optString("DISTRICT_ID");
			mpcsProject.mpcspProject = mpcsProjJsonObject.optString("MPCSP_PROJECT");
			mpcsProject.packageNo = mpcsProjJsonObject.optString("PACKAGE_NO");
			mpcsProject.projectType = mpcsProjJsonObject.optString("PROJECT_TYPE").trim();
			mpcsProject.dateTargetCompletion = mpcsProjJsonObject.optString("TAGET_COMPLETION_DATE");
			//mpcsProject.dateOfReport = mpcsProjJsonObject.optString("DATE_OF_REPORT");
			mpcsProject.contractAmount = mpcsProjJsonObject.optString("CONTRACT_AMOUNT");
			mpcsProjList.add(mpcsProject);
		}

		//TODO- Remove Timeline Service
		//getTimeLine();

		saveData(districtList, blockList, mpcsProjList);
	}

	/*
	 * private void getTimeLine() {
	 * 
	 * previousDateList.clear(); isHistoryTimelineService = true;
	 * HashMap<String, String> requestMap = new HashMap<String, String>();
	 * requestMap.put("PROJECT_ID", mpcsProjectId);
	 * //requestMap.put("PROJECT_ID", "45");
	 * volleyTaskManager.doPostFetchTimeLine(requestMap); }
	 */

	// =============== @ POPULATE DROPDOWN @ =======
	private void populateProjectDropdown() {

		projectList.clear();
		projectList = new ProjectDB().getMPCSProjects(mContext);

		dropDown_mpsProject.setText("");
		dropDown_mpsProject.setHint("");

		if (projectList.size() > 0) {

			dropDown_mpsProject.setEnabled(true);
			dropDown_mpsProject.setHint(" Select ");
			String[] array = new String[projectList.size()];
			int index = 0;
			for (Project value : projectList) {
				array[index] = (String) value.projectType;
				index++;
			}
			dropDown_mpsProject.setItems(array);

		} else {

			dropDown_mpsProject.setEnabled(false);
			bt_financial_info.setVisibility(View.INVISIBLE);
		}
	}

	private void populateDistrictDropdown() {

		districtList.clear();
		districtList = new DistrictDB().getDistrict(mContext, projectType);
		System.out.println("Size of District: " + districtList.size());
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

	}

	private void populateBlockDropdown(String districtId) {

		blockList.clear();
		// Fetch Blocks with respect to District ID
		blockList = new BlockDB().getBlocks(NewFormActivity.this, districtId, projectType);
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

	private void populateMPCSDropdown(String districtId, String blockId, String projectType) {

		mpcsProjList.clear();
		Log.d("populateMPCSDropdown", "Size after clear: " + mpcsProjList.size());
		// Fetch MPCS PROJECT with respect to District ID and Block ID
		mpcsProjList = new MPCSProjectDB().getMPCSProjects(mContext, districtId, blockId, projectType);
		dropDown_mpsName.setText("");
		dropDown_mpsName.setHint("");
		Log.v("populateMPCSDropdown", "Size: " + mpcsProjList.size());

		if (mpcsProjList.size() > 0) {

			dropDown_mpsName.setEnabled(true);
			dropDown_mpsName.setHint(" Select ");
			String[] array = new String[mpcsProjList.size()];
			int index = 0;
			for (MPCSProject value : mpcsProjList) {
				array[index] = (String) value.mpcspProject;
				index++;
			}
			dropDown_mpsName.setItems(array);

		} else {
			dropDown_mpsName.setEnabled(false);
		}

	}

	private void populateMilestoneDropdown() {

		dropDown_milestone.setText("");
		dropDown_milestone.setHint(" select ");

		dropDown_milestone.setEnabled(true);
		dropDown_milestone.setHint(" Select ");
		String[] array = getResources().getStringArray(R.array.milestones);

		dropDown_milestone.setItems(array);

	}

	private void populatePreviousDateDropdown() {

		/*
		 * dropDown_previousVisit.setText("");
		 * dropDown_previousVisit.setHint("");
		 * 
		 * if (previousDateList.size() > 0) {
		 * 
		 * dropDown_previousVisit.setEnabled(true);
		 * dropDown_previousVisit.setHint(" Select "); String[] array = new
		 * String[previousDateList.size()]; int index = 0; for (Milestone value
		 * : previousDateList) { array[index] = (String)
		 * value.projectSurveyPreviousDate; index++; }
		 * dropDown_previousVisit.setItems(array);
		 * 
		 * } else { dropDown_previousVisit.setEnabled(false); }
		 */

	}

	private void parseHistoryTimelineService(JSONObject resultJsonObject) {

		JSONObject projectObject = resultJsonObject.optJSONObject("GetProjectSurveyPreviousDatesResult");
		//packageContractAmount = projectObject.optString("PACKAGE_CONTRACT_AMOUNT");
		JSONArray surveyDateArray = projectObject.optJSONArray("proSurveyLst");
		try {
			for (int i = 0; i < surveyDateArray.length(); i++) {
				JSONObject districtJsonObject = surveyDateArray.optJSONObject(i);
				Milestone milestone = new Milestone();
				milestone.projectSurveyPreviousDate = districtJsonObject.optString("SURVEY_DATE");

				previousDateList.add(milestone);

			}
			populatePreviousDateDropdown();
		} catch (Exception e) {
			e.printStackTrace();
			//this.previousStageValue = new Stages();
			this.currentStageValue = new Stages();// initially both the current and previous values will be same

			tv_cumulative_physical_prog.setText("0.0");

		}
	}

	private void parseMilestoneService(JSONObject resultJsonObject) {
		Stages stages = new Stages();
		JSONObject stageObject = resultJsonObject.optJSONObject("GetProjectPhysicalProgressLogResult");
		try {
			stages.stageI = stageObject.optString("STAGE1");
			stages.stageII = stageObject.optString("STAGE2");
			stages.stageIII = stageObject.optString("STAGE3");
			stages.stageIV = stageObject.optString("STAGE4");
			stages.stageV = stageObject.optString("STAGE5");
			stages.stageVI = stageObject.optString("STAGE6");
			stages.stageVII = stageObject.optString("STAGE7");
			stages.stageVIII = stageObject.optString("STAGE8");
			stages.stageIX = stageObject.optString("STAGE9");
			stages.stageX = stageObject.optString("STAGE10");
			//stages.stageXI = stageObject.optString("STAGE11");
			stages.isAllValuesFetched = true;

			stages.stageI_absolute = Boolean.parseBoolean(stageObject.optString("STAGE1_ABSOLUTE"));
			stages.stageII_absolute = Boolean.parseBoolean(stageObject.optString("STAGE2_ABSOLUTE"));
			stages.stageIII_absolute = Boolean.parseBoolean(stageObject.optString("STAGE3_ABSOLUTE"));
			stages.stageIV_absolute = Boolean.parseBoolean(stageObject.optString("STAGE4_ABSOLUTE"));
			stages.stageV_absolute = Boolean.parseBoolean(stageObject.optString("STAGE5_ABSOLUTE"));
			stages.stageVI_absolute = Boolean.parseBoolean(stageObject.optString("STAGE6_ABSOLUTE"));
			stages.stageVII_absolute = Boolean.parseBoolean(stageObject.optString("STAGE7_ABSOLUTE"));
			stages.stageVIII_absolute = Boolean.parseBoolean(stageObject.optString("STAGE8_ABSOLUTE"));
			stages.stageIX_absolute = Boolean.parseBoolean(stageObject.optString("STAGE9_ABSOLUTE"));
			stages.stageX_absolute = Boolean.parseBoolean(stageObject.optString("STAGE10_ABSOLUTE"));
			//stages.stageXI_absolute = Boolean.parseBoolean(stageObject.optString("STAGE11_ABSOLUTE"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		//TODO-- Un-comment here for implementation of previous value
		//		this.previousStageValue = stages;
		//		this.currentStageValue = stages;// initially both the current and previous values will be same

		//TODO- Calculate cumulative progress
		/*
		 * String cumulativePhysicalProgress =
		 * Util.calculateCumulativePhysicalProgress(stages);
		 * 
		 * tv_cumulative_physical_prog.setText(cumulativePhysicalProgress);
		 */

	}

	private void parseSubmitService(JSONObject resultJsonObject) {

		Log.d("parseSubmitService", "" + resultJsonObject);

		if (resultJsonObject.toString() != null && !resultJsonObject.toString().trim().isEmpty()) {

			try {

				String result = "";

				result = resultJsonObject.optString("RES");
				ImageID = resultJsonObject.optString("ID");

				Log.v("TAG", "" + result);

				if (result.equalsIgnoreCase("1")) {

					if (imagesList.size() > 0) {
						sendingImages(0);
					} else {
						Util.showCallBackMessageWithOk(NewFormActivity.this, "Data Posted successfully.", new AlertDialogCallBack() {

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

	// ============= @ SAVE DATA IN DATABASE @ ==========

	private void saveProjectData(ArrayList<Project> projects) {

		for (int i = 0; i < projects.size(); i++) {

			ContentValues cv = new ContentValues();
			cv.put(DBConstants.PROJECT_ID, projects.get(i).projectId);
			cv.put(DBConstants.PROJECT_TYPE, projects.get(i).projectType);
			new ProjectDB().saveMPCSProjects(NewFormActivity.this, cv);
		}
		populateProjectDropdown();
	}

	private void saveData(ArrayList<District> district, ArrayList<Block> block, ArrayList<MPCSProject> mpcsProject) {

		Log.v("District Offline", "District Array Size: " + district.size());
		for (int i = 0; i < district.size(); i++) {

			ContentValues cv = new ContentValues();
			cv.put(DBConstants.DISTRICT_ID, district.get(i).DIS_ID);
			cv.put(DBConstants.DISTRICT_NAME, district.get(i).DIS_NAME);
			cv.put(DBConstants.DISTRICT_PROJ_TYPE, district.get(i).PROJECT_TYPE);
			new DistrictDB().saveDistrictData(NewFormActivity.this, cv);
		}

		for (int i = 0; i < block.size(); i++) {

			ContentValues cv = new ContentValues();
			cv.put(DBConstants.BLOCK_ID, block.get(i).BLOCK_ID);
			cv.put(DBConstants.BLOCK_NAME, block.get(i).BLOCK_NAME);
			cv.put(DBConstants.BLOCK_DISTRICT_ID, block.get(i).DIST_ID);
			cv.put(DBConstants.BLOCK_PROJ_TYPE, block.get(i).PROJECT_TYPE);
			new BlockDB().saveBlockData(NewFormActivity.this, cv);
		}

		for (int i = 0; i < mpcsProjList.size(); i++) {

			ContentValues cv = new ContentValues();
			cv.put(DBConstants.MPCS_BLOCK_ID, mpcsProject.get(i).blockId);
			cv.put(DBConstants.MPCS_DIST_ID, mpcsProject.get(i).districtId);
			cv.put(DBConstants.MPCS_PROJ_ID, mpcsProject.get(i).id);
			cv.put(DBConstants.MPCS_PROJ_NAME, mpcsProject.get(i).mpcspProject);
			cv.put(DBConstants.MPCS_PROJ_PACKAGE_NUMBER, mpcsProject.get(i).packageNo);
			cv.put(DBConstants.MPCS_PROJ_TYPE, mpcsProject.get(i).projectType);
			cv.put(DBConstants.MPCS_PROJ_TARGET_COMPLETION_DATE, mpcsProject.get(i).dateTargetCompletion);
			cv.put(DBConstants.MPCS_PROJ_DATE_OF_REPORT, mpcsProject.get(i).dateOfReport);
			cv.put(DBConstants.MPCS_PROJ_CONTRACT_AMOUNT, mpcsProject.get(i).contractAmount);
			new MPCSProjectDB().saveMPCSProjData(mContext, cv);

		}

		populateDistrictDropdown();

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
		Util.trimCache(NewFormActivity.this);
	}

	private void showpopup(View v) {

		if (popupWindow == null || !popupWindow.isShowing()) {
			LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			View popupView = layoutInflater.inflate(R.layout.options_menu, null);
			popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			//popupWindow.setBackgroundDrawable(new ColorDrawable());
			FrameLayout fl_popup = (FrameLayout) popupView.findViewById(R.id.mainmenu);
			fl_popup.getForeground().setAlpha(0);

			Button btn_reload = (Button) popupView.findViewById(R.id.btn_reload);
			Button btn_sync = (Button) popupView.findViewById(R.id.btn_sync);
			Button btn_feedback = (Button) popupView.findViewById(R.id.btn_feedback);
			Button btn_change_password = (Button) popupView.findViewById(R.id.btn_change_password);
			Button btn_logout = (Button) popupView.findViewById(R.id.btn_options);

			btn_reload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					Util.showCallBackMessageWithOkCancel(mContext, "Do you want to reload?", new AlertDialogCallBack() {
						@Override
						public void onSubmit() {
							onCreate(null);
						}

						@Override
						public void onCancel() {
						}
					});
				}
			});

			btn_sync.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();

					showOfflineUploadView();
					/*if (Util.checkConnectivity(mContext))
						syncOfflineData();
					else
						Toast.makeText(mContext, "No Internet Access.", Toast.LENGTH_SHORT).show();*/

				}
			});

			btn_feedback.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					showFeedbackWindow();
				}
			});

			btn_change_password.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					showChangePasswordDialog();

				}
			});

			btn_logout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					startActivity(new Intent(mContext, LoginActivity.class));
					finish();
				}
			});

			/*	if (dataSets == null) {
					btn_sync.setBackgroundResource(R.drawable.grey_bg);
					btn_sync.setEnabled(false);
					btn_sync.setText("Sync");
				} else {
					if (dataSets.size() == 0) {
						btn_sync.setBackgroundResource(R.drawable.grey_bg);
						btn_sync.setEnabled(false);
						btn_sync.setText("Sync");
					} else {
						btn_sync.setBackgroundResource(R.drawable.green_btn_selector);
						btn_sync.setEnabled(true);
						btn_sync.setText("Sync	 -----------> (" + dataSets.size() + ")");
					}
				}*/

			if (offlinedatasize == 0) {
				btn_sync.setBackgroundResource(R.drawable.grey_bg);
				btn_sync.setEnabled(false);
				btn_sync.setText("Sync");
			} else {
				btn_sync.setBackgroundResource(R.drawable.green_btn_selector);
				btn_sync.setEnabled(true);
				btn_sync.setText("Sync	 -----------> (" + offlinedatasize + ")");
			}

			popupWindow.setOutsideTouchable(false);
			popupWindow.showAsDropDown(v, 100, 0);

		} else {

			popupWindow.dismiss();

		}
	}

	private void showFeedbackWindow() {

		customDialog = new Dialog(mContext);

		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.dialog_feedback, null);

		ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				customDialog.dismiss();

			}
		});

		final EditText et_feedback = (EditText) view.findViewById(R.id.et_feedback);

		Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!(et_feedback.getText().toString().trim().isEmpty())) {

					HashMap<String, String> requestMap = new HashMap<String, String>();
					requestMap.put("USER_ID", Util.fetchUserClass(mContext).getDatabaseId());
					requestMap.put("FEEDBACK", et_feedback.getText().toString().trim());
					requestMap.put("SEND_DEVICE", "2");

					isFeedBackService = true;
					volleyTaskManager.doPostFeedback(requestMap, true);
				} else {
					Toast.makeText(mContext, "Please enter your feed back.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		customDialog.setCancelable(false);
		customDialog.setContentView(view);
		customDialog.setCanceledOnTouchOutside(false);
		// Start AlertDialog
		customDialog.show();

	}

	private void showChangePasswordDialog() {

		customDialog = new Dialog(mContext);

		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.dialog_change_password, null);

		ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				customDialog.dismiss();

			}
		});
		final EditText et_old_password = (EditText) view.findViewById(R.id.et_old_password);
		final EditText et_new_password = (EditText) view.findViewById(R.id.et_new_password);
		final EditText et_confirm_password = (EditText) view.findViewById(R.id.et_confirm_password);
		Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String oldPassword = et_old_password.getText().toString().trim();
				String newPassword = et_new_password.getText().toString().trim();
				String confirmPassword = et_confirm_password.getText().toString().trim();
				if (oldPassword.isEmpty()) {
					Toast.makeText(mContext, "Please enter old password.", Toast.LENGTH_SHORT).show();
				} else if (newPassword.isEmpty()) {
					Toast.makeText(mContext, "Please enter new password.", Toast.LENGTH_SHORT).show();
				} else if (oldPassword.equalsIgnoreCase(newPassword))
					Toast.makeText(mContext, "New password is same as the old password.", Toast.LENGTH_SHORT).show();
				else if (!newPassword.equalsIgnoreCase(confirmPassword)) {
					Toast.makeText(mContext, "The confirm new password does not match the new password.", Toast.LENGTH_SHORT).show();
				} else {
					HashMap<String, String> requestMap = new HashMap<String, String>();
					requestMap.put("USER_ID", Util.fetchUserClass(mContext).getDatabaseId());
					requestMap.put("NEW_PASSWORD", newPassword);
					requestMap.put("OLD_PASSWORD", oldPassword);
					isChangePasswordService = true;
					changedPassword = newPassword;
					volleyTaskManager.doPostChangePassword(requestMap, true);

				}
			}
		});
		customDialog.setCancelable(false);
		customDialog.setContentView(view);
		customDialog.setCanceledOnTouchOutside(false);
		// Start AlertDialog
		customDialog.show();
	}

	/** Method that clears the form */
	private void clearForm() {

		imageCount = 0;
		bt_financial_info.setVisibility(View.INVISIBLE);

		dropDown_mpsProject.setText("");
		dropDown_district.setText("");
		dropDown_block.setText("");
		dropDown_mpsName.setText("");
		dropDown_milestone.setText("");
		//dropDown_previousVisit.setText("");

		dropDown_mpsProject.setHint(" select ");
		dropDown_district.setHint("");
		dropDown_block.setHint("");
		dropDown_milestone.setHint(" select ");
		dropDown_mpsName.setHint("");
		//dropDown_previousVisit.setHint("");

		dropDown_block.setEnabled(false);
		dropDown_mpsName.setEnabled(false);
		//dropDown_previousVisit.setEnabled(false);
		dropDown_district.setEnabled(false);
		etComments.setText("");

		tv_imageProgress.setText("[Image added 0/3]");
		imagesList.clear();
		allImagesList.clear();
		previousDateList.clear();
		resetEmpEsmf();

		tv_cumulative_physical_prog.setText("0.0");
		resetMilestone();
		hideMilestone();

		imageUpdateOnView();
		tv_latitude.setText("");
		tv_longitude.setText("");
		llMilestoneView.removeAllViews();
		tv_packageNumber.setText("");
		tv_target_completionDate.setText("");
		//tv_reportDate.setText("");
		tv_contractAmount.setText("");

	}

	private void resetMilestone() {

		currentStageValue = new Stages();
		//previousStageValue = new Stages();
		tv_cumulative_physical_prog.setText("0.0");
		tv_contractAmount.setText("0.0");

	}

	/**
	 * 1.Resets the dropdowns and other UI data on MPCS Project selection
	 * */
	private void resetOnMPCSProjectSelection() {

		//new DistrictDB().clearAllDBTables(mContext);
		districtList.clear();
		previousDateList.clear();
		resetEmpEsmf();
		hideMilestone();
		// Disable remaining drop-downs
		dropDown_block.setText("");
		dropDown_block.setHint("");
		dropDown_block.setEnabled(false);
		dropDown_mpsName.setText("");
		dropDown_mpsName.setHint("");
		dropDown_mpsName.setEnabled(false);
		resetOnBlockSelection();
		llMilestoneView.setVisibility(View.GONE);
	}

	/**
	 * 2.Resets the dropdowns and other UI data on District Selection
	 * */
	private void resetOnDistrictSelection() {

		tv_packageNumber.setText("");
		resetEmpEsmf();
		hideMilestone();
		// Disable remaining dropdowns
		dropDown_mpsName.setText("");
		dropDown_mpsName.setHint("");
		dropDown_mpsName.setEnabled(false);
		tv_packageNumber.setText("");
	}

	/**
	 * 3.Resets the dropdowns and other UI data on Block Selection
	 * */
	private void resetOnBlockSelection() {

		tv_packageNumber.setText("");

		//		dropDown_previousVisit.setEnabled(false);
		//		dropDown_previousVisit.setText("");
		//		dropDown_previousVisit.setHint("");
		tv_target_completionDate.setText("");
		tv_contractAmount.setText("");
		//tv_reportDate.setText("");
		etComments.setText("");
		resetEmpEsmf();
		hideMilestone();
	}

	/**
	 * 4.Resets the dropdowns and other UI data on MPCS Name Selection
	 * */
	private void resetOnMPCSNameSelection() {

		resetEmpEsmf();
		dropDown_milestone.setText("");
		dropDown_milestone.setHint(" select ");
	}

	/**
	 * 5.Resets the dropdowns and other UI data on Previous Date Selection
	 * */
	/*
	 * private void resetOnPreviousDateSelection() {
	 * 
	 * hideMilestone(); dropDown_milestone.setText("");
	 * dropDown_milestone.setHint(" select "); }
	 */

	/**
	 * Overrides the default implementation for KeyEvent.KEYCODE_BACK so that
	 * all systems call onBackPressed().
	 */
	@Override
	public void onBackPressed() {

		if (isTaskRoot()) {
			if (back_pressed + 2000 > System.currentTimeMillis())
				super.onBackPressed();
			else
				Toast.makeText(mContext, "Press once again to exit!", Toast.LENGTH_SHORT).show();

			back_pressed = System.currentTimeMillis();
		} else {
			super.onBackPressed();
		}
	}

	private void updateProgressRealtime() {
		//TODO Calculate Cumulative progress
		String cumulativePhysicalProgress = Util.calculateCumulativePhysicalProgress(currentStageValue);
		tv_cumulative_physical_prog.setText(cumulativePhysicalProgress);
	}

	private void saveOfflineData(HashMap<String, String> offlineMap) {

		OfflineDataSet offlineDataSet = new OfflineDataSet();
		offlineDataSet.setParamsMap(offlineMap);

		if (imagesList.size() > 0)
			offlineDataSet.setImagesList(allImagesList);

		ArrayList<OfflineDataSet> offlineDataSetList = Util.fetchOfflineDataSetList(NewFormActivity.this);

		if (offlineDataSetList == null) {
			offlineDataSetList = new ArrayList<OfflineDataSet>();
		}

		offlineDataSetList.add(offlineDataSet);
		Util.saveOfflineDataSetList(NewFormActivity.this, offlineDataSetList);

		Util.showCallBackMessageWithOk(NewFormActivity.this, getString(R.string.offline_storage), new AlertDialogCallBack() {
			@Override
			public void onSubmit() {
				offlinedatasize = Util.fetchOfflineDataSetList(mContext).size();
				clearForm();
			}

			@Override
			public void onCancel() {
			}
		});
	}

	/**
	 * The Offline Sync Button posts the existing offline form data.
	 */
	private void syncOfflineData() {

		if (Util.checkConnectivity(mContext)) {
			mConnectionStateActivity = new CheckNetworkQuality(mContext);
			mConnectionStateActivity.registerListener();

			mConnectionStateActivity.checkConnectionSpeed();

			mConnectionStateActivity.networkListener = NewFormActivity.this;
			mProgressDialog.setMessage("Checking internet speed...");
			showProgressDialog();

		} else
			Util.showMessageWithOk(mContext, "No internet connection.");

	}

	// --> For sending offline data
	private class DataSyncReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("DataSyncReceiver", "DataSyncReceiver");

			hidefflineSyncProgress();
			offlineDataSyncProgressDialog.dismiss();

			boolean isDataSynced = intent.getBooleanExtra("DATA_SYNCED", false);

			if (isDataSynced) {
				Util.showMessageWithOk(mContext, intent.getStringExtra("MSG"));
				if (popupWindow.isShowing())
					popupWindow.dismiss();
			} else {
				Util.showMessageWithOk(mContext, intent.getStringExtra("MSG"));
			}

			unregisterReceiver(dataSyncReceiver);

		}
	}

	@Override
	public void getConnectionType(boolean isStrongConnection) {
		hideProgressDialog();
		if (isStrongConnection) {

			Toast.makeText(mContext, "Strong Connection", Toast.LENGTH_SHORT).show();
			runDataSync();

		}

		else {
			isDataSync = false;
			Toast.makeText(mContext, "Poor Connection", Toast.LENGTH_SHORT).show();

			Util.showMessageWithOk(NewFormActivity.this, "Your internet signal is weak!\nPlease try again later.");
		}

	}

	private void runDataSync() {

		offlineDataSyncProgressDialog = new ProgressDialog(this);
		offlineDataSyncProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		offlineDataSyncProgressDialog.setCancelable(false);
		offlineDataSyncProgressDialog.setCanceledOnTouchOutside(false);
		offlineDataSyncProgressDialog.setMessage("Syncing offline Data...");
		showOfflineSyncProgress();

		isDataSync = false;
		dataSyncReceiver = new DataSyncReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DataSyncService.DATASYNC_ACTION);
		registerReceiver(dataSyncReceiver, intentFilter);

		startService(new Intent(NewFormActivity.this, DataSyncService.class));

	}

	private void showOfflineSyncProgress() {
		if (!offlineDataSyncProgressDialog.isShowing())
			offlineDataSyncProgressDialog.show();

	}

	private void hidefflineSyncProgress() {

		if (mProgressDialog.isShowing())
			mProgressDialog.dismiss();

	}

	// Solution when the app-crashes when image uri is not found.
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mCapturedImageURI != null) {
			outState.putString("cameraImageUri", mCapturedImageURI.toString());
			Log.d("onSaveInstanceState", "mCapturedImageURI: " + mCapturedImageURI);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("cameraImageUri")) {
			mCapturedImageURI = Uri.parse(savedInstanceState.getString("cameraImageUri"));
			Log.d("onRestoreInstanceState", "mCapturedImageURI: " + mCapturedImageURI);
		}
	}

	private ProgressBar mProgress;
	private int mProgressStatus = 0;
	private Handler mHandler = new Handler();

	private void showProgress() {
		mProgress = (ProgressBar) findViewById(R.id.linear_progress_bar);

		// Start lengthy operation in a background thread
		new Thread(new Runnable() {
			public void run() {
				while (mProgressStatus < 100) {
					SystemClock.sleep(20);
					mProgressStatus += 1;
					// Update the progress bar
					mHandler.post(new Runnable() {
						public void run() {

							mProgress.setProgress(mProgressStatus);
						}
					});
				}
			}
		}).start();
	}

	private void showOfflineUploadView() {

		final Dialog customDialog = new Dialog(mContext);
		customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.WHITE));

		LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

		View view = layoutInflater.inflate(R.layout.dialog_offline_sync, null);

		ListView listView = (ListView) view.findViewById(R.id.lv_offline_data);

		List<DownloadInfo> downloadInfo = new ArrayList<DownloadInfo>();
		for (int i = 0; i < 10; ++i) {
			
			downloadInfo.add(new DownloadInfo("File " + i, 10));
		}

		listView.setAdapter(new DownloadInfoArrayAdapter(getApplicationContext(), R.id.lv_offline_data, downloadInfo));
		/*mProgress = (ProgressBar) view.findViewById(R.id.linear_progress_bar);

		// Start lengthy operation in a background thread
		new Thread(new Runnable() {
			public void run() {
				while (mProgressStatus < 100) {
					SystemClock.sleep(20);
					mProgressStatus += 1;
					// Update the progress bar
					mHandler.post(new Runnable() {
						public void run() {

							mProgress.setProgress(mProgressStatus);
						}
					});
				}
			}
		}).start();*/
		ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				customDialog.dismiss();
				mProgressStatus = 0;
			}
		});

		customDialog.setCancelable(false);
		customDialog.setContentView(view);
		customDialog.setCanceledOnTouchOutside(false);

		// Start AlertDialog
		//customDialog.show();

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(customDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		customDialog.show();
		customDialog.getWindow().setAttributes(lp);

	}

}
