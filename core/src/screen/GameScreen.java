package screen;


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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import assets.Assets;
import object.Explosion;
import object.Player.Player;
import object.Player.PlayerShip;
import object.enemy.Boss;
import object.enemy.BossShip;
import object.enemy.CreepShip;
import object.Laser;
import utility.Constant;
import view.GameHUD;


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
            creepShipTextureRegion, creepLaserTextureRegion,
            creepShipTwoTextureRegion, creepLaserTwoTextureRegion,
            bossShipTextureRegion, bossLaserTextureRegion;

    private Texture enemyExplosionTexture;
    private PlayerShip playerShip;
    private LinkedList<CreepShip> creepShipList;
    private BossShip bossShip;

    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> creepLaserList;
    private LinkedList<Laser> bossLaserlist;

    private LinkedList<Explosion> explosionList;
    private float timeBetweenCreepSpawns = 1f;
    private float creepSpawnTimer = 0;
    private int score = 0;
    private int crepCount = 0;
    private int playerHealth = 0;
    private int bossHealth = 0;
    //timing
    private int backgroundOffset;


    //head up Display
    private GameHUD gameHUD;
    private Player player;
    private Boss boss;

    public GameScreen() {
        camera = new OrthographicCamera(); //overview
        viewport = new StretchViewport(Constant.WIDTH, Constant.HEIGHT, camera);
        textureAtlas = new TextureAtlas(Assets.ATLAS_SHIP_AND_SHOT);

        gameHUD = new GameHUD(2);
        player = new Player();
        boss = new Boss(2);

        playerHealth = player.getHealth();
        bossHealth = boss.getHealth();

        background = textureAtlas.findRegion("background");
        backgroundOffset = 0;

        playerShipTextureRegion = textureAtlas.findRegion("player_ship");
        playerLaserTextureRegion = textureAtlas.findRegion("player_amor");

        creepShipTextureRegion = textureAtlas.findRegion("crep1");
        creepShipTwoTextureRegion = textureAtlas.findRegion("crep2");
        creepLaserTwoTextureRegion = textureAtlas.findRegion("crep_amor2");
        creepLaserTextureRegion = textureAtlas.findRegion("crep_amor1");

        bossShipTextureRegion = textureAtlas.findRegion("boss1");
        bossLaserTextureRegion = textureAtlas.findRegion("crep_amor1");

        enemyExplosionTexture = new Texture(Assets.CREP_EXPLOSION);
        //setup game object
        //ship
        playerShip = new PlayerShip(
                player.getShipMovementSpeed(),
                player.getHealth(),
                200, 200,
                Constant.WIDTH / 2, Constant.HEIGHT / 6,
                20, 80,
                player.getLaserMovementSpeed(),
                player.getTimeBetweenShots(),
                playerShipTextureRegion,
                playerLaserTextureRegion);
        bossShip = new BossShip(boss.getShipMovementSpeed(),
                boss.getHealth(),
                250, 250,
                SpaceJourney.random.nextFloat() * Constant.WIDTH - 250,
                Constant.HEIGHT - 250,
                20, 80,
                boss.getLaserMovementSpeed(),
                boss.getTimeBetweenShots(),
                bossShipTextureRegion, bossLaserTextureRegion);
        creepShipList = new LinkedList<>();

        //laser
        playerLaserList = new LinkedList<>();
        creepLaserList = new LinkedList<>();
        bossLaserlist = new LinkedList<>();

        //explosion
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
        gameHUD.updatePlayerHealth(playerHealth);
        gameHUD.updateBossHealth(bossHealth);
        gameHUD.updateScore(score);
        //draw ship
        playerShip.draw(batch);

        if (score >= Constant.MAX_SCORE_LEVEL_1 / 2) {
            gameHUD.drawBossHealthBar(batch);
            moveBoss(bossShip, delta);
            bossShip.update(delta);
            bossShip.draw(batch);
        } else {
            spawnCreepShip(delta);
            for (CreepShip creepShip : creepShipList) {
                moveCreep(creepShip, delta);
                creepShip.update(delta);
                creepShip.draw(batch);
            }
        }

        //draw laser
        renderLaser(delta);

        //detect collisions between lasers and ships
        detectCollisions();

        renderExplosion(delta);

        gameHUD.draw(batch);
        batch.end();
    }

    private void spawnCreepShip(float delta) {
        creepSpawnTimer += delta;
        float isEvenNumber = (int) (Math.random() * 100) % 2;
        if (crepCount < Constant.TOTAL_CREP_LEVEL_1 && creepSpawnTimer > timeBetweenCreepSpawns) {
            creepShipList.add(
                    new CreepShip(
                            200, 1,
                            150, 150,
                            SpaceJourney.random.nextFloat() * Constant.WIDTH - 160, Constant.HEIGHT - 160,
                            40, 80, 100, 1,
                            isEvenNumber == 0 ? creepShipTextureRegion : creepShipTwoTextureRegion,
                            isEvenNumber == 0 ? creepLaserTextureRegion : creepLaserTwoTextureRegion));
            creepSpawnTimer -= timeBetweenCreepSpawns;
            crepCount++;
        }
    }

    private void detectInput(float delta) {
        //keyboard
        float leftLimit, rightLimit, upLimit, downLimit;
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

    private void moveBoss(BossShip bossShip, float delta) {
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -bossShip.boundingBox.x;
        downLimit = (float) Constant.HEIGHT / 2 - bossShip.boundingBox.y;
        rightLimit = Constant.WIDTH - bossShip.boundingBox.x - bossShip.boundingBox.width;
        upLimit = Constant.HEIGHT - bossShip.boundingBox.y - bossShip.boundingBox.height;

        //scale to the maximum speed of the ship
        float xMove = bossShip.getDirectionVector().x * bossShip.movementSpeed * delta;
        float yMove = bossShip.getDirectionVector().y * bossShip.movementSpeed * delta;

        if (xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove, leftLimit);

        if (yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove, downLimit);

        bossShip.translate(xMove, yMove);
    }

    private void moveCreep(CreepShip creepShip, float delta) {
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -creepShip.boundingBox.x;
        downLimit = (float) Constant.HEIGHT / 2 - creepShip.boundingBox.y;
        rightLimit = Constant.WIDTH - creepShip.boundingBox.x - creepShip.boundingBox.width;
        upLimit = Constant.HEIGHT - creepShip.boundingBox.y - creepShip.boundingBox.height;

        //scale to the maximum speed of the ship
        float xMove = creepShip.getDirectionVector().x * creepShip.movementSpeed * delta;
        float yMove = creepShip.getDirectionVector().y * creepShip.movementSpeed * delta;

        if (xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove, leftLimit);

        if (yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove, downLimit);

        creepShip.translate(xMove, yMove);
    }


    /**
     * check whether player or enemy laser intersects
     */
    public void detectCollisions() {
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (score < (int) Constant.MAX_SCORE_LEVEL_1 / 2) {
                ListIterator<CreepShip> creepShipListIterator = creepShipList.listIterator();
                while (creepShipListIterator.hasNext()) {
                    CreepShip creepShip = creepShipListIterator.next();
                    if (creepShip.isIntersect(laser.boundingBox)) {
                        if (creepShip.hitAndCheckDestroy(laser)) {
                            creepShipListIterator.remove();
                            explosionList.add(new Explosion(enemyExplosionTexture,
                                    0.7f,
                                    new Rectangle(creepShip.boundingBox)));
                            score = score + 50;
                        }
                        laserListIterator.remove();
                        break;
                    }
                }
            } else {
                if (bossShip.isIntersect(laser.boundingBox)) {
                    if (bossShip.hitAndCheckDestroy(laser)) {
                        explosionList.add(
                                new Explosion(enemyExplosionTexture,
                                        1.6f,
                                        new Rectangle(bossShip.boundingBox)));
                        score = score + 500;
                    }
                    bossHealth--;
                    laserListIterator.remove();
                }
            }
        }
        if (score < (int) Constant.MAX_SCORE_LEVEL_1 / 2) {
            laserListIterator = creepLaserList.listIterator();
        } else {
            laserListIterator = bossLaserlist.listIterator();
        }
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.isIntersect(laser.boundingBox)) {
                if (playerShip.hitAndCheckDestroy(laser)) {
                    explosionList.add(
                            new Explosion(enemyExplosionTexture,
                                    1.6f,
                                    new Rectangle(playerShip.boundingBox)));

                }
                playerHealth--;
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


    private void renderLaser(float delta) {
        if (playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.fireLasers();
            playerLaserList.addAll(Arrays.asList(lasers));
        }
        if (bossShip.canFireLaser()) {
            Laser[] lasers = bossShip.fireLasers();
            bossLaserlist.addAll(Arrays.asList(lasers));
        }

        ListIterator<CreepShip> creepShipListIterator = creepShipList.listIterator();
        while (creepShipListIterator.hasNext()) {
            CreepShip creepShip = creepShipListIterator.next();
            if (creepShip.canFireLaser()) {
                Laser[] lasers = creepShip.fireLasers();
                creepLaserList.addAll(Arrays.asList(lasers));
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


        iterator = creepLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * delta;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }

        iterator = bossLaserlist.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * delta;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
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
