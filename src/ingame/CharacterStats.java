package ingame;

// 캐릭터별 기본 능력치
public class CharacterStats {
	private String id;
	private int baseSpeed;
	private int baseJump;
	private int baseHealth;

	public CharacterStats() {
	}

	public CharacterStats(String id, int baseSpeed, int baseJump, int baseHealth) {
		this.id = id;
		this.baseSpeed = baseSpeed;
		this.baseJump = baseJump;
		this.baseHealth = baseHealth;
	}

	public String getId() {
		return id;
	}

	public int getBaseSpeed() {
		return baseSpeed;
	}

	public int getBaseJump() {
		return baseJump;
	}

	public int getBaseHealth() {
		return baseHealth;
	}
}
