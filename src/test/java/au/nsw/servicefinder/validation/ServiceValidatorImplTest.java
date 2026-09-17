
package au.nsw.servicefinder.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import au.nsw.servicefinder.model.ServiceRecord;

/**
 * Unit tests for the
 * ServiceValidatorImplTest
 */
class ServiceValidatorImplTest {

    private final ServiceValidator validator = new ServiceValidatorImpl();

    @Test
    void shouldRejectServiceWhenIdIsMissing() {

        ServiceRecord service = new ServiceRecord(
                null,
                "Test Service",
                "Education",
                "Monday",
                "9am - 5pm",
                "https://example.com",
                "Test description");

        assertFalse(validator.isValid(service));
    }

    @Test
    void shouldRejectServiceWhenTitleIsMissing() {

        ServiceRecord service = new ServiceRecord(
                1,
                null,
                "Education",
                "Monday",
                "9am - 5pm",
                "https://example.com",
                "Test description");

        assertFalse(validator.isValid(service));
    }

    @Test
    void shouldAcceptServiceWhenOptionalDescriptionIsMissing() {

        ServiceRecord service = new ServiceRecord(
                1,
                "Test Service",
                "Education",
                "Monday",
                "9am - 5pm",
                "https://example.com",
                null);

        assertTrue(validator.isValid(service));
    }

}