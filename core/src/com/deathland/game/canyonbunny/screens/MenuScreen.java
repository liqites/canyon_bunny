package com.deathland.game.canyonbunny.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.deathland.game.canyonbunny.game.Assets;
import com.deathland.game.canyonbunny.screens.transitions.ScreenTransition;
import com.deathland.game.canyonbunny.screens.transitions.ScreenTransitionFade;
import com.deathland.game.canyonbunny.screens.transitions.ScreenTransitionSlice;
import com.deathland.game.canyonbunny.util.CharacterSkin;
import com.deathland.game.canyonbunny.util.Constants;
import com.deathland.game.canyonbunny.util.GamePreferences;

public class MenuScreen extends AbstractGameScreen {
   private static final String TAG = MenuScreen.class.getName();

   private Stage stage;
   private Skin skincanyonBunny;

   // menu
   private Image imgBackground;
   private Image imgLogo;
   private Image imgInfo;
   private Image imgCoins;
   private Image imgBunny;
   private Button btnMenuPlay;
   private Button btnMenuOptions;

   // options
   private Window winOptions;
   private TextButton btnWinOptSave;
   private TextButton btnWinOptCancel;
   private CheckBox chkSound;
   private Slider sldSound;
   private CheckBox chkMusic;
   private Slider sldMusic;
   private SelectBox<CharacterSkin> selCharSkin;
   private Image imgCharSkin;
   private CheckBox chkShowFpsCounter;

   // debug
   private final float DEBUG_REBUILD_INTERVAL = 5.0f;
   private boolean debugEnabled = false;
   private float debugRebuildStage;

   public MenuScreen(DirectedGame game) {
      super(game);
   }

   private Skin skinLibgdx;

   private void loadSettings() {
      GamePreferences prefs = GamePreferences.instance;
      prefs.load();

      chkSound.setChecked(prefs.sound);
      sldSound.setValue(prefs.volSound);
      chkMusic.setChecked(prefs.music);
      sldMusic.setValue(prefs.volMusic);
      selCharSkin.setSelectedIndex(prefs.charSkin);
      onCharSkinSelected(prefs.charSkin);
      chkShowFpsCounter.setChecked(prefs.showFpsCounter);
   }

   private void saveSettings() {
      GamePreferences prefs = GamePreferences.instance;
      prefs.sound = chkSound.isChecked();
      prefs.volSound = sldSound.getValue();
      prefs.music = chkMusic.isChecked();
      prefs.volMusic = sldMusic.getValue();
      prefs.charSkin = selCharSkin.getSelectedIndex();
      prefs.showFpsCounter = chkShowFpsCounter.isChecked();
      prefs.save();
   }

   private void onCharSkinSelected(int index) {
      CharacterSkin skin = CharacterSkin.values()[index];
      imgCharSkin.setColor(skin.getColor());
   }

   private void onSaveClicked() {
      saveSettings();
      onCancelClicked();
   }

   private void onCancelClicked() {
      btnMenuPlay.setVisible(true);
      btnMenuOptions.setVisible(true);
      winOptions.setVisible(false);
   }

   private void rebuildStage() {
      skincanyonBunny = new Skin(Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI),
            new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

      skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
            new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));

      // build all layers
      Table layerBackground = buildBackgroundLayer();
      Table layerObjects = buildObjectsLayer();
      Table layerLogos = buildLogosLayer();
      Table layerControls = buildControlsLayer();
      Table layerOptionsWindow = buildOptionsWindowLayer();

      // assemble stage for menu screen
      stage.clear();
      Stack stack = new Stack();
      stage.addActor(stack);
      stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
      stack.add(layerBackground);
      stack.add(layerObjects);
      stack.add(layerLogos);
      stack.add(layerControls);
      stage.addActor(layerOptionsWindow);
   }

   private Table buildOptWinAudioSettings() {
      Table tbl = new Table();
      // + Title: "Audio"
      tbl.pad(10, 10, 0, 10);
      tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
      tbl.row();
      tbl.columnDefaults(0).padRight(10);
      tbl.columnDefaults(1).padRight(10);
      // +Checkbox, "Sound" label, sound volume slider
      chkSound = new CheckBox("", skinLibgdx);
      tbl.add(chkSound);
      tbl.add(new Label("Sound", skinLibgdx));
      sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
      tbl.add(sldSound);
      tbl.row();
      // + Checkbox, "Music" label, music volume slider
      chkMusic = new CheckBox("", skinLibgdx);
      tbl.add(chkMusic);
      tbl.add(new Label("Music", skinLibgdx));
      sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
      tbl.add(sldMusic);
      tbl.row();
      return tbl;
   }

   private Table buildOptWinSkinSelection() {
      Table tbl = new Table();
      // + Tiele: "Character Skin"
      tbl.pad(10, 10, 0, 10);
      tbl.add(new Label("Character Skin", skinLibgdx, "default-font", Color.ORANGE)).colspan(2);
      // + Drop down box filled with skin items
      selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx);
      selCharSkin.setItems(CharacterSkin.values());

      selCharSkin.addListener(new ChangeListener() {
         @Override
         public void changed(ChangeEvent event, Actor actor) {
            onCharSkinSelected(((SelectBox<CharacterSkin>) actor).getSelectedIndex());
         }
      });

      tbl.add(selCharSkin).width(120).padRight(20);
      // + Skin preview image
      imgCharSkin = new Image(Assets.instance.bunny.head);
      tbl.add(imgCharSkin).width(50).height(50);
      return tbl;
   }

   private Table buildOptWinDebug() {
      Table tbl = new Table();
      // + Table: "Debug"
      tbl.pad(10, 10, 0, 10);
      tbl.add(new Label("Debug", skinLibgdx, "default-font", Color.RED)).colspan(3);
      tbl.row();
      tbl.columnDefaults(0).padRight(10);
      tbl.columnDefaults(1).padRight(10);
      // + CheckBox, "Show FPS Counter" label
      chkShowFpsCounter = new CheckBox("", skinLibgdx);
      tbl.add(new Label("Show FPS Counter", skinLibgdx));
      tbl.add(chkShowFpsCounter);
      tbl.row();
      return tbl;
   }

   private Table buildOptWinButtons() {
      Table tbl = new Table();
      // + Separator
      Label lbl = null;
      lbl = new Label("", skinLibgdx);
      lbl.setColor(0.75f, 0.75f, 0.75f, 1);
      lbl.setStyle(new LabelStyle(lbl.getStyle()));
      lbl.getStyle().background = skinLibgdx.newDrawable("white");
      tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
      tbl.row();
      lbl = new Label("", skinLibgdx);
      lbl.setColor(0.5f, 0.5f, 0.5f, 1);
      lbl.setStyle(new LabelStyle(lbl.getStyle()));
      lbl.getStyle().background = skinLibgdx.newDrawable("white");
      tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
      tbl.row(); // + Save Button with event handler
      btnWinOptSave = new TextButton("Save", skinLibgdx);
      tbl.add(btnWinOptSave).padRight(30);
      btnWinOptSave.addListener(new ChangeListener() {
         @Override
         public void changed(ChangeEvent event, Actor actor) {
            onSaveClicked();
         }
      }); 
      // + Cancel Button with event handler
      btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
      tbl.add(btnWinOptCancel);
      btnWinOptCancel.addListener(new ChangeListener() {
         @Override
         public void changed(ChangeEvent event, Actor actor) {
            onCancelClicked();
         }
      });
      return tbl;
   }

   private Table buildBackgroundLayer() {
      Table layer = new Table();
      // + Background
      imgBackground = new Image(skincanyonBunny, "background");
      layer.add(imgBackground);
      return layer;
   }

   private Table buildObjectsLayer() {
      Table layer = new Table();
      // + Coins
      imgCoins = new Image(skincanyonBunny, "coins");
      layer.addActor(imgCoins);
      imgCoins.setPosition(135, 80);
      // + Bunny
      imgBunny = new Image(skincanyonBunny, "bunny");
      layer.addActor(imgBunny);
      imgBunny.setPosition(355, 40);
      return layer;
   }

   private Table buildLogosLayer() {
      Table layer = new Table();
      layer.left().top();
      // + Game Logo
      imgLogo = new Image(skincanyonBunny, "logo");
      layer.add(imgLogo);
      layer.row().expandY();
      // + Info Logos
      imgInfo = new Image(skincanyonBunny, "info");
      layer.add(imgInfo).bottom();
      if (debugEnabled)
         layer.debug();
      return layer;
   }

   private Table buildControlsLayer() {
      Table layer = new Table();
      layer.right().bottom();
      // + Play Button
      btnMenuPlay = new Button(skincanyonBunny, "play");
      layer.add(btnMenuPlay);
      btnMenuPlay.addListener(new ChangeListener() {
         @Override
         public void changed(ChangeEvent event, Actor actor) {
            onPlayClicked();
         }
      });

      layer.row();
      // + Options Button
      btnMenuOptions = new Button(skincanyonBunny, "options");
      layer.add(btnMenuOptions);
      btnMenuOptions.addListener(new ChangeListener() {
         @Override
         public void changed(ChangeEvent event, Actor actor) {
            onOptionsClicked();
         }
      });
      return layer;
   }

   private void onPlayClicked() {
      Gdx.app.debug(TAG, "onPlayClicked");
       ScreenTransition transition = ScreenTransitionFade.init(0.75f);
//      ScreenTransition transition = ScreenTransitionSlice.init(2, ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);
      game.setScreen(new GameScreen(game), transition);
   }

   private void onOptionsClicked() {
      Gdx.app.debug(TAG, "onOptionsClicked");
      loadSettings();
      btnMenuPlay.setVisible(false);
      btnMenuOptions.setVisible(false);
      winOptions.setVisible(true);
   }

   private Table buildOptionsWindowLayer() {
      winOptions = new Window("Options", skinLibgdx);
      // + Aduio Settings: Sound/Music CheckBox and Volumne Slider
      winOptions.add(buildOptWinAudioSettings()).row();
      // + Character Skin: Selection Box (White, Gray, Brown)
      winOptions.add(buildOptWinSkinSelection()).row();
      // + Debug: Show FPS COunter
      winOptions.add(buildOptWinDebug()).row();
      // + Separator and Buttons (Save, Cancel)
      winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);

      // Make options window slightly transparent
      winOptions.setColor(1, 1, 1, 0.8f);
      // Hide Options window by default
      winOptions.setVisible(false);
      if(debugEnabled) winOptions.debug();
      // Let TableLayout recalculate widget sizes and positions
      winOptions.pack();
      // Move options window to botton right corner
      winOptions.setPosition(Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
      return winOptions;
   }

   @Override
   public void render(float deltaTime) {
      Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

      if (debugEnabled) {
         debugRebuildStage -= deltaTime;
         if (debugRebuildStage <= 0) {
            debugRebuildStage = DEBUG_REBUILD_INTERVAL;
            rebuildStage();
         }
      }
      stage.act(deltaTime);
      stage.draw();

      // FIXME: 原来的方法已经废弃了，使用新的 debug 方法
      // Table.drawDebug(stage);
      stage.setDebugAll(true);

      // if (Gdx.input.isTouched()) {
         // game.setScreen(new GameScreen(game));
      // }
   }

   @Override
   public void resize(int width, int height) {
      stage.getViewport().update(width, height, true);
   }

   @Override
   public void show() {
      stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
      rebuildStage();
   }

   @Override
   public void hide() {
      stage.dispose();
      skincanyonBunny.dispose();
   }

   @Override
   public void pause() {
   }

   @Override
   public InputProcessor getInputProcessor() {
      return stage;
   }
}