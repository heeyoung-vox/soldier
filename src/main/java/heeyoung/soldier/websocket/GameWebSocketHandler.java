package heeyoung.soldier.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import heeyoung.soldier.model.GameWorld;
import heeyoung.soldier.model.Player;
import heeyoung.soldier.model.PlayerInput;
import heeyoung.soldier.model.PlayerStat;
import heeyoung.soldier.service.GameMechanicService;
import heeyoung.soldier.service.NameGeneratorService;

@Component
public class GameWebSocketHandler extends AbstractWebSocketHandler {

    GameWorld gameWorld;
    NameGeneratorService nameGenerator;
    GameMechanicService gameMechanicService;

    ObjectMapper mapper = new ObjectMapper();
    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public GameWebSocketHandler(GameWorld gameWorld,
            NameGeneratorService nameGenerator,
            GameMechanicService gameMechanicService) {
        this.gameWorld = gameWorld;
        this.nameGenerator = nameGenerator;
        this.gameMechanicService = gameMechanicService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            if (message.getPayload() != null && !message.getPayload().trim().isEmpty()) {
                JsonNode root = mapper.readTree(message.getPayload());
                if (root.has("type")) {
                    String messageType = root.get("type").asText().toUpperCase();
                    switch (messageType) {
                        case "JOIN":
                            handleJoinMessage(session, root);
                            break;
                        case "MOVE":
                            handleMoveMessage(session, root);
                            break;
                        case "ROTATE":
                            handleRotateMessage(session, root);
                            break;
                        case "SHOOT":
                            handleShootMessage(session, root);
                            break;
                        default:
                            System.out.println("Unknown message type: " + messageType);
                            break;
                    }
                }

            }
        } catch (StreamReadException e) {
            System.out.println("SteamReadException: " + e.getMessage());
        } catch (JacksonException e) {
            System.out.println("JacksonException Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("General Error: " + e.getMessage());
        }
    }

    private void handleShootMessage(WebSocketSession session, JsonNode root) {
        Player player = gameWorld.getPlayer(session.getId());
        if (player == null || !player.isAlive()) {
            return;
        }
        player.updateShootInput(true);

    }

    private void handleMoveMessage(WebSocketSession session, JsonNode root) {
        Player player = gameWorld.getPlayer(session.getId());
        if (player == null || !player.isAlive()) {
            return;
        }
        player.updateMoveInput(root.get("dx").asDouble(), root.get("dy").asDouble());
    }

    private void handleRotateMessage(WebSocketSession session, JsonNode root) {
        Player player = gameWorld.getPlayer(session.getId());
        if (player == null || !player.isAlive()) {
            return;
        }
        player.updateRotateInput(root.get("angle").asDouble());
    }

    private void handleJoinMessage(WebSocketSession session, JsonNode root) {
        if (gameWorld.getPlayer(session.getId()) != null) {
            return;
        }

        String name;
        do {
            name = nameGenerator.generateRandomName();
        } while (!gameWorld.claimName(name));

        Player newPlayer = new Player(session.getId(), name);

        // starting stat for player
        newPlayer.updatePlayerStat(new PlayerStat(100, 100, 10));

        boolean isSucceeded = gameWorld.addPlayer(newPlayer);
        if (isSucceeded) {

            WebSocketSession decoratedSession = new ConcurrentWebSocketSessionDecorator(session, 10000, 512 * 1024);
            sessions.removeIf(s -> s.getId().equals(session.getId()));
            sessions.add(decoratedSession);

            // one time welcome packet
            try {
                Map<String, String> welcomeMessage = new HashMap<>();
                welcomeMessage.put("type", "WELCOME");
                welcomeMessage.put("id", newPlayer.getId());
                welcomeMessage.put("map-width", String.valueOf(gameWorld.MAP_WIDTH));
                welcomeMessage.put("map-height", String.valueOf(gameWorld.MAP_HEIGHT));
                String jsonMessage = mapper.writeValueAsString(welcomeMessage);
                decoratedSession.sendMessage(new TextMessage(jsonMessage));
            } catch (IOException e) {
                System.out.println("Failed to send unique welcome packet: " + e.getMessage());
            }
        } else {
            try {
                Map<String, String> errorMessage = new HashMap<>();
                errorMessage.put("type", "DENIED");
                errorMessage.put("message", "Server is full");
                String jsonMessage = mapper.writeValueAsString(errorMessage);
                session.sendMessage(new TextMessage(jsonMessage));
            } catch (IOException e) {
                System.out.println("Failed to send error packet: " + e.getMessage());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        gameWorld.removePlayer(session.getId());
        sessions.removeIf(s -> s.getId().equals(session.getId()));
    }

    public static List<WebSocketSession> getSessions() {
        return sessions;
    }

}
