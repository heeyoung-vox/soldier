package heeyoung.soldier.service.Collision;

import java.util.List;
import heeyoung.soldier.model.Position;
import heeyoung.soldier.service.Collision.Type;

public interface Collidable {
    double getAngle();

    Position getPosition();

    List<BoundingCircle> getBoundingCircles();

    Type getType();
}