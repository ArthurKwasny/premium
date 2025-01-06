package de.kwasny.premium.quote.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import de.kwasny.premium.quote.persistence.entity.CarPremium;

/**
 *
 * @author Arthur Kwasny
 */
public interface CarPremiumRepository extends JpaRepository<CarPremium, Long> {

}
