package com.sciman.main.Characters;

import java.util.ArrayList;
import java.util.Scanner;
import com.sciman.main.GameLoop;
import com.sciman.main.StatusEffects.StatusEffectBurning;

public class Dragon extends GameCharacter{

	Scanner reader = new Scanner(System.in);
	
	int target;//Which character are we targeting?
	Party currentParty;//What party are we targeting?
	
	public ArrayList<DragonAction> performedMoves;//List of all the moves this dragon has performed
	
	public Dragon(ArrayList<DragonAction> movesList) {
		super(null);//The dragon has no party :(
		setHpAmount(100);
		name = "Dragon";
		performedMoves = movesList;

		this.damageBase = 2;
		this.healBase = 5;
		this.bleed = 1;
		
		characterClassName = "Dragon";
	}
	
	public Dragon(){
		this(new ArrayList<DragonAction>());//Initialize dragon with an empty arraylist
	}
	
	public void setCurrentParty(Party p) {
		currentParty = p;
	}
	
	//Method to fetch the current target of the dragon
	private GameCharacter currentTarget() {
		return currentParty.group.get(target);
	}
	
	public void onDragonTurn(int move, int target) {
		//Get a target
		this.target = target;
		//Actually do the move
		performMove(move,false);
		//Decrement status effects
		onStatusEffectTurn();
	}
	
	@SuppressWarnings("unchecked")
	public void copy(Dragon d) {
		performedMoves = (ArrayList<DragonAction>) d.performedMoves.clone();
	}
	
	//Do all the turns we have recorded, in order
	public void runRecordedTurns(int i) {
//		for(int i = 0; i < performedMoves.size(); i++) {
		target = performedMoves.get(i).target;
		performMove(performedMoves.get(i).move,true);//TODO THIS COMMAND IS THE PROBLEM THAT IS STOPPING THE BACKGROUND FIGHTING
//		}
		
	}
	
	@Override
	public void performMove(int move, boolean hidden) {
		GameCharacter tar = currentTarget();
		switch(move) {
			case 0: //Move 1	Bite
//				System.out.println("Dragon bites " + tar.name + " and dealt " + this.damageBase*6 + " damage!");
				if (!hidden) { 
					GameLoop.instance.messageQueue.add("Dragon bites " + tar.name + " and dealt " + this.damageBase*6 + " damage!");
				}
				tar.dealDamage(this, this.damageBase*6, null);//TODO Fix this damage.
				if(tar.isDead()) {
					if(!hidden) {
						GameLoop.instance.messageQueue.add("Dragon healed himself for " + this.healBase + " health!");
					}
					this.healDamage(this, this.healBase, null);
				}
				break;
			case 1: //Move 2	Fire Breath
//				System.out.println("Dragon spits fire at " + tar.name + " and dealt " + this.damageBase*2 + " damage and burnt them!");
				if (!hidden) { 
					GameLoop.instance.messageQueue.add("Dragon spits fire at " + tar.name + " and dealt " + this.damageBase*2 + " damage and burnt them!");
				}
				tar.dealDamage(this, this.damageBase*2, new StatusEffectBurning(3));
				break;
			case 2: //Move 3	Claw Storm
				//				unlike the other attacks, this attack targets a random enemy.
				GameCharacter lastChar = null;
				
				do {
					//Get character
					GameCharacter clawStormTarget;
					do {
						clawStormTarget = currentParty.group.get((int)(Math.random() * currentParty.group.size()));
					}while (clawStormTarget.isDead() || (lastChar == clawStormTarget && currentParty.group.size() > 1));
					
					if (!hidden) {
						GameLoop.instance.messageQueue.add("Dragon claws up a storm and slashes " + clawStormTarget.name + " for " + this.damageBase*3 + " damage!");
					}
//					System.out.println("Dragon claws up a storm and slashes " + clawStormTarget.name + " for " + this.damageBase*3 + " damage!");
					clawStormTarget.dealDamage(this, this.damageBase*3, null);
					
					lastChar = clawStormTarget;
					
				}while(Math.random() <= .5);//the decimal is the chance of a repeated attack
				break;
			case 3: //Move 4
				break;
			default://Unknown Move
				break;
		}
		performedMoves.add(new DragonAction(move,target));
	}
	
	//Helper method for use in our turn
	/*private int getInt(Scanner input, int min, int max) {
		int result;
		do {
			System.out.print(">");
			while (!input.hasNextInt()) {
				System.out.println("Invalid Entry, enter a number between " + min + " and " + max);
				input.nextLine();
			}
			result = input.nextInt();
		}while (result < min || result >= max);
		return result;
	}*/

	@Override
	public void calculateFitness() {
		// Dragon has no fitness score
	}
	
	public String getName() {
		return "The Dragon";
	}
	
	//Class to hold dragon data
	public class DragonAction {
		public int move, target;
		public DragonAction(int m, int t) {
			this.move = m;
			this.target = t;
		}
	}
}
