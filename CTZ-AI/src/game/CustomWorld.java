package game;

import com.orbischallenge.ctz.objects.World;
import com.orbischallenge.game.engine.Point;
import com.orbischallenge.ctz.objects.enums.Direction;


public class CustomWorld {
	public static World world;
	
	public static boolean map[][];
	public static int height;
	public static int width;
	public static int maxDistance;
	
	public static void setWorld(World world_) {
		world = world_;
		height = world.getHeight();
		width = world.getWidth();
		maxDistance = 4 * CustomWorld.height * CustomWorld.width;
		map = new boolean[width][height];
		
		// Gets the board drawn
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				map[i][j] = world.getTile(new Point(i, j)).doesBlockMovement();
			}
		}
	}
	
	public static int evaluateRange(Direction direction, Point from) {
		int range = 0;
		Point p = new Point(from);
		while(!world.getTile(p).doesBlockMovement()) {
			p = direction.movePoint(p);
			range ++;
		}
		
		return range;
	}
	
}