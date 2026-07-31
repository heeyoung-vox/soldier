package heeyoung.soldier.model;

/**
 * Immutable PlayerInput snapshot.
 */
public final class PlayerInput {
    private final double dx;
    private final double dy;
    private final double angle;
    private final boolean isShooting;

    public PlayerInput() {
        this.dx = 0.0;
        this.dy = 0.0;
        this.angle = 0.0;
        this.isShooting = false;
    }

    public PlayerInput(double dx, double dy, double angle, boolean isShooting) {
        this.dx = dx;
        this.dy = dy;
        this.angle = angle;
        this.isShooting = isShooting;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public double getAngle() {
        return angle;
    }

    public boolean isShooting() {
        return isShooting;
    }
}
