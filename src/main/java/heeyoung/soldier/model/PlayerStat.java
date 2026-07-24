package heeyoung.soldier.model;

/**
 * Immutable PlayerStat snapshot.
 */
public final class PlayerStat {
    private final double maxHealth;
    private final double currentHealth;
    private final long reloadTime; // in ticks

    public PlayerStat() {
        this.maxHealth = 0.0;
        this.currentHealth = 0.0;
        this.reloadTime = 0;
    }

    public PlayerStat(double maxHealth, double currentHealth, long reloadTime) {
        this.maxHealth = maxHealth;
        if (currentHealth < 0) {
            this.currentHealth = 0;
        } else if (currentHealth > maxHealth) {
            this.currentHealth = maxHealth;
        } else {
            this.currentHealth = currentHealth;
        }
        this.reloadTime = reloadTime;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public long getReloadTime() {
        return reloadTime;
    }
}
