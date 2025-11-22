package test;

import entity.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestModel {
	// Add Entity you want.
	private final OmegaBoss entity;

	// Necessary variables
	private Ship player;
	private final int width;
	private final int height;

	// 총알 관리 (Ship.shoot()가 자동으로 여기에 추가함)
	private Set<Bullet> bullets;

	// 상단 경계
	private static final int TOP_BOUNDARY = 0;

	public TestModel(int width, int height) {
		this.width = width;
		this.height = height;
		this.player = new Ship(this.width/2, height-20, Color.GREEN);
		this.entity = new OmegaBoss(Color.blue, player);
		// 총알 Set 초기화
		this.bullets = new HashSet<>();
	}

	public void update(){
		updateEntity();

		// 총알 업데이트 (Bullet 클래스가 알아서 위치 이동)
		for (Bullet bullet : bullets) {
			bullet.update();
		}

		// 화면 밖 총알 제거
		// 3. 충돌 처리 (GameModel 로직 재활용)
		checkBulletCollisions();

		// 4. 화면 밖 총알 제거
		cleanBullets();
	}

	public void updateEntity(){
		entity.update();
	}

	public List<Entity> getEntities(){
		List<Entity> renderList = new ArrayList<>();
		renderList.add(entity);
		renderList.add(player);
		renderList.addAll(bullets);  // 총알도 포함
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
				// ⭐ 수정: 상단 경계 - 플레이어 상단이 TOP_BOUNDARY를 넘지 않도록
				boolean isUpBorder = player.getPositionY() - player.getSpeed() < TOP_BOUNDARY;
				if (!isUpBorder) player.moveUp();
				break;

			case "DOWN":
				// ⭐ 수정: 하단 경계 - 플레이어 하단이 화면 하단을 넘지 않도록
				boolean isDownBorder = player.getPositionY() + player.getHeight() + player.getSpeed() > this.height - 1;
				if (!isDownBorder) player.moveDown();
				break;
		}
	}
	/**
	 * 플레이어 발사 처리
	 * Ship 클래스의 shoot() 메서드를 그대로 활용
	 */
	public void playerFire(){
		if (!playerAvailable() || player.isDestroyed()) return;

		// Ship.shoot()가 알아서:
		// 1. 쿨다운 체크
		// 2. Bullet 생성
		// 3. bullets Set에 추가
		// 4. 사운드 재생
		player.shoot(bullets);
	}

	private void checkBulletCollisions() {
		Set<Bullet> recyclable = new HashSet<>();

		for (Bullet bullet : bullets) {
			if (bullet.getSpeed() < 0) {
				if (!entity.isDestroyed() && checkCollision(bullet, entity)) {
					entity.takeDamage(6);
					if (entity.getHealPoint() <= 0) {
						entity.destroy();
					}
					recyclable.add(bullet);
				}
			}
		}

		bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<>();

		for (Bullet bullet : bullets) {
			if (bullet.getPositionY() < TOP_BOUNDARY || bullet.getPositionY() > height) {
				recyclable.add(bullet);
			}
		}

		bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	private boolean checkCollision(final Entity a, final Entity b) {
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;

		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;

		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

}

