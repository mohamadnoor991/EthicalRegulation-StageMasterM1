package options;

public  abstract class Values {
	int value;
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	abstract int measure(Mode m);
	abstract  void measure(double dists, Mode m);

}
