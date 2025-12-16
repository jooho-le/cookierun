package progress;

import java.io.Serializable;

public class Achievement implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String title;
	private String description;
	private int progress;
	private int target;
	private boolean achieved;

	public Achievement() {
	}

	public Achievement(String id, String title, String description, int target) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.target = target;
		this.progress = 0;
		this.achieved = false;
	}

	public void addProgress(int amount) {
		if (achieved) {
			return;
		}
		this.progress += amount;
		if (this.progress >= target) {
			this.progress = target;
			this.achieved = true;
		}
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public int getProgress() {
		return progress;
	}

	public int getTarget() {
		return target;
	}

	public boolean isAchieved() {
		return achieved;
	}
}
