package com.sciman.main.StatusEffects;

import com.sciman.main.GameLoop;
import com.sciman.main.Characters.GameCharacter;

public class StatusEffectFrost extends StatusEffect {
	
	//Default constructor
		public StatusEffectFrost(int duration) {
			super(duration);
			name = "Frozen";
		}

		@Override
		public void onTurn(GameCharacter target) {
			GameLoop.instance.messageQueue.add(target.getName() + " takes 1 damage from being frozen!");
			GameLoop.instance.damagedSprites.add(target.getSprite());
			target.dealDamage(null,1,null);//Inflict 1 damage, no status effect, no source
			turnDuration--;
		}

		@Override
		public int onAttacked(GameCharacter target, int damageAmount) {
			return damageAmount;//Do nothing
		}

		@Override
		public int onHealed(GameCharacter target, int healAmount) {
			return healAmount;//Do nothing
		}

}
