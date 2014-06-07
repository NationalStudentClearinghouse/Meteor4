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
package org.meteornetwork.meteor.provider.data;

import java.io.StringReader;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.meteornetwork.meteor.common.xml.dataresponse.AddressInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.Contacts;
import org.meteornetwork.meteor.common.xml.dataresponse.DataProviderAggregateTotal;
import org.meteornetwork.meteor.common.xml.dataresponse.DataProviderData;
import org.meteornetwork.meteor.common.xml.dataresponse.Email;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderDetailInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderInfo;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderMsg;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.dataresponse.Phone;
import org.meteornetwork.meteor.common.xml.dataresponse.types.DataProviderTypeEnum;

public class MeteorDataResponseWrapper {

	private final MeteorRsMsg response;

	public MeteorDataResponseWrapper() {
		response = new MeteorRsMsg();
	}

	/**
	 * Unmarshalls a MeteorRsMsg xml document
	 * 
	 * @param xml
	 *            the xml document to unmarshal. Root element must be
	 *            MeteorRsMsg
	 * @throws MarshalException
	 *             the xml document could not be unmarshalled
	 * @throws ValidationException
	 *             the xml document is not valid against the Meteor schema
	 */
	public MeteorDataResponseWrapper(String xml) throws MarshalException, ValidationException {
		response = MeteorRsMsg.unmarshal(new StringReader(xml));
	}

	/**
	 * This method ensures that the MeteorRsMsg that this encapsulates is well
	 * formed. This will walk through the hierarchy and force creation of the
	 * elements that must be there to match the schema
	 */
	public void createMinimalResponse() {
		MeteorDataProviderInfo mdpi = null;
		if (response.getMeteorDataProviderInfoCount() < 1) {
			mdpi = new MeteorDataProviderInfo();
			response.addMeteorDataProviderInfo(mdpi);
		} else {
			mdpi = response.getMeteorDataProviderInfo()[0];
		}

		MeteorDataProviderDetailInfo mdpdi = mdpi.getMeteorDataProviderDetailInfo();
		if (mdpdi == null) {
			mdpdi = new MeteorDataProviderDetailInfo();
			mdpi.setMeteorDataProviderDetailInfo(mdpdi);
		}

		if (mdpdi.getDataProviderType() == null) {
			mdpdi.setDataProviderType(DataProviderTypeEnum.G.name());
		}

		DataProviderData dpd = mdpdi.getDataProviderData();
		if (dpd == null) {
			dpd = new DataProviderData();
			mdpdi.setDataProviderData(dpd);
		}

		Contacts contacts = dpd.getContacts();
		if (contacts == null) {
			contacts = new Contacts();
			dpd.setContacts(contacts);
		}

		DataProviderAggregateTotal dpat = mdpdi.getDataProviderAggregateTotal();
		if (dpat == null) {
			dpat = new DataProviderAggregateTotal();
			mdpdi.setDataProviderAggregateTotal(dpat);
		}
	}

	/**
	 * Adds a message to the first MeteorDataProviderInfo element in the
	 * response.
	 * 
	 * @param messageText
	 *            the text of the message
	 * @param level
	 *            the severity level of the message
	 */
	public void addMessage(String messageText, String level) {
		MeteorDataProviderMsg message = new MeteorDataProviderMsg();
		message.setRsMsg(messageText);
		message.setRsMsgLevel(level);

		if (response.getMeteorDataProviderInfoCount() < 1) {
			this.createMinimalResponse();
		}
		response.getMeteorDataProviderInfo(0).addMeteorDataProviderMsg(message);
	}

	public MeteorRsMsg getResponse() {
		return response;
	}

	public class DataProviderDataParams {
		private String name;
		private String id;
		private String url;
		private String type;

		private String phone;
		private String phoneType;
		private String email;
		private String addr1;
		private String addr2;
		private String addr3;
		private String city;
		private String stateProvince;
		private String postalCode;

		public void setDataProviderData() {
			if (response.getMeteorDataProviderInfoCount() < 1) {
				createMinimalResponse();
			}

			MeteorDataProviderDetailInfo detail = response.getMeteorDataProviderInfo(0).getMeteorDataProviderDetailInfo();
			if (detail == null) {
				detail = new MeteorDataProviderDetailInfo();
			}

			DataProviderData data = detail.getDataProviderData();
			if (data == null) {
				data = new DataProviderData();
			}

			if (id != null) {
				data.setEntityID(id);
			}

			if (name != null) {
				data.setEntityName(name);
			}

			if (url != null) {
				data.setEntityURL(url);
			}

			if (type != null) {
				detail.setDataProviderType(type);
			}

			Contacts contacts = data.getContacts();
			if (contacts == null) {
				contacts = new Contacts();
			}

			if (phone != null) {
				Phone phoneNum = new Phone();
				phoneNum.setPhoneNum(phone);
				if (phoneType != null) {
					phoneNum.setPhoneNumType(phoneType);
				}
				contacts.addPhone(phoneNum);
			}

			if (email != null) {
				Email emailAddr = new Email();
				emailAddr.setEmailAddress(email);
				contacts.setEmail(emailAddr);
			}

			if (addr1 != null || addr2 != null || addr3 != null || city != null || stateProvince != null || postalCode != null) {
				contacts.setAddressInfo(new AddressInfo());
			}

			if (addr1 != null) {
				contacts.getAddressInfo().addAddr(addr1);
			}

			if (addr2 != null) {
				contacts.getAddressInfo().addAddr(addr2);
			}

			if (addr3 != null) {
				contacts.getAddressInfo().addAddr(addr3);
			}

			if (city != null) {
				contacts.getAddressInfo().setCity(city);
			}

			if (stateProvince != null) {
				contacts.getAddressInfo().setStateProv(stateProvince);
			}

			if (postalCode != null) {
				contacts.getAddressInfo().setPostalCd(postalCode);
			}

			data.setContacts(contacts);
			detail.setDataProviderData(data);
			response.getMeteorDataProviderInfo(0).setMeteorDataProviderDetailInfo(detail);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getPhoneType() {
			return phoneType;
		}

		public void setPhoneType(String phoneType) {
			this.phoneType = phoneType;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getAddr1() {
			return addr1;
		}

		public void setAddr1(String addr1) {
			this.addr1 = addr1;
		}

		public String getAddr2() {
			return addr2;
		}

		public void setAddr2(String addr2) {
			this.addr2 = addr2;
		}

		public String getAddr3() {
			return addr3;
		}

		public void setAddr3(String addr3) {
			this.addr3 = addr3;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getStateProvince() {
			return stateProvince;
		}

		public void setStateProvince(String stateProvince) {
			this.stateProvince = stateProvince;
		}

		public String getPostalCode() {
			return postalCode;
		}

		public void setPostalCode(String postalCode) {
			this.postalCode = postalCode;
		}

	}
}