package dirty.behaviorTree.actions.mudBeetleActions;

import dirty.behaviorTree.NodeAction;
import dirty.behaviorTree.NodeCompletionStatus;
import dirty.behaviorTree.context.Context;
import dirty.behaviorTree.context.MudBeetleContext;
import com.jme3.math.Vector3f;

import java.util.Random;

import static dirty.behaviorTree.NodeCompletionStatus.SUCCESS;

public class SetRandomLookDirection extends NodeAction {

    private Random random = new Random();

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (MudBeetleContext) context;
        hc.putLookInRandomDirectionOnCooldown();
        hc.setDesiredLookDirection(new Vector3f(
                random.nextFloat(-1, 1), 0, random.nextFloat(-1, 1)
        )
        );

        return SUCCESS;
    }
}
