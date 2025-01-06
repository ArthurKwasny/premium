package de.kwasny.premium.quote.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import de.kwasny.premium.commons.dto.Factors;
import de.kwasny.premium.commons.dto.InsurancePolicy;
import de.kwasny.premium.quote.persistence.CarPremiumRepository;
import de.kwasny.premium.restclients.RiskDataRestClient;


/**
 *
 * @author Arthur Kwasny
 */
@SpringBootTest
@ImportAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class PremiumsServiceTest {

    @Autowired
    private PremiumQuoteService service;

    @MockitoBean
    private CarPremiumRepository repository;

    @MockitoBean
    private RiskDataRestClient riskData;

    @MockitoBean
    private PolicyService policyServiceMock;

    @Test
    public void testApplyFactors() {
        Factors factors;
        BigDecimal result;
        factors = () -> Stream.<BigDecimal>of(BigDecimal.ONE, BigDecimal.valueOf(2));
        result = service.applyFactors(BigDecimal.ONE, factors);
        Assertions.assertEquals(BigDecimal.valueOf(2), result);
    }

    @Test
    public void testApplyFactorToPolicies() {
        List<InsurancePolicy> policies = List.of(
                new InsurancePolicy("t1", BigDecimal.valueOf(100)),
                new InsurancePolicy("t2", BigDecimal.valueOf(200))
        );
        List<InsurancePolicy> result;
        result = service.applyFactorToPolicies(policies, BigDecimal.valueOf(2));
        Assertions.assertEquals(policies.size(), result.size());
        Assertions.assertEquals("t1", result.get(0).name());
        Assertions.assertEquals(BigDecimal.valueOf(200), result.get(0).premium());
        Assertions.assertEquals("t2", result.get(1).name());
        Assertions.assertEquals(BigDecimal.valueOf(400), result.get(1).premium());
    }

}
