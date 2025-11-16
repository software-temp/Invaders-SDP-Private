package screen;

import engine.DrawManager;
import engine.DTO.HUDInfoDTO;
import entity.GameModel;
import entity.LaserBullet;

/**
 * GameView
 * ----------
 * - View 계층 (MVC의 V)
 * - Controller(GameScreen)나 Screen 객체에 의존하지 않음
 * - HUDInfoDTO에서 HUD 데이터를 받고,
 *   Model에서 렌더링할 엔티티 리스트를 직접 받아서 그린다.
 */
public class GameView {

    private final GameModel model;
    private final DrawManager drawManager;

    public GameView(GameModel model, DrawManager drawManager) {
        this.model = model;
        this.drawManager = drawManager;
    }

    public void render(final HUDInfoDTO dto) {

        /** frame initialize */
        drawManager.initDrawing(dto.getWidth(), dto.getHeight());

        /** Entity Rendering */
        if (model.getEntitiesToRender() != null) {
            for (int i = 0; i < model.getEntitiesToRender().size(); i++) {
                var e = model.getEntitiesToRender().get(i);
				drawManager.getEntityRenderer().drawEntity(e);
            }
        }

        drawManager.getHUDRenderer().drawScore(dto.getWidth(), dto.getScoreP1(), 25);
        drawManager.getHUDRenderer().drawScore(dto.getWidth(), dto.getScoreP2(), 40);
        drawManager.getHUDRenderer().drawCoin(dto.getWidth(), dto.getHeight(), dto.getCoin());
        drawManager.getHUDRenderer().drawLivesP1(dto.getLivesP1());
        drawManager.getHUDRenderer().drawLivesP2(dto.getLivesP2());
        drawManager.getHUDRenderer().drawTime(dto.getHeight(), dto.getElapsedTimeMillis());
        drawManager.getHUDRenderer().drawItemsHUD(dto.getWidth());
        drawManager.getHUDRenderer().drawLevel(dto.getHeight(), dto.getLevelName());

        /** draw Line */
        drawManager.getUIRenderer().drawHorizontalLine(dto.getHeight(), GameScreen.SEPARATION_LINE_HEIGHT - 1);
        drawManager.getUIRenderer().drawHorizontalLine(dto.getHeight(), GameScreen.ITEMS_SEPARATION_LINE_HEIGHT);

        /** achievement popup */
        if (dto.getAchievementText() != null && !model.getAchievementPopupCooldown().checkFinished()) {
            drawManager.getHUDRenderer().drawAchievementPopup(dto.getWidth(), dto.getAchievementText());
        }

        /** health popup */
        if (dto.getHealthPopupText() != null && !model.getHealthPopupCooldown().checkFinished()) {
            drawManager.getHUDRenderer().drawHealthPopup(dto.getWidth(), dto.getHealthPopupText());
        }

        /** countdown */
        if (!model.isInputDelayFinished()) {
            int countdown = (int) ((GameModel.INPUT_DELAY
                    - (System.currentTimeMillis() - model.getGameStartTime())) / 1000);

            drawManager.getUIRenderer().drawCountDown(
                    dto.getWidth(),
                    dto.getHeight(),
                    dto.getLevel(),
                    countdown,
                    model.isBonusLife()
            );

            drawManager.getUIRenderer().drawHorizontalLine(dto.getHeight(), dto.getHeight() / 2 - dto.getHeight() / 12);
            drawManager.getUIRenderer().drawHorizontalLine(dto.getHeight(), dto.getHeight() / 2 + dto.getHeight() / 12);
        }

        /** frame complete */
        drawManager.completeDrawing();
    }
}
