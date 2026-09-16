package heeyoung.soldier.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import heeyoung.soldier.service.Collision.Collidable;
import heeyoung.soldier.service.Collision.Type;
import heeyoung.soldier.service.Collision.BoundingCircle;

public class Score implements Collidable {
    private static final AtomicLong scoreIdCounter = new AtomicLong(0);

    private final String id;
    public final ScoreType type;
    private double x;
    private double y;
    private final AtomicReference<Double> currentHealth;
    private final List<BoundingCircle> circles;

    public Score(ScoreType type, double x, double y) {
        this.id = String.valueOf(scoreIdCounter.getAndIncrement());
        this.type = type;
        this.x = x;
        this.y = y;
        this.currentHealth= new AtomicReference<Double>(type.maxHealth);
        this.circles = List.of(new BoundingCircle(0.0, 0.0, type.radius));
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public DamageResult applyDamage(double damage) {
        
        double prev = currentHealth.getAndUpdate(current -> {
            if (current <= 0) return current; 
            return Math.max(0, current - damage);
        });
        if (prev <= 0) {
            return new DamageResult(0, false, false); 
        }
        double next = Math.max(0, prev - damage);
        boolean lethality = (next == 0); 
        return new DamageResult(next, lethality, true);
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

    public double getCurrentHealth() {
        return currentHealth.get();
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
        return currentHealth.get() > 0;
    }

    public heeyoung.soldier.dto.ScoreDto toScoreDto() {
        return switch (type) {
            case Yellow -> new heeyoung.soldier.dto.ScoreDto(id, "yellow", x, y, currentHealth.get());
            case Maroon -> new heeyoung.soldier.dto.ScoreDto(id, "maroon", x, y, currentHealth.get());
            case Blue -> new heeyoung.soldier.dto.ScoreDto(id, "blue", x, y, currentHealth.get());
        };
    }
}
