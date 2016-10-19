package com.github.gigurra.glasciia;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.gigurra.glasciia.impl.CollidingParticleEmitter;

import java.io.*;
import java.util.HashMap;

/**
 * Created by johan on 2016-10-02.
 * Source copied straight out of ParticleEffect.Java in libgdx.
 * Necessary evil because of libgdx's poor constructor design
 *
 * Angle code taken from http://stackoverflow.com/questions/14839648/libgdx-particleeffect-rotation, answer by Aebsubis
 */
public class ParticleSource implements Disposable {
    private final ParticleCollider collider;
    private final Array<CollidingParticleEmitter> emitters;
    private BoundingBox bounds;
    private boolean ownsTexture;

    public ParticleSource(ParticleCollider collider) {
        this.collider = collider;
        emitters = new Array<>(8);
    }

    public ParticleSource(ParticleCollider collider, ParticleSource effect) {
        this.collider = collider;
        emitters = new Array<>(true, effect.emitters.size);
        for (int i = 0, n = effect.emitters.size; i < n; i++)
            emitters.add(new CollidingParticleEmitter(collider, effect.emitters.get(i)));
    }

    public ParticleSource(ParticleSource effect) {
        this(effect.collider, effect);
    }

    public ParticleSource setAngle(float angleDegrees) {
        if (angleDegrees != getAngle()) {
            offsetAngle(angleDegrees - getAngle());
        }
        return this;
    }

    public ParticleSource offsetAngle(float deltaDegrees) {
        Array<CollidingParticleEmitter> emitters = getEmitters();
        for (int i = 0; i < emitters.size; i++) {
            ParticleEmitter.ScaledNumericValue val = emitters.get(i).getAngle();
            val.setHigh(val.getHighMin() + deltaDegrees, val.getHighMax() + deltaDegrees);
            val.setLow(val.getLowMin() + deltaDegrees, val.getLowMax() + deltaDegrees);
        }
        return this;
    }

    public float getAngle() {
        if (emitters.size == 0) {
            return 0.0f;
        } else {
            return emitters.get(0).getAngle().getLowMin();
        }
    }

    public void start () {
        for (int i = 0, n = emitters.size; i < n; i++)
            emitters.get(i).start();
    }

    public void reset () {
        for (int i = 0, n = emitters.size; i < n; i++)
            emitters.get(i).reset();
    }

    public void update (float delta) {
        for (int i = 0, n = emitters.size; i < n; i++)
            emitters.get(i).update(delta);
    }

    public void draw (Batch spriteBatch) {
        for (int i = 0, n = emitters.size; i < n; i++)
            emitters.get(i).draw(spriteBatch);
    }

    public void draw (Batch spriteBatch, float delta) {
        for (int i = 0, n = emitters.size; i < n; i++)
            emitters.get(i).draw(spriteBatch, delta);
    }

    public void allowCompletion () {
        for (int i = 0, n = emitters.size; i < n; i++)
            emitters.get(i).allowCompletion();
    }

    public boolean isComplete () {
        for (int i = 0, n = emitters.size; i < n; i++) {
            ParticleEmitter emitter = emitters.get(i);
            if (!emitter.isComplete()) return false;
        }
        return true;
    }

    public ParticleSource setDuration (int duration) {
        for (int i = 0, n = emitters.size; i < n; i++) {
            ParticleEmitter emitter = emitters.get(i);
            emitter.setContinuous(false);
            emitter.duration = duration;
            emitter.durationTimer = 0;
        }
        return this;
    }

    public ParticleSource setPosition (float x, float y) {
        for (int i = 0, n = emitters.size; i < n; i++)
            emitters.get(i).setPosition(x, y);
        return this;
    }

    public ParticleSource setFlip (boolean flipX, boolean flipY) {
        for (int i = 0, n = emitters.size; i < n; i++)
            emitters.get(i).setFlip(flipX, flipY);
        return this;
    }

    public ParticleSource flipY () {
        for (int i = 0, n = emitters.size; i < n; i++)
            emitters.get(i).flipY();
        return this;
    }

    public Array<CollidingParticleEmitter> getEmitters () {
        return emitters;
    }

    /** Returns the emitter with the specified name, or null. */
    public ParticleEmitter findEmitter (String name) {
        for (int i = 0, n = emitters.size; i < n; i++) {
            ParticleEmitter emitter = emitters.get(i);
            if (emitter.getName().equals(name)) return emitter;
        }
        return null;
    }

    public void save (Writer output) throws IOException {
        int index = 0;
        for (int i = 0, n = emitters.size; i < n; i++) {
            ParticleEmitter emitter = emitters.get(i);
            if (index++ > 0) output.write("\n\n");
            emitter.save(output);
        }
    }

    public void load (FileHandle effectFile, FileHandle imagesDir) {
        loadEmitters(effectFile);
        loadEmitterImages(imagesDir);
    }

    public void load (FileHandle effectFile, TextureAtlas atlas) {
        load(effectFile, atlas, null);
    }

    public void load (FileHandle effectFile, TextureAtlas atlas, String atlasPrefix) {
        loadEmitters(effectFile);
        loadEmitterImages(atlas, atlasPrefix);
    }

    public void loadEmitters (FileHandle effectFile) {
        InputStream input = effectFile.read();
        emitters.clear();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(input), 512);
            while (true) {
                CollidingParticleEmitter emitter = new CollidingParticleEmitter(collider, reader);
                emitters.add(emitter);
                if (reader.readLine() == null) break;
                if (reader.readLine() == null) break;
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error loading effect: " + effectFile, ex);
        } finally {
            StreamUtils.closeQuietly(reader);
        }
    }

    public void loadEmitterImages (TextureAtlas atlas) {
        loadEmitterImages(atlas, null);
    }

    public void loadEmitterImages (TextureAtlas atlas, String atlasPrefix) {
        for (int i = 0, n = emitters.size; i < n; i++) {
            ParticleEmitter emitter = emitters.get(i);
            String imagePath = emitter.getImagePath();
            if (imagePath == null) continue;
            String imageName = new File(imagePath.replace('\\', '/')).getName();
            int lastDotIndex = imageName.lastIndexOf('.');
            if (lastDotIndex != -1) imageName = imageName.substring(0, lastDotIndex);
            if (atlasPrefix != null) imageName = atlasPrefix + imageName;
            Sprite sprite = atlas.createSprite(imageName);
            if (sprite == null) throw new IllegalArgumentException("SpriteSheet missing image: " + imageName);
            emitter.setSprite(sprite);
        }
    }

    public void loadEmitterImages (FileHandle imagesDir) {
        ownsTexture = true;
        HashMap<String, Sprite> loadedSprites = new HashMap<String, Sprite>(emitters.size);
        for (int i = 0, n = emitters.size; i < n; i++) {
            ParticleEmitter emitter = emitters.get(i);
            String imagePath = emitter.getImagePath();
            if (imagePath == null) continue;
            String imageName = new File(imagePath.replace('\\', '/')).getName();
            Sprite sprite = loadedSprites.get(imageName);
            if (sprite == null) {
                sprite = new Sprite(loadTexture(imagesDir.child(imageName)));
                loadedSprites.put(imageName, sprite);
            }
            emitter.setSprite(sprite);
        }
    }

    protected Texture loadTexture (FileHandle file) {
        return new Texture(file, false);
    }

    /** Disposes the texture for each sprite for each ParticleEmitter. */
    public void dispose () {
        if (!ownsTexture) return;
        for (int i = 0, n = emitters.size; i < n; i++) {
            ParticleEmitter emitter = emitters.get(i);
            emitter.getSprite().getTexture().dispose();
        }
    }

    /** Returns the bounding box for all active particles. z axis will always be zero. */
    public BoundingBox getBoundingBox () {
        if (bounds == null) bounds = new BoundingBox();

        BoundingBox bounds = this.bounds;
        bounds.inf();
        for (ParticleEmitter emitter : this.emitters)
            bounds.ext(emitter.getBoundingBox());
        return bounds;
    }

    public ParticleSource setTint(Color color) {
        for (int iEm = 0, n = emitters.size; iEm < n; iEm++) {
            ParticleEmitter emitter = emitters.get(iEm);

            final float[] colors = emitter.getTint().getColors();
            for (int i = 0; i < colors.length; i += 3) {
                colors[i + 0] = color.r;
                colors[i + 1] = color.g;
                colors[i + 2] = color.b;
            }
        }
        return this;
    }

    public ParticleSource scaleEffect (float scaleFactor) {
        for (ParticleEmitter particleEmitter : emitters) {
            particleEmitter.getScale().setHigh(particleEmitter.getScale().getHighMin() * scaleFactor,
                    particleEmitter.getScale().getHighMax() * scaleFactor);
            particleEmitter.getScale().setLow(particleEmitter.getScale().getLowMin() * scaleFactor,
                    particleEmitter.getScale().getLowMax() * scaleFactor);

            particleEmitter.getVelocity().setHigh(particleEmitter.getVelocity().getHighMin() * scaleFactor,
                    particleEmitter.getVelocity().getHighMax() * scaleFactor);
            particleEmitter.getVelocity().setLow(particleEmitter.getVelocity().getLowMin() * scaleFactor,
                    particleEmitter.getVelocity().getLowMax() * scaleFactor);

            particleEmitter.getGravity().setHigh(particleEmitter.getGravity().getHighMin() * scaleFactor,
                    particleEmitter.getGravity().getHighMax() * scaleFactor);
            particleEmitter.getGravity().setLow(particleEmitter.getGravity().getLowMin() * scaleFactor,
                    particleEmitter.getGravity().getLowMax() * scaleFactor);

            particleEmitter.getWind().setHigh(particleEmitter.getWind().getHighMin() * scaleFactor,
                    particleEmitter.getWind().getHighMax() * scaleFactor);
            particleEmitter.getWind().setLow(particleEmitter.getWind().getLowMin() * scaleFactor,
                    particleEmitter.getWind().getLowMax() * scaleFactor);

            particleEmitter.getSpawnWidth().setHigh(particleEmitter.getSpawnWidth().getHighMin() * scaleFactor,
                    particleEmitter.getSpawnWidth().getHighMax() * scaleFactor);
            particleEmitter.getSpawnWidth().setLow(particleEmitter.getSpawnWidth().getLowMin() * scaleFactor,
                    particleEmitter.getSpawnWidth().getLowMax() * scaleFactor);

            particleEmitter.getSpawnHeight().setHigh(particleEmitter.getSpawnHeight().getHighMin() * scaleFactor,
                    particleEmitter.getSpawnHeight().getHighMax() * scaleFactor);
            particleEmitter.getSpawnHeight().setLow(particleEmitter.getSpawnHeight().getLowMin() * scaleFactor,
                    particleEmitter.getSpawnHeight().getLowMax() * scaleFactor);

            particleEmitter.getXOffsetValue().setLow(particleEmitter.getXOffsetValue().getLowMin() * scaleFactor,
                    particleEmitter.getXOffsetValue().getLowMax() * scaleFactor);

            particleEmitter.getYOffsetValue().setLow(particleEmitter.getYOffsetValue().getLowMin() * scaleFactor,
                    particleEmitter.getYOffsetValue().getLowMax() * scaleFactor);
        }
        return this;
    }

    /** Sets the {@link com.badlogic.gdx.graphics.g2d.ParticleEmitter#setCleansUpBlendFunction(boolean) cleansUpBlendFunction}
     * parameter on all {@link com.badlogic.gdx.graphics.g2d.ParticleEmitter ParticleEmitters} currently in this ParticleEffect.
     * <p>
     * IMPORTANT: If set to false and if the next object to use this Batch expects alpha blending, you are responsible for setting
     * the Batch's blend function to (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) before that next object is drawn.
     * @param cleanUpBlendFunction */
    public ParticleSource setEmittersCleanUpBlendFunction (boolean cleanUpBlendFunction) {
        for (int i = 0, n = emitters.size; i < n; i++) {
            emitters.get(i).setCleansUpBlendFunction(cleanUpBlendFunction);
        }
        return this;
    }

}

