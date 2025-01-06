package de.kwasny.premium.commons.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for premium requests used by premium quote service.
 *
 * @author Arthur Kwasny
 */
public abstract class PremiumRequest {

    private List<String> policies;

    protected PremiumRequest() {
    }

    protected PremiumRequest(List<String> policies) {
        this.policies = policies;
    }

    /**
     * @return a modifiable copy of list of policy IDs to use for a premium
     *         quote, might be null
     */
    public List<String> getPolicies() {
        return policies == null ? null : new ArrayList<>(policies);
    }

    /**
     *
     * @param policies
     */
    public void setPolicies(List<String> policies) {
        this.policies = policies;
    }

}
