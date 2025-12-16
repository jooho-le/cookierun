package progress;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class PlayerProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name = "Player";
	private int speedLevel = 1;
	private int jumpLevel = 1;
	private int healthLevel = 1;
	private int coins = 0;
	private String selectedSkin = "default";
	private Set<String> unlockedSkins = new HashSet<>();

	public PlayerProfile() {
		unlockedSkins.add("default");
	}

	public void addCoins(int amount) {
		this.coins += amount;
	}

	public boolean upgradeSpeed() {
		int cost = 100 * speedLevel;
		if (coins >= cost) {
			coins -= cost;
			speedLevel += 1;
			return true;
		}
		return false;
	}

	public boolean upgradeJump() {
		int cost = 100 * jumpLevel;
		if (coins >= cost) {
			coins -= cost;
			jumpLevel += 1;
			return true;
		}
		return false;
	}

	public boolean upgradeHealth() {
		int cost = 100 * healthLevel;
		if (coins >= cost) {
			coins -= cost;
			healthLevel += 1;
			return true;
		}
		return false;
	}

	public void unlockSkin(String skinId) {
		unlockedSkins.add(skinId);
	}

	public boolean selectSkin(String skinId) {
		if (unlockedSkins.contains(skinId)) {
			selectedSkin = skinId;
			return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSpeedLevel() {
		return speedLevel;
	}

	public int getJumpLevel() {
		return jumpLevel;
	}

	public int getHealthLevel() {
		return healthLevel;
	}

	public int getCoins() {
		return coins;
	}

	public String getSelectedSkin() {
		return selectedSkin;
	}

	public Set<String> getUnlockedSkins() {
		return unlockedSkins;
	}
}
