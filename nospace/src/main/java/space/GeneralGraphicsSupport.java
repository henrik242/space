package space;

import java.util.List;

public interface GeneralGraphicsSupport {
    void fillOval(GeneralGraphics2D gfx, int x, int y, int diameter);

    void setWhiteColor(GeneralGraphics2D gfx);

    void setVisible(boolean b);

    void doInvokeAndWait();

    void setObjects(List<PhysicalObject> objects);

    void doPaint();

    void setMouseWheelEnabled(boolean b);

    void setMouseDraggedEnabled(boolean b);

    void setColorByMass(GeneralGraphics2D gfx, double mass);

    int getDimensionWidth();

    int getDimensionHeight();

    int getComponentWidth();

}
