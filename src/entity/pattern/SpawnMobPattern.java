package entity.pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.*;
import java.util.logging.Logger;
import engine.Core;

public class SpawnMobPattern {


    /** Mob cannot move below this boundary. */
    private final int bottomBoundary;
    /** The width of the screen. */
    private final int screenWidth;
    /** Boss spawn child ships */
    private List<MidBossMob> ChildShips;
    /** Current accumulated count of all ships spawned throughout the fight. */
    private int SPAWN_COUNT = 0;
    /** Movement speed of the spawned child ships. */
    private final int Child_Speed = 1;
    /** Strategy object managing the movement patterns of the child ships. */
    private MidBossMobMovement movementStrategy;
    /** Current active movement and spawn pattern (1 or 2). */
    private int currentPattern = 1;
    /** The current X position of the boss. */
    private int BOSS_PositionX;
    /** The current Y position of the boss. */
    private int BOSS_PositionY;
    /** The width of the boss. */
    private final int BOSS_WIDTH;
    /** The height of the boss. */
    private final int BOSS_HEIGHT;
    /** The maximum health points of the boss. */
    private final int BOSS_MAXHP;
    private Logger Logger;
    /** List of ship count increments for each spawn phase (5, 10, 15, 30) */
    private final int[] SPAWN_COUNT_LIST = {
            5,
            10,
            15,
            30
    };
    /** List of required accumulated spawn totals to trigger the next phase (0, 5, 15, 30). */
    private int SPAWN_COUNT_CHECK = 0;
    /** List of boss HP ratios (0.9, 0.7, 0.5, 0.2) required to trigger a spawn phase. */
    private final double[] HP_THRESHOLDS = {
            0.9,
            0.7,
            0.5,
            0.2
    };
    /** Color palette used to assign distinct colors to spawned children. */
    private Color[] colorPalette = {
            new Color( 0xFF4081),
            new Color( 0xFCDD8A),
            new Color( 0xFF5722),
            new Color( 0x8BC34A),
            new Color( 0x9C27B0),
            new Color( 0x6A89FF),
            new Color( 0x6756C9),
            new Color( 0xF2606F),
            new Color( 0xF5A5A5),
            new Color( 0x6F5E77),
            new Color( 0x32A9B3),
            new Color( 0x8303EE)
    };
    /**
     * Initializes the SpawnMobPattern component.
     * @param bottomBoundary The lowest point mobs can move to.
     * @param width The width of the screen/play area.
     * @param bossWidth The width of the boss.
     * @param bossHeight The height of the boss.
     * @param Boss_MaxHP The maximum health of the boss.
     */
    public SpawnMobPattern(int bottomBoundary, int width, int bossWidth, int bossHeight, int Boss_MaxHP) {
        this.bottomBoundary = bottomBoundary;
        this.screenWidth = width;
        this.ChildShips = new ArrayList<MidBossMob>();

        this.BOSS_WIDTH = bossWidth;
        this.BOSS_HEIGHT = bossHeight;
        this.BOSS_MAXHP = Boss_MaxHP;
        this.Logger = Core.getLogger();
        this.movementStrategy = new MidBossMobMovement(
                this.bottomBoundary,
                this.screenWidth,
                this.BOSS_WIDTH,
                this.BOSS_HEIGHT,
                this.Child_Speed
        );
    }

    /**
     * Updates the pattern status, checks for spawning, and updates child ship movement.
     * @param bossPositionX Initial X position of the boss (used for mob spawning).
     * @param bossPositionY Initial Y position of the boss (used for mob spawning).
     * @param bossHealPoint The current health point of the main boss.
     */
    public void update(int bossPositionX, int bossPositionY,int bossHealPoint) {
        this.BOSS_PositionX = bossPositionX;
        this.BOSS_PositionY = bossPositionY;
        this.checkPatternSwitch(bossHealPoint);
        this.spawnPattern(bossHealPoint);
        this.childMovePattern();
    }
    /**
     * Executes the current movement pattern for all active child ships.
     */
    public void childMovePattern() {
        if(this.currentPattern == 1) {
            this.movementStrategy.pattern_1_Movement(
                    this.BOSS_PositionX,
                    this.BOSS_PositionY,
                    this.ChildShips,
                    this.SPAWN_COUNT
            );
        } else {
            this.movementStrategy.pattern_2_Movement(
                    this.BOSS_PositionX,
                    this.BOSS_PositionY,
                    this.ChildShips,
                    this.SPAWN_COUNT
            );
        }
    }
    /**
     * Checks if the pattern needs to switch based on the boss's current health.
     * Switches to pattern 2 when health drops below 50%.
     * @param bossHealPoint The current health point of the main boss.
     */
    public void checkPatternSwitch(int bossHealPoint) {
        if(bossHealPoint < this.BOSS_MAXHP * 0.5 && this.currentPattern == 1) {
            this.currentPattern = 2;
            this.Logger.info("Pattern Change: Switching to Child Movement Pattern 2");
        }
    }
    /**
     * Cleans up destroyed children and checks the boss health to trigger the next spawn wave.
     * @param bossHealPoint The current health point of the main boss.
     */
    public void spawnPattern(int bossHealPoint) {
        this.cleanDestroyedChild();
        double HPRatio = (double) bossHealPoint/ this.BOSS_MAXHP;
        if(this.SPAWN_COUNT_CHECK == 3 && HPRatio < HP_THRESHOLDS[3]) {
            this.SPAWN_COUNT += this.SPAWN_COUNT_LIST[this.SPAWN_COUNT_CHECK];
            createChild(this.SPAWN_COUNT_LIST[this.SPAWN_COUNT_CHECK++]);
        }else if(this.SPAWN_COUNT_CHECK == 2 && HPRatio < HP_THRESHOLDS[2]) {
            this.SPAWN_COUNT += this.SPAWN_COUNT_LIST[this.SPAWN_COUNT_CHECK];
            createChild(this.SPAWN_COUNT_LIST[this.SPAWN_COUNT_CHECK++]);
        }else if(this.SPAWN_COUNT_CHECK == 1 && HPRatio < HP_THRESHOLDS[1]) {
            this.SPAWN_COUNT += this.SPAWN_COUNT_LIST[this.SPAWN_COUNT_CHECK];
            createChild(this.SPAWN_COUNT_LIST[this.SPAWN_COUNT_CHECK++]);
        }else if(this.SPAWN_COUNT_CHECK == 0 && HPRatio < HP_THRESHOLDS[0]) {
            this.SPAWN_COUNT += this.SPAWN_COUNT_LIST[this.SPAWN_COUNT_CHECK];
            createChild(this.SPAWN_COUNT_LIST[this.SPAWN_COUNT_CHECK++]);
        }
    }
    /**
     * Creates new child ships based on the required total count and adds them to the list.
     * @param shipCount The *total* number of children to be created in this wave.
     */
    public void createChild(int shipCount) {
        this.Logger.info("Create Child");
        for (int count = 0; count < shipCount; count++) {
            MidBossMob ship = new MidBossMob(
                    this.BOSS_PositionX ,
                    BOSS_PositionY + (int)(60.0/shipCount * (count+1)),
                    4,
                    10,
                    colorPalette[count % colorPalette.length]
            );
            this.ChildShips.add(ship);
        }
    }
    /**
     * Removes all child ships from the list that have been destroyed.
     */
    public void cleanDestroyedChild() {
        Iterator<MidBossMob> iterator = this.ChildShips.iterator();
        while(iterator.hasNext()){
            MidBossMob ship = iterator.next();
            if(ship.isDestroyed()) { iterator.remove(); }
        }
    }
    /**
     * Returns the list of currently active child ships.
     * NOTE: This is the getter method required by the game controller for collision and rendering.
     * @return The list of MidBossMob children.
     */
    public List<MidBossMob> getChildShips() { return ChildShips; }
}