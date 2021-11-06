package object;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class Ship {
    public float movementSpeed;
    public int shield;


    //laser information

    public float laserWidth, laserHeight;
    public float timeBetweenShots;
    public float laserMovementSpeed;
    public float timeSinceLastShot = 0;


    //graphics

    TextureRegion shipTexture;
    public TextureRegion laserTexture;

    public Rectangle boundingBox;


    public Ship(float movementSpeed, int shield,
                float width, float height,
                float xCenter, float yCenter,
                float laserWidth, float laserHeight, float laserMovementSpeed,
                float timeBetweenShots,
                TextureRegion shipTexture,
                TextureRegion laserTexture) {
        this.movementSpeed = movementSpeed;
        this.shield = shield;

        this.boundingBox = new Rectangle(xCenter - width / 2, yCenter - height / 2, width, height);
        this.laserWidth = laserWidth;
        this.laserHeight = laserHeight;
        this.laserMovementSpeed = laserMovementSpeed;
        this.timeBetweenShots = timeBetweenShots;
        this.shipTexture = shipTexture;
        this.laserTexture = laserTexture;
    }

    public void update(float deltaTime) {
        timeSinceLastShot += deltaTime;
    }

    public boolean canFireLaser() {
        return (timeSinceLastShot - timeBetweenShots >= 0);
    }

    public abstract Laser[] fireLasers();

    /**
     * @param otherRectangle
     * @return true or false if laser intersects with other laser
     */
    public boolean isIntersect(Rectangle otherRectangle) {
        return boundingBox.overlaps(otherRectangle);
    }

    public boolean hitAndCheckDestroy(Laser laser) {
        if (shield > 0) {
            shield--;
            return false;
        }
        return true;
    }

    public void translate(float xChange, float yChange) {
        boundingBox.setPosition(boundingBox.x + xChange, boundingBox.y + yChange);
    }

    public void draw(Batch batch) {
        batch.draw(shipTexture, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }
}
