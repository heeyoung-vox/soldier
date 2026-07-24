package heeyoung.soldier.model;

public class Score {
    private final String id;
    private final ScoreType type;
    private double x;
    private double y;
    private int currentHealth;

    public Score(String id, ScoreType type, double x, double y) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.currentHealth = type.maxHealth;
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

    public ScoreType getType() {
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

}
