package com.sciman.main.Characters;

import com.sciman.main.GameLoop;

public class Archer extends GameCharacter{

	public Archer(Party party){
		super(party);
		characterClassName = "Archer";

		setHpAmount(25);
		name = "Archer";

		this.damageBase = 2;
		this.healBase = 0;
		this.critBase = 5;
		
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
		
		System.out.println("Calculating Fitness of Archer " + fitness);
	}

	//
	public void performMove(int move, boolean hidden) {
		switch (move) {
		case 0://Move 1		Fire Bow at dragon
//			System.out.println(name + " shot her bow and dealt " + this.damageBase*3 + " to the Dragon!");
			GameLoop.instance.messageQueue.add(name + " shot her bow and dealt " + this.damageBase*3 + " to the Dragon!");
			myParty.partyDragon.dealDamage(this, this.damageBase*3, null);
			if ((int)(Math.random()*100+1)<=5){		//Critical chance
				GameLoop.instance.messageQueue.add(name + " crit and gave bleed to the Dragon!");
				myParty.partyDragon.dealDamage(this, myParty.partyDragon.bleed, null);
			}
			break;
		case 1://Move 2		Bite		dog bites dragon, applying 1 bleed counter
//			System.out.println(name + "\'s dog bite the dragon and gave the Dragon bleed");
			GameLoop.instance.messageQueue.add(name + "\'s dog bite the dragon and gave the Dragon bleed");
			myParty.partyDragon.bleed++;
			break;
		case 2://Move 3		Hail of Arrows		deal minimal damage, but a 75% chance to repeat
			int temp = 0;
			int count = 0;
			while (temp <= 75)
			{
				GameLoop.instance.messageQueue.add(name + " hailed arrows and dealt " + this.damageBase + " to the Dragon! (" + ++count + ")");
//				System.out.println
				myParty.partyDragon.dealDamage(this, this.damageBase, null);
				if ((int)(Math.random()*100+1)<=critBase){		//Critical chance
					GameLoop.instance.messageQueue.add(name + " crit and gave bleed to the Dragon!");
					myParty.partyDragon.dealDamage(this, myParty.partyDragon.bleed, null);
				}
				temp = (int)(Math.random()*100+1);
			}
			break;
		case 3://Move 4
			break;
		default://Unknown move
			break;
		}

	}




}
