package com.deathland.game.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.deathland.game.canyonbunny.game.Assets;

public class Mountains extends AbstractGameObject{
    private TextureRegion regMountainLeft;
    private TextureRegion regMountainRight;

    private int length;

    public Mountains(int length) {
        this.length = length;
        init();
    }

    public void init() {
        dimension.set(10, 2);

        regMountainLeft = Assets.instance.levelDecoration.mountainLeft;
        regMountainRight = Assets.instance.levelDecoration.mountainRight;

        // shift mountain and extend length
        origin.x = -dimension.x * 2;
        length += dimension.y * 2;
    }

    private void drawMountain(SpriteBatch batch, float offsetX, float offsetY, float tintColor) {
        TextureRegion reg = null;
        batch.setColor(tintColor, tintColor, tintColor, 1);
        float relX = dimension.x * offsetX;
        float relY = dimension.y * offsetY;

        // mountains span the whole world
        int mountainLength = 0;

    }

    @Override
    public void render(SpriteBatch batch) {

    }
}
