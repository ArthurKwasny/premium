package de.kwasny.premium.webclient;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Client utility class.
 *
 * @author Arthur Kwasny
 */
public class ClientUtil {

    public static final String ERR_NO_INPUT = "error.inputRequired";

    public static void displayErrorNotification(String text) {
        Notification notification = new Notification(text, 5000);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    private ClientUtil() {
    }

}
