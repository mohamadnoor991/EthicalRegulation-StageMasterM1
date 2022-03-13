package options;

import java.awt.Color;

public class Bike extends Mode{
	static Color color=new Color(48,22,83);
	public  Bike() {
	super("Bike", 70, 1.7, 0);
		// TODO Auto-generated constructor stub
	}

	
	public Color getColor() {
		// TODO Auto-generated method stub
		return color;
	}
	

}