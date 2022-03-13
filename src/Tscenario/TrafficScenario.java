package Tscenario;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import demand.TrafficGenerator;
import json.TransformJSON;
import scenario.Scenario;
import territoireConnection.TerritoireHttpConnection;

public class TrafficScenario extends Scenario {

	protected static String geturl = new String("https://territoire.emse.fr/applications/simulation-vehicle/get-data?");
	protected static String puturl = new String("https://territoire.emse.fr/applications/simulation-vehicle/put-data?");
	static List<String> cars = Arrays.asList("Trucks", "professional", "personal", "others");

	Random random;
	private static final String scenarioType = "Traffic";
	public int nbMaxVehicles;
	private  int[] numofVehicleBywindows;
	public ArrayList<Integer> temporalRepartition;
	private int start = 0;
	private int end;
	private int timeWindowsSize;
	//The new part of indicators 30-6-2020.
	/************************************/
	double  sex=25; //what is the percentage of the travellers will be Female. 
	double age=20; //what is the percentage of the travellers there age will be in the range that we need. 
	double educationLevel=60; //what is the percentage of the travellers there educationL will be in the range that we need.
	double  annulicom=43; //what is the percentage of the travellers there A-income will be in the range that we need.
	double timeRespect=22; //what is the percentage of the travellers  will be respect the schedule . 
	//new part 1-7-2020
	public TrafficScenario(JSONObject parameters) {
		super(parameters);
	}
	public  double getSex() {
		return sex;
	}

	public  void setSex(double sex) {
		this.sex = sex;
	}
	

	public double getAge() {
		return age;
	}

	public void setAge(double age) {
		this.age = age;
	}

	public double getEducationLevel() {
		return educationLevel;
	}

	public void setEducationLevel(double educationLevel) {
		this.educationLevel = educationLevel;
	}

	public double getAnnulicom() {
		return annulicom;
	}

	public void setAnnulicom(double annulicom) {
		this.annulicom = annulicom;
	}

	public double getTimeRespect() {
		return timeRespect;
	}

	public void setTimeRespect(double timeRespect) {
		this.timeRespect = timeRespect;
	}
/****************************************/
//	public ArrayList<JSONObject> TW_VehicleType = new ArrayList<JSONObject>(); 
	/*
																						 * here will be in each object 4
																						 * fields 1-start time of time
																						 * window 2- end time of time
																						 * window 3-percentage of car
																						 * that will start 4- type of
																						 * vehicle. - time of departure
																						 * ?
																						 */
		
	public ArrayList<JSONObject> temporalWindows ;
	private MapBounds complete;
	private MapBounds simulated;
	private MapBounds regulated;

	public TrafficScenario(JSONObject parameters, MapBounds complete_map, MapBounds simulated_map, MapBounds regulated_map) {
		super(parameters);
		TransformJSON.initInstanceFromJSONO(this, parameters);
		random = new Random(seed);		
		numofVehicleBywindows = new int[temporalRepartition.size()];
		temporalWindows = new ArrayList<JSONObject>();	
		this.complete = complete_map;
		this.simulated = simulated_map;
		this.regulated = regulated_map;
	}

	public void generateScenario() {
		createVehicleRepartition();
		setupTimeWindows();
	}
	
	private void createVehicleRepartition() {
		double storeMaxNumberOfVehicle = 0;
		for (int i : temporalRepartition)
			storeMaxNumberOfVehicle += i;
		
		for (int y = 0; y < temporalRepartition.size(); y++) {
			double perOfVehicle = temporalRepartition.get(y)/storeMaxNumberOfVehicle;
			int numOfVehicleTW = (int) (perOfVehicle * nbMaxVehicles);
			numofVehicleBywindows[y]= numOfVehicleTW;
		}
	}
	
	// Create windows and the information it contains
	private void setupTimeWindows() {

		timeWindowsSize = nbCycles / temporalRepartition.size();
		end = timeWindowsSize;
		
		for (int j = 0; j < numofVehicleBywindows.length; j++) {// how much time i need to generate the time window
			JSONObject obj = new JSONObject();

			obj.put("twi", start);
			obj.put("twj", end);
			obj.put("P-Gtraffic", numofVehicleBywindows[j]);
			start = start + timeWindowsSize;
			end = end + timeWindowsSize;

			for (int i = 0; i < cars.size(); i++) {// for type of vehicle

				int size = 0;
				if (numofVehicleBywindows[j] != 0)
					size = random.nextInt((numofVehicleBywindows[j]));
				obj.put("P-OF" + cars.get(i), size);
				numofVehicleBywindows[j] = numofVehicleBywindows[j] - size;
			}
			temporalWindows.add(obj);
		}
	
		
	}

//push the scenario to the platform.
	@Override
	protected void pushScenario(JSONObject parameters) {
		String login = (String) parameters.get("login");
		String password = (String) parameters.get("password");
		JSONArray result = new JSONArray();
		JSONObject jol = this.getJSONObject();
		result.add(jol);
		try {
			String rep = TerritoireHttpConnection.postURL(puturl, result, login, password, "scenario", scenarioName);
			System.out.println(rep);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override // generate json file with your parameters.that i will push it to the platfprm
	public JSONObject getJSONObject() {
		JSONObject jo = TransformJSON.createJSONFromInstance(this);
		jo.put(complete.mapName, complete.getJSONObject());
		jo.put(simulated.mapName, simulated.getJSONObject());
		jo.put(regulated.mapName, regulated.getJSONObject());
		//new part of work add percent for each indicators
		jo.put("numF",(int)((sex/100)*nbMaxVehicles));
	//	jo.put("numF",(int)((sex/100)*(int)(TrafficGenerator.myTrips.size())));
		jo.put("numAage",(int)((age/100)*nbMaxVehicles));
		jo.put("numEL",(int)((educationLevel/100)*nbMaxVehicles));
		jo.put("numAI",(int)((annulicom/100)*nbMaxVehicles));
		jo.put("numTR",(int)((timeRespect/100)*nbMaxVehicles));
		
		return jo;				
	}


	/*
	 * methods to chuck the values from user(future work). public void
	 * checkCompleteMap(double MINLAT,double MINLONG,double MAXLAT,double MAXLONG )
	 * { if(MINLAT< MAXLAT && MINLONG <MAXLONG)//we can add more conditions . {
	 * 
	 * MINLAT_COMPLET=MINLAT; MINLONG_COMPLET=MINLONG; MAXLAT_COMPLET=MAXLAT;
	 * MAXLONG_COMPLET=MAXLONG; }
	 * 
	 * }
	 * 
	 * public void checkSimulatedMap(double MINLAT,double MINLONG,double
	 * MAXLAT,double MAXLONG ) { if(MINLAT< MAXLAT && MINLONG
	 * <MAXLONG&&MINLAT_COMPLET>MINLAT&& MINLONG_COMPLET>MINLONG&&
	 * MAXLAT_COMPLET>MAXLAT&&MAXLONG_COMPLET>MAXLONG) //we can add more conditions
	 * . {
	 * 
	 * MINLAT_SIMULATED=MINLAT; MINLONG_SIMULATED=MINLONG; MAXLAT_SIMULATED=MAXLAT;
	 * MAXLONG_SIMULATED=MAXLONG; }
	 * 
	 * }
	 * 
	 * public void checkRegulatedMap(double MINLAT,double MINLONG,double
	 * MAXLAT,double MAXLONG ) { if(MINLAT< MAXLAT && MINLONG
	 * <MAXLONG&&MINLAT_SIMULATED>MINLAT&& MINLONG_SIMULATED>MINLONG&&
	 * MAXLAT_SIMULATED>MAXLAT&&MAXLONG_SIMULATED>MAXLONG) //we can add more
	 * conditions . {
	 * 
	 * MINLAT_REGULATED=MINLAT; MINLONG_REGULATED=MINLONG; MAXLAT_REGULATED=MAXLAT;
	 * MAXLONG_REGULATED=MAXLONG; }
	 * 
	 * }
	 */
	@Override
	public void initSpecificParameters() {
		// TODO Auto-generated method stub

	}

	public static String getScenariotype() {
		// TODO Auto-generated method stub
		return scenarioType;
	}

}
