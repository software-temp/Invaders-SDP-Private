package entity;

import engine.*;
import engine.level.Level;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import engine.*;
import engine.level.ItemDrop;
import entity.pattern.BlackHolePattern;

/**
 * Implements the Model for the game screen.
 * Contains all game state and game logic.
 */
public class GameModel {

    /** Milliseconds until the screen accepts user input. */
    public static final int INPUT_DELAY = 6000;
    /** Bonus score for each life remaining at the end of the level. */
    private static final int LIFE_SCORE = 100;
    /** Minimum time between bonus ship's appearances. */
    private static final int BONUS_SHIP_INTERVAL = 20000;
    /** Maximum variance in the time between bonus ship's appearances. */
    private static final int BONUS_SHIP_VARIANCE = 10000;
    /** Time until bonus ship explosion disappears. */
    private static final int BONUS_SHIP_EXPLOSION = 500;
    /** Time until bonus ship explosion disappears. */
    private static final int BOSS_EXPLOSION = 600;
    /** Time from finishing the level to screen change. */
    private static final int SCREEN_CHANGE_INTERVAL = 1500;

    /** Current level data (direct from Level system). */
    private Level currentLevel;
    /** Current difficulty level number. */
    private int level;
    /** Formation of enemy ships. */
    private EnemyShipFormationModel enemyShipFormationModel;
    /** Formation of special enemy ships. */
    private EnemyShipSpecialFormation enemyShipSpecialFormation;
    /** Player's ship. */
    private Ship ship;
    /** Second Player's ship. */
    private Ship shipP2;
    /** Bonus enemy ship that appears sometimes. */
    private EnemyShip enemyShipSpecial; // This seems unused, but keeping for compatibility
    /** Minimum time between bonus ship appearances. */
    private Cooldown enemyShipSpecialCooldown;
    /** team drawing may implement */
    private FinalBoss finalBoss;
    /** Time until bonus ship explosion disappears. */
    private Cooldown enemyShipSpecialExplosionCooldown;
    /** Time until Boss explosion disappears. */
    private Cooldown bossExplosionCooldown;
    /** Time from finishing the level to screen change. */
    private Cooldown screenFinishedCooldown;
    /** OmegaBoss */
    private MidBoss omegaBoss;
    /** Set of all bullets fired by on-screen ships. */
    private Set<Bullet> bullets;
    /** Set of all dropItems dropped by on screen ships. */
    private Set<DropItem> dropItems;
    /** Current score. */
    private int score;
    // === [ADD] Independent scores for two players ===
    private int scoreP1 = 0;
    private int scoreP2 = 0;
    /** Player lives left. */
    private int livesP1;
    private int livesP2;
    /** Total bullets shot by the player. */
    private int bulletsShot;
    /** Total ships destroyed by the player. */
    private int shipsDestroyed;
    /** Moment the game starts. */
    private long gameStartTime;
    /** Checks if the level is finished. */
    private boolean levelFinished;
    /** Checks if a bonus life is received. */
    private boolean bonusLife;
    /** Maximum number of lives. */
    private int maxLives;
    /** Current coin. */
    private int coin;

    /** bossBullets carry bullets which Boss fires */
    private Set<Bullet> bossBullets;
    /** Is the bullet on the screen erased */
    private boolean is_cleared = false;
    /** Timer to track elapsed time. */
    private GameTimer gameTimer;
    /** Elapsed time since the game started. */
    private long elapsedTime;
    // Achievement popup
    private String achievementText;
    private Cooldown achievementPopupCooldown;
    private enum StagePhase{wave, boss_wave};
    private StagePhase currentPhase;
    /** Health change popup. */
    private String healthPopupText;
    private Cooldown healthPopupCooldown;

    private GameState gameState;
    private Logger logger;
    private int width;
    private int height;

    /** Milliseconds until the screen accepts user input. */
    private Cooldown inputDelay;

    private List<Ship> ships;
    /** variables for Boss BlackHole Pattern */
    private boolean blackHoleActive = false;
    private int blackHoleCX;
    private int blackHoleCY;
    private int blackHoleRadius;
    private Cooldown blackHoleCooldown;
    private int lastHp;
    private static final int BLACK_HOLE_DURATION_MS = 7000;


    public GameModel(GameState gameState, Level level, boolean bonusLife, int maxLives, int width, int height) {
        this.logger = Core.getLogger();
        this.width = width;
        this.height = height;

        this.currentLevel = level;
        this.bonusLife = bonusLife;
        this.maxLives = maxLives;
        this.level = gameState.getLevel();
        this.score = gameState.getScore();
        this.coin = gameState.getCoin();
        this.livesP1 = gameState.getLivesRemaining();
        this.livesP2 = gameState.getLivesRemainingP2();
        this.gameState = gameState;
        if (this.bonusLife) {
            this.livesP1++;
            this.livesP2++;
        }
        this.bulletsShot = gameState.getBulletsShot();
        this.shipsDestroyed = gameState.getShipsDestroyed();
    }

    /**
     * Initializes basic model properties, and adds necessary elements.
     */
    public final void initialize() {
        /** Initialize the bullet Boss fired */
        this.bossBullets = new HashSet<>();
        enemyShipFormationModel = new EnemyShipFormationModel(this.currentLevel, width);
		enemyShipFormationModel.applyEnemyColorByLevel(this.currentLevel);
		this.ship = new Ship(this.width / 4, GameConstant.ITEMS_SEPARATION_LINE_HEIGHT * 19 / 20, Color.green);
        this.ship.setPlayerId(1);   //=== [ADD] Player 1 ===

        this.shipP2 = new Ship(this.width * 3 / 4, GameConstant.ITEMS_SEPARATION_LINE_HEIGHT * 19 / 20, Color.pink);
        this.shipP2.setPlayerId(2); // === [ADD] Player2 ===
        // special enemy initial

        GameSettings specialSettings = new GameSettings(
				currentLevel.getFormationWidth(),
		        currentLevel.getFormationHeight(),
		        currentLevel.getBaseSpeed(),
		        currentLevel.getShootingFrecuency()
	    );

	    enemyShipSpecialFormation = new EnemyShipSpecialFormation(specialSettings,
                Core.getVariableCooldown(BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE),
                new Cooldown(BONUS_SHIP_EXPLOSION));
        this.bossExplosionCooldown = new Cooldown(BOSS_EXPLOSION);
        this.screenFinishedCooldown = new Cooldown(SCREEN_CHANGE_INTERVAL);
        this.bullets = new HashSet<Bullet>();
        this.dropItems = new HashSet<DropItem>();

        // Special input delay / countdown.
        this.gameStartTime = System.currentTimeMillis();
        this.inputDelay = new Cooldown(INPUT_DELAY);
        this.inputDelay.reset();


        this.gameTimer = new GameTimer();
        this.elapsedTime = 0;
        this.finalBoss = null;
        this.omegaBoss = null;
        this.currentPhase = StagePhase.wave;

//        bossPattern = new BossPattern();
//        blackHoleCooldown = Core.getCooldown(BLACK_HOLE_DURATION_MS);
//        lastHp = Integer.MAX_VALUE;
        /** ships list for boss argument */
        this.ships = new ArrayList<>();
        if (this.ship != null) ships.add(this.ship);
        if (this.shipP2 != null) ships.add(this.shipP2);
    }

    /**
     * Unified scoring entry: maintains both P1/P2 and legacy this.score (total score)
     */
    private void addPointsFor(Bullet bullet, int pts) {
        Integer owner = (bullet != null ? bullet.getOwnerId() : null);
        if (owner != null && owner == 2) {
            this.scoreP2 += pts;   // P2
        } else {
            this.scoreP1 += pts;   // Default to P1 (for null compatibility)
        }
        this.score += pts;        // Keep maintaining the total score, for legacy process compatibility
    }

    /**
     * Processes a player move command received from the Controller.
     * (Includes boundary checking logic)
     * @param playerNum (1 or 2)
     * @param direction ("RIGHT", "LEFT", "UP", "DOWN")
     */
    public void playerMove(int playerNum, String direction) {
        Ship ship = (playerNum == 1) ? this.ship : this.shipP2;
        // If the ship doesn't exist or is destroyed, do nothing
        if (ship == null || ship.isDestroyed()) return;

        // Boundary logic brought over from the original processPlayerInput
        switch (direction) {
            case "RIGHT":
                boolean isRightBorder = ship.getPositionX() + ship.getWidth() + ship.getSpeed() > this.width - 1;
                if (!isRightBorder) ship.moveRight();
                break;
            case "LEFT":
                boolean isLeftBorder = ship.getPositionX() - ship.getSpeed() < 1;
                if (!isLeftBorder) ship.moveLeft();
                break;
            case "UP":
                boolean isUpBorder = ship.getPositionY() - ship.getSpeed() < GameConstant.STAT_SEPARATION_LINE_HEIGHT;
                if (!isUpBorder) ship.moveUp();
                break;
            case "DOWN":
                boolean isDownBorder = ship.getPositionY() + ship.getHeight() + ship.getSpeed() > GameConstant.ITEMS_SEPARATION_LINE_HEIGHT;
                if (!isDownBorder) ship.moveDown();
                break;
        }
    }

    /**
     * Processes a player fire command received from the Controller.
     * (Includes firing logic, bulletsShot count, and Achievement management)
     * @param playerNum (1 or 2)
     */
    public void playerFire(int playerNum) {
        Ship ship = (playerNum == 1) ? this.ship : this.shipP2;
        // If the ship doesn't exist or is destroyed, do nothing
        if (ship == null || ship.isDestroyed()) return;

        // Firing logic brought over from the original processPlayerInput
        if (ship.shoot(this.bullets)) {
            this.bulletsShot++;
            AchievementManager.getInstance().onShotFired();
        }
    }

    public void updateGameWorld() {
        // Phase 1: Update state/position of ALL entities
        this.updateAllEntities();

        // Phase 2: Process interactions and collisions
        this.processAllCollisions();

        // Phase 3: Clean up destroyed or off-screen entities
        this.cleanupAllEntities();
    }

    /**
     * Updates all non-player-controlled game logic.
     */
    public void updateAllEntities() {
        switch (this.currentPhase) {
            case wave:
                if (!DropItem.isTimeFreezeActive()) {
                    this.enemyShipFormationModel.update();
                    this.enemyShipFormationModel.shoot(this.bullets);
                }
                if (this.enemyShipFormationModel.isEmpty()) {
                    this.currentPhase = StagePhase.boss_wave;
                }
                break;
            case boss_wave:
                if (this.finalBoss == null && this.omegaBoss == null){
                    bossReveal();
                    this.enemyShipFormationModel.clear();
                }
                if(this.finalBoss != null){
                    finalbossManage();
                }
                else if (this.omegaBoss != null){
                    this.omegaBoss.update();
                    if (this.omegaBoss.isDestroyed()) {
                        if ("omegaAndFinal".equals(this.currentLevel.getBossId())) {
                            this.omegaBoss = null;
                            this.finalBoss = new FinalBoss(this.width / 2 - 50, 50, ships, this.width, this.height);
                            this.logger.info("Final Boss has spawned!");
                        } else {
                            this.levelFinished = true;
                            this.screenFinishedCooldown.reset();
                        }
                    }
                }
                else{
                    if(!this.levelFinished){
                        this.levelFinished = true;
                        this.screenFinishedCooldown.reset();
                    }
                }
                break;
        }
        this.ship.update();
        if (this.shipP2 != null) {
            this.shipP2.update();
        }
        // special enemy update
        this.enemyShipSpecialFormation.update();

        for (Bullet bullet : this.bullets) {
            bullet.update();
        }

        for (DropItem dropItem : this.dropItems) {
            dropItem.update();
        }
    }

	/**
	 * Detects collisions between all active entities.
	 * Each pair of collidables is checked, and their collision handlers are invoked.
	 */
	private void processAllCollisions() {

		List<Entity> entities = new ArrayList<>();

		if (ship != null) entities.add(ship);
		if (shipP2 != null) entities.add(shipP2);

		for (EnemyShip e : enemyShipFormationModel) {
			if (e != null && !e.isDestroyed()) entities.add(e);
		}

		for (EnemyShip e : enemyShipSpecialFormation) {
			if (e != null && !e.isDestroyed()) entities.add(e);
		}

		if (finalBoss != null && !finalBoss.isDestroyed()) entities.add(finalBoss);
		if (omegaBoss != null && !omegaBoss.isDestroyed()) entities.add(omegaBoss);

		entities.addAll(bullets);
		entities.addAll(bossBullets);
		entities.addAll(dropItems);

		for (int i = 0; i < entities.size(); i++) {
			Entity a = entities.get(i);

			for (int j = i + 1; j < entities.size(); j++) {
				Entity b = entities.get(j);

				if (checkCollision(a, b)) {
					a.onCollision(b, this);
					b.onCollision(a, this);
				}
			}
		}
		entities.clear();
	}

	/**
	 * Handles damage and rewards when a player bullet hits a normal enemy.
	 */
	public void requestEnemyHitByPlayerBullet(Bullet bullet, EnemyShip enemy) {

		if (!bullets.contains(bullet)) return;
		if (enemy.isDestroyed()) return;

		int pts = enemy.getPointValue();
		addPointsFor(bullet, pts);
		coin += pts / 10;

		AchievementManager.getInstance().onEnemyDefeated();

		attemptItemDrop(enemy);

		String type = enemy.getEnemyType();
		if ("enemySpecial".equals(type)) {
			if (enemyShipSpecialFormation != null) {
				enemyShipSpecialFormation.destroy(enemy);
			}
		} else {
			if (enemyShipFormationModel != null) {
				enemyShipFormationModel.destroy(enemy);
			}
		}

		if (!bullet.penetration()) {
			bullets.remove(bullet);
		}
	}


	/**
	 * Applies damage to a player ship.
	 * Handles hit effect, invincibility, life reduction, and game-over check.
	 */
	public void requestShipDamage(Ship ship, int amount) {

		if (ship.isInvincible()) return;

		ship.destroy();

		if (ship.getPlayerId() == 1) {
			livesP1 = Math.max(0, livesP1 - amount);  // ★ 여기!!
		} else {
			livesP2 = Math.max(0, livesP2 - amount);  // ★ 여기!!
		}

		if (this.isGameOver()) {
			this.setGameOver();
		}
	}

	public void requestRemoveBullet(Bullet bullet) {
		bullets.remove(bullet);
	}

	public void requestRemoveBossBullet(BossBullet bullet) {
		bossBullets.remove(bullet);
	}


	public void requestBossHitByPlayerBullet(Bullet bullet, BossEntity boss) {


		boss.takeDamage(1);

		if (!bullet.penetration()) {
			bullets.remove(bullet);
		}

		if (boss.getHealPoint() <= 0) {
			boss.destroy();

			int pts = boss.getPointValue();
			addPointsFor(bullet, pts);
			this.coin += pts / 10;

			AchievementManager.getInstance().unlockAchievement("Boss Slayer");
		}
	}

	/**
	 * When the player collides with an enemy, apply crash damage.
	 */
	public void requestPlayerCrash(Ship ship, Entity enemy) {

		if (enemy instanceof EnemyShip e) {
			if (!e.isDestroyed()) {
				String type = e.getEnemyType();

				if ("enemySpecial".equals(type)) {
					if (enemyShipSpecialFormation != null) {
						enemyShipSpecialFormation.destroy(e);
					}
				} else {
					if (enemyShipFormationModel != null) {
						enemyShipFormationModel.destroy(e);
					}
				}
			}
		}
		requestShipDamage(ship, 1);
	}


	/**
	 * Applies the effect of a collected drop item to the player ship.
	 */
	public void requestApplyItem(Ship ship, DropItem item) {

		if (!dropItems.contains(item)) return;

		ItemHUDManager.getInstance().addDroppedItem(item.getItemType());

		switch (item.getItemType()) {
			case Heal:
				if (ship.getPlayerId() == 1) gainLife();
				else gainLifeP2();
				break;

			case Shield:
				ship.activateInvincibility(5000);
				break;

			case Stop:
				DropItem.applyTimeFreezeItem(3000);
				break;

			case Push:
				pushEnemiesBack();
				break;

			case Explode:
				int destroyed = enemyShipFormationModel.destroyAll();
				int pts = destroyed * 5;
				if (ship.getPlayerId() == 2) scoreP2 += pts;
				else scoreP1 += pts;
				break;

			case Slow:
				enemyShipFormationModel.activateSlowdown();
				break;
		}

		dropItems.remove(item);
	}



	/**
	 * Randomly drops an item from a defeated enemy based on drop chance.
	 */
	public void attemptItemDrop(EnemyShip enemy) {

		String enemyType = enemy.getEnemyType();
		if (enemyType == null) return;

		if ("enemySpecial".equals(enemyType)) return;

		List<ItemDrop> drops = currentLevel.getItemDrops();
		if (drops == null || drops.isEmpty()) return;

		for (ItemDrop drop : drops) {

			if (!enemyType.equalsIgnoreCase(drop.getEnemyType())) continue;

			if (Math.random() > drop.getDropChance()) continue;

			DropItem.ItemType type = DropItem.fromString(drop.getItemId());
			if (type == null) {
				logger.warning("Invalid itemId in level config: " + drop.getItemId());
				continue;
			}

			DropItem item = ItemPool.getItem(
					enemy.getPositionX() + enemy.getWidth() / 2,
					enemy.getPositionY() + enemy.getHeight() / 2,
					2,
					type
			);

			dropItems.add(item);
			return;
		}
	}


	/**
	 * Pushes all enemy ships upward (used by Push-type item).
	 */
	public void pushEnemiesBack() {
		for (EnemyShip enemy : enemyShipFormationModel) {
			if (enemy != null && !enemy.isDestroyed()) {
				enemy.move(0, -20);
			}
		}
	}

    private void cleanupAllEntities() {
        cleanBullets();
        cleanItems();
    }


    /**
     * Cleans bullets that go off screen.
     */
    private void cleanBullets() {
        Set<Bullet> recyclable = new HashSet<Bullet>();
        for (Bullet bullet : this.bullets) {
            if (bullet.getPositionY() < GameConstant.STAT_SEPARATION_LINE_HEIGHT
                    || bullet.getPositionY() > this.height)
                recyclable.add(bullet);
        }
        this.bullets.removeAll(recyclable);
        BulletPool.recycle(recyclable);
    }

    /**
     * Cleans Items that go off screen.
     */

    private void cleanItems() {
        Set<DropItem> recyclable = new HashSet<DropItem>();
        for (DropItem dropItem : this.dropItems) {
            if (dropItem.getPositionY() < GameConstant.STAT_SEPARATION_LINE_HEIGHT
                    || dropItem.getPositionY() > this.height)
                recyclable.add(dropItem);
        }
        this.dropItems.removeAll(recyclable);
        ItemPool.recycle(recyclable);
    }


    /**
     * Checks if two entities are colliding.
     *
     * @param a
     * First entity, the bullet.
     * @param b
     * Second entity, the ship.
     * @return Result of the collision test.
     */
    private boolean checkCollision(final HasBounds a, final HasBounds b) {
        // Calculate center point of the entities in both axis.
        int centerAX = a.getPositionX() + a.getWidth() / 2;
        int centerAY = a.getPositionY() + a.getHeight() / 2;
        int centerBX = b.getPositionX() + b.getWidth() / 2;
        int centerBY = b.getPositionY() + b.getHeight() / 2;
        // Calculate maximum distance without collision.
        int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
        int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
        // Calculates distance.
        int distanceX = Math.abs(centerAX - centerBX);
        int distanceY = Math.abs(centerAY - centerBY);

        return distanceX < maxDistanceX && distanceY < maxDistanceY;
    }

    /**
     * Shows an achievement popup message on the HUD.
     *
     * @param message
     * Text to display in the popup.
     */
    public void showAchievement(String message) {
        this.achievementText = message;
        this.achievementPopupCooldown = new Cooldown(2500); // Show for 2.5 seconds
        this.achievementPopupCooldown.reset();
    }

    /**
     * Displays a notification popup when the player gains or loses health
     *
     * @param message
     * Text to display in the popup
     */

    public void showHealthPopup(String message) {
        this.healthPopupText = message;
        this.healthPopupCooldown = new Cooldown(500);
        this.healthPopupCooldown.reset();
    }

    /**
     * Returns a GameState object representing the status of the game.
     *
     * @return Current game state.
     */
    public final GameState getGameState() {
        if (this.coin > 2000) {
            AchievementManager.getInstance().unlockAchievement("Mr. Greedy");
        }
        return new GameState(this.level, this.score, this.livesP1,this.livesP2,
                this.bulletsShot, this.shipsDestroyed,this.coin);
    }
    /**
     * Adds one life to the player.
     */
    public final void gainLife() {
        if (this.livesP1 < this.maxLives) {
            this.livesP1++;
        }
    }

    public final void gainLifeP2() {
        if (this.livesP2 < this.maxLives) {
            this.livesP2++;
        }
    }

    private void bossReveal() {
        String bossName = this.currentLevel.getBossId();

        if (bossName == null || bossName.isEmpty()) {
            this.logger.info("No boss for this level. Proceeding to finish.");
            return;
        }

        this.logger.info("Spawning boss: " + bossName);
        switch (bossName) {
            case "finalBoss":
                this.finalBoss = new FinalBoss(this.width / 2 - 50, 80,  ships, this.width, this.height);
                this.logger.info("Final Boss has spawned!");
                break;
            case "omegaBoss":
            case "omegaAndFinal":
                this.omegaBoss = new OmegaBoss(Color.ORANGE, ship);

                this.logger.info("Omega Boss has spawned!");
                break;
            default:
                this.logger.warning("Unknown bossId: " + bossName);
                break;
        }
    }


    public void finalbossManage(){
        if (this.finalBoss != null && !this.finalBoss.isDestroyed()) {
            this.finalBoss.update();
            BlackHolePattern bh = finalBoss.getCurrentBlackHole();

            if (bh != null && bh.isActive()) {
                blackHoleActive = true;
                blackHoleCX = bh.getCenterX();
                blackHoleCY = bh.getCenterY();
                blackHoleRadius = bh.getRadius();
            } else {
                blackHoleActive = false;
            }

			if(this.finalBoss.getBossPhase() == 3 && !this.is_cleared){
				bossBullets.clear();
				is_cleared = true;
				logger.info("boss is angry");
			}
			bossBullets.addAll(this.finalBoss.getBossPattern().getBullets());

            /** bullets to erase */
            Set<Bullet> bulletsToRemove = new HashSet<>();

            for (Bullet b : bossBullets) {
                b.update();
                /** If the bullet goes off the screen */
                if (b.isOffScreen(width, height) || b.shouldBeRemoved()) {
                    /** bulletsToRemove carry bullet */
                    bulletsToRemove.add(b);
                }
                /** If the bullet collides with ship */
                else if (this.livesP1 > 0 && this.checkCollision(b, this.ship)) {
                    if (!this.ship.isDestroyed()) {
						requestShipDamage(this.ship, 1);
                        this.logger.info("Hit on player ship, " + this.livesP1 + " lives remaining.");
                    }
                    bulletsToRemove.add(b);
                }
                else if (this.shipP2 != null && this.livesP2 > 0 && !this.shipP2.isDestroyed() && this.checkCollision(b, this.shipP2)) {
                    if (!this.shipP2.isDestroyed()) {
						requestShipDamage(this.shipP2, 1);
                        this.logger.info("Hit on player ship2, " + this.livesP2 + " lives remaining.");
                    }
                    bulletsToRemove.add(b);
                }
            }
            /** all bullets are removed */
            bossBullets.removeAll(bulletsToRemove);

        }
        if (this.finalBoss != null && this.finalBoss.isDestroyed()) {
            this.levelFinished = true;
            this.screenFinishedCooldown.reset();
        }
    }

    // --- Timer and State Management Methods for Controller ---

    public boolean isTimerRunning() {
        return this.gameTimer.isRunning();
    }

    public void startTimer() {
        this.gameTimer.start();
    }

    public void stopTimer() {
        if (this.gameTimer.isRunning()) {
            this.gameTimer.stop();
        }
    }

    public void updateElapsedTime() {
        if (this.gameTimer.isRunning()) {
            this.elapsedTime = this.gameTimer.getElapsedTime();
            AchievementManager.getInstance().onTimeElapsedSeconds((int)(this.elapsedTime / 1000));
        }
    }

    public boolean isGameOver() {
        return (this.livesP1 == 0) && (this.shipP2 == null || this.livesP2 == 0);
    }

    public void setGameOver() {
        this.levelFinished = true;
        this.screenFinishedCooldown.reset();
        this.stopTimer();

        if ((this.livesP1 > 0) || (this.shipP2 != null && this.livesP2 > 0)) {
            if (this.level == 1) {
                AchievementManager.getInstance().unlockAchievement("Beginner");
            } else if (this.level == 3) {
                AchievementManager.getInstance().unlockAchievement("Intermediate");
            }
        }
    }

    public void processLevelCompletion() {
        if (this.livesP1 > 0 || (this.shipP2 != null && this.livesP2 > 0)) { // Check for win condition
            if (this.currentLevel.getCompletionBonus() != null) {
                this.coin += this.currentLevel.getCompletionBonus().getCurrency();
                this.logger.info("Awarded " + this.currentLevel.getCompletionBonus().getCurrency() + " coins for level completion.");
            }

            String achievement = this.currentLevel.getAchievementTrigger();
            if (achievement != null && !achievement.isEmpty()) {
                AchievementManager.getInstance().unlockAchievement(achievement);
                this.logger.info("Unlocked achievement: " + achievement);
            }
        }
    }

    public int calculateFinalScore() {
        this.score += LIFE_SCORE * Math.max(0, this.livesP1 - 1);
        this.score += LIFE_SCORE * Math.max(0, this.livesP2 - 1);
        return this.score;
    }

    // --- Getters for View ---

    public boolean isInputDelayFinished() {
        return this.inputDelay.checkFinished();
    }

    public long getGameStartTime() {
        return this.gameStartTime;
    }

    public Ship getShip() { return ship; }
    public Ship getShipP2() { return shipP2; }
    public int getLivesP1() { return livesP1; }
    public int getLivesP2() { return livesP2; }
    public EnemyShipSpecialFormation getEnemyShipSpecialFormation() { return enemyShipSpecialFormation; }
    public FinalBoss getFinalBoss() { return finalBoss; }
    public Set<Bullet> getBossBullets() { return bossBullets; }
    public EnemyShipFormationModel getEnemyShipFormationModel() { return enemyShipFormationModel; }
    public MidBoss getOmegaBoss() { return omegaBoss; }
    public Set<Bullet> getBullets() { return bullets; }
    public Set<DropItem> getDropItems() { return dropItems; }
    public int getScoreP1() { return scoreP1; }
    public int getScoreP2() { return scoreP2; }
    public int getCoin() { return coin; }
    public long getElapsedTime() { return elapsedTime; }
    public Level getCurrentLevel() { return currentLevel; }
    public String getAchievementText() { return achievementText; }
    public Cooldown getAchievementPopupCooldown() { return achievementPopupCooldown; }
    public String getHealthPopupText() { return healthPopupText; }
    public Cooldown getHealthPopupCooldown() { return healthPopupCooldown; }
    public int getLevel() { return level; }
    public boolean isBonusLife() { return bonusLife; }
    public boolean isLevelFinished() { return levelFinished; }
    public Cooldown getScreenFinishedCooldown() { return screenFinishedCooldown; }

    public boolean isBlackHoleActive() { return blackHoleActive; }
    public int getBlackHoleCX() { return blackHoleCX; }
    public int getBlackHoleCY() { return blackHoleCY; }
    public int getBlackHoleRadius() { return blackHoleRadius; }

    public List<Entity> getEntitiesToRender() {
        List<Entity> renderList = new ArrayList<>();

        // 1. added player ships
        if (getLivesP1() > 0 && getShip() != null) {
            renderList.add(getShip());
        }
        if (getShipP2() != null && getLivesP2() > 0) {
            renderList.add(getShipP2());
        }

        // 2. added special enemyship
        if (getEnemyShipSpecialFormation() != null) {
            for (EnemyShip specialEnemy : getEnemyShipSpecialFormation()) {
                renderList.add(specialEnemy);
            }
        }

        // 3. added enemyship
        if (getEnemyShipFormationModel() != null) {
            for (EnemyShip enemy : getEnemyShipFormationModel()) {
                renderList.add(enemy);
            }
        }

        // 4. added boss
        if (getOmegaBoss() != null) {
            renderList.add(getOmegaBoss());
        }
        if (getFinalBoss() != null && !getFinalBoss().isDestroyed()) {
            renderList.add(getFinalBoss());
        }

        // 5. added items and bullets
        if (getBullets() != null) {
            renderList.addAll(getBullets());
        }
        if (getBossBullets() != null && getFinalBoss() != null && !getFinalBoss().isDestroyed()) {
            renderList.addAll(getBossBullets());
        }
        if (getDropItems() != null) {
            renderList.addAll(getDropItems());
        }

        return renderList;
    }
//    List<Ship> ships = new ArrayList<>();
//            if (this.ship != null) ships.add(this.ship);
//            if (this.shipP2 != null) ships.add(this.shipP2);
//
//    int  curHp, maxHp, trigger1, trigger2, trigger3;
//    curHp = this.finalBoss.getHealPoint();
//    maxHp = this.finalBoss.getMaxHp();
//    trigger1 = maxHp;
//    trigger2 = maxHp-maxHp/3;
//    trigger3 = maxHp-2*maxHp/3;
//
//            if(!blackHoleActive && ((trigger1 < lastHp && curHp < trigger1)
//            || (trigger2 < lastHp && curHp < trigger2)
//            || (trigger3 < lastHp && curHp < trigger3))) {
//        blackHoleActive = true;
//        blackHoleCooldown.reset();
//        lastHp = curHp;
//
//        blackHoleCX = this.finalBoss.getPositionX() + this.finalBoss.getWidth() / 2;
//        blackHoleCY = this.finalBoss.getPositionY() + this.finalBoss.getHeight() + 50;
//        blackHoleRadius = 400;
//    }
//
//    /** BlackHole duration */
//            if(blackHoleActive){
//        if(blackHoleCooldown.checkFinished()){
//            blackHoleActive = false;
//        }
//        bossPattern.blackHolePattern(ships, blackHoleCX, blackHoleCY, blackHoleRadius);
//    }
}