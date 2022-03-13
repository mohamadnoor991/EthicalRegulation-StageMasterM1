package Tscenario;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import json.TransformJSON;
import scenario.Scenario;
import scenario.ScenarioTripDemand;
import territoireConnection.TerritoireHttpConnection;
import vocabulary.scenario;
import vocabulary.scenarioDemand;

public class TestScenario {
	
	
	
	public static String geturl = new String("https://territoire.emse.fr/applications/simulation-vehicle/get-data?");

		
	
	
	public static void main(String[] args) throws IOException {
		
		String login  = "balbo-sim";
		String password = "EMSEbalbo";
		String scenarioName = "Traffic2020";//my scenario name
		String cityName = "stetienne";//my scenario name
		int nbCycle = 50;
		int maxNumberOfVehicle = 150;
		
	////The coordinates of all map
		double MINLAT_COMPLET =  45.4325;
	    double MINLONG_COMPLET = 4.3782;
	    double MAXLAT_COMPLET = 45.444;
	    double MAXLONG_COMPLET = 4.4001;
	    String COMPLETE_MAP_NAME = "complete" ;
	    
	    
	  //The coordinates of simulated area of map
	    double MINLAT_SIMULATED = 45.43632;
	    double MINLONG_SIMULATED = 4.380861;
	    double MAXLAT_SIMULATED = 45.442024;
	    double MAXLONG_SIMULATED = 4.396207;
	    String SIMULATED_MAP_NAME = "simulated" ;
	     
	     //The coordinates of regulated area of map
	    double MINLAT_REGULATED = 45.4352;
	    double MINLONG_REGULATED = 4.386362;
	    double MAXLAT_REGULATED = 45.439544;
	    double MAXLONG_REGULATED = 4.391236 ;
	    String REGULATED_MAP_NAME = "regulated" ;
	    
	    /*new part 30-6-2020
	    TrafficScenario tra = null;*/
	    
	    int[] tr = {1,2,3,2,1}; 
	    ArrayList <Integer> temporaleRepartition = new ArrayList<Integer>();
	    for (int i : tr)
	    	temporaleRepartition.add(i);
		
		
						
			JSONObject parameters;
			
			MapBounds complete_map =  new MapBounds(MINLAT_COMPLET,MINLONG_COMPLET,MAXLAT_COMPLET,MAXLONG_COMPLET, COMPLETE_MAP_NAME);
			MapBounds simulated_map = new MapBounds(MINLAT_SIMULATED,MINLONG_SIMULATED,MAXLAT_SIMULATED,MAXLONG_SIMULATED, SIMULATED_MAP_NAME);
			MapBounds regulated_map = new MapBounds(MINLAT_REGULATED,MINLONG_REGULATED,MAXLAT_REGULATED,MAXLONG_REGULATED, REGULATED_MAP_NAME);
			
			parameters = new JSONObject();
		// new part 1-7-2020
			TrafficScenario trafficc=new TrafficScenario(parameters);
			parameters.put(scenario.scenarioType.toString(), TrafficScenario.getScenariotype());
			parameters.put(scenario.scenarioName.toString(),scenarioName);
			parameters.put(scenario.territory.toString(),cityName); 
			parameters.put(scenario.scenarioModel.toString(), "random");
			parameters.put("nbMaxVehicles",maxNumberOfVehicle);
		/*	parameters.put("complete", complete_map.getJSONObject());
			parameters.put("simulated", simulated_map.getJSONObject());
			parameters.put("regulated", regulated_map.getJSONObject());
		*/	parameters.put("seed",150);//can be random.?
			parameters.put("login", login);
			parameters.put("password", password);
			parameters.put("nbCycles",nbCycle);
			parameters.put("temporalRepartition", temporaleRepartition);
			parameters.put("processCreation", "random");
			//new part 30-6-2020
			parameters.put("POF",(int)((trafficc.getSex()/100)*maxNumberOfVehicle));
			parameters.put("POA",(int)((trafficc.getAge()/100)*maxNumberOfVehicle));
			parameters.put("POE",(int)((trafficc.getEducationLevel()/100)*maxNumberOfVehicle));
			parameters.put("POA",(int)((trafficc.getAnnulicom()/100)*maxNumberOfVehicle));
			parameters.put("POT",(int)((trafficc.getTimeRespect()/100)*maxNumberOfVehicle));
			
			//*********//1-7-2020
	
			TrafficScenario traffic = new TrafficScenario(parameters, complete_map, simulated_map, regulated_map);
			traffic.generateScenario();
			traffic.pushScenario(parameters);
			
			FileWriter file = new FileWriter("src/Jsonfiles/output.json");//the changes will save directly to the json file
			file.write(traffic.getJSONObject().toJSONString());
			file.close();
		
		}
	}