package de.kwasny.premium.quote.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;


/**
 * Base entity for premium quotes.
 *
 * @author Arthur Kwasny
 */
@MappedSuperclass
public abstract class Premium implements Serializable {

    private Long id;

    private ZonedDateTime createdAt;

    /**
     * Comma-separated list of requested policies.
     */
    private String policyNames;

    /**
     * Contains premium results mapped by policy id.
     */
    private Map<String, BigDecimal> results;

    protected Premium() {
    }

    protected Premium(Long id, Map<String, BigDecimal> results) {
        this.id = id;
        this.results = results;
    }

    @Id @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @CreationTimestamp
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "policies_requested")
    public String getPolicyNames() {
        return policyNames;
    }

    public void setPolicyNames(String policyNames) {
        this.policyNames = policyNames;
    }

    @ElementCollection
    @MapKeyColumn(name = "policy_id")
    @Column(name = "premium")
    public Map<String, BigDecimal> getResults() {
        return results;
    }

    public void setResults(Map<String, BigDecimal> results) {
        this.results = results;
    }

}
