package game.effects.VisualEffects;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VisualEffect {
    private List<PlayableEmitter> emitters = new ArrayList<>(3);

    public VisualEffect(PlayableEmitter... emitters){
        Collections.addAll(this.emitters, emitters);
    }

    public void playAt(Node parentNode, Vector3f pos, Vector3f lookDirection){
        for(var emitter : emitters){
            emitter.attachToNode(parentNode);
            emitter.setLocalTranslation(pos);
            emitter.lookAt(pos.add(lookDirection));
            emitter.play();
        }
    }

}
