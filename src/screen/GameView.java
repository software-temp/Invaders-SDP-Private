package screen;

import java.util.List;
import entity.*;
import engine.DrawManager;

/**
 * Implements the View for the game screen.
 * Responsible for all drawing operations.
 */
public class GameView {

    private GameModel model;
    private DrawManager drawManager;


    public GameView(GameModel model, DrawManager drawManager, int width, int height) {
        this.model = model;
        this.drawManager = drawManager;
    }

    /**
     * Draws the elements associated with the screen.
     */
    /**
     * Draws the elements associated with the screen.
     * The View is now decoupled from the Model's internal structure and only
     */
    public void draw(Screen screen) {
        drawManager.initDrawing(screen);

        List<Entity> entitiesToRender = model.getEntitiesToRender();

        for (Entity entity : entitiesToRender) {
            drawManager.getEntityRenderer().drawEntity(entity, entity.getPositionX(), entity.getPositionY());
        }

        drawManager.getHUDRenderer().drawScoreP1(screen, model.getScoreP1());
        drawManager.getHUDRenderer().drawScoreP2(screen, model.getScoreP2());
        drawManager.getHUDRenderer().drawCoin(screen, model.getCoin());
        drawManager.getHUDRenderer().drawLivesP1(screen, model.getLivesP1());
        drawManager.getHUDRenderer().drawLivesP2(screen, model.getLivesP2());
        drawManager.getHUDRenderer().drawTime(screen, model.getElapsedTime());
        drawManager.getHUDRenderer().drawItemsHUD(screen);
        drawManager.getHUDRenderer().drawLevel(screen, model.getCurrentLevel().getLevelName());
        drawManager.getUIRenderer().drawHorizontalLine(screen, GameScreen.SEPARATION_LINE_HEIGHT - 1);
        drawManager.getUIRenderer().drawHorizontalLine(screen, GameScreen.ITEMS_SEPARATION_LINE_HEIGHT);

        if (model.getAchievementText() != null && !model.getAchievementPopupCooldown().checkFinished()) {
            drawManager.getHUDRenderer().drawAchievementPopup(screen, model.getAchievementText());
        }
        if (model.getHealthPopupText() != null && !model.getHealthPopupCooldown().checkFinished()) {
            drawManager.getHUDRenderer().drawHealthPopup(screen, model.getHealthPopupText());
        }
        if (!model.isInputDelayFinished()) {
            int countdown = (int) ((GameModel.INPUT_DELAY
                    - (System.currentTimeMillis()
                    - model.getGameStartTime())) / 1000);
            drawManager.getUIRenderer().drawCountDown(screen, model.getLevel(), countdown,
                    model.isBonusLife());
            drawManager.getUIRenderer().drawHorizontalLine(screen, screen.getHeight() / 2 - screen.getHeight() / 12);
            drawManager.getUIRenderer().drawHorizontalLine(screen, screen.getHeight() / 2 + screen.getHeight() / 12);
        }

        drawManager.completeDrawing(screen);
    }
}
