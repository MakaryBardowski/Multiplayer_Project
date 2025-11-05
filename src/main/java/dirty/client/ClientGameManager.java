package dirty.client;
import lombok.Getter;
import dirty.server.GameManagerInterface;

public abstract class ClientGameManager implements GameManagerInterface {
    @Getter
    protected ClientLevelManager levelManager;
}
