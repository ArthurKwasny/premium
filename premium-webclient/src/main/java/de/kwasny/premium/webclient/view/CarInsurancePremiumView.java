package de.kwasny.premium.webclient.view;

import java.util.Optional;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import de.kwasny.premium.commons.dto.CarPremiumRequest;
import de.kwasny.premium.commons.dto.CarRiskFactorRequest;
import de.kwasny.premium.commons.dto.InsurancePolicy;
import de.kwasny.premium.commons.dto.PremiumResponse;
import de.kwasny.premium.commons.dto.Region;
import de.kwasny.premium.commons.dto.enums.VehicleEnum;
import de.kwasny.premium.restclients.ProxyRestClient;
import de.kwasny.premium.webclient.component.Card;
import de.kwasny.premium.webclient.component.RegionSelect;

import static de.kwasny.premium.webclient.ClientUtil.ERR_NO_INPUT;
import static de.kwasny.premium.webclient.ClientUtil.displayErrorNotification;

/**
 * A view for car insurance calculation, using a proxied calculation service.
 * Uses a proxied region service to retrieve regional data. Contains 3 input
 * fields for risk data and a dialog to select a region if necessary.
 *
 * @author Arthur Kwasny
 */
@Route("")
public class CarInsurancePremiumView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(CarInsurancePremiumView.class);

    private final ProxyRestClient restClient;

    private final Binder<CarRiskFactorRequest> requestBinder;
    private final FormLayout resultLayout;

    @Autowired
    public CarInsurancePremiumView(ProxyRestClient restClient) {
        this.restClient = restClient;

        // create components
        requestBinder = new Binder<>(CarRiskFactorRequest.class);

        // title
        H1 title = new H1(getTranslation("calcview.title"));

        // mileage input field
        IntegerField mileageField = new IntegerField();
        mileageField.setLabel(getTranslation("calcview.mileage.label"));
        mileageField.setSuffixComponent(new Div(getTranslation("calcview.mileage.suffix")));
        mileageField.setTooltipText(getTranslation("calcview.mileage.ttip"));
        mileageField.setValueChangeMode(ValueChangeMode.LAZY);
        mileageField.setClearButtonVisible(true);
        mileageField.setAutofocus(true);
        requestBinder.forField(mileageField)
                .asRequired(getTranslation(ERR_NO_INPUT))
                .bind("mileage");

        // postcode input field
        RegionSelect regionSelect = new RegionSelect(postcode -> {
            try {
                return Optional.of(restClient.getRegionByPostcode(postcode));
            } catch (HttpClientErrorException.NotFound ex) {
                return Optional.empty();
            }
        });
        requestBinder.forField(regionSelect.getPostcodeField())
                .asRequired(getTranslation(ERR_NO_INPUT))
                .withValidator(regionSelect)
                .withConverter(regionSelect)
                .bind("regionId");
        requestBinder.forField(regionSelect.getCitySelect())
                .asRequired()
                .withConverter(Region::id, id -> null)
                .bindReadOnly("regionId");


        // vehicle selection
        Select<VehicleEnum> vehicleBox = new Select<>();
        vehicleBox.setLabel(getTranslation("calcview.vehicle.label"));
        vehicleBox.setTooltipText(getTranslation("calcview.vehicle.ttip"));
        vehicleBox.setItems(VehicleEnum.values());
        vehicleBox.setItemLabelGenerator(type -> getTranslation("vehicle.name." + type));
        requestBinder.forField(vehicleBox)
                .asRequired(getTranslation(ERR_NO_INPUT))
                .bind("vehicleAsEnum");

        // submit button
        Button submitButton = new Button();
        submitButton.setText(getTranslation("calcview.submit.button"));
        submitButton.setIcon(VaadinIcon.EURO.create());
        submitButton.setEnabled(false);
        submitButton.addClickListener(this::submitRequest);
        submitButton.setDisableOnClick(true);
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        requestBinder.addValueChangeListener(event -> {
            boolean isChanged = requestBinder.hasChanges();
            submitButton.setEnabled(isChanged && requestBinder.isValid());
            if (isChanged) {
                hideResults();
            }
        });

        // compose layouts
        FormLayout.ResponsiveStep[] steps = {
            new FormLayout.ResponsiveStep("0rem", 1),
            new FormLayout.ResponsiveStep("35rem", 2),
            new FormLayout.ResponsiveStep("45rem", 3),
        };

        // input form
        FormLayout userInputForm = new FormLayout(mileageField, regionSelect, vehicleBox);
        userInputForm.setResponsiveSteps(steps);

        // result display
        resultLayout = new FormLayout();
        resultLayout.addClassName("result-cards");
        resultLayout.setVisible(false);
        resultLayout.setResponsiveSteps(steps);

        add(title, userInputForm, submitButton, resultLayout);
    }

    private void submitRequest(ComponentEvent<Button> event) {
        try {
            hideResults();

            CarPremiumRequest request = new CarPremiumRequest();
            request.setFactors(new CarRiskFactorRequest());
            requestBinder.writeBean(request.getFactors());
            PremiumResponse response = restClient.requestPremium(request);

            updateResults(response);
            showResults();
        } catch (ValidationException ex) {
            for (BindingValidationStatus<?> status : ex.getFieldValidationErrors()) {
                LOG.info("Validation status: {}", status.getField());
                if (status.isError() && status.getField() instanceof Focusable f) {
                    f.focus();
                    break;
                }
            }
        } catch (RestClientException ex) {
            displayErrorNotification(getTranslation("notification.server.error"));
            LOG.error("Premium calculation request failed", ex);
            event.getSource().setEnabled(true);
        }
    }

    private void showResults() {
        resultLayout.setVisible(true);
    }

    private void hideResults() {
        resultLayout.setVisible(false);
    }

    private void updateResults(PremiumResponse response) {
        resultLayout.removeAll();
        for (InsurancePolicy policy : response.policies()) {
            Card resultCard = createResultCard(policy);
            String price = getTranslation("price.output", policy.premium());
            resultCard.setPrice(price);
            resultLayout.add(resultCard);
        }
    }

    private Card createResultCard(InsurancePolicy policy) {
        return new Card(getTranslation("policy.name." + policy.name()),
                getTranslation("policy.desc." + policy.name()),
                event -> displayErrorNotification(getTranslation("notification.server.error")));
    }
}
