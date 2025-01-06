package de.kwasny.premium.restproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the proxy service.
 *
 * @author Arthur Kwasny
 */
@SpringBootApplication(scanBasePackages = "de.kwasny.premium")
public class RestProxyApp {

    public static void main(String[] args) {
        SpringApplication.run(RestProxyApp.class, args);
    }
}
