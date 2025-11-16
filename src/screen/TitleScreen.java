package screen;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;
import entity.Entity;
import entity.SoundButton;

import audio.SoundManager;


/**
 * Implements the title screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class TitleScreen extends Screen {

	/**
	 * A simple class to represent a star for the animated background.
	 * Stores the non-rotating base coordinates and speed.
	 */
	public static class Star {
		public float baseX;
		public float baseY;
		public float speed;
		public float brightness;
        public float brightnessOffset;

		public Star(float baseX, float baseY, float speed) {
			this.baseX = baseX;
			this.baseY = baseY;
			this.speed = speed;
			this.brightness = 0;
			this.brightnessOffset = (float) (Math.random() * Math.PI * 2);
		}
	}

	/**
	 * A simple class to represent a shooting star.
	 */
	public static class ShootingStar {
		public float x;
		public float y;
		public float speedX;
		public float speedY;

		public ShootingStar(float x, float y, float speedX, float speedY) {
			this.x = x;
			this.y = y;
			this.speedX = speedX;
			this.speedY = speedY;
		}
	}

	/**
	 * A simple class to represent a background enemy.
	 */
	private static class BackgroundEnemy extends Entity {
		private int speed;

		public BackgroundEnemy(int positionX, int positionY, int speed, SpriteType spriteType) {
			super(positionX, positionY, 12 * 2, 8 * 2, Color.WHITE);
			this.speed = speed;
			this.spriteType = spriteType;
		}

		public int getSpeed() {
			return speed;
		}
	}

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;
	/** Number of stars in the background. */
	private static final int NUM_STARS = 150;
	/** Speed of the rotation animation. */
    private static final float ROTATION_SPEED = 4.0f;
	/** Milliseconds between enemy spawns. */
	private static final int ENEMY_SPAWN_COOLDOWN = 2000;
	/** Probability of an enemy spawning. */
	private static final double ENEMY_SPAWN_CHANCE = 0.3;
	/** Milliseconds between shooting star spawns. */
    private static final int SHOOTING_STAR_COOLDOWN = 3000;
    /** Probability of a shooting star spawning. */
    private static final double SHOOTING_STAR_SPAWN_CHANCE = 0.2;

	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;
	/** Cooldown for enemy spawning. */
	private Cooldown enemySpawnCooldown;
	/** Cooldown for shooting star spawning. */
    private Cooldown shootingStarCooldown;

	/** List of stars for the background animation. */
	private List<Star> stars;
	/** List of background enemies. */
	private List<Entity> backgroundEnemies;
	/** List of shooting stars. */
    private List<ShootingStar> shootingStars;

	/** Sound button on/off object. */
	private SoundButton soundButton;

    private boolean musicStarted = false;

	/** Current rotation angle of the starfield. */
    private float currentAngle;
    /** Target rotation angle of the starfield. */
    private float targetAngle;

	/** Random number generator. */
    private Random random;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public TitleScreen(final int width, final int height, final int fps) {
		super(width, height, fps);

		// Defaults to play.
		this.returnCode = 2;
		this.soundButton = new SoundButton(0, 0);
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.enemySpawnCooldown = Core.getCooldown(ENEMY_SPAWN_COOLDOWN);
		this.shootingStarCooldown = Core.getCooldown(SHOOTING_STAR_COOLDOWN);
		this.selectionCooldown.reset();
		this.enemySpawnCooldown.reset();
		this.shootingStarCooldown.reset();

		this.random = new Random();
		this.stars = new ArrayList<Star>();
		for (int i = 0; i < NUM_STARS; i++) {
			float speed = (float) (Math.random() * 2.5 + 0.5);
			this.stars.add(new Star((float) (Math.random() * width),
					(float) (Math.random() * height), speed));
		}

		this.backgroundEnemies = new ArrayList<Entity>();
		this.shootingStars = new ArrayList<ShootingStar>();

		// Initialize rotation angles
		this.currentAngle = 0;
		this.targetAngle = 0;
	}


	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		// Smoothly animate the rotation angle
        if (currentAngle < targetAngle) {
            currentAngle = Math.min(currentAngle + ROTATION_SPEED, targetAngle);
        } else if (currentAngle > targetAngle) {
            currentAngle = Math.max(currentAngle - ROTATION_SPEED, targetAngle);
        }

		// Animate stars in their non-rotating space
		for (Star star : this.stars) {
			star.baseY += star.speed;
			if (star.baseY > this.getHeight()) {
				star.baseY = 0;
				star.baseX = (float) (Math.random() * this.getWidth());
			}
			// Update brightness for twinkling effect
			star.brightness = 0.5f + (float) (Math.sin(star.brightnessOffset + System.currentTimeMillis() / 500.0) + 1.0) / 4.0f;
		}

		// Spawn and move background enemies
		if (this.enemySpawnCooldown.checkFinished()) {
			this.enemySpawnCooldown.reset();
			if (Math.random() < ENEMY_SPAWN_CHANCE) {
				SpriteType[] enemyTypes = { SpriteType.EnemyShipA1, SpriteType.EnemyShipB1, SpriteType.EnemyShipC1 };
				SpriteType randomEnemyType = enemyTypes[random.nextInt(enemyTypes.length)];
				int randomX = (int) (Math.random() * this.getWidth());
				int speed = random.nextInt(2) + 1;
				this.backgroundEnemies.add(new BackgroundEnemy(randomX, -20, speed, randomEnemyType));
			}
		}

		java.util.Iterator<Entity> enemyIterator = this.backgroundEnemies.iterator();
		while (enemyIterator.hasNext()) {
			BackgroundEnemy enemy = (BackgroundEnemy) enemyIterator.next();
			enemy.setPositionY(enemy.getPositionY() + enemy.getSpeed());
			if (enemy.getPositionY() > this.getHeight()) {
				enemyIterator.remove();
			}
		}

		// Spawn and move shooting stars
        if (this.shootingStarCooldown.checkFinished()) {
            this.shootingStarCooldown.reset();
            if (Math.random() < SHOOTING_STAR_SPAWN_CHANCE) {
                float speedX = (float) (Math.random() * 10 + 5) * (Math.random() > 0.5 ? 1 : -1);
                float speedY = (float) (Math.random() * 10 + 5) * (Math.random() > 0.5 ? 1 : -1);
                this.shootingStars.add(new ShootingStar(random.nextInt(this.getWidth()), -10, speedX, speedY));
            }
        }

		java.util.Iterator<ShootingStar> shootingStarIterator = this.shootingStars.iterator();
        while (shootingStarIterator.hasNext()) {
            ShootingStar shootingStar = shootingStarIterator.next();
            shootingStar.x += shootingStar.speedX;
            shootingStar.y += shootingStar.speedY;
            if (shootingStar.x < -20 || shootingStar.x > this.getWidth() + 20 ||
                shootingStar.y < -20 || shootingStar.y > this.getHeight() + 20) {
                shootingStarIterator.remove();
            }
        }

		// Handle sound button color
		if (this.returnCode == 5) {
            float pulse = (float) ((Math.sin(System.currentTimeMillis() / 200.0) + 1.0) / 2.0);
            Color pulseColor = new Color(0, 0.5f + pulse * 0.5f, 0);
            this.soundButton.setColor(pulseColor);
        } else {
            this.soundButton.setColor(Color.WHITE);
        }

		draw();
		if (this.selectionCooldown.checkFinished()
				&& this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_UP)
					|| inputManager.isKeyDown(KeyEvent.VK_W)) {
				previousMenuItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
					|| inputManager.isKeyDown(KeyEvent.VK_S)) {
				nextMenuItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_SPACE)){
				if (this.returnCode != 5) {
					this.isRunning = false;
				} else {
					this.soundButton.changeSoundState();

					if (SoundButton.getIsSoundOn()) {
						SoundManager.uncutBGM();
					} else {
						SoundManager.cutBGM();
					}

					if (this.soundButton.isTeamCreditScreenPossible()) {
						this.returnCode = 8;
						this.isRunning = false;
					} else {
						this.selectionCooldown.reset();
					}
				}
			}
			if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)
					|| inputManager.isKeyDown(KeyEvent.VK_D)) {
				this.returnCode = 5;
				this.targetAngle += 90;
				this.selectionCooldown.reset();
			}
			if (this.returnCode == 5 && inputManager.isKeyDown(KeyEvent.VK_LEFT)
					|| inputManager.isKeyDown(KeyEvent.VK_A)) {
				this.returnCode = 4;
				this.targetAngle -= 90;
				this.selectionCooldown.reset();
			}
		}
	}

	/**
	 * Shifts the focus to the next menu item.
	 */
	private void nextMenuItem() {
        switch (this.returnCode) {
            case 0 -> this.returnCode = 2; // Exit → Play
            case 2 -> this.returnCode = 3; // Play → High Scores
            case 3 -> this.returnCode = 4; // High Scores → Shop
            case 4 -> this.returnCode = 6; // Shop → Achievements
            case 6 -> this.returnCode = 0; // Achievements → Exit
            case 5 -> this.returnCode = 0; // Sound button → Exit
            default -> this.returnCode = 0;
        }
		this.targetAngle += 90;
	}

	/**
	 * Shifts the focus to the previous menu item.
	 */
	private void previousMenuItem() {
        switch (this.returnCode) {
            case 0 -> this.returnCode = 6; // Exit → Achievements
            case 6 -> this.returnCode = 4; // Achievements → Shop
            case 4 -> this.returnCode = 3; // Shop → High Scores
            case 3 -> this.returnCode = 2; // High Scores → Play
            case 2 -> this.returnCode = 0; // Play → Exit
            case 5 -> this.returnCode = 6; // Sound button → Achievements
            default -> this.returnCode = 0;
        }
		this.targetAngle -= 90;
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this.width, this.height);

		// Draw stars with rotation
		drawManager.drawStars(this.width,this.height, this.stars, this.currentAngle);

		// Draw shooting stars with rotation
        drawManager.drawShootingStars( this.shootingStars, this.currentAngle);

		// Draw background enemies with rotation
		final double angleRad = Math.toRadians(this.currentAngle);
        final double cosAngle = Math.cos(angleRad);
        final double sinAngle = Math.sin(angleRad);
        final int centerX = this.getWidth() / 2;
        final int centerY = this.getHeight() / 2;

		for (Entity enemy : this.backgroundEnemies) {
			float relX = enemy.getPositionX() - centerX;
            float relY = enemy.getPositionY() - centerY;

            double rotatedX = relX * cosAngle - relY * sinAngle;
            double rotatedY = relX * sinAngle + relY * cosAngle;

            int screenX = (int) (rotatedX + centerX);
            int screenY = (int) (rotatedY + centerY);

			drawManager.getEntityRenderer().drawEntity(enemy, screenX, screenY);
		}

		drawManager.getUIRenderer().drawTitle(this.width,this.height);
		drawManager.getUIRenderer().drawMenu(this.width,this.height, this.returnCode);
		drawManager.getEntityRenderer().drawEntity(this.soundButton, this.width * 4 / 5 - 16,
				this.height * 4 / 5 - 16);

		drawManager.completeDrawing();
	}

	/**
	 * Getter for the sound state.
	 * @return isSoundOn of the sound button.
	 */
	public boolean getIsSoundOn() {
		return SoundButton.getIsSoundOn();
	}
}
