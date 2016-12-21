import java.awt.*;

/**
 * Created by hp on 21.12.2016.
 */
public class MyPoints extends Point {
    double x;
    double y;
    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    public MyPoints(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public MyPoints() {
        super();
    }
}
