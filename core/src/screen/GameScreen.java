package screen;


import static utility.Constant.TOUCH_MOVEMENT_THRESHOLD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.spacejourney.SpaceJourney;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import assets.Assets;
import object.Explosion;
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

    private TextureRegion
            playerShipTextureRegion, playerLaserTextureRegion,
            enemyShipTextureRegion, enemyLaserTextureRegion,
            enemyShipTwoTextureRegion, enemyLaserTwoTextureRegion,
            bossShipTextureRegion, bossLaserTextureRegion;

    private Texture enemyExplosionTexture;
    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipList;

    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;
    private LinkedList<Explosion> explosionList;
    private float timeBetweenEnemySpawns = 1f;
    private float enemySpawnTimer = 0;
    private int score = 0;
    private int crepCount = 0;

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
        enemyShipTwoTextureRegion = textureAtlas.findRegion("enemy_crep2");


        playerLaserTextureRegion = textureAtlas.findRegion("player_amor");
        enemyLaserTwoTextureRegion = textureAtlas.findRegion("crep_amor2");
        enemyLaserTextureRegion = textureAtlas.findRegion("crep_amor1");

        bossShipTextureRegion = textureAtlas.findRegion("boss1");
        bossLaserTextureRegion = textureAtlas.findRegion("shot1");

        enemyExplosionTexture = new Texture(Assets.CREP_EXPLOSION);
        //setup game object
        playerShip = new PlayerShip(1200, 100,
                200, 200,
                Constant.WIDTH / 2, Constant.HEIGHT / 6,
                20, 80, 200, 0.5f,
                playerShipTextureRegion, playerLaserTextureRegion);

        enemyShipList = new LinkedList<>();
        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();
        explosionList = new LinkedList<>();

        batch = new SpriteBatch();

    }


    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        batch.begin();

        renderBackground();
        detectInput(delta);

        playerShip.update(delta);
        spawnEnemyShip(delta);

        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            moveEnemy(enemyShip, delta);
            enemyShip.update(delta);
            enemyShip.draw(batch);

        }


        //draw ship
        playerShip.draw(batch);

        //draw laser
        renderLaser(delta);

        //detect collisions between lasers and ships
        detectCollisions();

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

    private void spawnEnemyShip(float delta) {
        enemySpawnTimer += delta;
        float isEvenNumber = (int) (Math.random() * 100) % 2;
        if (enemySpawnTimer > timeBetweenEnemySpawns && crepCount < Constant.TOTAL_CREP_LEVEL_1) {
            crepCount = crepCount + 1;

            System.out.println("score"+score);
//            boolean isBossAppear = score >= Constant.MAX_SCORE_LEVEL_1 / 2;
//            System.out.println(score >= Constant.MAX_SCORE_LEVEL_1 / 2);
            enemyShipList.add(
                    new EnemyShip(
                            200, 1,
                            150, 150,
                            SpaceJourney.random.nextFloat() * Constant.WIDTH - 160, Constant.HEIGHT - 160,
                            40, 80, 100, 2,
                            isEvenNumber == 0 ? enemyShipTextureRegion : enemyShipTwoTextureRegion,
                            isEvenNumber == 0 ? enemyLaserTextureRegion : enemyLaserTwoTextureRegion));
            enemySpawnTimer -= timeBetweenEnemySpawns;
        }
    }

    /**
     * @param enemyShip
     * @param delta     return enemies
     */
    private void moveEnemy(EnemyShip enemyShip, float delta) {
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
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                Collections.addAll(enemyLaserList, lasers);
            }
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
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while (enemyShipListIterator.hasNext()) {
                EnemyShip enemyShip = enemyShipListIterator.next();
                if (enemyShip.isIntersect(laser.boundingBox)) {
                    if (enemyShip.hitAndCheckDestroy(laser)) {
                        System.out.println("destroy" + enemyShip.hitAndCheckDestroy(laser));
                        score = score + 50;
                        enemyShipListIterator.remove();
                        explosionList.add(new Explosion(enemyExplosionTexture,
                                0.7f,
                                new Rectangle(enemyShip.boundingBox)));
                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }

        laserListIterator = enemyLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.isIntersect(laser.boundingBox)) {
                if (playerShip.hitAndCheckDestroy(laser)) {
                    explosionList.add(
                            new Explosion(enemyExplosionTexture,
                                    1.6f,
                                    new Rectangle(playerShip.boundingBox)));
                    playerShip.shield = 10;
                }
                laserListIterator.remove();
            }
        }
    }

    public void renderExplosion(float delta) {
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext()) {
            Explosion explosion = explosionListIterator.next();
            explosion.update(delta);
            if (explosion.isFinished()) {
                explosionListIterator.remove();
            } else {
                explosion.draw(batch);
            }
        }
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
