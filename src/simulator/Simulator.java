package simulator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.Random;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import agent.Agent;

import environment.interaction.CommunicationManagement;
import environment.physical.Source;
import pbc.MasComponent;
import scenario.Scenario;
import sharedInformation.ParameterEnum;
import taxiSimulation.Taxi;
import territoireData.CarNetwork;
import territoireData.Network;
import territoireData.Territoire;
import utils.Global;
import vehicle.behaviorState.VehicleBehaviorStateNameEnum;
import vehicleSimulation.Vehicle;
// import push Territoire
import territoireConnection.TerritoireHttpConnection;

public class Simulator {
	public static int cycle = 0;
	protected ArrayList<RegulatedVehicle> fleet;
	public static CommunicationManagement communicatingEnvironment = new CommunicationManagement();
	public static String geturl = new String("https://territoire.emse.fr/applications/simulation-vehicle/get-data?");
	public static String puturl = new String("https://territoire.emse.fr/applications/simulation-vehicle/put-data?");
	public static String cleanurl = new String("https://territoire.emse.fr/applications/simulation-vehicle/clean-sim?");
	private static JSONArray requests;
	private Network network;

	public static void main(String[] args) throws IOException {

		Simulator basic = new Simulator();
		Global.scenarioName = "Traffic2020";
		Global.login = "bader-sim";
		Global.password = "MDPbader1352";
		Global.pushTerritoire = true;
		basic.initSimulationData(true, Global.login, Global.password);
		long[] tabl = { Global.seed };
		TerritoireHttpConnection.cleanAll(Global.login, Global.password, Global.scenarioName);
		basic.run(tabl);
		
	}

	protected void resetSimulationData(boolean pushData, long seed) {
		
		cycle = 0;
		Global.seed = seed;
		Global.rng = new Random(seed);
		network.setRng(Global.rng);
		Simulator basic = new Simulator();
		initFleet();
		Source.reset();
		Taxi.totalNumberOfConsumedRequest = 0;
		Taxi.coordinationReceivedMessage = 0;
		Global.scenarioName = Global.scenarioName + seed;
		if (Global.pushTerritoire)
			cleanVehicle();

	}

	protected void initSimulationData(boolean pushData, String login, String password) {
		// clean the Territoire dataBase
		// cleanVehicle();

		cycle = 0;

		// data request
		JSONArray jScenario;
		JSONObject scenario;

		// test recorded scenario
		jScenario = Scenario.parseScenarioFile("src/Jsonfiles/output.json", login, password);
		// jScenario = TerritoireHttpConnection
		// .getSimulationData(geturl, login, password, Global.scenarioName, "scenario");
		// test file
		if (jScenario.size() == 0) {
			jScenario = Scenario.parseScenarioFile("src/Jsonfiles/output.json", login, password);
			Global.scenarioName = (String) ((JSONObject) jScenario.get(0)).get("scenarioName");
		}

		if (jScenario == null) {
			System.out.println("no scenario data for :" + Global.scenarioName);
			System.exit(0);
		}

		scenario = (JSONObject) jScenario.get(0);

		/*
		 * This part should uncommented when the creation of requests will be correct
		 * 
		 * requests = (JSONArray) TerritoireHttpConnection .getSimulationData(geturl,
		 * login, password, Global.scenarioName, "request");
		 * 
		 * if (requests.size() == 0) { JSONArray resultLoc =
		 * RequestGenerator.createRequests(scenario, login, password, pushData); for
		 * (Object jo : resultLoc) { JSONObject requestsL = new JSONObject();
		 * requestsL.put("requests",jo); requests.add(requestsL); } }
		 */

		JSONParser parser = new JSONParser();
		Simulator.requests = new JSONArray();
	
		
		try {
			JSONObject contentFile=(JSONObject) parser.parse(new FileReader("src/Jsonfiles/requests.json"));
			JSONArray requestsArray=new JSONArray();
			requestsArray.add(contentFile);
			Simulator.requests = (JSONArray)requestsArray;// parser.parse(new FileReader("src/Jsonfiles/requests.json"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// init Network : this must be modified using information from the jsonfile
		JSONObject edgeSimulatedMap = (JSONObject) scenario.get("complete");
		double minLatitude = (double) edgeSimulatedMap.get("min_lat");
		double maxLatitude = (double) edgeSimulatedMap.get("max_lat");
		double minLongitude = (double) edgeSimulatedMap.get("min_long");
		double maxLongitude = (double) edgeSimulatedMap.get("max_long");

		Territoire territoire = new Territoire(minLatitude, minLongitude, maxLatitude, maxLongitude, "stetienne");
		
		network = new CarNetwork(territoire,(int) Global.seed);
		// Network.cleanEdges(Network.getEdgesComplet());
		int j = 0;

		initGlobal(scenario);
		

		initFleet();
	}

	protected void initFleet() {
		// Taxis definition

		fleet = new ArrayList<RegulatedVehicle>(Simulator.requests.size());
		int i = 0;
		JSONArray requests = (JSONArray) ((JSONObject) Simulator.requests.get(0)).get("requests");
		for (Object o : requests) {
			HashMap<ParameterEnum, Object> param = new HashMap<>();
			/*
			 * // if request are given to the taxi at the beginning
			 * param.put(ParameterEnum.InitialRequests, ajo);
			 */
			RegulatedVehicle v = RegulatedVehicle.factory(((JSONObject) o), Global.trace, param, network);
			if (v != null) {
				fleet.add(v);
				communicatingEnvironment.add(v);
			}

		}
	}

	@SuppressWarnings("unchecked")
	public Simulator() {

		// Simulation Components initialization
		communicatingEnvironment = new CommunicationManagement();

		MasComponent.init();
		Agent.init();
		CommunicationManagement.init();

	}

	private void initGlobal(JSONObject scenario) {
		// init seed
		Global.seed = (Long) scenario.get("seed");

		if (scenario.get("nbCycles").getClass().getTypeName().compareTo("java.lang.Integer") == 0) {
			Global.duration = (Integer) scenario.get("nbCycles");

			int nbmaxvehicles = (Integer) scenario.get("nbMaxVehicles");
			Global.nb_taxis = (Integer) scenario.get("nbMaxVehicles");
		} else {
			Global.duration = Integer.parseInt(((Long) scenario.get("nbCycles")).toString());

			int nbmaxvehicles = Integer.parseInt(((Long) scenario.get("nbMaxVehicles")).toString());
			Global.nb_taxis = Integer.parseInt(((Long) scenario.get("nbMaxVehicles")).toString());
			// Integer.parseInt(((Long)jo.get("nbVehicles")).toString());
		}
		Global.rng = new Random(Global.seed);
	}

	private void pushTerritoireVehicle(ArrayList<RegulatedVehicle> traffic2, int cycle) {

		JSONArray trafficJSON = new JSONArray();
		cleanVehicle();
		for (RegulatedVehicle v : traffic2) {
			String name = v.getBehavior().getCurrentState().getName().toString();
			if ((v.getBehavior().getCurrentState().getName() == VehicleBehaviorStateNameEnum.GOINGTO)) {
				JSONObject vehicleJSON = createVehicle(v.getId(), v.getLocation().getCoordinates().getLatitude(),
						v.getLocation().getCoordinates().getLongitude(), 5, cycle, Global.scenarioName);
				trafficJSON.add(vehicleJSON);
			}
		}
		String st = "type=vehicle&user=" + Global.login + "&password=" + Global.password;

		try {
			String resp = TerritoireHttpConnection.postURL(puturl, trafficJSON, Global.login, Global.password,
					"vehicle", Global.scenarioName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private void cleanVehicle() {
		String st = "type=vehicle&user=" + Global.login + "&password=" + Global.password + "&simulationname="
				+ Global.scenarioName;
		try {
			String resp = TerritoireHttpConnection.getURL(cleanurl, st, new String());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JSONObject createVehicle(int id, double latitude, double longitude, int radius, int timesim,
			String simname) {
		JSONObject obj1 = new JSONObject();
		JSONObject obj1_pos = new JSONObject();
		JSONArray tabpos = new JSONArray();
		obj1.put("id", id);
		obj1_pos.put("latitude", latitude);
		obj1_pos.put("longitude", longitude);
		tabpos.add(obj1_pos);
		obj1.put("radius", radius);
		obj1.put("jsongeoms", tabpos);
		obj1.put("timesim", timesim);
		obj1.put("simulationname", simname);
		return obj1;
	}

	public void run(long[] tabseed) {

		for (int iemeSimulation = 0; iemeSimulation < tabseed.length; iemeSimulation++) {

			System.out.println("================  Simulation: " + (iemeSimulation + 1) + "| seed : " + Global.seed
					+ "================");

			long time = System.currentTimeMillis();

			loop();

			long simulationTime = (System.currentTimeMillis() - time);
			if (Global.trace)
				System.out.println(" (" + simulationTime + "ms)");
			if (Global.trace)
				for (RegulatedVehicle t : fleet) {
					t.displayStatistic();
				}

			double totalMovingDistance = 0.0;

			for (RegulatedVehicle t : fleet) {
				totalMovingDistance += t.getMovingDuration();

			}

			System.out.println("total moving distance:" + totalMovingDistance);

			// reset
			if (iemeSimulation < tabseed.length - 1)
				resetSimulationData(false, tabseed[iemeSimulation + 1]);

		}
	}

	protected void update(String scenarioName, String login, String password, int sleepDuration, boolean debug,
			boolean trace, int distance, boolean pushData) {

		Global.scenarioName = scenarioName;
		Global.login = login;
		Global.password = password;
		Global.sleepDuration = sleepDuration;
		Global.debug = debug;
		Global.trace = trace;
		Territoire.DISTANCE_DEFAULT = distance;
		Global.speed = Math.min(distance / 2, 1);
		Global.pushTerritoire = pushData;
	}

	protected void loop() {

		// first step to initialize DB
		for (RegulatedVehicle t : fleet) {
			t.oneStep(0);

		}
		// communicatingEnvironment.send();
		if (Global.pushTerritoire)
			pushTerritoireVehicle(fleet, 0);

		// for step by step control
		Scanner sc = new Scanner(System.in);

		if (Global.pushTerritoire) {
			System.out.println();
			System.out.println("[Entrer] pour lancer:" + Global.scenarioName);
			sc.nextLine();
			System.out.println("C'est parti");
		}

		for (cycle = 1; cycle < Global.duration; cycle++) {

		//	Collections.shuffle(fleet, Global.rng);
			if (Global.trace) {
				System.out.println();
				System.out.println(" ==========================  CYCLE: " + cycle + " ==========================  ");
			}
			for (RegulatedVehicle t : fleet) {
				t.oneStep(cycle);
			}

			if (Global.pushTerritoire)
				pushTerritoireVehicle(fleet, cycle);
			try {
				Thread.sleep(Global.sleepDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// communicatingEnvironment.send();
			if (Global.trace)
				CommunicationManagement.display();

			if (Global.debug) {
				System.out.println("[Enter] for next step");
				sc.nextLine();
			}

		}
	}

}
