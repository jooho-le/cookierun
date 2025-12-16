package engine;

public class PhysicsUtil {

	// AABB 충돌 체크
	public static boolean intersects(double x1, double y1, double w1, double h1, double x2, double y2, double w2,
			double h2) {
		return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
	}

	public static double clamp(double v, double min, double max) {
		return Math.max(min, Math.min(max, v));
	}

	public static double approach(double current, double target, double delta) {
		if (current < target) {
			return Math.min(current + delta, target);
		}
		return Math.max(current - delta, target);
	}
}
