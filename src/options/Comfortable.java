package options;

public class Comfortable extends Indicators  {

	@Override
	int measure(Mode m) {
		return value;
		
		// TODO Auto-generated method stub
		/*if ((Mode)o.mode=="car") {value=value+3;}//? 3 for car 2 for bus 1 for bike 0 for walk
		return value;*/
	}

	public Comfortable() {
		super();
		setName("Comfortable");
		// TODO Auto-generated constructor stub
	}

	@Override
	void measure(double dis, Mode m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void measure(double dist, Mode m, int arrive) {
		double t=(dist*m.speed)/arrive;
		value=(int) t;
		// TODO Auto-generated method stub
		
	}

}
