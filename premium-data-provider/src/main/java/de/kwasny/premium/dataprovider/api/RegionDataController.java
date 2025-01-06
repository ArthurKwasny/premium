package de.kwasny.premium.dataprovider.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.dataprovider.service.RegionDataService;

import static org.springframework.http.MediaType.*;

/**
 * Rest service providing access to regional data.
 *
 * @author Arthur Kwasny
 */
@RestController @RequestMapping("/regions/v1")
public class RegionDataController {

    private static final Logger LOG = LoggerFactory.getLogger(RegionDataController.class);

    private final RegionDataService service;

    @Autowired
    public RegionDataController(RegionDataService service) {
        this.service = service;
    }

    @GetMapping(
            path = "/region/{id}",
            produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}
    )
    public Region getRegionById(@PathVariable UUID id) {
        Optional<Region> region = service.getRegionById(id);
        if (region.isEmpty()) {
            ErrorResponseException ex = new ErrorResponseException(HttpStatus.NOT_FOUND);
            ex.setTitle("Region not found");
            ex.setDetail("Region with given id not found");
            ex.getHeaders().add(HttpHeaders.CONTENT_TYPE, APPLICATION_PROBLEM_JSON_VALUE);
            throw ex;
        }
        return region.get();
    }

    @GetMapping(
            path = "/region",
            produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}
    )
    public List<Region> getRegionsByPostcode(@RequestParam int postcode) {
        List<Region> regions = service.getRegionsByPostcode(postcode);
        if (regions.isEmpty()) {
            ErrorResponseException ex = new ErrorResponseException(HttpStatus.NOT_FOUND);
            ex.setTitle("Region not found");
            ex.setDetail("Region with given postcode not found");
            ex.getHeaders().add(HttpHeaders.CONTENT_TYPE, APPLICATION_PROBLEM_JSON_VALUE);
            throw ex;
        }
        return regions;
    }

}
