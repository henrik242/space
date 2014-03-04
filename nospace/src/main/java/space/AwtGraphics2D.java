package space;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AwtGraphics2D implements GeneralGraphics2D {

    Graphics2D graphics;
    BufferedImage buffer;

    public AwtGraphics2D(int width, int height) {
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics = buffer.createGraphics();
    }

    public void clearRect(int width, int height) {
        graphics.clearRect(0, 0, width, height);
    }

}
