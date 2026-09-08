package heeyoung.soldier.service.Collision;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import heeyoung.soldier.helper.Utility;
import heeyoung.soldier.model.Bullet;
import heeyoung.soldier.model.GameWorld;
import heeyoung.soldier.model.Player;
import heeyoung.soldier.model.PlayerStat;

@Service
public class CollisionService {
    private final Map<String, Collidable> collidables = new ConcurrentHashMap<>();
    GameWorld gameWorld;

    CollisionService(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void processCollision() {
        // check player vs bullet
        for (Player player : gameWorld.getAllPlayers().values()) {
            for (Bullet bullet : gameWorld.getAllBullets().values()) {
                if (player.isAlive() && checkCollision(player, bullet) && !bullet.getOwnerId().equals(player.getId())) {
                    PlayerStat stat = player.getPlayerStat();
                    double maxHealth = stat.getMaxHealth();
                    double currentHealth = stat.getCurrentHealth();
                    double newHealth = Math.clamp(currentHealth - bullet.getDamage(), 0, maxHealth);
                    player.updatePlayerStat(new PlayerStat(maxHealth, newHealth, stat.getReloadTime()));
                }
            }
        }
    }

    private boolean checkCollision(Collidable objectA, Collidable objectB) {
        List<BoundingCircle> listA = objectA.getBoundingCircles();
        List<BoundingCircle> listB = objectB.getBoundingCircles();

        for (BoundingCircle circleA : listA) {
            for (BoundingCircle circleB : listB) {
                double[] posA = Utility.rotate(
                        objectA.getPosition().x() + circleA.offsetX(),
                        objectA.getPosition().y() + circleA.offsetY(),
                        objectA.getPosition().x(),
                        objectA.getPosition().y(),
                        objectA.getAngle());

                double[] posB = Utility.rotate(
                        objectB.getPosition().x() + circleB.offsetX(),
                        objectB.getPosition().y() + circleB.offsetY(),
                        objectB.getPosition().x(),
                        objectB.getPosition().y(),
                        objectB.getAngle());

                double dx = posB[0] - posA[0];
                double dy = posB[1] - posA[1];
                double radiusSum = circleA.radius() + circleB.radius();

                if (dx * dx + dy * dy <= radiusSum * radiusSum) {
                    return true;
                }
            }
        }

        return false;
    }

}
