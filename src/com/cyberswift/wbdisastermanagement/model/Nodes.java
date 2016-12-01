package com.cyberswift.wbdisastermanagement.model;

import java.io.Serializable;

public class Nodes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	private String nodeName = "";
	private String nodeId = "";
	private String nodeDescription = "";
	private String parentInfo = "";

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeDescription() {
		return nodeDescription;
	}

	public void setNodeDescription(String nodeDescription) {
		this.nodeDescription = nodeDescription;
	}

	public String getParentInfo() {
		return parentInfo;
	}

	public void setParentInfo(String parentInfo) {
		this.parentInfo = parentInfo;
	}

}
