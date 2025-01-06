package de.kwasny.premium.commons.dto;

import java.beans.Transient;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import de.kwasny.premium.commons.dto.enums.StateEnum;

/**
 * A record of postcodes.csv, used by region data service.
 *
 * @author Arthur Kwasny
 */
public record Region(UUID id, int postcode, StateEnum state, String district,
        String county, String city, String area) {

    /**
     * Creates a region with a generated UUID based on given inputs.
     */
    public Region(int postcode, StateEnum state, String district, String county, String city, String area) {
        // ID generation should not be changed unless nececssary.
        // a change would require a migration of regionIds in the persistence layer
        this(createId(postcode, state, district, county, city, area),
                postcode, state, district, county, city, area);
    }

    @Transient
    public String fullCity() {
        if (area == null || area.isBlank()) {
            return city;
        }
        return "%s (%s)".formatted(city, area);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + this.postcode;
        hash = 97 * hash + Objects.hashCode(this.state);
        hash = 97 * hash + Objects.hashCode(this.district);
        hash = 97 * hash + Objects.hashCode(this.county);
        hash = 97 * hash + Objects.hashCode(this.city);
        hash = 97 * hash + Objects.hashCode(this.area);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Region other = (Region) obj;
        if (this.postcode != other.postcode) {
            return false;
        }
        if (!Objects.equals(this.district, other.district)) {
            return false;
        }
        if (!Objects.equals(this.county, other.county)) {
            return false;
        }
        if (!Objects.equals(this.city, other.city)) {
            return false;
        }
        if (!Objects.equals(this.area, other.area)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return this.state == other.state;
    }

    @Override
    public String toString() {
        return "Region{"
                + "id=" + id
                + ", postcode=" + postcode
                + ", state=" + state
                + ", district=" + district
                + ", county=" + county
                + ", city=" + city
                + ", area=" + area
                + '}';
    }

    /**
     * Creates a UUID based on given values.
     */
    public static UUID createId(Object... values) {
        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            sb.append(value);
        }
        return UUID.nameUUIDFromBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

}
