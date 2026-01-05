package com.mccurry.game.shared.ui;

import com.mccurry.game.shared.ui.model.UiResolutionSettings;

public interface RedrawableElement {
    void resizeForResolution(UiResolutionSettings resolutionSettings);
}
