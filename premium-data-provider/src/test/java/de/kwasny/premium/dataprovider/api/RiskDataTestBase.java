package de.kwasny.premium.dataprovider.api;

import java.math.BigDecimal;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import de.kwasny.premium.commons.GlobalExceptionHandler;
import de.kwasny.premium.commons.dto.CarRiskFactors;
import de.kwasny.premium.dataprovider.service.RiskDataService;

import static org.mockito.ArgumentMatchers.any;

/**
 * Test base class for CDC tests of risk data endpoint.
 *
 * @author Arthur Kwasny
 */
@WebMvcTest(RiskDataController.class)
@ContextConfiguration(classes = {
    RiskDataController.class,
    GlobalExceptionHandler.class,
})
public class RiskDataTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(RiskDataTestBase.class);

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RiskDataService riskDataMock;

    private final CarRiskFactors validOutput = new CarRiskFactors(
            BigDecimal.valueOf(.1),
            BigDecimal.valueOf(.2),
            BigDecimal.valueOf(.3)
    );

    @BeforeEach
    public void setup() {
        Mockito.when(riskDataMock.getCarRiskFactors(any())).thenReturn(validOutput);
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

}
