package utils;

import java.util.HashMap;

import com.orbischallenge.ctz.objects.enums.Direction;

public class DirectionList extends HashMap<String, Direction> {
	private static final long serialVersionUID = 5602530079261492340L;
	public static DirectionList all = new DirectionList();
	
	public DirectionList() {
		this.put("East", Direction.EAST);
		this.put("North", Direction.NORTH);
		this.put("South", Direction.SOUTH);
		this.put("West", Direction.WEST);
		this.put("NorthWest", Direction.NORTH_WEST);
		this.put("NorthEast", Direction.NORTH_EAST);
		this.put("SouthEast", Direction.SOUTH_EAST);
		this.put("SouthWest",Direction.SOUTH_WEST);	
	}
	
}