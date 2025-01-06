package de.kwasny.premium.restclients;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.kwasny.premium.commons.RestClientUtils;
import de.kwasny.premium.commons.dto.PremiumResponse;
import de.kwasny.premium.commons.dto.CarPremiumRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

/**
 * Rest client for the quote service.
 *
 * @author Arthur Kwasny
 */
@Lazy @Service
public class QuoteRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteRestClient.class);

    private final RestClientUtils clientUtils;

    private final RestClient restClient;

    public QuoteRestClient(RestClientUtils clientUtils,
            RestClient.Builder builder,
            @Value("${quoteservice.host}") String host,
            @Value("${quoteservice.port}") Integer port) {
        this.clientUtils = clientUtils;
        String endpoint = "http://%s:%s/premiums/v1".formatted(host, port);
        this.restClient = builder.baseUrl(endpoint).build();
        LOG.info("Premium quote service endpoint in use: {}", endpoint);
    }

    public PremiumResponse createCarPremium(CarPremiumRequest request) {
        return restClient.post().uri("/car/quote")
                .body(request).contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .retrieve().body(PremiumResponse.class);
    }

    public List<String> getAvailableCarPolicies() {
        return restClient.get().uri("/car/policy")
                .accept(APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .retrieve().body(new ParameterizedTypeReference<List<String>>() {});
    }
}
