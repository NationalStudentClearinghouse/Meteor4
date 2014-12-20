/*******************************************************************************
 * Copyright 2002 National Student Clearinghouse
 * 
 * This code is part of the Meteor system as defined and specified 
 * by the National Student Clearinghouse and the Meteor Sponsors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package org.meteornetwork.meteor.status.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.soap.SOAPFaultException;

import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.common.util.message.Messages;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityTokenImpl;
import org.meteornetwork.meteor.status.controller.model.DataProviderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

//@Service
public class StatusProviderManager implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(StatusProviderManager.class);

	private static final Integer MINIMUM_MAJOR_VERSION = 4;
	private static final Long DATAPROVIDER_TIMEOUT_DEFAULT = 20000L;

	private RegistryManager registryManager;
	private Properties authenticationProperties;
	private Properties statusProviderProperties;
	
	private ApplicationContext applicationContext;

	/**
	 * Gets all active data providers from the registry that are queryable by
	 * this status provider.
	 * 
	 * @return All active data providers from the registry
	 * 
	 * @throws RegistryException
	 */
	public List<DataProvider> getDataProviders() throws RegistryException {
		List<DataProvider> dataProviders = registryManager.getAllDataProviders();

		List<DataProvider> toRemove = new ArrayList<DataProvider>();
		for (DataProvider dp : dataProviders) {
			if (isVersionTooOld(dp.getMeteorVersion())) {
				toRemove.add(dp);
			}
		}

		dataProviders.removeAll(toRemove);
		return dataProviders;
	}

	public ArrayList<DataProviderStatus> getDataProviderStatuses(String[] dataProviderIdsToQuery) throws RegistryException {
		List<DataProvider> registryDataProviders = this.getDataProviders();

		List<DataProvider> toQuery = new ArrayList<DataProvider>();
		for (String dpId : dataProviderIdsToQuery) {
			DataProvider registryDataProvider = null;
			for (DataProvider regDataProv : registryDataProviders) {
				if (!regDataProv.getInstitutionIdentifier().equals(dpId)) {
					continue;
				}

				registryDataProvider = regDataProv;
				break;
			}

			if (registryDataProvider != null) {
				toQuery.add(registryDataProvider);
			}
		}

		return queryDataProviders(toQuery);
	}

	private ArrayList<DataProviderStatus> queryDataProviders(List<DataProvider> dataProviders) {
		populateRequestInfo();

		ArrayList<DataProviderStatus> dpStatuses = new ArrayList<DataProviderStatus>();
		List<DataProvider> dataProvidersToCall = new ArrayList<DataProvider>();
		Map<String, DataProviderStatus> dpStatusesMap = new HashMap<String, DataProviderStatus>();

		if (dataProviders.size() == 0) {
			return dpStatuses;
		}
		
		for (DataProvider dp : dataProviders) {
			if (isVersionTooOld(dp.getMeteorVersion())) {
				DataProviderStatus dpStatus = new DataProviderStatus(dp);
				dpStatus.setMessage("Data provider is Version " + dp.getMeteorVersion() + " and cannot be called by this version of the Status Provider");
				dpStatuses.add(dpStatus);
				continue;
			}

			dataProvidersToCall.add(dp);
			dpStatusesMap.put(dp.getInstitutionIdentifier(), new DataProviderStatus(dp));
		}

		ExecutorService threadPool = Executors.newFixedThreadPool(dataProvidersToCall.size());
		Map<String, Future<MeteorRsMsg>> futures = new HashMap<String, Future<MeteorRsMsg>>();

		Long timeout;
		try {
			timeout = Long.valueOf(statusProviderProperties.getProperty("dataprovider.timeout"));
		} catch (NumberFormatException e) {
			LOG.error("Invalid dataprovider timeout. Using default of " + DATAPROVIDER_TIMEOUT_DEFAULT, e);
			timeout = DATAPROVIDER_TIMEOUT_DEFAULT;
		}

		for (DataProvider dp : dataProvidersToCall) {
			StatusQueryCallable callable = applicationContext.getBean(StatusQueryCallable.class);
			callable.setDataProvider(dp);
			dpStatusesMap.get(dp.getInstitutionIdentifier()).setStartTime(Calendar.getInstance().getTime());
			futures.put(dp.getInstitutionIdentifier(), threadPool.submit(callable));
		}

		threadPool.shutdown();
		try {
			LOG.debug("Waiting on data providers...");
			threadPool.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			LOG.warn("ExecutorService was interrupted", e);
		}

		for (Map.Entry<String, Future<MeteorRsMsg>> futureEntry : futures.entrySet()) {
			if (futureEntry.getValue().isDone()) {
				LOG.debug("Checking response for data provider " + futureEntry.getKey());
				try {
					futureEntry.getValue().get();
					DataProviderStatus dpStatus = dpStatusesMap.get(futureEntry.getKey());
					dpStatus.setEndTime(Calendar.getInstance().getTime());
				} catch (Exception e) {
					LOG.debug("Exception thrown when getting data from data provider (ID: " + futureEntry.getKey() + ")", e);
					DataProviderStatus dpStatus = dpStatusesMap.get(futureEntry.getKey());
					if (e instanceof ExecutionException) {
						dpStatus.setMessage("Unable to respond: " + getErrorMessage((ExecutionException) e));
					} else {
						dpStatus.setMessage("Unable to respond: " + e.getMessage());
					}
				}
			} else {
				LOG.debug("Data provider " + futureEntry.getKey() + " was unable to respond");
				DataProviderStatus dpStatus = dpStatusesMap.get(futureEntry.getKey());
				dpStatus.setMessage("Unable to respond in timeout window.");
			}
		}

		for (DataProviderStatus dpStatus : dpStatusesMap.values()) {
			if (dpStatus.getEndTime() != null) {
				dpStatus.setElapsedTime(getSecondDelta(dpStatus.getStartTime(), dpStatus.getEndTime()));
			}
			dpStatuses.add(dpStatus);
		}

		return dpStatuses;
	}

	private boolean isVersionTooOld(String meteorVersion) {
		return MINIMUM_MAJOR_VERSION.compareTo(new Version(meteorVersion).getMajor()) > 0;
	}

	private String getErrorMessage(ExecutionException e) {
		String message;
		if (e.getCause() instanceof SOAPFaultException) {
			SOAPFaultException soapException = (SOAPFaultException) e.getCause();
			message = Messages.getMessage(soapException.getMessage());
			if (message.equals(soapException.getMessage())) {
				message = Messages.getMessage(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE.getPropertyRef());
			}
		} else {
			message = Messages.getMessage(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE.getPropertyRef());
		}

		return message;
	}

	private void populateRequestInfo() {
		RequestInfo requestInfo = getRequestInfo();
		requestInfo.setSecurityToken(new SecurityTokenImpl());
		requestInfo.getSecurityToken().setAuthenticationProcessId(authenticationProperties.getProperty("authentication.process.identifier"));
		requestInfo.getSecurityToken().setLevel(3);
		requestInfo.getSecurityToken().setRole(Role.HELPDESK);
		requestInfo.getSecurityToken().setUserHandle("meteor");
	}

	/**
	 * Return the number of seconds between the two dates.
	 * 
	 * @param d1
	 *            Date The first date to compare
	 * @param d2
	 *            Date The second date to compare
	 * @return double The number of seconds between the two dates. If the result
	 *         is negative then d2 is before d1.
	 */
	private double getSecondDelta(Date d1, Date d2) {
		double millisDiff = getMillisecondDelta(d1, d2);
		return millisDiff / 1000.0;
	}

	private double getMillisecondDelta(Date d1, Date d2) {
		if (d1 == null || d2 == null) {
			return Double.NaN;
		}

		double millisDiff = d2.getTime() - d1.getTime();

		return millisDiff;
	}

	public RequestInfo getRequestInfo() {
		// overridden by spring method-injection
		return null;
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

	public Properties getAuthenticationProperties() {
		return authenticationProperties;
	}

	@Autowired
	@Qualifier("authenticationProperties")
	public void setAuthenticationProperties(Properties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

	public Properties getStatusProviderProperties() {
		return statusProviderProperties;
	}

	@Autowired
	@Qualifier("statusProviderProperties")
	public void setStatusProviderProperties(Properties statusProviderProperties) {
		this.statusProviderProperties = statusProviderProperties;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
