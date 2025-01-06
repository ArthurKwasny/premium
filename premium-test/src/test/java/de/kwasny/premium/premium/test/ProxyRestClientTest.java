package de.kwasny.premium.premium.test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.CarRiskFactorRequest;
import de.kwasny.premium.commons.dto.PremiumResponse;
import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.commons.dto.enums.VehicleEnum;
import de.kwasny.premium.restclients.ProxyRestClient;

import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import static de.kwasny.premium.premium.test.RegionDataRestClientTest.contractRegion;

/**
 *
 * @author Arthur Kwasny
 */
public class ProxyRestClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyRestClientTest.class);

    @RegisterExtension
    static StubRunnerExtension stubRunnerExtension = new StubRunnerExtension()
            .stubsMode(StubRunnerProperties.StubsMode.CLASSPATH)
            .downloadLatestStub("de.kwasny.premium", "premium-restproxy", "stubs");

    static ProxyRestClient client;

    static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    @AfterAll
    static void setupProps() {
        System.clearProperty("stubrunner.repository.root");
        System.clearProperty("stubrunner.classifier");
    }

    @BeforeAll
    static void setupClient() {
        URL url = stubRunnerExtension.findStubUrl("de.kwasny.premium", "premium-restproxy");
        client = new ProxyRestClient(new RestClientUtils(new ObjectMapper()), RestClient.builder(), url.getHost(), url.getPort());
    }

    @Test
    public void validate_should_return_premium_quote() throws Exception {
        CarRiskFactorRequest riskFactorInput = new CarRiskFactorRequest(1000, UUID.fromString("1aba33ab-5261-3286-95b4-865265d9e768"), null, VehicleEnum.CABRIO);
        PremiumResponse response = client.requestPremium(new CarPremiumRequest(Collections.emptyList(), riskFactorInput));

        DocumentContext parsedJson = JsonPath.parse(objectMapper.writeValueAsString(response));
        assertThatJson(parsedJson).array("['policies']").contains("['name']").isEqualTo("auto_flex");
        assertThatJson(parsedJson).array("['policies']").contains("['premium']").isEqualTo(300);
        assertThatJson(parsedJson).array("['policies']").contains("['name']").isEqualTo("drive_secure");
        assertThatJson(parsedJson).array("['policies']").contains("['premium']").isEqualTo(400);
        assertThatJson(parsedJson).array("['policies']").contains("['name']").isEqualTo("mobil_komfort");
        assertThatJson(parsedJson).array("['policies']").contains("['premium']").isEqualTo(600);
    }

    @Test
    public void validate_should_return_region_array() throws Exception {
        List<Region> regions = client.getRegionByPostcode(contractRegion.postcode());

        DocumentContext parsedJson = JsonPath.parse(objectMapper.writeValueAsString(regions));
        assertThatJson(parsedJson).array().contains("['id']").isEqualTo("1aba33ab-5261-3286-95b4-865265d9e768");
        assertThatJson(parsedJson).array().contains("['state']").isEqualTo("BB");
        assertThatJson(parsedJson).array().contains("['district']").isEqualTo("district");
        assertThatJson(parsedJson).array().contains("['county']").isEqualTo("county");
        assertThatJson(parsedJson).array().contains("['city']").isEqualTo("city");
        assertThatJson(parsedJson).array().contains("['area']").isEqualTo("area");
    }

    @Test
    public void validate_should_return_404_not_found_instead_of_region() throws Exception {
        try {
            Region region = client.getRegionById(UUID.fromString("12345678-1234-1234-1234-865265d9e768"));
            Assertions.fail("HttpClientErrorException expected but received: " + region);
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(NOT_FOUND);
            DocumentContext parsedJson = JsonPath.parse(ex.getResponseBodyAsString());
            assertThatJson(parsedJson).field("['type']").isEqualTo("about:blank");
            assertThatJson(parsedJson).field("['title']").isEqualTo("Region not found");
            assertThatJson(parsedJson).field("['status']").isEqualTo(404);
            assertThatJson(parsedJson).field("['detail']").isEqualTo("Region with given id not found");
            assertThatJson(parsedJson).field("['instance']").isEqualTo("/api/v1/region/12345678-1234-1234-1234-865265d9e768");
        }
    }

    @Test
    public void validate_should_return_404_not_found_instead_of_region_array() throws Exception {
        try {
            List<Region> regions = client.getRegionByPostcode(54321);
            Assertions.fail("HttpClientErrorException expected but received: " + Arrays.toString(regions.toArray()));
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(NOT_FOUND);
            DocumentContext parsedJson = JsonPath.parse(ex.getResponseBodyAsString());
            assertThatJson(parsedJson).field("['type']").isEqualTo("about:blank");
            assertThatJson(parsedJson).field("['title']").isEqualTo("Region not found");
            assertThatJson(parsedJson).field("['status']").isEqualTo(404);
            assertThatJson(parsedJson).field("['detail']").isEqualTo("Region with given postcode not found");
            assertThatJson(parsedJson).field("['instance']").isEqualTo("/api/v1/region");
        }
    }

}
