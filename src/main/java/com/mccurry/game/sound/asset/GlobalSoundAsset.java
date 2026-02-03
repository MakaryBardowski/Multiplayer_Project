package com.mccurry.game.sound.asset;

import com.mccurry.game.sound.model.global.GlobalSoundEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class GlobalSoundAsset extends SoundAsset {
    @NonNull private final GlobalSoundEntry soundEntry;
}
