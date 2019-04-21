package gui.styling;

import javafx.scene.control.TextField;

public class NumericTextField extends TextField {
	
	@Override
	public void replaceText(int start, int end, String string) {
		if(string.matches("[\\d]*"))
			super.replaceText(start, end, string);
	}
}
