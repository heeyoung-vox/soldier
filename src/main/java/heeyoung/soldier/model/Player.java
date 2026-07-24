package heeyoung.soldier.model;

public class Player {
    private String id;
    private String name;
    private double x, y;

    private long lastShotTime = -1;

    private PlayerInput playerInput = new PlayerInput();
    private PlayerStat stat = new PlayerStat();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public PlayerInput getPlayerInput() {
        return playerInput;
    }

    public PlayerStat getPlayerStat() {
        return stat;
    }

    public long getLastShotTime() {
        return this.lastShotTime;
    }

    public void setLastShotTime(long lastShotTime) {
        this.lastShotTime = lastShotTime;
    }

}
