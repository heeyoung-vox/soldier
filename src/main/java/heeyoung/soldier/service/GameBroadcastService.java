package heeyoung.soldier.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import heeyoung.soldier.model.GameWorld;
import heeyoung.soldier.websocket.GameWebSocketHandler;
import tools.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;

@Service
public class GameBroadcastService {
    GameWorld gameWorld;
    PhysicsSimulationService physicsSimulationService;
    ObjectMapper mapper = new ObjectMapper();

    public GameBroadcastService(GameWorld gameWorld, PhysicsSimulationService physicsSimulationService) {
        this.gameWorld = gameWorld;
        this.physicsSimulationService = physicsSimulationService;
    }

    // Thread-safe collection to hold active player sessions

    @Scheduled(fixedRate = 50)
    public void BroadcastMessage() throws Exception {
        // Run physics simulation for all players before broadcasting
        physicsSimulationService.simulate();

        var players = gameWorld.getAllPlayers().values().stream()
                .map(p -> new heeyoung.soldier.dto.PlayerDto(p.getId(), p.getName(), p.getX(), p.getY(),
                        p.getPlayerInput().getAngle()))
                .toList();
        var bullets = gameWorld.getAllBullets().values().stream()
                .map(b -> new heeyoung.soldier.dto.BulletDto(b.getId(), b.getX(), b.getY()))
                .toList();
        if (players.size() == 0) {
            gameWorld.resetTick();
            return;
        }

        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("type", "WORLD_STATE");
        messagePayload.put("players", players);
        messagePayload.put("bullets", bullets);
        messagePayload.put("tick", gameWorld.incrementAndGetTick());
        String broadcastPayload = mapper.writeValueAsString(messagePayload);
        for (WebSocketSession session : GameWebSocketHandler.getSessions()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(broadcastPayload));

                } catch (IOException e) {
                    session.close();
                    GameWebSocketHandler.getSessions().remove(session);
                }
            }
        }
    }
}
