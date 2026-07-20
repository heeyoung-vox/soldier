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

import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import heeyoung.soldier.model.GameWorld;
import heeyoung.soldier.model.Player;
import heeyoung.soldier.model.PlayerInput;
import heeyoung.soldier.service.NameGeneratorService;

@Component
public class GameWebSocketHandler extends AbstractWebSocketHandler {

    GameWorld gameWorld;
    NameGeneratorService nameGenerator;
    ObjectMapper mapper = new ObjectMapper();
    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public GameWebSocketHandler(GameWorld gameWorld, NameGeneratorService nameGenerator) {
        this.gameWorld = gameWorld;
        this.nameGenerator = nameGenerator;
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

    private void handleMoveMessage(WebSocketSession session, JsonNode root) {
        Player player = gameWorld.getPlayer(session.getId());
        if (player == null) {
            return;
        }
        PlayerInput input = player.getPlayerInput();
        input.setDx(root.get("dx").asDouble());
        input.setDy(root.get("dy").asDouble());
        input.setAngle(root.get("angle").asDouble());

    }

    private void handleJoinMessage(WebSocketSession session, JsonNode root) {
        if (gameWorld.getPlayer(session.getId()) != null) {
            return;
        }

        AtomicReference<String> nameRef = new AtomicReference<>();
        do {
            nameRef.set(nameGenerator.generateRandomName());
        } while (gameWorld.getAllPlayers().values().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(nameRef.get())));

        Player newPlayer = new Player();
        newPlayer.setId(session.getId());
        newPlayer.setName(nameRef.get());
        gameWorld.addPlayer(newPlayer);
        sessions.add(session);

        // one time welcome packet
        try {
            Map<String, String> welcomeMessage = new HashMap<>();
            welcomeMessage.put("type", "WELCOME");
            welcomeMessage.put("id", newPlayer.getId());

            String jsonMessage = mapper.writeValueAsString(welcomeMessage);
            session.sendMessage(new TextMessage(jsonMessage));
        } catch (IOException e) {
            System.out.println("Failed to send unique welcome packet: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        gameWorld.removePlayer(session.getId());
        sessions.remove(session);
    }

    public static List<WebSocketSession> getSessions() {
        return sessions;
    }

}
