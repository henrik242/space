package space;

import java.util.ArrayList;
import java.util.List;

public abstract class Space {

    public static final double EARTH_WEIGHT = 5.9736e24;
    static final double ASTRONOMICAL_UNIT = 149597870.7e3;

    static final double G = 6.67428e-11; // m3/kgs2

    public double seconds = 1;
    List<PhysicalObject> objects = new ArrayList<PhysicalObject>();
    double centrex = 0.0;
    double centrey = 0.0;
    double scale = 10;
    int step = 0;
    int nrOfObjects = 75;
    int frameRate = 25;
    GeneralGraphicsSupport graphicsSupport;

    public Space() {
        this.graphicsSupport = new AwtGraphicsSupport(this);
        setupVariables();
        graphicsSupport.setVisible(true);
        while (true) {
            final long start = System.currentTimeMillis();
            graphicsSupport.doInvokeAndWait();
            try {
                long ahead = 1000 / frameRate - (System.currentTimeMillis() - start);
                if (ahead > 50) {
                    Thread.sleep(ahead);
                    if(frameRate<25) frameRate++;
                } else {
                    Thread.sleep(50);
                    frameRate--;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    abstract void paintPhysicalObject(double mass, double radius, double x, double y, GeneralGraphics2D graphics);

    abstract void setupVariables();

    double randSquare() {
        double random = Math.random();
        return random * random;
    }

    public void setStepSize(double seconds) {
        this.seconds = seconds;
    }

    public PhysicalObject add(double weightKilos, double x, double y,
                                     double vx, double vy, double radius) {
        PhysicalObject physicalObject = new PhysicalObject(weightKilos, x, y,
                vx, vy, radius);
        objects.add(physicalObject);
        return physicalObject;
    }

    public void step() {
        performStep();
        step++;
        graphicsSupport.setObjects(objects);
        graphicsSupport.doPaint();

    }

    abstract void performStep();

    void collide() {
        List<PhysicalObject> remove = new ArrayList<PhysicalObject>();
        for (PhysicalObject one : objects) {
            if (remove.contains(one))
                continue;
            for (PhysicalObject other : objects) {
                if (one == other || remove.contains(other))
                    continue;
                collideOrAbsorb(remove, one, other);
            }
            handleCollision(remove, one);

        }
        objects.removeAll(remove);
    }

    abstract void handleCollision(List<PhysicalObject> remove, PhysicalObject one);

    abstract void collideOrAbsorb(List<PhysicalObject> remove, PhysicalObject one, PhysicalObject other);

}
