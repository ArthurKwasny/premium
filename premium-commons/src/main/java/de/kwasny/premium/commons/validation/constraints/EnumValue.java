package de.kwasny.premium.commons.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import de.kwasny.premium.commons.validation.validators.EnumValueValidator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must be a {@code string}.
 * An {@link #enumClass() enum class} must be specified.
 *
 * @author Arthur Kwasny
 */
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
@Target({FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
public @interface EnumValue {

    String message() default "Invalid value";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * @return the enum class to validate against
     */
    Class<? extends Enum> enumClass();

    /**
     * @return whether available enum values will be added to the default message
     */
    boolean showEnumValues() default true;

}
