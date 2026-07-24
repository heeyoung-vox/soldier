package heeyoung.soldier.helper;

public class Utility {
    public static double[] angleToVector(double radians) {
        double x = Math.cos(radians);
        double y = Math.sin(radians);
        return new double[] { x, y };
    }
}
