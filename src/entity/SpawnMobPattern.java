package entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.*;
import engine.DrawManager;
import java.util.logging.Logger;
import engine.Core;

public class SpawnMobPattern {


    /** Boss cannot move below this boundary. */
    private final int bottomBoundary;
    private final int screenWidth;
    /** Boss spawn child ships */
    private List<MidBossMob> ChildShips;
    private int SPAWN_COUNT = 0;
    private int Child_Speed = 1;
    private MidBossMobMovement movementStrategy;
    private int currentPattern = 1;
    private int BOSS_PositionX;
    private int BOSS_PositionY;
    private int BOSS_WIDTH;
    private int BOSS_HEIGHT;
    private int BOSS_MAXHP;
    private Logger Logger;
    private final int[] SPAWN_COUNT_LIST = {
            5,
            10,
            15,
            30
    };
    private final int[] SPAWN_COUNTS_LIST = {
            0,
            5,
            15,
            30
    };

    private final double[] HP_THRESHOLDS = {
            0.9,
            0.7,
            0.5,
            0.2
    };

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

    public SpawnMobPattern(int bottomBoundary, int width,int bossPositionX, int bossPositionY, int bossWidth, int bossHeight, int Boss_MaxHP) {
        this.bottomBoundary = bottomBoundary;
        this.screenWidth = width;
        this.ChildShips = new ArrayList<MidBossMob>();
        this.BOSS_PositionX = bossPositionX;
        this.BOSS_PositionY = bossPositionY;
        this.BOSS_WIDTH = bossWidth;
        this.BOSS_HEIGHT = bossHeight;
        this.BOSS_MAXHP = Boss_MaxHP;
        this.Logger = Core.getLogger();
        this.movementStrategy = new MidBossMobMovement(
                this.bottomBoundary,
                this.screenWidth,
                this.BOSS_WIDTH,
                this.BOSS_HEIGHT,
                this.Child_Speed);
    }


    public void update(int bossHealPoint) {
        this.checkPatternSwitch(bossHealPoint);
        this.spawnPattern(bossHealPoint);
        this.childMovePattern();
    }

    public void childMovePattern() {
        if(this.currentPattern == 1) { this.movementStrategy.pattern_1_Movement(this.BOSS_PositionX,this.BOSS_PositionY,this.ChildShips,this.SPAWN_COUNT);}
        else { this.movementStrategy.pattern_2_Movement(this.BOSS_PositionX,this.BOSS_PositionY,this.ChildShips,this.SPAWN_COUNT); }
    }

    public void checkPatternSwitch(int bossHealPoint) {
        if(bossHealPoint < this.BOSS_MAXHP * 0.5 && this.currentPattern == 1) {
            this.currentPattern = 2;
            this.Logger.info("Pattern Change: Switching to Child Movement Pattern 2");
        }
    }

    public void spawnPattern(int bossHealPoint) {
        this.cleanDestroyedChild();
        double HPRatio = (double) bossHealPoint/ this.BOSS_MAXHP;
        if(this.SPAWN_COUNT == SPAWN_COUNTS_LIST[3] && HPRatio < HP_THRESHOLDS[3]) {
            this.SPAWN_COUNT += SPAWN_COUNT_LIST[3];
            createChild(this.SPAWN_COUNT);
        }else if(this.SPAWN_COUNT == SPAWN_COUNTS_LIST[2] && HPRatio < HP_THRESHOLDS[2]) {
            this.SPAWN_COUNT += SPAWN_COUNT_LIST[2];
            createChild(this.SPAWN_COUNT);
        }else if(this.SPAWN_COUNT == SPAWN_COUNTS_LIST[1] && HPRatio < HP_THRESHOLDS[1]) {
            this.SPAWN_COUNT += SPAWN_COUNT_LIST[1];
            createChild(this.SPAWN_COUNT);
        }else if(this.SPAWN_COUNT == SPAWN_COUNTS_LIST[0] && HPRatio < HP_THRESHOLDS[0]) {
            this.SPAWN_COUNT += SPAWN_COUNT_LIST[0];
            createChild(this.SPAWN_COUNT);
        }
    }

    public void createChild(int shipCount) {
        this.Logger.info("Delta: Create Child");
        for (int count = 0; count < shipCount; count++) {
            MidBossMob ship = new MidBossMob(this.BOSS_PositionX ,BOSS_PositionY + 60/shipCount * (count+1),6,10,colorPalette[count%12]);
            this.ChildShips.add(ship);
        }
    }

    public void cleanDestroyedChild() {
        Iterator<MidBossMob> iterator = this.ChildShips.iterator();
        while(iterator.hasNext()){
            MidBossMob ship = iterator.next();
            if(ship.isDestroyed()) { iterator.remove(); }
        }
    }

    public List<MidBossMob> getChildShips() { return ChildShips; }
}