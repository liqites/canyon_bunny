package com.deathland.game.canyonbunny.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.deathland.game.canyonbunny.game.objects.*;

public class Level {
    public static final String TAG = Level.class.getName();

    public enum BLOCK_TYPE {
        GOAL(255, 0, 0), // red
        EMPTY(0, 0, 0), // black
        ROCK(0, 255, 0), // green
        PLAYER_SPAWNPOINT(255, 255, 255), // white
        ITEM_FEATHER(255, 0, 255), // purple
        ITEM_GOLD_COIN(255, 255, 0); // yellow

        private int color;

        private BLOCK_TYPE(int r, int g, int b) {
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        private boolean samColor(int color) {
            return this.color == color;
        }

        private int getColor() {
            return color;
        }
    }

    // Carrots and Goal
    public Array<Carrot> carrots;
    public Goal goal;

    // player
    public BunnyHead bunnyHead;

    // objects
    public Array<GoldCoin> goldCoins;
    public Array<Feather> feathers;

    public Array<Rock> rocks;

    // decoration
    public Clouds clouds;
    public Mountains mountains;
    public WaterOverlay waterOverlay;

    public Level(String filename) {
        init(filename);
    }

    private void init(String filename) {
        // player
        bunnyHead = null;
        // objects
        rocks = new Array<Rock>();
        goldCoins = new Array<GoldCoin>();
        feathers = new Array<Feather>();
        carrots = new Array<Carrot>();

        // load image file that represents the level data;
        Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
        // scan pixels from top-left to bottom-right
        int lastPixel = -1;
        for(int pixelY = 0;pixelY < pixmap.getHeight(); pixelY++) {
            for(int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
                AbstractGameObject obj = null;
                float offsetHeight = 0;
                // height grows from bottom to top;
                float baseHeight = pixmap.getHeight() - pixelY;
                // get color of current pixel as 32-bit RGBA value
                int currentPixel = pixmap.getPixel(pixelX, pixelY);
                // find match color value to identify block type at (x,y)
                // point and create the corresponding game object if there is
                // a match

                // empty space
                if(BLOCK_TYPE.EMPTY.samColor(currentPixel)) {
                    // TODO: do nothing
                } else if( BLOCK_TYPE.ROCK.samColor(currentPixel) ) {
                    // rock
                    if(lastPixel != currentPixel) {
                        obj = new Rock();
                        float heightIncreaseFactor = 0.25f;
                        offsetHeight = -2.5f;
                        obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight);
                        rocks.add((Rock)obj);
                    } else {
                        rocks.get(rocks.size - 1).increaseLength(1);
                    }
                } else if(BLOCK_TYPE.PLAYER_SPAWNPOINT.samColor(currentPixel)) {
                    // player spawn point
                    obj = new BunnyHead();
                    offsetHeight = -3.0f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    obj.terminalVelocity = new Vector2(6, 3);
                    bunnyHead = (BunnyHead)obj;
                    Gdx.app.debug(TAG, "BunnyHead initialized at x<" + pixelX + "> y <" + pixelY + ">");
                } else if(BLOCK_TYPE.ITEM_FEATHER.samColor(currentPixel)) {
                    // feather
                    obj = new Feather();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    feathers.add((Feather) obj);
                    Gdx.app.debug(TAG, "Feather initialized at x<" + pixelX + "> y <" + pixelY + ">");
                } else if(BLOCK_TYPE.ITEM_GOLD_COIN.samColor(currentPixel)) {
                    // gold coin
                    obj = new GoldCoin();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    goldCoins.add((GoldCoin)obj);
                }  else if(BLOCK_TYPE.GOAL.samColor(currentPixel)) {
                    // goal
                    obj = new Goal();
                    offsetHeight = -7.0f;
                    obj.position.set(pixelX, baseHeight + offsetHeight);
                    goal = (Goal)obj;
                } else {
                    // unknown object/pixel color
                    int r = 0xff & (currentPixel >> 24); // red color channel
                    int g = 0xff & (currentPixel >> 16); // green color channel
                    int b = 0xff & (currentPixel >> 8); // blue color channel
                    int a = 0xff & currentPixel; // alpha channel
                    Gdx.app.error(
                            TAG,
                            "Unknow object at x<" + pixelX + "> y <" + pixelY + ">:r <" + r + "> g <" + g + "> b<" + b + "> a<" + a + ">"
                    );
                    lastPixel = currentPixel;
                }
            }

            // decoration
            clouds = new Clouds(pixmap.getWidth());
            clouds.position.set(0, 2);
            mountains = new Mountains(pixmap.getWidth());
            mountains.position.set(-1, -1);
            waterOverlay = new WaterOverlay(pixmap.getWidth());
            waterOverlay.position.set(0, -3.75f);
        }
        // free memory
        pixmap.dispose();
        Gdx.app.debug(TAG, "level '" + filename + "' loaded");
    }

    public void render(SpriteBatch batch) {
        // Draw mountains
        mountains.render(batch);
        //
        goal.render(batch);
        // Draw Rocks
        for(Rock rock : rocks) {
            rock.render(batch);
        }
        // Draw GoldCoins
        for(GoldCoin goldCoin : goldCoins) {
            goldCoin.render(batch);
        }
        // Draw Feathers
        for(Feather feather : feathers) {
            feather.render(batch);
        }
        // Draw Water Overlay
        waterOverlay.render(batch);
        // Draw clouds
        clouds.render(batch);
        // Draw Carrots
        for(Carrot carrot : carrots) {
            carrot.render(batch);
        }
        // Draw Player Character
        bunnyHead.render(batch);
    }

    public void update(float deltaTime) {
        bunnyHead.update(deltaTime);
        for(Rock  rock: rocks) {
            rock.update(deltaTime);
        }
        for(GoldCoin goldCoin : goldCoins) {
            goldCoin.update(deltaTime);
        }
        for(Feather feather : feathers) {
            feather.update(deltaTime);
        }
        for(Carrot carrot : carrots) {
            carrot.update(deltaTime);
        }
        clouds.update(deltaTime);
    }
}
