package ingame;

import java.awt.Image;

// Lombok 제거: 수동 getter/setter 제공
public class Cookie {

	private Image image; // 쿠키 이미지

	// 쿠키의 좌표와 넓이 높이
	private int x = 160;
	private int y = 0;
	private int width = 80;
	private int height = 120;

	// 쿠키의 투명도 (0이 투명, 255가 완전 불투명)
	private int alpha = 255;

	// 쿠키의 체력
	private int health = 1000;

	// 쿠키의 상태 (거대화와 가속화 미구현)
	private int big = 0; // 거대화 남은 시간
	private int fast = 0; // 가속화 남은 시간
	private int countJump = 0; // 점프 횟수
	private boolean invincible = false; // 무적 여부
	private boolean fall = false; // 낙하 여부
	private boolean jump = false; // 점프 여부

	public Cookie() {
	}

	public Cookie(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
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

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getBig() {
		return big;
	}

	public void setBig(int big) {
		this.big = big;
	}

	public int getFast() {
		return fast;
	}

	public void setFast(int fast) {
		this.fast = fast;
	}

	public int getCountJump() {
		return countJump;
	}

	public void setCountJump(int countJump) {
		this.countJump = countJump;
	}

	public boolean isInvincible() {
		return invincible;
	}

	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}

	public boolean isFall() {
		return fall;
	}

	public void setFall(boolean fall) {
		this.fall = fall;
	}

	public boolean isJump() {
		return jump;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}
}
