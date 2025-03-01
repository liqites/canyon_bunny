package com.deathland.game.canyonbunny.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Application.ApplicationType;
import com.deathland.game.canyonbunny.screens.transitions.ScreenTransitionSlice;
import com.deathland.game.canyonbunny.util.AudioManager;
import com.deathland.game.canyonbunny.util.CameraHelper;
import com.deathland.game.canyonbunny.util.Constants;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.deathland.game.canyonbunny.game.objects.BunnyHead;
import com.deathland.game.canyonbunny.game.objects.Carrot;
import com.deathland.game.canyonbunny.game.objects.BunnyHead.JUMP_STATE;
import com.deathland.game.canyonbunny.screens.DirectedGame;
import com.deathland.game.canyonbunny.screens.MenuScreen;
import com.deathland.game.canyonbunny.screens.transitions.ScreenTransition;
import com.deathland.game.canyonbunny.game.objects.Feather;
import com.deathland.game.canyonbunny.game.objects.GoldCoin;
import com.deathland.game.canyonbunny.game.objects.Rock;

public class WorldController extends InputAdapter implements Disposable{
    private static final String TAG = WorldController.class.getName();

    private DirectedGame game;

    private void backToMenu() {
        // switch to menu screen

        ScreenTransition transition = ScreenTransitionSlice.init(2, ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);
        game.setScreen(new MenuScreen(game), transition);
    }

    public CameraHelper cameraHelper;
    public Level level;
    public int lives;
    public int score;

    public float livesVisual;
    public float scoreVisual;

    private boolean goalReached;
    public World b2World;

    public WorldController(DirectedGame game) {
        this.game = game;
        init();
    }

    private void init() {
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        livesVisual = lives;
        timeLeftGameOverDelay = 0;
        initLevel();
    }

    private void initPhysics() {
        if(b2World != null) {
            b2World.dispose();
        }
        b2World = new World(new Vector2(0, -9.8f), true);
        // Rocks
        Vector2 origin = new Vector2();
        for(Rock rock : level.rocks) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.KinematicBody;
            bodyDef.position.set(rock.position);
            Body body = b2World.createBody(bodyDef);
            rock.body = body;
            PolygonShape polygonShape = new PolygonShape();
            origin.x = rock.bounds.width / 2.0f;
            origin.y = rock.bounds.height / 2.0f;
            polygonShape.setAsBox(rock.bounds.width / 2.0f, rock.bounds.height / 2.0f, origin, 0);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }
    }

    private void initLevel() {
        score = 0;
        scoreVisual = score;
        goalReached = false;
        level = new Level(Constants.LEVEL_01);
        cameraHelper.setTarget(level.bunnyHead);
        initPhysics();
    }

    private void spawnCarrots(Vector2 pos, int numCarrots, float radius) {
        Gdx.app.debug(TAG, "spawnCarrots(pos: " + pos + ", numCarrots: " + numCarrots + ", radius: " + radius + ")");
        float carrotShapeScale = 0.5f;
        // create carrots with box2d body and fixture
        for(int i = 0; i < numCarrots; i++) {
            Carrot carrot = new Carrot();
            // calculate random spawn position, rotation, and scale
            float x = MathUtils.random(-radius, radius);
            float y = MathUtils.random(5.0f, 15.0f);

            float rotation = MathUtils.random(0.0f, 360.0f) * MathUtils.degreesToRadians;
            float carrotScale = MathUtils.random(0.5f, 1.5f);
            carrot.scale.set(carrotScale, carrotScale);
            // create box2d body for carrot with start position
            // and angle of rotatio
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(pos);
            bodyDef.position.add(x, y);
            bodyDef.angle = rotation;
            Body body = b2World.createBody(bodyDef);
            body.setType(BodyType.DynamicBody);
            carrot.body = body;
            // create rectangular shape for carrot to allow
            // interactions (collisions) with other objects
            PolygonShape polygonShape = new PolygonShape();
            float halfWidth = carrot.bounds.width / 2.0f * carrotScale;
            float halfHeight = carrot.bounds.height / 2.0f * carrotScale;
            polygonShape.setAsBox(halfWidth * carrotShapeScale, halfHeight * carrotShapeScale);
            // set physics attributes
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.density = 50;
            fixtureDef.restitution = 0.5f; // 恢复系数
            fixtureDef.friction = 0.5f;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
            // finally, add new carrot to list for updating/rendering
            level.carrots.add(carrot);
        }
    }

    private Pixmap createProceduralPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
        // Fill square with red color at 50% opacity
        pixmap.setColor(1, 0, 0, 0.5f);
        pixmap.fill();
        // Draw a yellow-colored X shape on square
        pixmap.setColor(1, 1, 0, 1);
        pixmap.drawLine(0, 0, width, height);
        pixmap.drawLine(width, 0, 0, height);
        // Draw a cyan-colored border around square
        pixmap.setColor(0, 1, 1, 1);
        pixmap.drawRectangle(0, 0, width, height);
        return pixmap;
    }

    public void update(float deltaTime) {
        handleDebugInput(deltaTime);
        if(isGameOver() || goalReached) {
            timeLeftGameOverDelay -= deltaTime;
            if(timeLeftGameOverDelay < 0) {
                backToMenu();
            }
        } else {
            handleInputGame(deltaTime);
        }
        level.update(deltaTime);
        testCollision();
        b2World.step(deltaTime, 8, 3);
        cameraHelper.update(deltaTime);
        if(!isGameOver() && isPlayerInWater()) {
            AudioManager.instance.play(Assets.instance.sounds.liveLost);
            lives --;
            if(isGameOver()) {
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            } else {
                initLevel();
            }
        }
        level.mountains.updateScrollPosition(cameraHelper.getPosition());
        if(livesVisual > lives) {
            livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);
        }
        if(scoreVisual < score) {
            scoreVisual = Math.min(score, scoreVisual + 250 * deltaTime);
        }
    }

    private void handleDebugInput(float deltaTime) {
        if(Gdx.app.getType() != Application.ApplicationType.Desktop) return;

        if(!cameraHelper.hasTarget(level.bunnyHead)) {
            // Camera Controls (move)
            float camMoveSpeed = 5 * deltaTime;
            float camMoveSpeedAccelerationFactor = 5;
            if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
                camMoveSpeed *= camMoveSpeedAccelerationFactor;
            }
            if(Gdx.input.isKeyPressed(Keys.LEFT)) {
                moveCamera(-camMoveSpeed, 0);
            }
            if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
                moveCamera(camMoveSpeed, 0);
            }
            if(Gdx.input.isKeyPressed(Keys.UP)) {
                moveCamera(0, camMoveSpeed);
            }
            if(Gdx.input.isKeyPressed(Keys.DOWN)) {
                moveCamera(0, -camMoveSpeed);
            }
            if(Gdx.input.isKeyPressed(Keys.BACKSPACE)) {
                cameraHelper.setPosition(0,0);
            }

            // Camera Controls (reset camera to current sprite)
            // Added by Tesla Lee
            if(Gdx.input.isKeyPressed(Keys.F)) {

            }
            // Camera Controls (zoom)
            float camZoomSpeed = 1 * deltaTime;
            float camZoomSpeedAccelerationFactor = 5;
            if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
                camZoomSpeed *=camZoomSpeedAccelerationFactor;
            }
            if(Gdx.input.isKeyPressed(Keys.COMMA)) {
                cameraHelper.addZoom(camZoomSpeed);
            }
            if(Gdx.input.isKeyPressed(Keys.PERIOD)) {
                cameraHelper.addZoom(-camZoomSpeed);
            }
            if(Gdx.input.isKeyPressed(Keys.SLASH)) {
                cameraHelper.setZoom(1);
            }
        }
    }

    private void handleInputGame(float deltaTime) {
        if(cameraHelper.hasTarget(level.bunnyHead)) {
            // Player Movement
            if(Gdx.input.isKeyPressed(Keys.LEFT)) {
                level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
            } else if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
                level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
            } else {
                // Execute auto-foward movement on non-desktop platform
                if(Gdx.app.getType() != ApplicationType.Desktop) {
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
                }
            }

            // Bunny Jump
            if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) {
                level.bunnyHead.setJumping(true);
            } else {
                level.bunnyHead.setJumping(false);
            }
        }
    }

    private void moveCamera(float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }

    @Override
    public void dispose() {
        if(b2World != null) {
            b2World.dispose();
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        // Reset game world
        if (keycode == Keys.R) {
            init();
            Gdx.app.debug(TAG, "Game world rested");
        } else if(keycode == Keys.ENTER) {
            // Toggle camera follow
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.bunnyHead);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        } else if(keycode == Keys.ESCAPE || keycode == Keys.BACK) {
            backToMenu();
        }

        return false;
    }

    // Rectangles for collision detection
    private Rectangle r1 = new Rectangle();
    private Rectangle r2 = new Rectangle();

    // TODO: 理解原理
    private void onCollisionBunnyHeadWithRock(Rock rock) {
        BunnyHead bunnyHead = level.bunnyHead;
        float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y +rock.bounds.height));
        if(heightDifference > 0.25f) {
            boolean hitRightEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
            if(hitRightEdge) {
                bunnyHead.position.x = rock.position.x + rock.bounds.width;
            } else {
                bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
            }
            return;
        }

        switch(bunnyHead.jumpState) {
            case GROUNDED:
                break;
            case FALLING:
            case JUMP_FALLING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                bunnyHead.jumpState = JUMP_STATE.GROUNDED;
                break;
            case JUMP_RISING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                break;
        }
    }
    
    private void onCollisionBunnyWithGoal() {
        Gdx.app.debug(TAG, "onCollisionBunnyWithGoal");
        goalReached = true;
        timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_FINISHED;
        Vector2 centerPosBunnyHead = new Vector2(level.bunnyHead.position);
        centerPosBunnyHead.x += level.bunnyHead.bounds.width;
        spawnCarrots(centerPosBunnyHead, Constants.CARROTS_SPAWN_MAX, Constants.CARROTS_SPAWN_RADIUN);
    }

    private void onCollisionBunnyWithGoldCoin(GoldCoin goldCoin) {
        goldCoin.collected = true;
        AudioManager.instance.play(Assets.instance.sounds.pickupCoin);
        score += goldCoin.getScore();
        Gdx.app.log(TAG, "Gold coin collected");
    }
    private void onCollisionBunnyWithFeather(Feather feather) {
        feather.collected = true;
        AudioManager.instance.play(Assets.instance.sounds.pickupFeather);
        score += feather.getScore();
        level.bunnyHead.setFeatherPowerup(true);
        Gdx.app.log(TAG, "Feathers collected");
    }

    private void testCollision() {
        r1.set(
            level.bunnyHead.position.x,
            level.bunnyHead.position.y,
            level.bunnyHead.bounds.width,
            level.bunnyHead.bounds.height
        );

        // Text Collision: Bunny Head <-> Rocks
        for(Rock rock : level.rocks) {
            r2.set(
                rock.position.x,
                rock.position.y,
                rock.bounds.width,
                rock.bounds.height
                );
            if(!r1.overlaps(r2)) continue;
            onCollisionBunnyHeadWithRock(rock);
            // IMPORTANT: must do all collisions for valid
            // edge testing on rocks
        }

        // Test Collision: Bunny Head <-> Gold coins
        for(GoldCoin goldCoin : level.goldCoins) {
            if(goldCoin.collected) continue;
            r2.set(
                goldCoin.position.x,
                goldCoin.position.y,
                goldCoin.bounds.width,
                goldCoin.bounds.height
            );
            if(!r1.overlaps(r2)) continue;
            onCollisionBunnyWithGoldCoin(goldCoin);
            break;
        }

        // Test Collision: Bunny Head <-> Feathers
        for(Feather feather : level.feathers) {
            if(feather.collected) continue;
            r2.set(
                feather.position.x,
                feather.position.y,
                feather.bounds.width,
                feather.bounds.height
            );
            if(!r1.overlaps(r2)) continue;
            onCollisionBunnyWithFeather(feather);
            break;
        }
        if(!goalReached) {
            r2.set(level.goal.bounds);
            r2.x += level.goal.position.x;
            r2.y += level.goal.position.y;
            if(r1.overlaps(r2)) {
                onCollisionBunnyWithGoal();
            }
        }
    }

    private float timeLeftGameOverDelay;

    public boolean isGameOver() {
        return lives < 0;
    }

    public boolean isPlayerInWater() {
        return level.bunnyHead.position.y < -5;
    }
}