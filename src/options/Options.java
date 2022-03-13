package options;

import java.util.ArrayList;
import java.util.Random;

public class Options {
	Random rand=new Random();
	ArrayList  <Option> choiceOption = new ArrayList<Option>();

	
	public Options() {
		super();
	}
	public Options(ArrayList<Option> choiceOption) {
		super();
		this.choiceOption = choiceOption;
		
	}
	//trying
	public int getArrivelTime(double depart,double numOfpath) {
		int ar= (int) ((int)depart+numOfpath+rand.nextInt(6));
		return ar;
	}

}
