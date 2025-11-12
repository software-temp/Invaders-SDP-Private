package screen;

import engine.DrawManager;
import engine.DTO.HUDInfoDTO;
import entity.GameModel;

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

    /**
     * Controller가 만들어준 DTO를 이용해서 전체 프레임을 렌더링.
     */
    public void render(final HUDInfoDTO dto) {

        // 1️⃣ 프레임 초기화
        drawManager.initDrawing(dto.getWidth(), dto.getHeight());

        // 2️⃣ 게임 월드의 엔티티들 렌더링
        // (기존 구조 유지 — drawEntity 직접 호출)
        // Player, Enemies, Bullets, Boss 등 Model의 엔티티
        if (model.getEntitiesToRender() != null) {
            for (int i = 0; i < model.getEntitiesToRender().size(); i++) {
                var e = model.getEntitiesToRender().get(i);
                drawManager.getEntityRenderer().drawEntity(e, e.getPositionX(), e.getPositionY());
            }
        }

        // 3️⃣ HUD: 점수, 코인, 라이프, 시간, 아이템, 레벨 표시
        drawManager.getHUDRenderer().drawScoreP1(dto.getWidth(), dto.getScoreP1());
        drawManager.getHUDRenderer().drawScoreP2(dto.getWidth(), dto.getScoreP2());
        drawManager.getHUDRenderer().drawCoin(dto.getWidth(), dto.getHeight(), dto.getCoin());
        drawManager.getHUDRenderer().drawLivesP1(dto.getLivesP1());
        drawManager.getHUDRenderer().drawLivesP2(dto.getLivesP2());
        drawManager.getHUDRenderer().drawTime(dto.getHeight(), dto.getElapsedTimeMillis());
        drawManager.getHUDRenderer().drawItemsHUD(dto.getWidth());
        drawManager.getHUDRenderer().drawLevel(dto.getHeight(), dto.getLevelName());

        // 4️⃣ 구분선 (UI)
        drawManager.getUIRenderer().drawHorizontalLine(dto.getHeight(), GameScreen.SEPARATION_LINE_HEIGHT - 1);
        drawManager.getUIRenderer().drawHorizontalLine(dto.getHeight(), GameScreen.ITEMS_SEPARATION_LINE_HEIGHT);

        // 5️⃣ 업적 팝업
        if (dto.getAchievementText() != null && !model.getAchievementPopupCooldown().checkFinished()) {
            drawManager.getHUDRenderer().drawAchievementPopup(dto.getWidth(), dto.getAchievementText());
        }

        // 6️⃣ 체력 변화 팝업
        if (dto.getHealthPopupText() != null && !model.getHealthPopupCooldown().checkFinished()) {
            drawManager.getHUDRenderer().drawHealthPopup(dto.getWidth(), dto.getHealthPopupText());
        }

        // 7️⃣ 시작 카운트다운 표시
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

        // 8️⃣ 프레임 완료
        drawManager.completeDrawing();
    }
}
