package org.meteornetwork.meteor.common.ws;

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.Role;

@WebService
public interface RegistryService {

	@WebMethod(operationName = "GetCertificate")
	@WebResult(name = "GetCertificateResponse")
	byte[] getCertificate(@WebParam(name = "MeteorInstitutionId") String meteorInstitutionId, @WebParam(name = "ProviderType") ProviderType providerType) throws RegistryException;

	@WebMethod(operationName = "GetIndexProviders")
	@WebResult(name = "GetIndexProvidersResponse")
	ArrayList<IndexProvider> getIndexProviders(@WebParam(name = "MeteorVersion") Version meteorVersion) throws RegistryException;

	@WebMethod(operationName = "GetDataProvider")
	@WebResult(name = "GetDataProviderResponse")
	DataProvider getDataProvider(@WebParam(name = "MeteorInstitutionId") String meteorInstitutionId) throws RegistryException;

	@WebMethod(operationName = "GetAllDataProviders")
	@WebResult(name = "GetAllDataProvidersResponse")
	ArrayList<DataProvider> getAllDataProviders() throws RegistryException;

	@WebMethod(operationName = "GetAliases")
	@WebResult(name = "GetAliasesResponse")
	ArrayList<String> getAliases(@WebParam(name = "MeteorInstitutionId") String meteorInstitutionId, @WebParam(name = "ProviderType") ProviderType providerType) throws RegistryException;

	@WebMethod(operationName = "GetAuthenticationLevel")
	@WebResult(name = "GetAuthenticationLevelResponse")
	Integer getAuthenticationLevel(@WebParam(name = "MeteorInstitutionId") String meteorInstitutionId, @WebParam(name = "AuthenticationProcessId") String authProcId, @WebParam(name = "ProviderType") ProviderType providerType, @WebParam(name = "Role") Role role) throws RegistryException;

	@WebMethod(operationName = "GetRoles")
	@WebResult(name = "GetRolesResponse")
	ArrayList<Role> getRoles(@WebParam(name = "MeteorInstitutionId") String meteorInstitutionId, @WebParam(name = "AuthenticationProcessId") String authProcId, @WebParam(name = "ProviderType") ProviderType providerType) throws RegistryException;
}
