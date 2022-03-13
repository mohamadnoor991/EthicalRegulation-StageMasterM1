package options;

import java.awt.Color;
import java.util.ArrayList;

import territoireData.Coordinates;

public abstract class Mode {
	final String name;
	final double speed;
	final double cost;
	final double costEn;
	
	
	public Mode(String name, double speed, double cost,double costEn) {
		super();
		this.name = name;
		this.speed = speed;
		this.cost = cost;
		this.costEn=costEn;
		
		
	}
	public abstract Color getColor();

}