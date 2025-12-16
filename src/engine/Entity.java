package engine;

import java.awt.Graphics2D;
import java.awt.Image;

// 공통 엔티티 베이스 클래스 (위치/크기/속도/이미지)
public class Entity {
	protected double x;
	protected double y;
	protected double width;
	protected double height;
	protected double vx;
	protected double vy;
	protected Image image;

	public Entity() {
	}

	public void update(double dt) {
		x += vx * dt;
		y += vy * dt;
	}

	public void render(Graphics2D g2) {
		if (image != null) {
			g2.drawImage(image, (int) x, (int) y, (int) width, (int) height, null);
		}
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getVx() {
		return vx;
	}

	public void setVx(double vx) {
		this.vx = vx;
	}

	public double getVy() {
		return vy;
	}

	public void setVy(double vy) {
		this.vy = vy;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
