package main.engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public final class BackBuffer {

    private final Frame frame;
    private BufferedImage buffer;
    private Graphics graphics;
    private Graphics backGraphics;

    public BackBuffer(Frame frame) {
        this.frame = frame;
    }

    public void initDraw(final int screenWidth, final int screenHeight) {
        buffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        graphics = frame.getGraphics();
        backGraphics = buffer.getGraphics();

        backGraphics.setColor(Color.BLACK);
        backGraphics.fillRect(0, 0, screenWidth, screenHeight);
    }

    public void end() {
        graphics.drawImage(buffer, frame.getInsets().left, frame.getInsets().top, frame);
    }

    public Graphics getGraphics() {
        return backGraphics;
    }
}
