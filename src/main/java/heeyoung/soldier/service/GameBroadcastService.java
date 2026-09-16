package heeyoung.soldier.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import heeyoung.soldier.model.GameWorld;
import heeyoung.soldier.model.Player;
import heeyoung.soldier.model.PlayerStat;
import heeyoung.soldier.model.Position;
import heeyoung.soldier.websocket.GameWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Service
public class GameBroadcastService {
    GameWorld gameWorld;
    PhysicsSimulationService physicsSimulationService;
    ObjectMapper mapper = new ObjectMapper();

    public GameBroadcastService(GameWorld gameWorld, PhysicsSimulationService physicsSimulationService) {
        this.gameWorld = gameWorld;
        this.physicsSimulationService = physicsSimulationService;
    }

    @Scheduled(fixedRate = 50)
    public void BroadcastMessage() throws Exception {
        // Run physics simulation for all players before broadcasting
        physicsSimulationService.simulate();

        // remove dead player and notify their session with a QUIT signal
        List<Player> deadPlayers = gameWorld.getAllPlayers().values().stream()
                .filter(p -> !p.isAlive())
                .toList();

        for (Player p : deadPlayers) {
            gameWorld.removePlayer(p.getId());

            Map<String, Object> quitPayload = new HashMap<>();
            quitPayload.put("type", "QUIT");
            quitPayload.put("reason", "DEAD");
            String quitJson = mapper.writeValueAsString(quitPayload);

            WebSocketSession deadSession = GameWebSocketHandler.getSessions().stream()
                    .filter(s -> s.getId().equals(p.getId()))
                    .findFirst()
                    .orElse(null);

            if (deadSession != null && deadSession.isOpen()) {
                try {
                    deadSession.sendMessage(new TextMessage(quitJson));
                } catch (IOException e) {
                    try {
                        deadSession.close();
                    } catch (IOException ignored) {
                    }
                    GameWebSocketHandler.getSessions().removeIf(s -> s.getId().equals(deadSession.getId()));
                }
            }
        }

        // remove expired bullets
        gameWorld.getAllBullets().values().forEach(b -> {
            if (b.getRemainingTime() <= 0) {
                gameWorld.removeBullet(b.getId());
            }
        });

        // remove dead scores
        gameWorld.getAllScores().values().stream()
                .filter(s -> !s.isAlive())
                .forEach(s -> gameWorld.removeScore(s.getId()));

        var players = gameWorld.getAllPlayers().values().stream()
                .map(p -> {
                    Position pos = p.getPosition();
                    PlayerStat stat = p.getPlayerStat();
                    return new heeyoung.soldier.dto.PlayerDto(
                            p.getId(), p.getName(), pos.x(), pos.y(),
                            p.getPlayerInput().getAngle(),
                            stat.getMaxHealth(),
                            stat.getCurrentHealth(),
                            p.getPoints());
                })
                .toList();
        var bullets = gameWorld.getAllBullets().values().stream()
                .map(b -> new heeyoung.soldier.dto.BulletDto(b.getId(), b.getX(), b.getY()))
                .toList();

        var scores = gameWorld.getAllScores().values().stream()
                .map(s -> s.toScoreDto())
                .toList();

        if (players.size() == 0) {
            gameWorld.resetTick();
            return;
        }

        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("type", "WORLD_STATE");
        messagePayload.put("players", players);
        messagePayload.put("bullets", bullets);
        messagePayload.put("scores", scores);
        messagePayload.put("tick", gameWorld.incrementAndGetTick());
        String broadcastPayload = mapper.writeValueAsString(messagePayload);
        for (WebSocketSession session : GameWebSocketHandler.getSessions()) {
            if (session.isOpen() && gameWorld.getPlayer(session.getId()) != null) {
                try {
                    session.sendMessage(new TextMessage(broadcastPayload));

                } catch (IOException e) {
                    try {
                        session.close();
                    } catch (IOException ignored) {
                    }
                    GameWebSocketHandler.getSessions().removeIf(s -> s.getId().equals(session.getId()));
                }
            }
        }
    }
}
