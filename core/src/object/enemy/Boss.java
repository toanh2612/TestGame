package object.enemy;

public class Boss {
    public int health = 40;
    public int shipMovementSpeed = 150;
    public int laserMovementSpeed = 150;
    public float timeBetweenShots = 0.2f;
    public int bossLevel = 1;

    public Boss(int bossLevel) {
        this.bossLevel = bossLevel;
        initBoss();
    }
    private void initBoss() {
        switch (bossLevel) {
            case 1:
                setHealth(40);
                setLaserMovementSpeed(150);
                setLaserMovementSpeed(150);
                setTimeBetweenShots(0.7f);
                break;
            case 2:
                setHealth(80);
                setLaserMovementSpeed(180);
                setLaserMovementSpeed(180);
                setTimeBetweenShots(0.6f);
                break;
            case 3:
                setHealth(120);
                setLaserMovementSpeed(200);
                setLaserMovementSpeed(200);
                setTimeBetweenShots(0.5f);
                break;
        }
    }

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
