package com.deathland.game.canyonbunny.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.deathland.game.canyonbunny.util.Constants;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

// 主要负责渲染
public class WorldRenderer implements Disposable {
    private OrthographicCamera camera;
    private OrthographicCamera cameraGUI;
    private SpriteBatch batch;
    private WorldController worldController;

    public WorldRenderer(WorldController worldController) {
        this.worldController = worldController;
        init();
    }

    private void init() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(
                Constants.VIEWPORT_WIDTH,
                Constants.VIEWPORT_HEIGHT
        );
        camera.position.set(0, 0, 0);
        camera.update();
        cameraGUI = new OrthographicCamera(
                Constants.VIEWPORT_GUI_WIDTH,
                Constants.VIEWPORT_GUI_HEIGHT
        );
        cameraGUI.position.set(0, 0, 0);
        cameraGUI.setToOrtho(true);
        cameraGUI.update();
    }

    public void render() {
        renderWorld(batch);
        renderGui(batch);
    }

    private void renderGuiScore(SpriteBatch batch) {
        float x = -15;
        float y = -15;

        batch.draw(
                Assets.instance.goldCoin.goldCoin,
                x,y,
                50, 50,
                100, 100,
                0.35f, -0.35f,
                0
                );

        Assets.instance.fonts.defaultBig.draw(
                batch,
                "" + worldController.score,
                x + 75, y + 40
        );
    }

    private void renderGuidExtraLive(SpriteBatch batch) {
        float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
        float y = -15;
        for(int i = 0;i < Constants.LIVES_START; i++) {
            if(worldController.lives <= i) {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            batch.draw(
                    Assets.instance.bunny.head,
                    x + i * 50, y, 50, 50, 120, 100, 0.35f, -.35f, 0
            );
            batch.setColor(1, 1, 1, 1);
        }
    }

    private void renderGuiBunnyJumpSatte(SpriteBatch batch) {
        float x = cameraGUI.viewportWidth - 255;
        float y = cameraGUI.viewportHeight - 15;
        // int fps = Gdx.graphics.getFramesPerSecond();
        BitmapFont bunnyFont = Assets.instance.fonts.defaultNormal;
        bunnyFont.draw(batch, "JumpState: " + worldController.level.bunnyHead.jumpState, x, y);
        bunnyFont.setColor(1, 1, 1, 1);
    }

    private void renderGuiFpsCounter(SpriteBatch batch) {
        float x = cameraGUI.viewportWidth - 55;
        float y = cameraGUI.viewportHeight - 15;
        int fps = Gdx.graphics.getFramesPerSecond();
        BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
        if(fps >= 45) {
            // 45 or more FPS show up in green
            fpsFont.setColor(0, 1, 0, 1);
        } else if(fps >= 30) {
            // 30 or more FPS show up in yellow
            fpsFont.setColor(1, 1, 0, 1);
        } else {
            // less than 30 FPS show up in red
            fpsFont.setColor(1, 0, 0, 1);
        }
        fpsFont.draw(batch, "FPS: " + fps, x, y);
        fpsFont.setColor(1, 1, 1, 1);
    }

    private void renderGui(SpriteBatch batch) {
        batch.setProjectionMatrix(cameraGUI.combined);
        batch.begin();
        // draw collected gold coin icon + text
        // (anchored to top left edge
        renderGuiScore(batch);
        // draw extra lives icon + text
        // (anchored to top right edge)
        renderGuidExtraLive(batch);
        // draw FPS text
        // (anchored to bottom right edge)
        renderGuiFpsCounter(batch);

        renderGuiBunnyJumpSatte(batch);
        batch.end();
    }

    private void renderWorld(SpriteBatch batch) {
        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldController.level.render(batch);
        batch.end();
    }

    public void resize(int width, int height) {
        camera.viewportWidth = (Constants.VIEWPORT_WIDTH / height) * width;
        camera.update();

        cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
        cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float)height) * (float)width;
        cameraGUI.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
