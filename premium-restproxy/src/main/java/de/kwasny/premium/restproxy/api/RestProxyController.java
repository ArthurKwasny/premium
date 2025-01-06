package de.kwasny.premium.restproxy.api;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.PremiumResponse;
import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.restclients.QuoteRestClient;
import de.kwasny.premium.restclients.RegionDataRestClient;

import static org.springframework.http.MediaType.*;

/**
 * Rest proxy for client applications.
 *
 * @author Arthur Kwasny
 */
@RestController
@RequestMapping("/api/v1")
public class RestProxyController {

    private final QuoteRestClient quoteService;
    private final RegionDataRestClient regionDataService;

    public RestProxyController(QuoteRestClient quoteService, RegionDataRestClient regionDataService) {
        this.quoteService = quoteService;
        this.regionDataService = regionDataService;
    }

    // quote service proxy methods
    @PostMapping(
            path = "/car/quote",
            produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}
    )
    public PremiumResponse createCarPremium(@RequestBody CarPremiumRequest request) {
        return quoteService.createCarPremium(request);
    }

    @GetMapping(
            path = "/car/policy",
            produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}
    )
    public List<String> getAvailableCarPolicies() {
        return quoteService.getAvailableCarPolicies();
    }

    // region data service proxy methods
    @GetMapping(
            path = "/region/{id}",
            produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}
    )
    public Region getRegionById(@PathVariable UUID id) {
        return regionDataService.getRegionById(id);
    }

    @GetMapping(
            path = "/region",
            produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}
    )
    public List<Region> getRegionByPostcode(@RequestParam int postcode) {
        return regionDataService.getRegionByPostcode(postcode);
    }

}
