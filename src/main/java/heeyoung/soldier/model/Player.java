package heeyoung.soldier.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import heeyoung.soldier.service.Collision.*;

public class Player implements Collidable {

    // --- Identity: set once, never changes after construction ---
    private final String id;
    private final String name;

    private final List<BoundingCircle> circles;

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

    private final AtomicReference<Position> position = new AtomicReference<>(new Position(0, 0));

    @Override
    public Position getPosition() {
        return position.get(); // consistent (x, y) pair, no torn reads
    }

    public void setPosition(double x, double y) {
        position.set(new Position(x, y));
    }

    // --- Shot cooldown: atomic check-and-update, no double-fire race ---
    private final AtomicLong lastShotTime = new AtomicLong(-1);

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

    private final AtomicReference<PlayerInput> playerInput = new AtomicReference<>(new PlayerInput());
    private final AtomicReference<PlayerStat> stat = new AtomicReference<>(new PlayerStat());

    public PlayerInput getPlayerInput() {
        return playerInput.get();
    }

    public void updateMoveInput(double dx, double dy) {
        PlayerInput current = playerInput.get();
        while (!playerInput.compareAndSet(current, new PlayerInput(dx, dy, current.getAngle()))) {
            current = playerInput.get();
        }
    }

    public void updateRotateInput(double angle) {
        PlayerInput current = playerInput.get();
        while (!playerInput.compareAndSet(current, new PlayerInput(current.getDx(), current.getDy(), angle))) {
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
    public Type getType() {
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