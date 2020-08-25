package com.deathland.game.canyonbunny.util;

public class Constants {
    // Visible game world is 5 meters wide
    public static final float VIEWPORT_WIDTH = 5.0f;

    // Visible game world is 5 meters tall
    public static final float VIEWPORT_HEIGHT = 5.0f;

    // Location of description file for texture atlas
    public static final String TEXTURE_ATLAS_OBJECTS = "canyonbunny.pack.atlas";

    // Location of image file for level 01
    public static final String LEVEL_01 = "levels/level-01.png";

    // GUI Width
    public static final float VIEWPORT_GUI_WIDTH = 800.0f;
    // GUI Height
    public static final float VIEWPORT_GUI_HEIGHT = 480.0f;
    // Amount of extra lives at level start
    public static final int LIVES_START = 3;

    // Duration of feather power-up in seconds
    public static final float ITEM_FEATHER_POWERUP_DURATION = 9;

    // Delay after game over
    public static final float TIME_DELAY_GAME_OVER = 3;
 
    public static final String TEXTURE_ATLAS_UI = "canyonbunny-ui.pack.atlas";
    public static final String TEXTURE_ATLAS_LIBGDX_UI = "uiskin.atlas";
    public static final String SKIN_LIBGDX_UI = "uiskin.json";
    public static final String SKIN_CANYONBUNNY_UI = "canyonbunny-ui.json";

    public static final String PREFERENCES = "canyonbunny.preferences";

    // Number of carrots to spawn
    public static final int CARROTS_SPAWN_MAX = 100;

    // Spawn radius for carrots
    public static final float CARROTS_SPAWN_RADIUN = 3.5f;

    // Delay after game finished
    public static final float TIME_DELAY_GAME_FINISHED = 6;
}
