package game.effects.VisualEffects.ParticleInfluencers;

import com.epagagames.particles.Emitter;
import com.epagagames.particles.influencers.ParticleInfluencer;
import com.epagagames.particles.particle.ParticleData;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;

import java.io.IOException;

public class CustomSpriteInfluencer extends ParticleInfluencer {
    private boolean useRandomImage = false;
    private boolean animate = true;
    private boolean cycle = false;

    private float fixedDuration = .125f;
    private int spriteCols = 1;
    private int spriteRows = 1;

    private boolean renderByRows = false;
    private boolean useRandomRow = false;
    private int useRow = 0;

    // temp or working variables
    private int totalFrames;
    private transient float currentInterval, targetInterval;

    public void update(ParticleData p, float tpf) {
        if (enabled) {
            if (animate) {
                currentInterval = (Float)p.getData("frameInterval");
                currentInterval += tpf;
                if (!cycle) {
                    targetInterval = (Float)p.getData("frameDuration");

                } else {
                    targetInterval = fixedDuration;
                }
                if (currentInterval >= targetInterval) {
                    updateFrame(p);
                }
                p.setData("frameInterval", currentInterval);
            }
        }
    }

    private void updateFrame(ParticleData p) {
        p.spriteCol = (Integer)p.getData("frameCol")+1;
        if (p.spriteCol == spriteCols) {
            p.spriteCol = 0;

            if (!renderByRows)  {
                p.spriteRow = (Integer) p.getData("frameRow") + 1;
                if (p.spriteRow == spriteRows)
                    p.spriteRow = 0;
                p.setData("frameRow", p.spriteRow);
            } else {
                p.spriteRow = useRow;
            }
        }
        p.setData("frameCol", p.spriteCol);
        currentInterval -= targetInterval;
    }

    @Override
    public void initializeInfluencer(Emitter emitter) {
        super.initializeInfluencer(emitter);

        emitter.getMesh().setImagesXY(spriteCols, spriteRows);
    }

    @Override
    public void initialize(ParticleData p) {
        totalFrames = spriteCols*spriteRows;
        if (useRandomImage) {
            p.spriteCol = FastMath.nextRandomInt(0,spriteCols-1);
            p.spriteRow = FastMath.nextRandomInt(0,spriteRows-1);
        }
        if (renderByRows) {
            p.spriteRow = useRow;

            if (useRandomRow) {
                p.spriteRow = FastMath.nextRandomInt(0, spriteRows - 1);
            }
        }
        if (animate) {
            p.setData("frameInterval", 0f);
            p.setData("frameDuration", p.startlife/(float)totalFrames);
            p.setData("frameCol", p.spriteCol);
            p.setData("frameRow", p.spriteRow);
        }
    }

    @Override
    public void reset(ParticleData p) {
        p.spriteCol = 0; //FastMath.nextRandomInt(1,p.particles.getSpriteColCount());
        p.spriteRow = 0; //FastMath.nextRandomInt(1,p.particles.getSpriteRowCount());
    }

    /**
     * Particles will/will not use sprite animations
     * @param animate boolean
     */
    public void setAnimate(boolean animate) { this.animate = animate; }

    /**
     * Current animation state of particle
     * @return Returns if particles use sprite animation
     */
    public boolean getAnimate() { return this.animate; }

    /**
     * Sets if particles should select a random start image from the provided sprite texture
     * @param useRandomImage boolean
     */
    public void setUseRandomImage(boolean useRandomImage) { this.useRandomImage = useRandomImage; }

    /**
     * Returns if particles currently select a random start image from the provided sprite texture
     * @param useRandomImage boolean
     * @return
     */
    public boolean getUseRandomImage(boolean useRandomImage) { return this.useRandomImage; }

    /**
     * Animated texture should cycle and use the provided duration between frames (0 diables cycling)
     * @param fixedDuration duration between frame updates
     */
    public void setFixedDuration(float fixedDuration) {
        if (fixedDuration != 0) {
            this.cycle = true;
            this.fixedDuration = fixedDuration;
        } else {
            this.cycle = false;
            this.fixedDuration = 0;
        }
    }
    /**
     * Returns the current duration used between frames for cycled animation
     * @return
     */
    public float getFixedDuration() { return this.fixedDuration; }

    public int getSpriteCols() {
        return spriteCols;
    }

    public void setSpriteCols(int spriteCols) {
        this.spriteCols = spriteCols;
        emitter.getMesh().setImagesXY(spriteCols, spriteRows);
    }

    public int getSpriteRows() {
        return spriteRows;
    }

    public void setSpriteRows(int spriteRows) {
        this.spriteRows = spriteRows;
        emitter.getMesh().setImagesXY(spriteCols, spriteRows);
    }

    public boolean isRenderByRows() {
        return renderByRows;
    }

    public void setRenderByRows(boolean renderByRows) {
        this.renderByRows = renderByRows;
    }

    public boolean isUseRandomRow() {
        return useRandomRow;
    }

    public void setUseRandomRow(boolean useRandomRow) {
        this.useRandomRow = useRandomRow;
    }

    public int getUseRow() {
        return useRow;
    }

    public void setUseRow(int useRow) {
        this.useRow = useRow;
    }

    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(useRandomImage, "userandomimage", false);
        oc.write(animate, "animate", true);
        oc.write(cycle, "cycle", false);
        oc.write(fixedDuration, "fixedduration", 0.125f);
        oc.write(spriteCols, "spritecolumns", 1);
        oc.write(spriteRows, "spriterows", 1);
    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        useRandomImage = ic.readBoolean("userandomimage", false);
        animate = ic.readBoolean("animate", true);
        cycle = ic.readBoolean("cycle", false);
        fixedDuration = ic.readFloat("fixedduration", 0.125f);
        spriteCols = ic.readInt("spritecolumns", 1);
        spriteRows = ic.readInt("spriterows", 1);
    }

    @Override
    public ParticleInfluencer clone() {
        CustomSpriteInfluencer clone = (CustomSpriteInfluencer) super.clone();
        return clone;
    }

}