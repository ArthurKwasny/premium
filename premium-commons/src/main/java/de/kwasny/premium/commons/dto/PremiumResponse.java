package de.kwasny.premium.commons.dto;

import java.util.List;

/**
 * Car insurance premium response model used by premium quote service.
 *
 * @author Arthur Kwasny
 */
public record PremiumResponse(List<InsurancePolicy> policies) {

}
