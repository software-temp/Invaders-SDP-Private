package entity;

import java.util.ArrayList;
import java.util.List;

import engine.DrawManager.SpriteType;
import engine.level.Level;

/**
 * Builds the 2D list of EnemyShips based on Level data.
 * (Builder Pattern)
 */
public class EnemyShipFactory {

	/**
	 * Proportion of C-type ships.
	 */
	private static final double PROPORTION_C = 0.2;
	/**
	 * Proportion of B-type ships.
	 */
	private static final double PROPORTION_B = 0.4;
	/**
	 * Initial position in the x-axis.
	 */
	private static final int INIT_POS_X = 20;
	/**
	 * Initial position in the y-axis.
	 */
	private static final int INIT_POS_Y = 100;
	/**
	 * Distance between ships.
	 */
	private static final int SEPARATION_DISTANCE = 40;

	/**
	 * Creates the 2D list of enemy ships for the formation.
	 *
	 * @param level      The level data.
	 * @param nShipsWide How many ships wide the formation is.
	 * @param nShipsHigh How many ships high the formation is.
	 * @return A 2D list of fully constructed EnemyShip objects.
	 */
	public List<List<EnemyShip>> build(Level level, int nShipsWide, int nShipsHigh) {

		List<List<EnemyShip>> enemyShips = new ArrayList<>();
		for (int i = 0; i < nShipsWide; i++) {
			enemyShips.add(new ArrayList<EnemyShip>());
		}

		final int cells = nShipsWide * nShipsHigh;
		List<SpriteType> spriteQueue = buildLayeredQueueFromLevel(level, nShipsWide, nShipsHigh);
		boolean useQueue = (spriteQueue != null && spriteQueue.size() == cells);
		int qIndex = 0;

		for (int j = 0; j < nShipsWide; j++) {
			List<EnemyShip> column = enemyShips.get(j);
			for (int i = 0; i < nShipsHigh; i++) {
				SpriteType chosen;
				if (useQueue) {
					chosen = spriteQueue.get(qIndex++);
				} else {
					// Fallback
					if (i / (float) nShipsHigh < PROPORTION_C)
						chosen = SpriteType.EnemyShipC1;
					else if (i / (float) nShipsHigh < PROPORTION_B + PROPORTION_C)
						chosen = SpriteType.EnemyShipB1;
					else
						chosen = SpriteType.EnemyShipA1;
				}

				column.add(new EnemyShip(
						(SEPARATION_DISTANCE * j) + INIT_POS_X,
						(SEPARATION_DISTANCE * i) + INIT_POS_Y,
						chosen));
				// shipCount is now managed by the Model, not the builder.
			}
		}
		return enemyShips;
	}

	/**
	 * (This method was moved from EnemyShipFormationModel)
	 * Creates a queue of enemy types based on level settings.
	 */
	private List<SpriteType> buildLayeredQueueFromLevel(final Level level, final int width, final int height) {
		final int cells = width * height;
		List<SpriteType> rowMajor = new ArrayList<>(cells);

		if (level == null || level.getEnemyTypes() == null || level.getEnemyTypes().isEmpty()) {
			return new ArrayList<>(); // empty -> caller will fallback
		}

		int countA = 0, countB = 0, countC = 0;
		for (engine.level.EnemyType t : level.getEnemyTypes()) {
			String kind = (t.getType() == null) ? "enemya" : t.getType().trim().toLowerCase();
			int cnt = Math.max(0, t.getCount());
			switch (kind) {
				case "enemya":
				case "a":
					countA += cnt;
					break;
				case "enemyb":
				case "b":
					countB += cnt;
					break;
				case "enemyc":
				case "c":
					countC += cnt;
					break;
				default:
					countA += cnt;
			}
		}

		int total = countA + countB + countC;
		if (total < cells) countA += (cells - total);


		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (countC > 0) {
					rowMajor.add(SpriteType.EnemyShipC1);
					countC--;
				} else if (countB > 0) {
					rowMajor.add(SpriteType.EnemyShipB1);
					countB--;
				} else if (countA > 0) {
					rowMajor.add(SpriteType.EnemyShipA1);
					countA--;
				} else {
					// Safety pad
					rowMajor.add(SpriteType.EnemyShipA1);
				}
			}
		}

		// Convert to column-major order because constructor consumes by column then row.
		List<SpriteType> columnMajor = new ArrayList<>(cells);
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				columnMajor.add(rowMajor.get(row * width + col));
			}
		}

		// Clamp/pad to exact cells size for safety.
		if (columnMajor.size() > cells) {
			return new ArrayList<>(columnMajor.subList(0, cells));
		}
		while (columnMajor.size() < cells) {
			columnMajor.add(SpriteType.EnemyShipA1);
		}
		return columnMajor;
	}
}