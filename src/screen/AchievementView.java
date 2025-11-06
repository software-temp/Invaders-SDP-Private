package screen;
import engine.Achievement;
import engine.AchievementManager;
import engine.IController;
import entity.IModel;
import screen.Screen;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class AchievementView extends Screen implements IView {
    public AchievementView(int width, int height, int fps){
        super(width, height, fps);
        this.returnCode = 1; // Default return code
    }
    @Override
    public void loadAssets() {

    }

    @Override
    public void draw(IModel achievement) {
    }
    public void draw(List<Achievement> achievements){
        drawManager.initDrawing(this);
        drawManager.drawAchievements(this, achievements);
        drawManager.completeDrawing(this);
    }


    @Override
    public void dispose() {

    }
}

