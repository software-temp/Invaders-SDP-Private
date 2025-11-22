package screen;

import engine.DrawManager;
import engine.DTO.HUDInfoDTO;
import entity.DropItem;
import entity.GameModel;
import entity.LaserBullet;
import entity.pattern.ApocalypseAttackPattern;
import entity.GameConstant;

/**
 * GameView
 * ----------
 * - View layer (the V in MVC)
 * - Does not depend on the Controller (GameScreen) or any Screen objects
 * - Receives HUD data from HUDInfoDTO
 *   and directly gets the list of entities to render from the Model
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

                if (e instanceof DropItem) {
                    drawManager.getItemRenderer().render((DropItem) e);
                    continue;
                }

                // Draw OmegaBoss Warning / Attack
                if (e == model.getOmegaBoss() && model.getOmegaBoss() != null) {
                    // Get the pattern object.
                    ApocalypseAttackPattern pattern = model.getOmegaBoss().getApocalypsePattern();

                    if (pattern != null) {
                        if (pattern.isWarningActive()) {
                            // State 1: Warning
                            drawManager.getUIRenderer().drawApocalypseWarning(
                                    dto.getWidth(),
                                    dto.getHeight(),
                                    pattern.getSafeZoneColumn()
                            );
                        } else if (pattern.isAttacking()) {
                            // State 2: Attack Animation
                            drawManager.getUIRenderer().drawApocalypseAttack(
                                    dto.getWidth(),
                                    dto.getHeight(),
                                    pattern.getSafeZoneColumn(),
                                    pattern.getAttackAnimationProgress() // Pass the progress
                            );
                        }
                    }
                }
                drawManager.getEntityRenderer().drawEntity(e, e.getPositionX(), e.getPositionY());
            }
        }

        drawManager.getHUDRenderer().drawScore(dto.getWidth(), dto.getScoreP1(), 25);
        drawManager.getHUDRenderer().drawScore(dto.getWidth(), dto.getScoreP2(), 50);
        drawManager.getHUDRenderer().drawCoin(dto.getWidth(), dto.getHeight(), dto.getCoin());
        drawManager.getHUDRenderer().drawLivesP1(dto.getLivesP1());
        drawManager.getHUDRenderer().drawLivesP2(dto.getLivesP2());
        drawManager.getHUDRenderer().drawTime(GameConstant.ITEMS_SEPARATION_LINE_HEIGHT, dto.getElapsedTimeMillis());
        drawManager.getHUDRenderer().drawItemsHUD(dto.getWidth(), dto.getHeight());
        drawManager.getHUDRenderer().drawLevel(GameConstant.ITEMS_SEPARATION_LINE_HEIGHT, dto.getLevelName());

        /** draw Line */
        drawManager.getUIRenderer().drawHorizontalLine(dto.getWidth(), GameConstant.STAT_SEPARATION_LINE_HEIGHT - 1);
        drawManager.getUIRenderer().drawHorizontalLine(dto.getWidth(), GameConstant.ITEMS_SEPARATION_LINE_HEIGHT);

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

            drawManager.getUIRenderer().drawHorizontalLine(dto.getWidth(), dto.getHeight() / 2 - dto.getHeight() / 12);
            drawManager.getUIRenderer().drawHorizontalLine(dto.getWidth(), dto.getHeight() / 2 + dto.getHeight() / 12);
        }

        /** frame complete */
        drawManager.completeDrawing();
    }
}
