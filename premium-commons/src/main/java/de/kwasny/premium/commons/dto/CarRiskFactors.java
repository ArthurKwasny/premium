package de.kwasny.premium.commons.dto;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.stream.Stream;

/**
 * Car risk factor response model used by risk data service.
 *
 * @author Arthur Kwasny
 */
public record CarRiskFactors(BigDecimal mileage, BigDecimal region, BigDecimal vehicle)
        implements Factors {

    @Transient
    @Override
    public Stream<BigDecimal> stream() {
        return Stream.of(mileage, region, vehicle);
    }
}
