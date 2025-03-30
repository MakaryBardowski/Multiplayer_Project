package game.effects.VisualEffects.ParticleInfluencers;

import com.epagagames.particles.influencers.ParticleInfluencer;
import com.epagagames.particles.particle.ParticleData;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.Random;

/**
 *
 * @author 48793
 */
public class RandomInitialRotationInfluencer extends ParticleInfluencer{
    private final Random random = new Random();

    @Override
    public void update(ParticleData pd, float f) {
    }

    @Override
    public void initialize(ParticleData pd) {
        var rotation = new Vector3f(random.nextFloat(-0.1f,0.1f),0,0);
        pd.angles = rotation;


    }

    @Override
    public void reset(ParticleData pd) {
    }

}
