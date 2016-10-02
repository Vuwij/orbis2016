package action;

import game.AnyUnit;
import game.CustomWorld;

import com.orbischallenge.ctz.objects.Pickup;
import com.orbischallenge.ctz.objects.enums.PickupType;
import com.orbischallenge.ctz.utils.PointUtils;

public class OtherAction extends Action{

	public OtherAction(String name_) {
		super(name_);
	}

	@Override
	public void EvaluateScore() {
		System.out.println("  Movement: " + this.name);
		
		try {
			score = 0;

			if(this.name == "PickupObject") {
				score += EvaluatePickupObjectOrStop();
				System.out.println("    EvaluatePickupObjectOrStop: " + score);
			}
			if(this.name == "ActivateShield") {
				score += EvaluateActivateShield();
				System.out.println("    EvaluateActivateShield: " + score);	
			}

			System.out.println("    Total Score: " + score);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public float EvaluatePickupObjectOrStop() throws Exception {
		Pickup pickUp = null;
		for(Pickup p : CustomWorld.world.getPickups()) {
			if(p.getPosition().getX() == this.currentUnit.unit.getPosition().getX() && 
					p.getPosition().getY() == this.currentUnit.unit.getPosition().getY()) {
				pickUp = p;
				break;
			}
		}
		if(pickUp == null) throw new Exception("Attempted to pick up and failed");
		
		int playerHealth = this.currentUnit.unit.getHealth();
		AnyUnit closestEnemyUnitRange = this.currentUnit.getClosestEnemyInShootingRange();
		AnyUnit closestEnemyUnit = this.currentUnit.getClosestEnemyInRange();
		int closestEnemyUnitRangeHealth = (closestEnemyUnitRange == null) ? 0 : closestEnemyUnitRange.unit.getHealth();
		int closestEnemyUnitHealth = (closestEnemyUnit == null) ? 0 : closestEnemyUnit.unit.getHealth();
		int currentWeaponStrength = this.currentUnit.unit.getCurrentWeapon().getDamage();
		int currentWeaponRange = this.currentUnit.unit.getCurrentWeapon().getRange();
		int closestEnemyDistance = PointUtils.chebyshevDistance(this.currentUnit.unit.getPosition(),closestEnemyUnit.unit.getPosition());
		
		if(pickUp.getPickupType() == PickupType.REPAIR_KIT) {
			
			// Returns 0 if I can kill someone immediately
			
			
			return (playerHealth - closestEnemyUnitHealth + 30) * OtherActionPoints.PickupWeapon;
 		}
		// This is way too complicated
		else if (pickUp.getPickupType() == PickupType.SHIELD) {
			
			// Returns 0 if I can kill someone immediately
			if(closestEnemyDistance <= currentWeaponRange) {
				if(closestEnemyUnitRange != null) {
					if(currentWeaponStrength >= closestEnemyUnitHealth) {
						return 0.0f;
					}
				}
			}
			
			return (playerHealth - closestEnemyUnitHealth + 30) * OtherActionPoints.PickupWeapon;
		}
		// Weapons
		else {
			if(closestEnemyDistance <= currentWeaponRange) {
				if(currentWeaponStrength >= closestEnemyUnitHealth) {
					return 0.0f;
				}
			}
			
			return (playerHealth - closestEnemyUnitHealth + 30) * OtherActionPoints.PickupWeapon;
		}
	}
	
	public float EvaluateActivateShield() {
		int playerHealth = this.currentUnit.unit.getHealth();
		AnyUnit closestEnemyUnitRange = this.currentUnit.getClosestEnemyInShootingRange();
		AnyUnit closestEnemyUnit = this.currentUnit.getClosestEnemyInRange();
		int closestEnemyUnitRangeHealth = closestEnemyUnitRange.unit.getHealth();
		int closestEnemyUnitHealth = closestEnemyUnit.unit.getHealth();
		int currentWeaponStrength = this.currentUnit.unit.getCurrentWeapon().getDamage();
		int currentWeaponRange = this.currentUnit.unit.getCurrentWeapon().getRange();
		int closestEnemyDistance = PointUtils.chebyshevDistance(this.currentUnit.unit.getPosition(),closestEnemyUnitRange.unit.getPosition());
		
		return (playerHealth - closestEnemyUnitHealth + 30) * OtherActionPoints.PickupWeapon;
	}
}
