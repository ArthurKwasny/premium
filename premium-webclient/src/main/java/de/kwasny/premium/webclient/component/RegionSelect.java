package de.kwasny.premium.webclient.component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import de.kwasny.premium.commons.dto.Region;

import static de.kwasny.premium.webclient.ClientUtil.displayErrorNotification;

/**
 * Composition of a postcode input field and region select.
 * Implements Validator and Converter for postcode field binding, eg:
 * {@snippet :
 * RegionSelect regionSelect = new RegionSelect(...);
 * requestBinder.forField(regionSelect.getPostcodeField())
 *      .asRequired(getTranslation(ERR_NO_INPUT))
 *      .withValidator(regionSelect)
 *      .withConverter(regionSelect)
 *      .bind("regionId");
 * }
 *
 * @author Arthur Kwasny
 */
public class RegionSelect extends Composite<HorizontalLayout>
        implements Validator<Integer>, Converter<Integer, UUID> {

    private static final Logger LOG = LoggerFactory.getLogger(RegionSelect.class);

    /**
     * Postcode input field.
     */
    private final IntegerField postcodeField;
    /**
     * Region select. Might be auto-selected and read-only in case less than two
     * regions exist for a postcode.
     */
    private final Select<Region> citySelect;

    /**
     * Function used to update regionSelect items.
     */
    private final Function<Integer, Optional<List<Region>>> provideRegions;

    /**
     * Regions mapped by user requested postcode.
     */
    private final Map<Integer, List<Region>> regionMap = new HashMap<>();

    public RegionSelect(Function<Integer, Optional<List<Region>>> provideRegions) {
        this.provideRegions = provideRegions;

        postcodeField = new IntegerField();
        postcodeField.setLabel(getTranslation("regionSelect.postcode.label"));
        postcodeField.setTooltipText(getTranslation("regionSelect.postcode.ttip"));
        postcodeField.setValueChangeMode(ValueChangeMode.LAZY);
        postcodeField.setClearButtonVisible(true);
        postcodeField.setRequired(true);
        postcodeField.addValueChangeListener(event -> onPostcodeChange(event.getValue()));

        citySelect = new Select<>();
        citySelect.setLabel(getTranslation("regionSelect.region.label"));
        citySelect.setTooltipText(getTranslation("regionSelect.region.ttip"));
        citySelect.setItemLabelGenerator(Region::fullCity);
        citySelect.setReadOnly(true);

        getContent().addClassName("region-composite");
        getContent().add(postcodeField, citySelect);
    }

    public IntegerField getPostcodeField() {
        return postcodeField;
    }

    public Select<Region> getCitySelect() {
        return citySelect;
    }

    /**
     * Updates {@link #citySelect} items based on given postcode.
     *
     * @param postcode
     */
    private void onPostcodeChange(Integer postcode) {
        LOG.debug("Updating city select items for {}", postcode);
        citySelect.setReadOnly(true);
        citySelect.setValue(null);

        List<Region> regions = getRegions(postcode);
        if (regions != null) {
            citySelect.setItems(regions);
            citySelect.setReadOnly(regions.size() <= 1);
            if (!regions.isEmpty()) {
                citySelect.setValue(regions.get(0));
            }
        }
    }

    private List<Region> getRegions(Integer postcode) {
        if (postcode == null) {
            return Collections.emptyList();
        }

        return regionMap.computeIfAbsent(postcode, p -> {
            try {
                return provideRegions.apply(p).orElse(Collections.emptyList());
            } catch (HttpServerErrorException | ResourceAccessException ex) {
                displayErrorNotification(getTranslation("regionSelect.notification.server.error.postcodevalidation"));
                LOG.error("Failed to retrieve regions: {}", ex.getMessage());
                return null;
            }
        });
    }

    // implements Validator
    @Override
    public ValidationResult apply(Integer postcode, ValueContext context) {
        if (regionMap.get(postcode) != null && !regionMap.get(postcode).isEmpty()) {
            return ValidationResult.ok();
        }
        return ValidationResult.error(getTranslation("regionSelect.error.invalidPostcode"));
    }

    // implements Converter
    @Override
    public Result<UUID> convertToModel(Integer postcode, ValueContext context) {
        return Result.ok(citySelect.getValue().id());
    }

    @Override
    public Integer convertToPresentation(UUID id, ValueContext context) {
        if (id == null) {
            return null;
        }
        return citySelect.getValue().postcode();
    }

}
