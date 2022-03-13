package options;

abstract class Indicators {
	protected int value;
	private String name;
	abstract int measure(Mode m); //each subclass will implement it in it is way
	//can i make it void because i am just adding numbers to values field

/*public int getValue() {
		return value;
	}*/

	public String getName() {
		return name;
	}

	protected void name() {
		
	} void setName(String name) {
		this.name = name;
	}
//another type of measure to calculate time
	abstract void measure(double dis, Mode m) ;

	abstract void measure(double dist, Mode m, int arrive);
	
		
	


}
