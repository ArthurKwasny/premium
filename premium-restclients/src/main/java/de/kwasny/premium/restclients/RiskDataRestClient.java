package de.kwasny.premium.restclients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import de.kwasny.premium.commons.RestClientUtils;
import de.kwasny.premium.commons.dto.CarRiskFactorRequest;
import de.kwasny.premium.commons.dto.CarRiskFactors;

import static org.springframework.http.MediaType.*;

/**
 * Rest client for the risk data service.
 *
 * @author Arthur Kwasny
 */
@Lazy @Service
public class RiskDataRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(RiskDataRestClient.class);

    private final RestClientUtils clientUtils;

    private final RestClient restClient;


    public RiskDataRestClient(RestClientUtils clientUtils,
            RestClient.Builder builder,
            @Value("${dataprovider.host}") String host,
            @Value("${dataprovider.port}") Integer port) {
        this.clientUtils = clientUtils;
        String endpoint = "http://%s:%s/factors/v1".formatted(host, port);
        this.restClient = builder.baseUrl(endpoint).build();
        LOG.info("Risk data endpoint in use: {}", endpoint);
    }

    public CarRiskFactors getCarRiskFactors(CarRiskFactorRequest request) {
        MultiValueMap<String, String> queryParams = clientUtils.toQueryParams(request);
        return restClient.get().uri(ub -> ub.path("/car/risk")
                .queryParams(queryParams).build())
                .accept(APPLICATION_JSON, APPLICATION_PROBLEM_JSON)
                .retrieve().body(CarRiskFactors.class);
    }

}
