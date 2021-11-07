package view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import assets.Assets;
import utility.Constant;

public class GameHUD {
    private TextureAtlas textureAtlas;

    private TextureRegion playerHealthTextureRegion, btnPauseTextureRegion, healthDotTextureRegion;
    private int health;
    public GameHUD() {
        textureAtlas = new TextureAtlas(Assets.ATLAS_SETTING);
        btnPauseTextureRegion = textureAtlas.findRegion("Pause_BTN");
        playerHealthTextureRegion = textureAtlas.findRegion("Health_Bar_Table");
        healthDotTextureRegion = textureAtlas.findRegion("Health_Dot");
    }

    public void updatePlayerHealth(int health) {
        System.out.println(health);
        this.health = health;
    }
    public void draw(Batch batch) {
        batch.draw(btnPauseTextureRegion, Constant.WIDTH - 150, Constant.HEIGHT - 150, 100, 100);
        batch.draw(playerHealthTextureRegion, 50, Constant.HEIGHT - 120, 400, 70);

        batch.draw(healthDotTextureRegion, 50 + 6, Constant.HEIGHT - 120 + 6, 40, 58);
        batch.draw(healthDotTextureRegion, 50 + 6 + 40, Constant.HEIGHT - 120 + 6, 40, 58);
    }
}
