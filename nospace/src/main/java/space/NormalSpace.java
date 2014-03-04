package space;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class NormalSpace extends Space {

    @Override
    void paintPhysicalObject(double mass, double radius, double x, double y, Graphics2D graphics) {
        graphics.setColor(weightToColor(mass));
        int diameter = mass >= EARTH_WEIGHT * 10000 ? 7 : 2;
        int xtmp = (int) ((x - centrex) / scale + frame.getSize().width / 2);
        int ytmp = (int) ((y - centrey) / scale + frame.getSize().height / 2);
        graphics.fillOval(
                xtmp-diameter/2,
                ytmp-diameter/2,
                diameter,
                diameter);
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        final NormalSpace space = new NormalSpace();
        space.init();
    }

    @Override
    void setupVariables() {
        setStepSize(3600 * 24 * 7);

        double outerLimit = ASTRONOMICAL_UNIT * 20;

        for (int i = 0; i < nrOfObjects; i++) {
            double angle = randSquare() * 2 * Math.PI;
            double radius = (0.1 + 0.9 * Math.sqrt(randSquare())) * outerLimit;
            double weightKilos = 1e3 * EARTH_WEIGHT * (Math.pow(0.00001 + 0.99999 * randSquare(), 12));
            double x = radius * Math.sin(angle);
            double y = radius * Math.cos(angle);
            double speedRandom = Math.sqrt(1 / radius) * 2978000*1500 * (0.4 + 0.6 * randSquare());

            double vx = speedRandom * Math.sin(angle - Math.PI / 2);
            double vy = speedRandom * Math.cos(angle - Math.PI / 2);
            add(weightKilos, x, y, vx, vy, 1);
        }

        scale = outerLimit / getWidth();

        add(EARTH_WEIGHT * 20000, 0, 0, 0, 0, 1);
    }

    @Override
    void performStep() {
        for (PhysicalObject aff : objects) {
            double fx = 0;
            double fy = 0;
            for (PhysicalObject oth : objects) {
                if (aff == oth)
                    continue;
                double[] d = new double[]{aff.x - oth.x, aff.y - oth.y};
                double r2 = Math.pow(d[0], 2) + Math.pow(d[1], 2);
                double f = G * aff.mass * oth.mass / r2;
                double sqrtOfR2 = Math.sqrt(r2);
                fx += f * d[0] / sqrtOfR2;
                fy += f * d[1] / sqrtOfR2;
            }
            double ax = fx / aff.mass;
            double ay = fy / aff.mass;
            aff.x = aff.x - ax * Math.pow(seconds, 2) / 2 + aff.vx * seconds;
            aff.y = aff.y - ay * Math.pow(seconds, 2) / 2 + aff.vy * seconds;
            aff.vx = aff.vx - ax * seconds;
            aff.vy = aff.vy - ay * seconds;
        }
    }

    @Override
    void handleCollision(List<PhysicalObject> remove, PhysicalObject one) {
        //NOOP
    }

    @Override
    void collideOrAbsorb(List<PhysicalObject> remove, PhysicalObject one, PhysicalObject other) {
        if (Math.sqrt(Math.pow(one.x - other.x, 2) + Math.pow(one.y - other.y, 2)) < 5e9) {
            one.absorb(other);
            remove.add(other);
        }
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        scale = scale + scale * (Math.min(9, e.getWheelRotation())) / 10 + 0.0001;
        getGraphics().clearRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (lastDrag == null) {
            lastDrag = e.getPoint();
        }
        centrex = centrex - ((e.getX() - lastDrag.x) * scale);
        centrey = centrey - ((e.getY() - lastDrag.y) * scale);
        lastDrag = e.getPoint();
        getGraphics().clearRect(0, 0, getWidth(), getHeight());
    }
}
