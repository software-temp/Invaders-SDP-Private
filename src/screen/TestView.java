package screen;

import java.util.List;

import engine.DTO.HUDInfoDTO;
import entity.*;
import engine.DrawManager;

/**
 * Implements the View for the game screen.
 * Responsible for all drawing operations.
 */
public class TestView {

	private TestModel model;
	private DrawManager drawManager;


	public TestView(TestModel model, DrawManager drawManager, int width, int height) {
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
	public void render(final HUDInfoDTO dto) {
		drawManager.initDrawing(dto.getWidth(), dto.getHeight());

		if(model.getEntities()!=null){
			for (Entity entity : model.getEntities()) {
				drawManager.getEntityRenderer().drawEntity(entity, entity.getPositionX(), entity.getPositionY());
			}
		}
		drawManager.completeDrawing();
	}
}
