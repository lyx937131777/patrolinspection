package com.example.patrolinspection.psam;

import java.util.List;

public class TrackRecord {
	private String unitName;//单位名称
	private String hireDate;//入职时间
	private String serviceObject;//服务对象
	private String lastServiceObject;//上次服务对象
	private String historyRecordCount;//历史从业记录数
	private List<String> historyRecord;//历史从业记录
	/**
	 * @return the unitName
	 */
	public String getUnitName() {
		return unitName;
	}
	/**
	 * @param unitName the unitName to set
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	/**
	 * @return the hireDate
	 */
	public String getHireDate() {
		return hireDate;
	}
	/**
	 * @param hireDate the hireDate to set
	 */
	public void setHireDate(String hireDate) {
		this.hireDate = hireDate;
	}
	/**
	 * @return the serviceObject
	 */
	public String getServiceObject() {
		return serviceObject;
	}
	/**
	 * @param serviceObject the serviceObject to set
	 */
	public void setServiceObject(String serviceObject) {
		this.serviceObject = serviceObject;
	}
	/**
	 * @return the lastServiceObject
	 */
	public String getLastServiceObject() {
		return lastServiceObject;
	}
	/**
	 * @param lastServiceObject the lastServiceObject to set
	 */
	public void setLastServiceObject(String lastServiceObject) {
		this.lastServiceObject = lastServiceObject;
	}
	/**
	 * @return the historyRecordCount
	 */
	public String getHistoryRecordCount() {
		return historyRecordCount;
	}
	/**
	 * @param historyRecordCount the historyRecordCount to set
	 */
	public void setHistoryRecordCount(String historyRecordCount) {
		this.historyRecordCount = historyRecordCount;
	}
	/**
	 * @return the historyRecord
	 */
	public List<String> getHistoryRecord() {
		return historyRecord;
	}
	/**
	 * @param historyRecord the historyRecord to set
	 */
	public void setHistoryRecord(List<String> historyRecord) {
		this.historyRecord = historyRecord;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TrackRecord [unitName=" + unitName + ", hireDate=" + hireDate
				+ ", serviceObject=" + serviceObject + ", lastServiceObject="
				+ lastServiceObject + ", historyRecordCount="
				+ historyRecordCount + ", historyRecord=" + historyRecord + "]";
	}
	
	
}
