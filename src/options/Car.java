package options;

import java.awt.Color;
import java.util.ArrayList;

import territoireData.Coordinates;

public class Car extends Mode{
	
	
	 Color color=new Color(20,15,30);
	public Car() {
		super("Car", 50.6, 4, 2);
		
		// TODO Auto-generated constructor stub
	}
	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return color;
	}

}