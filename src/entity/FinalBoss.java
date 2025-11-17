package entity;

import audio.SoundManager;
import engine.Cooldown;
import engine.Core;
import engine.DrawManager;
import entity.pattern.*;

import java.awt.*;
import java.util.logging.Logger;

public class FinalBoss extends Entity implements BossEntity{

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

	private Logger logger;


    /** basic attribute of final boss */

    public FinalBoss(int positionX, int positionY, HasBounds playerPosition, int screenWidth, int screenHeight){

        super(positionX, positionY,50 * 2,40 * 2, Color.RED);
        this.healPoint = 80;
        this.maxHp = healPoint;
        this.pointValue = 1000;
        this.spriteType = DrawManager.SpriteType.FinalBoss1;
        this.isDestroyed = false;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.animationCooldown = new Cooldown(500);

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
	        bossPattern = new TimeGapAttackPattern(this,playerPosition,screenWidth,screenHeight);
        }
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
}