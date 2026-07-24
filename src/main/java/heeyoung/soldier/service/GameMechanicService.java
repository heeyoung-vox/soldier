package heeyoung.soldier.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import heeyoung.soldier.model.Bullet;
import heeyoung.soldier.model.GameWorld;
import heeyoung.soldier.model.Player;
import heeyoung.soldier.helper.*;

@Service
public class GameMechanicService {
    GameWorld gameWorld;
    private final AtomicLong bulletIdCounter = new AtomicLong(0);
    private final AtomicLong scoreIdCounter = new AtomicLong(0);

    private final double BULLET_SPEED = 1;
    private final int BULLET_REMAINING_TIME = 30; // tick

    public GameMechanicService(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void PlayerShoot(Player player) {

        if (player.getPlayerStat().getReloadTime() <= gameWorld.getCurrentTick() - player.getLastShotTime()
                || player.getLastShotTime() == -1) {
            player.setLastShotTime(gameWorld.getCurrentTick());
            double[] velocity = Utility.angleToVector(player.getPlayerInput().getAngle());
            Bullet bullet = new Bullet(
                    Long.toString(bulletIdCounter.getAndIncrement()),
                    player.getId(),
                    player.getX(),
                    player.getY(),
                    velocity[0] * BULLET_SPEED,
                    velocity[1] * BULLET_SPEED,
                    BULLET_REMAINING_TIME);
            gameWorld.addBullet(bullet);
        }
        System.out.println("bullet number: " + gameWorld.getAllBullets().size());
    }
}
