package progress;

import java.io.Serializable;
import java.time.LocalDate;

public class DailyMission implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String description;
	private int target;
	private int progress;
	private boolean completed;
	private LocalDate date;

	public DailyMission() {
	}

	public DailyMission(String id, String description, int target, LocalDate date) {
		this.id = id;
		this.description = description;
		this.target = target;
		this.progress = 0;
		this.completed = false;
		this.date = date;
	}

	public void addProgress(int amount) {
		if (completed) {
			return;
		}
		this.progress += amount;
		if (this.progress >= target) {
			this.progress = target;
			this.completed = true;
		}
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public int getTarget() {
		return target;
	}

	public int getProgress() {
		return progress;
	}

	public boolean isCompleted() {
		return completed;
	}

	public LocalDate getDate() {
		return date;
	}
}
