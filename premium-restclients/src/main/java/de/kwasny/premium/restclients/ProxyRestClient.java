package de.kwasny.premium.restclients;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.kwasny.premium.commons.RestClientUtils;
import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.PremiumResponse;
import de.kwasny.premium.commons.dto.Region;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

/**
 *
 * @author Arthur Kwasny
 */
@Lazy @Service
public class ProxyRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyRestClient.class);

    private final RestClientUtils clientUtils;

    private final RestClient client;

    public ProxyRestClient(RestClientUtils clientUtils,
            RestClient.Builder builder,
            @Value("${proxyservice.host}") String host,
            @Value("${proxyservice.port}") Integer port) {
        this.clientUtils = clientUtils;
        String endpoint = "http://%s:%s/api/v1".formatted(host, port);
        this.client = builder.baseUrl(endpoint).build();
        LOG.info("Proxy service endpoint in use: {}", endpoint);
    }

    // quote service proxy methods
    public PremiumResponse requestPremium(CarPremiumRequest request) {
        return client.post().uri("/car/quote")
                .body(request).contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .retrieve().body(PremiumResponse.class);
    }

    public List<String> getAvailableCarPolicies() {
        return client.get().uri("/car/policy")
                .accept(APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .retrieve().body(new ParameterizedTypeReference<List<String>>() {
                });
    }

    // region data service proxy methods
    public Region getRegionById(UUID id) {
        return client.get().uri("/region/{id}", id)
                .accept(APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .retrieve().body(Region.class);
    }

    public List<Region> getRegionByPostcode(int postcode) {
        return client.get().uri(ub -> ub.path("/region")
                .queryParam("postcode", postcode).build())
                .accept(APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .retrieve().body(new ParameterizedTypeReference<List<Region>>() {});
    }

}
