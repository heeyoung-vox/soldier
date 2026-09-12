package heeyoung.soldier.service.Collision;

import java.util.List;
import heeyoung.soldier.model.Position;

public interface Collidable {
    double getAngle();

    Position getPosition();

    List<BoundingCircle> getBoundingCircles();

    Type getCollidableType();
}