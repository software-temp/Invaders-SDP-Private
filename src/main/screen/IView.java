package main.screen;

public interface IView {
	/** Loads all resources needed by this view (fonts, images, sounds, etc.). */
	void loadAssets();
	/**
	 * Draws the view's contents on the main.screen.
	 */
	void draw();
	/** Event when this view shows */
	default void onShow() {}
	/** Event when this view hides */
	default void onHide() {}
	/** Dispose resources */
	void dispose();
	/** Get view's width */
	int getWidth();
	/** Get view's height */
	int getHeight();
}
