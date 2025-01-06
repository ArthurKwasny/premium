package de.kwasny.premium.commons.dto;

import java.beans.Transient;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import de.kwasny.premium.commons.dto.enums.VehicleEnum;
import de.kwasny.premium.commons.validation.constraints.EnumValue;
import de.kwasny.premium.commons.validation.constraints.UniqueRegionIdentifier;

/**
 * Car risk factor request model used by risk data service.
 *
 * @author Arthur Kwasny
 */
@UniqueRegionIdentifier
public class CarRiskFactorRequest implements RegionIdentifier {

    private Integer mileage;
    private UUID regionId;
    private Integer postcode;
    private String vehicle;

    public CarRiskFactorRequest() {
    }

    public CarRiskFactorRequest(int mileage, UUID regionId, Integer postcode, String vehicle) {
        this.mileage = mileage;
        this.regionId = regionId;
        this.postcode = postcode;
        this.vehicle = vehicle;
    }

    public CarRiskFactorRequest(int mileage, UUID regionId, Integer postcode, VehicleEnum vehicle) {
        this(mileage, regionId, postcode, vehicle.name());
    }

    @NotNull(message = "must not be null")
    @PositiveOrZero(message = "must not be negative")
    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    @Override
    public UUID getRegionId() {
        return regionId;
    }

    public void setRegionId(UUID regionId) {
        this.regionId = regionId;
    }

    @Override
    public Integer getPostcode() {
        return postcode;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }

    @NotNull(message = "must not be null")
    @EnumValue(enumClass = VehicleEnum.class)
    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    @Transient
    public VehicleEnum getVehicleAsEnum() {
        if (vehicle == null || vehicle.isEmpty()) {
            return null;
        }
        return VehicleEnum.valueOf(vehicle);
    }

    public void setVehicleAsEnum(VehicleEnum vehicle) {
        this.vehicle = (vehicle == null) ? null : vehicle.name();
    }

}
