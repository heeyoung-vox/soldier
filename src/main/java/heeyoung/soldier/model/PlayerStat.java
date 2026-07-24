package heeyoung.soldier.model;

public class PlayerStat {
    private double maxHealth;
    private double currentHealth;
    private long reloadTime;// in tick

    public double getMaxHealth() {
        return this.maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public double getCurrentHealth() {
        return this.currentHealth;
    }

    public void setCurrentHealth(double currentHealth) {
        if (currentHealth < 0) {
            this.currentHealth = 0;
        } else if (currentHealth > this.maxHealth) {
            this.currentHealth = this.maxHealth;
        } else {
            this.currentHealth = currentHealth;
        }
    }

    public long getReloadTime() {
        return this.reloadTime;
    }

    public void setReloadTime(long reloadTime) {
        this.reloadTime = reloadTime;
    }
}
