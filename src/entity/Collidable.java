package entity;

public interface Collidable {

	/**
	 * Defines how this object should react when a collision occurs.
	 * Each entity implements its own collision behavior.
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
