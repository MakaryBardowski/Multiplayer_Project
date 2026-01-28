package com.mccurry.game.shared.settings.elements.settingsTab;


import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.mccurry.game.shared.settings.elements.option.slider.SliderOptionSelector;
import com.mccurry.game.shared.settings.model.UserSettings;
import com.mccurry.game.shared.ui.util.ContainerUtil;
import com.simsilica.lemur.Container;

public class SoundSettingsTab extends SettingsTab {

    private UserSettings temporaryUserSettings;

    private final SliderOptionSelector masterVolumeOption;
    private final SliderOptionSelector musicVolumeOption;
    private final SliderOptionSelector sfxVolumeOption;
    private final SliderOptionSelector gunshotVolumeOption;

    private Container paddingContainer;

    public SoundSettingsTab(
            AssetManager assetManager
    ) {
        super(assetManager);

        masterVolumeOption = new SliderOptionSelector("Master_Volume",
                (s) -> {
                    temporaryUserSettings.getSoundSettings().setMasterVolume((int) Math.round(s.getModel().getValue()));
                }, assetManager);

        musicVolumeOption = new SliderOptionSelector("Music_Volume",
                (s) -> {
                    temporaryUserSettings.getSoundSettings().setMusicVolume((int) Math.round(s.getModel().getValue()));
                }, assetManager);

        sfxVolumeOption = new SliderOptionSelector("Sfx_Volume",
                (s) -> {
                    temporaryUserSettings.getSoundSettings().setSfxVolume((int) Math.round(s.getModel().getValue()));
                }, assetManager);

        gunshotVolumeOption = new SliderOptionSelector("Gunshot_Volume", (s) -> {
            temporaryUserSettings.getSoundSettings().setGunshotVolume((int) Math.round(s.getModel().getValue()));
        }, assetManager);

        this.addChild(masterVolumeOption);
        this.addChild(musicVolumeOption);
        this.addChild(sfxVolumeOption);
        this.addChild(gunshotVolumeOption);

        paddingContainer = ContainerUtil.noBackgroundContainer();

        this.addChild(paddingContainer);

        layout();
    }

    private void layout() {
        var size = new Vector3f(360, 135 - 18, 1);
        this.setPreferredSize(size);

        var insets = getTabInsets();
        this.setInsets(insets);

        paddingContainer.setPreferredSize(
                this.getPreferredSize().clone().subtractLocal(
                        0,
                        4 * masterVolumeOption.getPreferredSize().getY() + insets.getMinInsets().getY() + insets.getMaxInsets().getY(),
                        0
                )
        );
    }

    @Override
    public void populateWithSettings(UserSettings temporaryUserSettings) {
        this.temporaryUserSettings = temporaryUserSettings;
        masterVolumeOption.setValue(this.temporaryUserSettings.getSoundSettings().getMasterVolume());
        musicVolumeOption.setValue(this.temporaryUserSettings.getSoundSettings().getMusicVolume());
        sfxVolumeOption.setValue(this.temporaryUserSettings.getSoundSettings().getSfxVolume());
        gunshotVolumeOption.setValue(this.temporaryUserSettings.getSoundSettings().getGunshotVolume());
    }
}