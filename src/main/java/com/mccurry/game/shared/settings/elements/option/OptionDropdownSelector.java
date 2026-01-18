package com.mccurry.game.shared.settings.elements.option;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.mccurry.game.shared.ui.elements.UiScalableSelector;
import com.mccurry.game.shared.ui.util.FontUtil;
import com.mccurry.game.shared.ui.util.TextureUtil;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.Selector;
import com.simsilica.lemur.core.VersionedList;

import java.util.List;


public class OptionDropdownSelector<T> extends OptionSelector {
    private final Selector<T> selector;

    public OptionDropdownSelector(
            String optionName,
            List<T> list,
            com.google.common.base.Function<T, String> stringTransform,
            int maxVisibleItems,
            Command<ListBox> onDropdownClick,
            AssetManager assetManager) {
        super(optionName, assetManager);
        var font = FontUtil.loadPixelPerfectFont(assetManager,"Interface/Fonts/settings/settings7px.fnt");
        var background = TextureUtil.loadPixelPerfectTexture(assetManager, "Interface/GUI/Settings/SettingSelectorBackground.png");
        selector = new UiScalableSelector<>(VersionedList.wrap(list),stringTransform,font,background);
        selector.setMaximumVisibleItems(maxVisibleItems);

        selector.getListBox().addClickCommands(onDropdownClick);

        selectorContainer.addChild(selector);
        layout();
    }

    private void layout() {
        selector.setPreferredSize(
                new Vector3f(
                        MAX_SELECTOR_SIZE_X,
                        12,
                        1
                )
        );
        float width = selector.getPreferredSize().x;
        Vector3f popupSize = selector.getPopupContainer().getPreferredSize().clone();
        popupSize.setX(width);
        selector.getPopupContainer().setPreferredSize(popupSize);
    }

    public void setSelectedItem(T dropdownItem){
        selector.setSelectedItem(dropdownItem);
    }
}
