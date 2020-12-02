package com.sciman.main.Characters;

import com.sciman.main.GameLoop;
import com.sciman.main.StatusEffects.StatusEffectResistance;
import com.sciman.main.StatusEffects.StatusEffectTaunt;

public class Warrior extends GameCharacter{
	
	public Warrior(Party party){
		super(party);
		characterClassName = "Warrior";

		setHpAmount(25);
		name = "Warrior";

		this.damageBase = 2;
		this.healBase = 0;
		this.critBase = 0;
		
		brain = new Brain(10);
				
		//Give ourselves the 'resistance' status effect
		statusEffectStack.add(new StatusEffectResistance(brain.moves.length));
	}

	//Calculate how well a given Warrior did.
	public void calculateFitness() {
		if(myParty.partyDragon.isDead()) {
			fitness = (float)(1.0/16.0 + 10000.0/(Math.pow(brain.step,2)));
		}else {
			fitness = (float)(Math.pow(totalDamageDealt, 1.5) + totalDamageTaken);
		}
		
		System.out.println("Calculating Fitness of Wizard " + fitness);
	}

	@Override
	public void performMove(int move, boolean hidden) {
		switch (move) {
		case 0://Move 1			Powerful sword strike
			GameLoop.instance.messageQueue.add(name + " did a powerful strike and did " + this.damageBase*6 + " damage to the Dragon!");
			myParty.partyDragon.dealDamage(this, this.damageBase*6, null);
			break;
		case 1://Move 2			Shield Bash
			GameLoop.instance.messageQueue.add(name + " did a shield bash and did " + this.damageBase + " damage to the Dragon!");
			myParty.partyDragon.dealDamage(this, this.damageBase, null);
			
			//Give Warrior the status effect "Taunt"
			statusEffectStack.add(new StatusEffectTaunt(2));//TODO fix taunt.
			
			GameLoop.instance.messageQueue.add(name + " now has taunt!");
			break;
		case 2://Move 3			Flurry of 3 sword strikes
			GameLoop.instance.messageQueue.add(name + " did a flury of 3 strikes to the Dragon!");
			for (int i = 1; i < 3; i++){
				GameLoop.instance.messageQueue.add("--" + name + " did " + this.damageBase*i + " damage to the Dragon!");
				myParty.partyDragon.dealDamage(this, this.damageBase * i, null);
				
			}
			break;
		case 3://Move 4 Does nothing for now. //TODO figure out if we want this to be anything.
			GameLoop.instance.messageQueue.add(name + " is tired and does nothing");
			break;
		default://Unknown move
			break;
		}
	}
}
