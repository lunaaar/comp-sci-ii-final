package com.sciman.main;

import java.util.ArrayList;
import java.util.LinkedList;

import com.sciman.Extras.HealthBar;
import com.sciman.Extras.OptionSelector;
import com.sciman.Extras.Sprite;
import com.sciman.main.Characters.Dragon;
import com.sciman.main.Characters.GameCharacter;
import com.sciman.main.Characters.Party;
import com.sciman.main.Characters.Population;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

//This class contains the actual code for running the game.
public class GameLoop extends Task<Void> {
	
	public static GameLoop instance;
	
	GameApp app;
	final int framedelay = 1000/60;
	
	//Selecting
	OptionSelector currentSelector;
	boolean skip = false;
	
	//Game strings
	static String[] moves = new String[] {"Bite","Fire Breath","Claw Storm"};
	static String[] descriptions = new String[] {"Damage a target","Damage a target and set them on fire","Damages enemies chosen at random"};
	static String selectMoveString = "--Select a move--";
	
	//Selectors
	OptionSelector moveSelector, targetSelector, continueSelector;
	
	public LinkedList<String> messageQueue;//Queue to hold messages
	public LinkedList<Sprite> damagedSprites;//List to hold sprites that need to be animated to indicate damage taken
	
	public boolean runGame = true;
	
	Party currentParty;
	Dragon player;
	ArrayList<String> partyMemberNames;
	
	HealthBar dragonHealthBar;
	HealthBar[] partyHealthBars;
	
	public static Image tombstoneImg;
	
	//Constructor
	public GameLoop(GameApp g) {
		app = g;
		messageQueue = new LinkedList<String>();
		damagedSprites = new LinkedList<Sprite>();
		
		instance = this;
		
		tombstoneImg = new Image("/assets/graphics/sprites/tombstone.png");
	}
	
	public void setPartyNames(ArrayList<String> pMN) {
		partyMemberNames = pMN;
	}
	
	//Main loop call
	@Override
	protected Void call() throws Exception {
		
		//create player dragon object
		player = new Dragon();
		
		//Create AI player population
		Population population = new Population(partyMemberNames, 100, player);//TODO add way to get names
		
		//Create sprites
		//Dragon sprite
		Sprite dragonSprite = new Sprite(100,105,new Image("/assets/graphics/sprites/dragon.png"));
		dragonHealthBar = new HealthBar(4,-10,88);
		dragonSprite.subsprites.add(dragonHealthBar);
		app.sprites.add(dragonSprite);
		player.setSprite(dragonSprite);
		//app.sprites.add(dragonHealthBar);
		
		//Party sprites
		Sprite[] partySprites = new Sprite[4];
		partyHealthBars = new HealthBar[4];
		for (int i=0;i<partyMemberNames.size();i++) {
			int x = 330+(i%2) * 110;
			int y = 80+(i/2) * 80;
			//Create sprite and health bar
			partySprites[i] = new Sprite(x,y,new Image("/assets/graphics/sprites/" + partyMemberNames.get(i).toLowerCase() + ".png"));
			partyHealthBars[i] = new HealthBar(4,-10,56);
			
			//Add sprites to the sprite list
			app.sprites.add(partySprites[i]);
			partySprites[i].subsprites.add(partyHealthBars[i]);
			
			//Set sprite
			population.groups[0].group.get(i).setSprite(partySprites[i]);
		}
		
		//Redraw
		app.redraw();
		
		//Get party names as array
		String[] names = new String[partyMemberNames.size()];
		partyMemberNames.toArray(names);
		
		//Create selectors
		moveSelector = new OptionSelector(selectMoveString,moves,descriptions);
		targetSelector = new OptionSelector("--Select a target--",names,new String[4]);
		continueSelector = new OptionSelector("Continue Playing?",new String[] {"Yes","No"},new String[] {"Start next level","Quit game"});
	
		//Runs the loop of the game
		while(runGame) {
				
			if (Thread.interrupted()) break;//Break out of loop
			
			currentParty = population.groups[0];//Get first party, for sake of gameplay
			
			//Set the target party for the dragon
			player.setCurrentParty(currentParty);
			
			//Reset all sprites
			for (int i=0;i<currentParty.group.size();i++) {
				partySprites[i].changeImage(null);
			}
			
			int turncounter = 1;
			//Stops combat if you pass 10 turns, the party dies, or dragon dies.
			
			while (player.isDead() == false && currentParty.dead == false && turncounter <= 10) {
				
				//Clear message queue
				messageQueue.clear();
				
				//Update health bars
				updateHealthDisplay(dragonHealthBar,partyHealthBars);
				
				//TODO fix all this crap
				displayMessage("It is the dragon's turn! [" + turncounter + "/10]\nHealth: " + player.hp + "/" + player.maxHp);
				
				//Select a move
				setSelector(moveSelector);
				waitForSelection();
				int move = currentSelector.getSelection();
				currentSelector = null;
				
				//Select a target
				int target = 0;
				if (move != 2) {//Claw storm doesn't need a target
					//Set the target selector properly
					String[] statusStrings = new String[currentParty.group.size()];
					for (int i=0;i<statusStrings.length;i++) {
						GameCharacter c = currentParty.group.get(i);
						if (c.isDead()) {
							statusStrings[i] = "Dead";
						}else {
							statusStrings[i] = c.hp + "/" + c.maxHp + "hp";
							if (c.statusEffectStack.size() != 0) {
								statusStrings[i] += " - ";
								for (int j=0;j<c.statusEffectStack.size();j++) {
									statusStrings[i] += c.statusEffectStack.get(j).name;
									if (j != c.statusEffectStack.size()-1) statusStrings[i] += ", ";
								}
							}
						}
					}
					targetSelector.setDescriptions(statusStrings);
					
					setSelector(targetSelector);
					waitForSelection();
					target = currentSelector.getSelection();
					currentSelector = null;
				}
				//Perform move
				player.onDragonTurn(move, target);
				//Display all messages in queue
				processMessageQueue();
				
				//Let party do their thing
				currentParty.fight();
				processMessageQueue();
	
				//Increment turn counter
				turncounter++;
			}
			if(turncounter >= 10) {//Message to q if the fighting ended due to turn limit
				displayMessage("The fighting lasted too long and the party fled.");
			}else if(player.isDead()) {
				displayMessage("The dragon has fainted the battle stops.");
			}else if(currentParty.dead){
				displayMessage("The party stood no chance and died fighting.");
			}
			
			//We're done fighting
			currentParty.doneFighting = true;
			
			//Gives all dragons the moves that you made.
			for(Party p : population.groups) {
				p.partyDragon.copy(player);
			}
			
			//Run population methods
			System.out.println("Running hidden fights...");
			while(population.allPartiesDone() == false) {	
				population.update();
				if (Thread.interrupted()) {
					System.out.println("We Broke Out");
					break;
				}
				System.out.println(population.allPartiesDone());
			}
			System.out.println("Running population methods...");
			population.calculateFitness();
			population.naturalSelection();
			population.mutateBabies();
	
			//Add code here to make game stop running if we desire
			setSelector(continueSelector);
			waitForSelection();
			runGame = (currentSelector.getSelection() == 0);
			if (!runGame) break;
			
		}
		displayMessage("Thank you for playing!");
		System.out.println("Exiting game...");
		GameLoop.instance.app.endGame();
		System.exit(0);
		return null;
	}
	
	//Used to wait for the player to make a selection.
	void waitForSelection() throws InterruptedException {
		while (!currentSelector.isLockedIn) {if (Thread.interrupted()) {System.exit(0);} Thread.yield();};
	}
	
	void setSelector(OptionSelector s) {
		currentSelector = s;
		currentSelector.reset();
		//Display a selection
		currentSelector.updateSelection(0, app.description);
	}
	
	//Animations
	//Make a sprite wobble horizontally
	public void anim_wobbleSprite(Sprite spr) throws InterruptedException {
		if (spr == null) return;
		int pos = spr.x;
		for (int i=16;i>=0;i--) {
			spr.x = pos + (i % 2 == 0 ? i : -i);
			app.redraw();
			if (Thread.interrupted()) break;
			Thread.sleep(10);
		}
	}
	
	//Make a sprite flicker horizontally
	public void anim_flickerSprite(Sprite spr) throws InterruptedException {
		if (spr == null) return;
		for (int i=10;i>=0;i--) {
			spr.visible = i % 2 == 0;
			app.redraw();
			if (Thread.interrupted()) break;//ADD THIS LINE TO EVRYTHING THAT USES THREADING OR THE THING WILL CRASH
			Thread.sleep(50);
		}
	}
	
	//Method is called when a key is pressed
	public void onKeyPressed(KeyEvent e) {
		if (currentSelector != null) {
			//Change selection
			if (e.getCode() == KeyCode.UP) {
				currentSelector.updateSelection(-1, app.description);
				if(skip) {skip=false;}
			}else if (e.getCode() == KeyCode.DOWN) {
				currentSelector.updateSelection(1, app.description);
				if(skip) {skip=false;}
			}else if (e.getCode() == KeyCode.ENTER) {
				currentSelector.isLockedIn = true;
				if(skip) {skip=false;}
			}
		}else if (e.getCode() == KeyCode.SHIFT) {
			skip = true;
		}
	}
	
	//Display a message to the game display
	public void displayMessage(String msg, int charDelay) throws InterruptedException {
		app.description.setText("");
		for (int i=0;i<=msg.length();i++) {
			if (Thread.interrupted()) break;
			app.description.setText(msg.substring(0, i));
			Thread.sleep(charDelay);
		}
		if (skip) {
			Thread.sleep(500);
		}
		else {
			Thread.sleep(1000);
		}
		
	}
	//override
	public void displayMessage(String msg) throws Exception {
		if (skip) {
			displayMessage(msg,20);
		}
		else {displayMessage(msg,25);}
	}
	
	//Process all messages in queue
	void processMessageQueue() throws Exception {
		while (!messageQueue.isEmpty()) {
			displayMessage(messageQueue.removeFirst());
			//Is there a sprite to animate?
			if (!damagedSprites.isEmpty()) {
				anim_wobbleSprite(damagedSprites.pop());
				//Update health bars
				updateHealthDisplay(dragonHealthBar,partyHealthBars);
			}
		}
		damagedSprites.clear();
	}
	
	void updateHealthDisplay(HealthBar dragonHB, HealthBar[] partyHB) {
		//Display health for everyone
		dragonHB.fillAmount = player.getHealthAsPercentage();
		for (int i=0;i<currentParty.group.size();i++) {
			partyHB[i].fillAmount = currentParty.group.get(i).getHealthAsPercentage();
		}
		app.redraw();
	}
}
