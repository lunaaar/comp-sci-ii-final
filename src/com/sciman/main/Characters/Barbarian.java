package com.sciman.main.Characters;

public class Barbarian extends GameCharacter
{
	public Barbarian(Party party){
		super(party);
		characterClassName = "Barbarian";

		setHpAmount(25);
		name = "Barbarian";

		this.damageBase = 1;
		this.healBase = 5;
		
		brain = new Brain(10);
	}

	//Calculate how well a given Archer did.
	public void calculateFitness() {
		if(myParty.partyDragon.isDead()) {
			//Fitness function for if we've succeeded - optimizes time taken to kill dragon
			fitness = (float)(1.0/16.0 + 10000.0/(Math.pow(brain.step,2)));
		}else {
			//fitness function for if we haven't yet succeeded - optimizes damage dealt
			fitness = (float)(Math.pow(totalDamageDealt, 1.5) + totalDamageTaken);
		}
	}

	//
	public void performMove(int move, boolean hidden) {
		switch (move) {
		case 0://Move 1		Strike the Dragon		with his 2 handed crimson sword
			System.out.println(name + " slammed his heavy blade and dealt " + this.damageBase*6 + " to the Dragon!");
			myParty.partyDragon.dealDamage(this, this.damageBase*6, null);
			this.healDamage(this, this.damageBase*3, null);
			break;
		case 1://Move 2		Rampage!
			System.out.println(name + " is on a RAMPAGE! " + name + " slashed the dragon TWICE for " + this.damageBase*6 + " damage with each swing!");
			myParty.partyDragon.dealDamage(this, this.damageBase*6, null);
			myParty.partyDragon.dealDamage(this, this.damageBase*6, null);
			this.bleed += 2;
			break;
		case 2://Move 3		ENRAGE!
			System.out.println(name + " RAGES!  " + name + " shruggs off some of their wounds, AND will be dealing more damage from now on!");
			this.damageBase++;
			this.healDamage(this, this.healBase, null);
			this.bleed--;
			break;
		case 3://Move 4
			break;
		default://Unknown move
			break;
		}
	}
}
