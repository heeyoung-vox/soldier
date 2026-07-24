package heeyoung.soldier.model;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe Player.
 *
 * Design:
 * - id / name are set once at creation and never mutated afterward -> plain
 * finals, no sync needed.
 * - Position (x, y) is stored as a single immutable Position snapshot behind an
 * AtomicReference,
 * so readers never see a torn combination of old-x/new-y.
 * - lastShotTime uses AtomicLong with compareAndSet so the "can I shoot?"
 * check-then-update
 * is a single atomic step (no double-fire race).
 * - playerInput / stat still need the same treatment internally if their fields
 * are mutated
 * from multiple threads (see note at the bottom).
 */
public class Player {

    // --- Identity: set once, never changes after construction ---
    private final String id;
    private final String name;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static final class Position {
        public final double x;
        public final double y;

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private final AtomicReference<Position> position = new AtomicReference<>(new Position(0, 0));

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

    public void updatePlayerStat(PlayerStat newStat) {
        stat.set(newStat);
    }
}