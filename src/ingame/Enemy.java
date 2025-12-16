package ingame;

// 적 오브젝트 (단순 도형 기반)
public class Enemy {
	private int x;
	private int y;
	private int width;
	private int height;
	private int vx;
	private int vy;
	private EnemyType type;
	private long lastFire = 0L;

	public Enemy() {
	}

	public Enemy(int x, int y, int width, int height, int vx, int vy, EnemyType type) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.vx = vx;
		this.vy = vy;
		this.type = type;
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

	public EnemyType getType() {
		return type;
	}

	public void setType(EnemyType type) {
		this.type = type;
	}

	public long getLastFire() {
		return lastFire;
	}

	public void setLastFire(long lastFire) {
		this.lastFire = lastFire;
	}
}
