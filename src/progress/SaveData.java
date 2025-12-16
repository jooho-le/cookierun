package progress;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
		// 기본 업적/미션 예시
		data.achievements.add(new Achievement("a1", "첫 점프", "점프 10번 하기", 10));
		data.achievements.add(new Achievement("a2", "수집가", "젤리 50개 먹기", 50));
		LocalDate today = LocalDate.now();
		data.missions.add(new DailyMission("m1", "오늘 1000m 달리기", 1000, today));
		data.missions.add(new DailyMission("m2", "오늘 젤리 30개 먹기", 30, today));
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
}
