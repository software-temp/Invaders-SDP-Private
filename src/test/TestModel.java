package test;

import main.entity.Entity;
import main.entity.OmegaBoss;
import main.entity.Ship;
import main.screen.GameScreen;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TestModel {
	// Add Entity you want.
	private final OmegaBoss entity;

	// Necessary variables
	private Ship player;
	private final int width;

	public TestModel(int width) {
		this.width = width;
		this.entity = new OmegaBoss(Color.blue, width, 400);
		this.player = new Ship(this.width/2, GameScreen.ITEMS_SEPARATION_LINE_HEIGHT-20, Color.GREEN);
	}

	public void update(){
		updateEntity();
	}

	public void updateEntity(){
		entity.update();
	}

	public List<Entity> getEntities(){
		List<Entity> renderList = new ArrayList<>();
		renderList.add(entity);
		renderList.add(player);
		return renderList;
	}

	public boolean playerAvailable(){
		return player != null;
	}
	/**
	 * Processes a player move command received from the Controller.
	 * (Includes boundary checking logic)
	 * @param direction ("RIGHT", "LEFT", "UP", "DOWN")
	 */
	public void playerMove(String direction) {
		// If the ship doesn't exist or is destroyed, do nothing
		if (!playerAvailable()) return;

		// Boundary logic brought over from the original processPlayerInput
		switch (direction) {
			case "RIGHT":
				boolean isRightBorder = player.getPositionX() + player.getWidth() + player.getSpeed() > this.width - 1;
				if (!isRightBorder) player.moveRight();
				break;
			case "LEFT":
				boolean isLeftBorder = player.getPositionX() - player.getSpeed() < 1;
				if (!isLeftBorder) player.moveLeft();
				break;
			case "UP":
				boolean isUpBorder = player.getPositionY() - player.getSpeed() < GameScreen.SEPARATION_LINE_HEIGHT;
				if (!isUpBorder) player.moveUp();
				break;
			case "DOWN":
				boolean isDownBorder = player.getPositionY() + player.getHeight() + player.getSpeed() > GameScreen.ITEMS_SEPARATION_LINE_HEIGHT;
				if (!isDownBorder) player.moveDown();
				break;
		}
	}
	public void playerFire(){
		// TODO: Implement player firing logic for test mode.
	}
}
