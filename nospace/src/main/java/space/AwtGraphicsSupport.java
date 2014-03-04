package space;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.*;

public class AwtGraphicsSupport extends JFrame implements MouseWheelListener,
        MouseMotionListener, KeyListener, GeneralGraphicsSupport {

    Point lastDrag = null;
    boolean showWake = false;
    JFrame frame;
    Space space;
    private List<PhysicalObject> objects;
    private boolean mouseWheelEnabled = true;
    private boolean mouseDraggedEnabled = true;

    public AwtGraphicsSupport(Space space) {
        this.space = space;
        frame = this;
        setBackground(Color.BLACK);
        addMouseWheelListener((MouseWheelListener) this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setSize(800, 820);
    }

    @Override
    public void doInvokeAndWait() {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    space.collide();
                    space.step();
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    public Color weightToColor(double weight) {
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

    @Override
    public void mouseMoved(MouseEvent e) {
        lastDrag = null;
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }


    @Override
    public void keyReleased(KeyEvent e) {
    }


    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'w')
            showWake = !showWake;
    }

    @Override
    public void doPaint() {
        paint(getGraphics());
    }

    @Override
    public void paint(Graphics original) {
        if (original != null) {

            AwtGraphics2D gfx = new AwtGraphics2D(getWidth(), getHeight());

            if (!showWake) {
                gfx.clearRect(getWidth(), getHeight());
            }
            if (objects != null) {
                for (PhysicalObject po : objects) {
                    space.paintPhysicalObject(po.mass, po.radius, po.x, po.y, gfx);
                    String string = "Objects:" + objects.size() + " scale:" + space.scale + " steps:" + space.step + " frame rate: " + space.frameRate;
                    setTitle(string);
                }
            }
            original.drawImage(gfx.buffer, 0, 0, getWidth(), getHeight(), null);
        }

    }

    @Override
    public void setObjects(List<PhysicalObject> objects) {
        this.objects = objects;
    }

    @Override
    public void setColorByMass(GeneralGraphics2D gfx, double mass) {
        ((AwtGraphics2D) gfx).graphics.setColor(weightToColor(mass));
    }

    @Override
    public int getDimensionWidth() {
        return getSize().width;
    }

    @Override
    public int getDimensionHeight() {
        return getSize().height;
    }

    @Override
    public int getComponentWidth() {
        return getWidth();
    }

    @Override
    public void fillOval(GeneralGraphics2D gfx, int x, int y, int diameter) {
        ((AwtGraphics2D) gfx).graphics.fillOval(
                x,
                y,
                diameter,
                diameter);
    }

    @Override
    public void setWhiteColor(GeneralGraphics2D gfx) {
        ((AwtGraphics2D) gfx).graphics.setColor(Color.WHITE);
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        if (mouseWheelEnabled) {
            space.scale = space.scale + space.scale * (Math.min(9, e.getWheelRotation())) / 10 + 0.0001;
            getGraphics().clearRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (mouseDraggedEnabled) {
            if (lastDrag == null) {
                lastDrag = e.getPoint();
            }
            space.centrex = space.centrex - ((e.getX() - lastDrag.x) * space.scale);
            space.centrey = space.centrey - ((e.getY() - lastDrag.y) * space.scale);
            lastDrag = e.getPoint();
            getGraphics().clearRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public void setMouseWheelEnabled(boolean mouseWheelEnabled) {
        this.mouseWheelEnabled = mouseWheelEnabled;
    }

    @Override
    public void setMouseDraggedEnabled(boolean mouseDraggedEnabled) {
        this.mouseDraggedEnabled = mouseDraggedEnabled;
    }
}
