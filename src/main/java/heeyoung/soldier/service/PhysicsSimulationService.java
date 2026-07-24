package heeyoung.soldier.service;

import org.springframework.stereotype.Service;

import heeyoung.soldier.model.Bullet;
import heeyoung.soldier.model.GameWorld;
import heeyoung.soldier.model.Player;

@Service
public class PhysicsSimulationService {
    final static double PLAYER_SPEED = 10.0f;
    GameWorld gameWorld;

    PhysicsSimulationService(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void simulate() {
        gameWorld.getAllPlayers().values().forEach(this::simulatePlayer);
        gameWorld.getAllBullets().values().forEach(this::simulateBullet);
    }

    private void simulateBullet(Bullet bullet) {
        bullet.update();
        if (!bullet.isAlive()) {
            gameWorld.removeBullet(bullet.getId());
        }
    }

    private void simulatePlayer(Player player) {
        double dx = player.getPlayerInput().getDx() * PLAYER_SPEED;
        double dy = player.getPlayerInput().getDy() * PLAYER_SPEED;

        // check border
        if (player.getX() + dx < GameWorld.MAP_WIDTH && player.getX() + dx > 0)
            player.setX(player.getX() + dx);
        else if (player.getX() + dx >= GameWorld.MAP_WIDTH)
            player.setX(GameWorld.MAP_WIDTH);
        else if (player.getX() + dx <= 0)
            player.setX(0);

        if (player.getY() + dy < GameWorld.MAP_HEIGHT && player.getY() + dy > 0)
            player.setY(player.getY() + dy);
        else if (player.getY() + dy >= GameWorld.MAP_HEIGHT)
            player.setY(GameWorld.MAP_HEIGHT);
        else if (player.getY() + dy <= 0)
            player.setY(0);
    }
}
