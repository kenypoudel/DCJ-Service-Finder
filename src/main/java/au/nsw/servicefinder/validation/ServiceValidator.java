package au.nsw.servicefinder.validation;
import au.nsw.servicefinder.model.ServiceRecord;
public interface ServiceValidator { 
	boolean isValid(ServiceRecord service); 
}