package options;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import territoireData.Coordinates;
import territoireData.VehicleLocation;

public class Option {
	
	ArrayList<VehicleLocation> path = new ArrayList<VehicleLocation>();
	double departureTime;
	ArrayList<Indicators> indicators = new ArrayList<Indicators>();
	ArrayList<Values> values = new ArrayList<Values>();

	/*
	 * Indicators iValues v int sunOfindicators=i.value +v.value; result of sum
	 * indicators and values
	 */
	public Option( ArrayList<VehicleLocation> path, double departureTime,
			ArrayList<Indicators> indicators, ArrayList<Values> values) {
		super();
		
		this.path = path;
		this.departureTime = departureTime;
		this.indicators = indicators;
		this.values = values;
	}
/*	public void evaluateTripValues(){
		// indicators list
		Time time = new Time();
		Cost cost = new Cost();
		Comfortable comf = new Comfortable();
		// add
		ArrayList<Indicators> indicators = new ArrayList();
		indicators.add(time);
		indicators.add(cost);
		indicators.add(comf);

		// value list
		Enviro en = new Enviro();
		// add
		ArrayList<Values> values = new ArrayList();
		values.add(en);
		
	}*/
int measure(){
	
	int sum=0;
	for ( int j=0; j<indicators.size(); j++ ){
		sum+=indicators.get(j).value;
	}
	for ( int j=0; j<values.size(); j++ ){
		sum+=values.get(j).value;
	}
	return sum;
}
	
}
