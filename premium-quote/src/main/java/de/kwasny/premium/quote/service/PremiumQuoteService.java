package de.kwasny.premium.quote.service;

import java.math.BigDecimal;
import java.util.List;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.kwasny.premium.commons.ServiceException;
import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.Factors;
import de.kwasny.premium.commons.dto.InsurancePolicy;
import de.kwasny.premium.commons.dto.PremiumRequest;
import de.kwasny.premium.commons.dto.PremiumResponse;
import de.kwasny.premium.quote.persistence.CarPremiumRepository;
import de.kwasny.premium.quote.persistence.EntityMapper;
import de.kwasny.premium.quote.persistence.entity.CarPremium;
import de.kwasny.premium.quote.persistence.entity.Premium;

/**
 * Insurance premium quote service.
 *
 * @author Arthur Kwasny
 */
@Service
public class PremiumQuoteService {

    private static final Logger LOG = LoggerFactory.getLogger(PremiumQuoteService.class);

    @Autowired
    private FactorService factorService;
    @Autowired
    private PolicyService policyService;

    @Autowired
    private EntityMapper entityMapper;
    @Autowired
    private CarPremiumRepository carPremiumRepository;

    /**
     * Determines premium factors based on given input and returns eligible
     * policies.
     *
     * @param request
     * @return
     */
    @Transactional
    public PremiumResponse createPremium(PremiumRequest request) {
        BigDecimal premiumFactor = BigDecimal.ONE;

        // apply all factors
        // risk factors
        Factors riskFactors = factorService.getRiskFactors(request);
        premiumFactor = applyFactors(premiumFactor, riskFactors);

        // retrieve eligible policies
        List<InsurancePolicy> policies = policyService.getEligiblePolicies(request);
        policies = applyFactorToPolicies(policies, premiumFactor);

        // persist request / results
        persistPremiumRequest(request, policies);

        // return results
        return new PremiumResponse(policies);
    }

    /**
     * Applies given factors to given premium.
     *
     * @param premium base premium factors get applied to, not null
     * @param factors factors to apply, not null
     *
     * @return the new premium
     */
    protected BigDecimal applyFactors(BigDecimal premium, Factors factors) {
        return factors.stream().reduce(premium, BigDecimal::multiply);
    }
    /**
     * Returns new policies with applied factor.
     * @param policies
     * @param premiumFactor
     * @return
     */
    protected List<InsurancePolicy> applyFactorToPolicies(List<InsurancePolicy> policies, BigDecimal premiumFactor) {
        return policies.stream().map(policy-> new InsurancePolicy(policy.name(),
                policy.premium().multiply(premiumFactor))).toList();
    }

    /**
     * Transforms given parameters to a request-specific {@link Premium} entity
     * which is then persisted.
     *
     * @param request
     * @param policyResults
     * @return
     */
    protected Premium persistPremiumRequest(PremiumRequest request, List<InsurancePolicy> policyResults) {
        if (request instanceof CarPremiumRequest cpr) {
            CarPremium entity = entityMapper.mapToCarPremium(cpr, policyResults);
            return carPremiumRepository.save(entity);
        } else {
            throw new ServiceException("Unable to persist unknown request type: " + request);
        }
    }

}
