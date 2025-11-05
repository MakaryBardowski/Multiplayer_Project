package dirty.game.effects.VisualEffects;

import com.epagagames.particles.Emitter;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.function.Consumer;

public class PlayableEmitter {
    private final Emitter emitter;
    private final Consumer<Emitter> player;

    public PlayableEmitter(Emitter emitter, Consumer<Emitter> player){
        this.emitter = emitter;
        this.player = player;
    }

    public void play(){
        player.accept(emitter);
    }

    public void setLocalTranslation(Vector3f pos){
        emitter.setLocalTranslation(pos);
    }

    public void lookAt(Vector3f direction){

        emitter.lookAt(
                emitter.getWorldTranslation().add(direction.subtract(emitter.getWorldTranslation()).mult(10))
                , Vector3f.UNIT_Z);
        emitter.rotate(FastMath.DEG_TO_RAD*90,0,0);
    }

    public void attachToNode(Node parent){
        if(emitter.getParent() != parent){
            parent.attachChild(emitter);
        }
    }

    public String getName(){
        return emitter.getName();
    }
}
