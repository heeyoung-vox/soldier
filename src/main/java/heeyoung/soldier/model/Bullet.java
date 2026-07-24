package heeyoung.soldier.model;

/**
 * Immutable Bullet value object.
 */
public final class Bullet {
    private final String id;
    private final String ownerId;
    private final double x;
    private final double y;
    private final double vx;
    private final double vy;
    private final int remainingTime;

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

    public int getRemainingTime() {
        return remainingTime;
    }
}