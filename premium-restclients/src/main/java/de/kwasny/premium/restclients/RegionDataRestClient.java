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

import de.kwasny.premium.commons.dto.Region;

import static org.springframework.http.MediaType.*;

/**
 *
 * @author Arthur Kwasny
 */
@Lazy @Service
public class RegionDataRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(RegionDataRestClient.class);

    private final RestClient restClient;

    public RegionDataRestClient(RestClient.Builder builder,
            @Value("${dataprovider.host}") String host,
            @Value("${dataprovider.port}") Integer port) {
        String endpoint = "http://%s:%s/regions/v1".formatted(host, port);
        this.restClient = builder.baseUrl(endpoint).build();
        LOG.info("Region data endpoint in use: {}", endpoint);
    }

    public Region getRegionById(UUID id) {
        return restClient.get().uri("/region/{id}", id)
                .accept(APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .retrieve().body(Region.class);
    }

    public List<Region> getRegionByPostcode(int postcode) {
        return restClient.get().uri(ub -> ub.path("/region")
                .queryParam("postcode", postcode).build())
                .accept(APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .retrieve().body(new ParameterizedTypeReference<List<Region>>() {});
    }

}
