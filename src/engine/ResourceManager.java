package engine;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

// 이미지/상수 매핑을 캐싱하는 간단한 리소스 매니저
public class ResourceManager {
	private static final Map<String, ImageIcon> iconCache = new HashMap<>();

	public static ImageIcon getIcon(String path) {
		return iconCache.computeIfAbsent(path, p -> new ImageIcon(p));
	}

	// 맵 색상 → 타입 매핑 테이블 예시
	public static final int COLOR_JELLY1 = 16776960;
	public static final int COLOR_JELLY2 = 13158400;
	public static final int COLOR_JELLY3 = 9868800;
	public static final int COLOR_HP = 16737280;
	public static final int COLOR_FLOOR = 0;
	public static final int COLOR_FLOATING = 6579300;
	public static final int COLOR_TACLE1 = 16711680;
	public static final int COLOR_TACLE2 = 16711830;
	public static final int COLOR_TACLE3 = 16711935;
}
