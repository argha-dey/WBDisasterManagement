package com.cyberswift.wbdisastermanagement.model;

import java.io.Serializable;
import java.util.ArrayList;

public class AllDataForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6L;

	ArrayList<Nodes> districtList = new ArrayList<Nodes>(),
			blockList = new ArrayList<Nodes>(),
			panchayatList = new ArrayList<Nodes>(),
			habitationList = new ArrayList<Nodes>(),
			villageList = new ArrayList<Nodes>();

	public ArrayList<Nodes> getDistrictList() {
		return districtList;
	}

	public void setDistrictList(ArrayList<Nodes> districtList) {
		this.districtList = districtList;
	}

	public ArrayList<Nodes> getBlockList() {
		return blockList;
	}

	public void setBlockList(ArrayList<Nodes> blockList) {
		this.blockList = blockList;
	}

	public ArrayList<Nodes> getPanchayatList() {
		return panchayatList;
	}

	public void setPanchayatList(ArrayList<Nodes> panchayatList) {
		this.panchayatList = panchayatList;
	}

	public ArrayList<Nodes> getHabitationList() {
		return habitationList;
	}

	public void setHabitationList(ArrayList<Nodes> habitationList) {
		this.habitationList = habitationList;
	}

	public ArrayList<Nodes> getVillageList() {
		return villageList;
	}

	public void setVillageList(ArrayList<Nodes> villageList) {
		this.villageList = villageList;
	}

}
