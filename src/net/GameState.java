package net;

import java.io.Serializable;

// 게임 상태 직렬화용 (간단히 위치/점수/체력/이미지 인덱스)
public class GameState implements Serializable {
	private static final long serialVersionUID = 1L;

	public int seq;
	public int x;
	public int y;
	public int health;
	public int score;
	public int imageIndex;
	public long timestamp;
}
