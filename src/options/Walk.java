package options;

import java.awt.Color;
import java.util.ArrayList;

import territoireData.Coordinates;

public class Walk extends Mode{
	
	
	static Color color=new Color(48,15,68);
	public Walk() {
		super("Walk", 4, 1.2, 0);
		
		// TODO Auto-generated constructor stub
	}
	public Color getColor() {
		// TODO Auto-generated method stub
		return color;
	}
}