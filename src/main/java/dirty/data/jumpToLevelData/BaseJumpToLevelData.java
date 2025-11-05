package dirty.data.jumpToLevelData;

import dirty.game.map.MapType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BaseJumpToLevelData {
    protected final int levelIndex;
    protected final long levelSeed;
    protected final MapType levelType;
}
