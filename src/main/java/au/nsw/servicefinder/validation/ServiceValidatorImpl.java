package au.nsw.servicefinder.validation;

import au.nsw.servicefinder.model.ServiceRecord;

public final class ServiceValidatorImpl implements ServiceValidator {
    @Override
    public boolean isValid(ServiceRecord service) {
        return service != null
                && service.id() != null
                && service.title() != null
                && !service.title().trim().isEmpty();
    }
}
