package test;

import entity.Entity;
import entity.GameConstant;
import entity.OmegaBoss;
import entity.Ship;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TestModel {
	// Add Entity you want.
	private final OmegaBoss entity;

	// Necessary variables
	private Ship player;
	private final int width;
	private final int height;

	public TestModel(int width, int height) {
		this.width = width;
		this.height = height;
		this.entity = new OmegaBoss(Color.blue, width, height);
		this.player = new Ship(this.width/2, height-20, true);
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
				boolean isUpBorder = player.getPositionY() - player.getSpeed() < GameConstant.STAT_SEPARATION_LINE_HEIGHT;
				if (!isUpBorder) player.moveUp();
				break;
			case "DOWN":
				boolean isDownBorder = player.getPositionY() + player.getHeight() + player.getSpeed() > GameConstant.ITEMS_SEPARATION_LINE_HEIGHT;
				if (!isDownBorder) player.moveDown();
				break;
		}
	}
	public void playerFire(){
		// TODO: Implement player firing logic for test mode.
	}

    public Ship getPlayer() {
        return player;
    }
}
