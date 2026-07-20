package heeyoung.soldier.model;

public class PlayerInput {
    private double dx, dy, angle;

    public PlayerInput() {
        dx = 0f;
        dy = 0f;
        angle = 0f;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

}
