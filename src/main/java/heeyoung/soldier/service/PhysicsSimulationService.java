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
        int nextRemainingTime = bullet.getRemainingTime() - 1;
        if (nextRemainingTime <= 0) {
            gameWorld.removeBullet(bullet.getId());
        } else {
            Bullet updatedBullet = new Bullet(
                    bullet.getId(),
                    bullet.getOwnerId(),
                    bullet.getX() + bullet.getVx(),
                    bullet.getY() + bullet.getVy(),
                    bullet.getVx(),
                    bullet.getVy(),
                    nextRemainingTime);
            gameWorld.addBullet(updatedBullet);
        }
    }

    private void simulatePlayer(Player player) {
        double dx = player.getPlayerInput().getDx() * PLAYER_SPEED;
        double dy = player.getPlayerInput().getDy() * PLAYER_SPEED;

        Player.Position pos = player.getPosition();
        double new_x, new_y;

        // check border
        if (pos.x + dx < GameWorld.MAP_WIDTH && pos.x + dx > 0)
            new_x = pos.x + dx;
        else if (pos.x + dx >= GameWorld.MAP_WIDTH)
            new_x = GameWorld.MAP_WIDTH;
        else
            new_x = 0;

        if (pos.y + dy < GameWorld.MAP_HEIGHT && pos.y + dy > 0)
            new_y = pos.y + dy;
        else if (pos.y + dy >= GameWorld.MAP_HEIGHT)
            new_y = GameWorld.MAP_HEIGHT;
        else
            new_y = 0;

        player.setPosition(new_x, new_y);
    }
}
