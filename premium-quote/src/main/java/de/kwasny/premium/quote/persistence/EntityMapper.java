package de.kwasny.premium.quote.persistence;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.InsurancePolicy;
import de.kwasny.premium.quote.persistence.entity.CarPremium;

/**
 *
 * @author Arthur Kwasny
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EntityMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "request.factors.mileage", target = "mileage")
    @Mapping(source = "request.factors.regionId", target = "regionId")
    @Mapping(source = "request.factors.postcode", target = "postcode")
    @Mapping(source = "request.factors.vehicle", target = "vehicle")
    @Mapping(source = "request.policies", target = "policyNames", qualifiedByName = "mapCollectionToString")
    @Mapping(source = "policies", target = "results", qualifiedByName = "mapPoliciesToMap")
    CarPremium mapToCarPremium(CarPremiumRequest request, List<InsurancePolicy> policies);

    @Named("mapPoliciesToMap")
    default Map<String, BigDecimal> mapPoliciesToMap(Collection<InsurancePolicy> policies) {
        if (policies == null) {
            return null;
        }

        return policies.stream().collect(Collectors.toMap(
                InsurancePolicy::name,
                InsurancePolicy::premium
        ));
    }

    @Named("mapCollectionToString")
    default String mapCollectionToString(Collection<?> collection) {
        if (collection == null) {
            return null;
        }

        return collection.stream()
                .map(Objects::toString)
                .reduce((str1, str2) -> str1 + "," + str2)
                .orElse(null);
    }
}
