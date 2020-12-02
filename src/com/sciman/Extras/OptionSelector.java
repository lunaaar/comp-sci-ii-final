package com.sciman.Extras;

import javafx.scene.text.Text;

public class OptionSelector {
	
	int selected;//Which option is selected
	int numSelections;//How many options can be selected
	
	String[] selectionNames, selectionDescriptions;
	String prompt;
	
	public boolean isLockedIn;
	
	public OptionSelector(String title, String[] names, String[] desc) throws Exception {
		prompt = title;
		selectionNames = names;
		selectionDescriptions = desc;
		
		if (selectionNames.length != selectionDescriptions.length) {
			throw new Exception();
		}
		numSelections = selectionNames.length;
	}
	
	public void updateSelection(int x, Text label) {
		selected += x;
		if (selected < 0) selected = numSelections-1;
		else if (selected >= numSelections) selected = 0;
		
		//Update text to match
		String txt = "";
		for (int i=0;i<numSelections;i++) {
			if (i == selected) {
				txt += "> " + selectionNames[i] + " [" + selectionDescriptions[i] + "]\n";
			}else {
				txt += selectionNames[i] + "\n";
			}
		}
		label.setText(prompt + "\n" + txt);
	}
	
	public void setDescriptions(String[] desc) {
		selectionDescriptions = desc;
	}
	
	public int getSelection() {
		return selected;
	}
	
	public void reset() {
		selected = 0;
		isLockedIn = false;
	}

}
