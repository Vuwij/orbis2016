package action;

import java.util.Map;

import utils.DirectionList;

import com.orbischallenge.ctz.objects.ControlPoint;
import com.orbischallenge.ctz.objects.Pickup;
import com.orbischallenge.ctz.utils.*;
import com.orbischallenge.ctz.objects.enums.*;
import com.orbischallenge.game.engine.Point;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;

import game.*;

public class MovementAction extends Action{
	public final int standardDistance = 100;
	
	public Direction direction;
	public Point newPosition;
	
	public MovementAction(String name) {
		super(name);
	}

	@Override
	public void EvaluateScore() {
		System.out.println("  Movement: " + this.name);
		
		try {
			direction = NameToDirection();
			newPosition = currentUnit.unit.getPosition();
			newPosition = direction.movePoint(newPosition);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			score = 0;
			float deltaScore = 0.0f;
			
//			deltaScore = EvaluateCloserToAlly();
//			score += deltaScore;
//			System.out.println("    EvaluateCloserToAlly: " + deltaScore);
//			deltaScore = EvaluateFurtherToAlly();
//			score += deltaScore;
//			System.out.println("    EvaluateFurtherToAlly: " + deltaScore);
			deltaScore = EvaluateCloserToEnemy();
			score += deltaScore * deltaScore;
//			System.out.println("    EvaluateCloserToEnemy: " + deltaScore);
//			deltaScore = EvaluateFurtherToEnemy();
//			score += deltaScore;
//			System.out.println("    EvaluateFurtherToEnemy: " + deltaScore);
//			deltaScore = ConstrainEnemyPosition();
//			score += deltaScore;
//			System.out.println("    ConstrainEnemyPosition: " + deltaScore);
			deltaScore = CloserToWeaponPickup();
			score += deltaScore;
			System.out.println("    CloserToWeaponPickup: " + deltaScore);
			deltaScore = CloserToControlPoint();
			score += deltaScore * deltaScore;
			System.out.println("    CloserToControlPoint: " + deltaScore);
//			deltaScore = MoreRangeInMovement();
//			score += deltaScore;
//			System.out.println("    MoreRangeInMovement: " + deltaScore);
//			deltaScore = MoreRangeInShooting();
//			score += deltaScore;
//			System.out.println("    MoreRangeInShooting: " + deltaScore);
//			score += deltaScore;
//			System.out.println("    Total Score: " + score);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private Direction NameToDirection() throws Exception {
		switch(this.name) {
		case "MoveNorth": return Direction.NORTH;
		case "MoveSouth": return Direction.SOUTH;
		case "MoveEast": return Direction.EAST;
		case "MoveWest": return Direction.WEST;
		case "MoveNorthEast": return Direction.NORTH_EAST;
		case "MoveSouthEast": return Direction.SOUTH_EAST;
		case "MoveNorthWest": return Direction.NORTH_WEST;
		case "MoveSouthWest": return Direction.SOUTH_WEST;
		default : throw new Exception("WTF is this direction:" + this.name);
		}
	}
	
	private int getPositionBonusDelta(Point toPosition) {
		int delta = 0;
		delta += PointUtils.chebyshevDistance(toPosition, newPosition);
		delta -= PointUtils.chebyshevDistance(toPosition, currentUnit.unit.getPosition());
		return delta;
	}
	
	private float getPositionBonusDeltaFactored(Point toPosition) {
		float delta = getPositionBonusDelta(toPosition);
		float distance = PointUtils.chebyshevDistance(toPosition, this.currentUnit.unit.getPosition());
		delta /= (distance * distance);
		return delta;
	}
	
	private float EvaluateCloserToAlly() {
		float totalDistanceToAllies = (float) CustomWorld.maxDistance;
		for(AnyUnit friend : friendlyUnits) {
			if(friend.IsDead()) continue;
			if(friend == this.currentUnit) continue;
			totalDistanceToAllies += getPositionBonusDelta(friend.unit.getPosition());
		}
		
		return (CustomWorld.maxDistance / totalDistanceToAllies) * MovementActionPoints.CloserToAlly;
	}
	
	private float EvaluateFurtherToAlly() {
		float totalDistanceToAllies = (float) CustomWorld.maxDistance;
		for(AnyUnit friend : friendlyUnits) {
			if(friend.IsDead()) continue;
			if(friend == this.currentUnit) continue;
			totalDistanceToAllies += getPositionBonusDelta(friend.unit.getPosition());
		}
		
		return totalDistanceToAllies * MovementActionPoints.FurtherToAlly;
	}
	
	private float EvaluateCloserToEnemy() {
		float totalDistanceToEnemies = (float) CustomWorld.maxDistance;
		
		int enemyCount = 0;
		for(AnyUnit enemy : enemyUnits) {
			if(enemy.IsDead()) continue;
			enemyCount++;
			totalDistanceToEnemies += getPositionBonusDeltaFactored(enemy.unit.getPosition());
		}
		
		float finalScore = (((float)CustomWorld.maxDistance) / totalDistanceToEnemies) * MovementActionPoints.CloserToEnemy / this.currentUnit.unit.getCurrentWeapon().getRange() / (enemyCount * enemyCount * enemyCount * enemyCount * enemyCount);
		
		return finalScore;
	}
	
	private float EvaluateFurtherToEnemy() {
		float totalDistanceToEnemies = (float) CustomWorld.maxDistance;
		for(AnyUnit enemy : enemyUnits) {
			if(enemy.IsDead()) continue;
			totalDistanceToEnemies += getPositionBonusDelta(enemy.unit.getPosition());
		}
		
		return totalDistanceToEnemies * MovementActionPoints.FurtherToEnemy;
	}
	
	//TODO Make this one extremely good
	private float ConstrainEnemyPosition() {
		return 0.0f;
	}
	
	private float ItemBonusCalculator(float pickUpDistance, float enemyDistanceDelta, float weaponDamage, float range, float enemyHealth) throws Exception {
		float bonus = CustomWorld.maxDistance / ((pickUpDistance + 1) * (pickUpDistance + 1));
		// Range is more important when the enemy is closer
		bonus *= (range / (CustomWorld.maxDistance - enemyDistanceDelta));
		bonus *= weaponDamage / enemyHealth;
		
		if(pickUpDistance < 0.0f || pickUpDistance >= 100000) throw new Exception("Invalid Result");
		if(weaponDamage <= 0.0f || weaponDamage >= 100000) throw new Exception("Invalid Result");
		if(range <= 0.0f || range >= 100000) throw new Exception("Invalid Result");
		if(enemyHealth <= 0.0f || enemyHealth >= 100000) throw new Exception("Invalid Result");
		
		
		/*
		System.out.println("        PickUpDistance: " + pickUpDistance);
		System.out.println("        EnemyDistanceDelta: " + (CustomWorld.maxDistance - enemyDistanceDelta));
		System.out.println("        WeaponDamage: " + weaponDamage);
		System.out.println("        Range: " + range);
		System.out.println("        EnemyHealth: " + enemyHealth);
		System.out.println("        Final Bonus: " + bonus);
		*/
		
		return bonus;
	}
	
	/* If you were closer to an enemy, damage is more important
	 * If you were further from an enemy, damage is less important
	 * 
	 * But when you pick up the item, the enemy might have moved
	 * 
	 * You want to move to a single weapon, rather than multiple, so bonuses are important
	 * 
	 * Pickup Distance +
	 * 
	 */
	private float CloserToWeaponPickup() throws Exception {
		float totalPickupBonus = 0.0f;
		// If the enemy can reach the weapon first then it would be a bit less
		for(Pickup pickup : CustomWorld.world.getPickups()) {
			float pickUpDistance = (float) PointUtils.chebyshevDistance(pickup.getPosition(), newPosition);
			
			float enemyDistanceDelta = (float) standardDistance;
			float enemyHealth = 0.0f;
			
			System.out.println("      Pickup: " + pickup.getPickupType().name());
			
			for(AnyUnit enemy : this.enemyUnits) {
				enemyDistanceDelta /= PointUtils.chebyshevDistance(enemy.unit.getPosition(), newPosition);
				enemyHealth += enemy.unit.getHealth();
			}
			
			if(pickup.getPickupType() == PickupType.WEAPON_LASER_RIFLE) {
				int weaponDamage = 8;
				int range = 4;
				totalPickupBonus += ItemBonusCalculator(pickUpDistance, enemyDistanceDelta, weaponDamage, range, enemyHealth);
			}
			else if(pickup.getPickupType() == PickupType.WEAPON_RAIL_GUN) {
				int weaponDamage = 6;
				int range = 10;
				totalPickupBonus += ItemBonusCalculator(pickUpDistance, enemyDistanceDelta, weaponDamage, range, enemyHealth);			
			}
			else if(pickup.getPickupType() == PickupType.WEAPON_SCATTER_GUN) {
				int weaponDamage = 25;
				int range = 2;
				totalPickupBonus += ItemBonusCalculator(pickUpDistance, enemyDistanceDelta, weaponDamage, range, enemyHealth);
			}
			else if(pickup.getPickupType() == PickupType.SHIELD) {
				float weaponDamage = (100 / (float) this.currentUnit.unit.getHealth());
				int range = 5;
				totalPickupBonus += ItemBonusCalculator(pickUpDistance, enemyDistanceDelta, weaponDamage, range, enemyHealth);
			}
			else if(pickup.getPickupType() == PickupType.REPAIR_KIT) {
				float weaponDamage = (30 / (float) this.currentUnit.unit.getHealth());
				int range = 5;
				totalPickupBonus += ItemBonusCalculator(pickUpDistance, enemyDistanceDelta, weaponDamage, range, enemyHealth);
			}
		}
		
		System.out.println("      Final Bonus From Pickups: " + totalPickupBonus);
		return totalPickupBonus * MovementActionPoints.CloserToWeaponPickup;
	}
	
	// Being closer to a control point is more important if you have low score
	private float CloserToControlPoint() {
		float totalControlPointBonus = (float) CustomWorld.maxDistance;
		
		ControlPoint[] controls = CustomWorld.world.getControlPoints();
		for(ControlPoint point : controls) {
			// If there is good control of the control point then end
			float distance = PointUtils.chebyshevDistance(this.currentUnit.unit.getPosition(), point.getPosition());
			
			if(point.getControllingTeam() == this.currentUnit.unit.getTeam()) {
				totalControlPointBonus -= (float) getPositionBonusDelta(point.getPosition()) * 0.9
						 / (distance * distance);
			}
			
			if(point.isMainframe()) {
				// TODO mainFrame is more important when player is almost dead
				totalControlPointBonus += (float) getPositionBonusDelta(point.getPosition()) *
						MovementActionPoints.CloserToMainFrame / MovementActionPoints.CloserToControlPoint
						 / (distance * distance);
			}
			else {
				// TODO control point is more important the the turns are about to end
				totalControlPointBonus += (float) getPositionBonusDelta(point.getPosition()) / (distance * distance);
			}
		}
		
		// TODO if score is low, the control points are more valuable
		return (((float)CustomWorld.maxDistance) / totalControlPointBonus) * MovementActionPoints.CloserToControlPoint;
	}
	
	private float MoreRangeInMovement() {
		int rangeBonus = 0;
		for(Map.Entry<String, Direction> dir : DirectionList.all.entrySet()) {
			Point p = new Point(newPosition);
			p = dir.getValue().movePoint(p);
			if(CustomWorld.world.getTile(p).doesBlockMovement()) rangeBonus -= 10;
		}
		return rangeBonus * MovementActionPoints.MoreRangeInMovement;
	}
	
	private float MoreRangeInShooting() {
		int rangeBonusShooting = 0;
		
		for(Map.Entry<String, Direction> dir : DirectionList.all.entrySet()) {
			rangeBonusShooting += CustomWorld.evaluateRange(dir.getValue(), newPosition);
		}
		
		return rangeBonusShooting * MovementActionPoints.MoreRangeInShooting;
	}
}

/// FOR REFRENCE
//public class MovementActionPoints {
//	public static int CloserToAlly = 100;
//	public static int FurtherToAlly = 100;
//	public static int CloserToEnemy = 100;
//	public static int FurtherToEnemy = 100;
//	public static int ConstrainEnemyPosition = 1000;
//	public static int CloserToWeaponPickup = 1000;
//	public static int CloserToControlPoint = 1000;
//	public static int CloserToMainFrame = 2000;
//}