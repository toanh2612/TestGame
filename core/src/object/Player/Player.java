package object.Player;

public class Player {
    public int health = 80;
    public int shipMovementSpeed = 1200;
    public int laserMovementSpeed = 300;
    public float timeBetweenShots = 0.5f;

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getShipMovementSpeed() {
        return shipMovementSpeed;
    }

    public void setShipMovementSpeed(int shipMovementSpeed) {
        this.shipMovementSpeed = shipMovementSpeed;
    }

    public int getLaserMovementSpeed() {
        return laserMovementSpeed;
    }

    public void setLaserMovementSpeed(int laserMovementSpeed) {
        this.laserMovementSpeed = laserMovementSpeed;
    }

    public float getTimeBetweenShots() {
        return timeBetweenShots;
    }

    public void setTimeBetweenShots(float timeBetweenShots) {
        this.timeBetweenShots = timeBetweenShots;
    }
}
