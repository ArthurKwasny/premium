package de.kwasny.premium.dataprovider.service;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.kwasny.premium.commons.dto.Region;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Arthur Kwasny
 */
@ExtendWith(SpringExtension.class)
public class RegionDataServiceTest {

    @Value("classpath:postcodes.csv")
    private Resource postcodesDefault;

    /**
     * Ensures postcodes.csv in classpath can be parsed.
     * @throws IOException
     */
    @Test
    public void testParsePostcodesCsv() throws IOException {
        List<Region> regions = RegionDataService.parsePostcodesCsv(postcodesDefault);
        assertFalse(regions.isEmpty());
        assertNotNull(regions.get(0).id());
        assertNotNull(regions.get(0).state());
    }

}
