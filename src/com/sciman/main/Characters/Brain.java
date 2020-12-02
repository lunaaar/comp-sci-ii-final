package com.sciman.main.Characters;

import java.util.Random;

public class Brain{

	int[] moves;//The moves the character should do, in which order
	int step = 0;//
	Random random = new Random();

	public Brain(int size){
		moves = new int[size];
		randomize();
	}

	void randomize(){
		for(int i = 0; i < moves.length; i++){
			moves[i] =  random.nextInt(3) + 1;
		}
	}

	protected Brain clone() {
		Brain clone = new Brain(moves.length);
		for (int i = 0; i < moves.length; i++) {
			clone.moves[i] = moves[i];
		}

		return clone;
	}

	void mutate() {
		double mutationRate = 0.01;//chance that any vector in directions gets changed
		for (int i =0; i< moves.length; i++) {
			double rand = Math.random();
			if (rand < mutationRate) {
				moves[i] =  random.nextInt(4) + 1;
			}
		}
	}

}