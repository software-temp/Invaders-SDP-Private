package entity;

import audio.SoundManager;
import engine.Cooldown;
import engine.Core;
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
    private static final int OMEGA_WIDTH = 43 * 2;
    /** Height of Omega */
    private static final int OMEGA_HEIGHT = 41 * 2;
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
    private boolean isMove = false;
    private boolean ishit = false;
    private Cooldown animationCooldown;
	/**
	 * Constructor, establishes the boss entity's generic properties.
	 *
 	 * @param color             Color of the boss entity.
 	 * @param widthBoundary		The rightmost X-coordinate for the boss's movement. The boss cannot move over this value.
 	 * @param bottomBoundary    The lowermost Y-coordinate for the boss's movement. The boss cannot move below this value.
	 */
	public OmegaBoss(Color color, int widthBoundary, int bottomBoundary) {
		super(INIT_POS_X, INIT_POS_Y, OMEGA_WIDTH, OMEGA_HEIGHT, OMEGA_HEALTH, OMEGA_POINT_VALUE, color);
		this.widthBoundary = widthBoundary;
		this.bottomBoundary = bottomBoundary;
        this.spriteType= DrawManager.SpriteType.OmegaBoss4;
        this.animationCooldown = Core.getCooldown(200);
		this.logger.info("OMEGA : Initializing Boss OMEGA");
		this.logger.info("OMEGA : move using the default pattern");
        SoundManager.stop("sfx/OmegaBossAppearance.wav");
        SoundManager.play("sfx/OmegaBossAppearance.wav");
	}

	/** move simple */
	@Override
	public void move(int distanceX, int distanceY) {
		this.positionX += distanceX;
		this.positionY += distanceY;
	}

	/**
	 * Executes the appropriate move pattern based on the boss's health status.
	 * Calls {@link #patternFirst()} if health is greater than half, otherwise
	 * Calls {@link #patternSecond()}.
	 *
	 * @see #patternFirst()
	 * @see #patternSecond()
	 */
	private void movePatterns(){
		if(this.pattern!=2 && this.healPoint < this.maxHp/2){
			this.pattern=2;
            this.isMove = true;
            this.animationCooldown = Core.getCooldown(50);
			this.color=PATTERN_2_COLOR;
			this.spriteType = DrawManager.SpriteType.OmegaBoss2;
			logger.info("OMEGA : move using second pattern");
		}

		switch(pattern){
			case 1:
				this.patternFirst();
				break;
			case 2:
				this.patternSecond();
				break;
		}
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
        SoundManager.stop("sfx/OmegaBoss_hitting.wav");
        SoundManager.play("sfx/OmegaBoss_hitting.wav");
        ishit =true;
	}

	/**
	 * Updates the entity's state for the current game frame.
	 * This method is called on every tick of the game loop and is responsible for
	 * executing the boss's movement patterns.
	 */
	@Override
	public void update() {
        if (this.animationCooldown.checkFinished()) {
            this.animationCooldown.reset();
            if (this.isMove) {
                if (isRight) {
                    // 오른쪽 이동 중이면 3↔4 프레임 토글
                    if (this.spriteType == DrawManager.SpriteType.OmegaBossMoving3)
                        this.spriteType = DrawManager.SpriteType.OmegaBossMoving4;
                    else
                        this.spriteType = DrawManager.SpriteType.OmegaBossMoving3;

                } else {
                    // 왼쪽 이동 중이면 1↔2 프레임 토글
                    if (this.spriteType == DrawManager.SpriteType.OmegaBossMoving1)
                        this.spriteType = DrawManager.SpriteType.OmegaBossMoving2;
                    else
                        this.spriteType = DrawManager.SpriteType.OmegaBossMoving1;

                }
            }
            else {
                if (isRight) {
                    // 오른쪽 이동 중이면 3↔4 프레임 토글
                    if (ishit){
                        this.spriteType = DrawManager.SpriteType.OmegaBossHitting1;
                        ishit = false;
                    }
                    else {
                        if (this.spriteType == DrawManager.SpriteType.OmegaBoss3)
                            this.spriteType = DrawManager.SpriteType.OmegaBoss4;
                        else
                            this.spriteType = DrawManager.SpriteType.OmegaBoss3;
                    }
                } else {
                    if (ishit){
                        this.spriteType = DrawManager.SpriteType.OmegaBossHitting;
                        ishit = false;
                    }
                    // 왼쪽 이동 중이면 1↔2 프레임 토글
                    else {
                        if (this.spriteType == DrawManager.SpriteType.OmegaBoss1)
                            this.spriteType = DrawManager.SpriteType.OmegaBoss2;
                        else
                            this.spriteType = DrawManager.SpriteType.OmegaBoss1;
                    }
                }
            }



        }
		this.movePatterns();
	}
}
