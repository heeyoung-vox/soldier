package heeyoung.soldier.service;

import org.springframework.stereotype.Service;
import heeyoung.soldier.model.Player;

@Service
public class PhysicsSimulationService {
    final static double PLAYER_SPEED = 10.0f;

    public void simulate(Player player) {
        double dx = player.getPlayerInput().getDx() * PLAYER_SPEED;
        double dy = player.getPlayerInput().getDy() * PLAYER_SPEED;
        player.setX(player.getX() + dx);
        player.setY(player.getY() + dy);
    }
}
