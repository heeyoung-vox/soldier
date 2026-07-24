package heeyoung.soldier.model;

public class Bullet {
    private final String id;
    private final String ownerId;
    private double x;
    private double y;
    private final double vx;
    private final double vy;
    private double lifetime;

    public Bullet(String id, String ownerId, double x, double y, double vx, double vy, double lifetime) {
        this.id = id;
        this.ownerId = ownerId;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.lifetime = lifetime;
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

    public double getLifetime() {
        return lifetime;
    }

    public void setLifetime(double lifetime) {
        this.lifetime = lifetime;
    }

    public void update(double deltaTime) {
        x += vx * deltaTime;
        y += vy * deltaTime;
        lifetime -= deltaTime;
    }

    public boolean isAlive() {
        return lifetime > 0;
    }
}