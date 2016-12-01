package com.cyberswift.wbdisastermanagement.util;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cyberswift.wbdisastermanagement.R;
import com.cyberswift.wbdisastermanagement.model.FinancialInfo;

public class PopUpView {

	private Context mContext;

	private PopupWindow popupWindow;

	private ArrayList<FinancialInfo> financialInfoList;

	public PopUpView(PopupWindow popupWindow, Context mContext, ArrayList<FinancialInfo> financialInfoList) {

		this.mContext = mContext;

		this.popupWindow = popupWindow;

		this.financialInfoList = financialInfoList;

	}

	public void showDialogView() {

		final Dialog customDialog = new Dialog(mContext);

		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.popup_project_description, null);

		ImageButton ib_closeWindow = (ImageButton) view.findViewById(R.id.ib_closeWindow);
		ib_closeWindow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				customDialog.dismiss();
			}
		});

		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		TextView tvProjectCost = (TextView) view.findViewById(R.id.tvProjectCost);
		TextView tv_fund_received_during_theyear = (TextView) view.findViewById(R.id.tv_fund_received_during_theyear);
		TextView tvCumulativeFund_received_uptoDate = (TextView) view.findViewById(R.id.tvCumulativeFund_received_uptoDate);
		TextView tv_expenditure_uptoDate = (TextView) view.findViewById(R.id.tv_expenditure_uptoDate);
		TextView tv_fundutilized_uptoDate = (TextView) view.findViewById(R.id.tv_fundutilized_uptoDate);
		TextView tv_referenceto_allotment_order = (TextView) view.findViewById(R.id.tv_referenceto_allotment_order);
		TextView tv_cumulative_fund_releasedto_pwd = (TextView) view.findViewById(R.id.tv_cumulative_fund_releasedto_pwd);
		TextView tv_cumulative_fund_available = (TextView) view.findViewById(R.id.tv_cumulative_fund_available);
		TextView tv_gross_expenditure_incurred_uptoDate = (TextView) view.findViewById(R.id.tv_gross_expenditure_incurred_uptoDate);

		tv_fund_received_during_theyear.setText(financialInfoList.get(0).getCOLUMN_VALUE());
		tvCumulativeFund_received_uptoDate.setText(financialInfoList.get(1).getCOLUMN_VALUE());
		tv_expenditure_uptoDate.setText(financialInfoList.get(2).getCOLUMN_VALUE());
		tv_fundutilized_uptoDate.setText(financialInfoList.get(3).getCOLUMN_VALUE());
		tv_referenceto_allotment_order.setText(financialInfoList.get(4).getCOLUMN_VALUE());
		tv_cumulative_fund_releasedto_pwd.setText(financialInfoList.get(5).getCOLUMN_VALUE());
		tv_cumulative_fund_available.setText(financialInfoList.get(6).getCOLUMN_VALUE());
		tv_gross_expenditure_incurred_uptoDate.setText(financialInfoList.get(7).getCOLUMN_VALUE());

		if (financialInfoList.get(0).getPROJECT_TYPE_ID().trim().equalsIgnoreCase("1")) {

			tv_title.setText("FINANCIAL INFO(NCRMP-II Project)");
			tvProjectCost.setText("NCRMP-II Project");
		} else {

			tv_title.setText("FINANCIAL INFO(ICZMP Project)");
			tvProjectCost.setText("ICZMP Project");
		}
		customDialog.setCancelable(false);
		customDialog.setContentView(view);
		customDialog.setCanceledOnTouchOutside(false);
		// Start AlertDialog
		customDialog.show();

	}

}
