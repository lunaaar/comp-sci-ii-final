package com.sciman.main.Characters;

import java.util.ArrayList;

import com.sciman.Extras.Sprite;
import com.sciman.main.GameLoop;
import com.sciman.main.StatusEffects.StatusEffect;
import com.sciman.main.StatusEffects.StatusEffectTaunt;

public abstract class GameCharacter {

	//Name the fallen human
	String name;
	String characterClassName;
	
	Sprite mySprite;
	public Sprite getSprite() {
		return mySprite;
	}

	//Health related vars
	public int hp; //Current Health of Character
	public int maxHp; //Maximum amount of Health a character can have.
	final int minHp = 0; //Minimum amount of Health a character can have.
	public boolean dead = false; //Holds the current status of whether or not character is alive.
	public boolean hasTaunt = false; //Holds the current status of whether or not character has taunt(Damage goes to them).
	//base damage increment for each character
	public int damageBase;	//Medium damage is 3*damageBase		//Heavy damage is 6*damageBase
	public int healBase;
	public int critBase;
	
	//Status Effect related variables				To be changed later
	public int bleed;	//Number of Bleed counters
	public int burning;	//Number of Burn counters
	
	//Some Kingly stuff, mostly trackers for between turn stuff
	public boolean drunk = false;
	public int decree = 0;
	public boolean startTrigger = true;

	//Status effect stack
	public ArrayList<StatusEffect> statusEffectStack;

	//Refrence to our party
	protected Party myParty;

	Brain brain;//The brain for this character, used to determine order of moves
	int nextMove;
	float fitness = 0;

	//Character stats
	public int totalDamageDealt;
	public int totalLifeHealed;
	public int totalDamageTaken;

	//Empty constructor
	public GameCharacter(Party party) {
		//Create initial status effect stack
		statusEffectStack = new ArrayList<StatusEffect>();

		//Set our party
		myParty = party;
	}

	//Set hp
	protected void setHpAmount(int health) {
		maxHp = health;
		hp = maxHp;
	}

	//Runs on a turn, currently only used to decrement status effects
	public void onStatusEffectTurn() {
		for (int i=statusEffectStack.size()-1;i>=0;i--) {
			StatusEffect e = statusEffectStack.get(i);
			e.onTurn(this);
			if (e.turnDuration <= 0) { 
				//System.out.println("Attempted to remove");
				statusEffectStack.remove(e);//Remove expended status effects
			}
		}
	}
	
	public void setSprite(Sprite spr) {
		mySprite = spr;
	}

	//Return true if the action was successful, false if not
	//Makes this player take damage
	public boolean dealDamage(GameCharacter source, int damage, StatusEffect inflictsEffect) {
		//Is the damage coming from the dragon, AND we have a party, AND we don't have taunt....
		if (source instanceof Dragon && myParty != null && !hasTaunt) {
			//Search for a party member with taunt
			for (GameCharacter c : myParty.group) {
				for (StatusEffect e : c.statusEffectStack) {
					if (e instanceof StatusEffectTaunt && source.equals(myParty.partyDragon)) { //Makes sure the attacking character is the dragon.
						//Deal damage to new target
						//System.out.println("... But the damage went to " + c.name + ", who was taunting!");
						GameLoop.instance.messageQueue.add("... But the damage went to " + c.name + ", who was taunting!");
						c.dealDamage(this,damage,inflictsEffect);
						//c.hasTaunt = false; //Resets the taunt value for that character.
						return false;//Don't deal damage to us
					}
				}
			}
		}

		//First, try and modify damage
		for (StatusEffect effect : statusEffectStack) {

			damage = effect.onAttacked(this, damage);
			if (inflictsEffect != null && effect.getClass().equals(inflictsEffect.getClass())) {
				//If we already have the effect we're trying to inflict, then just rest the timer
				effect.turnDuration = inflictsEffect.turnDuration;
				inflictsEffect = null;
			}
		}

		//Apply a new status effect
		if (inflictsEffect != null) {
			statusEffectStack.add(inflictsEffect);
		}

		//Decrement the target hp, without going beyond their min

		if (!isDead()) {
			hp = Math.max(hp-damage,minHp);
			totalDamageTaken += damage;//Increment the total damage taken by the target, for training purposes
			if (source != null) source.totalDamageDealt += damage;//Increment the total damage dealt by this character, for training purposes
			
			GameLoop.instance.damagedSprites.add(mySprite);
			
			//Did we die?
			if (hp <= minHp) {
//				System.out.println(name + " has died!");
				GameLoop.instance.messageQueue.add(name + " has died!");
				//Change sprite
				if (mySprite != null) mySprite.changeImage(GameLoop.tombstoneImg);
				//Clear all status effects
				statusEffectStack.clear();
				hasTaunt = false;
				bleed = 0;
				burning = 0;
				drunk = false;
				decree = 0;
				if (name.substring(0,4).equals("King")) {		//if the killed person was the king, then the party loses their king buffs
					for (GameCharacter each : myParty.group){
					each.damageBase--;
					}
				}
			}
			return true;
		}
		return false;
	}

	//Returns true if the action was successful, false if not
	boolean healDamage(GameCharacter source, int heal, StatusEffect inflictsEffect) {

		//First, try and modify damage
		for (StatusEffect effect : statusEffectStack) {
			heal = effect.onAttacked(this, heal);
			if (inflictsEffect != null && effect.getClass().equals(inflictsEffect.getClass())) {
				//If we already have the effect we're trying to inflict, then just rest the timer
				effect.turnDuration = inflictsEffect.turnDuration;
				inflictsEffect = null;
			}
		}

		//Apply a new status effect
		if (inflictsEffect != null) {
			statusEffectStack.add(inflictsEffect);
		}

		//Add to the target health, without going above their max hp, assuming they aren't dead
		if (!isDead()) {
			hp = Math.min(hp+heal,maxHp);
			if (source != null) source.totalLifeHealed += heal;
			return true;
		}
		return false;
	}

	//Returns health as a percentage, for ai purposes and crap
	public double getHealthAsPercentage() {
		return ((double)hp)/maxHp;
	}

	//Is the character dead?
	public boolean isDead() {
		if(hp <= minHp) {
			dead = true;
		}else {
			dead = false;
		}
		return dead;
	}

	//Fitness function
	public abstract void calculateFitness();

	//Move function
	//This function takes a move index in as it's parameter, and as a result does something
	public abstract void performMove(int move, boolean hidden);

	//ToString, displays health and status effects
	public String toString() {
		String result = characterClassName + ": ";
		//Display health
		result += (hp + "/" + maxHp + " hp");
		//Display status effects
		if (statusEffectStack.size() != 0) {
			result += " (";
			for (int i=0;i<statusEffectStack.size();i++) {
				StatusEffect e = statusEffectStack.get(i);
				result += (e.name + " [" + e.turnDuration + "]" + (i == statusEffectStack.size()-1 ? ")" : ", "));
			}
		}
		return result;
	}
	
	public String getName() {
		return name;
	}
}
