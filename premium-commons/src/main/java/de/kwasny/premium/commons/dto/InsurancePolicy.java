package de.kwasny.premium.commons.dto;

import java.math.BigDecimal;

/**
 * Demo policy object with a name for identification and a premium base value.
 *
 * @author Arthur Kwasny
 */
public record InsurancePolicy(String name, BigDecimal premium) {
}
