package de.kwasny.premium.commons.dto;

import java.math.BigDecimal;
import java.util.stream.Stream;

/**
 * Provides access to factors for premium calculation.
 *
 * @author Arthur Kwasny
 */
public interface Factors {

    /**
     * @return all factors as a {@code Stream<BigDecimal>}
     */
    Stream<BigDecimal> stream();
}
