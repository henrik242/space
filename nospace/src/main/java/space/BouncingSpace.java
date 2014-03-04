package space;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class BouncingSpace extends Space  {

    static boolean IS_BREAKOUT = true; // Opens bottom, only active if IS_BOUNCING_BALLS is true

    @Override
    void paintPhysicalObject(double mass, double radius, double x, double y, Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        int xtmp = (int) ((x - centrex)  + frame.getSize().width / 2);
        int ytmp = (int) ((y - centrey)  + frame.getSize().height / 2);
        graphics.fillOval(
                (int) (xtmp - radius),
                (int) (ytmp - radius),
                (int) (2 * radius),
                (int) (2 * radius));
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        final BouncingSpace space = new BouncingSpace();
        space.init();
    }

    @Override
    void setupVariables() {
        nrOfObjects = 50;
        setStepSize(1); // One second per iteration
        for (int i = 0; i < nrOfObjects; i++) {
            // radius,weight in [1,20]
            double radiusAndWeight = 1 + 19 * Math.random();
            //x,y in [max radius, width or height - max radius]
            add(radiusAndWeight, 20 + 760 * Math.random(), 20 + 760 * Math.random(), 3 - 6 * Math.random(), 3 - 6 * Math.random(), radiusAndWeight);
        }
        scale = 1;
        centrex = 400;
        centrey = 390; //Must compensate for title bar
    }

    @Override
    void performStep() {
        for (PhysicalObject physicalObject : objects) {
            physicalObject.x = physicalObject.x + physicalObject.vx * seconds;
            physicalObject.y = physicalObject.y + physicalObject.vy * seconds;
        }
    }


    @Override
    void handleCollision(List<PhysicalObject> remove, PhysicalObject one) {
        // Wall collision reverses speed in that direction
        if (one.x - one.radius < 0) {
            one.vx = -one.vx;
        }
        if (one.x + one.radius > 800) {
            one.vx = -one.vx;
        }
        if (one.y - one.radius < 0) {
            one.vy = -one.vy;
        }
        if (one.y + one.radius > 800 && !IS_BREAKOUT) {
            one.vy = -one.vy;
        } else if (one.y - one.radius > 800) {
            remove.add(one);
        }
    }

    @Override
    void collideOrAbsorb(List<PhysicalObject> remove, PhysicalObject one, PhysicalObject other) {
        double distance = Math.sqrt(Math.pow(one.x - other.x, 2) + Math.pow(one.y - other.y, 2));
        double collsionDistance = one.radius + other.radius;
        if (distance < collsionDistance) {
            one.hitBy(other, seconds);
        }
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        //NOOP
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        //NOOP
    }
}
