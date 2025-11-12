package engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import entity.Entity;
import entity.FinalBoss;
import entity.Ship;
import engine.Achievement;
import screen.CreditScreen;
import screen.Screen;
import engine.Score;
import screen.TitleScreen;
import screen.TitleScreen.Star;
import screen.TitleScreen.ShootingStar;

/**
 * Manages screen drawing.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class DrawManager {

	/** Singleton instance of the class. */
	private static DrawManager instance;
	/** Current frame. */
	private static Frame frame;
	/** FileManager instance. */
	private static FileManager fileManager;
	/** Application logger. */
	private static final Logger logger = Core.getLogger();

    private SpriteAtlas spriteAtlas;
    private FontPack fontPack;
    private BackBuffer backBuffer;

    private EntityRenderer entityRenderer;
    private HUDRenderer hudRenderer;
    private ShopRenderer shopRenderer;
    private UIRenderer uiRenderer;

	/** Sprite types mapped to their images. */
	private static Map<SpriteType, boolean[][]> spriteMap;

	/** Sprite types. */
	public static enum SpriteType {
		Ship, ShipDestroyed, Bullet, EnemyBullet, EnemyShipA1, EnemyShipA2,
		EnemyShipB1, EnemyShipB2, EnemyShipC1, EnemyShipC2, EnemyShipSpecial,
		FinalBoss1, FinalBoss2,FinalBossBullet,FinalBossDeath,OmegaBoss1, OmegaBoss2,OmegaBossDeath, Explosion, SoundOn, SoundOff, Item_MultiShot,
		Item_Atkspeed, Item_Penetrate, Item_Explode, Item_Slow, Item_Stop,
		Item_Push, Item_Shield, Item_Heal
	}

	/**
	 * Private constructor.
	 */
	private DrawManager() {
        fileManager = Core.getFileManager();
        logger.info("Started initializing DrawManager...");
        spriteAtlas = new SpriteAtlas(fileManager);
        logger.info("Sprite atlas loaded!");
        logger.info("DrawManager initialized successfully");
    }

	/**
	 * Returns shared instance of DrawManager.
	 */
	public static DrawManager getInstance() {
		if (instance == null)
			instance = new DrawManager();
		return instance;
	}

	/**
	 * Sets the frame to draw the image on.
	 */
	public void setFrame(final Frame currentFrame) {
		frame = currentFrame;
        backBuffer = new BackBuffer(frame);
	}

	/**
	 * First part of the drawing process.
	 */
	public void initDrawing(final Screen screen) {
        if(backBuffer == null){
            logger.warning("BackBuffer is not initialized! Call setFrame() first.");
            return;
        }
        backBuffer.initDraw(screen);
        if(fontPack == null){
            fontPack = new FontPack(backBuffer.getGraphics(), fileManager);
            entityRenderer = new EntityRenderer(spriteAtlas.getSpriteMap(),backBuffer);
            hudRenderer = new HUDRenderer(backBuffer, fontPack, entityRenderer);
            shopRenderer = new ShopRenderer(backBuffer,fontPack);
            uiRenderer = new UIRenderer(backBuffer,fontPack);
        }
    }

	/**
	 * Draws the completed drawing on screen.
	 */
	public void completeDrawing(final Screen screen) {
        if(backBuffer == null){
            logger.warning("BackBuffer is not initialized!");
            return;
        }
        backBuffer.end(screen);
	}
	/**
	 * Draws the starfield background.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param stars
	 *            List of stars to draw.
	 * @param angle
	 *            Current rotation angle.
	 */
	public void drawStars(final Screen screen, final List<Star> stars, final float angle) {
        Graphics g = backBuffer.getGraphics();
		final int centerX = screen.getWidth() / 2;
		final int centerY = screen.getHeight() / 2;
		final double angleRad = Math.toRadians(angle);
		final double cosAngle = Math.cos(angleRad);
		final double sinAngle = Math.sin(angleRad);

		for (Star star : stars) {
			float relX = star.baseX - centerX;
			float relY = star.baseY - centerY;

			double rotatedX = relX * cosAngle - relY * sinAngle;
			double rotatedY = relX * sinAngle + relY * cosAngle;

			int screenX = (int) (rotatedX + centerX);
			int screenY = (int) (rotatedY + centerY);

			// Use star's brightness to set its color for twinkling effect
			float b = star.brightness;
			if (b < 0) b = 0;
			if (b > 1) b = 1;
			g.setColor(new Color(b, b, b));
			g.drawRect(screenX, screenY, 1, 1);
		}
	}

    public void drawShootingStars(final Screen screen, final List<ShootingStar> shootingStars, final float angle) {    }

    public ShopRenderer getShopRenderer() {
        return shopRenderer;
    }

    public EntityRenderer getEntityRenderer() {
        return entityRenderer;
    }

    public HUDRenderer getHUDRenderer() {return  hudRenderer;}

    public UIRenderer  getUIRenderer() {return uiRenderer;}

}