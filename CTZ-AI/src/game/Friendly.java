package game;

import com.orbischallenge.ctz.Constants;
import com.orbischallenge.ctz.objects.EnemyUnit;
import com.orbischallenge.ctz.objects.FriendlyUnit;
import com.orbischallenge.ctz.objects.World;
import com.orbischallenge.ctz.objects.enums.*;
import com.orbischallenge.game.engine.Point;

import java.util.*;

import action.*;

@SuppressWarnings("unused")
public class Friendly extends AnyUnit {
	public static Friendly[] friendlyUnits = {
		new Friendly(),
		new Friendly(),
		new Friendly(),
		new Friendly()
	};

	public static FriendlyUnit getUnit(int i) {
		return (FriendlyUnit) friendlyUnits[i].unit;
	}
	
	public static void setFriendlyUnits(FriendlyUnit[] units) {
		for(int i = 0; i < 4; i++)
			friendlyUnits[i].setFriendlyUnits(units[i]);
	}
	
	public static void setupActions() {
		for(int i = 0; i < 4; i++)
			friendlyUnits[i].SetupActionInformation(friendlyUnits[i], Enemy.enemyUnits, Friendly.friendlyUnits);		
	}
	
	public static void evaluateActions() {
		for(int i = 0; i < 4; i++)
			friendlyUnits[i].CheckAllActions();
		for(int i = 0; i < 4; i++)
			friendlyUnits[i].EvaluateAllActions();
	}
	
	public void setFriendlyUnits(FriendlyUnit unit) {
		this.unit = unit;
	}
	
	// Checks if the action is valid
	public void CheckAllActions() {
		ResetAllActions();
		
		actions.get("MoveNorth").isValid = (((FriendlyUnit) unit).checkMove(Direction.NORTH) == MoveResult.MOVE_VALID);
		actions.get("MoveSouth").isValid = (((FriendlyUnit) unit).checkMove(Direction.SOUTH) == MoveResult.MOVE_VALID);
		actions.get("MoveEast").isValid = (((FriendlyUnit) unit).checkMove(Direction.EAST) == MoveResult.MOVE_VALID);
		actions.get("MoveWest").isValid = (((FriendlyUnit) unit).checkMove(Direction.WEST) == MoveResult.MOVE_VALID);
		actions.get("MoveNorthEast").isValid = (((FriendlyUnit) unit).checkMove(Direction.NORTH_EAST) == MoveResult.MOVE_VALID);
		actions.get("MoveNorthWest").isValid = (((FriendlyUnit) unit).checkMove(Direction.NORTH_WEST) == MoveResult.MOVE_VALID);
		actions.get("MoveSouthEast").isValid = (((FriendlyUnit) unit).checkMove(Direction.SOUTH_EAST) == MoveResult.MOVE_VALID);
		actions.get("MoveSouthWest").isValid = (((FriendlyUnit) unit).checkMove(Direction.SOUTH_WEST) == MoveResult.MOVE_VALID);
		
		for(int i = 0; i < 4; i++) {
			EnemyUnit enemy = Enemy.getUnit(i);
			if(enemy.getHealth() == 0) continue;
			if(((FriendlyUnit) unit).checkShotAgainstEnemy(enemy) == ShotResult.CAN_HIT_ENEMY) {
				setActionForDirections(actions, Direction.fromTo(((FriendlyUnit) unit).getPosition(), enemy.getPosition()));
			}
		} 
		
		actions.get("PickupObject").isValid = (((FriendlyUnit) unit).checkPickupResult() == PickupResult.PICK_UP_VALID);
		actions.get("ActivateShield").isValid = (((FriendlyUnit) unit).checkShieldActivation() == ActivateShieldResult.SHIELD_ACTIVATION_VALID);
	}
	
	public ShotResult checkShotAgainstEnemy(EnemyUnit enemy) {
		return ((FriendlyUnit) unit).checkShotAgainstEnemy(enemy);
	}
	
}
