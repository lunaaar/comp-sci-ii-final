package com.sciman.main.Characters;

import java.util.ArrayList;

public class Population {
	public Party[] groups;

	float fitnessSum;
	int gen = 1;

	int bestGroup = 0;//the index of the best party in the groups[] array

	int minStep = 1000;

	//Constructor that defines all the Parties to have the same characters.
	public Population(ArrayList<String> list, int size, Dragon dragon) {
		groups = new Party[size];
		for (int i = 0; i< size; i++) {
			groups[i] = new Party(list,i==0?dragon:new Dragon());
		}
	}

	//Calls update on all Parties, kills parties that have taken more then the minimum amount of allowed steps.
	public void update() {
		for (int i = 1; i< groups.length; i++) {
			if (groups[i].numTurns > minStep) {//if the dot has already taken more steps than the best dot has taken to reach the goal
				groups[i].dead = true;//then it dead
			}else {
				groups[i].update();
			}
		}
	}

	//Runs the calculateFitness function for all parties witch calculates the fitness for all characters.
	public void calculateFitness() {
		for (int i = 0; i< groups.length; i++) {
			groups[i].calculateFitness();
		}
	}

	//Returns true if all parties are dead. False if not.
	public boolean allPartiesDone() {
		for (int i = 0; i< groups.length; i++) {
			if (!groups[i].doneFighting) { 
				return false;
			}
		}

		return true;
	}

	boolean allCharactersDead() {

		for (int i = 0; i< groups.length; i++) {
			for (GameCharacter c : groups[i].group) {
				if (!c.isDead()) return false;
			}
		}
		return true;
	}

	//Does the natural selection of the next generation.
	public void naturalSelection() {
		Party[] newGroups = new Party[groups.length];//next gen
		setBestParty();
		calculateFitnessSum();

		//the champion lives on 
		newGroups[0] = groups[bestGroup].produceBaby();
		newGroups[0].isBest = true;
		for (int i = 1; i< newGroups.length; i++) {
			//select parent based on fitness
			Party parent = selectParent();

			//get baby from them
			newGroups[i] = parent.produceBaby();
		}

		groups = newGroups.clone();
		gen++;
	}

	//Calculates the Sum of all the fitness scores of a Party.
	void calculateFitnessSum() {
		fitnessSum = 0;
		for (int i = 0; i< groups.length; i++) {
			fitnessSum += groups[i].fitness;
		}
	}

	Party selectParent() {
		double rand = Math.random() * fitnessSum;

		float runningSum = 0;

		for (int i = 0; i< groups.length; i++) {
			runningSum += groups[i].fitness;
			if (runningSum >= rand) {
				return groups[i];
			}
		}

		//should never get to this point

		return null;
	}

	public void mutateBabies() {
		for (int i = 1; i< groups.length; i++) {
			groups[i].mutate();
		}
	}

	void setBestParty() {
		float max = 0;
		int maxIndex = 0;

		for (int i = 0; i< groups.length; i++) {
			if (groups[i].fitness > max) {
				max = groups[i].fitness;
				maxIndex = i;
			}
		}

		bestGroup = maxIndex;

		//if this dot reached the goal then reset the minimum number of steps it takes to get to the goal
		if (groups[bestGroup].killedDragon) {
			minStep = groups[bestGroup].numTurns;
			System.out.printf("step:", minStep);
		}
	}

	void calculateBestParty() {
		int bestIndex = -1;
		float maxFitness = 0;
		//Loop over all parties
		for (int i=0;i<groups.length;i++) {
			//Calculate total party fitness
			groups[i].calculateFitness();
			float fit = groups[i].fitnessSum();
			//Does this have the highest score?
			if (fit >= maxFitness) {
				maxFitness = fit;
				bestIndex = i;
			}
		}
		bestGroup = bestIndex;
	}
}