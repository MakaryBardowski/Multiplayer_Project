package com.mccurry.game.sound.asset;

import com.mccurry.game.sound.model.positional.PositionalSoundEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PositionalSoundAsset extends SoundAsset{
    @NonNull
    private final PositionalSoundEntry soundEntry;
}
