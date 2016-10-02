package game;

import java.util.*;

import utils.DirectionList;
import action.ActionList;

import com.orbischallenge.ctz.Constants;
import com.orbischallenge.ctz.objects.*;
import com.orbischallenge.ctz.objects.enums.*;
import com.orbischallenge.game.engine.Point;

@SuppressWarnings("unused")
public class Enemy extends AnyUnit {
	public static Enemy[] enemyUnits = {
		new Enemy(),
		new Enemy(),
		new Enemy(),
		new Enemy()
	};

	public static void setEnemyUnits(EnemyUnit[] units) {
		for(int i = 0; i < 4; i++)
			enemyUnits[i].setEnemyUnit(units[i]);
	}
	
	public static void setupActions() {
		for(int i = 0; i < 4; i++)
			enemyUnits[i].SetupActionInformation(enemyUnits[i], Friendly.friendlyUnits, Enemy.enemyUnits);
	}
	
	public static void evaluateActions() {
		for(int i = 0; i < 4; i++)
			enemyUnits[i].CheckAllActions();
		for(int i = 0; i < 4; i++)
			enemyUnits[i].EvaluateAllActions();
	}
	
	public static EnemyUnit getUnit(int i) {
		return (EnemyUnit) enemyUnits[i].unit;
	}
	
	public void setEnemyUnit(EnemyUnit enemyUnit) {
		this.unit = enemyUnit;
	}
	
	@Override
	public void CheckAllActions() {
		ResetAllActions();
		
		for(Map.Entry<String, Direction> dir : DirectionList.all.entrySet()) {
			Point enemyPosition = unit.getPosition();
			dir.getValue().movePoint(enemyPosition);
			actions.get("Move" + dir.getKey()).isValid = CustomWorld.world.getTile(enemyPosition).doesBlockMovement();
		}
		
		for(int i = 0; i < 4; i++) {
			FriendlyUnit friendly = Friendly.getUnit(i);
			if(friendly.getHealth() == 0) continue;
			if(friendly.checkShotAgainstEnemy((EnemyUnit) unit) == ShotResult.CAN_HIT_ENEMY) {
				setActionForDirections(actions, Direction.fromTo(unit.getPosition(), friendly.getPosition()));
			}
		}
		
		Point enemyPosition = unit.getPosition();
		for(Pickup p : CustomWorld.world.getPickups()) {
			if(enemyPosition == p.getPosition()) actions.get("PickupObject").isValid = true;
		}
		
		actions.get("ActivateShield").isValid = (((EnemyUnit) unit).getNumShields() > 0);

			
	}
	
	public ShotResult checkShotAgainstEnemy(FriendlyUnit friendly) {
		return (friendly.checkShotAgainstEnemy((EnemyUnit) unit));
	}
}
