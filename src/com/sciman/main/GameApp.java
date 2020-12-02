package com.sciman.main;

import java.util.ArrayList;

import com.sciman.Extras.Sprite;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GameApp extends Application {
	
	public GraphicsContext gc;//Context to draw to
	public Text description;//Text at the bottom of the screen
	public Thread gameThread;//Thread to run code looping
	
	GameLoop gl;//Reference to our game loop
	
	public ArrayList<Sprite> sprites;//Arraylist to hold all sprites we need to draw
	Image background;
	
	@Override
	public void start(Stage stage) throws Exception {
		//Build UI
		ArrayList<String> names = new ArrayList<String>();
		HBox characterButtons = new HBox();
		Button archer = new Button("Archer"); Button cleric = new Button("Cleric"); Button warrior  = new Button("Warrior"); Button wizard = new Button("Wizard"); Button done = new Button("Done");
		
		characterButtons.getChildren().add(archer); characterButtons.getChildren().add(cleric); characterButtons.getChildren().add(warrior); characterButtons.getChildren().add(wizard); characterButtons.getChildren().add(done);
		
		Text partyLabel = new Text("Your party consists of: ");
		characterButtons.getChildren().add(partyLabel);
		
		archer.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		        if (names.size() < 4) {
		        	names.add("archer");
		        	partyLabel.setText(partyLabel.getText()+"\nArcher");
		        }
		    }
		});
		
		cleric.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		    	if (names.size() < 4) {
		        	names.add("cleric");
		        	partyLabel.setText(partyLabel.getText()+"\nCleric");
		        }
		    }
		});
		
		warrior.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		    	if (names.size() < 4) {
		        	names.add("warrior");
		        	partyLabel.setText(partyLabel.getText()+"\nWarrior");
		        }
		    }
		});
		
		wizard.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		    	if (names.size() < 4) {
		        	names.add("wizard");
		        	partyLabel.setText(partyLabel.getText()+"\nWizard");
		        }
		    }
		});
		
		characterButtons.setAlignment(Pos.CENTER);
		
		BorderPane characters = new BorderPane();
		characters.setCenter(characterButtons);
		
		Scene partyCreation = new Scene(characters, 640, 480);
		
		//Create canvas
		Canvas canvas = new Canvas(640,320);
		gc = canvas.getGraphicsContext2D();//Get drawing context
		gc.setGlobalBlendMode(BlendMode.SRC_OVER);
		//TEMP used to fill
		sprites = new ArrayList<Sprite>();
		background = new Image("/assets/graphics/sprites/backdrop.png");
		redraw();
		
		//Create description
		description = new Text("UNINITIALIZED");
		description.setWrappingWidth(640);
		description.getStyleClass().add("text-box");
		//Text wrapper for description
		VBox wrapper = new VBox();
		wrapper.setMinHeight(160);
		wrapper.setPadding(new Insets(8));
		wrapper.setAlignment(Pos.TOP_LEFT);
		wrapper.getStyleClass().add("text-box");
		wrapper.getChildren().add(description);
		
		//Put it all together
		BorderPane bp = new BorderPane();
		bp.setTop(canvas);
		bp.setBottom(wrapper);
		
		Scene fightingScene = new Scene(bp,640,480);
		
		//Load CSS
		fightingScene.getStylesheets().add("/assets/graphics/style.css");
		
		//Done button from Party Creation
		done.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		    	if (names.size() == 4) {		    	
			    	gl.setPartyNames(names);
					gameThread.start();
			    	stage.setScene(fightingScene);
		    	}else {
		    		new Alert(AlertType.INFORMATION,"You must have 4 people in your party!",ButtonType.OK).showAndWait();
		    	}
		    }
		});
		
		//TitleScreen
		Label title = new Label("The Quest for the Dragon's Vest");
		
		title.setFont(new Font(40));
		title.setTextAlignment(TextAlignment.CENTER);
		
		HBox buttons = new HBox();
		Button start = new Button("Start");
		Button exit = new Button("Exit");
		
		
		start.setMaxSize(200,48);
		exit.setMaxSize(200, 48);
		start.setStyle("-fx-font-size: 40px;");
		exit.setStyle("-fx-font-size: 40px;");
		
		buttons.getChildren().add(start);
		buttons.getChildren().add(exit);
		buttons.setSpacing(100);
		buttons.setAlignment(Pos.CENTER);
		start.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		       stage.setScene(partyCreation);
		    }
		});
		exit.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		       stage.close();
		    }
		});
		BorderPane tp = new BorderPane();
		tp.setTop(title);
		tp.setCenter(buttons);
		BorderPane.setAlignment(title, Pos.CENTER);
		
		Scene startScene = new Scene(tp, 640, 480);
		
		stage.setScene(startScene);	
		
		//stage.setScene(fightingScene);
		stage.setTitle("The Quest for the Dragon's Vest");
		stage.setResizable(false);
		stage.setOnHidden(e -> {
			//System.out.println("Before methods");
			gl.runGame = false;
			gameThread.interrupt();
			//System.out.println("After methods");
		});//Stop the thread when the window is closed
		stage.show();
		
		//Start game thread
		gl = new GameLoop(this);
		fightingScene.setOnKeyPressed(e -> gl.onKeyPressed(e));
		
		gameThread = new Thread(gl);
	}
	
	//Redraw the screen
	public void redraw() {
		//Draw BG
		gc.drawImage(background, 0, 0);
		//Draw sprites
		for (Sprite spr : sprites) {
			spr.draw(gc);
		}
	}
	
	//End the game
	public void endGame() {
		gameThread.interrupt();
		Platform.exit();
	}
	
	//Code entrypoint
	public static void main(String[] args) {
		launch(args);
	}
	
}
