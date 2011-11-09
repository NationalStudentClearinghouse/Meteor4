package org.meteornetwork.meteor.status.controller.model;

import java.util.ArrayList;

public class StatusQueryModel {

	private String[] dataProvidersToQuery;
	private ArrayList<DataProviderStatus> dataProviders;
	private String error;

	public ArrayList<DataProviderStatus> getDataProviders() {
		return dataProviders;
	}

	public void setDataProviders(ArrayList<DataProviderStatus> dataProviders) {
		this.dataProviders = dataProviders;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String[] getDataProvidersToQuery() {
		return dataProvidersToQuery;
	}

	public void setDataProvidersToQuery(String[] dataProvidersToQuery) {
		this.dataProvidersToQuery = dataProvidersToQuery;
	}
}
