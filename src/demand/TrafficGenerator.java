package demand;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import Tscenario.TrafficScenario;
import environment.physical.Area;
import scenario.Scenario;
import territoireConnection.TerritoireHttpConnection;
import territoireData.CarNetwork;
import territoireData.Coordinates;
import territoireData.Edge;
import territoireData.Network;
import territoireData.Territoire;
import utils.Global;
import org.json.simple.JSONObject;

public class TrafficGenerator extends RequestGenerator {

	static Random random = new Random(150);// the value 15 it is equal to the "seed"

	public static String geturl = new String("https://territoire.emse.fr/applications/simulation-vehicle/get-data?");
	public static String login = "bader-sim";
	public static String password = "MDPbader1352";
	public static String scenarioName = "Traffic2020";// my scenario name

	// public static JSONArray simulated_map;
	static Territoire areapartNW;
	static Territoire areapartNE;
	static Territoire areapartSE;
	static Territoire areapartSW;

	public static ArrayList<TripTraffic> myTrips = new ArrayList<TripTraffic>();// make it public
	static JSONArray partNW;
	static JSONArray partNE;
	static JSONArray partSE;
	static JSONArray partSW;

	public TrafficGenerator() {
	}

	public static HashMap<String, JSONArray> areas = new HashMap<String, JSONArray>();

	// coordinate of simulated map.
	static double MINLAT_SIMULATED;
	static double MINLONG_SIMULATED;
	static double MAXLAT_SIMULATED;
	static double MAXLONG_SIMULATED;

	// coordinate of simulated map.
	static double MINLAT_REGULATED;
	static double MINLONG_REGULATED;
	static double MAXLAT_REGULATED;
	static double MAXLONG_REGULATED;

	static ArrayList<Long> forbiddenEdges = new ArrayList<Long>();

	private static double MINLAT_COMPLETE;
	private static double MINLONG_COMPLETE;
	private static double MAXLAT_COMPLETE;
	private static double MAXLONG_COMPLETE;

	// create simulated map and the divided parts of map
	public static void createMap() {

		// simulated map parameters from json file.

		JSONArray source = generateJson();

		// The coordinates of simulated area of map
		JSONObject jsonObject = new JSONObject();
		jsonObject = (JSONObject) source.get(0);

		JSONObject complete = (JSONObject) jsonObject.get("complete");
		MINLAT_COMPLETE = (double) complete.get("min_lat");
		MINLONG_COMPLETE = (double) complete.get("min_long");
		MAXLAT_COMPLETE = (double) complete.get("max_lat");
		MAXLONG_COMPLETE = (double) complete.get("max_long");

		JSONObject simulated = (JSONObject) jsonObject.get("simulated");
		MINLAT_SIMULATED = (double) simulated.get("min_lat");
		MINLONG_SIMULATED = (double) simulated.get("min_long");
		MAXLAT_SIMULATED = (double) simulated.get("max_lat");
		MAXLONG_SIMULATED = (double) simulated.get("max_long");

		JSONObject regulated = (JSONObject) jsonObject.get("regulated");
		MINLAT_REGULATED = (double) regulated.get("min_lat");
		MINLONG_REGULATED = (double) regulated.get("min_long");
		MAXLAT_REGULATED = (double) regulated.get("max_lat");
		MAXLONG_REGULATED = (double) regulated.get("max_long");

		// depends of attribute territory;
		Territoire territoire1 = new Territoire(TrafficGenerator.MINLAT_COMPLETE, TrafficGenerator.MINLONG_COMPLETE,
				TrafficGenerator.MAXLAT_COMPLETE, TrafficGenerator.MAXLONG_COMPLETE, "stetienne");
		// TrafficGenerator.simulated_map =
		computeForbiddenEdge(territoire1.getNetworkCoordinates());

		// parts section
		areapartNW = new Territoire((TrafficGenerator.MINLAT_SIMULATED + TrafficGenerator.MAXLAT_SIMULATED) / 2,
				TrafficGenerator.MINLONG_SIMULATED, TrafficGenerator.MAXLAT_SIMULATED,
				(TrafficGenerator.MAXLONG_SIMULATED + TrafficGenerator.MINLONG_SIMULATED) / 2, "stetienne");

		areapartNE = new Territoire((TrafficGenerator.MAXLAT_SIMULATED + TrafficGenerator.MINLAT_SIMULATED) / 2,
				(TrafficGenerator.MAXLONG_SIMULATED + TrafficGenerator.MINLONG_SIMULATED) / 2,
				TrafficGenerator.MAXLAT_SIMULATED, TrafficGenerator.MAXLONG_SIMULATED, "stetienne");// BE

		areapartSW = new Territoire(TrafficGenerator.MINLAT_SIMULATED, TrafficGenerator.MINLONG_SIMULATED,
				(TrafficGenerator.MAXLAT_SIMULATED + TrafficGenerator.MINLAT_SIMULATED) / 2,
				(TrafficGenerator.MAXLONG_SIMULATED + TrafficGenerator.MINLONG_SIMULATED) / 2, "stetienne");

		areapartSE = new Territoire(TrafficGenerator.MINLAT_SIMULATED,
				(TrafficGenerator.MAXLONG_SIMULATED + TrafficGenerator.MINLONG_SIMULATED) / 2,
				(TrafficGenerator.MAXLAT_SIMULATED + TrafficGenerator.MINLAT_SIMULATED) / 2,
				TrafficGenerator.MAXLONG_SIMULATED, "stetienne");// CE

		partNW = areapartNW.getNetworkCoordinates();
		partNE = areapartNE.getNetworkCoordinates();
		partSE = areapartSE.getNetworkCoordinates();
		partSW = areapartSW.getNetworkCoordinates();

		/*
		 * Network.initNetwork(part1); part1=Network.getCompleteJSONEdges();
		 * Network.initNetwork(part2); part2=Network.getCompleteJSONEdges();
		 * Network.initNetwork(part3); part3=Network.getCompleteJSONEdges();
		 * Network.initNetwork(part4);
		 * 
		 * part4=Network.getCompleteJSONEdges();
		 */
	}

	private static void computeForbiddenEdge(JSONArray edges) {
		JSONArray jsonEdges = new JSONArray();

		for (int i = 0; i < edges.size(); i++) {
			if (((JSONObject) edges.get(i)).get("jsongeoms") != null)
				jsonEdges.add(new Edge((JSONObject) edges.get(i)));
		}
		
		Network network = new CarNetwork();
		network.setEdges(jsonEdges);
		// create link between nodes
		// System.out.println("Create graph");
		network.createLinkedEdge();
		// delete isolated edges
		// System.out.println("Delete idle arcs");

		// Network.cleanEdges();
		Iterator<Map.Entry<Long, HashSet<Long>>> it = network.linkedEdges.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, HashSet<Long>> me = it.next();
			if (me.getValue().size() <= 1) {
				Edge edgeToRemove = network.getEdgeWithId(me.getKey());
				network.getEdgesComplet().remove(edgeToRemove);
				forbiddenEdges.add(edgeToRemove.getId());
				it.remove();
			}
		}
		JSONArray tempo = network.getCompleteJSONEdges();
		
		
		setOrderCompleteGraph(network);
		createConnexeGraph(network);

		// difference
		int j = 0;
		for (Object edge : tempo) {
			JSONObject jo = (JSONObject) edge;
			boolean forbidden = true;
			int i = 0;
			System.out.println(j++);
			while (i < network.getCompleteJSONEdges().size() && forbidden) {
				String tempoName = (String) jo.get("osm_id");
				String currentName = (String) ((JSONObject) network.getCompleteJSONEdges().get(i)).get("osm_id");
				if (currentName.compareTo(tempoName) == 0)
					forbidden = false;
				i++;
			}
			if (forbidden)
				forbiddenEdges.add(Long.parseLong((String) jo.get("osm_id")));

		}
System.out.println("fin");
	}

	public static void createConnexeGraph(Network network) {
		
		ArrayList<JSONArray> connectedComponents = new ArrayList<JSONArray>();

		// create connected components
		
		while (network.getEdgesComplet().size() != 0) {
			
			for (Map.Entry<Long, HashSet<Long>> ld : network.linkedEdges.entrySet())
				if (ld.getKey()== 8320746)
					System.out.println(ld);
			
			Edge[] weightedEdge = network.getShortestPath((Edge) network.getEdgesComplet().get(0));
			JSONArray currentComponent = new JSONArray();

			for (int i = weightedEdge.length - 1; i >= 0; i--)
				if (weightedEdge[i] != null) { // && weightedEdge[i].getId() !=
												// Network.getEdgesComplet().get(0).getId()) {
					Object e = network.getEdgesComplet().remove(i);
					
					currentComponent.add(e);
				}
			currentComponent.add((Edge) network.getEdgesComplet().remove(0));
			
			setOrderCompleteGraph(network);
			network.createLinkedEdge();
			connectedComponents.add(currentComponent);

		}
		// find bigger components
		int max = 0;
		int sum = 0;
		for (int i = 0; i < connectedComponents.size(); i++) {
			sum += connectedComponents.get(i).size();
			if (connectedComponents.get(i).size() > connectedComponents.get(max).size())
				max = i;
		}

		/*
		 * for (JSONArray cc : connectedComponents) {
		 * System.out.println(" ====================  " ); for (Object o : cc)
		 * System.out.println(o); System.out.println(" ====================  " +
		 * connectedComponents.size()); }
		 */
		// update networks
		network.setEdges(connectedComponents.get(max));

	}

	public static void setOrderCompleteGraph(Network network) {
		network.setEdgeCount(0);
		for (int i = 0; i < network.getEdgesComplet().size(); i++) {
			((Edge) network.getEdgesComplet().get(i)).setOrder(network.incEdgeCount() + 1);
		}
	}

	public static JSONArray initAreaNetwork(JSONArray edgesParameters) {
		// create the list of edges
		// initNetwork(subEdges,edgesParameters);
		// debug
		JSONArray jsonEdges = new JSONArray();

		for (int i = 0; i < edgesParameters.size(); i++) {
			if (((JSONObject) edgesParameters.get(i)).get("jsongeoms") != null)
				jsonEdges.add(new Edge((JSONObject) edgesParameters.get(i)));
		}
		/*
		 * Network.setEdges(jsonEdges); // create link between nodes //
		 * System.out.println("Create graph"); Network.createLinkedEdge(); // delete
		 * isolated edges // System.out.println("Delete idle arcs");
		 * 
		 * // Network.cleanEdges(); Iterator<Map.Entry<Long, HashSet<Long>>> it =
		 * Network.linkedEdges.entrySet().iterator(); while (it.hasNext()) {
		 * Map.Entry<Long, HashSet<Long>> me = it.next(); if (me.getValue().size() <= 1)
		 * { Edge edgeToRemove = Network.getEdgeWithId(me.getKey());
		 * Network.getEdgesComplet().remove(edgeToRemove); it.remove(); } }
		 * 
		 * return Network.getCompleteJSONEdges();
		 */
		return jsonEdges;
	}

	// make the keys of each part
	public static void areas() {
		areas.put("NORTHWEST", partNW);
		areas.put("NORTHEAST", partNE);
		areas.put("SOUTHEAST", partSE);
		areas.put("SOUTHWEST", partSW);
	}
	/*
	 * public static JSONArray getRequests() { JSONArray jar = new JSONArray();
	 * JSONObject jo = new JSONObject(); JSONArray values = new JSONArray(); for
	 * (TripTraffic trip : myTrips) {
	 * values.add(json.TransformJSON.createJSONFromInstance(trip)); }
	 * jo.put("requests", values); jar.add(jo); return jar;
	 * 
	 * }!!!!!
	 */

	/**
	 * 
	 * @param scenarioName name of the scenario for which requests are generated
	 * @return the request list ordered by cycles. A cell is the JSONObject with the
	 *         requests of the corresponding cycle
	 */
	public static JSONArray createRequests(JSONObject scenarioParameters, String login, String password,
			boolean pushData) {
		createMap();
		areas();
		accessibilityAlgorithm();
		String simulationName = (String) scenarioParameters.get("scenarioName");
		JSONArray result = getRequests();// removed:simulationName, login, password
		if (result != null) {
			try {
				// System.out.println("post:"+ TerritoireHttpConnection.postURL(puturl, result,
				// login, password, "request", simulationName));
				TerritoireHttpConnection.postURL(puturl, result, login, password, "request", simulationName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	// get the parameters from JSON file
	public static JSONArray generateJson() {

		// JSONArray jScenario = TerritoireHttpConnection
		// .getSimulationData(geturl, login, password, scenarioName, "scenario");//third
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

	// methods to choice the points(origin-destination)
	// method to generate the origin and donation in different areas

	public static JSONObject genratepoints() {
		JSONObject jo = new JSONObject();

		HashMap<String, JSONArray> copyOfarea1 = new HashMap<String, JSONArray>();
		copyOfarea1.putAll(areas);

		Object[] values = copyOfarea1.keySet().toArray();
		String origin = (String) values[random.nextInt(values.length)];
		jo.put("origin", copyOfarea1.get(origin));

		copyOfarea1.remove(origin);// the origin key element will be delete.

		Object[] value = copyOfarea1.keySet().toArray();
		String destination = (String) value[random.nextInt(value.length)];
		jo.put("destination", copyOfarea1.get(destination));
		return jo;
	}

	// generate the origin and destination randomly
	// here it should give random point from the different area for each one .
	private static TripTraffic randomGeneration(String typeOfVehicle, long twi, long twj) {
		JSONObject po = genratepoints();
		JSONObject origin;
		JSONObject destination;
		JSONArray originEntry = (JSONArray) po.get("origin");
		origin = getLocationNotInRegulatedMap(originEntry);
		// (JSONObject)originEntry.get(random.nextInt(originEntry.size()));

		JSONArray destinationEntry = (JSONArray) po.get("destination");
		destination = getLocationNotInRegulatedMap(destinationEntry);
		// (JSONObject) destinationEntry.get(random.nextInt(destinationEntry.size()));

		/*
		 * System.out.println("originName "+po.get("origin").toString());//convert the
		 * key to string
		 * System.out.println("destinationName "+po.get("destination").toString());//
		 * convert the key to string System.out.println("origin "+poo);
		 * System.out.println("destination "+poo1);
		 */
		long tw = twj - twi;
		int u = (int) tw;
		int departureTime = (int) (twi + TrafficGenerator.random.nextInt(u));
		return new TripTraffic(origin, destination, departureTime, typeOfVehicle);
	}

	private static JSONObject getLocationNotInRegulatedMap(JSONArray locationEntry) {

		JSONObject location = (JSONObject) locationEntry.get(random.nextInt(locationEntry.size()));
		ArrayList<JSONObject> geoms = (ArrayList<JSONObject>) location.get("jsongeoms");
		Long osmId = Long.parseLong((String) location.get("osm_id"));
		JSONObject point = geoms.get(TrafficGenerator.random.nextInt(geoms.size()));

		while (notValideLocation(point, osmId)) {
			location = (JSONObject) locationEntry.get(random.nextInt(locationEntry.size()));
			geoms = (ArrayList<JSONObject>) location.get("jsongeoms");
			osmId = Long.parseLong((String) location.get("osm_id"));
			point = geoms.get(TrafficGenerator.random.nextInt(geoms.size()));
		}
		location = new JSONObject();
		location.put("jsongeoms", point);
		location.put("osm_id", "" + osmId);
		return location;
	}

	private static boolean notValideLocation(JSONObject point, Long osmId) {

		double latitude = (Double) point.get("latitude");
		double longitude = (Double) point.get("longitude");
		return (((latitude < MAXLAT_REGULATED) && (latitude > MINLAT_REGULATED) && (longitude < MAXLONG_REGULATED)
				&& (longitude > MINLONG_REGULATED)) || forbiddenEdges.contains(osmId));

	}

	// create the trips for each vehicle in my json file.
	public static void accessibilityAlgorithm() {
		JSONArray source = generateJson();// will contain all the JSON file(parameters).
		int NumOfWindows = 0;
		JSONArray par = new JSONArray();
		JSONObject jsonObject = (JSONObject) source.get(0);
		par = (JSONArray) jsonObject.get("temporalWindows");

		for (int i = 0; i < par.size(); i++) {
			JSONObject ff = (JSONObject) par.get(i);
			long np = (long) ff.get("P-OFpersonal");
			long nt = (long) ff.get("P-OFTrucks");
			long no = (long) ff.get("P-OFothers");
			long npr = (long) ff.get("P-OFprofessional");

			for (int j = 0; j < np; j++) {
				myTrips.add(randomGeneration("P-OFpersonal", (long) ff.get("twi"), (long) ff.get("twj")));
			}
			for (int j = 0; j < nt; j++) {
				// I create a trip
				myTrips.add(randomGeneration("P-OFTrucks", (long) ff.get("twi"), (long) ff.get("twj")));// call the
																										// method that
																										// will generate
																										// the points
			}
			for (int j = 0; j < no; j++) {
				// I create a trip
				myTrips.add(randomGeneration("P-OFothers", (long) ff.get("twi"), (long) ff.get("twj")));// call the
																										// method that
																										// will generate
																										// the points
			}
			for (int j = 0; j < npr; j++) {
				// I create a trip
				myTrips.add(randomGeneration("P-OFprofessional", (long) ff.get("twi"), (long) ff.get("twj")));// call
																												// the
																												// method
																												// that
																												// will
																												// generate
																												// the
																												// points
			}
		}

	}

//new method 1-7-2020 this one will add the information of the constant 
	public static JSONArray getRequests() {
		Random rand = new Random();
		JSONArray jar = new JSONArray();
		JSONObject jo = new JSONObject();
		JSONObject joo = new JSONObject();// for loop
		JSONArray values = new JSONArray();// for loop
		JSONArray source = generateJson();// will contain all the JSON file(parameters).
		JSONObject jsonObject = (JSONObject) source.get(0);// get all the json element "can take it one by one by name
															// of element"
		long numofsex = (long) jsonObject.get("numF");
		long numofAge = (long) jsonObject.get("numAage");
		long numofEL = (long) jsonObject.get("numEL");
		long numofAI = (long) jsonObject.get("numAI");
		long numofTR = (long) jsonObject.get("numTR");

		// System.out.println( jsonObject.get("numF"));
		int u;
		int maxG = 0; // the max number of female
		int maxA = 0; // the max number of Age range
		int maxE = 0; // the max number of educationL range
		int maxAN = 0; // the max number of annulIcome range
		int maxT = 0; // the max number of TIME RESPECT range
		// System.out.println(myTrips.size());
		for (TripTraffic trip : myTrips) {

		//******//	joo = (json.TransformJSON.createJSONFromInstance(trip));
			// for(int i=1;i<=myTrips.size();i++) {//this loop for gender 1=female;

			//// SEX
			
			if (maxG > (int) numofsex) {
				u = 0;}
			else {
				u = rand.nextInt(2);
			maxG = maxG + u;}
			trip.setSex(u);
			//joo.put("sex", u);
			//// AGE
			
			if (maxA > (int) numofAge) {
				u = 0;
			}
			else {
				u = rand.nextInt(2);
				maxA = maxA + u;
			}
			//joo.put("age", u);
			trip.setAge(u);
			/// EDUCATION

			u = rand.nextInt(2);
			maxE = maxE + u;
			if (maxE > (int) numofEL) {
				u = 0;
			}
			else {
				u = rand.nextInt(2);
				maxE = maxE + u;
			}
			//joo.put("educL", u);
			trip.setEducL(u);
			/// ANUULINCOME

			u = rand.nextInt(2);
			maxAN = maxAN + u;
			if (maxAN > (int) numofAI) {
				u = 0;
			}
			else {
				u = rand.nextInt(2);
				maxAN = maxAN + u;
			}
			//joo.put("annuI", u);
			trip.setAnnuI(u);
			//// TIME RESPECT

			u = rand.nextInt(2);
			maxT = maxT + u;
			if (maxT > (int) numofAge) {
				u = 0;
			}
			else
			{
				u = rand.nextInt(2);
				maxT = maxT + u;
			}
		//	joo.put("timeR", u);
			trip.setTimeR(u);
			//values.add(joo);
			values.add(json.TransformJSON.createJSONFromInstance(trip));
		}
		jo.put("requests", values);// json object
		jar.add(jo);// new json array
		return jar;// new
		// return values; return the trips without "requests"

	}

}
