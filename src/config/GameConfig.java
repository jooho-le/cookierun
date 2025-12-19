package config;

import java.nio.file.Path;
import java.util.EnumMap;

import ingame.BuffType;

public class GameConfig {
	public double spawnBuff = 0.0025;
	public double spawnPlatform = 0.0018;
	public double spawnEnemy = 0.0015;
	public double healthDrainPerSec = 2.0;
	public EnumMap<BuffType, Long> buffDuration = new EnumMap<>(BuffType.class);

	public static GameConfig load() {
		GameConfig cfg = new GameConfig();
		for (BuffType t : BuffType.values()) {
			cfg.buffDuration.put(t, defaultDuration(t));
		}
		Path p = Path.of("config/game.json");
		if (!p.toFile().exists()) {
			return cfg;
		}
		try {
			String json = ConfigParser.readFile(p);
			cfg.spawnBuff = ConfigParser.readDouble(json, "spawnBuff", cfg.spawnBuff);
			cfg.spawnPlatform = ConfigParser.readDouble(json, "spawnPlatform", cfg.spawnPlatform);
			cfg.spawnEnemy = ConfigParser.readDouble(json, "spawnEnemy", cfg.spawnEnemy);
			cfg.healthDrainPerSec = ConfigParser.readDouble(json, "healthDrainPerSec", cfg.healthDrainPerSec);
			for (BuffType t : BuffType.values()) {
				String key = "buffDuration." + t.name();
				long dur = ConfigParser.readLong(json, key, cfg.buffDuration.get(t));
				cfg.buffDuration.put(t, dur);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cfg;
	}

	private static long defaultDuration(BuffType t) {
		switch (t) {
		case MAGNET:
			return 8000;
		case SHIELD:
			return 9000;
		case SPEED:
			return 7000;
		case GIANT:
			return 6000;
		case DOUBLE_SCORE:
			return 8000;
		case SLOW:
			return 6000;
		default:
			return 7000;
		}
	}
}
