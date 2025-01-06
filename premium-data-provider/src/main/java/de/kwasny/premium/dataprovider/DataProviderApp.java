package de.kwasny.premium.dataprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the risk data provider.
 *
 * @author Arthur Kwasny
 */
@SpringBootApplication(scanBasePackages = "de.kwasny.premium")
public class DataProviderApp {

    public static void main(String[] args) {
        SpringApplication.run(DataProviderApp.class, args);
    }
}
