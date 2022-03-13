package options;

import java.awt.Color;

public class PublicTransport extends Mode{
	static Color color=new Color(70,0,12);
	public PublicTransport() {
		
		super("PubTransport", 30, 2, 1);
		// TODO Auto-generated constructor stub
	}
	public Color getColor() {
		// TODO Auto-generated method stub
		return color;
	}
}