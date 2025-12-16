package ingame;

import java.awt.Color;

// 특수 발판 (움직이는/낙하/붕괴/점프패드/레일)
public class SpecialPlatform {
	private int x;
	private int y;
	private int width;
	private int height;
	private int vx;
	private int vy;
	private PlatformType type;
	private boolean triggered = false;
	private long collapseStart = 0L;
	private Color color = new Color(180, 180, 180, 200);

	public SpecialPlatform() {
	}

	public SpecialPlatform(int x, int y, int width, int height, PlatformType type, int vx, int vy, Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
		this.vx = vx;
		this.vy = vy;
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getVx() {
		return vx;
	}

	public void setVx(int vx) {
		this.vx = vx;
	}

	public int getVy() {
		return vy;
	}

	public void setVy(int vy) {
		this.vy = vy;
	}

	public PlatformType getType() {
		return type;
	}

	public void setType(PlatformType type) {
		this.type = type;
	}

	public boolean isTriggered() {
		return triggered;
	}

	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}

	public long getCollapseStart() {
		return collapseStart;
	}

	public void setCollapseStart(long collapseStart) {
		this.collapseStart = collapseStart;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
