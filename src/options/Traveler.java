package options;

import java.util.ArrayList;

import json.TransformJSON;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import demand.TrafficGenerator;
import demand.TripTraffic;
import environment.physical.NetworkLocation;
import scenario.Scenario;
import simulator.RegulatedVehicle;
import territoireData.Coordinates;
import territoireData.VehicleLocation;
import utils.Global;

public class Traveler extends RegulatedVehicle {
	
	
	NetworkLocation origin;//new 20-7
	Coordinates destination;//new 20-7
	
	Options myOption;
	Option currentBestOption;
	long educL;
	long timeR;
	long sex;
	long age;
	long annuI;
	double departureTime;
	double distance;
	int arriavelTime;
	Option myoption;
	ArrayList<VehicleLocation> pathOfTowPoints ;
	JSONArray filedecesion=new JSONArray();
	JSONObject objectFile=new JSONObject();
	//
	JSONObject info = new JSONObject();
	JSONArray inffo = new JSONArray();
	// *Mode
	ArrayList<Mode> mode = new ArrayList<Mode>();
	Bike b = new Bike();
	Walk w = new Walk();
	Car c = new Car();
	PublicTransport pub = new PublicTransport();
	// *indicator
	ArrayList<Indicators> indicators = new ArrayList();
	Time time = new Time();
	Cost cost = new Cost();
	Comfortable comf = new Comfortable();
	CostEnviroment costEnviroment=new CostEnviroment();
	// Values
	ArrayList<Values> values = new ArrayList();
	

	//
	public Traveler(NetworkLocation origin, NetworkLocation destination,
			double departureTime) {
		super(origin, destination, departureTime);
		// TODO Auto-generated constructor stub
	}

	public Traveler(NetworkLocation origin, NetworkLocation destination,
			double departureTime, long educL, long timeR, long sex, long age,
			long annuI,double distance,int arriavelTime,Option myoption,ArrayList<VehicleLocation> pathOfTowPoints ) {
		super(origin, destination, departureTime);

		this.educL = educL;
		this.timeR = timeR;
		this.sex = sex;
		this.age = age;
		this.annuI = annuI;
		this.distance=distance;
		this.arriavelTime=arriavelTime;
		this.myoption=myoption;
		this.pathOfTowPoints=pathOfTowPoints;
	}

	public long getIndecators() {// method to get the values for
											// indicators
		long total = age + annuI + educL + sex + timeR;
		return total;

	}

	public JSONArray evaluationsTrip() {

		// mode List
		mode.add(b);
		mode.add(w);
		mode.add(c);
		mode.add(pub);
		// indicators list
		indicators.add(time);
		indicators.add(cost);
		indicators.add(comf);
		

		// value list
		
		values.add(costEnviroment);
		
		long sumOfIn=getIndecators();
		
		for (int p = 0; p < mode.size(); p++) {
			
			time.measure(distance, mode.get(p), arriavelTime);// Execute
																	// the time
			// method
			// time.measure(totalDistance, b,y);//Execute the time method
			cost.measure(distance, mode.get(p));
			comf.measure(distance, mode.get(p), arriavelTime);
			
			costEnviroment.measure(distance, mode.get(p));
			// calcualte the path and send it to costractors

			myoption = new Option( pathOfTowPoints, departureTime, indicators,
					values);// option for each mode//....
			int valueofoption = (int) (sumOfIn + myoption.measure());// work
																		// point
			System.out.println("total points of option withe mode "
					+ mode.get(p).name + " = " + valueofoption);
			
			
			objectFile.put("total points of option withe mode "
					+ mode.get(p).name + " = ", valueofoption);
			
		
		
		}
		filedecesion.add(objectFile);
		bestOption();
		return filedecesion; //it just to display some result it can be in the end void method
	}
	//method to choice the best option of trip
	public void bestOption(){
		int min=0;
		JSONObject myChoice=new JSONObject();
		JSONObject myChoice200=new JSONObject();
		ArrayList<Integer> vALUE=new ArrayList<Integer>();
		myChoice=(JSONObject) filedecesion.get(0);
		int pubT=(int) myChoice.get("total points of option withe mode PubTransport = ");
		int car=(int) myChoice.get("total points of option withe mode Car = ");
		int walk=(int) myChoice.get("total points of option withe mode Walk = ");
		int bike=(int) myChoice.get("total points of option withe mode Bike = ");
	vALUE.add(pubT);
	vALUE.add(car);
	vALUE.add(walk);
	vALUE.add(bike);
	min=vALUE.get(0);
	for(int i=0;i<vALUE.size();i++){
		
		if(vALUE.get(i)<min){
			min=vALUE.get(i);
			
		}
	
	}
			System.out.println("the best choice for us is = "+min);
	}

	// getter and setters
	public long getEducL() {
		return educL;
	}

	public void setEducL(long educL) {
		this.educL = educL;
	}

	public long getTimeR() {
		return timeR;
	}

	public void setTimeR(long timeR) {
		this.timeR = timeR;
	}

	public long getSex() {
		return sex;
	}

	public void setSex(long sex) {
		this.sex = sex;
	}

	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}

	public long getAnnuI() {
		return annuI;
	}

	public void setAnnuI(long annuI) {
		this.annuI = annuI;
	}

}