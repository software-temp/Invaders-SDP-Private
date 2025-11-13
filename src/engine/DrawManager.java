package engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import engine.Renderer.EntityRenderer;
import engine.Renderer.HUDRenderer;
import engine.Renderer.ShopRenderer;
import engine.Renderer.UIRenderer;
import screen.Screen;
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
    private double scaleX;
    private double scaleY;
    private int shakeDuration = 0;   // 흔들리는 남은 프레임 수
    private int shakeIntensity = 0;  // 흔들림 강도 (픽셀)

	/** Sprite types mapped to their images. */
	private static Map<SpriteType, boolean[][]> spriteMap;

	/** Sprite types. */
    public static enum SpriteType {
        ShipP1,ShipP2,ShipP1Move,ShipP2Move,ShipP2Explosion1,ShipP2Explosion2, ShipP2Explosion3,Life, ShipP1Explosion1,ShipP1Explosion2,ShipP1Explosion3,
        Bullet, EnemyBullet, EnemyShipA1, EnemyShipA2,
        EnemyShipB1, EnemyShipB2, EnemyShipC1, EnemyShipC2, EnemyShipSpecial, EnemyShipSpecialLeft, EnemySpecialExplosion,
        FinalBoss1, FinalBoss2,FinalBossBullet,FinalBossDeath,OmegaBoss1, OmegaBoss2,OmegaBoss3, OmegaBoss4,OmegaBossMoving1,OmegaBossMoving2,OmegaBossMoving3,OmegaBossMoving4,OmegaBossDeath, Explosion, SoundOn, SoundOff, Item_MultiShot,
        Item_Atkspeed, Item_Penetrate, Item_Explode, Item_Slow, Item_Stop, Shield,
        Item_Push, Item_Shield, Item_Heal,OmegaBoss100,OmegaBoss101
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
		fontPack = null;
	}

	/**
	 * First part of the drawing process.
	 */
	public void initDrawing(final int screenWidth, final int screenHeight) {
        if(backBuffer == null){
            logger.warning("BackBuffer is not initialized! Call setFrame() first.");
            return;
        }
        backBuffer.initDraw(screenWidth, screenHeight);
        if(fontPack == null){
            fontPack = new FontPack(backBuffer.getGraphics(), fileManager);
            entityRenderer = new EntityRenderer(spriteAtlas.getSpriteMap(),backBuffer, this.scaleX, this.scaleY);
            hudRenderer = new HUDRenderer(backBuffer, fontPack, entityRenderer,spriteAtlas.getSpriteMap());
            shopRenderer = new ShopRenderer(backBuffer,fontPack);
            uiRenderer = new UIRenderer(backBuffer,fontPack);
        }
    }

	/**
	 * Draws the completed drawing on screen.
	 */
    public void completeDrawing() {
        if (backBuffer == null) return;

        int offsetX = 0;
        int offsetY = 0;

        if (shakeDuration > 0) {
            offsetX = (int)(Math.random() * shakeIntensity * 2 - shakeIntensity);
            offsetY = (int)(Math.random() * shakeIntensity * 2 - shakeIntensity);
            shakeDuration--;
        }

        Graphics g = frame.getGraphics();
        g.drawImage(backBuffer.getBuffer(),
                frame.getInsets().left + offsetX,
                frame.getInsets().top + offsetY,
                frame);

        g.dispose();
    }
    public void startShake(int duration, int intensity) {
        this.shakeDuration = duration;
        this.shakeIntensity = intensity;
    }

	/**
	 * Draws the starfield background.
	 *
	 * @param stars
	 *            List of stars to draw.
	 * @param angle
	 *            Current rotation angle.
	 */
	public void drawStars(final int screenWidth, final int screenHeight, final List<Star> stars, final float angle) {
        Graphics g = backBuffer.getGraphics();
		final int centerX = screenWidth / 2;
		final int centerY = screenHeight / 2;
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
    public void setScale(double scaleX, double scaleY){
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public void drawShootingStars(final List<ShootingStar> shootingStars, final float angle) {    }

    public ShopRenderer getShopRenderer() {
        return shopRenderer;
    }

    public EntityRenderer getEntityRenderer() {
        return entityRenderer;
    }

    public HUDRenderer getHUDRenderer() { return hudRenderer; }

    public UIRenderer getUIRenderer() { return uiRenderer; }

}