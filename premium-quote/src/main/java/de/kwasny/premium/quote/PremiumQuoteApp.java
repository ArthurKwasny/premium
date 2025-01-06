package de.kwasny.premium.quote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of quote service.
 *
 * @author Arthur Kwasny
 */
@SpringBootApplication(scanBasePackages = "de.kwasny.premium")
public class PremiumQuoteApp {

    public static void main(String[] args) {
        SpringApplication.run(PremiumQuoteApp.class, args);
    }

}
