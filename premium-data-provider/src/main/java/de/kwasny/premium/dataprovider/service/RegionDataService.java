package de.kwasny.premium.dataprovider.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import de.kwasny.premium.commons.ServiceException;
import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.commons.dto.enums.StateEnum;

/**
 * Provides services for regional data.
 *
 * @author Arthur Kwasny
 */
@Service
public class RegionDataService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionDataService.class);

    /**
     * Regions of postcodes.csv.
     */
    private List<Region> regions;

    public RegionDataService(@Value("classpath:postcodes.csv") Resource csvResource) throws IOException {
        if (!csvResource.exists()) {
            throw new ServiceException("No postcodes.csv found in classpath.");
        }

        regions = parsePostcodesCsv(csvResource);
        LOG.info("Parsed {} regions from {}.", regions.size(), csvResource.getFilename());
    }

    public List<Region> getRegionsByPostcode(int postcode) {
        LOG.debug("getRegionsByPostcode({})", postcode);
        return findRegions(r -> r.postcode() == postcode).toList();
    }

    /**
     * @param id
     * @return an optional region for given ID
     */
    public Optional<Region> getRegionById(UUID id) {
        LOG.debug("getRegionById({})", id);
        return findRegions(r -> r.id().equals(id)).findAny();
    }

    /**
     * @param filter
     * @return a stream of regions matching given filter
     */
    private Stream<Region> findRegions(Predicate<? super Region> filter) {
        return regions.stream().filter(filter);
    }

    /**
     * Maps CSV records of given resource to {@link Region} records.
     *
     * @param csvResource
     * @return
     * @throws IOException
     * @see Region
     * @see RegionCsvMixin : For CSV mapping
     * @see StateEnumCsvMixin : For ISO 3166-2:DE mapping
     */
    public static List<Region> parsePostcodesCsv(Resource csvResource) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(csvResource.getInputStream())) {
            ObjectMapper mapper = new CsvMapper()
                    .enable(CsvParser.Feature.TRIM_SPACES)
                    .addMixIn(Region.class, RegionCsvMixin.class)
                    .addMixIn(StateEnum.class, StateEnumCsvMixin.class);
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            MappingIterator<Region> iterator = mapper
                    .readerFor(Region.class)
                    .with(schema)
                    .readValues(isr);

            return iterator.readAll();
        }
    }

    /**
     * Mixin which maps postcodes.csv records to Region records.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private abstract static class RegionCsvMixin {

        @JsonCreator
        public RegionCsvMixin(
                @JsonProperty("POSTLEITZAHL") int postcode,
                @JsonProperty("ISO_3166_1_ALPHA_2_REGION_CODE") StateEnum state,
                @JsonProperty("REGION2") String district,
                @JsonProperty("REGION3") String county,
                @JsonProperty("ORT") String city,
                @JsonProperty("AREA1") String area
        ) {}
    }

    /**
     * Mixin which deserilializes ISO 3166-2:DE codes to StateEnum values.
     */
    @JsonDeserialize(using = StateEnumCsvMixin.StateEnumDeserializer.class)
    private abstract static class StateEnumCsvMixin {

        static class StateEnumDeserializer extends JsonDeserializer<StateEnum> {

            @Override
            public StateEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getText();
                try {
                    // strip off 'DE-' prefix
                    String stateValue = value.substring(3).toUpperCase();
                    return StateEnum.valueOf(stateValue);
                } catch (IllegalArgumentException | NullPointerException ex) {
                    throw ctxt.weirdStringException(value, StateEnum.class, "Invalid value");
                }
            }
        }
    }

}
