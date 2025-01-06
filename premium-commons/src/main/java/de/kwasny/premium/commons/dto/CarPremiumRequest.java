package de.kwasny.premium.commons.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

/**
 * Car insurance premium request model used by premium quote service.
 *
 * @author Arthur Kwasny
 */
public class CarPremiumRequest extends PremiumRequest {

    private CarRiskFactorRequest factors;

    public CarPremiumRequest() {
    }

    public CarPremiumRequest(List<String> policies, CarRiskFactorRequest factors) {
        super(policies);
        this.factors = factors;
    }

    @NotNull
    public CarRiskFactorRequest getFactors() {
        return factors;
    }

    public void setFactors(CarRiskFactorRequest factors) {
        this.factors = factors;
    }

}
