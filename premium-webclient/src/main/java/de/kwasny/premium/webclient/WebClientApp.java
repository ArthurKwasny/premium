package de.kwasny.premium.webclient;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of web client.
 *
 * @author Arthur Kwasny
 */
@SpringBootApplication(scanBasePackages = "de.kwasny.premium")
@Theme(value = "default-theme")
public class WebClientApp implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(WebClientApp.class, args);
    }

}
