package heeyoung.soldier.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import heeyoung.soldier.service.Collision.Collidable;
import heeyoung.soldier.service.Collision.Type;
import heeyoung.soldier.service.Collision.BoundingCircle;

public class Score implements Collidable {
    private static final AtomicLong scoreIdCounter = new AtomicLong(0);

    private final String id;
    private final ScoreType type;
    private double x;
    private double y;
    private int currentHealth;
    private final List<BoundingCircle> circles;

    public Score(ScoreType type, double x, double y) {
        this.id = String.valueOf(scoreIdCounter.getAndIncrement());
        this.type = type;
        this.x = x;
        this.y = y;
        this.currentHealth = type.maxHealth;
        this.circles = List.of(new BoundingCircle(0.0, 0.0, type.radius));
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public String getId() {
        return id;
    }

    public ScoreType getScoreType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public Type getCollidableType() {
        return Type.SCORE;
    }

    @Override
    public Position getPosition() {
        return new Position(x, y);
    }
    
    @Override 
    public double getAngle() {
        return 0;
    }

    @Override 
    public List<BoundingCircle> getBoundingCircles() {
        return circles;
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public heeyoung.soldier.dto.ScoreDto toScoreDto() {
        return switch (type) {
            case Yellow -> new heeyoung.soldier.dto.ScoreDto(id, "yellow", x, y, currentHealth);
            case Maroon -> new heeyoung.soldier.dto.ScoreDto(id, "maroon", x, y, currentHealth);
            case Blue -> new heeyoung.soldier.dto.ScoreDto(id, "blue", x, y, currentHealth);
        };
    }
}
