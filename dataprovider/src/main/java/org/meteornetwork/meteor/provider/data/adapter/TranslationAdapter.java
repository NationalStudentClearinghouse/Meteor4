package org.meteornetwork.meteor.provider.data.adapter;

/**
 * Interface for the translation layer. Implementors translate from a different
 * version of meteor into this version's request and response wrappers.
 * 
 * @author jlazos
 * 
 */
public interface TranslationAdapter {

	/**
	 * @return RequestWrapper object or null on error
	 */
	RequestWrapper getRequest();

	/**
	 * Sets response data from ResponseWrapper or nothing on error
	 * 
	 * @param response
	 */
	void setResponse(ResponseWrapper response);
}
