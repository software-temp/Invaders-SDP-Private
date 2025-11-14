package engine.level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LevelManager {

    private List<Level> levels;

    public LevelManager() {
        loadLevels();
    }

    /**
     * Loads the levels from the maps.json resource file.
     */
    private void loadLevels() {
        try (InputStream inputStream = LevelManager.class.getClassLoader().getResourceAsStream("maps/maps.json")) {
            if (inputStream == null) {
                throw new IOException("Cannot find resource file: maps/maps.json");
            }
            
            String jsonContent;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                jsonContent = reader.lines().collect(Collectors.joining("\n"));
            }
            
            this.levels = JsonLoader.parse(jsonContent);

        } catch (Exception e) {
            System.err.println("Failed to load levels from JSON resource: " + e.getMessage());
            System.err.println("Falling back to hardcoded levels.");
            e.printStackTrace();
            
            // If loading from JSON fails, use hardcoded levels as a fallback.
            this.levels = new ArrayList<>();
            this.levels.add(new Level(1, 5, 4, 60, 2000));
            this.levels.add(new Level(2, 5, 5, 50, 2500));
            this.levels.add(new Level(3, 6, 5, 40, 1500));
            this.levels.add(new Level(4, 6, 6, 30, 1500));
            this.levels.add(new Level(5, 7, 6, 20, 1000));
            this.levels.add(new Level(6, 7, 7, 10, 1000));
            this.levels.add(new Level(7, 8, 7, 2, 500));
        }
    }
    
    /**
     * Gets the settings for a specific level.
     *
     * @param levelNumber The level number to get the settings for.
     * @return The Level object for the specified level number, or null if not found.
     */
    public Level getLevel(int levelNumber) {
        if (levels == null) {
            return null;
        }

        for (Level level : levels) {
            if (level.getLevel() == levelNumber) {
                return level;
            }
        }
        return null;
    }

    /**
     * Returns the total number of levels loaded.
     * @return The number of levels.
     */
    public int getNumberOfLevels() {
        if (this.levels == null) {
            return 0;
        }
        return this.levels.size();
    }
}