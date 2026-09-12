package heeyoung.soldier.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import heeyoung.soldier.service.Collision.Collidable;
import heeyoung.soldier.service.Collision.BoundingCircle;
import heeyoung.soldier.service.Collision.Type;

/**
 * Thread-safe Bullet containing an immutable BulletStat.
 */
public final class Bullet implements Collidable {
    private final String id;
    private final String ownerId;
    private final AtomicReference<BulletStat> stat;
    private final List<BoundingCircle> circles;

    public record BulletStat(double x, double y, double vx, double vy, int remainingTime, double damage) {
    }

    public Bullet(String id, String ownerId,
            double x, double y, double vx, double vy, int remainingTime, double damage) {
        this.id = id;
        this.ownerId = ownerId;
        this.stat = new AtomicReference<>(new BulletStat(x, y, vx, vy, remainingTime, damage));
        this.circles = List.of(new BoundingCircle(0.0, 0.0, 7.5f)); // Default bounding circle with radius 5.0
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public double getX() {
        return stat.get().x();
    }

    public double getY() {
        return stat.get().y();
    }

    public double getVx() {
        return stat.get().vx();
    }

    public double getVy() {
        return stat.get().vy();
    }

    public int getRemainingTime() {
        return stat.get().remainingTime();
    }

    public double getDamage() {
        return stat.get().damage();
    }

    public BulletStat getStat() {
        return stat.get();
    }

    public void updateStat(BulletStat newStat) {
        BulletStat current = stat.get();
        while (!stat.compareAndSet(current, newStat)) {
            current = stat.get();
        }
    }

    @Override
    public double getAngle() {
        BulletStat current = stat.get();
        return Math.atan2(current.vy(), current.vx());
    }

    @Override
    public Position getPosition() {
        BulletStat current = stat.get();
        return new Position(current.x(), current.y());
    }

    @Override
    public List<BoundingCircle> getBoundingCircles() {
        return circles;
    }

    @Override
    public Type getCollidableType() {
        return Type.BULLET;
    }

    //implement stop bullet logic later
    public boolean isAlive() {
        return getRemainingTime() > 0;
    }    
}