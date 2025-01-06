package de.kwasny.premium.commons;

import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

/**
 * Utility class for rest clients.
 *
 * @author Arthur Kwasny
 */
@Component
public class RestClientUtils {

    private static final Logger LOG = LoggerFactory.getLogger(RestClientUtils.class);

    private final ObjectMapper objectMapper;

    @Autowired
    public RestClientUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Basic mapping of objects to a {@code MultiValueMap<String, String>}.
     * Supports only basic single value fields. Values must be convertible to
     * string by Jackson.
     *
     * @param obj the object to transform
     * @return a map filled with object values
     */
    public MultiValueMap<String, String> toQueryParams(Object obj) {
        HashMap<String, String> map = objectMapper.convertValue(obj,
                new TypeReference<HashMap<String, String>>() {});
        return MultiValueMap.fromSingleValue(map);
    }

}
