package com.deathland.game.canyonbunny.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.deathland.game.canyonbunny.game.WorldController;
import com.deathland.game.canyonbunny.game.WorldRenderer;
import com.deathland.game.canyonbunny.util.GamePreferences;

public class GameScreen extends AbstractGameScreen{
   private static final String TAG = GameScreen.class.getName();

   private WorldController worldController;
   private WorldRenderer worldRenderer;

   private boolean paused;

   public GameScreen(DirectedGame game) {
      super(game);
   }

   @Override
   public void render(float deltaTime) {
      // Do not update game world when paused
      if(!paused) {
         // Update game world by the time that has passed
         // since last rendered frame.
         worldController.update(deltaTime);
      }

      // Sets the cler screen color to: Cornfolower Blue
      Gdx.gl.glClearColor(
         0x64/255.0f,
         0x95/255.0f,
         0xed/255.0f,
         0xff/255.0f
      );

      // Clears the screen
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
      // Render game world to screen
      worldRenderer.render();
   }

   @Override
   public void resize(int width, int height) {
      worldRenderer.resize(width, height);
   }

   @Override
   public void show() {
      GamePreferences.instance.load();
      worldController = new WorldController(game);
      worldRenderer = new WorldRenderer(worldController);
      // Gdx.input.setCatchBackKey(true);
      Gdx.input.setCatchKey(Keys.BACK, true);
   }

   @Override
   public void hide() {
      worldController.dispose();
      worldRenderer.dispose();
      Gdx.input.setCatchKey(Keys.BACK, false);
   }

   @Override
   public void pause() {
      paused = true;
   }

   @Override
   public void resume() {
      super.resume();
      // NOTE: Only called on Android!
      paused = false;
   }

   @Override
   public InputProcessor getInputProcessor() {
      return worldController;
   }
}