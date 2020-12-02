package com.sciman.main.Characters;

import com.sciman.main.GameLoop;
import com.sciman.main.StatusEffects.StatusEffectBurning;
import com.sciman.main.StatusEffects.StatusEffectFrost;

public class Wizard extends GameCharacter{
	public Wizard(Party party){
		super(party);
		characterClassName = "Wizard";
		
		setHpAmount(25);
		name = "Wizard";
		
		this.damageBase = 2;
		this.healBase = 0;
		this.critBase = 0;
		brain = new Brain(10);
	}

	//Calculate how well a given Wizard did.
	public void calculateFitness() {
		if(myParty.partyDragon.isDead()) {
			fitness = (float)(1.0/16.0 + 10000.0/(Math.pow(brain.step,2)));
		}else {
			fitness = (float)(Math.pow(totalDamageDealt, 2) - Math.pow(totalDamageTaken, 1.5));
		}
		
		System.out.println("Calculating Fitness of Wizard " + fitness);
	}

	@Override
	public void performMove(int move, boolean hidden) {
		switch (move) {
		case 0://Move 1		Fireball
			GameLoop.instance.messageQueue.add(name + " cast Fireball and dealt " + this.damageBase*3 + " damage to the Dragon and burned it!");
			myParty.partyDragon.dealDamage(this, this.damageBase*3, new StatusEffectBurning(3));
			break;
		case 1://Move 2		Staff Attack
			GameLoop.instance.messageQueue.add(name + " attacked with its staff and dealt " + this.damageBase + " damage to the Dragon!");
			myParty.partyDragon.dealDamage(this, this.damageBase, null);
			break;
		case 2://Move 3		Frost Bolt
			GameLoop.instance.messageQueue.add(name + " casts Frost Bolt and dealt " + this.damageBase*3 + " damage to the Dragon and froze it!");
			myParty.partyDragon.dealDamage(this, this.damageBase*3, new StatusEffectFrost(2));//TODO STATUS EFFECT NEEDS WORK	
			break;
		case 3://Move 4		Magic Missile (3 magic burst, 1/10 chance to add effect)
			for (int i=0;i<3;i++) {
				boolean burn = Math.random() >= .1;
				myParty.partyDragon.dealDamage(this, this.damageBase, burn ? null : new StatusEffectBurning(1));
				GameLoop.instance.messageQueue.add(name + " cast Magic Missle (" + (i+1) + ") and dealt " + this.damageBase + " damage to the Dragon!" + (burn ? "The dragon is burnt!" : ""));//TODO Add line for status effects
			}
			break;
		default://Unknown move
			break;
		}
	}
}
