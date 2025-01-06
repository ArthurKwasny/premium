package de.kwasny.premium.premium.test;

import java.math.BigDecimal;
import java.net.URL;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerExtension;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import de.kwasny.premium.commons.RestClientUtils;
import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.commons.dto.CarRiskFactorRequest;
import de.kwasny.premium.commons.dto.CarRiskFactors;
import de.kwasny.premium.commons.dto.enums.StateEnum;
import de.kwasny.premium.commons.dto.enums.VehicleEnum;
import de.kwasny.premium.restclients.RiskDataRestClient;

import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

/**
 * CDC consumer tests for the risk data rest client.
 *
 * @author Arthur Kwasny
 */
public class RiskDataRestClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(RiskDataRestClientTest.class);

    static final Region contractRegion = new Region(
            UUID.fromString("1aba33ab-5261-3286-95b4-865265d9e768"),
            12345, StateEnum.BB, "district", "county", "city", "area");

    static RiskDataRestClient client;

    @RegisterExtension
    static StubRunnerExtension stubRunnerExtension = new StubRunnerExtension()
            .stubsMode(StubRunnerProperties.StubsMode.CLASSPATH)
            .downloadLatestStub("de.kwasny.premium", "premium-data-provider", "stubs");

    @BeforeAll
    @AfterAll
    static void setupProps() {
        System.clearProperty("stubrunner.repository.root");
        System.clearProperty("stubrunner.classifier");
    }

    @BeforeAll
    static void setupClient() {
        URL url = stubRunnerExtension.findStubUrl("de.kwasny.premium", "premium-data-provider");
        client = new RiskDataRestClient(new RestClientUtils(new ObjectMapper()), RestClient.builder(), url.getHost(), url.getPort());
    }

    @Test
    public void validate_should_return_risk_factor_result() throws Exception {
        CarRiskFactors factorValues = client.getCarRiskFactors(new CarRiskFactorRequest(1000, contractRegion.id(), null, VehicleEnum.CABRIO));

        assertThat(factorValues.mileage()).isEqualTo(BigDecimal.valueOf(.1));
        assertThat(factorValues.region()).isEqualTo(BigDecimal.valueOf(.2));
        assertThat(factorValues.vehicle()).isEqualTo(BigDecimal.valueOf(.3));
    }

    @Test
    public void validate_should_return_400_for_negative_mileage() throws Exception {
        try {
            client.getCarRiskFactors(new CarRiskFactorRequest(-1, contractRegion.id(), null, VehicleEnum.CABRIO));
            Assertions.fail("Client error exception expected");
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(ex.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

            DocumentContext parsedJson = JsonPath.parse(ex.getResponseBodyAsString());
            assertThatJson(parsedJson).field("['type']").isEqualTo("about:blank");
            assertThatJson(parsedJson).field("['title']").isEqualTo("Bad Request");
            assertThatJson(parsedJson).field("['status']").isEqualTo(400);
            assertThatJson(parsedJson).field("['detail']").isEqualTo("Invalid request content.");
            assertThatJson(parsedJson).field("['instance']").isEqualTo("/factors/v1/car/risk");
            assertThatJson(parsedJson).field("['field_errors']").field("['mileage']").isEqualTo("must not be negative");
        }
    }

    @Test
    public void validate_should_return_400_for_invalid_region() throws Exception {
        try {
            client.getCarRiskFactors(new CarRiskFactorRequest(1000, UUID.fromString("12345678-1234-1234-1234-123456789012"), 12345, VehicleEnum.CABRIO));
            Assertions.fail("Client error exception expected");
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(ex.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

            DocumentContext parsedJson = JsonPath.parse(ex.getResponseBodyAsString());
            assertThatJson(parsedJson).field("['type']").isEqualTo("about:blank");
            assertThatJson(parsedJson).field("['title']").isEqualTo("Bad Request");
            assertThatJson(parsedJson).field("['status']").isEqualTo(400);
            assertThatJson(parsedJson).field("['detail']").isEqualTo("Invalid request content.");
            assertThatJson(parsedJson).field("['instance']").isEqualTo("/factors/v1/car/risk");
            assertThatJson(parsedJson).array("['errors']").arrayField().isEqualTo("Either regionId or postcode must be set.").value();
        }
    }

    @Test
    public void validate_should_return_400_for_invalid_region_2() throws Exception {
        try {
            client.getCarRiskFactors(new CarRiskFactorRequest(1000, null, null, VehicleEnum.CABRIO));
            Assertions.fail("Client error exception expected");
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(ex.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

            DocumentContext parsedJson = JsonPath.parse(ex.getResponseBodyAsString());
            assertThatJson(parsedJson).field("['type']").isEqualTo("about:blank");
            assertThatJson(parsedJson).field("['title']").isEqualTo("Bad Request");
            assertThatJson(parsedJson).field("['status']").isEqualTo(400);
            assertThatJson(parsedJson).field("['detail']").isEqualTo("Invalid request content.");
            assertThatJson(parsedJson).field("['instance']").isEqualTo("/factors/v1/car/risk");
            assertThatJson(parsedJson).array("['errors']").arrayField().isEqualTo("Either regionId or postcode must be set.").value();
        }
    }

    @Test
    public void validate_should_return_400_for_invalid_vehicle() throws Exception {
        try {
            client.getCarRiskFactors(new CarRiskFactorRequest(1000, contractRegion.id(), null, "INVALID"));
            Assertions.fail("Client error exception expected");
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(ex.getResponseHeaders().getContentType()).isEqualTo(APPLICATION_PROBLEM_JSON);

            DocumentContext parsedJson = JsonPath.parse(ex.getResponseBodyAsString());
            assertThatJson(parsedJson).field("['type']").isEqualTo("about:blank");
            assertThatJson(parsedJson).field("['title']").isEqualTo("Bad Request");
            assertThatJson(parsedJson).field("['status']").isEqualTo(400);
            assertThatJson(parsedJson).field("['detail']").isEqualTo("Invalid request content.");
            assertThatJson(parsedJson).field("['instance']").isEqualTo("/factors/v1/car/risk");
            assertThatJson(parsedJson).field("['field_errors']").field("['vehicle']").isEqualTo("Invalid value. Must be one of [CABRIO, COMBI, COMPACT, COUPE, LIMOUSINE, MINIVAN, PICKUP, ROADSTER, SUV, VAN]");
        }
    }

}
