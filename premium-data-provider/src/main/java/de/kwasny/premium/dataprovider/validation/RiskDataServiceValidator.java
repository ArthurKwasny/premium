package de.kwasny.premium.dataprovider.validation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.kwasny.premium.commons.InvalidServiceDataException;
import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.commons.dto.RegionIdentifier;
import de.kwasny.premium.dataprovider.service.RegionDataService;

/**
 * Enhanced validation for risk data service.
 *
 * @author Arthur Kwasny
 */
@Component
public class RiskDataServiceValidator {

    private static final Logger LOG = LoggerFactory.getLogger(RiskDataServiceValidator.class);

    private final RegionDataService regionDataService;

    @Autowired
    public RiskDataServiceValidator(RegionDataService regionDataService) {
        this.regionDataService = regionDataService;
    }

    /**
     * Ensures region is identifiable and has no ambiguous state.
     *
     * @param identifier
     * @throws InvalidServiceDataException if invalid
     */
    public void validateRegionForCarRiskFactor(RegionIdentifier identifier) {
        LOG.info("validateRegionForCarRiskFactor({})", identifier);
        Optional<Region> region = Optional.empty();
        UUID regionId = identifier.getRegionId();
        Integer postcode = identifier.getPostcode();
        if (regionId != null) {
            region = regionDataService.getRegionById(regionId);
        } else {
            List<Region> regions = regionDataService.getRegionsByPostcode(postcode);
            if (!regions.isEmpty()) {
                Region first = regions.get(0);
                boolean isAmbiguousState = regions.stream().anyMatch(r -> !r.state().equals(first.state()));
                if (isAmbiguousState) {
                    throw new InvalidServiceDataException("Cannot determine factors by ambiguous postcode - use regionId instead.");
                }
                region = Optional.of(first);
            }
        }
        if (region.isEmpty()) {
            throw new InvalidServiceDataException("No region found for regionId=" + regionId + ", postcode=" + postcode);
        }
    }

}
