package com.sciman.main.Characters;

import com.sciman.main.GameLoop;

public class Cleric extends GameCharacter{

	Party referenceParty;
	public Cleric(Party party){
		super(party);
		referenceParty = party;
		characterClassName = "Cleric";

		setHpAmount(25);
		name = "Cleric";

		this.damageBase = 1;
		this.healBase = 1;
		this.critBase = 0;
		
		brain = new Brain(10);
	}


	//Calculate how well a given Cleric did.
	public void calculateFitness() {
		if(myParty.partyDragon.isDead()) {
			fitness = (float)(1.0/16.0 + 10000.0/(Math.pow(brain.step,2)));
		}else {
			fitness = (float)(Math.pow(totalLifeHealed, 2) - Math.pow(totalDamageTaken, 1.5) + totalDamageDealt);
		}
		
		System.out.println("Calculating Fitness of Cleric " + fitness);
	}

	@Override
	public void performMove(int move, boolean hidden) {
		switch (move) {
		case 0://Move 1		//Some damage for him to do.
			GameLoop.instance.messageQueue.add(name + " used holy wrath and dealt " + this.damageBase + " to the Dragon!");
			myParty.partyDragon.dealDamage(this, this.damageBase, null);
			break;
		case 1://Move 2		//All adventurers heal for 5% of their max HP
			GameLoop.instance.messageQueue.add(name + " healed his party for 5% their health!");
			for (GameCharacter each : myParty.group){
				each.healDamage(this, (each.maxHp/10)/2, null);
			}
			break;
		case 2://Move 3		//Big heal for 1 person.
			int target = (int)(Math.random() * referenceParty.group.size());
			GameLoop.instance.messageQueue.add(name + " used his divine spirit and healed " + myParty.group.get(target).name + " for 15 health!");
			myParty.group.get(target).healDamage(this, this.healBase * 15, null);
			break;
		case 3://Move 4
			GameLoop.instance.messageQueue.add(name + " is tired and performs no action.");
			break;
		default://Unknown move
			break;
		}

	}
}
