package dirty.server;

import lombok.Getter;

public abstract class ServerGameManager implements GameManagerInterface{
    protected int gamemodeId;
    protected final ServerContext serverContext;

    public ServerGameManager(ServerContext context){
        this.serverContext = context;
    }
    
    @Getter
    protected ServerLevelManager levelManager;

}
