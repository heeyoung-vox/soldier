package heeyoung.soldier.service;

import java.io.IOException;

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
        gameWorld.getAllPlayers().values().forEach(physicsSimulationService::simulate);

        var players = gameWorld.getAllPlayers().values().stream()
                .map(p -> new heeyoung.soldier.dto.PlayerDto(p.getId(), p.getName(), p.getX(), p.getY(),
                        p.getPlayerInput().getAngle()))
                .toList();
        String playerList = mapper.writeValueAsString(players);
        for (WebSocketSession session : GameWebSocketHandler.getSessions()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(playerList));

                } catch (IOException e) {
                    session.close();
                    GameWebSocketHandler.getSessions().remove(session);
                }
            }
        }
    }
}
