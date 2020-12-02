package com.sciman.Extras;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HealthBar extends Sprite {
	
	public double fillAmount = 1;
	
	public HealthBar(int x, int y, int w) {
		super(x,y,null);
		width = w;
	}
	
	@Override
	public void drawOffset(GraphicsContext gc, int xx, int yy) {
		if (visible) {
			
			gc.setFill(Color.BLACK);
			gc.fillRect(xx+x-1, yy+y-1, width+2, 10);
			
			if (fillAmount < 1) {
				gc.setFill(Color.RED);
				gc.fillRect(x+xx, y+yy, width, 8);
			}
			gc.setFill(Color.LIMEGREEN);
			gc.fillRect(x+xx, y+yy, width*fillAmount, 8);
		}
	}

}
