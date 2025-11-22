package entity;

public interface Collidable {

	/**
	 * Defines the action to be taken when this object collides with another.
	 *
	 * @param other The {@code Collidable} object this object has collided with.
	 */
	void onCollision(Collidable other, GameModel gameModel);

	default void onHitByPlayerBullet(Bullet bullet, GameModel model) {}
	default void onHitByEnemyBullet(Bullet bullet, GameModel model) {}
	default void onHitByBossBullet(BossBullet bullet, GameModel model) {}

	default void onCollideWithShip(Ship ship, GameModel model) {}
	default void onCollideWithEnemyShip(EnemyShip enemy, GameModel model) {}
	default void onCollideWithBoss(BossEntity boss, GameModel model) {}
	default void onCollideWithDropItem(DropItem item, GameModel model) {}

}
