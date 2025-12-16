package config;

// 단순 설정(볼륨, 키맵)
public class Settings {
	private int bgmVolume = 70;
	private int sfxVolume = 80;
	private int keyJump = java.awt.event.KeyEvent.VK_SPACE;
	private int keySlide = java.awt.event.KeyEvent.VK_DOWN;
	private int keyPause = java.awt.event.KeyEvent.VK_ESCAPE;

	public int getBgmVolume() {
		return bgmVolume;
	}

	public void setBgmVolume(int bgmVolume) {
		this.bgmVolume = bgmVolume;
	}

	public int getSfxVolume() {
		return sfxVolume;
	}

	public void setSfxVolume(int sfxVolume) {
		this.sfxVolume = sfxVolume;
	}

	public int getKeyJump() {
		return keyJump;
	}

	public void setKeyJump(int keyJump) {
		this.keyJump = keyJump;
	}

	public int getKeySlide() {
		return keySlide;
	}

	public void setKeySlide(int keySlide) {
		this.keySlide = keySlide;
	}

	public int getKeyPause() {
		return keyPause;
	}

	public void setKeyPause(int keyPause) {
		this.keyPause = keyPause;
	}
}
