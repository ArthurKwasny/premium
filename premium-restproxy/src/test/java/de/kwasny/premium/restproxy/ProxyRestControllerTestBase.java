package de.kwasny.premium.restproxy;

import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerExtension;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import de.kwasny.premium.commons.GlobalExceptionHandler;
import de.kwasny.premium.commons.RestClientUtils;
import de.kwasny.premium.restclients.QuoteRestClient;
import de.kwasny.premium.restclients.RegionDataRestClient;
import de.kwasny.premium.restproxy.ProxyRestControllerTestBase.ClientTestConfiguration;
import de.kwasny.premium.restproxy.api.RestProxyController;

import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.INHERIT;

/**
 * Test base for {@link RestProxyController} CDC provider tests. Uses generated
 * stubs for proxied service clients.
 *
 * @author Arthur Kwasny
 */
@WebMvcTest(RestProxyController.class)
@Import(ClientTestConfiguration.class)
@ContextConfiguration(classes = {
    ClientTestConfiguration.class,
    RestProxyController.class,
    GlobalExceptionHandler.class,
    RestClientUtils.class,
})
public abstract class ProxyRestControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    static StubRunnerExtension quoteStubRunnerExtension = new StubRunnerExtension()
            .stubsMode(StubRunnerProperties.StubsMode.LOCAL)
            .downloadLatestStub("de.kwasny.premium", "premium-quote", "stubs");

    @RegisterExtension
    static StubRunnerExtension dataStubRunnerExtension = new StubRunnerExtension()
            .stubsMode(StubRunnerProperties.StubsMode.LOCAL)
            .downloadLatestStub("de.kwasny.premium", "premium-data-provider", "stubs");

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @NestedTestConfiguration(INHERIT)
    static class ClientTestConfiguration {

        @Bean
        public QuoteRestClient quoteRestClient(RestClientUtils clientUtils) {
            URL url = quoteStubRunnerExtension.findStubUrl("de.kwasny.premium", "premium-quote");
            return new QuoteRestClient(clientUtils, RestClient.builder(), url.getHost(), url.getPort());
        }

        @Bean
        public RegionDataRestClient regionDataRestClient(ObjectMapper objectMapper) {
            URL url = dataStubRunnerExtension.findStubUrl("de.kwasny.premium", "premium-data-provider");
            return new RegionDataRestClient(RestClient.builder(), url.getHost(), url.getPort());
        }
    }
}
