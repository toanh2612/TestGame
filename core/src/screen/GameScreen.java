package screen;


import static utility.Constant.TOUCH_MOVEMENT_THRESHOLD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.spacejourney.SpaceJourney;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

import assets.Assets;
import object.enemy.EnemyShip;
import object.Laser;
import object.player.PlayerShip;
import object.Ship;
import utility.Constant;


public class GameScreen implements Screen {
    //screen
    private Camera camera;
    private Viewport viewport;
    //graphics
    private SpriteBatch batch;

    private TextureAtlas textureAtlas;
    private TextureRegion background;

    private TextureRegion playerShipTextureRegion,
            playerShieldTextureRegion,
            enemyShipTextureRegion, enemyShieldTextureRegion,
            enemyLaserTextureRegion, playerLaserTextureRegion;

    private PlayerShip playerShip;
    private EnemyShip enemyShip;

    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;

    //timing
    private int backgroundOffset;


    public GameScreen() {
        camera = new OrthographicCamera(); //overview
        viewport = new StretchViewport(Constant.WIDTH, Constant.HEIGHT, camera);
        textureAtlas = new TextureAtlas(Assets.ATLAS_SHIP_AND_SHOT);
        background = textureAtlas.findRegion("background");
        backgroundOffset = 0;

        playerShipTextureRegion = textureAtlas.findRegion("player_ship");
        enemyShipTextureRegion = textureAtlas.findRegion("enemy_crep1");

        playerShieldTextureRegion = textureAtlas.findRegion("player_ship");
        enemyShieldTextureRegion = textureAtlas.findRegion("enemy_crep1");

        playerLaserTextureRegion = textureAtlas.findRegion("player_amor");
        enemyLaserTextureRegion = textureAtlas.findRegion("crep_amor1");

        //setup game object
        playerShip = new PlayerShip(400, 3,
                200, 200,
                Constant.WIDTH / 2, Constant.HEIGHT / 6,
                20, 80, 200, 1,
                playerShieldTextureRegion, playerShipTextureRegion, playerLaserTextureRegion);
        enemyShip = new EnemyShip(300, 3,
                150, 150,
                SpaceJourney.random.nextFloat() * Constant.WIDTH - 160, Constant.HEIGHT - 160,
                40, 80, 200, 1,
                enemyShieldTextureRegion, enemyShipTextureRegion, enemyLaserTextureRegion);

        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();

        batch = new SpriteBatch();


    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        batch.begin();

        detectInput(delta);
        moveEnemies(delta);

        playerShip.update(delta);
        enemyShip.update(delta);
        //scrolling background
        renderBackground();

        //draw ship
        playerShip.draw(batch);
        enemyShip.draw(batch);

        //draw laser
        renderLaser(delta);

        detectCollisions();

        //draw explosion
        renderExplosion(delta);

        batch.end();
    }

    private void detectInput(float delta) {
        //keyboard
        float leftLimit, rightLimit, upLimit, downLimit;
//        System.out.println(-playerShip.boundingBox.x);
        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = Constant.WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = (float) Constant.HEIGHT / 2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0) {
            playerShip.translate(Math.min(playerShip.movementSpeed * delta, rightLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0) {
            playerShip.translate(0f, Math.min(playerShip.movementSpeed * delta, upLimit));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0) {
            playerShip.translate(Math.max(-playerShip.movementSpeed * delta, leftLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0) {
            playerShip.translate(0f, Math.max(-playerShip.movementSpeed * delta, downLimit));
        }

        //mouse also touch screen
        if (Gdx.input.isTouched()) {
            //get the screen position of the touch
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            //convert to world position
            Vector2 touchPoint = new Vector2(xTouchPixels, yTouchPixels);
            touchPoint = viewport.unproject(touchPoint);

            //calculate the x and y differences
            Vector2 playerShipCentre = new Vector2(
                    playerShip.boundingBox.x + playerShip.boundingBox.width / 2,
                    playerShip.boundingBox.y + playerShip.boundingBox.height / 2);

            float touchDistance = touchPoint.dst(playerShipCentre);

            if (touchDistance > Constant.TOUCH_MOVEMENT_THRESHOLD) {
                float xTouchDifference = touchPoint.x - playerShipCentre.x;
                float yTouchDifference = touchPoint.y - playerShipCentre.y;

                //scale to the maximum speed of the ship
                float xMove = xTouchDifference / touchDistance * playerShip.movementSpeed * delta;
                float yMove = yTouchDifference / touchDistance * playerShip.movementSpeed * delta;

                if (xMove > 0) xMove = Math.min(xMove, rightLimit);
                else xMove = Math.max(xMove, leftLimit);

                if (yMove > 0) yMove = Math.min(yMove, upLimit);
                else yMove = Math.max(yMove, downLimit);

                playerShip.translate(xMove, yMove);
            }
        }
    }

    private void renderBackground() {
        backgroundOffset++;
        if (backgroundOffset % Constant.HEIGHT == 0) {
            backgroundOffset = 0;
        }
        //loop background
        batch.draw(background, 0, -backgroundOffset, Constant.WIDTH, Constant.HEIGHT);
        batch.draw(background, 0, -backgroundOffset + Constant.HEIGHT, Constant.WIDTH, Constant.HEIGHT);
    }

    private void moveEnemies(float delta) {
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -enemyShip.boundingBox.x;
        downLimit = (float) Constant.HEIGHT / 2 - enemyShip.boundingBox.y;
        rightLimit = Constant.WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        upLimit = Constant.HEIGHT - enemyShip.boundingBox.y - enemyShip.boundingBox.height;

        //scale to the maximum speed of the ship
        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * delta;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed * delta;

        if (xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove, leftLimit);

        if (yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove, downLimit);

        enemyShip.translate(xMove, yMove);
    }

    /**
     * @param delta draw laser
     */
    private void renderLaser(float delta) {
        if (playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.fireLasers();
            Collections.addAll(playerLaserList, lasers);
        }

        if (enemyShip.canFireLaser()) {
            Laser[] lasers = enemyShip.fireLasers();
            Collections.addAll(enemyLaserList, lasers);
        }


        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed * delta;
            if (laser.boundingBox.y > Constant.HEIGHT) {
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * delta;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
    }

    /**
     * check whether player or enemy laser intersects
     */
    public void detectCollisions() {
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            if (enemyShip.isIntersect(laser.boundingBox)) {
                playerShip.hit(laser);
                iterator.remove();
            }
        }

        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            if (playerShip.isIntersect(laser.boundingBox)) {
                enemyShip.hit(laser);
                iterator.remove();
            }
        }
    }

    public void renderExplosion(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
