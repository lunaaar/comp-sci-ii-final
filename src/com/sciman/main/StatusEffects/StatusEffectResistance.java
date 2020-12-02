package com.sciman.main.StatusEffects;

import com.sciman.main.Characters.GameCharacter;

public class StatusEffectResistance extends StatusEffect {

	public StatusEffectResistance(int duration) {
		super(duration);
		name = "Resistance";
	}

	@Override
	public void onTurn(GameCharacter target) {
	}

	@Override
	public int onAttacked(GameCharacter target, int damageAmount) {
		return Math.max(damageAmount-3,0);
	}

	@Override
	public int onHealed(GameCharacter target, int healAmount) {
		return healAmount;
	}

}
