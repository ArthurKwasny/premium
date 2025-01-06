package de.kwasny.premium.commons.validation.validators;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import de.kwasny.premium.commons.validation.constraints.EnumValue;

/**
 * Validate that the string is a value of given enum class.
 *
 * @author Arthur Kwasny
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

    private Class<? extends Enum> enumClass;
    private boolean showEnumValues;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
        showEnumValues = constraintAnnotation.showEnumValues();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            Enum.valueOf(enumClass, value);
            return true;
        } catch (IllegalArgumentException | NullPointerException ex) {
            if (showEnumValues) {
                setCustomMessage(context, "Invalid value. Must be one of "
                        + Arrays.toString(enumClass.getEnumConstants()));
            }
            return false;
        }
    }

    private void setCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}
