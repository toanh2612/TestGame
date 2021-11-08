package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;

import java.util.Locale;

import assets.Assets;
import assets.Font;
import object.Player.Player;
import object.enemy.Boss;
import utility.Constant;

public class GameHUD {
    private TextureAtlas textureAtlas;

    private TextureRegion
            playerHealthTextureRegion,
            btnPauseTextureRegion,
            healthDotTextureRegion,
            bossHealthTableTextureRegion,
            bossHealthDotLeftTextureRegion,
            bossHealthDotMiddleTextureRegion,
            bossHealthDotRightTextureRegion,
            tableScoreTextureRegion,
            scoreLabelTextureRegion;
    private int playerHealth = 100;
    private int bossHealth = 40;
    private int bossLevel = 1;
    private int score = 0;
    private Player player;
    private Boss boss;
    BitmapFont font;


    public GameHUD(int bossLevel) {
        this.bossLevel = bossLevel;
        textureAtlas = new TextureAtlas(Assets.ATLAS_SETTING);
        btnPauseTextureRegion = textureAtlas.findRegion("Pause_BTN");
        playerHealthTextureRegion = textureAtlas.findRegion("Health_Bar_Table");
        healthDotTextureRegion = textureAtlas.findRegion("Health_Dot");
        bossHealthTableTextureRegion = textureAtlas.findRegion("Boss_HP_Table");
        bossHealthDotLeftTextureRegion = textureAtlas.findRegion("Boss_HP_LEFT");
        bossHealthDotMiddleTextureRegion = textureAtlas.findRegion("Boss_HP_CENTER");
        bossHealthDotRightTextureRegion = textureAtlas.findRegion("Boss_HP_RIGHT");
        tableScoreTextureRegion = textureAtlas.findRegion("Table");
        scoreLabelTextureRegion = textureAtlas.findRegion("Score");
        player = new Player();
        boss = new Boss(bossLevel);
        fontSetup();
    }

    public void fontSetup() {
        //Create a BitmapFont from our font file
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(Font.FONT_REGULAR));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 400;
        fontParameter.borderWidth = 0.2f;
        fontParameter.color = new Color(255, 255, 255, 1);
        fontParameter.borderColor = new Color(255, 255, 255, 1);
        font = fontGenerator.generateFont(fontParameter);
        //scale the font to fit world
        font.getData().setScale(0.2f);
    }


    public void updatePlayerHealth(int health) {
        this.playerHealth = health;
        player.setHealth(playerHealth);
    }

    public void updateBossHealth(int health) {
        this.bossHealth = health;
        boss.setHealth(bossHealth);
    }

    public void updateScore(int score) {
        this.score = score;
    }


    public void drawBossHealthBar(Batch batch) {

        batch.draw(bossHealthTableTextureRegion, 50, Constant.HEIGHT - 250, Constant.WIDTH - 100, 30);
        for (int i = 0; i < boss.getHealth() / (bossLevel * 2) - 1; i++) {
            if (i < bossHealth / (bossLevel * 2) - 1) {
                if (i == 0) {
                    batch.draw(bossHealthDotLeftTextureRegion, 50 + 20, Constant.HEIGHT - 250 + 5, 50, 20);
                } else if (i == 18) {
                    batch.draw(bossHealthDotRightTextureRegion, 50 + 20 + i * 50, Constant.HEIGHT - 250 + 5, 50, 20);
                } else {
                    batch.draw(bossHealthDotMiddleTextureRegion, 50 + 20 + i * 50, Constant.HEIGHT - 250 + 5, 50, 20);
                }
            }
        }
    }

    public void draw(Batch batch) {
        batch.draw(btnPauseTextureRegion, Constant.WIDTH - 150, Constant.HEIGHT - 150, 100, 100);
        batch.draw(playerHealthTextureRegion, 50, Constant.HEIGHT - 120, 400, 70);
        //score
        font.draw(batch, String.format(Locale.getDefault(), "%04d", score), Constant.WIDTH / 2, Constant.HEIGHT - 50, 200, Align.left, false);
        for (int i = 0; i < player.getHealth() / 10; i++) {
            if (i < playerHealth / 10) {
                batch.draw(healthDotTextureRegion, 50 + 6 + i * 41, Constant.HEIGHT - 120 + 6, 40, 58);
            }
        }
    }
}
