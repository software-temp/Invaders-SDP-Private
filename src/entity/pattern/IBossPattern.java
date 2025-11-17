package entity.pattern;

import entity.HasBounds;

import java.awt.*;

public interface IBossPattern {
	void attack();
	void move();
	Point getBossPosition();
	void setTarget(HasBounds target);
}
