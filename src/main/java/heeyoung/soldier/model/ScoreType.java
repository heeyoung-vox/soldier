package heeyoung.soldier.model;

public enum ScoreType {
    Yellow(10, 10, 15),
    Maroon(20, 20, 22.5),
    Blue(30, 30, 45);

    public final double maxHealth;
    public final double scoreValue;
    public final double radius;

    ScoreType(int maxHealth, int scoreValue, double radius) {
        this.maxHealth = maxHealth;
        this.scoreValue = scoreValue;
        this.radius = radius;
    }
}