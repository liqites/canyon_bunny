package com.deathland.game.canyonbunny.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
    // 在 x，y 轴上的速度 m/s
    public Vector2 velocity;
    // 在 x，y 轴上的最大速度 m/s
    public Vector2 terminalVelocity;
    // 摩擦力
    public Vector2 friction;
    // 加速度 m/s(2)
    public Vector2 acceleration;
    // 边界，用于碰撞检测
    public Rectangle bounds;

    public AbstractGameObject() {
        position = new Vector2();
        dimension = new Vector2(1, 1);
        origin = new Vector2();
        scale = new Vector2(1,1);
        rotation = 0;
        velocity = new Vector2();
        terminalVelocity = new Vector2(2, 1);
        friction = new Vector2();
        acceleration = new Vector2();
        bounds = new Rectangle();
    }

    public void update(float deltaTime) {
        updateMotionX(deltaTime);
        updateMotionY(deltaTime);
        // move to new position
        // INFO: 根据速度，计算出下一个新的位置
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
        // Gdx.app.debug(this.getClass().getName(), "position updated: x <"+position.x+"> y<"+position.y+">");
    }

    // 更新 X 轴方向的速度
    protected void updateMotionX(float deltaTime) {
        if(velocity.x != 0) {
            // Apply friction
            if(velocity.x > 0) {
                velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
            } else {
                velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0);
            }
        }
        // Apply acceleration
        velocity.x += acceleration.x * deltaTime;
        // Make sure the object's velocity does not exceed the
        // positive or negative terminal velocity
        velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x , terminalVelocity.x);
    }

    // 更新 Y 轴方向的速度
    protected void updateMotionY(float deltaTime) {
        if(velocity.y != 0) {
            // Apply friction
            if(velocity.y > 0) {
                velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
            } else {
                velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0);
            }
        }
        // Apply acceleration
        velocity.y += acceleration.y * deltaTime;
        // Make sure the objects's velocity does not exceed the
        // positive or negative terminal velocity
        velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
    }

    public abstract void render (SpriteBatch batch);
}