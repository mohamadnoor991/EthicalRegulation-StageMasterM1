package options;

public class Time extends Indicators {
	
	
	public Time() {
		super();
		setName("Time");
		// TODO Auto-generated constructor stub
	}

	@Override
	void measure(double dist,Mode m,int arriveT) {
		// TODO Auto-generated method stub
		
		int t=(int)(dist/m.speed);
		if(t<=arriveT)
		value= 1;
	}

	@Override
	int measure(Mode m) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	void measure(double dis, Mode m) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
