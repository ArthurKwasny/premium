package de.kwasny.premium.premium.test;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Collections;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerExtension;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.web.client.RestClient;

import de.kwasny.premium.commons.RestClientUtils;
import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.PremiumResponse;
import de.kwasny.premium.commons.dto.CarRiskFactorRequest;
import de.kwasny.premium.commons.dto.enums.VehicleEnum;
import de.kwasny.premium.restclients.QuoteRestClient;

import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;

/**
 *
 * @author Arthur Kwasny
 */
public class QuoteRestClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteRestClientTest.class);

    static QuoteRestClient client;

    @RegisterExtension
    static StubRunnerExtension stubRunnerExtension = new StubRunnerExtension()
            .stubsMode(StubRunnerProperties.StubsMode.CLASSPATH)
            .downloadLatestStub("de.kwasny.premium", "premium-quote", "stubs");

    @BeforeAll
    @AfterAll
    static void setupProps() {
        System.clearProperty("stubrunner.repository.root");
        System.clearProperty("stubrunner.classifier");
    }

    @BeforeAll
    static void setupClient() {
        URL url = stubRunnerExtension.findStubUrl("de.kwasny.premium", "premium-quote");
        client = new QuoteRestClient(new RestClientUtils(new ObjectMapper()), RestClient.builder(), url.getHost(), url.getPort());
    }

    @Test
    public void validate_should_return_premium_quote() throws Exception {

        CarRiskFactorRequest riskFactorInput = new CarRiskFactorRequest(1000, UUID.fromString("1aba33ab-5261-3286-95b4-865265d9e768"), null, VehicleEnum.CABRIO);
        PremiumResponse response = client.createCarPremium(new CarPremiumRequest(Collections.emptyList(), riskFactorInput));

        assertThat(response.policies()).anySatisfy(p -> assertThat(p.name()).isEqualTo("auto_flex"));
        assertThat(response.policies()).anySatisfy(p -> assertThat(p.premium()).isEqualTo(BigDecimal.valueOf(300)));
        assertThat(response.policies()).anySatisfy(p -> assertThat(p.name()).isEqualTo("drive_secure"));
        assertThat(response.policies()).anySatisfy(p -> assertThat(p.premium()).isEqualTo(BigDecimal.valueOf(400)));
        assertThat(response.policies()).anySatisfy(p -> assertThat(p.name()).isEqualTo("mobil_komfort"));
        assertThat(response.policies()).anySatisfy(p -> assertThat(p.premium()).isEqualTo(BigDecimal.valueOf(600)));
    }
}
