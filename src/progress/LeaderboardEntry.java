package progress;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LeaderboardEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private String playerName;
	private int score;
	private LocalDateTime timestamp;

	public LeaderboardEntry() {
	}

	public LeaderboardEntry(String playerName, int score, LocalDateTime timestamp) {
		this.playerName = playerName;
		this.score = score;
		this.timestamp = timestamp;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getScore() {
		return score;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
