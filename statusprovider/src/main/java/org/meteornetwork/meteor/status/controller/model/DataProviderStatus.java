package org.meteornetwork.meteor.status.controller.model;

import java.util.Date;

import org.meteornetwork.meteor.common.registry.data.DataProvider;

public class DataProviderStatus {

	private String id;
	private String description;
	private Date startTime;
	private Date endTime;
	private Double elapsedTime;
	private String message;

	public DataProviderStatus() {
	}

	public DataProviderStatus(DataProvider registryDataProvider) {
		id = registryDataProvider.getInstitutionIdentifier();
		description = registryDataProvider.getDescription();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Double getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(Double elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
