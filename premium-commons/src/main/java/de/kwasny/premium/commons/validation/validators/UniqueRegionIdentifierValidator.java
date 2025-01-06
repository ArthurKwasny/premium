package de.kwasny.premium.commons.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kwasny.premium.commons.dto.RegionIdentifier;
import de.kwasny.premium.commons.validation.constraints.UniqueRegionIdentifier;

/**
 * Validate that either regionId or postcode is set exclusively.
 *
 * @author Arthur Kwasny
 */
public class UniqueRegionIdentifierValidator implements ConstraintValidator<UniqueRegionIdentifier, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(UniqueRegionIdentifierValidator.class);

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof RegionIdentifier id) {
            return (id.getRegionId() == null) != (id.getPostcode() == null);
        } else if (value != null) {
            LOG.warn("{} does not implement {}", value.getClass().getName(), RegionIdentifier.class.getName());
        }

        return false;
    }

}
