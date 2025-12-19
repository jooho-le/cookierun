package engine;

public final class CollisionLayer {
	private CollisionLayer() {}

	public static final int PLAYER = 1 << 0;
	public static final int PLATFORM = 1 << 1;
	public static final int ITEM = 1 << 2;
	public static final int ENEMY = 1 << 3;
	public static final int PROJECTILE = 1 << 4;
}
