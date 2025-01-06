package de.kwasny.premium.dataprovider.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import de.kwasny.premium.commons.GlobalExceptionHandler;
import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.commons.dto.enums.StateEnum;
import de.kwasny.premium.dataprovider.service.RegionDataService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

/**
 * Test base class for CDC tests of region data endpoint.
 *
 * @author Arthur Kwasny
 */
@WebMvcTest(RegionDataController.class)
@ContextConfiguration(classes = {
    RegionDataController.class,
    RegionDataService.class,
    GlobalExceptionHandler.class,
})
public abstract class RegionDataTestBase {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RegionDataService serviceMock;

    @BeforeEach
    public void setUp() {
        Region validRegion = new Region(12345, StateEnum.BB, "district", "county", "city", "area");
        Mockito.when(serviceMock.getRegionsByPostcode(anyInt()))
                .thenAnswer(invocation -> {
                    if (invocation.getArgument(0, Integer.class) == 12345) {
                        return List.of(validRegion);
                    }
                    return Collections.emptyList();
                });
        Mockito.when(serviceMock.getRegionById(any()))
                .thenAnswer(invocation -> {
                    if (invocation.getArgument(0, UUID.class).equals(validRegion.id())) {
                        return Optional.of(validRegion);
                    }
                    return Optional.empty();
                });
        RestAssuredMockMvc.mockMvc(mockMvc);
    }
}
