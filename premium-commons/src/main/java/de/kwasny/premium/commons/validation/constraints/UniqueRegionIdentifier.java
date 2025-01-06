package de.kwasny.premium.commons.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import de.kwasny.premium.commons.dto.RegionIdentifier;
import de.kwasny.premium.commons.validation.validators.UniqueRegionIdentifierValidator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must implement {@link RegionIdentifier}.
 *
 * @author Arthur Kwasny
 */
@Documented
@Constraint(validatedBy = UniqueRegionIdentifierValidator.class)
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
public @interface UniqueRegionIdentifier {

    String message() default "Either regionId or postcode must be set.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
