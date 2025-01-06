package de.kwasny.premium.commons.dto;

import java.util.UUID;

/**
 * Provides access to region identifiers like ID or postcode.
 *
 * @author Arthur Kwasny
 */
public interface RegionIdentifier {
    UUID getRegionId();
    Integer getPostcode();
}
