package progress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import util.DebugLog;

// 간단한 직렬화 기반 세이브/로드. 슬롯별 파일 저장.
public class SaveManager {

	private static final String SAVE_DIR = "save";

	public static SaveData load(String slotName, String playerNameIfNew) {
		try {
			Path dir = Path.of(SAVE_DIR);
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			File slot = dir.resolve(slotName + ".dat").toFile();
			if (!slot.exists()) {
				return SaveData.createDefault(playerNameIfNew);
			}
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(slot))) {
				Object obj = ois.readObject();
				if (obj instanceof SaveData) {
					return (SaveData) obj;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SaveData.createDefault(playerNameIfNew);
	}

	public static void save(String slotName, SaveData data) {
		try {
			Path dir = Path.of(SAVE_DIR);
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			File slot = dir.resolve(slotName + ".dat").toFile();
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(slot))) {
				oos.writeObject(data);
				oos.flush();
				DebugLog.add("Saved slot: " + slot.getName());
			}
		} catch (IOException e) {
			DebugLog.add("Save failed: " + e.getMessage());
		}
	}
}
