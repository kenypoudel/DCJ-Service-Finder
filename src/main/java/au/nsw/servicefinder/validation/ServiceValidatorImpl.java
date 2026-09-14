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

// package au.nsw.servicefinder.validation;

// import au.nsw.servicefinder.model.ServiceRecord;

// public class ServiceValidatorImpl implements ServiceValidator {

//     @Override
//     public boolean isValid(ServiceRecord service) {

//         if (service == null) { // Check if the service object is null
//             return false;
//         }

//         if (service.id() == null) {
//             return false;
//         }

//         if (service.title() == null || service.title().isBlank()) {
//             return false;
//         }

//         return true;
//     }
// }