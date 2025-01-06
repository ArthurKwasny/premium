package de.kwasny.premium.quote.persistence.entity;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import de.kwasny.premium.commons.dto.enums.VehicleEnum;

import static jakarta.persistence.EnumType.STRING;

/**
 * A car premium quote which contains request and result data.
 *
 * @author Arthur Kwasny
 */
@Entity @Table(name = "car_premium")
public class CarPremium extends Premium {

    private Integer mileage;
    private UUID regionId;
    private Integer postcode;
    private VehicleEnum vehicle;

    public CarPremium() {
    }

    public CarPremium(Long id, Integer mileage, UUID regionId, Integer postcode,
            VehicleEnum vehicle, Map<String, BigDecimal> results) {
        super(id, results);
        this.mileage = mileage;
        this.regionId = regionId;
        this.postcode = postcode;
        this.vehicle = vehicle;
    }

    @Column(name = "mileage", nullable = false)
    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    @Column(name = "region_id")
    public UUID getRegionId() {
        return regionId;
    }

    public void setRegionId(UUID regionId) {
        this.regionId = regionId;
    }

    @Column(name = "postcode")
    public Integer getPostcode() {
        return postcode;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }

    @Enumerated(STRING)
    @Column(name = "vehicle", nullable = false)
    public VehicleEnum getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleEnum vehicle) {
        this.vehicle = vehicle;
    }

}
