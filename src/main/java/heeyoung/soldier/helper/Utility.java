package heeyoung.soldier.helper;

public class Utility {
    public static double[] angleToVector(double radians) {
        double x = Math.cos(radians);
        double y = Math.sin(radians);
        return new double[] { x, y };
    }

    public static double[] rotate(double x, double y, double xc, double yc, double rad) {
        // Translate point to origin relative to pivot
        double xRel = x - xc;
        double yRel = y - yc;

        // Apply rotation matrix
        double xRot = xRel * Math.cos(rad) - yRel * Math.sin(rad);
        double yRot = xRel * Math.sin(rad) + yRel * Math.cos(rad);

        // Translate back to pivot
        double xNew = xRot + xc;
        double yNew = yRot + yc;

        return new double[] { xNew, yNew };
    }
}
