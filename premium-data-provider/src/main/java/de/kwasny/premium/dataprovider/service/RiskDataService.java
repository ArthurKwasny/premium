package de.kwasny.premium.dataprovider.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.kwasny.premium.commons.dto.CarRiskFactorRequest;
import de.kwasny.premium.commons.dto.CarRiskFactors;
import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.commons.dto.RegionIdentifier;
import de.kwasny.premium.commons.dto.enums.StateEnum;
import de.kwasny.premium.commons.dto.enums.VehicleEnum;
import de.kwasny.premium.dataprovider.validation.RiskDataServiceValidator;

/**
 * Provides services for insurance risk factors.
 *
 * @author Arthur Kwasny
 */
@Service
public class RiskDataService {

    private static final Logger LOG = LoggerFactory.getLogger(RiskDataService.class);

    private final RegionDataService regionDataService;
    private final RiskDataServiceValidator validator;

    @Autowired
    public RiskDataService(RegionDataService regionDataService, RiskDataServiceValidator validator) {
        this.regionDataService = regionDataService;
        this.validator = validator;
    }

    public CarRiskFactors getCarRiskFactors(CarRiskFactorRequest input) {
        LOG.debug("getCarRiskFactors({})", input);
        validator.validateRegionForCarRiskFactor(input);
        return new CarRiskFactors(
                getCarMileageFactor(input.getMileage()),
                getCarRegionFactor(input),
                getCarVehicleFactor(input.getVehicleAsEnum())
        );
    }

    private BigDecimal getCarMileageFactor(int mileage) {
        LOG.debug("getCarMileageFactor({})", mileage);
        double factor;
        if (mileage <= 5_000) {
            factor = .5;
        } else if (mileage <= 10_000) {
            factor = 1;
        } else if (mileage <= 20_000) {
            factor = 1.5;
        } else {
            factor = 2;
        }
        return BigDecimal.valueOf(factor);
    }

    private BigDecimal getCarVehicleFactor(VehicleEnum vehicle) {
        LOG.debug("getCarVehicleFactor({})", vehicle);
        double factor = switch (vehicle) {
            case CABRIO -> 1.8;
            case COMBI -> 1.1;
            case COMPACT -> 1;
            case COUPE -> 1.8;
            case LIMOUSINE -> 1.2;
            case MINIVAN -> 1.3;
            case PICKUP -> 1.5;
            case ROADSTER -> 2;
            case SUV -> 1.5;
            case VAN -> 1.2;
            default -> throw new IllegalStateException("Unsupported vehicle type: " + vehicle);
        };
        return BigDecimal.valueOf(factor);
    }

    private BigDecimal getCarRegionFactor(RegionIdentifier identifier) {
        LOG.debug("getCarRegionFactor({})", identifier);
        Region region;
        if (identifier.getRegionId() != null) {
            region = regionDataService.getRegionById(identifier.getRegionId()).get();
        } else {
            region = regionDataService.getRegionsByPostcode(identifier.getPostcode()).get(0);
        }
        StateEnum state = region.state();
        double factor = switch (state) {
            case BB -> 1.10;
            case BE -> 1.15;
            case BW -> 0.95;
            case BY -> 0.90;
            case HB -> 1.05;
            case HE -> 1.00;
            case HH -> 1.10;
            case MV -> 1.05;
            case NI -> 1.00;
            case NW -> 1.10;
            case RP -> 1.10;
            case SH -> 0.90;
            case SL -> 0.95;
            case SN -> 1.05;
            case ST -> 1.10;
            case TH -> 1.00;
            default -> throw new IllegalStateException("Unsupported region: " + region);
        };
        return BigDecimal.valueOf(factor);
    }

}
