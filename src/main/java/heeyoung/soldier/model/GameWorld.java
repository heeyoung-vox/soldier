package heeyoung.soldier.model;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GameWorld {
    public static final double MAP_WIDTH = 2000.0;
    public static final double MAP_HEIGHT = 2000.0;
    public static final double MIN_SPAWN_DISTANCE = 50.0;
    public static final int MAX_PLAYERS = 10;

    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final Map<String, Bullet> bullets = new ConcurrentHashMap<>();
    private final Map<String, Score> scores = new ConcurrentHashMap<>();
    private final Set<String> activeNames = ConcurrentHashMap.newKeySet();
    private final Object spawnLock = new Object();

    public boolean claimName(String name) {
        return activeNames.add(name.toLowerCase());
    }

    public boolean addPlayer(Player player) {

        synchronized (spawnLock) {
            if (players.size() >= MAX_PLAYERS)
                return false;
            double x = ThreadLocalRandom.current().nextDouble(MAP_WIDTH);
            double y = ThreadLocalRandom.current().nextDouble(MAP_HEIGHT);

            while (!checkValidPlayerPos(x, y)) {
                x = ThreadLocalRandom.current().nextDouble(MAP_WIDTH);
                y = ThreadLocalRandom.current().nextDouble(MAP_HEIGHT);
            }

            player.setPosition(x, y);
            players.put(player.getId(), player);
            return true;
        }
    }

    public void addBullet(Bullet bullet) {
        bullets.put(bullet.getId(), bullet);
    }

    public void addScore(Score score) {
        scores.put(score.getId(), score);
    }

    public void removePlayer(String id) {
        synchronized (spawnLock) {
            Player player = players.remove(id);
            if (player != null) {
                activeNames.remove(player.getName().toLowerCase());
            }
        }
    }

    public void removeBullet(String id) {
        bullets.remove(id);
    }

    public void removeScore(String id) {
        scores.remove(id);
    }

    public Player getPlayer(String id) {
        return players.get(id);
    }

    public Bullet getBullet(String id) {
        return bullets.get(id);
    }

    public Score getScore(String id) {
        return scores.get(id);
    }

    public Map<String, Player> getAllPlayers() {
        return players;
    }

    public Map<String, Bullet> getAllBullets() {
        return bullets;
    }

    public Map<String, Score> getAllScores() {
        return scores;
    }

    private boolean checkValidPlayerPos(double x, double y) {
        boolean status = true;
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Position position = entry.getValue().getPosition();
            double ex = position.x();
            double ey = position.y();

            double dx = ex - x;
            double dy = ey - y;

            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < MIN_SPAWN_DISTANCE) {
                status = false;
                break;
            }
        }

        return status;
    }

    private final AtomicLong currentTick = new AtomicLong(0);

    // Call this inside your @Scheduled 20Hz tick method
    public long incrementAndGetTick() {
        return currentTick.incrementAndGet();
    }

    public long getCurrentTick() {
        return currentTick.get();
    }

    // Call this when all players disconnect
    public void resetTick() {
        currentTick.set(0);
    }

}
