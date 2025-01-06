package de.kwasny.premium.quote.service;

import java.math.BigDecimal;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.CarRiskFactorRequest;
import de.kwasny.premium.commons.dto.CarRiskFactors;
import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.commons.dto.enums.StateEnum;
import de.kwasny.premium.commons.dto.enums.VehicleEnum;
import de.kwasny.premium.quote.persistence.CarPremiumRepository;
import de.kwasny.premium.quote.persistence.entity.CarPremium;
import de.kwasny.premium.restclients.RiskDataRestClient;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author Arthur Kwasny
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
@Transactional
public class PremiumsServiceIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(PremiumsServiceIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarPremiumRepository repository;

    @MockitoBean
    private RiskDataRestClient riskData;

    @Autowired
    private ObjectMapper objectMapper;

    private Region testRegion = new Region(12345, StateEnum.BB, "district", "county", "city", "area");
    CarRiskFactorRequest riskFactorInput = new CarRiskFactorRequest(1000, testRegion.id(), null, VehicleEnum.CABRIO);
    private CarPremiumRequest testRequest = new CarPremiumRequest(Collections.emptyList(), riskFactorInput);

    @Test
    void testCreateCarPremium() throws Exception {
        Mockito.when(riskData.getCarRiskFactors(any()))
                .thenReturn(new CarRiskFactors(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));
        String content = objectMapper.writeValueAsString(new CarPremiumRequest(null, riskFactorInput));

        mockMvc.perform(post("/premiums/v1/car/quote")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policies").isArray())
                .andExpect(jsonPath("$.policies[0].name").value("auto_flex"))
                .andExpect(jsonPath("$.policies[0].premium").value(300))
                .andExpect(jsonPath("$.policies[1].name").value("drive_secure"))
                .andExpect(jsonPath("$.policies[1].premium").value(400))
                .andExpect(jsonPath("$.policies[2].name").value("mobil_komfort"))
                .andExpect(jsonPath("$.policies[2].premium").value(600));

        Assertions.assertEquals(1, repository.count());
        CarPremium premium = repository.findAll().get(0);

        Assertions.assertEquals((Integer) 1000, premium.getMileage());
        Assertions.assertEquals(testRegion.id(), premium.getRegionId());
        Assertions.assertEquals(VehicleEnum.CABRIO, premium.getVehicle());
        Assertions.assertEquals(3, premium.getResults().size());
    }

    @Test
    void testCreateCarPremiumWithInvalidFactors() throws Exception {
        Mockito.when(riskData.getCarRiskFactors(any()))
                .thenReturn(new CarRiskFactors(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE));
        String content = objectMapper.writeValueAsString(new CarPremiumRequest(null, riskFactorInput));

        mockMvc.perform(post("/premiums/v1/car/quote")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.detail").value("Received invalid factors from risk data service."));

        Assertions.assertEquals(0, repository.count());
    }
}
