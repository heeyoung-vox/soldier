package heeyoung.soldier.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import heeyoung.soldier.helper.Utility;
import heeyoung.soldier.service.Collision.*;

public class Player implements Collidable {

    // --- Identity: set once, never changes after construction ---
    private final String id;
    private final String name;

    private final List<BoundingCircle> circles;
    private final AtomicReference<Position> position = new AtomicReference<>(new Position(0, 0));

    // --- Shot cooldown: atomic check-and-update, no double-fire race ---
    private final AtomicLong lastShotTime = new AtomicLong(-1);

    private final AtomicReference<PlayerInput> playerInput = new AtomicReference<>(new PlayerInput());
    private final AtomicReference<PlayerStat> stat = new AtomicReference<>(new PlayerStat());
    
    public Player(String id, String name) {
        this.id = id;
        this.name = name;

        this.circles = List.of(
                new BoundingCircle(0f, 0f, 25.0f));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    

    @Override
    public Position getPosition() {
        return position.get(); // consistent (x, y) pair, no torn reads
    }

    public void setPosition(double x, double y) {
        position.set(new Position(x, y));
    }

    

    public long getLastShotTime() {
        return lastShotTime.get();
    }

    public boolean tryShoot(long now, long cooldownTick) {
        long prev = lastShotTime.get();
        while (now - prev >= cooldownTick || prev == -1) {
            if (lastShotTime.compareAndSet(prev, now)) {
                return true; // won the race, shot accepted
            }
            prev = lastShotTime.get(); // someone else updated it, retry with fresh value
        }
        return false; // still on cooldown
    }

    

    public PlayerInput getPlayerInput() {
        return playerInput.get();
    }

    public void updateMoveInput(double dx, double dy) {
        PlayerInput current = playerInput.get();
        double[] standardized = Utility.standardize(dx, dy);
        double x = standardized[0];
        double y = standardized[1];
        while (!playerInput.compareAndSet(current, new PlayerInput(x, y, current.getAngle(), current.isShooting()))) {
            current = playerInput.get();
        }
    }

    public void updateRotateInput(double angle) {
        PlayerInput current = playerInput.get();
        while (!playerInput.compareAndSet(current,
                new PlayerInput(current.getDx(), current.getDy(), angle, current.isShooting()))) {
            current = playerInput.get();
        }
    }

    public void updateShootInput(boolean isShooting) {
        PlayerInput current = playerInput.get();
        while (!playerInput.compareAndSet(current,
                new PlayerInput(current.getDx(), current.getDy(), current.getAngle(), isShooting))) {
            current = playerInput.get();
        }
    }

    public PlayerStat getPlayerStat() {
        return stat.get();
    }

    public boolean isAlive() {
        return stat.get().getCurrentHealth() > 0;
    }

    public void updatePlayerStat(PlayerStat newStat) {
        PlayerStat current = stat.get();
        while (!stat.compareAndSet(current, newStat)) {
            current = stat.get();
        }
    }

    @Override
    public Type getCollidableType() {
        return Type.PLAYER;
    }

    @Override
    public double getAngle() {
        return playerInput.get().getAngle();
    }

    @Override
    public List<BoundingCircle> getBoundingCircles() {
        return circles;
    }
}