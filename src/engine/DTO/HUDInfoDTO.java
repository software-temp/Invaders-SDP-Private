package engine.DTO;

import entity.Ship;

public final class HUDInfoDTO {

    /** screen info */
    private final int width;
    private final int height;

    /** player info */
    private final int scoreP1;
    private final int scoreP2;
    private final int livesP1;
    private final int livesP2;
    private final int level;
//    private final double[] shipP1;
//    private final double[] shipP2;
    private final Ship shipP1;
    private final Ship shipP2;

    /** item and gameState */
    private final int coin;
    private final long elapsedTimeMillis;
    private final String levelName;

    /** popup */
    private final String achievementText;
    private final String healthPopupText;

    public HUDInfoDTO(
            int width,
            int height,
            int scoreP1,
            int scoreP2,
            int coin,
            int livesP1,
            int livesP2,
            int level,
            Ship shipP1,
            Ship shipP2,

            long elapsedTimeMillis,
            String levelName,
            String achievementText,
            String healthPopupText) {

        this.width = width;
        this.height = height;
        this.scoreP1 = scoreP1;
        this.scoreP2 = scoreP2;
        this.coin = coin;
        this.livesP1 = livesP1;
        this.livesP2 = livesP2;
        this.level = level;
        this.elapsedTimeMillis = elapsedTimeMillis;
        this.levelName = levelName;
        this.achievementText = achievementText;
        this.healthPopupText = healthPopupText;
        this.shipP1 = shipP1;
        this.shipP2 = shipP2;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getScoreP1() {
        return scoreP1;
    }

    public int getScoreP2() {
        return scoreP2;
    }

    public int getCoin() {
        return coin;
    }

    public int getLivesP1() {
        return livesP1;
    }

    public int getLivesP2() {
        return livesP2;
    }

    public long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getAchievementText() {
        return achievementText;
    }

    public String getHealthPopupText() {
        return healthPopupText;
    }

    public int getLevel(){
        return level;
    }

    public Ship getShipP1() {return this.shipP1;}
    public Ship getShipP2() {return this.shipP2;}


    @Override
    public String toString() {
        return "HUDInfoDTO{" +
                "width=" + width +
                ", height=" + height +
                ", scoreP1=" + scoreP1 +
                ", scoreP2=" + scoreP2 +
                ", coin=" + coin +
                ", livesP1=" + livesP1 +
                ", livesP2=" + livesP2 +
                ", elapsedTimeMillis=" + elapsedTimeMillis +
                ", levelName='" + levelName + '\'' +
                ", achievementText='" + achievementText + '\'' +
                ", healthPopupText='" + healthPopupText + '\'' +
                '}';
    }
}
