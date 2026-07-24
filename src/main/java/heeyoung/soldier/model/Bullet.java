package heeyoung.soldier.model;

public class Bullet {
    private final String id;
    private final String ownerId;
    private double x;
    private double y;
    private final double vx;
    private final double vy;
    private int remainingTime;

    public Bullet(String id, String ownerId,
            double x, double y, double vx, double vy, int remainingTime) {
        this.id = id;
        this.ownerId = ownerId;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.remainingTime = remainingTime;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public int getremainingTime() {
        return remainingTime;
    }

    public void setremainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void update() {
        x += vx;
        y += vy;
        remainingTime -= 1;
    }

    public boolean isAlive() {
        return remainingTime > 0;
    }
}