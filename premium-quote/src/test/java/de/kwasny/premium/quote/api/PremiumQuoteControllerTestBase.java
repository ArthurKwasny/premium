package de.kwasny.premium.quote.api;

import java.math.BigDecimal;
import java.util.List;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import de.kwasny.premium.commons.GlobalExceptionHandler;
import de.kwasny.premium.commons.dto.InsurancePolicy;
import de.kwasny.premium.commons.dto.PremiumResponse;
import de.kwasny.premium.quote.service.PolicyService;
import de.kwasny.premium.quote.service.PremiumQuoteService;

import static org.mockito.ArgumentMatchers.any;

/**
 * Test base for {@link PremiumQuoteController} CDC provider tests.
 *
 * @author Arthur Kwasny
 */
@WebMvcTest(PremiumQuoteController.class)
@ContextConfiguration(classes = {
    PremiumQuoteController.class,
    PremiumQuoteService.class,
    PolicyService.class,
    GlobalExceptionHandler.class,
})
public abstract class PremiumQuoteControllerTestBase {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PremiumQuoteService serviceMock;

    @BeforeEach
    public void setUp() {
        Mockito.when(serviceMock.createPremium(any()))
                .thenAnswer(invocation -> {
                    return new PremiumResponse(List.of(
                            new InsurancePolicy("auto_flex", BigDecimal.valueOf(300)),
                            new InsurancePolicy("drive_secure", BigDecimal.valueOf(400)),
                            new InsurancePolicy("mobil_komfort", BigDecimal.valueOf(600))
                    ));
                });
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

}
