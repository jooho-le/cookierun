package ingame;

// 버프/디버프 종류 정의
public enum BuffType {
	MAGNET, // 젤리를 끌어당김
	SHIELD, // 1회 피격 무효
	SPEED, // 속도 상승
	GIANT, // 거대화 (장애물 파괴)
	DOUBLE_SCORE, // 점수 2배
	SLOW, // 속도 저하 디버프
	REVERSE_GRAVITY // (추후 확장) 역중력 디버프
}
