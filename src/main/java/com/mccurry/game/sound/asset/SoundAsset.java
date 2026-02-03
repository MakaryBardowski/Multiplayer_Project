package com.mccurry.game.sound.asset;

import com.jme3.audio.AudioData;
import com.mccurry.game.sound.model.SoundCategory;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class SoundAsset {
    @NonNull private final String assetPath;
    @NonNull private final SoundCategory soundCategory;
    @NonNull private final AudioData.DataType type;
}
