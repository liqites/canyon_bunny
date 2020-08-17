package com.deathland.game.canyonbunny.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.deathland.game.canyonbunny.game.Assets;
import com.deathland.game.canyonbunny.util.Constants;

public class BunnyHead extends AbstractGameObject{
   public static final String TAG = BunnyHead.class.getName();

   private final float JUMP_TIME_MAX = 0.3f;
   private final float JUMP_TIME_MIN = 0.1f;
   private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;

   public enum VIEW_DIRECTION {LEFT, RIGHT};

   public enum JUMP_STATE {
      GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
   }
   private TextureRegion regHead;

   public VIEW_DIRECTION viewDirection;
   public float timeJumping;
   public JUMP_STATE jumpState;
   public boolean hasFeatherPowerup;
   public float timeLeftFeatherPowerup;

   public BunnyHead() {
      init();
   }

   public void init() {
      dimension.set(1, 1);
      regHead = Assets.instance.bunny.head;
      // Center image on game object
      origin.set(dimension.x / 2, dimension.y / 2);
      // Bounding box for collsion deetection
      bounds.set(0, 0, dimension.x, dimension.y);
      // Set pyhsics values
      terminalVelocity.set(3.0f, 4.0f);
      friction.set(12.0f, 0.0f);
      acceleration.set(0.0f, -25.0f);
      // View direction
      viewDirection = VIEW_DIRECTION.RIGHT;
      // Jump State
      jumpState = JUMP_STATE.FALLING;
      // Power-up
      hasFeatherPowerup = false;
      timeLeftFeatherPowerup = 0;
   }

   public void setJumping(boolean jumpKeyPressed) {
      switch(jumpState) {
         case GROUNDED: // Character is standing or a platform
            if(jumpKeyPressed) {
               // Start counting jump time from the beginning
               timeJumping = 0;
               jumpState = JUMP_STATE.JUMP_RISING;
            }
            break;
         case JUMP_RISING:
            if(!jumpKeyPressed) {
               jumpState = JUMP_STATE.JUMP_FALLING;
            }
            break;
         case FALLING: // FALLING down
         case JUMP_FALLING: // Falling down after jump
            if(jumpKeyPressed && hasFeatherPowerup) {
               // TODO: 猜测
               // 如果获得了羽毛能量，则可以在下落的过程中再次弹跳。
               timeJumping = JUMP_TIME_OFFSET_FLYING;
               jumpState = JUMP_STATE.JUMP_RISING;
            }
            break;
      }
   }

   public void setFeatherPowerup(boolean pickedUp) {
      hasFeatherPowerup = pickedUp;
      if(pickedUp) {
         timeLeftFeatherPowerup = Constants.ITEM_FEATHER_POWERUP_DURATION;
      }
   }

   public boolean hasFeatherPowerup() {
      return hasFeatherPowerup && timeLeftFeatherPowerup > 0;
   }

   @Override
   public void update(float deltaTime) {
      super.update(deltaTime);
      // TODO: 更新朝向
      if(velocity.x != 0) {
         viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
      }
      if(timeLeftFeatherPowerup > 0) {
         timeLeftFeatherPowerup -= deltaTime;
         if(timeLeftFeatherPowerup < 0 ) {
            // disable power-up
            timeLeftFeatherPowerup = 0;
            setFeatherPowerup(false);
         }
      }
   }

   // 重写更新 Y 轴方向的方法
   @Override
   protected void updateMotionY(float deltaTime) {
      switch (jumpState) {
         case GROUNDED:
            jumpState = JUMP_STATE.FALLING;
            break;
         case JUMP_RISING:
            // Keep track of jump time
            if(timeJumping <= JUMP_TIME_MAX) {
               // Still jumping
               velocity.y = terminalVelocity.y;
            }
            break;
         case FALLING:
            break;
         case JUMP_FALLING:
            // Add delta times to track jump time
            timeJumping += deltaTime;
            // Jump to minimal height if jump key was pressed too short
            if(timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) {
               // Still jumping
               velocity.y = terminalVelocity.y;
            }
         }
         if(jumpState != JUMP_STATE.GROUNDED) {
            super.updateMotionY(deltaTime);
         }
   }

   @Override
   public void render(SpriteBatch batch) {
      TextureRegion reg = null;

      // Set special color when game object  has a feather power-up
      if(hasFeatherPowerup) {
         batch.setColor(1.0f, 0.8f, 0.1f, 1.0f);
      }

      // Draw image
      reg = regHead;

      batch.draw(
         reg.getTexture(),
         position.x,
         position.y,
         origin.x,
         origin.y,
         dimension.x,
         dimension.y,
         scale.x,
         scale.y,
         rotation,
         reg.getRegionX(),
         reg.getRegionY(),
         reg.getRegionWidth(),
         reg.getRegionHeight(),
         viewDirection == VIEW_DIRECTION.LEFT,
         false
      );

      // Reset color to white
      batch.setColor(1, 1, 1, 1);
   }
}