package options;

public class CostEnviroment extends Values{
	
	public CostEnviroment() {
		super();
		setName("CostEnviroment");
		// TODO Auto-generated constructor stub
	}

	@Override
	int measure(Mode m) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	void measure(double dis, Mode m) {
		double measureCost=(dis*m.costEn);
		value=(int) measureCost;
		// TODO Auto-generated method stub
		
	}

	
}
