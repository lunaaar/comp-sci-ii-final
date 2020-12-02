package com.sciman.Extras;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {
	
	//Sprite position and image
	public int x, y;
	Image image;
	double width, height;
	double scale = 1;
	public boolean visible = true;
	
	public ArrayList<Sprite> subsprites;//Subsprites contains an arraylist of sprites that will be drawn RELATIVE to the parent sprite
	
	Image originalImg;
	
	public Sprite(int x, int y, Image img) {
		setPos(x,y);
		if (img != null) {
			this.image = img;
			originalImg = this.image;
			width = image.getWidth();
			height = image.getHeight();
		}
		subsprites = new ArrayList<Sprite>();
	}
	
	//Set image position
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void changeImage(Image img) {
		if (img == null) {
			this.image = originalImg;
		}else {
			this.image = img;
		}
	}
	
	//Draw sprite to context
	public void draw(GraphicsContext gc) {
		drawOffset(gc,0,0);
	}
	//Draw sprite, offset by an amount
	protected void drawOffset(GraphicsContext gc, int xx, int yy) {
		if (visible) {
			gc.drawImage(image,xx+x,yy+y,width*scale,height*scale);
			for (Sprite s : subsprites) {
				s.drawOffset(gc,x,y);
			}
		}
	}
	
	public void setScale(double s) {
		scale = s;
	}
	
	//Make sprite from file
	public static Sprite createFromImage(String fname) {
		Image img = new Image("/assets/graphics/sprites/"+fname);
		return new Sprite(0,0,img);
	}

}
