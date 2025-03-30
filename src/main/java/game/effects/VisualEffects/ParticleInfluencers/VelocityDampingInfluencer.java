package game.effects.VisualEffects.ParticleInfluencers;

import com.epagagames.particles.influencers.ParticleInfluencer;
import com.epagagames.particles.particle.ParticleData;
import java.util.Random;

/**
 *
 * @author 48793
 */
public class VelocityDampingInfluencer extends ParticleInfluencer{
    private final Random random = new Random();

    private float minDamp;
    private float maxDamp;

    public VelocityDampingInfluencer(float minDamp, float maxDamp){
        this.minDamp = minDamp;
        this.maxDamp = maxDamp;
    }

    @Override
    public void update(ParticleData pd, float f) {
        pd.velocity.multLocal((Float)pd.getData("damp"));
    }

    @Override
    public void initialize(ParticleData pd) {
        pd.setData("damp", random.nextFloat(minDamp,maxDamp));
    }

    @Override
    public void reset(ParticleData pd) {
    }

}
