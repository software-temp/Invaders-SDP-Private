package main.screen;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;


/**
 *  Implements the CreditScreen.
 *  main.screen number 8
 */
public class CreditScreen extends Screen {

    private List<Credit> creditList;

    public static class Credit {
        private final int no;
        private final String teamName;
        private final String role;

        /**
         * Initializes Credit objects
         */
        public Credit(final int no, final String teamName, final String role) {
            this.no = no;
            this.teamName = teamName;
            this.role = role;
        }

        // Getter methods
        public int getNo() { return no; }
        public String getTeamName() { return teamName; }

        public String getRole() { return role; }
    }

    /**
     * constructor: Set the main.screen properties and load credit data.
     *
     * @param width Screen width
     * @param height Screen height
     * @param fps Frames per second
     */
    public CreditScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        // When the main.screen closes, it returns to the main menu 1.
        this.returnCode = 1;
        this.creditList = new ArrayList<>();
        loadCredits();
    }

    /**
     * credit information
     */
    private void loadCredits() {
        creditList.add(new Credit(0, "Instructors", "Instruct students"));
        creditList.add(new Credit(1, "Mix&Match", "Currency System"));
        creditList.add(new Credit(2, "C# Only", "Level Design"));
        creditList.add(new Credit(3, "SoundSept", "Sound Effects/BGM"));
        creditList.add(new Credit(4, "MainStream", "Main Menu"));
        creditList.add(new Credit(5, "space_bar", "Records & Achievements System"));
        creditList.add(new Credit(6, "temp", "Ship Variety"));
        creditList.add(new Credit(7, "Team8", "Gameplay HUD"));
        creditList.add(new Credit(8, "KWAK", "Item System"));
        creditList.add(new Credit(9, "ten", "Two-player Mode"));
        creditList.add(new Credit(10, "IET", "Visual effects system"));
    }

    /**
     * Starts the main loop for the main.screen.
     *
     * @return Code to switch to the next main.screen.
     */
    public final int run() {
        super.run();
        return this.returnCode;
    }

    /**
     * Every frame, we update main.screen elements and check for events.
     */
    @Override
    protected final void update() {
        super.update();
        draw();
        // Pressing the spacebar will exit the main.screen.
        if (inputManager.isKeyDown(KeyEvent.VK_SPACE) && this.inputDelay.checkFinished()) {
            this.isRunning = false;
        }
    }

    private void draw() {

        drawManager.initDrawing(this.width,this.height);

        drawManager.getUIRenderer().drawCreditsMenu(this.width,this.height);
        drawManager.getUIRenderer().drawCredits(
                this.width,
                this.height,
                creditList.stream()
                        .map(c -> c.getTeamName() + " - " + c.getRole())
                        .toList()
        );

        drawManager.completeDrawing();
    }
}