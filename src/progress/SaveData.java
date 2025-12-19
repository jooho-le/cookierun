package progress;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveData implements Serializable {
	private static final long serialVersionUID = 1L;

	private PlayerProfile profile = new PlayerProfile();
	private List<Achievement> achievements = new ArrayList<>();
	private List<DailyMission> missions = new ArrayList<>();
	private List<LeaderboardEntry> leaderboard = new ArrayList<>();

	public SaveData() {
	}

	public static SaveData createDefault(String playerName) {
		SaveData data = new SaveData();
		data.profile.setName(playerName);
		data.achievements.addAll(loadAchievements());
		data.missions.addAll(loadMissions());
		if (data.achievements.isEmpty()) {
			data.achievements.add(new Achievement("a1", "첫 점프", "점프 10번 하기", 10));
			data.achievements.add(new Achievement("a2", "수집가", "젤리 50개 먹기", 50));
		}
		if (data.missions.isEmpty()) {
			LocalDate today = LocalDate.now();
			data.missions.add(new DailyMission("m1", "오늘 1000m 달리기", 1000, today));
			data.missions.add(new DailyMission("m2", "오늘 젤리 30개 먹기", 30, today));
		}
		data.leaderboard.add(new LeaderboardEntry(playerName, 0, LocalDateTime.now()));
		return data;
	}

	public PlayerProfile getProfile() {
		return profile;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public List<DailyMission> getMissions() {
		return missions;
	}

	public List<LeaderboardEntry> getLeaderboard() {
		return leaderboard;
	}

	private static List<Achievement> loadAchievements() {
		List<Achievement> list = new ArrayList<>();
		try {
			String json = config.ConfigParser.readFile(java.nio.file.Path.of("config/achievements.json"));
			Pattern p = Pattern.compile("\\{[^}]*\"id\"\\s*:\\s*\"([^\"]+)\"[^}]*\"title\"\\s*:\\s*\"([^\"]+)\"[^}]*\"description\"\\s*:\\s*\"([^\"]+)\"[^}]*\"target\"\\s*:\\s*([0-9]+)[^}]*\\}");
			Matcher m = p.matcher(json);
			while (m.find()) {
				String id = m.group(1);
				String title = m.group(2);
				String desc = m.group(3);
				int target = Integer.parseInt(m.group(4));
				list.add(new Achievement(id, title, desc, target));
			}
		} catch (Exception ignored) {
		}
		return list;
	}

	private static List<DailyMission> loadMissions() {
		List<DailyMission> list = new ArrayList<>();
		try {
			String json = config.ConfigParser.readFile(java.nio.file.Path.of("config/missions.json"));
			Pattern p = Pattern.compile("\\{[^}]*\"id\"\\s*:\\s*\"([^\"]+)\"[^}]*\"description\"\\s*:\\s*\"([^\"]+)\"[^}]*\"target\"\\s*:\\s*([0-9]+)[^}]*\\}");
			Matcher m = p.matcher(json);
			while (m.find()) {
				String id = m.group(1);
				String desc = m.group(2);
				int target = Integer.parseInt(m.group(3));
				list.add(new DailyMission(id, desc, target, LocalDate.now()));
			}
		} catch (Exception ignored) {
		}
		return list;
	}
}
