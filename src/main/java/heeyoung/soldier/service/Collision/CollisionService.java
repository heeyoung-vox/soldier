package heeyoung.soldier.service.Collision;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import heeyoung.soldier.helper.Utility;
import heeyoung.soldier.model.Bullet;
import heeyoung.soldier.model.DamageResult;
import heeyoung.soldier.model.GameWorld;
import heeyoung.soldier.model.Player;
import heeyoung.soldier.model.PlayerStat;
import heeyoung.soldier.model.Score;

@Service
public class CollisionService {
    GameWorld gameWorld;

    CollisionService(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    
    public boolean checkCollisionAgainstPlayers(Collidable object) {
        for (Player player : gameWorld.getAllPlayers().values()) {
            if (player.isAlive() && checkCollision(player, object)) {
                return true;
            }
        }
        return false;
    }
    public boolean checkCollisionAgainstScores(Collidable object) {
        for (Score score : gameWorld.getAllScores().values()) {
            if (score.isAlive() && checkCollision(score, object)) {
                return true;
            }
        }
        return false;
    }
    public boolean checkCollisionAgainstBullets(Collidable object) {
        for (Bullet bullet : gameWorld.getAllBullets().values()) {
            if (bullet.isAlive() && checkCollision(bullet, object)) {
                return true;
            }
        }
        return false;
    }

    public void processCollision() {
        // check player vs bullet
        for (Player player : gameWorld.getAllPlayers().values()) {
            for (Bullet bullet : gameWorld.getAllBullets().values()) {
                if (player.isAlive()
                     && checkCollision(player, bullet) 
                    && !bullet.getOwnerId().equals(player.getId())
                    && bullet.isAlive() 
                    && !bullet.hasHitEntity(player.getId())
                ) {
                    bullet.addHitEntity(player.getId());
                    PlayerStat stat = player.getPlayerStat();
                    double maxHealth = stat.getMaxHealth();
                    double currentHealth = stat.getCurrentHealth();
                    double newHealth = Math.clamp(currentHealth - bullet.getDamage(), 0, maxHealth);
                    player.updatePlayerStat(new PlayerStat(maxHealth, newHealth, stat.getReloadTime()));
                }
            }
        }
        //check bullet vs score
        for (Bullet bullet : gameWorld.getAllBullets().values()) {
            for (Score score : gameWorld.getAllScores().values()) {
                if (score.isAlive() && checkCollision(bullet, score) && !bullet.hasHitEntity(score.getId())) {
                    DamageResult result =  score.applyDamage(bullet.getDamage());
                    if (result.wasHit())
                        bullet.addHitEntity(score.getId());
                    if (result.lethalBlow() && result.wasHit()) {
                        try {
                            gameWorld.getPlayer(bullet.getOwnerId()).addPoints((long) score.type.scoreValue);
                        } catch (NullPointerException e) {
                            // Player no longer exists in gameWorld
                        }
                    }
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
