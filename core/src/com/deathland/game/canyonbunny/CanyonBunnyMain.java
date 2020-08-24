package com.deathland.game.canyonbunny;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Interpolation;
import com.deathland.game.canyonbunny.game.Assets;
import com.deathland.game.canyonbunny.screens.DirectedGame;
import com.deathland.game.canyonbunny.screens.MenuScreen;
import com.deathland.game.canyonbunny.screens.transitions.ScreenTransition;
import com.deathland.game.canyonbunny.screens.transitions.ScreenTransitionSlice;
import com.deathland.game.canyonbunny.util.AudioManager;
import com.deathland.game.canyonbunny.util.GamePreferences;

public class CanyonBunnyMain extends DirectedGame {
	static final String TAG = CanyonBunnyMain.class.getName();
	
	@Override
	public void create () {
		// Set Libgdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// TODO: change to LOG_NONE or LOG_INFO before publish code

		// Load Assets
		Assets.instance.init(new AssetManager());

		// Load preferences for audio settings and start playing music
		GamePreferences.instance.load();
		AudioManager.instance.play(Assets.instance.music.song01);

		// Start game at menu screen
		ScreenTransition transition = ScreenTransitionSlice.init(2, ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);
		setScreen(new MenuScreen(this));
	}
}
