package com.github.gigurra.glasciia.impl;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.github.gigurra.glasciia.impl.CollidingParticle;
import com.github.gigurra.glasciia.ParticleCollider;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Function;

/**
 * Created by johan on 2016-10-02.
 *
 * This has to be a java class, because of stupid libgdx constructor design :S
 */
public class CollidingParticleEmitter extends ParticleEmitter {

    final ParticleCollider collider;
    private FloatHolder floatHolder = new FloatHolder(0);


    @Override
    public void update (float delta) {
        floatHolder.setValue(delta);
        super.update(delta);
    }

    @Override
    public void draw (Batch batch, float delta) {
        floatHolder.setValue(delta);
        super.draw(batch, delta);
    }

    public CollidingParticleEmitter (ParticleCollider collider) {
        super();
        this.collider = collider;
    }

    public CollidingParticleEmitter (ParticleCollider collider, BufferedReader reader) throws IOException {
        super(reader);
        this.collider = collider;
    }

    public CollidingParticleEmitter (ParticleCollider collider, CollidingParticleEmitter emitter) {
        super(emitter);
        this.collider = collider;
    }

    @Override
    protected CollidingParticle newParticle (Sprite sprite) {
        return new CollidingParticle(sprite, collider, floatHolder, 0.01f);
    }
}
