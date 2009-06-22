package org.mesh4j.ektoo.ui.component;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class PopupDialog extends JDialog{

	public PopupDialog(JFrame owner,String title){
		super(owner,title);
		setAlwaysOnTop(true);
		int x = (int)owner.getLocationOnScreen().getX() + 50;
		int y = (int)owner.getLocationOnScreen().getY() + 50;
		setLocation(x,y);
	}
}
