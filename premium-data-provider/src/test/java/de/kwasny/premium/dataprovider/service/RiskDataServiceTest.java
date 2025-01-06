package de.kwasny.premium.dataprovider.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.kwasny.premium.commons.dto.CarRiskFactorRequest;
import de.kwasny.premium.commons.dto.CarRiskFactors;
import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.commons.dto.enums.StateEnum;
import de.kwasny.premium.commons.dto.enums.VehicleEnum;
import de.kwasny.premium.dataprovider.validation.RiskDataServiceValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/**
 *
 * @author Arthur Kwasny
 */
public class RiskDataServiceTest {

    private RiskDataService service;
    private RegionDataService regionDataService = Mockito.mock(RegionDataService.class);

    public RiskDataServiceTest() {
        RiskDataServiceValidator regionValidator = new RiskDataServiceValidator(regionDataService);
        service = new RiskDataService(regionDataService, regionValidator);
    }

    @Test
    void testGetVehicleFactor() {
        for (VehicleEnum type : VehicleEnum.values()) {
            try {
                Region region = new Region(12345, StateEnum.BB, "district", "county", "city", "area");
                Mockito.when(regionDataService.getRegionById(any())).thenReturn(Optional.of(region));
                CarRiskFactors output = service.getCarRiskFactors(new CarRiskFactorRequest(0, region.id(), null, type));
                Assertions.assertNotNull(output.vehicle());
            } catch (IllegalStateException ex) {
                fail("Missing implementation in RiskDataService.getVehicleFactor: VehicleEnum." + type);
            }
        }
    }

    @Test
    void testGetRegionFactor() {
        for (StateEnum type : StateEnum.values()) {
            try {
                Region region = new Region(12345, type, "district", "county", "city", "area");
                Mockito.when(regionDataService.getRegionById(any())).thenReturn(Optional.of(region));
                CarRiskFactors output = service.getCarRiskFactors(new CarRiskFactorRequest(0, region.id(), null, VehicleEnum.CABRIO));
                Assertions.assertNotNull(output.region());
            } catch (IllegalStateException ex) {
                fail("Missing implementation in RiskDataService.getRegionFactor: StateEnum." + type);
            }
        }
    }

}
