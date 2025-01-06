package de.kwasny.premium.quote.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.kwasny.premium.commons.InvalidServiceDataException;
import de.kwasny.premium.commons.ServiceException;
import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.InsurancePolicy;
import de.kwasny.premium.commons.dto.PremiumRequest;

/**
 * Service which simulates the retrieval of insurance policies.
 *
 * @author Arthur Kwasny
 */
@Service
public class PolicyService {

    private static final Logger LOG = LoggerFactory.getLogger(PolicyService.class);

    // example policies
    private static final String POLICY1 = "auto_flex";
    private static final String POLICY2 = "drive_secure";
    private static final String POLICY3 = "mobil_komfort";

    /**
     * @return a list of available car policies
     */
    public List<String> getAvailableCarPolicies() {
        return List.of(POLICY1, POLICY2, POLICY3);
    }

    /**
     * Retrieves insurance policies eligible for given request.
     *
     * @param request
     * @return
     */
    public List<InsurancePolicy> getEligiblePolicies(PremiumRequest request) {
        List<InsurancePolicy> policies;
        if (request instanceof CarPremiumRequest cpr) {
            policies = getEligibleCarPolicies(cpr);
        } else {
            throw new ServiceException("Unable to get policies for unknown request type: " + request);
        }

        return policies;
    }

    /**
     * Simulates retrieval of eligible car insurance policies.
     *
     * @param request
     * @param premiumFactor
     *
     * @return
     */
    private List<InsurancePolicy> getEligibleCarPolicies(CarPremiumRequest request) {
        List<String> availableCarPolicies = getAvailableCarPolicies();
        List<String> policyNames = request.getPolicies();

        if (policyNames == null || policyNames.isEmpty()) {
            // fallback if nothing was specified
            policyNames = availableCarPolicies;
        } else {
            // remove invalid policies
            List<String> unknownPolicyNames = new ArrayList<>(policyNames);
            unknownPolicyNames.removeAll(availableCarPolicies);
            policyNames.removeAll(unknownPolicyNames);
            if (policyNames.isEmpty()) {
                throw new InvalidServiceDataException("No valid policy specified.");
            }
        }


        // get policies
        return policyNames.stream().map(name -> {
            // determine policy base premium
            Integer basePremium = switch (name) {
                case POLICY1 -> 300;
                case POLICY2 -> 400;
                case POLICY3 -> 600;
                default -> null;
            };
            if (basePremium == null) {
                throw new ServiceException("Unable to retrieve policy data for: " + name);
            }
            return new InsurancePolicy(name, BigDecimal.valueOf(basePremium));
        }).filter(Objects::nonNull).toList();
    }

}
