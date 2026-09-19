package au.nsw.servicefinder.validation;

import au.nsw.servicefinder.model.ServiceRecord;
 /**
 * Validator interface for checking the validity of service records.
 */
public interface ServiceValidator {
	boolean isValid(ServiceRecord service);
}