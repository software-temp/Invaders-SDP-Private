package entity;

import java.awt.Color;
import engine.DrawManager.SpriteType;

/**
 *  Implements a sound button, to change sound state such as on/off.
 * 
 */
public class SoundButton extends Entity {

    /** State of all Sound on/off */
    private static boolean isSoundOn = true;
    /** Variables to store the number of times the button has been pressed (Using Easter egg)*/
    private int turnOnSound = 0;

    /**
     * Constructor, establishes the button's properties.
     * 
     * @param positionX
     *            Initial position of the button in the X axis.
     * @param positionY
     *            Initial position of the button in the Y axis.
     */

    public SoundButton(final int positionX, final int positionY) {
        super(positionX, positionY, 32, 32, Color.WHITE);

        if (isSoundOn) {
            this.spriteType = SpriteType.SoundOn;
        } else {
            this.spriteType = SpriteType.SoundOff;
        }
    }

    /**
     *  Getter the state of the sound.
     * @return isSoundOn
     */
    public static boolean getIsSoundOn() {
        return isSoundOn;
    }

    /**
     * Change the sound state and the button sprite.
     */
    public void changeSoundState() {

        if (isSoundOn) {
            this.spriteType = SpriteType.SoundOff;
            isSoundOn = false;
        } else {
            this.spriteType = SpriteType.SoundOn;
            isSoundOn = true;
            turnOnSound++;
        }
    }
    /**
     * Determine if you need to switch to the credit screen.
     * @return If you turn on the sound more than five times, it's true, otherwise it's false.
     * (Using Easter egg)
     */
    public boolean isTeamCreditScreenPossible() {
        return this.turnOnSound >= 5;
    }
}