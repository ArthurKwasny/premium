package de.kwasny.premium.webclient.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Card component with a title, description, price label and order button.
 *
 * @author Arthur Kwasny
 */
public class Card extends Composite<VerticalLayout> {

    private final H3 title;
    private final Paragraph description;
    private final Paragraph price;

    public Card(String titleText, String descriptionText,
            ComponentEventListener<ClickEvent<Button>> orderButtonAction) {

        // card layout
        VerticalLayout layout = getContent();
        layout.addClassName("card");

        // title
        title = new H3(titleText);

        // description label
        description = new Paragraph(descriptionText);
        description.addClassName("descr");

        // price label
        price = new Paragraph();
        price.addClassName("price");

        // order button
        Button orderButton = new Button(VaadinIcon.CART.create());
        orderButton.addClickListener(orderButtonAction);

        // compose layout
        HorizontalLayout bottomBar = new HorizontalLayout();
        bottomBar.addClassName("bottombar");
        bottomBar.addAndExpand(price);
        bottomBar.add(orderButton);
        layout.add(title, description, bottomBar);
        layout.setFlexGrow(1, description);
    }

    public void setTitle(String titleText) {
        title.setText(titleText);
    }

    public void setDescription(String descriptionText) {
        description.setText(descriptionText);
    }

    public void setPrice(String priceText) {
        price.setText(priceText);
    }
}
