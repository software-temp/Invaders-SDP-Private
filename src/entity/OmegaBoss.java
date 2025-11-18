package entity;

import engine.DrawManager;

import java.awt.*;

/**
 * Omega - Middle Boss
 */
public class OmegaBoss extends MidBoss {

	/** Initial position in the x-axis. */
	private static final int INIT_POS_X = 224;
	/** Initial position in the y-axis. */
	private static final int INIT_POS_Y = 80;
	/** Width of Omega */
	private static final int OMEGA_WIDTH = 64;
	/** Height of Omega */
	private static final int OMEGA_HEIGHT = 28;
	/** Current Health of Omega */
	private static final int OMEGA_HEALTH = 45;
	/** Point of Omega when destroyed */
	private static final int OMEGA_POINT_VALUE = 500;
	/** Speed of x in pattern 1 */
	private static final int PATTERN_1_X_SPEED = 1;
	/** Speed of x in pattern 2 */
	private static final int PATTERN_2_X_SPEED = 4;
	/** Speed of y in pattern 2 */
	private static final int PATTERN_2_Y_SPEED = 3;
	/** Color of pattern 2 */
	private static final Color PATTERN_2_COLOR = Color.MAGENTA;
	/** Current horizontal movement direction. true for right, false for left. */
	private boolean isRight = true;
	/** Current vertical movement direction. true for down, false for up. */
	private boolean isDown = true;
	/** Boss cannot move over this boundary. */
	private final int widthBoundary;
	/** Boss cannot move below this boundary. */
	private final int bottomBoundary;
	/**
	 * Check if HP is below 50%
	 */
	private boolean midSkillTriggered = false;
	/**
	 * Whether the mid-skill is active
	 */
	private boolean midSkillActive = false;
	/**
	 * Whether the boss is moving to the center
	 */
	private boolean movingToCenter = false;
	/**
	 * Whether one sweep cycle is finished
	 */
	private boolean sweepFinished = false;

	private GameModel game;
	/**
	 * Target X position for sweep (from left → right)
	 */
	private int currentTargetX = 0;
	/**
	 * Sweep horizontal step size (how dense the sweep is)
	 */
	private static final int SWEEP_STEP = 12;

	/**
	 * Constructor, establishes the boss entity's generic properties.
	 *
 	 * @param color             Color of the boss entity.
 	 * @param widthBoundary		The rightmost X-coordinate for the boss's movement. The boss cannot move over this value.
 	 * @param bottomBoundary    The lowermost Y-coordinate for the boss's movement. The boss cannot move below this value.
	 */
	public OmegaBoss(Color color, int widthBoundary, int bottomBoundary, GameModel model) {
		super(INIT_POS_X, INIT_POS_Y, OMEGA_WIDTH, OMEGA_HEIGHT, OMEGA_HEALTH, OMEGA_POINT_VALUE, color);
		this.widthBoundary = widthBoundary;
		this.bottomBoundary = bottomBoundary;
		this.game = model;
		this.spriteType = DrawManager.SpriteType.OmegaBoss1;
		this.logger.info("OMEGA : Initializing Boss OMEGA");
		this.logger.info("OMEGA : move using the default pattern");
	}
	// ✅ 호환용 생성자 (GameModel 없는 기존 코드/테스트용)
	public OmegaBoss(Color color, int widthBoundary, int bottomBoundary) {
		this(color, widthBoundary, bottomBoundary, null);
		// GameModel이 없으면 스윕 패턴은 자동으로 스킵되도록 (performSweep에서 null 체크됨)
	}

	/** move simple */
	@Override
	public void move(int distanceX, int distanceY) {
		this.positionX += distanceX;
		this.positionY += distanceY;
	}

	/**
	 * Handles the boss's mid-skill behavior:
	 * - Move to center
	 * - Perform one full sweep attack
	 * - Switch to Pattern 2 when done
	 *
	 * @see #moveToCenter()
	 * @see #performSweep()
	 */
	private void runMidSkill() {

		if (movingToCenter) {
			moveToCenter();
			return;
		}

		if (!sweepFinished) {
			performSweep();
			return;
		}

		midSkillActive = false;
		this.pattern = 2;
		this.color = PATTERN_2_COLOR;
		this.spriteType = DrawManager.SpriteType.OmegaBoss2;
		logger.info("OMEGA : Mid skill finished. Switching to Pattern 2.");
	}

	/**
	 * Moves the boss horizontally toward the center.
	 * When centered, resets values and begins the sweep attack.
	 */
	private void moveToCenter() {
		int targetX = (widthBoundary - this.width) / 2;

		if (this.positionX < targetX) this.positionX++;
		else if (this.positionX > targetX) this.positionX--;

		if (Math.abs(this.positionX - targetX) <= 1) {
			movingToCenter = false;

			currentTargetX = 0;
			sweepFinished = false;

			logger.info("OMEGA : Arrived center. Starting sweep.");
		}
	}

	/**
	 * Performs the mid-skill sweep attack.
	 * The boss fires bullet while smoothly aiming from left to right.
	 */
	private void performSweep() {

		Ship ship = game.getShip();
		if (ship == null) {
			sweepFinished = true;
			logger.warning("OMEGA : No ship found. Aborting sweep.");
			return;
		}

		int safeSpace = ship.getWidth();
		int rightLimit = widthBoundary - safeSpace;

		if (currentTargetX >= rightLimit) {
			sweepFinished = true;
			logger.info("OMEGA : Sweep completed.");
			return;
		}

		int gunX = this.positionX + this.width / 2;
		int gunY = this.positionY + this.height;

		int targetX = currentTargetX;
		int targetY = ship.getPositionY();

		double dirX = targetX - gunX;
		double dirY = targetY - gunY;

		double length = Math.sqrt(dirX * dirX + dirY * dirY);
		if (length == 0) {
			length = 1;
		}

		double speed = 5.0;

		int vx = (int) Math.round(dirX / length * speed);
		int vy = (int) Math.round(dirY / length * speed);

		if (vx == 0 && vy == 0) {
			vy = 1;
		}

		BossBullet bullet = new BossBullet(
				gunX,
				gunY,
				vx,
				vy,
				6,
				12,
				Color.ORANGE
		);

		game.getBossBullets().add(bullet);

		currentTargetX += SWEEP_STEP;
	}

	/**
	 * Selects which movement pattern to use.
	 * <p>
	 * If HP < 50% and mid-skill is not active,
	 * force switch to pattern 2 (fallback).
	 * <p>
	 * pattern == 1 → patternFirst()
	 * pattern == 2 → patternSecond()
	 */
	private void movePatterns(){
		if(this.pattern!=2 && this.healPoint < this.maxHp/2){
			this.pattern=2;
			this.color=PATTERN_2_COLOR;
			this.spriteType = DrawManager.SpriteType.OmegaBoss2;
			logger.info("OMEGA : move using second pattern");
		}

		if (pattern == 1) patternFirst();
		else patternSecond();
	}

	/**
	 * The boss's phase first pattern, which makes it move from side to side across the screen.
	 * @see #move(int, int)
	 */
	private void patternFirst(){
		int dx = this.isRight ? PATTERN_1_X_SPEED : -PATTERN_1_X_SPEED;
		this.move(dx, 0);

		if (this.positionX <= 0) {
			this.isRight = true;
		} else if (this.positionX + this.width >= widthBoundary) {
			this.isRight = false;
		}
	}

	/**
	 * The boss's phase Second pattern, which combines horizontal and vertical movement
	 * Horizontally, it patrols from side to side at a faster speed than in {@link #patternFirst()}.
	 * @see #move(int, int)
	 */
	private void patternSecond(){
		int dx = this.isRight ? PATTERN_2_X_SPEED : -PATTERN_2_X_SPEED;
		int dy = this.isDown ? PATTERN_2_Y_SPEED : -PATTERN_2_Y_SPEED;

		this.move(dx, dy);

		if (this.positionX <= 0) {
			this.positionX = 0;
			this.isRight = true;
		} else if (this.positionX + this.width >= widthBoundary) {
			this.positionX = widthBoundary - this.width;
			this.isRight = false;
		}

		if (this.positionY <= INIT_POS_Y) {
			this.positionY = INIT_POS_Y;
			this.isDown = true;
		} else if (this.positionY + this.height >= bottomBoundary) {
			this.positionY = bottomBoundary - this.height;
			this.isDown = false;
		}
	}

	/** Marks the entity as destroyed and changes its sprite to an explosion. */
	@Override
	public void destroy() {
		this.isDestroyed = true;
		this.spriteType = DrawManager.SpriteType.OmegaBossDeath;
		this.logger.info("OMEGA : Boss OMEGA destroyed!");
	}

	/**
	 * Reduces health and destroys the entity if it drops to zero or below.
	 *
	 * @param damage The amount of damage to inflict.
	 */
	@Override
	public void takeDamage(int damage) {
		this.healPoint -= damage;
	}

	/**
	 * Updates the entity's state for the current game frame.
	 * This method is called on every tick of the game loop and is responsible for
	 * executing the boss's movement patterns.
	 */
	@Override
	public void update() {

		if (!midSkillTriggered && this.healPoint < this.maxHp / 2) {
			midSkillTriggered = true;
			midSkillActive = true;
			movingToCenter = true;
			logger.info("OMEGA : Mid skill triggered!");
		}

		if (midSkillActive) {
			runMidSkill();
			return;
		}

		movePatterns();
	}

	/**
	 * Renders the entity at its current position using the provided DrawManager.
	 */
	public void draw(DrawManager drawManager) {
		drawManager.getEntityRenderer().drawEntity(this, this.positionX, this.positionY);
	}
}
