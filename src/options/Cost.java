package options;

public class Cost extends Indicators  {

	@Override
	int measure(Mode m) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Cost() {
		super();
		setName("Cost");
		// TODO Auto-generated constructor stub
	}

	@Override
	void measure(double dis, Mode m) {
		double t=dis*m.cost;
		value=(int) t;
		// TODO Auto-generated method stub
		
	}

	@Override
	void measure(double dist, Mode m, int arrive) {
		
		// TODO Auto-generated method stub
		
	}

}
