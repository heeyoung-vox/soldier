package heeyoung.soldier.model;

/**
 * Immutable PlayerInput snapshot.
 */
public final class PlayerInput {
    private final double dx;
    private final double dy;
    private final double angle;

    public PlayerInput() {
        this.dx = 0.0;
        this.dy = 0.0;
        this.angle = 0.0;
    }

    public PlayerInput(double dx, double dy, double angle) {
        this.dx = dx;
        this.dy = dy;
        this.angle = angle;
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
}
