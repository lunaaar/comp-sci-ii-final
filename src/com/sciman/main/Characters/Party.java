package com.sciman.main.Characters;

import java.util.ArrayList;
public class Party {

	public float fitness = 0;

	public boolean dead = false;
	public boolean killedDragon = false;
	public boolean isBest = false;
	public boolean doneFighting = false;

	public Dragon partyDragon;

	public int numTurns = 0;
	int canFight = 0;

	public ArrayList<GameCharacter> group;

	public Party(ArrayList<String> names, Dragon drag){

		partyDragon = drag;
		partyDragon.setCurrentParty(this);
		group = new ArrayList<GameCharacter>();

		for(String s : names) {

			switch (s.toLowerCase()) {
			case "archer":
				group.add(new Archer(this));//Adds archer to party.
				break;

			case "wizard":
				group.add(new Wizard(this));//Adds wizard to party.
				break;

			case "warrior":
				group.add(new Warrior(this));//Adds warrior to party.
				break;

			case "cleric":
				group.add(new Cleric(this));//Adds cleric to party.
				break;

			case "barbarian":
				group.add(new Barbarian(this));//Adds barbarian to party.
				break;
			default:
				System.out.println("Unknown character class " + s);
				break;
			}

		}
	}

	//Fighting for the first party that you see.
	public void fight() {
		int step = 0;
		if(!dead) { //Checks if the party is alive.
			dead = true;
			for(GameCharacter c : group) {
				if(!c.isDead()) {
					c.performMove(c.brain.moves[step],false);
					//Decrement status effect nonsense
					c.onStatusEffectTurn();
					dead = false;//If ANYONE is alive, dead is false
				}
				//System.out.println();
			}
			step++;
		}
	}

	//Carries out the fight behind the scenes.
	static int fightCounter = 1;

	public void fightBehind(int i) {
		System.out.println("Fight Behind Counter = " + fightCounter);
		int turn = 0;
		//Stops combat if you pass 10 turns, the party dies, or dragon dies.
		if(i > 0) {
			while (partyDragon.isDead() == false && this.dead == false && turn < 10) {
//				System.out.println(partyDragon);
				for (GameCharacter c : this.group) {
					if (!c.isDead()) {
//						System.out.println(c);
					}
				}
				//Run the current DRAGONS's method of getting input
				partyDragon.runRecordedTurns(turn);
//				System.out.println("");
				partyDragon.onStatusEffectTurn();
//				System.out.println("");//Space lines out
				//Run the PARTY's fight method
				this.fight();
//				System.out.println("");//Spacing
//				System.out.println(turn + 1);
				turn++;
			}
			
			
			fightCounter++;
		}

	}

	public void move() {
		numTurns++;
		for(GameCharacter c : group) {
			canFight = 0;
			//Checks if we still have moves to make.
			if(c.brain.moves.length > c.brain.step) {
				canFight = 1;
				c.brain.step++;
			} else {
				c.dead = true;
				return;//We shouldn't fight if we're dead
			}
		}
		fightBehind(canFight);
	}

	public void update() {
		if(!dead && !killedDragon && numTurns <= 10) {
			move();
			dead = true;
			for(GameCharacter c : group) {
				if (!c.dead) {
					dead = false;
				}
			}
		}
		doneFighting = dead || partyDragon.isDead() || numTurns > 10;
	}

	//Calculates the Fitness score of all characters in a party.
	public void calculateFitness() {
		for(GameCharacter c : group) {
			c.calculateFitness();
		}
	}

	//Adds up all the fitness scores of a party.
	public float fitnessSum() {
		for(GameCharacter c : group) {
			fitness += c.fitness;
		}
		return fitness;
	}

	//Changes a given percentile of moves in a given party's character's brains.
	public void mutate() {
		for(GameCharacter c : group) {
			c.brain.mutate();
		}
	}

	//Returns a copy of a given Party
	public Party produceBaby(){
		ArrayList<String> babyGroup = new ArrayList<String>();

		for(GameCharacter c : group) {
			babyGroup.add(c.characterClassName);
		}

		Party baby = new Party(babyGroup,new Dragon(partyDragon.performedMoves));
		for(int i = 0; i < group.size(); i++) {
			baby.group.get(i).brain = group.get(i).brain.clone();
		}
		return baby;
	}

}
