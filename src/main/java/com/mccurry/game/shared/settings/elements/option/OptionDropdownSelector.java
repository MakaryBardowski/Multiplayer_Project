package com.mccurry.game.shared.settings.elements.option;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.mccurry.game.shared.ui.model.UiResolutionSettings;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.Selector;
import com.simsilica.lemur.core.VersionedList;

public class OptionDropdownSelector<T> extends OptionSelector {
    private final Selector<T> selector;

    public OptionDropdownSelector(
            String optionName,
            VersionedList<T> model,
            com.google.common.base.Function<T, String> stringTransform,
            int maxVisibleItems,
            Command<ListBox> onDropdownClick,
            AssetManager assetManager) {
        super(optionName, assetManager);
        selector = new Selector<>(model, stringTransform);
        selector.setMaximumVisibleItems(maxVisibleItems);
        selector.getListBox().addClickCommands(onDropdownClick);

        this.addChild(selector);
    }

    @Override
    public void resizeForResolution(UiResolutionSettings resolutionSettings) {
        super.resizeForResolution(resolutionSettings);

        selector.setPreferredSize(
                resolutionSettings.getSizeFor(
                        180,
                        10,
                        1
                )
        );

        float width = selector.getPreferredSize().x;
        Vector3f popupSize = selector.getPopupContainer().getPreferredSize().clone();
        popupSize.setX(width);
        selector.getPopupContainer().setPreferredSize(popupSize);
    }

    public Vector3f getMutableLabelSize(){
        return label.getPreferredSize();
    }

    public void setSelectedItem(T dropdownItem){
        selector.setSelectedItem(dropdownItem);
    }
}
