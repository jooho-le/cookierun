package config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

// 설정 JSON 로드/저장 (단순 파서)
public class ConfigManager {

	public static Settings load(String path) {
		Settings settings = defaultSettings();
		try {
			Path p = Path.of(path);
			if (!Files.exists(p)) {
				save(path, settings);
				return settings;
			}
			StringBuilder sb = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new FileReader(p.toFile()))) {
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			}
			String json = sb.toString();
			settings.setBgmVolume(readInt(json, "bgm", settings.getBgmVolume()));
			settings.setSfxVolume(readInt(json, "sfx", settings.getSfxVolume()));
			settings.setKeyJump(readInt(json, "keyJump", settings.getKeyJump()));
			settings.setKeySlide(readInt(json, "keySlide", settings.getKeySlide()));
			settings.setKeyPause(readInt(json, "keyPause", settings.getKeyPause()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return settings;
	}

	public static void save(String path, Settings settings) {
		try {
			File f = new File(path);
			File parent = f.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			String json = "{\n" + String.format("  \"bgm\": %d,\n", settings.getBgmVolume())
					+ String.format("  \"sfx\": %d,\n", settings.getSfxVolume())
					+ String.format("  \"keyJump\": %d,\n", settings.getKeyJump())
					+ String.format("  \"keySlide\": %d,\n", settings.getKeySlide())
					+ String.format("  \"keyPause\": %d\n", settings.getKeyPause()) + "}\n";

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
				bw.write(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int readInt(String json, String key, int def) {
		try {
			int idx = json.indexOf("\"" + key + "\"");
			if (idx == -1) {
				return def;
			}
			int colon = json.indexOf(":", idx);
			if (colon == -1) {
				return def;
			}
			int end = json.indexOf(",", colon);
			if (end == -1) {
				end = json.indexOf("}", colon);
			}
			String num = json.substring(colon + 1, end).replaceAll("[^0-9-]", "").trim();
			return Integer.parseInt(num);
		} catch (Exception e) {
			return def;
		}
	}

	private static Settings defaultSettings() {
		return new Settings();
	}
}
