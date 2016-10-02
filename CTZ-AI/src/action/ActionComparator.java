package action;

import java.util.Comparator;

public class ActionComparator implements Comparator<Action> {
	
	@Override
	public int compare(Action object1, Action object2) {
        return (int)(object1.score - object2.score);
    }
}
