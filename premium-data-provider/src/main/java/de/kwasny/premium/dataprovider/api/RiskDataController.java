package de.kwasny.premium.dataprovider.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.kwasny.premium.commons.dto.CarRiskFactorRequest;
import de.kwasny.premium.commons.dto.CarRiskFactors;
import de.kwasny.premium.dataprovider.service.RiskDataService;

import static org.springframework.http.MediaType.*;

/**
 * Rest service providing access to risk data.
 *
 * @author Arthur Kwasny
 */
@RestController
@RequestMapping("/factors/v1")
public class RiskDataController {

    private final RiskDataService riskDataService;

    @Autowired
    public RiskDataController(RiskDataService riskDataService) {
        this.riskDataService = riskDataService;
    }

    @GetMapping(
            path = "/car/risk",
            produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}
    )
    public CarRiskFactors getCarRiskFactors(@Validated @ModelAttribute CarRiskFactorRequest input) {
        return riskDataService.getCarRiskFactors(input);
    }
}
