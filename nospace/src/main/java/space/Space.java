package space;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public abstract class Space extends JFrame implements MouseWheelListener,
        MouseMotionListener, KeyListener {

    public static final double EARTH_WEIGHT = 5.9736e24;
    static final double ASTRONOMICAL_UNIT = 149597870.7e3;

    static final double G = 6.67428e-11; // m3/kgs2

    public double seconds = 1;
    List<PhysicalObject> objects = new ArrayList<PhysicalObject>();
    double centrex = 0.0;
    double centrey = 0.0;
    double scale = 10;
    private boolean showWake = false;
    private int step = 0;
    int nrOfObjects = 75;
    int frameRate = 25;

    JFrame frame;

    public Space() {
        setBackground(Color.BLACK);
        frame = this;
    }

    @Override
    public void paint(Graphics original) {
        if (original != null) {
            BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = buffer.createGraphics();

            if (!showWake) {
                graphics.clearRect(0, 0, getWidth(), getHeight());
            }
            for (PhysicalObject po : objects) {
                paintPhysicalObject(po.mass, po.radius, po.x, po.y, graphics);
                String string = "Objects:" + objects.size() + " scale:" + scale + " steps:" + step + " frame rate: " + frameRate;
                setTitle(string);
            }
            original.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
        }

    }

    abstract void paintPhysicalObject(double mass, double radius, double x, double y, Graphics2D graphics);

    public static Color weightToColor(double weight) {
        if (weight < 1e10) return Color.GREEN;
        if (weight < 1e12) return Color.CYAN;
        if (weight < 1e14) return Color.MAGENTA;
        if (weight < 1e16) return Color.BLUE;
        if (weight < 1e18) return Color.GRAY;
        if (weight < 1e20) return Color.RED;
        if (weight < 1e22) return Color.ORANGE;
        if (weight < 1e25) return Color.PINK;
        if (weight < 1e28) return Color.YELLOW;
        return Color.WHITE;
    }

    void init() throws InterruptedException, InvocationTargetException {
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setSize(800, 820);

        setupVariables();
        setVisible(true);
        while (true) {
            final long start = System.currentTimeMillis();
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    collide();
                    step();
                }
            });
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
        paint(getGraphics());

    }

    abstract void performStep();

    private void collide() {
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

    public abstract void mouseWheelMoved(final MouseWheelEvent e);

    Point lastDrag = null;

    public abstract void mouseDragged(final MouseEvent e);

    public void mouseMoved(MouseEvent e) {
        lastDrag = null;
    }


    public void keyPressed(KeyEvent e) {
    }


    public void keyReleased(KeyEvent e) {
    }


    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'w')
            showWake = !showWake;
    }

}
