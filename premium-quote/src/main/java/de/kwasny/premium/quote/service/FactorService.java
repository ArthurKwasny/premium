package de.kwasny.premium.quote.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.kwasny.premium.commons.ServiceException;
import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.Factors;
import de.kwasny.premium.commons.dto.PremiumRequest;
import de.kwasny.premium.restclients.RiskDataRestClient;

/**
 * Provides functionality regarding premium factors.
 *
 * @author Arthur Kwasny
 */
@Service
public class FactorService {

    private static final Logger LOG = LoggerFactory.getLogger(FactorService.class);

    @Autowired
    private RiskDataRestClient riskDataService;

    /**
     * Retrieves risk factor values for premium calculations.
     *
     * @param request
     * @return
     * @throws ServiceException if request type is unknown or invalid factors
     *                          were delivered
     */
    public Factors getRiskFactors(PremiumRequest request) {
        Factors factors;
        if (request instanceof CarPremiumRequest cpr) {
            factors = riskDataService.getCarRiskFactors(cpr.getFactors());
        } else {
            throw new ServiceException("Unknown request type: " + request);
        }

        validateFactors(factors);
        return factors;
    }

    /**
     * Ensures each factor is a positive value.
     *
     * @param factors
     * @throws ServiceException if factors contain non-positive values
     */
    protected void validateFactors(Factors factors) {
        boolean containsInvalidFactors = factors.stream().anyMatch(f -> f.compareTo(BigDecimal.ZERO) < 1);
        if (containsInvalidFactors) {
            throw new ServiceException("Received invalid factors from risk data service.");
        }
    }

}
