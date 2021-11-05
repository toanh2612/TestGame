package object.player;

import com.badlogic.gdx.graphics.g2d.TextureRegion;


import object.Laser;
import object.Ship;

public class PlayerShip extends Ship {

    public PlayerShip(float movementSpeed, int shield,
                      float width, float height,
                      float xCenter, float yCenter,
                      float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShots,
                      TextureRegion shieldTexture,
                      TextureRegion shipTexture,
                      TextureRegion laserTexture) {
        super(movementSpeed, shield, width, height, xCenter, yCenter, laserWidth, laserHeight, laserMovementSpeed, timeBetweenShots, shieldTexture, shipTexture, laserTexture);
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] lasers = new Laser[2];
        lasers[0] = new Laser(laserMovementSpeed, boundingBox.x + boundingBox.width * 0.07f, boundingBox.y + boundingBox.height * 0.5f, laserWidth, laserHeight, laserTexture);
        lasers[1] = new Laser(laserMovementSpeed, boundingBox.x + boundingBox.width * 0.97f, boundingBox.y + boundingBox.height * 0.5f, laserWidth, laserHeight, laserTexture);
        timeSinceLastShot = 0;
        return lasers;
    }

}
