package options;

import java.awt.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import environment.physical.NetworkLocation;
import scenario.Scenario;
import simulator.Simulator;
import territoireData.CarNetwork;
import territoireData.Coordinates;
import territoireData.Network;
import territoireData.PublicTransportNetwork;
import territoireData.Territoire;
import territoireData.VehicleLocation;
import territoireData.WalkNetwork;
import utils.Global;

public class Testoption {
	public static String login = "bader-sim";
	public static String password = "MDPbader1352";

	public static void main(String[] args) throws IOException {
		JSONArray tValue = new JSONArray();///// result of execute traveller method.
		int dis = 5;// the average of step length
		Options option = new Options();
		Option myoption = null;
		
	//	Territoire se = new Territoire();
		// init Network : this must be modified using information from the jsonfile
		
		/*****/
		//23-7-2020 new part added for the new modifications
		JSONArray jScenario=generateJsonSe();
		JSONObject scenario;
		scenario = (JSONObject) jScenario.get(0);
				JSONObject edgeSimulatedMap = (JSONObject) scenario.get("complete");
				double minLatitude = (double) edgeSimulatedMap.get("min_lat");
				double maxLatitude = (double) edgeSimulatedMap.get("max_lat");
				double minLongitude = (double) edgeSimulatedMap.get("min_long");
				double maxLongitude = (double) edgeSimulatedMap.get("max_long");

				Territoire territoire = new Territoire(minLatitude, minLongitude, maxLatitude, maxLongitude, "stetienne");
		Network netWork;
		Network netWorkBus;
		Network netWorkWB;
		//netWork = new CarNetwork(territoire,(int) Global.seed);
		
		/*****/
		//netWork = new PublicTransportNetwork (territoire,(int) Global.seed); //29-7-2020--for public transportation
		netWork = new WalkNetwork (territoire,(int) Global.seed); //29-7-2020  --for walk and bike
		/*****/
		
		//end new part 23-7-2020
		/*****/
		//netWork.initNetwork(se);//solved!
		JSONArray requestFile = generateJson();
		JSONObject containtFile = (JSONObject) requestFile.get(0);
		JSONArray req = (JSONArray) containtFile.get("requests");
		Traveler traveler;
		Coordinates coOrigin;// give the coordinate of the Origin points
		Coordinates coDes;// give the coordinate of the Destination points
		NetworkLocation originPoint = null;
		NetworkLocation desPoint;
		/// loop parts
		for (int i = 0; i < req.size(); i++) {// loop for requests
			System.out.println("the information of trip number " + (i + 1) + "=");
			JSONObject r = (JSONObject) req.get(i);
			double departureTime = (double) r.get("DepartureTime");
			long educL = (long) r.get("educL");
			long timeR = (long) r.get("timeR");
			long sex = (long) r.get("sex");
			long age = (long) r.get("age");
			long annuI = (long) r.get("annuI");
			long ID_RESOURCE = (long) r.get("ID_RESOURCE");
			JSONObject origin = (JSONObject) r.get("origin");
			JSONObject destination = (JSONObject) r.get("destination");
			String VehicleType = (String) r.get("VehicleType");
			String osm_id = (String) origin.get("osm_id");
			long id = Long.parseLong(osm_id);// arcid
			// origin coordinate
			JSONObject jsongeomsOrigin = (JSONObject) origin.get("jsongeoms");
			double latitudeOrigin = (double) jsongeomsOrigin.get("latitude");
			double longitudeOrigin = (double) jsongeomsOrigin.get("longitude");
			// Destination coordinate
			JSONObject jsongeomsDes = (JSONObject) destination.get("jsongeoms");
			double latitudeDes = (double) jsongeomsDes.get("latitude");
			double longitudeDes = (double) jsongeomsDes.get("longitude");
			coOrigin = new Coordinates(latitudeOrigin, longitudeOrigin);// coordinate
																		// of
																		// origin
			coDes = new Coordinates(latitudeDes, longitudeDes);// coordinate of destination
			originPoint = new NetworkLocation(coOrigin, id,netWork);// arc number?
			
			desPoint = new NetworkLocation(coDes, id,netWork);//solve the problem with netWork
			double distance = originPoint.distance(coDes);// distance between 2 points
		
		
			ArrayList<VehicleLocation> pathOfTowPoints = originPoint.getPath(coDes);//new changes about coordinate important one to look
			/**********/
			//the same here will be path by bike and ude to collcelate the for the mode
			//same concept different parameters
			//new 29-7----ArrayList<Coordinates> pathOfTowPointsbyBike = originPoint.getPath(coDes);
			int arriavelTime = option.getArrivelTime(departureTime, distance);// arrival time for each trip
			double totalDistance = dis * distance;//by meters 
			System.out.println("arrival time for trip number " + (i + 1) + "= " + arriavelTime);// arrival time for each
																								// trip
			System.out.println("real distance " + totalDistance);
			traveler = new Traveler(originPoint, desPoint, departureTime, educL, timeR, sex, age, annuI, distance,
					arriavelTime, myoption, pathOfTowPoints);// travelers instance
			tValue = traveler.evaluationsTrip();
			System.out.println("MY VALUES " + tValue);
			System.out.println("/////////////////////////////////");
		}
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}// end main

	/*****
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 *****/
	public static JSONArray generateJson() {

		// JSONArray jScenario = TerritoireHttpConnection
		// .getSimulationData(geturl, login, password, scenarioName,
		// "scenario");//third
		// field my scenario name
		JSONArray jScenario = Scenario.parseScenarioFile("src/Jsonfiles/requests.json", login, password);

		// test file
		if (jScenario.size() == 0) {
			jScenario = Scenario.parseScenarioFile("src/Jsonfiles/requests.json", login, password);
			Global.scenarioName = (String) ((JSONObject) jScenario.get(0)).get("scenarioName");
		}

		if (jScenario == null) {
			System.out.println("no scenario data for :" + Global.scenarioName);
			System.exit(0);
		}
		return jScenario;
	}
	/*****/
	public static JSONArray generateJsonSe() {

		// JSONArray jScenario = TerritoireHttpConnection
		// .getSimulationData(geturl, login, password, scenarioName,
		// "scenario");//third
		// field my scenario name
		JSONArray jScenario = Scenario.parseScenarioFile("src/Jsonfiles/output.json", login, password);

		// test file
		if (jScenario.size() == 0) {
			jScenario = Scenario.parseScenarioFile("src/Jsonfiles/output.json", login, password);
			Global.scenarioName = (String) ((JSONObject) jScenario.get(0)).get("scenarioName");
		}

		if (jScenario == null) {
			System.out.println("no scenario data for :" + Global.scenarioName);
			System.exit(0);
		}
		return jScenario;
	}
	
	

}