package com.deathland.game.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractGameObject {
    // 位置
    public Vector2 position;
    // 纬度
    public Vector2 dimension;
    // 中心位置
    public Vector2 origin;
    // 缩放
    public Vector2 scale;
    // 旋转
    public float rotation;

    public AbstractGameObject() {
        position = new Vector2();
        dimension = new Vector2(1, 1);
        origin = new Vector2();
        scale = new Vector2(1,1);
        rotation = 0;
    }

    public void update(float deltaTime) {

    }

    public abstract void render (SpriteBatch batch);
}