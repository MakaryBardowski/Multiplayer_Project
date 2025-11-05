package com.mccurry.game.gameStates.boot.events;

import com.mccurry.events.core.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AssetLoadingPercentAdvanceEvent implements Event {
    private final float normalizedLoadPercent;
}
