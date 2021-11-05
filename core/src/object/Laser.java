package object;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


public class Laser {

    public Rectangle boundingBox;
    //laser physical characteristic
    public float movementSpeed;



    TextureRegion textureRegion;

    public Laser(float movementSpeed,
                 float xCenter, float yBottom,
                 float width, float height,
                 TextureRegion textureRegion) {
        this.movementSpeed = movementSpeed;
        this.boundingBox = new Rectangle(xCenter - width / 2, yBottom - height / 2, width, height);
        this.textureRegion = textureRegion;
    }



    public void draw(Batch batch) {
        batch.draw(textureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }

    /**
     * @return a bounding box wrap laser
     */
    public Rectangle getBoundingBox() {
        return boundingBox;
    }
}
