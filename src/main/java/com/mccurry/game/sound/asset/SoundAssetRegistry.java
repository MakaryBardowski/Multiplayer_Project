package com.mccurry.game.sound.asset;

import com.jme3.audio.AudioData;
import com.mccurry.game.sound.model.global.GlobalSoundEntry;
import com.mccurry.game.sound.model.SoundCategory;

import java.util.ArrayList;
import java.util.List;

public class SoundAssetRegistry {
    private static final String soundPath = "Sound/";

    public static final List<GlobalSoundAsset> GLOBAL_SOUND_ASSETS = new ArrayList<>(){{
        add(
                GlobalSoundAsset.builder()
                        .assetPath(soundPath+"MainMenu/MainMenuButtonClick.wav")
                        .soundEntry(GlobalSoundEntry.MENU_BUTTON_CLICK)
                        .soundCategory(SoundCategory.SFX)
                        .type(AudioData.DataType.Buffer)
                        .build()
        );
    }};

    public static final List<PositionalSoundAsset> POSITIONAL_SOUND_ASSETS = new ArrayList<>(){{

    }};
}
