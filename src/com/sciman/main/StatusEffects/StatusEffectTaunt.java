package com.sciman.main.StatusEffects;

import com.sciman.main.Characters.GameCharacter;

public class StatusEffectTaunt extends StatusEffect{

	//Default Constructor
	public StatusEffectTaunt(int duration) {
		super(duration);
		name = "Taunting";
	}

	@Override
	public void onTurn(GameCharacter target) {
		if(!target.dead) {
			target.hasTaunt = true;
		}
		
		//Remove 'taunt' effect
		if (--turnDuration <= 0) {
			target.hasTaunt = false;
		}
	}

	@Override
	public int onAttacked(GameCharacter target, int damageAmount) {
		return damageAmount;
	}

	@Override
	public int onHealed(GameCharacter target, int healAmount) {
		return healAmount;
	}

}
