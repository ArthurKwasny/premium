package de.kwasny.premium.premium.test;

import java.net.URL;
import java.util.Arrays;
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

import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.commons.dto.enums.StateEnum;
import de.kwasny.premium.restclients.RegionDataRestClient;

import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

/**
 * CDC consumer tests for the risk data rest client.
 *
 * @author Arthur Kwasny
 */
public class RegionDataRestClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(RegionDataRestClientTest.class);

    static final Region contractRegion = new Region(12345, StateEnum.BB, "district", "county", "city", "area");

    @RegisterExtension
    static StubRunnerExtension stubRunnerExtension = new StubRunnerExtension()
            .stubsMode(StubRunnerProperties.StubsMode.CLASSPATH)
            .downloadLatestStub("de.kwasny.premium", "premium-data-provider", "stubs");

    static RegionDataRestClient client;

    static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    @AfterAll
    static void setupProps() {
        System.clearProperty("stubrunner.repository.root");
        System.clearProperty("stubrunner.classifier");
    }

    @BeforeAll
    static void setupClient() {
        URL url = stubRunnerExtension.findStubUrl("de.kwasny.premium", "premium-data-provider");
        client = new RegionDataRestClient(RestClient.builder(), url.getHost(), url.getPort());
    }

    @Test
    public void validate_should_return_region() throws Exception {
        Region region = client.getRegionById(contractRegion.id());
        DocumentContext parsedJson = JsonPath.parse(objectMapper.writeValueAsString(region));

        assertThatJson(parsedJson).field("['id']").isEqualTo("1aba33ab-5261-3286-95b4-865265d9e768");
        assertThatJson(parsedJson).field("['postcode']").isEqualTo(12345);
        assertThatJson(parsedJson).field("['state']").isEqualTo("BB");
        assertThatJson(parsedJson).field("['district']").isEqualTo("district");
        assertThatJson(parsedJson).field("['county']").isEqualTo("county");
        assertThatJson(parsedJson).field("['city']").isEqualTo("city");
        assertThatJson(parsedJson).field("['area']").isEqualTo("area");
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
    public void validate_should_return_404_not_found_instead_of_region() {
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
            assertThatJson(parsedJson).field("['instance']").isEqualTo("/regions/v1/region/12345678-1234-1234-1234-865265d9e768");
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
            assertThatJson(parsedJson).field("['instance']").isEqualTo("/regions/v1/region");
        }
    }

}
