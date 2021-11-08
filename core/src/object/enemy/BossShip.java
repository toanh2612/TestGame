package object.enemy;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.spacejourney.SpaceJourney;

import object.Laser;
import object.Ship;

public class BossShip extends Ship {

    Vector2 directionVector;
    float timeSinceLastDirectionChange = 0;
    float directionChangeFrequency = 1f;


    public BossShip(float movementSpeed, int health,
                    float width, float height,
                    float xCenter, float yCenter,
                    float laserWidth, float laserHeight,
                    float laserMovementSpeed, float timeBetweenShots,
                    TextureRegion shipTexture,
                    TextureRegion laserTexture) {
        super(movementSpeed, health,
                width, height,
                xCenter, yCenter,
                laserWidth, laserHeight,
                laserMovementSpeed, timeBetweenShots,
                shipTexture, laserTexture);
        directionVector = new Vector2(0, -1);
    }

    public Vector2 getDirectionVector() {
        return directionVector;
    }

    public void randomizeDirectionVector() {
        double bearing = SpaceJourney.random.nextDouble() * 6.23815;
        directionVector.x = (float) Math.sin(bearing);
        directionVector.y = (float) Math.cos(bearing);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        timeSinceLastDirectionChange += deltaTime;
        if (timeSinceLastDirectionChange > directionChangeFrequency) {
            randomizeDirectionVector();
            timeSinceLastDirectionChange -= directionChangeFrequency;
        }
    }

    @Override
    public Laser[] fireLasers() {
        Laser[] lasers = new Laser[2];
        lasers[0] = new Laser(laserMovementSpeed, boundingBox.x + boundingBox.width * 0.37f, boundingBox.y + boundingBox.height * 0.5f, laserWidth, laserHeight, laserTexture);
        lasers[1] = new Laser(laserMovementSpeed, boundingBox.x + boundingBox.width * 0.67f, boundingBox.y + boundingBox.height * 0.5f, laserWidth, laserHeight, laserTexture);
        timeSinceLastShot = 0;
        return lasers;
    }
}
