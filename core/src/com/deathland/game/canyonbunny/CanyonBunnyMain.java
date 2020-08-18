package com.deathland.game.canyonbunny;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.deathland.game.canyonbunny.game.Assets;
import com.deathland.game.canyonbunny.screens.MenuScreen;

public class CanyonBunnyMain extends Game {
	static final String TAG = CanyonBunnyMain.class.getName();
	
	@Override
	public void create () {
		// Set Libgdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// TODO: change to LOG_NONE or LOG_INFO before publish code

		// Load Assets
		Assets.instance.init(new AssetManager());

		// Start game at menu screen
		setScreen(new MenuScreen(this));
	}
}
