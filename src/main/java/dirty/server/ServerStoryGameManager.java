package dirty.server;

import dirty.data.jumpToLevelData.BaseJumpToLevelData;

public class ServerStoryGameManager extends ServerGameManager {

    private static final int LEVEL_COUNT = 130;//13;

    public ServerStoryGameManager(ServerContext serverContext) {
        super(serverContext);
        gamemodeId = 1;
    }

    @Override
    public void startGame() {
        setupLevelManager();
        levelManager.jumpToLevel(new BaseJumpToLevelData(0,levelManager.getLevelSeeds()[0],levelManager.getLevelTypes()[0]));
        levelManager.instantiatePlayersAndNotify(serverContext);
    }

    @Override
    public void updateMainLoop(float tpf) {}

    private void setupLevelManager() {
        levelManager = new ServerLevelManager(LEVEL_COUNT, serverContext);
        levelManager.setupLevelSeeds();
    }

}
