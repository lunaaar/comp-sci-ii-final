package com.sciman.main.StatusEffects;

import com.sciman.main.Characters.GameCharacter;

public abstract class StatusEffect {
	
	public int turnDuration;//How long does the status effect last, in turns
	public String name;
	
	public StatusEffect(int duration) {
		turnDuration = duration;
	}
	
	//Call during turn, will effect the target
	public abstract void onTurn(GameCharacter target);
	
	//Call during an attack - will return a modified damage amount, if applicable. otherwise, just pass through the base damage
	public abstract int onAttacked(GameCharacter target, int damageAmount);
	
	//Call when healed - will return a modified heal amount, if applicable. otherwise, just passthrough the base amount
	public abstract int onHealed(GameCharacter target, int healAmount);

}
