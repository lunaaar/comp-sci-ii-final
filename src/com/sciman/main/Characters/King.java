package com.sciman.main.Characters;

public class King extends GameCharacter
{
	public King(Party party){
		super(party);
		characterClassName = "King";

		setHpAmount(25);
		name = "King Tiberius";

		this.damageBase = 2;
		
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
		this.turnStart();	//	I put this here because it will trigger regardless of which move is chosen
		switch (move) {
		case 0://Move 1		Royal Scepter
			System.out.println(name + " bashed the dragon with his Royal Scepter, dealing " + this.damageBase*6 + " damage!");
			myParty.partyDragon.dealDamage(this, this.damageBase*6, null);
			break;
		case 1://Move 2		Liquid Courage		..."just 1 bottle of wine wouldn't hurt," ha
			//	King heals for between 10% and 25% (determined randomly) of his max HP
			this.healDamage(this, (this.maxHp/((int)(Math.random()*15 + 11)))/2, null);
			//	King's move next turn is determined randomly rather than by the AI, cause he's drunk and can't strategy
			this.drunk = true;
			break;
		case 2://Move 3		Royal Decree
			//	All allies have damageBase+1 for their next turn
			//	If this was chosen randomly because of Liquid Courage, All allies have damageBase-1 for their next turn
			if (!(this.drunk))
			{	//All allies have damageBase+1 until the start of King's next move
				this.decree++;
				for (GameCharacter each : myParty.group){
					each.damageBase++;
				}
			}
			else
			{	//All allies have damageBase-1 until the start of King's next move
				this.decree--;
				for (GameCharacter each : myParty.group){
					each.damageBase--;
				}
			}
			break;
		case 3://Move 4		The Crown		PASSIVE
			//	While the King is alive, all allies get damageBase+1 (should probably change later, for now this is simple to implement)		lost on death
				//	Added into the dealDamage function, if the character is killed and was a king then all party members lose the buff.
			//	Once the King is killed, all allies lose 1 hp and 1 maxHp on the start of their turn
				//TODO	this needs to be done
			break;
		default://Unknown move
			break;
		}
	}
	
	public void turnStart() {
		if (this.startTrigger)
		{
			for (GameCharacter each : myParty.group){
				each.damageBase++;
			}
			this.startTrigger = false;
		}
		if (this.decree < 0) {
			this.decree++;
			for (GameCharacter each : myParty.group){
				each.damageBase++;
			}
		}
		else if (this.decree > 0) {
			this.decree--;
			for (GameCharacter each : myParty.group){
				each.damageBase--;
			}
		}
	}
}
