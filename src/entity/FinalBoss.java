package entity;

import audio.SoundManager;
import engine.Cooldown;
import engine.Core;
import engine.DrawManager;
import entity.pattern.*;
import entity.Ship;
import java.util.List;

import java.awt.*;
import java.util.logging.Logger;

public class FinalBoss extends Entity implements BossEntity, Collidable{

    private int healPoint;
    private final int maxHp;
    private final int pointValue;
    private boolean isDestroyed;

    private Cooldown animationCooldown;
    private int screenWidth;
    private int screenHeight;

	private BossPattern bossPattern;
	private HasBounds playerPosition;
	private int bossPhase = 1;

    private List<Ship> ships;
    private boolean blackHole70 = false;
    private boolean blackHole40 = false;
    private boolean blackHole10 = false;
    private BlackHolePattern currentBlackHole = null;

	private Logger logger;


    /** basic attribute of final boss */

    public FinalBoss(int positionX, int positionY, List<Ship> ships, int screenWidth, int screenHeight){

        super(positionX, positionY, 50 * 2,40 * 2, Color.RED);
        this.healPoint = 80;
        this.maxHp = healPoint;
        this.pointValue = 1000;
        this.spriteType = DrawManager.SpriteType.FinalBoss1;
        this.isDestroyed = false;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.animationCooldown = new Cooldown(500);

        this.ships = ships;

		this.playerPosition = playerPosition;
		logger = Core.getLogger();
	    choosePattern();
	}

    /** for vibrant moving with final boss
     * final boss spritetype is the same with special enemy and enemyshipA, because final boss spritetype have not yet implemented
     * becasue final boss is single object, moving and shooting pattern are included in update methods
     */
    @Override
    public void update(){
        if(this.animationCooldown.checkFinished()){
            this.animationCooldown.reset();

            switch (this.spriteType) {
                case FinalBoss1:
                    this.spriteType = DrawManager.SpriteType.FinalBoss2;
                    break;
                case FinalBoss2:
                    this.spriteType = DrawManager.SpriteType.FinalBoss1;
                    break;
            }
        }
        choosePattern();
        double hpRatio = (double) this.healPoint / this.maxHp;

        if (!blackHole70 && hpRatio <= 0.7) {
            activateBlackHole();
            blackHole70 = true;
        }
        if (!blackHole40 && hpRatio <= 0.4) {
            activateBlackHole();
            blackHole40 = true;
        }
        if (!blackHole10 && hpRatio <= 0.1) {
            activateBlackHole();
            blackHole10 = true;
        }

        if (currentBlackHole != null) {
            currentBlackHole.attack();
            if (currentBlackHole.isFinished()) {
                currentBlackHole = null;
            }
        }

		bossPattern.move();
		bossPattern.attack();

		this.positionX = bossPattern.getBossPosition().x;
		this.positionY = bossPattern.getBossPosition().y;
    }

    /** decrease boss' healpoint */
    @Override
    public void takeDamage(int damage){
        this.healPoint -= damage;
        SoundManager.stop("sfx/pikachu.wav");
        SoundManager.play("sfx/pikachu.wav");
        if(this.healPoint <= 0){
            this.destroy();
        }
    }

    /** movement pattern of final boss */
    private void choosePattern(){
        if(this.healPoint > this.maxHp /2 && this.bossPhase == 1){
			++this.bossPhase;
			bossPattern = new PinnedBossPattern(this, screenWidth, screenHeight);
			logger.info("FINAL: Pinned Pattern");
        }
        else if (this.healPoint <= this.maxHp /2 && this.bossPhase == 2){
			++this.bossPhase;
            bossPattern = new ZigZagPattern(this, screenWidth, screenHeight);
	        logger.info("FINAL: Zigzag Pattern");
        }
        else if (this.healPoint <= this.maxHp /4 && this.bossPhase == 3) {
			++this.bossPhase;
			bossPattern = new ZigZagAngryPattern(this,screenWidth, screenHeight);
	        logger.info("FINAL: Angry Pattern");

        }
		else if (this.healPoint <= this.maxHp /6 && this.bossPhase == 4) {
			++this.bossPhase;
	        bossPattern = new TimeGapAttackPattern(this,ships,screenWidth,screenHeight);
        }
    }

    private void activateBlackHole() {
        logger.info("FINAL: Black Hole Pattern Activated!");

        int cx = this.positionX + this.width / 2;
        int cy = this.positionY + this.height + 60;
        int radius = screenHeight;

        currentBlackHole = new BlackHolePattern(this, ships, cx, cy, radius, 0.005, 7000);
    }

    /** move simple */
    @Override
    public void move(int distanceX, int distanceY){
        this.positionX += distanceX;
        this.positionY += distanceY;
    }

    /** flag final boss' destroy */
    @Override
    public void destroy(){
        if(!this.isDestroyed){
            this.spriteType = DrawManager.SpriteType.FinalBossDeath;
            this.isDestroyed = true;
        }
    }

    /** check final boss' destroy */
    @Override
    public boolean isDestroyed(){
        return this.isDestroyed;
    }

	@Override
	public int getHealPoint(){
		return this.healPoint;
	}

	public int getMaxHp(){
		return this.maxHp;
	}

	@Override
	public int getPointValue(){
		return this.pointValue;
	}

	public BossPattern getBossPattern() { return bossPattern; }

	public int getBossPhase() { return bossPhase; }

	public void setTarget(HasBounds target){
		this.playerPosition = target;
		if(bossPattern != null){
			bossPattern.setTarget(target);
		}
	}

    public BlackHolePattern getCurrentBlackHole(){
        return currentBlackHole;
    }

	@Override
	public void onCollision(Collidable other, GameModel model) {
		other.onCollideWithBoss(this, model);
	}

	@Override
	public void onHitByPlayerBullet(Bullet bullet, GameModel model) {
		model.requestBossHitByPlayerBullet(bullet, this);
	}
}