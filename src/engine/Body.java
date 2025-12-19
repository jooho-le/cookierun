package engine;

public class Body {
	private double x;
	private double y;
	private double width;
	private double height;
	private int layer;
	private int mask;

	public Body() {
	}

	public Body(double x, double y, double width, double height, int layer, int mask) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.layer = layer;
		this.mask = mask;
	}

	public boolean intersects(Body other) {
		if (!canCollide(other)) return false;
		return x < other.x + other.width && x + width > other.x && y < other.y + other.height && y + height > other.y;
	}

	private boolean canCollide(Body other) {
		return (this.mask & other.layer) != 0 && (other.mask & this.layer) != 0;
	}

	public void set(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setLayerMask(int layer, int mask) {
		this.layer = layer;
		this.mask = mask;
	}
}
