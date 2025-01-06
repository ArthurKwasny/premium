package de.kwasny.premium.quote.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.PremiumResponse;
import de.kwasny.premium.quote.service.PolicyService;
import de.kwasny.premium.quote.service.PremiumQuoteService;

import static org.springframework.http.MediaType.*;

/**
 * Rest service providing access to premium quote services.
 *
 * @author Arthur Kwasny
 */
@RestController @RequestMapping("/premiums/v1")
public class PremiumQuoteController {

    private static final Logger LOG = LoggerFactory.getLogger(PremiumQuoteController.class);

    @Autowired
    private PremiumQuoteService service;
    @Autowired
    private PolicyService policyService;

    @PostMapping(
            path = "/car/quote",
            consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}
    )
    public PremiumResponse createCarPremium(@Validated @RequestBody CarPremiumRequest request) {
        return service.createPremium(request);
    }

    @GetMapping(
            path = "/car/policy",
            produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}
    )
    public List<String> getAvailableCarPolicies() {
        return policyService.getAvailableCarPolicies();
    }
}
