package entity;

public interface Collidable {

	/** Used for bounding-box collision checks. */
	Entity asEntity();

	/**
	 * Defines how this object should react when a collision occurs.
	 * Each entity implements its own collision behavior.
	 */
	void onCollision(Collidable other, GameModel gameModel);

	// ===== [NEW] Bullet-related collision handlers =====
	default void onHitByPlayerBullet(Bullet bullet, GameModel model) {}
	default void onHitByEnemyBullet(Bullet bullet, GameModel model) {}
	default void onHitByBossBullet(BossBullet bullet, GameModel model) {}

	// ===== [NEW] Entity-to-entity collision handlers =====
	default void onCollideWithShip(Ship ship, GameModel model) {}
	default void onCollideWithEnemyShip(EnemyShip enemy, GameModel model) {}
	default void onCollideWithBoss(BossEntity boss, GameModel model) {}
	default void onCollideWithDropItem(DropItem item, GameModel model) {}

}
